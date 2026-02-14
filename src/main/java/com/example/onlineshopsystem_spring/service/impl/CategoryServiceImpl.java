package com.example.onlineshopsystem_spring.service.impl;

import com.example.onlineshopsystem_spring.model.Category;
import com.example.onlineshopsystem_spring.repository.CategoryRepository;
import com.example.onlineshopsystem_spring.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Integer categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public boolean deleteCategoryById(Integer id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return false;
        }
        if(!category.getProducts().isEmpty()) {
            return  false;
        }
        categoryRepository.delete(category);
        return true;
    }
}
