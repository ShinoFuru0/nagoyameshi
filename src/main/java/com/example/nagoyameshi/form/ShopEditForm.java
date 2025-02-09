package com.example.nagoyameshi.form;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ShopEditForm {
	private final Integer id;
	private final String name;
	private final MultipartFile imageFile;
	 private final String description;
	 private final String address;
	 private final String phoneNumber;
	 private final String email;
	 private final Integer category;

}
