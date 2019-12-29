package io.github.hobbstech.bpm.commons.validations;

public interface CommandValidator<T> {

    void validate(T t);

}
