package io.github.hobbstech.bpm.process;

import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;

import java.util.Map;

public interface ProcessInstancesService {

    ProcessInstanceDto findProcessInstance(String processInstanceId);

    Map<String, Object> findProcessVariables(String processInstanceId);

}
