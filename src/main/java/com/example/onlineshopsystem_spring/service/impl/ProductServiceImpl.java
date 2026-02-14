package com.example.onlineshopsystem_spring.service.impl;

import com.example.onlineshopsystem_spring.model.Product;
import com.example.onlineshopsystem_spring.repository.ProductRepository;
import com.example.onlineshopsystem_spring.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Value("${product.image.directory.path}")
    private String imageDirectoryPath;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product save(Product product, MultipartFile multipartFile) {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageDirectoryPath + fileName);

            try {
                multipartFile.transferTo(file);
                product.setPicName(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return productRepository.save(product);
    }

    @Override
    public Product changeProduct(Product product, MultipartFile multipartFile) {

        Product dbProduct = productRepository.findById(product.getId())
                .orElseThrow(()->new RuntimeException("Product not found"));

        dbProduct.setTitle(product.getTitle());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setUser(product.getUser());


        if (multipartFile != null && !multipartFile.isEmpty()) {

            if(dbProduct.getPicName() != null) {
                File previousFile = new File(imageDirectoryPath + dbProduct.getPicName());
                if(previousFile.exists()) {
                    previousFile.delete();
                }
            }

            String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File newFile = new File(imageDirectoryPath + fileName);

            try {
                multipartFile.transferTo(newFile);
                dbProduct.setPicName(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return productRepository.save(dbProduct);
    }



    @Override
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> findByCategoryId(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> searchProductsByTitleAndUsername(String keyword) {
        return productRepository.searchProductByTitleAndUsername(keyword);
    }

    @Override
    public List<Product> searchProductAndFilter(String keyword, Integer categoryId) {
        if(keyword != null && !keyword.isBlank() && categoryId != null) {
            return productRepository.searchProductsByKeywordAndCategory(keyword, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            return productRepository.searchProductByTitleAndUsername(keyword);
        }

        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        return productRepository.findAll();
    }

    @Override
    public List<Product> searchProductByCategoryIdAndTitle(Integer categoryId, String keyword) {
        return productRepository.findByCategoryIdAndTitleStartingWithIgnoreCase(categoryId, keyword);
    }

}
