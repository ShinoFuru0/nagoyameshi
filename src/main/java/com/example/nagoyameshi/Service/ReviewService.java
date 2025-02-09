package com.example.nagoyameshi.Service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.ReviewRepository;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;        
    
    public ReviewService(ReviewRepository reviewRepository) {        
        this.reviewRepository = reviewRepository;        
    }     
    
    @Transactional
    public void create(Shop shop, User user,ReviewRegisterForm reviewRegisterForm) {
   	 Review review = new Review();
   	
   	 review.setShop(shop);                
        review.setUser(user);
   	 review.setContent(reviewRegisterForm.getContent());
   	
   	 
   	 reviewRepository.save(review);
    }
    
    public boolean hasUserAlreadyReviewed(Shop shop, User user) {
        return reviewRepository.findByShopAndUser(shop, user) != null;
    }
    @Transactional
    public void update(ReviewEditForm reviewEditForm) {
        Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
       
        
                      
        review.setContent(reviewEditForm.getContent());
        
                    
        reviewRepository.save(review);
    }    
   
    
}