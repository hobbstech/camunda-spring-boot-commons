package io.github.hobbstech.bpm.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;

@Data
public final class ChangeTaskPropertiesContext {

    @NotBlank(message = "Task id should be provided")
    private String taskId;

    @NotBlank(message = "Process instance id should be provided")
    private String processInstanceId;

    private String newAssigneeUsername;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy", locale = "en_ZW", timezone = "Africa/Harare")
    private Date dueDate;

    private Collection<String> candidateUsers;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy", locale = "en_ZW", timezone = "Africa/Harare")
    private Date followUpDate;

}
