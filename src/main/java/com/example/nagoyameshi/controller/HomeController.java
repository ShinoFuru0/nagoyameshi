package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Controller
public class HomeController {
     private final ShopRepository shopRepository;
     private final UserRepository userRepository;
     
     public HomeController(ShopRepository shopRepository,UserRepository userRepository) {
         this.shopRepository = shopRepository;   
         this.userRepository = userRepository;
     }    
     
     @GetMapping("/")
     public String index(Model model) {
         List<Shop> newShop = shopRepository.findTop10ByOrderByCreatedAtDesc();
         model.addAttribute("newShop", newShop);        
        
        return "index";
}
}