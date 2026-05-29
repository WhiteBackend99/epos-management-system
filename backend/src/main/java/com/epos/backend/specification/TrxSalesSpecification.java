package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.epos.backend.model.dto.request.search.PosSalesSearchRequest;
import com.epos.backend.model.entity.TrxSales;
import com.epos.backend.model.entity.TrxSalesPayment;
import com.epos.backend.util.QueryUtil;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class TrxSalesSpecification {

    public static Specification<TrxSales> filter(PosSalesSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = QueryUtil.like(request.getKeyword().toLowerCase());

                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("salesNo")), keyword),
                    cb.like(cb.lower(root.get("customerName")), keyword),
                    cb.like(cb.lower(root.get("memberCodeSnapshot")), keyword),
                    cb.like(cb.lower(root.get("memberNameSnapshot")), keyword)
                ));
            }

            if (StringUtils.hasText(request.getSalesNo())) {
                predicates.add(cb.like(cb.lower(root.get("salesNo")), QueryUtil.like(request.getSalesNo().toLowerCase())));
            }

            if (StringUtils.hasText(request.getCustomerName())) {
                predicates.add(cb.like(cb.lower(root.get("customerName")), QueryUtil.like(request.getCustomerName().toLowerCase())));
            }

            if (StringUtils.hasText(request.getMemberCode())) {
                predicates.add(cb.like(cb.lower(root.get("memberCodeSnapshot")), QueryUtil.like(request.getMemberCode().toLowerCase())));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (StringUtils.hasText(request.getCashierUsername())) {
                predicates.add(cb.equal(root.get("createdBy"), request.getCashierUsername()));
            }

            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getStartDate()));
            }

            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.getEndDate()));
            }

            if (request.getMinGrandTotal() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("grandTotal"), request.getMinGrandTotal()));
            }

            if (request.getMaxGrandTotal() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("grandTotal"), request.getMaxGrandTotal()));
            }

            if (request.getPaymentMethod() != null) {
                Join<TrxSales, TrxSalesPayment> paymentJoin = root.join("payments", JoinType.LEFT);
                predicates.add(cb.equal(paymentJoin.get("paymentMethod"), request.getPaymentMethod()));
                query.distinct(true);
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
