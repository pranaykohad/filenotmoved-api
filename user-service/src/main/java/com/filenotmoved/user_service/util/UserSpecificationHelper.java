package com.filenotmoved.user_service.util;

import java.util.List;

import org.hibernate.query.sqm.PathElementException;
import org.springframework.data.jpa.domain.Specification;

import com.filenotmoved.user_service.dto.SearchFilter;
import com.filenotmoved.user_service.entity.Issues;
import com.filenotmoved.user_service.exception.custom.GenericException;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserSpecificationHelper {

    public static Specification<Issues> buildSpecification(List<SearchFilter> filterList) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            for (SearchFilter filter : filterList) {
                final Predicate newPredicate = buildPredicate(filter, root, criteriaBuilder);
                if (newPredicate != null) {
                    predicate = criteriaBuilder.and(predicate, newPredicate);
                }
            }
            return predicate;
        };
    }

    private static Predicate buildPredicate(SearchFilter criteria, Root<Issues> root, CriteriaBuilder criteriaBuilder) {
        final String key = criteria.getKey();
        final Object value = criteria.getValue();

        try {
            switch (criteria.getOperator()) {
                case EQUALS -> {
                    return criteriaBuilder.equal(root.get(key), value);
                }
                case LIKE -> {
                    return criteriaBuilder.like(root.get(key), "%" + value + "%");
                }
                case GT -> {
                    return criteriaBuilder.greaterThan(root.get(key), (Comparable) value);
                }
                case GTE -> {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Comparable) value);
                }
                case LT -> {
                    return criteriaBuilder.lessThan(root.get(key), (Comparable) value);
                }
                case LTE -> {
                    return criteriaBuilder.lessThanOrEqualTo(root.get(key), (Comparable) value);
                }
                case RANGE -> {
                    if (criteria.getValueTo() != null) {
                        return criteriaBuilder.between(root.get(key), (Comparable) value,
                                (Comparable) criteria.getValueTo());
                    }
                }
                default -> throw new IllegalArgumentException("Unsupported operation: " + criteria.getOperator());
            }
        } catch (PathElementException e) {
            throw new GenericException("Error processing criteria: " + criteria.getKey());
        }

        return null;
    }

}
