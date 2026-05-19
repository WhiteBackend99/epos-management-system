package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.epos.backend.model.dto.request.search.TrxSalesReturnSearchRequest;
import com.epos.backend.model.entity.TrxSalesReturn;
import com.epos.backend.util.QueryUtil;

import jakarta.persistence.criteria.Predicate;

public class TrxSalesReturnSpecification {

    public static Specification<TrxSalesReturn> filter (TrxSalesReturnSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("returnNo")), QueryUtil.like(request.getKeyword())),
                    cb.like(cb.lower(root.get("salesNo")), QueryUtil.like(request.getKeyword())),
                    cb.like(cb.lower(root.get("customerName")), QueryUtil.like(request.getKeyword())),
                    cb.like(cb.lower(root.get("createdBy")), QueryUtil.like(request.getKeyword()))
                ));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("returnStatus"), request.getStatus()));
            }
            
            if (request.getRefundStatus() != null) {
                predicates.add(cb.equal(root.get("refundStatus"), request.getRefundStatus()));
            }

            if (request.getStartDate() != null && request.getEndDate() != null) {
                predicates.add(cb.between(root.get("createdAt"), request.getStartDate(), request.getEndDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
