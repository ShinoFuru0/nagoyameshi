package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.CategoryShopRepository;
import com.example.nagoyameshi.repository.ShopRepository;

@Controller
@RequestMapping("/category")
public class CategoryController {
	 private final ShopRepository shopRepository;
	  private final CategoryRepository categoryRepository;
	  private final CategoryShopRepository categoryShopRepository;
	     
	     public CategoryController(CategoryRepository categoryRepository,ShopRepository shopRepository,CategoryShopRepository categoryShopRepository) {
	    	 this.shopRepository = shopRepository;  
	    	 this.categoryRepository = categoryRepository;     
	    	 this.categoryShopRepository = categoryShopRepository;
	     }    
	     @GetMapping
	     public String index(Model model, @RequestParam(name = "keyword", required = false) String keyword,@PageableDefault Pageable pageable) {
	    	 Page<Category> categoryPage;
	    	  if (keyword != null && !keyword.isEmpty()) {
	              categoryPage = categoryRepository.findByCategoryLike("%" + keyword + "%", pageable);                
	          }
	    	  else {
	              categoryPage = categoryRepository.findAll(pageable);
	          }  
	         List<Category> newCategory = categoryRepository.findTop10ByOrderById();
	         model.addAttribute("categoryPage",categoryPage);
	         model.addAttribute("newCategory", newCategory);
	         model.addAttribute("keyword", keyword);
	         
	        
	        return "category/index";
	}
	     @GetMapping("/{id}")
	     public String show(Model model,@PathVariable(name = "id") Integer id,@PageableDefault Pageable pageable){
	    	 Category category = categoryRepository.getReferenceById(id);
	    	  Page<Shop> shopPage;
	    	  shopPage = shopRepository.findAll(pageable);
	    	  
	    	  model.addAttribute("shopPage",shopPage);
	    	 model.addAttribute("category",category);
	    	 
	    	 return "category/show";
	     }
	     
	   
}
