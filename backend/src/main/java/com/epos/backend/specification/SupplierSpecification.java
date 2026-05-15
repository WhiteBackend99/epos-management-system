package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.epos.backend.model.dto.request.search.SupplierSearchRequest;
import com.epos.backend.model.entity.Supplier;
import com.epos.backend.util.ParseUtil;
import com.epos.backend.util.QueryUtil;

import jakarta.persistence.criteria.Predicate;

public class SupplierSpecification {

    public static Specification<Supplier> filter(SupplierSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (request == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            if (ParseUtil.hasText(request.getSupplierCode())) {
                predicates.add(cb.like(cb.lower(root.get("supplierCode")), QueryUtil.like(request.getSupplierCode())));
            }

            if (ParseUtil.hasText(request.getPhone())) {
                predicates.add(cb.like(cb.lower(root.get("phone")), QueryUtil.like(request.getPhone())));
            }

            if (ParseUtil.hasText(request.getEmail())) {
                predicates.add(cb.like(cb.lower(root.get("email")), QueryUtil.like(request.getEmail())));
            }

            if (ParseUtil.hasText(request.getPicName())) {
                predicates.add(cb.like(cb.lower(root.get("picName")), QueryUtil.like(request.getPicName())));
            }

            if (request.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.getIsActive()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
