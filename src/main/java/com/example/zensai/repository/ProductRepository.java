package com.example.zensai.repository;

import com.example.zensai.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    @Query("""
        SELECT p FROM Product p
        WHERE FUNCTION('jsonb_extract_path_text', p.attributes, :key) = :value
    """)
    List<Product> findByAttributeValue(
            @Param("key") String key,
            @Param("value") String value
    );
}