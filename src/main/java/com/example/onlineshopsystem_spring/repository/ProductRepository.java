package com.example.onlineshopsystem_spring.repository;

import com.example.onlineshopsystem_spring.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product,Integer> {

    @Query("""
        SELECT p FROM Product p
        WHERE
            LOWER(p.title) LIKE LOWER(CONCAT(:keyword, '%'))
            OR LOWER(p.user.name) LIKE LOWER(CONCAT(:keyword, '%'))
    """)
    Page<Product> searchProductByTitleAndUsername(@Param("keyword") String keyword,Pageable pageable);

    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        WHERE
            (
                LOWER(p.title) LIKE LOWER(CONCAT(:keyword, '%'))
                OR LOWER(p.user.name) LIKE LOWER(CONCAT(:keyword, '%'))
            )
            AND p.category.id = :categoryId
    """)
    Page<Product> searchProductsByKeywordAndCategory(@Param("keyword") String keyword,
                                               @Param("categoryId") Integer categoryId,Pageable pageable);

    Page<Product>findByCategoryIdAndTitleStartingWithIgnoreCase(Integer categoryId, String keyword,Pageable pageable);
}
