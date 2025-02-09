package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Shop;

public interface ShopRepository extends JpaRepository<Shop, Integer>{
 public Page<Shop> findByNameLike(String keyword,Pageable pageable);
 public Page<Shop> findByCategory(Integer categoryId,Pageable pageable);
 
 public List<Shop> findTop10ByOrderByCreatedAtDesc();
 List<Shop> findByCategoryId(Integer categoryId);
 public Shop getReferenceById(Integer id);
}
