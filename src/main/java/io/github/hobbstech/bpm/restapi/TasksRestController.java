package io.github.hobbstech.bpm.restapi;

import io.github.hobbstech.bpm.commons.AbstractUser;
import io.github.hobbstech.bpm.task.ChangeTaskPropertiesContext;
import io.github.hobbstech.bpm.task.TaskExecutionCount;
import io.github.hobbstech.bpm.task.TaskModel;
import io.github.hobbstech.bpm.task.TaskPropertiesService;
import org.camunda.bpm.engine.AuthorizationException;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class TasksRestController {

    private final TaskPropertiesService taskPropertiesService;

    public TasksRestController(TaskPropertiesService taskPropertiesService) {
        this.taskPropertiesService = taskPropertiesService;
    }

    @PostMapping("/v1/tasks/set-properties")
    public TaskDto setTaskProperties(@RequestBody ChangeTaskPropertiesContext changeTaskPropertiesContext) {
        return taskPropertiesService.changeTaskProperties(changeTaskPropertiesContext);
    }

    @GetMapping("/v1/tasks")
    public TaskDto getTask(@RequestParam String taskId) {
        return taskPropertiesService.findTask(taskId);
    }

    @GetMapping("/v1/tasks/candidate-users")
    public Collection<AbstractUser> getCandidateUsers(@RequestParam String taskId) {
        return taskPropertiesService.findCandidateUsers(taskId);
    }

    @GetMapping("/v1/tasks/task-execution-count")
    public TaskExecutionCount determineTaskExecutionCount(@RequestParam String processInstanceId,
                                                          @RequestParam String taskDefinitionKey) {
        return taskPropertiesService.determineTaskExecutionCount(processInstanceId, taskDefinitionKey);
    }

    @GetMapping("/v1/tasks/process-tasks")
    public Collection<TaskModel> findProcessTask(@RequestParam String processInstanceId) {
        return taskPropertiesService.findProcessTasks(processInstanceId);
    }

    @GetMapping("/v1/tasks/process-task")
    public TaskDto findByProcessInstanceAndTaskDefinitionKey(
            @RequestParam String processInstanceId, @RequestParam String taskDefinitionKey) {
        return taskPropertiesService.findByProcessInstanceAndTaskDefinitionKey(processInstanceId, taskDefinitionKey);
    }

    @GetMapping("/v1/tasks/process-task/by-process-instance-id")
    public TaskDto findByProcessInstanceId(@RequestParam String processInstanceId) {
        return taskPropertiesService.findByProcessInstanceId(processInstanceId);
    }

    @GetMapping("/v1/task/users")
    public Page<TaskDto> getUserTasks(@PageableDefault Pageable pageable) throws AuthorizationException {

        return taskPropertiesService.findUserTasks(pageable);
    }

}
