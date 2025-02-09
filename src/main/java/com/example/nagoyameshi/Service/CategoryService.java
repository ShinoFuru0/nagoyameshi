package com.example.nagoyameshi.Service;

import java.util.NoSuchElementException;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;

@Service
public class CategoryService {
	 private final CategoryRepository categoryRepository;
	 
	 public CategoryService( CategoryRepository categoryRepository) {
 
         this.categoryRepository = categoryRepository;
     }    
	 
	 public Category findById(Integer id) {
         return categoryRepository.findById(id).orElseThrow(() -> new NoSuchElementException("カテゴリが見つかりません"));
     }
	 public void updateCategory(CategoryEditForm form) {
         Category category = categoryRepository.findById(form.getId()).orElseThrow(() -> new NoSuchElementException("カテゴリが見つかりません"));
         category.setCategory(form.getCategory());
         

         
         categoryRepository.save(category);
     }
	 
	  @Transactional
	     public void create(CategoryRegisterForm categoryRegisterForm) {
	         Category category = new Category();        

	         category.setCategory(categoryRegisterForm.getCategory()); 
	         categoryRepository.save(category);
	        
	     }  
	 
	   @Transactional
	     public void update(CategoryEditForm categoryEditForm) {
	         Category category = categoryRepository.getReferenceById(categoryEditForm.getId());

	         
	         category.setCategory(categoryEditForm.getCategory());                

	         categoryRepository.save(category);
	     }
	     
}
