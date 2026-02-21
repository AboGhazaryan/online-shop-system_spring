package com.example.onlineshopsystem_spring.service;

import com.example.onlineshopsystem_spring.model.Category;

import java.util.List;

public interface CategoryService  {

    List<Category> findAll();

    Category findById(Integer categoryId);

    Category save(Category category);

    boolean deleteCategoryById(Integer id);

}
