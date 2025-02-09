package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class ReviewEditForm {
	 @NotNull
	  private Integer id;
	 
	 @NotNull
	  private Integer content;
}
