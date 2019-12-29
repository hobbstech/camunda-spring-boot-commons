package io.github.hobbstech.bpm.task;

import io.github.hobbstech.bpm.commons.AbstractBusinessService;
import io.github.hobbstech.bpm.commons.AbstractUser;
import io.github.hobbstech.bpm.commons.UserRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.hobbstech.bpm.commons.Validations.validate;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.BooleanUtils.negate;

@Slf4j
@Component
public class TaskPropertiesServiceImpl extends AbstractBusinessService implements TaskPropertiesService {

    private final UserRepository userRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final TaskRepository taskRepository;

    public TaskPropertiesServiceImpl(IdentityService identityService, TaskService taskService, UserRepository userRepository, ApplicationEventPublisher applicationEventPublisher, TaskRepository taskRepository) {
        super(identityService, taskService);
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskDto changeTaskProperties(ChangeTaskPropertiesContext changeTaskPropertiesContext) {

        validate(changeTaskPropertiesContext);

        val taskId = changeTaskPropertiesContext.getTaskId();

        val processInstanceId = changeTaskPropertiesContext.getProcessInstanceId();

        val task = validateTaskId(taskId, processInstanceId);

        validateAssignee(taskId);

        if (nonNull(changeTaskPropertiesContext.getNewAssigneeUsername())) {
            task.setAssignee(changeTaskPropertiesContext.getNewAssigneeUsername());
        }

        val currentCandidateUsers = taskService.getIdentityLinksForTask(taskId);

        val currentCandidateUsernames = currentCandidateUsers.stream().map(IdentityLink::getUserId).collect(toSet());

        val requestCandidateUsersCopy = new HashSet<String>(changeTaskPropertiesContext.getCandidateUsers());

        requestCandidateUsersCopy.removeAll(currentCandidateUsernames);

        val newCandidateUserAssignmentNotifierEvent = new NewCandidateUserAssignmentEventNotifier(this,
                requestCandidateUsersCopy, TaskDto.fromEntity(task));

        applicationEventPublisher.publishEvent(newCandidateUserAssignmentNotifierEvent);

        changeTaskPropertiesContext.getCandidateUsers().forEach(userId -> taskService.deleteCandidateUser(taskId,
                userId));

        if (nonNull(changeTaskPropertiesContext.getCandidateUsers()) && negate(changeTaskPropertiesContext.getCandidateUsers().isEmpty())) {
            changeTaskPropertiesContext.getCandidateUsers().forEach(candidateUser -> taskService.addCandidateUser(taskId, candidateUser));
        }

        val followUpDate = changeTaskPropertiesContext.getFollowUpDate();

        val dueDate = changeTaskPropertiesContext.getDueDate();

        if (nonNull(dueDate)) {
            task.setDueDate(dueDate);
        }

        if (nonNull(followUpDate)) {
            val registeredDueDate = task.getDueDate();
            if (isNull(registeredDueDate)) {
                throw new IllegalArgumentException("Can no set a follow up date without having set a due date");
            }

            if (negate(followUpDate.before(registeredDueDate))) {
                throw new IllegalArgumentException("Follow up date should be after due date of a task");
            }

            task.setFollowUpDate(followUpDate);
        }

        taskService.saveTask(task);

        return TaskDto.fromEntity(task);
    }

    @Override
    public TaskDto findTask(String taskId) {
        return Optional.ofNullable(taskService.createTaskQuery()
                .taskId(taskId)
                .initializeFormKeys()
                .singleResult())
                .map(TaskDto::fromEntity)
                .orElseThrow(() -> new NoSuchElementException("Task record was not found"));
    }

    @Override
    public Collection<AbstractUser> findCandidateUsers(String taskId) {

        val identityLinks = taskService.getIdentityLinksForTask(taskId);

        return identityLinks.stream().map(identityLink -> userRepository.findByUsername(identityLink.getUserId()))
                .collect(Collectors.toCollection(HashSet::new));

    }

    @Override
    public TaskExecutionCount determineTaskExecutionCount(String processInstanceId, String taskDefinitionKey) {

        val tasks = taskRepository.findTasksForProcessAndTaskDefinitionKey(processInstanceId, taskDefinitionKey);

        val taskExecutionCount = new TaskExecutionCount();

        taskExecutionCount.setFirstTime(tasks.size() <= 1);

        taskExecutionCount.setExecutionCount(tasks.size());

        return taskExecutionCount;

    }

    @Override
    public Collection<TaskModel> findProcessTasks(String processInstanceId) {

        return taskRepository.findTasksForProcess(processInstanceId);

    }

    @Override
    public Page<TaskDto> findUserTasks(Pageable pageable) {

        val firstResult = pageable.getPageSize() * pageable.getPageNumber();

        val user = identityService.getCurrentAuthentication();

        if (isNull(user)) {
            return new PageImpl<>(Collections.emptyList());
        }

        val taskList = taskService.createTaskQuery()
                .taskAssignee(user.getUserId())
                .orderByTaskCreateTime()
                .desc()
                .initializeFormKeys()
                .active()
                .listPage(firstResult, pageable.getPageSize());

        val totalNumberOfTasks = taskService.createTaskQuery().taskAssignee(user.getUserId())
                .initializeFormKeys()
                .active()
                .count();

        val page = new PageImpl<Task>(taskList, pageable, totalNumberOfTasks);

        log.debug("---> Page requested : {}", page);

        return page.map(TaskDto::fromEntity);

    }

    @Override
    public TaskDto findByProcessInstanceAndTaskDefinitionKey(String processInstanceId, String taskDefinitionKey) {
        val task = taskService.createTaskQuery()
                .initializeFormKeys()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey)
                .list()
                .stream()
                .findFirst()
                .orElse(null);

        if (isNull(task)) {
            return null;
        }
        return TaskDto.fromEntity(task);
    }

    @Override
    public TaskDto findByProcessInstanceId(String processInstanceId) {
        val task = taskService.createTaskQuery()
                .initializeFormKeys()
                .processInstanceId(processInstanceId)
                .active()
                .singleResult();
        if (isNull(task)) {
            return null;
        }
        return TaskDto.fromEntity(task);
    }
}
