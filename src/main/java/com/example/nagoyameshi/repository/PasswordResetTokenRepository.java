package com.example.nagoyameshi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
	 public PasswordResetToken findByToken(String token);
	 @Transactional
	public void deleteByUser(User user);
	

	
}
