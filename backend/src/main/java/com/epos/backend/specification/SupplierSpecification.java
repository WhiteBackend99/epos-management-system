package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.epos.backend.model.dto.request.search.SupplierSearchRequest;
import com.epos.backend.model.entity.Supplier;
import com.epos.backend.util.QueryUtil;

import jakarta.persistence.criteria.Predicate;

public class SupplierSpecification {

    public static Specification<Supplier> filter(SupplierSearchRequest request) {
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
                    cb.like(cb.lower(root.get("supplierCode")), keyword),
                    cb.like(cb.lower(root.get("name")), keyword),
                    cb.like(cb.lower(root.get("phone")), keyword),
                    cb.like(cb.lower(root.get("email")), keyword),
                    cb.like(cb.lower(root.get("picName")), keyword)));
            }

            if (StringUtils.hasText(request.getSupplierCode())) {
                predicates.add(cb.like(cb.lower(root.get("supplierCode")), QueryUtil.like(request.getSupplierCode().trim().toLowerCase())));
            }

            if (StringUtils.hasText(request.getName())) {
                predicates.add(cb.like(cb.lower(root.get("name")), QueryUtil.like(request.getName().trim().toLowerCase())));
            }

            if (StringUtils.hasText(request.getPhone())) {
                predicates.add(cb.like(cb.lower(root.get("phone")), QueryUtil.like(request.getPhone().trim().toLowerCase())));
            }

            if (StringUtils.hasText(request.getEmail())) {
                predicates.add(cb.like(cb.lower(root.get("email")), QueryUtil.like(request.getEmail().trim().toLowerCase())));
            }

            if (StringUtils.hasText(request.getPicName())) {
                predicates.add(cb.like(cb.lower(root.get("picName")), QueryUtil.like(request.getPicName().trim().toLowerCase())));
            }

            if (request.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.getIsActive()));
            }

            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
