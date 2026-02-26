package com.example.onlineshopsystem_spring.service.impl;

import com.example.onlineshopsystem_spring.model.Product;
import com.example.onlineshopsystem_spring.repository.ProductRepository;
import com.example.onlineshopsystem_spring.service.ProductService;
import com.example.onlineshopsystem_spring.service.security.SpringUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Value("${product.image.directory.path}")
    private String imageDirectoryPath;

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product save(Product product, MultipartFile multipartFile, SpringUser springUser) {

        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageDirectoryPath + fileName);

            try {
                multipartFile.transferTo(file);
                product.setPicName(fileName);
                log.info("File uploaded successfully: {},by user:{}", fileName, springUser.getUsername());
            } catch (IOException e) {
                log.error("Failed to upload file '{}': {}", multipartFile.getOriginalFilename(), e.getMessage());

            }
        }

        return productRepository.save(product);
    }

    @Override
    public Product changeProduct(Product product, MultipartFile multipartFile,SpringUser springUser) {

        Product dbProduct = productRepository.findById(product.getId())
                .orElseThrow(()->new RuntimeException("Product not found"));

        boolean isOwner = dbProduct.getUser().getEmail().equals(springUser.getUsername());
        boolean isAdmin = springUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        if (!isOwner && !isAdmin) {
            log.error("Unauthorized edit attempt: user={} productId={}",
                    springUser.getUsername(), product.getId());
            throw new RuntimeException("You are not allowed to edit this product");
        }

        dbProduct.setTitle(product.getTitle());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());

        if (isAdmin && product.getUser() != null) {
            dbProduct.setUser(product.getUser());
        }



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
                log.error("Failed to upload file '{}': {}", multipartFile.getOriginalFilename(), e.getMessage());
            }
        }

        return productRepository.save(dbProduct);
    }



    @Override
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> findByCategoryId(Integer categoryId,Pageable pageable) {
        return productRepository.findByCategoryId(categoryId,pageable);
    }

    @Override
    public Page<Product> searchProductsByTitleAndUsername(String keyword,Pageable pageable) {
        return productRepository.searchProductByTitleAndUsername(keyword,pageable);
    }

    @Override
    public Page<Product> searchProductAndFilter(String keyword, Integer categoryId,Pageable pageable) {
        if(keyword != null && !keyword.isBlank() && categoryId != null) {
            return productRepository.searchProductsByKeywordAndCategory(keyword, categoryId, pageable);
        }
        if (keyword != null && !keyword.isBlank()) {
            return productRepository.searchProductByTitleAndUsername(keyword, pageable);
        }

        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId, pageable);
        }
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> searchProductByCategoryIdAndTitle(Integer categoryId, String keyword,Pageable pageable) {
        return productRepository.findByCategoryIdAndTitleStartingWithIgnoreCase(categoryId, keyword, pageable);
    }
}
