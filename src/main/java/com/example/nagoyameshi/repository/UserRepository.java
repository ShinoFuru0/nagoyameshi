package com.example.nagoyameshi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {	
    // メールアドレスでユーザーを検索
    public User findByEmail(String email);

    // IDでユーザーを取得（参照用）
    public User getReferenceById(Integer id);
    
    // 名前またはふりがなで検索
    public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);

    // Stripeの顧客IDでユーザーを検索
    Optional<User> findByCustomerId(String customerId);

    // 名前でユーザーを取得
    public User getByName(String name);

    // 名前でユーザーを検索
    Optional<User> findByName(String name);
    
    // IDでユーザーを検索
    public Optional<User> findById(Integer userId);

}



//public interface UserRepository extends JpaRepository<User, Integer> {	
// 	public User findByEmail(String email);
// 	public User  getReferenceById(Integer id);
// 	 public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
// 	 Optional<User> findByCustomerId(String customerId);
//	public User getByName(String name);
//	Optional<User> findByName(String name);
//	public Optional<User> findById(Integer userId);
//}
