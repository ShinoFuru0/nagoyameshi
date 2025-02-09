package com.example.nagoyameshi.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.Service.FavService;
import com.example.nagoyameshi.Service.ReviewService;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.FavForm;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.FavRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;

@Controller
@RequestMapping("/shop")
public class ShopController {
	 private final ShopRepository shopRepository;
	 private final ReviewRepository reviewRepository;
     private final ReviewService reviewService;
     private final FavRepository favRepository;
     private final FavService favService;
	 
	 public ShopController(ShopRepository shopRepository,ReviewRepository reviewRepository,ReviewService reviewService,FavRepository favRepository,FavService favService) {
         this.shopRepository = shopRepository;    
         this.reviewRepository = reviewRepository;
         this.reviewService = reviewService;
         this.favRepository =  favRepository;
         this.favService =  favService;
          
     }	
	 
	 @GetMapping
     public String index(Model model,@PageableDefault Pageable pageable, @RequestParam(name = "keyword", required = false) String keyword,
    		 @RequestParam(name = "category", required = false) String category)  {
    	 
    	 Page<Shop> shopPage;
    	 
    	  if (keyword != null && !keyword.isEmpty()) {
              shopPage = shopRepository.findByNameLike("%" + keyword + "%", pageable);                
          }
//    	  else if(category != null && !category.isEmpty()){
//    		  shopPage = shopRepository.findByCategory( category , pageable);
//    	  }
    	  else {
              shopPage = shopRepository.findAll(pageable);
          }  
    	 model.addAttribute("shopPage",shopPage);
    	 model.addAttribute("keyword", keyword);
    	 model.addAttribute("category", category);
    	 
    	 return "shop/index";
     }
	 @GetMapping("/{id}")
     public String show(@PathVariable(name = "id") Integer id, Model model, 
    		 @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		 Principal principal) {
         Shop shop = shopRepository.getReferenceById(id);
         boolean hasUserAlreadyReviewed = false;
         boolean isFav = false;
         if(userDetailsImpl!=null) {
        	 User user = userDetailsImpl.getUser();
        	 hasUserAlreadyReviewed = reviewService.hasUserAlreadyReviewed(shop,user);
        	 isFav = favService.isFav(shop, user);
         }
         
         if(principal != null) {
        	 User user = userDetailsImpl.getUser();
        	 isFav = favService.isFav(shop,user);
         }
         
         List<Review> newReviews =reviewRepository.findTop6ByShopOrderByCreatedAtDesc(shop);
         long totalReviewCount = reviewRepository.countByShop(shop);  
         
         model.addAttribute("shop", shop);   
         model.addAttribute("reservationInputForm", new ReservationInputForm());
         model.addAttribute("hasUserAlreadyReviewed", hasUserAlreadyReviewed);
         model.addAttribute("newReviews",newReviews);
         model.addAttribute("totalReviewCount",totalReviewCount);
         model.addAttribute("isFav",isFav);
         
         return "shop/show";
     }    
	 
	 @GetMapping("/{id}/review/register")
     public String register(@PathVariable(name = "id") Integer id, Model model) {
         Shop shop = shopRepository.getReferenceById(id);
         model.addAttribute("shop", shop); 
    	 model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
    	 return "review/register";
    	
     }
	 
	 @GetMapping("/{id}/review/index")
     public String index(@PathVariable(name = "id") Integer id,Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
//    	 Page<Review> reviewPage = reviewRepository.findAll(pageable);
    	 
    	 
//    	 House house = houseRepository.getReferenceById(id);
//    	 List<Review> reviewPage = reviewRepository.findByHouse(house);
    	  
//    	 model.addAttribute("reviewPage",reviewPage);
//    	 model.addAttribute(house);
    	
    	 Shop shop = shopRepository.getReferenceById(id);
         boolean hasUserAlreadyReviewed = false;
         
         if(userDetailsImpl!=null) {
        	 User user = userDetailsImpl.getUser();
        	 hasUserAlreadyReviewed = reviewService.hasUserAlreadyReviewed(shop,user);
         }
//         List<Review> newReviews =reviewRepository.findTop6ByHouseOrderByCreatedAtDesc(house);
         List<Review> reviewList = reviewRepository.findAllByShopOrderByCreatedAtDesc(shop);
         Page<Review> reviewPage = reviewRepository.findAllByShopOrderByCreatedAtDesc(shop,pageable);
         long totalReviewCount = reviewRepository.countByShop(shop);  
         
         
         model.addAttribute("shop", shop);         
         model.addAttribute("reservationInputForm", new ReservationInputForm());
         model.addAttribute("hasUserAlreadyReviewed", hasUserAlreadyReviewed);
         model.addAttribute("reviewList",reviewList);
         model.addAttribute("reviewPage",reviewPage);
         model.addAttribute("totalReviewCount",totalReviewCount);
    	 
    	 return "/review/index";
    	 
     }
	 
	 @PostMapping("/{shopId}/fav/add")
     public String fav(@PathVariable(name="shopId") Integer shopId,
    		 @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		 Model model,
    		 @ModelAttribute @Validated FavForm favForm ) {
    	 
    	 Shop shop = shopRepository.getReferenceById(shopId);
    	
    	 User user = userDetailsImpl.getUser();
    	 
    	 favService.create(shop,user,favForm);
    	 
//    	 return "houses/{id}";
    	 return "redirect:/shop/" + shopId;
     }
    
     @PostMapping("/{shopId}/fav/delete")
     public String delete(@PathVariable(name="shopId") Integer shopId,
    		 @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		 Model model,
    		 @ModelAttribute @Validated FavForm favForm ) {
    	 
    	 Shop shop = shopRepository.getReferenceById(shopId);
    	
    	 User user = userDetailsImpl.getUser();
    	 
    	 favService.create(shop,user,favForm);
//    	 
//    	 return "houses/{id}";
    	 return "redirect:/shop/" + shopId;
     }
     
}
