package org.example.lexer;

public class NumberOverflowException extends LexerException {
    public NumberOverflowException(String num, Location location) {
        super("Number literal \"" + num + "\" is too large", location);
    }
}