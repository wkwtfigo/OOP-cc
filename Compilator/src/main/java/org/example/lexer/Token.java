package org.example.lexer;

public class Token {
    private  final TokenType type;
    private final String lexeme;
    private final Location location;
    private Token previousToken;

    public Token(TokenType type, String lexeme, Location location) {
        this.type = type;
        this.lexeme = lexeme;
        this.location = location;
        this.previousToken = null;
    }

    public Token(TokenType type, String lexeme, Location location, Token previousToken) {
        this.type = type;
        this.lexeme = lexeme;
        this.location = location;
        this.previousToken = previousToken;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    public TokenType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public int getLine() {
        return location.line;
    }

    public int getColumn() {
        return location.column;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Token getPreviousToken() {
        return previousToken;
    }

    public void setPreviousToken(Token previousToken) {
        this.previousToken = previousToken;
    }

    /**
     * Helper method to check if this token follows a specific token type
     */
    public boolean follows(TokenType expectedPreviousType) {
        return previousToken != null && previousToken.getType() == expectedPreviousType;
    }

    /**
     * Helper method to check if this token follows any of the specified token types
     */
    public boolean followsAny(TokenType... expectedPreviousTypes) {
        if (previousToken == null) return false;
        for (TokenType expectedType : expectedPreviousTypes) {
            if (previousToken.getType() == expectedType) {
                return true;
            }
        }
        return false;
    }
}
