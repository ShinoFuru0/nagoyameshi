package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Reservations;
import com.example.nagoyameshi.entity.User;

public interface ReservationRepository extends JpaRepository<Reservations, Integer> {
	 public Page<Reservations> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);


}
