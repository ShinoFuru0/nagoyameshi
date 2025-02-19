package com.example.nagoyameshi.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.Service.StripeService;
import com.example.nagoyameshi.Service.UserService;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Controller
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final StripeService stripeService;

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    public UserController(UserRepository userRepository, UserService userService, StripeService stripeService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.stripeService = stripeService;
    }

    // ユーザー情報を表示
    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        model.addAttribute("user", user);
        return "user/index";
    }

    // ユーザー情報編集フォーム
    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getFurigana(), user.getEmail());
        model.addAttribute("userEditForm", userEditForm);
        return "user/edit";
    }

    // ユーザー情報更新
    @PostMapping("/update")
    public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        userService.update(userEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        return "redirect:/user";
    }

    // サブスク料金の支払い処理
    @PostMapping("/create-checkout-session")
    public ResponseEntity<String> subscribeUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, String> payload) {
        Stripe.apiKey = stripeApiKey;

        try {
            User user = userRepository.getReferenceById(userDetails.getUser().getId());

            // 顧客IDがなければ作成
            if (user.getCustomerId() == null) {
                Customer customer = stripeService.createCustomer(user);
                user.setCustomerId(customer.getId());
                userRepository.save(user);
            }

            String customerId = user.getCustomerId();

            // 支払い方法のチェック
            PaymentMethod defaultPaymentMethod = stripeService.getDefaultPaymentMethod(customerId);
            if (defaultPaymentMethod == null) {
                String paymentMethodId = payload.get("paymentMethodId");
                stripeService.attachPaymentMethodToCustomer(customerId, paymentMethodId);
                // フォームから送信された支払い方法を顧客のデフォルトの支払い方法に設定する
                stripeService.setDefaultPaymentMethod(paymentMethodId, customerId);
            }

            // プランID（事前作成したStripeのID）
            String planId = "price_1QqVPqC7xSmMs4vI9ZQ5EMzg";

            // サブスクリプション作成
            Subscription subscription = stripeService.createSubscription(user.getCustomerId(), planId);

            // サブスクリプション作成後、ユーザーのロールを更新
            stripeService.upgradeUserRoleToPaidMember(user.getId());

            // StripeのCheckout Sessionを作成
            SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setCustomer(user.getCustomerId())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl("http://localhost:8080//user/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://yourdomain.com/canceled")
                .addLineItem(SessionCreateParams.LineItem.builder()
                    .setPrice(planId)
                    .setQuantity(1L)
                    .build())
                .build();

            Session checkoutSession = Session.create(params);
            return ResponseEntity.ok(checkoutSession.getId());

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error while creating subscription: " + e.getMessage());
        }
        
    }
    
 // 支払い方法更新
    @PostMapping("/update-payment-method")
    public ResponseEntity<String> updatePaymentMethod(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, String> payload) {
        try {
            User user = userRepository.getReferenceById(userDetails.getUser().getId());
            String customerId = user.getCustomerId();

            if (customerId == null) {
                return ResponseEntity.badRequest().body("Customer ID not found");
            }

            String newPaymentMethodId = payload.get("paymentMethodId");
            if (newPaymentMethodId == null || newPaymentMethodId.isEmpty()) {
                return ResponseEntity.badRequest().body("New payment method ID is missing");
            }

            // Stripe API の呼び出し
            try {
                stripeService.attachPaymentMethodToCustomer(customerId, newPaymentMethodId);
                stripeService.updateDefaultPaymentMethod(customerId, newPaymentMethodId);
            } catch (StripeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error while updating payment method: " + e.getMessage());
            }

            return ResponseEntity.ok("Payment method updated successfully");

        } catch (RuntimeException e) { // その他の例外処理（データベースエラー等）
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // 支払い方法更新
   // @PostMapping("/update-payment-method")
   // public ResponseEntity<String> updatePaymentMethod(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, String> payload) {
       
   // 	try {
    	    // Stripe API の処理（例: Session.retrieve は StripeException をスローする可能性がある）
  //  	    Session session = Session.retrieve(sessionId);  
  //  	    String customerId = session.getCustomer();

    //	    User user = userRepository.findByCustomerId(customerId)
    	//            .orElseThrow(() -> new RuntimeException("User not found"));

    	//    userRepository.save(user);
    	//    redirectAttributes.addFlashAttribute("message", "支払処理が完了しました");

    //	    return "redirect:/";
    //	} catch (StripeException e) { // ここで StripeException をキャッチ
    //	    model.addAttribute("error", e.getMessage());
    //	    return "error";
    //	}
    	
  //  }
    	
    	
   // 	try {
   //         User user = userRepository.getReferenceById(userDetails.getUser().getId());
   //         String customerId = user.getCustomerId();

   //         if (customerId == null) {
   //             return ResponseEntity.badRequest().body("Customer ID not found");
   //         }

   //         String newPaymentMethodId = payload.get("paymentMethodId");
   //         if (newPaymentMethodId == null || newPaymentMethodId.isEmpty()) {
  //              return ResponseEntity.badRequest().body("New payment method ID is missing");
   //         }

    //        stripeService.attachPaymentMethodToCustomer(customerId, newPaymentMethodId);
    //        stripeService.updateDefaultPaymentMethod(customerId, newPaymentMethodId);

    //        return ResponseEntity.ok("Payment method updated successfully");

     //   } catch (StripeException e) {
     //       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //            .body("Error while updating payment method: " + e.getMessage());
    //    }
  //  }

    // サブスク成功
 //   @GetMapping("/success")
   // public String success(@RequestParam("session_id") String sessionId, Model model, RedirectAttributes redirectAttributes) {
     //   try {
       //     Session session = Session.retrieve(sessionId);
         //   String customerId = session.getCustomer();
         //   User user = userRepository.findByCustomerId(customerId).orElseThrow();
         //   userRepository.save(user);
         //   redirectAttributes.addFlashAttribute("message", "支払処理が完了しました");
         //   return "redirect:/";
        //} catch (StripeException e) {
        //    model.addAttribute("error", e.getMessage());
       //     return "error";
       // }
        
        
        
     // サブスク成功
        @GetMapping("/success")
        public String success(@RequestParam("session_id") String sessionId, Model model, RedirectAttributes redirectAttributes) {
            try {
                // Stripe API の処理（Session.retrieve は StripeException をスローする可能性がある）
                Session session = Session.retrieve(sessionId);  
                String customerId = session.getCustomer();

                User user = userRepository.findByCustomerId(customerId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                userRepository.save(user);
                redirectAttributes.addFlashAttribute("message", "支払処理が完了しました");

                return "redirect:/";
            } catch (StripeException e) { // Stripe API 呼び出しの例外処理
                model.addAttribute("error", e.getMessage());
                return "error";
            } catch (RuntimeException e) { // ユーザーが見つからない場合の処理
                model.addAttribute("error", "ユーザーが見つかりませんでした。");
                return "error";
            }
        }
        
        
        
        
    }
