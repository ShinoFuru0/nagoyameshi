package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nagoyameshi.entity.Category;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, Integer>{
	public Category  getReferenceById(Integer id);
	public Page<Category> findByCategoryLike(String keyword,Pageable pageable);
	public Page<Category> findByCategory(String category,Pageable pageable);
	 public List<Category> findTop10ByOrderById();
}
