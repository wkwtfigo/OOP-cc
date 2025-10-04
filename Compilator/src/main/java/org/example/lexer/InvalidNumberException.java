package org.example.lexer;

public class InvalidNumberException extends LexerException {
    public InvalidNumberException(String num, Location location) {
        super("Invalid number literal \"" + num + "\"", location);
    }
}