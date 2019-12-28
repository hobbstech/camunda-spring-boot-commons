package io.github.hobbstech.bpm.task;

import lombok.Getter;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

public class NewCandidateUserAssignmentEventNotifier extends ApplicationEvent {

    @Getter
    private final Set<String> candidateUsers;

    @Getter
    private final TaskDto task;

    public NewCandidateUserAssignmentEventNotifier(Object source, Set<String> candidateUsers, TaskDto task) {
        super(source);
        this.candidateUsers = candidateUsers;
        this.task = task;
    }
}
