package com.example.onlineshopsystem_spring.service;

import com.example.onlineshopsystem_spring.model.Comment;
import com.example.onlineshopsystem_spring.model.User;

import java.util.List;

public interface CommentService {
    Comment addComment(String comment, int productId, User userId);

    void deleteComment(int commentId, User userId);

    List<Comment> findByProductId(Integer productId);

}
