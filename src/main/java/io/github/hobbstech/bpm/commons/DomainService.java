package io.github.hobbstech.bpm.commons;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface DomainService<T, C, U> {

    T findById(Long id);

    void delete(Long id);

    Page<T> findAll(Pageable pageable, String searchQuery);

    T create(C createCommand);

    T update(U updateCommand);

    Collection<T> findAll();

    void pseudoDelete(Long id);

}
