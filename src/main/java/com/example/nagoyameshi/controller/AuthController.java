package com.example.nagoyameshi.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.Service.UserService;
import com.example.nagoyameshi.Service.VerificationTokenService;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.event.SignupEventPublisher;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.repository.VerificationTokenRepository;

@Controller
public class AuthController {
 private final UserService userService;
 private final SignupEventPublisher signupEventPublisher; 
 private final VerificationTokenService verificationTokenService;
 private final VerificationTokenRepository verificationTokenRepository;
     
 public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, 
		 VerificationTokenService verificationTokenService,VerificationTokenRepository verificationTokenRepository) { 
         this.userService = userService;  
         this.signupEventPublisher = signupEventPublisher;
         this.verificationTokenService = verificationTokenService;
         this.verificationTokenRepository = verificationTokenRepository;
     }    
@GetMapping("/login")
public String login() {
	return "auth/login";
}
@GetMapping("/signup")
public String signup(Model model) {        
    model.addAttribute("signupForm", new SignupForm());
    return "auth/signup";
}    
@PostMapping("/signup")
public String signup(@ModelAttribute @Validated SignupForm signupForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
    // メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
    if (userService.isEmailRegistered(signupForm.getEmail())) {
        FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
        bindingResult.addError(fieldError);                       
    }    
    
    // パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
    if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
        FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
        bindingResult.addError(fieldError);
    }        
    
    if (bindingResult.hasErrors()) {
        return "auth/signup";
    }
    
    if (bindingResult.hasErrors()) {
        System.out.println(bindingResult);
        return "auth/signup";
    }
    System.out.println("ユーザ登録前");
    User createdUser = userService.create(signupForm);
    System.out.println("ユーザ登録後");
    System.out.println("メール送信前");
    String requestUrl = new String(httpServletRequest.getRequestURL());
    signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
    System.out.println("メール送信後");
    redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。"); 

    System.out.println("リダイレクト処理前"); 

    return "redirect:/";
}    

@GetMapping("/signup/verify")
public String verify(@RequestParam(name = "token") String token, Model model,Integer id) {
    VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
    
    if (verificationToken != null) {
        User user = verificationToken.getUser();  
        userService.enableUser(user);
        String successMessage = "会員登録が完了しました。";
        model.addAttribute("successMessage", successMessage);   
//        verificationTokenRepository.deleteByUser(user);
    } else {
        String errorMessage = "トークンが無効です。";
        model.addAttribute("errorMessage", errorMessage);
    }
    
    return "auth/verify";         
}    
}
