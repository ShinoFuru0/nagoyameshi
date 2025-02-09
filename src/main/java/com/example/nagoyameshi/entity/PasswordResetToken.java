package com.example.nagoyameshi.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "password_tokens")
@Data
public class PasswordResetToken {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
	private String token;
	
	
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;    
    
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
    
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt; 
}
