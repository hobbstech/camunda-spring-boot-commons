package io.github.hobbstech.bpm.exceptions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class InvalidRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @Getter
    private List<String> violations;

    public InvalidRequestException(String message) {
        super(message);
        this.violations = new ArrayList<String>();
        violations.add(message);
    }

}
