package com.example.nagoyameshi.Service;

import java.time.LocalDate;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Reservations;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.ShopRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReservationService {
	 private final ReservationRepository reservationRepository;  
     private final ShopRepository shopRepository;  
     private final UserRepository userRepository;  
     
     public ReservationService(ReservationRepository reservationRepository, ShopRepository shopRepository, UserRepository userRepository) {
         this.reservationRepository = reservationRepository;  
         this.shopRepository = shopRepository;  
         this.userRepository = userRepository;  
     }    
     
     @Transactional
     public void create(ReservationRegisterForm reservationRegisterForm) { 
         Reservations reservations = new Reservations();
         Shop shop = shopRepository.getReferenceById(reservationRegisterForm.getShopId());
         User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
         LocalDate checkinDate = reservationRegisterForm.getCheckinDate(); 
                  
                 
         reservations.setShop(shop);
         reservations.setUser(user);
         reservations.setCheckinDate(checkinDate);
         
         
         reservationRepository.save(reservations);
     }    
    
}
