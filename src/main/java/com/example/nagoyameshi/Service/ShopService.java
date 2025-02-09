package com.example.nagoyameshi.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Shop;
import com.example.nagoyameshi.form.ShopEditForm;
import com.example.nagoyameshi.form.ShopRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.ShopRepository;

@Service
public class ShopService {
 private final ShopRepository shopRepository;   
 private final CategoryRepository categoryRepository;
     
     public ShopService(ShopRepository shopRepository, CategoryRepository categoryRepository) {
         this.shopRepository = shopRepository;   
         this.categoryRepository = categoryRepository;
     }    
     
     public Shop findById(Integer id) {
         return shopRepository.findById(id).orElseThrow(() -> new NoSuchElementException("店舗が見つかりません"));
     }
     
     public void updateShop(ShopEditForm form) {
         Shop shop = shopRepository.findById(form.getId()).orElseThrow(() -> new NoSuchElementException("店舗が見つかりません"));
         shop.setName(form.getName());
         shop.setDescription(form.getDescription());
         shop.setAddress(form.getAddress());
         shop.setPhoneNumber(form.getPhoneNumber());
         shop.setEmail(form.getEmail());

         shopRepository.save(shop);
     }
     
     @Transactional
     public void create(ShopRegisterForm shopRegisterForm) {
         Shop shop = new Shop();  
         Category category = categoryRepository.getReferenceById(shopRegisterForm.getCategory());

         MultipartFile imageFile = shopRegisterForm.getImageFile();
         
         if (!imageFile.isEmpty()) {
             String imageName = imageFile.getOriginalFilename(); 
             String hashedImageName = generateNewFileName(imageName);
             Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
             copyImageFile(imageFile, filePath);
             shop.setImageName(hashedImageName);
         }
         
         
         shop.setName(shopRegisterForm.getName());                
         shop.setDescription(shopRegisterForm.getDescription());
         shop.setCategory(category);
         shop.setAddress(shopRegisterForm.getAddress());
         shop.setPhoneNumber(shopRegisterForm.getPhoneNumber());
         shop.setEmail(shopRegisterForm.getEmail());
                     
         shopRepository.save(shop);
     }  
     
     public List<Shop> getShopsByCategory(Integer categoryId) {
         return shopRepository.findByCategoryId(categoryId);
     }
     
     public List<Shop> getAllShops() {
         return shopRepository.findAll();
     }
     
     public void saveShop(Shop shop) {
         shopRepository.save(shop);
     }
     
     @Transactional
     public void update(ShopEditForm shopEditForm) {
         Shop shop = shopRepository.getReferenceById(shopEditForm.getId());
         MultipartFile imageFile = shopEditForm.getImageFile();
         
         if (!imageFile.isEmpty()) {
             String imageName = imageFile.getOriginalFilename(); 
             String hashedImageName = generateNewFileName(imageName);
             Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
             copyImageFile(imageFile, filePath);
             shop.setImageName(hashedImageName);
         }
         
         shop.setName(shopEditForm.getName());                
         shop.setDescription(shopEditForm.getDescription());
//         shop.setCategoryId(shopEditForm.getCategory());
        
         shop.setAddress(shopEditForm.getAddress());
         shop.setPhoneNumber(shopEditForm.getPhoneNumber());
         shop.setEmail(shopEditForm.getEmail());
                     
         shopRepository.save(shop);
     }
     
     // UUIDを使って生成したファイル名を返す
     public String generateNewFileName(String fileName) {
         String[] fileNames = fileName.split("\\.");                
         for (int i = 0; i < fileNames.length - 1; i++) {
             fileNames[i] = UUID.randomUUID().toString();            
         }
         String hashedFileName = String.join(".", fileNames);
         return hashedFileName;
     }     
     
     // 画像ファイルを指定したファイルにコピーする
     public void copyImageFile(MultipartFile imageFile, Path filePath) {           
         try {
             Files.copy(imageFile.getInputStream(), filePath);
         } catch (IOException e) {
             e.printStackTrace();
         }          
     } 
 }
