package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.epos.backend.model.entity.TrxStockMovement;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class TrxStockMovementSpecification {

    public static Specification<TrxStockMovement> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            String keyword = "%" + search.trim().toLowerCase() + "%";

            Join<Object, Object> product = root.join("product");
            Join<Object, Object> category = product.join("category");

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(product.get("name")), keyword));
            predicates.add(cb.like(cb.lower(category.get("name")), keyword));
            predicates.add(cb.like(cb.lower(root.get("referenceNo")), keyword));

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

}
