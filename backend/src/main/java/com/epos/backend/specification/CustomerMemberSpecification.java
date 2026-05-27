package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.epos.backend.model.dto.request.search.CustomerMemberSearchRequest;
import com.epos.backend.model.entity.CustomerMember;
import com.epos.backend.util.QueryUtil;

import jakarta.persistence.criteria.Predicate;

public class CustomerMemberSpecification {

    public static Specification<CustomerMember> filter(CustomerMemberSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = QueryUtil.like(request.getKeyword());

                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("memberCode")), keyword),
                    cb.like(cb.lower(root.get("fullName")), keyword),
                    cb.like(cb.lower(root.get("phoneNumber")), keyword),
                    cb.like(cb.lower(root.get("email")), keyword)
                ));
            }

            if (StringUtils.hasText(request.getMemberCode())) {
                predicates.add(cb.like(cb.lower(root.get("memberCode")), QueryUtil.like(request.getMemberCode().toLowerCase())));
            }

            if (StringUtils.hasText(request.getFullName())) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), QueryUtil.like(request.getFullName().toLowerCase())));
            }

            if (StringUtils.hasText(request.getPhoneNumber())) {
                predicates.add(cb.like(cb.lower(root.get("phoneNumber")), QueryUtil.like(request.getPhoneNumber().toLowerCase())));
            }

            if (StringUtils.hasText(request.getEmail())) {
                predicates.add(cb.like(cb.lower(root.get("email")), QueryUtil.like(request.getEmail().toLowerCase())));
            }

            if (request.getMemberStatus() != null) {
                predicates.add(cb.equal(root.get("memberStatus"), request.getMemberStatus()));
            }

            if (request.getMemberTierId() != null) {
                predicates.add(cb.equal(root.get("memberTier").get("id"), request.getMemberTierId()));
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
