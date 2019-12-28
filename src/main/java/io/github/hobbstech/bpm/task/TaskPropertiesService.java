package io.github.hobbstech.bpm.task;

import io.github.hobbstech.bpm.commons.AbstractUser;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface TaskPropertiesService {

    Page<TaskDto> findUserTasks(Pageable pageable);

    TaskDto changeTaskProperties(ChangeTaskPropertiesContext changeTaskPropertiesContext);

    TaskDto findTask(String taskId);

    Collection<AbstractUser> findCandidateUsers(String taskId);

    TaskExecutionCount determineTaskExecutionCount(String processInstanceId, String taskDefinitionKey);

    Collection<TaskModel> findProcessTasks(String processInstanceId);

    TaskDto findByProcessInstanceAndTaskDefinitionKey(String processInstanceId, String taskDefinitionKey);

    TaskDto findByProcessInstanceId(String processInstanceId);
}
