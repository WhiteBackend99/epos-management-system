package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.epos.backend.model.dto.request.search.ProductSearchRequest;
import com.epos.backend.model.entity.Product;
import com.epos.backend.util.QueryUtil;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

    public static Specification<Product> search(ProductSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (request == null) {
                query.orderBy(cb.desc(root.get("createdAt")));
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = QueryUtil.like(request.getKeyword().trim().toLowerCase());

                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("sku")), keyword),
                    cb.like(cb.lower(root.get("barcode")), keyword),
                    cb.like(cb.lower(root.get("name")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword),
                    cb.like(cb.lower(root.get("category").get("name")), keyword)
                ));
            }

            if (StringUtils.hasText(request.getSku())) {
                predicates.add(cb.like(cb.lower(root.get("sku")), QueryUtil.like(request.getSku().trim().toLowerCase())));
            }

            if (StringUtils.hasText(request.getBarcode())) {
                predicates.add(cb.like(cb.lower(root.get("barcode")), QueryUtil.like(request.getBarcode().trim().toLowerCase())));
            }

            if (StringUtils.hasText(request.getName())) {
                predicates.add(cb.like(cb.lower(root.get("name")), QueryUtil.like(request.getName().trim().toLowerCase())));
            }

            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), request.getCategoryId()));
            }

            if (request.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.getIsActive()));
            }

            if (request.getMinSellingPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("sellingPrice"), request.getMinSellingPrice()));
            }

            if (request.getMaxSellingPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("sellingPrice"), request.getMaxSellingPrice()));
            }

            if (request.getMinStock() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("stock"), request.getMinStock()));
            }

            if (request.getMaxStock() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("stock"), request.getMaxStock()));
            }

            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
