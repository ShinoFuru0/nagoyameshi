package com.example.nagoyameshi.controller;

import java.util.UUID;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.Service.PassService;
import com.example.nagoyameshi.Service.UserService;
import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.form.PasswordEditForm;
import com.example.nagoyameshi.repository.PasswordResetTokenRepository;

@Controller
//@RequestMapping("/auth")
public class PassController {
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final PassService passService;
	private final JavaMailSender javaMailSender;
	private final UserService userService;
	
	public PassController(PasswordResetTokenRepository passwordResetTokenRepository,PassService passService
			,JavaMailSender mailSender,UserService userService) {
		this.passwordResetTokenRepository = passwordResetTokenRepository;
		this.passService  = passService;
		this.javaMailSender = mailSender;
		this.userService = userService;
	}
	
	@GetMapping("/passreset")
	    public String showForgotPasswordForm() {
	    	
	        return "/auth/passreset";
	    }
	
    @PostMapping("/passreset")
	    public String processForgotPassword(String email, RedirectAttributes redirectAttributes) {
//	    	token作成
	        String token = UUID.randomUUID().toString();
	        userService.createPasswordResetTokenForUser(email, token);

//	      リセットのためのtokenのついたURLを作成
	        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
	        try {
//	        	メール送信：メール内容は、下のsendEmail()で実行
	            sendEmail(email, resetUrl);
	            redirectAttributes.addFlashAttribute("successMessage", "パスワード再発行リンクをメールで送信しました。");
	        } catch (MessagingException e) {
	            redirectAttributes.addFlashAttribute("errorMessage", "メール送信に失敗しました。");
	        }

	        return "redirect:/auth/passreset";
	    }

	    // パスワード再発行③：パスワードを再発行のためのメール発行・送信
	    private void sendEmail(String email, String resetUrl) throws MessagingException {
	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message);

	        helper.setTo(email);
	        helper.setSubject("NAGOYAMESHI：パスワード再発行リンク");
	        helper.setText("以下のリンクをクリックしてパスワードを再発行してください: " + resetUrl);

	        javaMailSender.send(message);
	    }
	    
	    @GetMapping("/reset-password")
	    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
	        PasswordResetToken resetToken = userService.getPasswordResetToken(token);
	        if (resetToken == null) {
	            model.addAttribute("message", "無効なトークンです。");
	            return "redirect:/login";
	        }
	        model.addAttribute("token", token);
	        return "/auth/passedit";
	    }

	    @PostMapping("/passedit")
	    public String processResetPassword(@RequestParam("token") String token,
	                                       @Valid PasswordEditForm passwordEditForm,
	                                       BindingResult result,
	                                       RedirectAttributes redirectAttributes) {
	        if (result.hasErrors()) {
//	        	result.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
	            redirectAttributes.addFlashAttribute("error", "フォームにエラーがあります。");
	            return "redirect:/auth/passedit?token=" + token;
	        }
	        boolean success = userService.updatePassword(token, passwordEditForm.getNewPassword());
	        if (success) {
	            redirectAttributes.addFlashAttribute("message", "パスワードが正常に更新されました。");
	            return "redirect:/login";
	        } else {
	            redirectAttributes.addFlashAttribute("error", "パスワードの更新に失敗しました。");
	            return "redirect:/auth/passedit?token=" + token;
	        }
	    }

}
