package com.example.onlineshopsystem_spring.service;

import com.example.onlineshopsystem_spring.model.Product;
import com.example.onlineshopsystem_spring.service.security.SpringUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    Page<Product> findAll(Pageable pageable);

    Product findById(Integer id);

    Product save(Product product, MultipartFile multipartFile, SpringUser springUser);

    Product changeProduct(Product product, MultipartFile multipartFile, SpringUser springUser);

    void deleteById(Integer id);

    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);

    Page<Product> searchProductsByTitleAndUsername(String keyword,Pageable pageable);

    Page<Product> searchProductAndFilter(String keyword, Integer categoryId,Pageable pageable);

    Page<Product> searchProductByCategoryIdAndTitle(Integer categoryId, String keyword , Pageable pageable);



}
