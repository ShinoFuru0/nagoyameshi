package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.Service.CategoryService;
import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.CategoryShopRepository;
import com.example.nagoyameshi.repository.ShopRepository;

@Controller
@RequestMapping("admin/category")
public class AdminCategoryController {
	 private final ShopRepository shopRepository;
	  private final CategoryRepository categoryRepository;
	  private final CategoryShopRepository categoryShopRepository;
	  private final CategoryService categoryService;
	     
	     public AdminCategoryController(CategoryRepository categoryRepository,ShopRepository shopRepository,CategoryShopRepository categoryShopRepository,CategoryService categoryService) {
	    	 this.shopRepository = shopRepository;  
	    	 this.categoryRepository = categoryRepository;     
	    	 this.categoryShopRepository = categoryShopRepository;
	    	 this.categoryService = categoryService;
	     }    
	     @GetMapping
	     public String index(Model model, @RequestParam(name = "keyword", required = false) String keyword,@PageableDefault Pageable pageable) {
	    	 Page<Category> categoryPage;
	    	  if (keyword != null && !keyword.isEmpty()) {
	              categoryPage = categoryRepository.findByCategoryLike("%" + keyword + "%", pageable);                
	          }
//	    	  else if(category != null && !category.isEmpty()){
//	    		  shopPage = shopRepository.findByCategory( category , pageable);
//	    	  }
	    	  else {
	              categoryPage = categoryRepository.findAll(pageable);
	          }  
//	    	 Page<Category> categoryPage  = categoryRepository.findAll(pageable);
//	    	 categoryPage = categoryRepository.findAll(pageable);
//	    	 Shop shop = shopRepository.getReferenceById(shop.getId());
	         List<Category> newCategory = categoryRepository.findTop10ByOrderById();
	         model.addAttribute("categoryPage",categoryPage);
	         model.addAttribute("newCategory", newCategory);
	         model.addAttribute("keyword", keyword);
//	         model.addAttribute("shop", shop); 
//	    	 Category category =  categoryRepository.getReferenceById();  
	         
	        
	        return "admin/category/index";
	}
	     @GetMapping("/{id}")
	     public String show(Model model,@PathVariable(name = "id") Integer id,@PageableDefault Pageable pageable){
	    	 Category category = categoryRepository.getReferenceById(id);
//	    	 Shop shop = shopRepository.getReferenceById(shopId);
//	    	  Shop shop = shopRepository.getReferenceById(id);
	    	  Page<Shop> shopPage;
	    	  shopPage = shopRepository.findAll(pageable);
	    	  
	    	  model.addAttribute("shopPage",shopPage);
	    	 model.addAttribute("category",category);
//	    	 model.addAttribute("shop", shop);   
	    	 
	    	 return "admin/category/show";
	     }
	     
	     @GetMapping("/register")
	     public String register(Model model) {
	         model.addAttribute("categoryRegisterForm", new CategoryRegisterForm());
	         return "admin/category/register";
	     }  
	     
	     @PostMapping("/create")
	     public String create(@ModelAttribute @Validated CategoryRegisterForm categoryRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
	         if (bindingResult.hasErrors()) {
	             return "admin/category/register";
	         }
	         categoryService.create(categoryRegisterForm);
	         redirectAttributes.addFlashAttribute("successMessage", "カテゴリを登録しました。");    
	         
	         return "redirect:/admin/category";
}
	     
	     @GetMapping("/{id}/edit")
	     public String edit(@PathVariable(name = "id") Integer id, Model model) {
	         Category category = categoryRepository.getReferenceById(id);
	        
//	         List<Category> categories = categoryRepository.findAll();
	       
	         CategoryEditForm form = new CategoryEditForm(category.getId(), category.getCategory());

	         model.addAttribute("categoryEditForm", form);
//	         model.addAttribute("imageName", imageName);
//	         model.addAttribute("categories", categories);
	         return "admin/category/edit";
	     }
	     
	     @PostMapping("/{id}/update")
	     public String update(@ModelAttribute @Validated CategoryEditForm categoryEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
	         if (bindingResult.hasErrors()) {
	             return "admin/category/edit";
	         }
	         
	         categoryService.update(categoryEditForm);
	         redirectAttributes.addFlashAttribute("successMessage", "カテゴリ情報を編集しました。");
	         
	         return "redirect:/admin/category";
	     }    
	     
	     @PostMapping("/{id}/delete")
	     public String delete(@PathVariable(name="id")Integer id,RedirectAttributes redirectAttributes) {
	   	  categoryRepository.deleteById(id);
	   	  
	   	  redirectAttributes.addFlashAttribute("successMessage","カテゴリを削除しました。");
	   	  
	   	  return "redirect:/admin/category";
	     }
}