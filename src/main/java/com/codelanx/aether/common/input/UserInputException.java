package com.codelanx.aether.common.input;

public class UserInputException extends RuntimeException {

    public UserInputException(String message) {
        super(message);
    }

    public UserInputException(String message, Throwable t) {
        super(message, t);
    }
}
