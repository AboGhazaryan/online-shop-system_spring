package com.example.onlineshopsystem_spring.repository;

import com.example.onlineshopsystem_spring.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {

    List<Comment> findByProductIdOrderByIdDesc(Integer productId);

}
