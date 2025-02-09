package com.example.nagoyameshi.form;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
	 
@Data
	 public class ShopRegisterForm {
	     @NotBlank(message = "店舗名を入力してください。")
	     private String name;
	         
	     private MultipartFile imageFile;
	     
	     @NotBlank(message = "説明を入力してください。")
	     private String description;   
	     
	     @NotNull(message = "カテゴリを入力してください。")
	     private Integer  category;  
	     
	     
	     @NotBlank(message = "住所を入力してください。")
	     private String address;
	     
	     @NotBlank(message = "電話番号を入力してください。")
	     private String phoneNumber;
	     
	     @NotBlank(message = "メールアドレスを入力してください。")
	     private String email;

		
	}

