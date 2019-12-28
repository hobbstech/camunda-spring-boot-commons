package io.github.hobbstech.bpm.commons;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static io.github.hobbstech.bpm.commons.Validations.requireNonNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.negate;

@Slf4j
public abstract class AbstractBusinessService {

    protected final IdentityService identityService;

    protected final TaskService taskService;

    public AbstractBusinessService(IdentityService identityService, TaskService taskService) {
        this.identityService = identityService;
        this.taskService = taskService;
    }

    protected Task validateTaskId(String taskId, String processInstanceId) {

        val optionalTask = Optional.ofNullable(taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskId(taskId)
                .active()
                .initializeFormKeys()
                .singleResult());

        return optionalTask.orElseThrow(() -> new IllegalStateException("Task record was not found"));

    }

    @Transactional
    public void completeTask(String taskId) {
        val isValid = validateAssignee(taskId);
        if (isValid) {
            taskService.complete(taskId);
        }
    }

    @Transactional
    public void completeTask(String taskId, Map<String, Object> variables) {
        val isValid = validateAssignee(taskId);
        if (isValid) {
            taskService.complete(taskId, variables);
        }
    }

    protected String getAuthenticatedUser() {
        val authentication = identityService.getCurrentAuthentication();
        requireNonNull(authentication, () -> new IllegalArgumentException("You need to be logged in to performTask this operation"));
        return authentication.getUserId();
    }

    protected String getAuthenticatedUserFullName() {
        val userRepository = BeanUtil.getBean(UserRepository.class);
        val user = userRepository.findByUsername(getAuthenticatedUser());

        if (nonNull(user)) {
            return user.getFirstName() + " " + user.getLastName();
        }
        return this.getAuthenticatedUser();
    }

    protected String getTaskDefinitionKey(String taskId, String processInstanceId) {
        val task = validateTaskId(taskId, processInstanceId);
        return task.getTaskDefinitionKey();
    }

    protected boolean validateAssignee(String taskId) {

        val userId = getAuthenticatedUser();

        val task = Optional.ofNullable(taskService.createTaskQuery().taskId(taskId).singleResult())
                .orElseThrow(() -> new NoSuchElementException("Task record was not found"));

        val assignee = task.getAssignee();

        val identityLinksForTask = taskService.getIdentityLinksForTask(taskId);

        if (Objects.isNull(assignee)) {

            log.error("Task assignee was not found");

            return true;

        }

        if (negate(assignee.equalsIgnoreCase(userId))) {
            return isCandidateOfTask(identityLinksForTask, getAuthenticatedUser());

        }

        return true;
    }

    private boolean isCandidateOfTask(List<IdentityLink> identityLinks, String authenticatedUser) {

        return identityLinks.stream().anyMatch(identityLink -> identityLink.getUserId().equalsIgnoreCase(authenticatedUser));

    }

}
