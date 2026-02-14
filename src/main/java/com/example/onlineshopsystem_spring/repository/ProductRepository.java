package com.example.onlineshopsystem_spring.repository;

import com.example.onlineshopsystem_spring.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {

    @Query("""
        SELECT p FROM Product p
        WHERE
            LOWER(p.title) LIKE LOWER(CONCAT(:keyword, '%'))
            OR LOWER(p.user.name) LIKE LOWER(CONCAT(:keyword, '%'))
    """)
    List<Product> searchProductByTitleAndUsername(@Param("keyword") String keyword);

    List<Product> findByCategoryId(Integer categoryId);

    @Query("""
        SELECT p FROM Product p
        WHERE
            (
                LOWER(p.title) LIKE LOWER(CONCAT(:keyword, '%'))
                OR LOWER(p.user.name) LIKE LOWER(CONCAT(:keyword, '%'))
            )
            AND p.category.id = :categoryId
    """)
    List<Product> searchProductsByKeywordAndCategory(@Param("keyword") String keyword,
                                               @Param("categoryId") Integer categoryId);

    List<Product>findByCategoryIdAndTitleStartingWithIgnoreCase(Integer categoryId, String keyword);
}
