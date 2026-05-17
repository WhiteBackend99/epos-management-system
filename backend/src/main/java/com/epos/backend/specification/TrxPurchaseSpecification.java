package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.epos.backend.model.dto.request.search.TrxPurchaseSearchRequest;
import com.epos.backend.model.entity.TrxPurchase;
import com.epos.backend.model.entity.TrxPurchaseDetail;
import com.epos.backend.util.ParseUtil;
import com.epos.backend.util.QueryUtil;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class TrxPurchaseSpecification {

    public static Specification<TrxPurchase> filter(TrxPurchaseSearchRequest request) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (request == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            if (ParseUtil.hasText(request.getPurchaseNo())) {
                predicates.add(cb.like(cb.lower(root.get("purchaseNo")), QueryUtil.like(request.getPurchaseNo())));
            }
            if (ParseUtil.hasText(request.getInvoiceNo())) {
                predicates.add(cb.like(cb.lower(root.get("invoiceNo")), QueryUtil.like(request.getInvoiceNo())));
            }
            if (ParseUtil.hasText(request.getSupplierName())) {
                predicates.add(cb.like(cb.lower(root.get("supplier").get("name")), QueryUtil.like(request.getSupplierName())));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getPurchaseDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("purchaseDate"), request.getPurchaseDateFrom()));
            }
            if (request.getPurchaseDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("purchaseDate"), request.getPurchaseDateTo()));
            }
            if (ParseUtil.hasText(request.getProductName()) || ParseUtil.hasText(request.getSku())) {
                Join<TrxPurchase, TrxPurchaseDetail> detailJoin = root.join("details", JoinType.LEFT);

                if (ParseUtil.hasText(request.getProductName())) {
                    predicates.add(cb.like(cb.lower(detailJoin.get("productNameSnapshot")), QueryUtil.like(request.getProductName())));
                }
                if (ParseUtil.hasText(request.getSku())) {
                    predicates.add(cb.like(cb.lower(detailJoin.get("skuSnapshot")), QueryUtil.like(request.getSku())));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
