package com.example.nagoyameshi.form;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class ReservationInputForm {
	@NotNull(message = "利用日を選択してください。")
	 @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkinDate;


	 
}
