package io.github.hobbstech.bpm.jpa;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wilson
 * @version 1.0.0
 *
 * <p>Holds the data that is needed to build a specification.</p>
 * @since 1.0.0
 */
@Data
class SearchCriteria implements Serializable {

    /**
     * <p>The first level property name of the entity class that we are searching with</p>
     */
    private final String key;

    /**
     * <p>The type of operation to be executed, > or < or :</p>
     */
    private final String operation;

    /**
     * <p>The value to be search for, which is the same type as the type of the key</p>
     */
    private final Object value;

    private SearchCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    static SearchCriteria createSearchCriteria(String key, String operation, Object value) {
        return new SearchCriteria(key, operation, value);
    }

}
