package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Fav;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;

@Controller
public class FavController {
 private final FavRepository favRepository;

 
 public FavController(FavRepository favRepository) {
	 this.favRepository = favRepository;
 }
 
 @GetMapping("/fav")
 public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "favId", direction = Direction.ASC)Pageable pageable, Model model) {
 User user = userDetailsImpl.getUser();
 Page<Fav> favPage = favRepository.findByUserOrderByCreatedAtDesc(user,pageable);
 
 model.addAttribute("favPage",favPage);
 
 return "fav/index";
}
}
