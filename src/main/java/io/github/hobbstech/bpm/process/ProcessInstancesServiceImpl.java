package io.github.hobbstech.bpm.process;

import lombok.val;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProcessInstancesServiceImpl implements ProcessInstancesService {

    private final RuntimeService runtimeService;

    public ProcessInstancesServiceImpl(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public ProcessInstanceDto findProcessInstance(String processInstanceId) {

        val processInstance = Optional.ofNullable(runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult())
                .orElseThrow(() -> new NoSuchElementException("Process instance record was not found"));

        return ProcessInstanceDto.fromProcessInstance(processInstance);

    }

    public Map<String, Object> findProcessVariables(String processInstanceId) {

        return runtimeService.getVariables(processInstanceId);

    }

}
