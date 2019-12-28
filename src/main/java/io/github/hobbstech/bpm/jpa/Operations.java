package io.github.hobbstech.bpm.jpa;

public enum Operations {

    GREATER_THAN(">"),
    LESS_THAN("<"),
    EQUALS(":");

    String sign;

    Operations(String sign) {
        this.sign = sign;
    }
}
