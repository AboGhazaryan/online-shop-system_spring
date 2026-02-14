package com.example.onlineshopsystem_spring.service;

import com.example.onlineshopsystem_spring.model.Product;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface ProductService {
    List<Product> findAll();

    Product findById(Integer id);

    Product save(Product product, MultipartFile multipartFile);

    Product changeProduct(Product product, MultipartFile multipartFile);

    void deleteById(Integer id);

    List<Product> findByCategoryId(Integer categoryId);

    List<Product> searchProductsByTitleAndUsername(String keyword);

    List<Product> searchProductAndFilter(String keyword, Integer categoryId);

    List<Product> searchProductByCategoryIdAndTitle(Integer categoryId, String keyword);
}
