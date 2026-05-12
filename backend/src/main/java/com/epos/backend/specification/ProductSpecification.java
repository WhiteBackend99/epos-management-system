package com.epos.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.epos.backend.model.entity.Product;

public class ProductSpecification {

    public static Specification<Product> search(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + search.toLowerCase() + "%";
            
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("sku")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("barcode")), pattern)
            );
        };
    }

    public static Specification<Product> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    public static Specification<Product> active(Boolean active) {
        if (active == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), active);
    }

}
