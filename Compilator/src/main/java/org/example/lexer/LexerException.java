package org.example.lexer;

public class LexerException extends RuntimeException {
    public LexerException(String message, Location location) {
        super(message + " at " + location);
    }
}