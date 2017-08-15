package com.codelanx.aether.common.menu.dialog;

/**
 * Created by rogue on 8/14/2017.
 */
public class DialogueMismatchException extends RuntimeException {
    
    public DialogueMismatchException(String message) {
        super(message);
    }
    
    public DialogueMismatchException(String message, Throwable ex) {
        super(message, ex);
    }
}
