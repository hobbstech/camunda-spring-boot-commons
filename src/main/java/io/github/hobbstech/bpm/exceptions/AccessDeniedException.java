package io.github.hobbstech.bpm.exceptions;

public class AccessDeniedException extends RuntimeException {
    private static final long serialVersionUID = 4220414727564742998L;

  public AccessDeniedException(String s) {
        super(s);
    }
}
