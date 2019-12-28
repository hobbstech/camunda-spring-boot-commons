package io.github.hobbstech.bpm.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseDao<T> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
}
