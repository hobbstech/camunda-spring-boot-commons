package io.github.hobbstech.bpm.exceptions;

public class IllegalOperationException extends RuntimeException {

    private static final long serialVersionUID = 5839669394399688226L;

  public IllegalOperationException(String message) {
        super(message);
    }
}
