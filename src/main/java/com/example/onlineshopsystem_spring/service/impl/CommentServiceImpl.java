package com.example.onlineshopsystem_spring.service.impl;

import com.example.onlineshopsystem_spring.model.Comment;
import com.example.onlineshopsystem_spring.model.Product;
import com.example.onlineshopsystem_spring.model.User;
import com.example.onlineshopsystem_spring.model.UserType;
import com.example.onlineshopsystem_spring.repository.CommentRepository;
import com.example.onlineshopsystem_spring.repository.ProductRepository;
import com.example.onlineshopsystem_spring.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;

    @Override
    public Comment addComment(String text, int productId, User user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Comment comment = new Comment();
        comment.setComment(text);
        comment.setProduct(product);
        comment.setUser(user);

        return commentRepository.save(comment);
    }


    @Override
    public void deleteComment(int commentId, User user) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (comment.getUser().getId() !=(user.getId())
                && user.getType() != UserType.ADMIN) {
            throw new RuntimeException("You cannot delete this comment");
        }

        commentRepository.delete(comment);
    }


    @Override
    public List<Comment> findByProductId(Integer productId) {
        return commentRepository.findByProductIdOrderByIdDesc(productId);
    }

}
