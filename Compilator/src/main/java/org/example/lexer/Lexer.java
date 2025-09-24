package org.example.lexer;

import java.io.IOException;
import java.util.Map;
import org.example.parser.Parser;
/**
 * The lexer is responsible for converting the raw source code string
 * into a sequence of {@link Token} objects. Each token has a type
 * ({@link TokenType}), its lexeme (original text), and a location
 * (line and column number). 
 */
public class Lexer implements Parser.Lexer {
    private final String input;
    private int pos = 0;
    private int line = 1;
    private int col = 1;
    private  Token currentToken;
    /**
     * Map of language keywords and their corresponding token types.
     */
    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
            Map.entry("class", TokenType.TOK_CLASS),
            Map.entry("extends", TokenType.TOK_EXTENDS),
            Map.entry("is", TokenType.TOK_IS),
            Map.entry("end", TokenType.TOK_END),
            Map.entry("var", TokenType.TOK_VAR),
            Map.entry("method", TokenType.TOK_METHOD),
            Map.entry("this", TokenType.TOK_THIS),
            Map.entry("if", TokenType.TOK_IF),
            Map.entry("then", TokenType.TOK_THEN),
            Map.entry("else", TokenType.TOK_ELSE),
            Map.entry("while", TokenType.TOK_WHILE),
            Map.entry("loop", TokenType.TOK_LOOP),
            Map.entry("return", TokenType.TOK_RETURN),
            Map.entry("true", TokenType.TOK_BOOL_LIT),
            Map.entry("false", TokenType.TOK_BOOL_LIT),
            Map.entry("print", TokenType.TOK_PRINT)
    );

    public Lexer(String input) {
        this.input = input;
    }

    /**
     * Reads the next token from the input stream.
     *
     * @return the next {@link Token}, or {@link TokenType#TOK_EOF} when input ends
     * @throws UnknownCharacterException if an unexpected character is found
     */
    public Token nextToken() {
        skipWhitespaceAndComments();

        if (eof()) {
            return new Token(TokenType.TOK_EOF, "", new Location(line, col));
        }

        char c = peek();

        if (c == ':' && peekNext() == '=') {
            consume(); consume();
            return new Token(TokenType.TOK_ASSIGN, ":=", new Location(line, col - 2));
        }
        if (c == '=' && peekNext() == '>') {
            consume(); consume();
            return new Token(TokenType.TOK_ARROW, "=>", new Location(line, col - 2));
        }

        switch (c) {
            case '(' -> { return makeSimple(TokenType.TOK_LPAR); }
            case ')' -> { return makeSimple(TokenType.TOK_RPAR); }
            case '{' -> { return makeSimple(TokenType.TOK_LBRACE); }
            case '}' -> { return makeSimple(TokenType.TOK_RBRACE); }
            case '[' -> { return makeSimple(TokenType.TOK_LBRACK); }
            case ']' -> { return makeSimple(TokenType.TOK_RBRACK); }
            case ':' -> { return makeSimple(TokenType.TOK_COLON); }
            case ',' -> { return makeSimple(TokenType.TOK_COMMA); }
            case '.' -> { return makeSimple(TokenType.TOK_DOT); }
            case '<' -> { return makeSimple(TokenType.TOK_LT); }
            case '>' -> { return makeSimple(TokenType.TOK_RT); }
        }

        // Numbers
        if (Character.isDigit(c)) {
            return number();
        }

        // Identifiers or keywords
        if (Character.isLetter(c) || c == '_') {
            return identifier();
        }

        // unknown character
        throw new UnknownCharacterException(c, new Location(line, col));
    }

    /**
     * Parses an identifier or keyword starting at the current position.
     *
     * @return a {@link Token} of type {@link TokenType#TOK_ID} or keyword type
     */
    private Token identifier() {
        int startCol = col;
        StringBuilder sb = new StringBuilder();

        while (!eof() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            sb.append(consume());
        }
        String lexeme = sb.toString();

        TokenType type;
        if (KEYWORDS.containsKey(lexeme)) {
            type = KEYWORDS.get(lexeme);
        } else if (lexeme.equals("Integer") || lexeme.equals("Real") || lexeme.equals("Boolean")) {
            type = TokenType.TOK_TYPE_ID;
        } else {
            type = TokenType.TOK_ID;
        }

        return new Token(type, lexeme, new Location(line, startCol));
    }

    /**
     * Parses a numeric literal (integer or real).
     *
     * @return a {@link Token} of type {@link TokenType#TOK_INT_LIT}
     *         or {@link TokenType#TOK_REAL_LIT}
     * @throws InvalidNumberException if invalid form of number is met (.123, 10., etc.)
     * @throws NumberOverflowException
     * @throws InvalidIdentifierException
     */
    private Token number() {
        int startCol = col;
        StringBuilder sb = new StringBuilder();
        while (!eof() && Character.isDigit(peek())) {
            sb.append(consume());
        }

        if (!eof() && (Character.isLetter(peek()) || peek() == '_')) {
            throw new InvalidIdentifierException(
                "Identifiers cannot start with digit: " + sb.toString() + peek(),
                new Location(line, startCol)
            );
        }

        if (!eof() && peek() == '.') {
            sb.append(consume());
            boolean hasDigit = false;
            while (!eof() && Character.isDigit(peek())) {
                sb.append(consume());
                hasDigit = true;
            }
            if (!hasDigit) {
                throw new InvalidNumberException(sb.toString(), new Location(line, startCol));
            }
            
            String realStr = sb.toString();
            // check real overflow
            try {
                double value = Double.parseDouble(realStr);
                if (Double.isInfinite(value)) {
                    throw new NumberOverflowException(realStr, new Location(line, startCol));
                }
            } catch (NumberFormatException e) {
                throw new NumberOverflowException(realStr, new Location(line, startCol));
            }
            
            return new Token(
                    TokenType.TOK_REAL_LIT, realStr, new Location(line, startCol));
        }

        // check for integer overflow (for 32-bit int)
        String intStr = sb.toString();
        try {
            long longValue = Long.parseLong(intStr);
            if (longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE) {
                throw new NumberOverflowException(intStr, new Location(line, startCol));
            }
        } catch (NumberFormatException e) {
            throw new NumberOverflowException(intStr, new Location(line, startCol));
        }
        return new Token(
                TokenType.TOK_INT_LIT, intStr, new Location(line, startCol));
    }

    /**
     * Creates a simple single-character token (like parentheses, colon, etc.).
     *
     * @param type the {@link TokenType} to create
     * @return the created token
     */
    private Token makeSimple(TokenType type) {
        char c = consume();
        return new Token(type, String.valueOf(c), new Location(line, col - 1));
    }

    /**
     * Skips whitespace, line breaks, and comments.
     * Supports single-line (//) and multi-line (/* ... *​/) comments.
     *
     * @throws UnterminatedCommentException if a multi-line comment is not properly closed
     */ 
    private void skipWhitespaceAndComments() {
        while (!eof()) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r') {
                consume();
            } else if (c == '\n') {
                consume();
                line++;
                col = 1;
            } else if (c == '/' && peekNext() == '/') { // single line comment
                consume(); consume();
                while (!eof() && peek() != '\n') consume();
            } else if (c == '/' && peekNext() == '*') { // multi line comment
                consume(); consume();
                while (!eof()) {
                    if (peek() == '*' && peekNext() == '/') {
                        consume(); consume();
                        break;
                    }
                    if (peek() == '\n') {
                        line++;
                        col = 0;
                    }
                    consume();
                }
                if (eof()) {
                    throw new UnterminatedCommentException(new Location(line, col));
                }
            } else {
                break;
            }
        }
    }

    /**
     * Peeks at the current character without consuming it.
     *
     * @return the current character
     */
    private char peek() {
        return input.charAt(pos);
    }

    /**
     * Peeks at the next character without consuming it.
     *
     * @return the next character, or '\0' if at end of input
     */
    private char peekNext() {
        if (pos + 1 >= input.length()) return '\0';
        return input.charAt(pos + 1);
    }

    /**
     * Consumes the current character and moves forward.
     *
     * @return the consumed character
     */
    private char consume() {
        char c = input.charAt(pos++);
        col++;
        return c;
    }

    /**
     * Checks if the end of the input string has been reached.
     *
     * @return true if end of input, false otherwise
     */
    private boolean eof() {
        return pos >= input.length();
    }

    @Override
    public Object getLVal() {
        return this.currentToken;
    }

    @Override
    public int yylex() throws IOException {
        this.currentToken = nextToken();

        return currentToken.getType()
                .ordinal();
    }

    @Override
    public void yyerror(String msg) {
        System.out.println("Error at line " + currentToken.getLine() +
                ", column " + currentToken.getColumn() + ": " + msg);
    }
}
