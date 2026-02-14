package com.example.onlineshopsystem_spring.repository;

import com.example.onlineshopsystem_spring.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
}
