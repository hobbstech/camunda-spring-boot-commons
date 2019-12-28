package io.github.hobbstech.bpm.restapi;

import io.github.hobbstech.bpm.process.ProcessInstancesService;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/process-engine/v1/")
public class ProcessInstanceRestController {

    private final ProcessInstancesService processInstancesService;

    public ProcessInstanceRestController(ProcessInstancesService processInstancesService) {
        this.processInstancesService = processInstancesService;
    }

    @GetMapping("/process-instance")
    public ProcessInstanceDto getProcessInstance(@RequestParam("processInstanceId") String processInstanceId) {

        return processInstancesService.findProcessInstance(processInstanceId);

    }

    @GetMapping("/process-instance/variables")
    public Map<String, Object> getProcessInstanceVariables(@RequestParam("processInstanceId") String processInstanceId) {

        return processInstancesService.findProcessVariables(processInstanceId);

    }
}
