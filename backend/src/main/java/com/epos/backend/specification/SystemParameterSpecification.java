package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.epos.backend.model.dto.request.search.SystemParameterSearchRequest;
import com.epos.backend.model.entity.SystemParameter;
import com.epos.backend.util.QueryUtil;

import jakarta.persistence.criteria.Predicate;

public class SystemParameterSpecification {

    public static Specification<SystemParameter> search(SystemParameterSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = QueryUtil.like(request.getKeyword());
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("parameterCode")), keyword),
                    cb.like(cb.lower(root.get("parameterName")), keyword),
                    cb.like(cb.lower(root.get("parameterValue")), keyword)
                ));
            }

            if (StringUtils.hasText(request.getParameterCode())) {
                predicates.add(cb.like( cb.lower(root.get("parameterCode")), QueryUtil.like(request.getParameterCode().toLowerCase())));
            }

            if (StringUtils.hasText(request.getParameterType())) {
                predicates.add(cb.equal(root.get("parameterType"), request.getParameterType()));
            }

            if (request.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.getIsActive()));
            }

            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
}
