package io.github.hobbstech.bpm.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.hobbstech.bpm.commons.BeanUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;

import javax.validation.constraints.NotBlank;
import java.util.NoSuchElementException;
import java.util.Optional;

import static io.github.hobbstech.bpm.commons.Validations.requireNonNull;
import static java.util.Objects.isNull;

@Slf4j
@Data
public class CommonTaskRequest {

    @NotBlank(message = "Task id should be provided")
    private String taskId;

    @NotBlank(message = "Process instance id should be provided")
    private String processInstanceId;

    @JsonIgnore
    private String username;


    public static void validateCommonTaskRequest(CommonTaskRequest commonTaskRequest) {

        requireNonNull(commonTaskRequest, () -> new IllegalStateException("Request object is null"));

        requireNonNull(commonTaskRequest.getTaskId(),
                () -> new IllegalStateException("Task ID should be provided"));

        requireNonNull(commonTaskRequest.getProcessInstanceId(),
                () -> new IllegalStateException("Process Instance ID should not be null"));

        requireNonNull(commonTaskRequest.getUsername(),
                () -> new IllegalStateException("Username should not be null"));

        val taskService = BeanUtil.getBean(TaskService.class);

        val runtimeService = BeanUtil.getBean(RuntimeService.class);

        log.debug("---> Logged user {}", commonTaskRequest.getUsername());

        var task = Optional.ofNullable(taskService.createTaskQuery()
                .processInstanceId(commonTaskRequest.processInstanceId)
                .taskId(commonTaskRequest.taskId)
                .initializeFormKeys()
                .active()
                .singleResult())
                .orElseThrow(() -> new NoSuchElementException("Task record was not found"));

        val assignedUser = task.getAssignee();

        if (isNull(assignedUser)) {
            return;
        }

        if (assignedUser.equalsIgnoreCase(commonTaskRequest.getUsername())) {
            return;
        }


        log.info("-----> candidate user {} ", commonTaskRequest.getUsername());

        val identityLinks = taskService.getIdentityLinksForTask(task.getId());

        val candidatePresent =
                identityLinks.stream()
                        .anyMatch(identityLink -> identityLink.getUserId().equalsIgnoreCase(commonTaskRequest.getUsername()));

        if (candidatePresent) {
            return;
        }

        throw new NoSuchElementException("Task record was not found for the logged in user");

    }


}
