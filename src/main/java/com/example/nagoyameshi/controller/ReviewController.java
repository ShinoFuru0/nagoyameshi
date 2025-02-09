package com.example.nagoyameshi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.Service.ReviewService;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;

	@Controller
	public class ReviewController{
		private final ReviewRepository reviewRepository;
		private final ReviewService reviewService;
		private final ShopRepository shopRepository;
		
		public ReviewController(ReviewRepository reviewRepository,ReviewService reviewService,ShopRepository shopRepository) {
			this.reviewRepository = reviewRepository;
			this.reviewService = reviewService;
			this.shopRepository = shopRepository;
		}
		

		 @PostMapping("/shop/{shopId}/review/create")
		  public String create(@PathVariable(name = "shopId") Integer shopId,
	             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	             @ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
	             BindingResult bindingResult,
	             RedirectAttributes redirectAttributes,
	             Model model)
		{    
			Shop shop = shopRepository.getReferenceById(shopId);
			User user = userDetailsImpl.getUser();
			
			if (bindingResult.hasErrors()) {
				System.out.println(bindingResult.hasErrors());
				model.addAttribute("shop", shop);
				
				return "review/register";
			}        
			reviewService.create(shop, user, reviewRegisterForm);
			redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");    
			
			return "redirect:/shop/{shopId}";
		}
			
		@GetMapping("/shop/{shopId}/review/{reviewId}/edit")
	     public String edit(@PathVariable(name="shopId") Integer shopId,
	             @PathVariable(name="reviewId") Integer reviewId, 
	             Model model) {
	        Shop shop = shopRepository.getReferenceById(shopId);
	         Review review = reviewRepository.getReferenceById(reviewId);
	    	 ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(),review.getContent());
	    	
	    	 model.addAttribute("shop",shop);
	    	 model.addAttribute("review",review);
	    	 model.addAttribute("reviewEditForm",reviewEditForm);
	    	 
	    	 
	    	 return "review/edit";
	   }
	     
	     @PostMapping("review/{id}/delete")
	     public String delete(@PathVariable(name="id") Integer id,RedirectAttributes redirectAttributes) {
	    	 
	    	 reviewRepository.deleteById(id);
	    	 
	    	 System.out.println(id);
	    	 
	    	  redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
	    	  
	    	  return "redirect:/shop/{id}";
	     }
	     
	     @PostMapping("/shop/{shopId}/review/{reviewId}/update")
	     public String update(@PathVariable(name = "shopId") Integer shopId,@PathVariable(name = "reviewId") Integer reviewId,
	             @ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes,Model model) { 
	    	 Shop shop = shopRepository.getReferenceById(shopId);
	    	 Review review = reviewRepository.getReferenceById(reviewId);
	 		
	         if (bindingResult.hasErrors()) {
	        	 System.out.println(bindingResult.hasErrors());
	 			model.addAttribute("shop", shop);
	 			model.addAttribute("review", review);
	             return "review/edit";
	         }
	         
	         reviewService.update(reviewEditForm);
	         redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
	         
	         return "redirect:/shop/{shopId}";
	     }    
	     
	     

	}

