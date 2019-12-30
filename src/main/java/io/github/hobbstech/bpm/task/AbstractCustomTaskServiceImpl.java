package io.github.hobbstech.bpm.task;

import io.github.hobbstech.bpm.exceptions.AccessDeniedException;
import lombok.val;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public abstract class AbstractCustomTaskServiceImpl implements AbstractCustomTaskService {

    private final TaskService taskService;

    protected String businessProcessKey;

    private final IdentityService identityService;

    public AbstractCustomTaskServiceImpl(TaskService taskService, IdentityService identityService) {
        this.taskService = taskService;
        this.identityService = identityService;
    }

    @Override
    public Collection<TaskDto> findActiveTasksByBusinessProcess() {

        return taskService.createTaskQuery()
                .processInstanceBusinessKey(businessProcessKey)
                .initializeFormKeys()
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list()
                .stream()
                .map(TaskDto::fromEntity)
                .sorted((Comparator.comparing(TaskDto::getCreated)
                        .reversed()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<TaskDto> findActiveTasksByProcessInstanceId(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .initializeFormKeys()
                .active()
                .list().stream().map(TaskDto::fromEntity).collect(toList());
    }

    @Override
    public Collection<TaskDto> findTasksByProcessInstanceId(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .initializeFormKeys()
                .list().stream().map(TaskDto::fromEntity).collect(toList());
    }


    @Override
    public Collection<TaskDto> findMyTasks() {

        val auth = Optional.ofNullable(identityService.getCurrentAuthentication()).
                orElseThrow(() -> new AccessDeniedException("You need to be logged in to perform this operation"));

        val username = auth.getUserId();

        return taskService.createTaskQuery()
                .processInstanceBusinessKey(businessProcessKey)
                .taskAssignee(username)
                .initializeFormKeys()
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list()
                .stream()
                .map(TaskDto::fromEntity)
                .sorted((Comparator.comparing(TaskDto::getCreated).reversed())).collect(Collectors.toList());
    }
}
