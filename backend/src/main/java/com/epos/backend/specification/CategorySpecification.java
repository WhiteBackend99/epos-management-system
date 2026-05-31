package com.epos.backend.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.epos.backend.model.dto.request.search.CategorySearchRequest;
import com.epos.backend.model.entity.Category;
import com.epos.backend.util.QueryUtil;

import jakarta.persistence.criteria.Predicate;

public class CategorySpecification {

    public static Specification<Category> search(CategorySearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = QueryUtil.like(request.getKeyword());
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword)
                ));
            }

            if (StringUtils.hasText(request.getName())) {
                predicates.add(cb.like(cb.lower(root.get("name")), QueryUtil.like(request.getName().toLowerCase())));
            }

            if (request.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.getIsActive()));
            }

            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
}
