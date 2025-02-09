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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.Service.ShopService;
import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.form.ShopEditForm;
import com.example.nagoyameshi.form.ShopRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.ShopRepository;
@Controller
@RequestMapping("/admin/shop")
public class AdminShopController { 
 private final ShopRepository shopRepository; 
 private final ShopService shopService;  
 private final CategoryRepository categoryRepository;
     
     public AdminShopController(ShopRepository shopRepository, ShopService shopService, CategoryRepository categoryRepository) {
         this.shopRepository = shopRepository;        
         this.shopService = shopService; 
         this.categoryRepository = categoryRepository;
     }	
     
     @GetMapping
     public String index(Model model,@PageableDefault Pageable pageable, @RequestParam(name = "keyword", required = false) String keyword,
    		 @RequestParam(name = "category", required = false) Integer categoryId)  {
    	 
    	 Page<Shop> shopPage;
    	 
    	  if (keyword != null && !keyword.isEmpty()) {
              shopPage = shopRepository.findByNameLike("%" + keyword + "%", pageable);                
          }
//    	  else if(categoryId != null && !categoryId.isEmpty()){
//    		  shopPage = shopRepository.findByCategory( categoryId , pageable);
//    	  }
    	  else {
              shopPage = shopRepository.findAll(pageable);
          }  
    	 model.addAttribute("shopPage",shopPage);
    	 model.addAttribute("keyword", keyword);
    	 model.addAttribute("categoryId", categoryId);
    	 
    	 return "admin/shop/index";
     }

     @GetMapping("/{id}")
     public String show(@PathVariable(name = "id") Integer id, Model model) {
         Shop shop = shopRepository.getReferenceById(id);
         
         model.addAttribute("shop", shop);
         
         return "admin/shop/show";
     }    
     
     @GetMapping("/register")
     public String register(Model model) {
         model.addAttribute("shopRegisterForm", new ShopRegisterForm());
         
         return "admin/shop/register";
     }    
     
     @PostMapping("/create")
     public String create(@ModelAttribute @Validated ShopRegisterForm shopRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
         if (bindingResult.hasErrors()) {
             return "admin/shop/register";
         }
         
         shopService.create(shopRegisterForm);
         redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。");    
         
         return "redirect:/admin/shop";
     }    
     
     @GetMapping("/{id}/edit")
     public String edit(@PathVariable(name = "id") Integer id, Model model) {
         Shop shop = shopRepository.getReferenceById(id);
         Category category = shop.getCategory();
         List<Category> categories = categoryRepository.findAll();
         String categoryName = shop.getCategory().getCategory();
         Integer categoryId = shop.getCategory().getId();
         System.out.println(categoryId);
         System.out.println(category);
         System.out.println(categoryName);
         String imageName = shop.getImageName();
//         List<Category> categories = categoryRepository.findAll();
//         System.out.println(categories);
         ShopEditForm form = new ShopEditForm(
        		 shop.getId(), shop.getName(),null, shop.getDescription(), 
        		 shop.getAddress(), shop.getPhoneNumber(), shop.getEmail()
//        		 ,shop.getCategory().getId()
        		 , (shop.getCategory() != null) ? shop.getCategory().getId() : null
        		 );
//         Category category = shop.getCategory();

//        		 );
        
//         form.setCategory(shop.getCategory());
         model.addAttribute("shopEditForm", form);
         model.addAttribute("imageName", imageName);
         model.addAttribute("category", category);
         model.addAttribute("categoryId", categoryId);
         model.addAttribute("categoryName", categoryName);
         model.addAttribute("categories", categories);
         System.out.println(categories);
//         System.out.println(categoryId);
         return "admin/shop/edit";
     }
     
     @PostMapping("/{id}/edit")
     public String updateShop(@PathVariable(name = "id") Integer id, @ModelAttribute ShopEditForm form
    		 				  ,BindingResult result, Model model) {
    	 System.out.println("テスト");
    	   if (result.hasErrors()) {
    	        List<Category> categories = categoryRepository.findAll();
    	        model.addAttribute("categories", categories);
    	        return "/admin/shop/edit"; // エラーページにリダイレクトします
    	    }
    	   
    	   System.out.println("テスト");
    	   
         Shop shop = shopRepository.getReferenceById(id);
         Category category = categoryRepository.findById(form.getCategory()).orElse(null);
         System.out.println(category);
         if (category != null) {
             shop.setCategory(category);
         }
         // フォームの他のプロパティも同様に設定
         shop.setName(form.getName());
         shop.setDescription(form.getDescription());
         shop.setAddress(form.getAddress());
         shop.setPhoneNumber(form.getPhoneNumber());
         shop.setEmail(form.getEmail());
         
         shopRepository.save(shop);
         
         
         return "redirect:/admin/shop";
     }
     
     
     @PostMapping("/edit")
     public String handleEditForm(@ModelAttribute ShopEditForm shopEditForm) {
         // 画像ファイルの取得を確認
         MultipartFile imageFile = shopEditForm.getImageFile();
         
         if (imageFile != null && !imageFile.isEmpty()) {
             // 画像を処理するロジックをここに追加
         }
         
         // フォームの処理が完了したら、次のページにリダイレクト
         return "redirect:/success";
     }


     @PostMapping("/{id}/update")
     public String update(@ModelAttribute @Validated ShopEditForm shopEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
         if (bindingResult.hasErrors()) {
             return "admin/shop/edit";
         }
         
         shopService.update(shopEditForm);
         redirectAttributes.addFlashAttribute("successMessage", "店舗情報を編集しました。");
         
         return "redirect:/admin/shop";
     }    
     
     @PostMapping("/{id}/delete")
     public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {        
         shopRepository.deleteById(id);
                 
         redirectAttributes.addFlashAttribute("successMessage", "店舗を削除しました。");
         
         return "redirect:/admin/shop";
     }    


     @GetMapping("/new")
     public String createShopForm(Model model) {
         model.addAttribute("shop", new Shop());
         model.addAttribute("categories", categoryRepository.findAll());
         return "shop/create";
     }

     @PostMapping
     public String saveShop(@ModelAttribute Shop shop) {
         shopService.saveShop(shop);
         return "redirect:/shops";
     }
}
