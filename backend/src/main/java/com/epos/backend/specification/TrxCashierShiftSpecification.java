package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.epos.backend.model.dto.request.search.TrxCashierShiftSearchRequest;
import com.epos.backend.model.entity.TrxCashierShift;

import jakarta.persistence.criteria.Predicate;

public class TrxCashierShiftSpecification {

    public static Specification<TrxCashierShift> filter(TrxCashierShiftSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String like = "%" + request.getKeyword().toLowerCase() + "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("shiftNo")), like),
                        cb.like(cb.lower(root.get("cashierUsername")), like),
                        cb.like(cb.lower(root.get("createdBy")), like),
                        cb.like(cb.lower(root.get("openNotes")), like),
                        cb.like(cb.lower(root.get("closeNotes")), like),
                        cb.like(cb.lower(root.get("status").as(String.class)), like)
                ));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("openedAt"), request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("openedAt"), request.getEndDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
