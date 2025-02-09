package com.example.nagoyameshi.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
     
     @GetMapping
     public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {         
         User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
         
         model.addAttribute("user", user);
         
         return "user/index";
     }
     @GetMapping("/edit")
     public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {        
         User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
         UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getFurigana(),  user.getEmail());
         
         model.addAttribute("userEditForm", userEditForm);
         
         return "user/edit";
     }    
     @PostMapping("/update")
     public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
         // メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
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
     
     @GetMapping("/{id}/grade")
     public String grade(@PathVariable(name = "id") Integer id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,  
             HttpServletRequest httpServletRequest,
            Model model) {
    	 User user = userDetailsImpl.getUser(); 

          
          return "user/{id}/grade";
     }
     
//     サブスク料金の支払い
     @PostMapping("/create-checkout-session")
     public ResponseEntity<String> 

     subscribeUser(@AuthenticationPrincipal UserDetailsImpl userDetails,

    		 @RequestBody Map<String, String> payload
             ) {
    	 Stripe.apiKey = stripeApiKey;
    	 System.out.println("テスト");
    	
try {
User user = userRepository.getReferenceById(userDetails.getUser().getId());

// Stripeの顧客IDを確認し、必要なら作成
if (user.getCustomerId() == null) {
Customer customer = stripeService.createCustomer(user);
user.setCustomerId(customer.getId());
userRepository.save(user);
}

System.out.println("テスト2");
System.out.println("Name: " + user.getName());


String customerId = user.getCustomerId();
System.out.println("Customer ID: " + customerId);
//顧客のデフォルト支払い方法を確認
PaymentMethod defaultPaymentMethod = stripeService.getDefaultPaymentMethod(customerId);

// デフォルト支払い方法が存在しない場合、新しい支払い方法を追加
if (defaultPaymentMethod == null) {
    System.out.println("No default payment method found, attaching a new one.");
    String paymentMethodId = payload.get("paymentMethodId");
    stripeService.attachPaymentMethodToCustomer(customerId, paymentMethodId);
} else {
    System.out.println("Default payment method found: " + defaultPaymentMethod.getId());
}
String paymentMethodId = payload.get("paymentMethodId");
stripeService.updateSubscription(customerId,paymentMethodId);
System.out.println("テスト紐付け");
// 事前に作成したプランIDでサブスクリプションを作成
String planId = "price_1QTlYlBZ4UD9z1bMerQL8aai"; // 事前にStripeで作成したプランIDを指定します
System.out.println("プランID"+planId);
Subscription subscription = stripeService.createSubscription(user.getCustomerId(), planId);
//stripeService.createSubscription(user.getCustomerId(), planId);
System.out.println("テスト３");
stripeService.upgradeUserRoleToPaidMember(user.getId());
System.out.println("テスト4");


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
System.out.println(checkoutSession.getId());

SecurityContext auth = SecurityContextHolder.getContext();
Collection<GrantedAuthority> authorities = new ArrayList<>();         
authorities.add(new SimpleGrantedAuthority("ROLE_PAID_MEMBER"));
auth.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,user.getPassword(),authorities));
System.out.println("テスト5");
//return ResponseEntity.ok("Subscription successful: " + subscription.getId());
return ResponseEntity.ok(checkoutSession.getId());
//return "user/success";
} catch (StripeException e) {
e.printStackTrace();
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
.body("Error while creating subscription: " + e.getMessage());
}

}


     
     @GetMapping("/cancel")
     public String showCancelPage(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,Model model) throws StripeException {
         // 必要に応じてモデルにデータを追加
    	 User user = userDetailsImpl.getUser();

    	 
         return "user/cancel"; // cancel.htmlまたは対応するテンプレート名
     }


     
     @PostMapping("/cancel-subscription")
     public String cancelSubscriptionbtn(@RequestParam String paymentMethodId, Model model
    		 							
    		 							,@AuthenticationPrincipal UserDetailsImpl userDetails) {
    	 User user = userRepository.getReferenceById(userDetails.getUser().getId());
    	 
    	    String customerId = user.getCustomerId();
    	 System.out.println("テスト");
    	   try {
    	        // サブスクリプションIDを取得
    		   System.out.println("テスト2");
    	        String subscriptionId = stripeService.getSubscriptionIdForUser(customerId);
    	        System.out.println("subscriptionId"+subscriptionId);
    	        if (subscriptionId == null) {
    	            model.addAttribute("message", "No active subscription found.");
    	            return "user/cancelResult";
    	        }

    	        // サブスクリプションをキャンセル
    	        Subscription subscription = Subscription.retrieve(subscriptionId);
    	        subscription.cancel();

    	        model.addAttribute("message", "Subscription canceled successfully.");
    	    } catch (StripeException e) {
    	        e.printStackTrace();
    	        model.addAttribute("message", "Failed to cancel subscription: " + e.getMessage());
    	    }
    	   stripeService.upgradeUserRoleToGeneralMember(user.getId());
    	   System.out.println("テスト3");
    	   SecurityContext auth = SecurityContextHolder.getContext();
    	   Collection<GrantedAuthority> authorities = new ArrayList<>();         
    	   authorities.add(new SimpleGrantedAuthority("ROLE_GENERAL"));
    	   auth.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,user.getPassword(),authorities));
    	    return "user/cancelResult"; // 操作結果を表示するページ
    	}
     
     
     @GetMapping("/change")
     public String change( @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
    	 User user = userDetailsImpl.getUser(); 

          return "user/change";
     }
 
     @PostMapping("/update-payment-method")
     public ResponseEntity<String> updatePaymentMethod(
         @AuthenticationPrincipal UserDetailsImpl userDetails,
         @RequestBody Map<String, String> payload) {
    	 System.out.println("テスト");
         try {
             User user = userRepository.getReferenceById(userDetails.getUser().getId());
             String customerId = user.getCustomerId();
             if (customerId == null) {
                 return ResponseEntity.badRequest().body("Customer ID not found");
             }
             System.out.println("テスト1.5");
             String newPaymentMethodId = payload.get("paymentMethodId");
             if (newPaymentMethodId == null || newPaymentMethodId.isEmpty()) {
                 return ResponseEntity.badRequest().body("New payment method ID is missing");
             }
             System.out.println("テスト2");
             // 新しい支払い方法を顧客に追加
             stripeService.attachPaymentMethodToCustomer(customerId, newPaymentMethodId);
             System.out.println("テスト3");
             // 顧客のデフォルト支払い方法を更新
             stripeService.updateDefaultPaymentMethod(customerId, newPaymentMethodId);
             System.out.println("テスト4");
             return ResponseEntity.ok("Payment method updated successfully");
             
         } catch (StripeException e) {
             e.printStackTrace();
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                 .body("Error while updating payment method: " + e.getMessage());
         }
     }
     
     
     
     
 //  サブスク料金支払処理が成功した後の処理
     @GetMapping("/success")
     public String success(@RequestParam("session_id") String sessionId, Model model, RedirectAttributes redirectAttributes) {
         // セッションIDから関連するサブスクリプション情報を取得
         try {
             Session session = Session.retrieve(sessionId);
             // 顧客情報（例として、顧客IDを取得）
             String customerId = session.getCustomer();
             // 顧客情報からユーザーを特定（仮定として、顧客IDをユーザーテーブルのフィールドとして保持）
             User user = userRepository.findByCustomerId(customerId).orElseThrow();
//             user.setSubscriptionStartDate(LocalDate.now());
//             user.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
             
             userRepository.save(user);
             
             redirectAttributes.addFlashAttribute("message", "支払処理が完了しました");
             return "redirect:/";
         } catch (StripeException e) {
             model.addAttribute("error", e.getMessage());
             return "error";
         }
     }

}