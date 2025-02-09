package com.example.nagoyameshi.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Fav;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.FavForm;
import com.example.nagoyameshi.repository.FavRepository;
import com.example.nagoyameshi.repository.ShopRepository;

@Service
public class FavService {
	private final FavRepository favRepository;
	private final ShopRepository shopRepository;
	
	public FavService(FavRepository favRepository,ShopRepository shopRepository) {
		this.favRepository = favRepository;
		this.shopRepository = shopRepository;
	}
	@Transactional
	public void create(
			Shop shop,User user,

			 FavForm favForm
			 ) {
		 favForm.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

		    
		   
	  //すでにいいねしていた場合、いいねを取り消す
	  if(favRepository.existsByUserAndShop(user, shop) == true) {
	      favRepository.deleteByUserAndShop(user, shop);
	  }else {  //いいねしていなかった場合、投稿へのいいねを登録する
		 Fav fav = new Fav();

	    fav.setUser(user);
	    fav.setShop(shop);
	    LocalDateTime ldt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	    fav.setCreatedAt(ldt);
	    fav.setUpdatedAt(ldt);
	    favRepository.save(fav);
	  }
	}

	public boolean isFav(Shop shop, User user) {
				
		 return favRepository.existsByUserAndShop(user, shop);
		
		
	}
	

}
