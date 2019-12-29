package io.github.hobbstech.bpm.process;

import io.github.hobbstech.bpm.jpa.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessDetailsEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    protected String processInstanceId;

    @Column(nullable = false)
    protected String processDefinitionKey;

    @Column(nullable = false)
    protected String processBusinessKey;

    @Column(nullable = false, unique = true)
    protected String executionId;


}
