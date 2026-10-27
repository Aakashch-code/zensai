package com.example.zensai.controller;

import com.example.zensai.dto.ProductDto;
import com.example.zensai.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(requestDto));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String attributeKey,
            @RequestParam(required = false) String attributeValue,
            @ParameterObject Pageable pageable) {

        Page<ProductDto> products = productService.getProducts(
                search,
                category,
                minPrice,
                maxPrice,
                attributeKey,
                attributeValue,
                pageable
        );

        return ResponseEntity.ok(products);
    }
}