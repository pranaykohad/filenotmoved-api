package com.filenotmoved.user_service.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.query.sqm.PathElementException;
import org.springframework.data.jpa.domain.Specification;

import com.filenotmoved.user_service.dto.SearchFilter;
import com.filenotmoved.user_service.entity.Issues;
import com.filenotmoved.user_service.exception.custom.GenericException;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
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

        Path<?> path;

        try {
            path = root.get(key);
        } catch (IllegalArgumentException e) {
            throw new GenericException("Invalid field: " + key);
        }

        Class<?> fieldType = path.getJavaType();

        Object typedValue = convertValue(value, fieldType);

        try {
            switch (criteria.getOperator()) {
                case EQUALS -> {
                    return criteriaBuilder.equal(root.get(key), typedValue);
                }
                case LIKE -> {
                    return criteriaBuilder.like(root.get(key), "%" + typedValue + "%");
                }
                case GT -> {
                    return criteriaBuilder.greaterThan(root.get(key), (Comparable) typedValue);
                }
                case GTE -> {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Comparable) typedValue);
                }
                case LT -> {
                    return criteriaBuilder.lessThan(root.get(key), (Comparable) typedValue);
                }
                case LTE -> {
                    return criteriaBuilder.lessThanOrEqualTo(root.get(key), (Comparable) typedValue);
                }
                case RANGE -> {
                    if (criteria.getValueTo() != null) {
                        return criteriaBuilder.between(root.get(key), (Comparable) typedValue,
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

    private static Object convertValue(Object value, Class<?> type) {
        if (value == null)
            return null;

        if (type == String.class) {
            return value.toString();
        } else if (type == Integer.class) {
            return Integer.valueOf(value.toString());
        } else if (type == Long.class) {
            return Long.valueOf(value.toString());
        } else if (type == Double.class) {
            return Double.valueOf(value.toString());
        } else if (type == Boolean.class) {
            return Boolean.valueOf(value.toString());
        } else if (type == LocalDate.class) {
            return LocalDate.parse(value.toString());
        } else if (type == LocalDateTime.class) {
            return LocalDateTime.parse(value.toString());
        }

        return value;
    }

}
