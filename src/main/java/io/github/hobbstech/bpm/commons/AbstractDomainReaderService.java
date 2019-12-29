package io.github.hobbstech.bpm.commons;

import io.github.hobbstech.bpm.exceptions.InvalidRequestException;
import io.github.hobbstech.bpm.jpa.BaseDao;
import io.github.hobbstech.bpm.jpa.BaseEntity;
import io.github.hobbstech.bpm.jpa.CustomSpecificationTemplateImplBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.NoSuchElementException;

import static java.util.Objects.isNull;

@Slf4j
public abstract class AbstractDomainReaderService<T> {

    protected final BaseDao<T> repository;

    public AbstractDomainReaderService(BaseDao<T> repository) {
        this.repository = repository;
    }

    public T findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Record not found for the given id : " + id));
    }

    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            log.error("---> Error message : {}", ex.getMessage());
            log.error("---> Error : ", ex);
            throw new InvalidRequestException("You can not delete this record is might be used by another record");
        }
    }

    public Page<T> findAll(Pageable pageable, String searchQuery) {
        if (isNull(searchQuery))
            return repository.findAll(pageable);
        val spec = new CustomSpecificationTemplateImplBuilder<T>()
                .buildSpecification(searchQuery);
        return repository.findAll(spec, pageable);
    }

    public Collection<T> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void pseudoDelete(Long id) {
        val target = findById(id);
        if (target.getClass().isAssignableFrom(BaseEntity.class)) {
            val entity = (BaseEntity) target;
            entity.setDeleted(true);
            repository.save((T) entity);
        }

    }
}
