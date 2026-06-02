package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.epos.backend.model.dto.request.search.PromoSearchRequest;
import com.epos.backend.model.entity.Promo;
import com.epos.backend.util.QueryUtil;

import jakarta.persistence.criteria.Predicate;

public class PromoSpecification {

    public static Specification<Promo> search(PromoSearchRequest request) {
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
                    cb.like(cb.lower(root.get("promoNo")), keyword),
                    cb.like(cb.lower(root.get("promoName")), keyword),
                    cb.like(cb.lower(root.get("promoCode")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword)));
            }

            if (StringUtils.hasText(request.getPromoNo())) {
                predicates.add(cb.like(cb.lower(root.get("promoNo")), QueryUtil.like(request.getPromoNo().trim().toLowerCase())));
            }

            if (StringUtils.hasText(request.getPromoName())) {
                predicates.add(cb.like(cb.lower(root.get("promoName")), QueryUtil.like(request.getPromoName().trim().toLowerCase())));
            }

            if (StringUtils.hasText(request.getPromoCode())) {
                predicates.add(cb.like(cb.lower(root.get("promoCode")), QueryUtil.like(request.getPromoCode().trim().toLowerCase())));
            }

            if (request.getApplyType() != null) {
                predicates.add(cb.equal(root.get("applyType"), request.getApplyType()));
            }

            if (request.getPromoType() != null) {
                predicates.add(cb.equal(root.get("promoType"), request.getPromoType()));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.getIsActive()));
            }

            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), request.getStartDate()));
            }

            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), request.getEndDate()));
            }

            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
}
