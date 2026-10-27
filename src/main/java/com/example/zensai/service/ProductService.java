package com.example.zensai.service;

import com.example.zensai.domain.Product;
import com.example.zensai.dto.ProductDto;
import com.example.zensai.repository.ProductRepository;
import com.example.zensai.security.TenantContext;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductDto createProduct(ProductDto dto) {
        String currentTenant = TenantContext.getCurrentTenant();

        if (currentTenant == null || "GLOBAL".equals(currentTenant)) {
            throw new IllegalStateException("Only store administrators can create products.");
        }

        Product product = new Product();
        product.setTenantId(currentTenant); 
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setAttributes(dto.getAttributes());

        Product savedProduct = productRepository.save(product);

        return mapToDto(savedProduct);
    }

    public Page<ProductDto> getProducts(
            String search,
            String category,
            Double minPrice,
            Double maxPrice,
            String attrKey,
            String attrValue,
            Pageable pageable) {

        String currentTenant = TenantContext.getCurrentTenant();

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (currentTenant != null && !"GLOBAL".equals(currentTenant)) {
                predicates.add(cb.equal(root.get("tenantId"), currentTenant));
            }

            if (search != null && !search.trim().isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("name")), likePattern),
                                cb.like(cb.lower(root.get("category")), likePattern)
                        )
                );
            }

            if (category != null && !category.trim().isEmpty()) {
                predicates.add(
                        cb.equal(
                                cb.lower(root.get("category")),
                                category.toLowerCase()
                        )
                );
            }

            if (minPrice != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("price"),
                                minPrice
                        )
                );
            }

            if (maxPrice != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("price"),
                                maxPrice
                        )
                );
            }

            if (attrKey != null && attrValue != null && !attrKey.trim().isEmpty()) {
                predicates.add(
                        cb.equal(
                                cb.function(
                                        "jsonb_extract_path_text",
                                        String.class,
                                        root.get("attributes"),
                                        cb.literal(attrKey)
                                ),
                                attrValue
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository
                .findAll(spec, pageable)
                .map(this::mapToDto);
    }

    private ProductDto mapToDto(Product entity) {
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setCategory(entity.getCategory());
        dto.setAttributes(entity.getAttributes());
        return dto;
    }
}