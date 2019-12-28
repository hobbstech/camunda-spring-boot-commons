package io.github.hobbstech.bpm.task;

import lombok.Data;

@Data
public class TaskExecutionCount {

    private boolean firstTime;

    private Integer executionCount;

}
