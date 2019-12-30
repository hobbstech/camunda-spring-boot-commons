package io.github.hobbstech.bpm.task;

import org.camunda.bpm.engine.rest.dto.task.TaskDto;

import java.util.Collection;

public interface AbstractCustomTaskService {

    Collection<TaskDto> findActiveTasksByBusinessProcess();

    Collection<TaskDto> findActiveTasksByProcessInstanceId(String processInstanceId);

    Collection<TaskDto> findTasksByProcessInstanceId(String processInstanceId);

    Collection<TaskDto> findMyTasks();
}
