package com.example.nagoyameshi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Entity
@Table(name="fav")
@Data

public class Fav {
	@Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name="id")
	 private Integer favId;
	
	@ManyToOne
	 @JoinColumn(name="shop_id")
	 private Shop shop;
	
	@ManyToOne
	 @JoinColumn(name="user_id")
	 private User user;
	
	@Column(name = "created_at", insertable = false, updatable = false)	  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@CreationTimestamp
	  private LocalDateTime createdAt;
	
	 @Column(name = "updated_at", insertable = false)
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@UpdateTimestamp
	 private LocalDateTime updatedAt;
	
}