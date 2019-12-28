package io.github.hobbstech.bpm.jpa;

import lombok.val;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * @param <T> The Entity class type that is being used, to which the specification is to be created for.
 * @author wilson
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomSpecificationTemplateImpl<T> implements Specification<T> {

    private final SearchCriteria searchCriteria;

    CustomSpecificationTemplateImpl(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    /**
     * @param root    @see org.springframework.data.jpa.domain.Specification
     * @param query   @see org.springframework.data.jpa.domain.Specification
     * @param builder @see org.springframework.data.jpa.domain.Specification
     *
     *                <p>This method created a predication for a where clause.
     *                This determines the action and predicate to be created
     *                basing the the operation that is set in the search criteria</p>
     */
    @Override
    //@SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        val keys = searchCriteria.getKey().split(".");


        if (searchCriteria.getOperation().equalsIgnoreCase(Operations.GREATER_THAN.sign)) {

            return builder.greaterThanOrEqualTo(getRoot(root, keys), searchCriteria.getValue().toString());

        } else if (searchCriteria.getOperation().equalsIgnoreCase(Operations.LESS_THAN.sign)) {

            return builder.lessThanOrEqualTo(getRoot(root, keys), searchCriteria.getValue().toString());

        } else if (searchCriteria.getOperation().equalsIgnoreCase(Operations.EQUALS.sign)) {

            if (getRoot(root, keys).getJavaType().equals(String.class)) {

                return builder.like(getRoot(root, keys), "%" + searchCriteria.getValue() + "%");

            } else {

                return builder.equal(getRoot(root, keys), searchCriteria.getValue());

            }
        }
        return null;
    }

    private Expression getRoot(Root<T> root, String... keys) {
        Expression expression = root.get(keys[0]);

        for (int i = 1; i < keys.length; i++) {
            expression = root.get(keys[i]);
        }
        return expression;
    }

}
