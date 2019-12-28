package io.github.hobbstech.bpm.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CustomSpecificationTemplateImplBuilder<T> {

    private final List<SearchCriteria> searchCriterias;

    public CustomSpecificationTemplateImplBuilder() {
        this.searchCriterias = new ArrayList<>();
    }

    private void withSearchCriteria(String key, Object value, String operation) {
        searchCriterias.add(SearchCriteria.createSearchCriteria(key, operation, value));
    }

    private Specification<T> build() {
        if (searchCriterias.isEmpty())
            return null;
        List<Specification<T>> specifications = new ArrayList<>();
        searchCriterias.forEach(searchCriteria -> specifications.add(new CustomSpecificationTemplateImpl<>(searchCriteria)));
        Specification<T> result = specifications.get(0);
        for (int i = 1; i < specifications.size(); i++)
            result = Specifications.where(result).and(specifications.get(i));
        return result;
    }

    public Specification<T> buildSpecification(String searchQuery) {
        log.trace("------> Search query : {}", searchQuery);
        CustomSpecificationTemplateImplBuilder<T> builder = new CustomSpecificationTemplateImplBuilder<>();
        Pattern pattern = Pattern.compile("(\\w.+?)([:<>])((\\w+[\\s\\w]*)+?)");
        Matcher matcher = pattern.matcher(searchQuery + ",");
        while (matcher.find()) {
            builder.withSearchCriteria(matcher.group(1), matcher.group(3), matcher.group(2));
        }

        return builder.build();
    }

}
