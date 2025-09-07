import java.util.Map;

public class Lexer {
    private final String input;
    private int pos = 0;
    private int line = 1;
    private int col = 1;

    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
            Map.entry("class", TokenType.CLASS),
            Map.entry("extends", TokenType.EXTENDS),
            Map.entry("is", TokenType.IS),
            Map.entry("end", TokenType.END),
            Map.entry("var", TokenType.VAR),
            Map.entry("method", TokenType.METHOD),
            Map.entry("this", TokenType.THIS),
            Map.entry("if", TokenType.IF),
            Map.entry("then", TokenType.THEN),
            Map.entry("else", TokenType.ELSE),
            Map.entry("while", TokenType.WHILE),
            Map.entry("loop", TokenType.LOOP),
            Map.entry("return", TokenType.RETURN),
            Map.entry("true", TokenType.TRUE),
            Map.entry("false", TokenType.FALSE),
            Map.entry("print", TokenType.PRINT)
    );

    public Lexer(String input) {
        this.input = input;
    }

    public Token nextToken() {
        skipWhitespaceAndComments();

        if (eof()) {
            return new Token(TokenType.EOF, "", line, col);
        }

        char c = peek();

        if (c == ':' && peekNext() == '=') {
            consume(); consume();
            return new Token(TokenType.ASSIGN, ":=", line, col - 2);
        }
        if (c == '=' && peekNext() == '>') {
            consume(); consume();
            return new Token(TokenType.ARROW, "=>", line, col - 2);
        }

        switch (c) {
            case '(' -> { return makeSimple(TokenType.LPAR); }
            case ')' -> { return makeSimple(TokenType.RPAR); }
            case '{' -> { return makeSimple(TokenType.LBRACE); }
            case '}' -> { return makeSimple(TokenType.RBRACE); }
            case '[' -> { return makeSimple(TokenType.LBRACK); }
            case ']' -> { return makeSimple(TokenType.RBRACK); }
            case ':' -> { return makeSimple(TokenType.COLON); }
            case ',' -> { return makeSimple(TokenType.COMMA); }
            case '.' -> { return makeSimple(TokenType.DOT); }
            case '<' -> { return makeSimple(TokenType.LT); }
            case '>' -> { return makeSimple(TokenType.RT); }
        }

        // Numbers
        if (Character.isDigit(c)) {
            return number();
        }

        // Identifiers or keywords
        if (Character.isLetter(c) || c == '_') {
            return identifier();
        }

        // String literals
        if (c == '"') {
            return stringLiteral();
        }

        throw new RuntimeException("Unexpected character '" + c + "' at " + line + ":" + col);
    }

    private Token identifier() {
        int startCol = col;
        StringBuilder sb = new StringBuilder();

        while (!eof() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            sb.append(consume());
        }
        String lexeme = sb.toString();

        TokenType type = KEYWORDS.getOrDefault(lexeme, TokenType.ID);

        return new Token(type, lexeme, line, startCol);
    }

    private Token stringLiteral() {
        int startCol = col;
        
        consume();
        
        StringBuilder sb = new StringBuilder();

        while (!eof() && peek() != '"') {
            sb.append(consume());
        }

        if (eof()) {
            throw new RuntimeException("Unterminated string literal at " + line + ":" + startCol);
        }

        consume();
        String lexeme = sb.toString();
        return new Token(TokenType.ID, lexeme, line, startCol);
    }

    // reads a sequance of numbers
    private Token number() {
        int startCol = col;
        StringBuilder sb = new StringBuilder();
        while (!eof() && Character.isDigit(peek())) {
            sb.append(consume());
        }

        if (!eof() && peek() == '.') {
            sb.append(consume());
            while (!eof() && Character.isDigit(peek())) {
                sb.append(consume());
            }
            return new Token(TokenType.REAL_LIT, sb.toString(), line, startCol);
        }
        return new Token(TokenType.INT_LIT, sb.toString(), line, startCol);
    }

    // for simple symbols - : ( )
    private Token makeSimple(TokenType type) {
        char c = consume();
        return new Token(type, String.valueOf(c), line, col - 1);
    }

    // skip spaces, comments, and next line symbols 
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
            } else if (c == '/' && peekNext() == '*') { // multi-line comment
                consume(); consume();
                while (!eof() && !(peek() == '*' && peekNext() == '/')) {
                    if (peek() == '\n') {
                        line++;
                        col = 1;
                    }
                    consume();
                }
                if (!eof()) { consume(); consume(); } // closing */
            } else {
                break;
            }
        }
    }

    // get next token, is used for identifying a token
    private char peek() {
        return input.charAt(pos);
    }

    private char peekNext() {
        if (pos + 1 >= input.length()) return '\0';
        return input.charAt(pos + 1);
    }

    // get next token, token is known
    private char consume() {
        char c = input.charAt(pos++);
        col++;
        return c;
    }

    // check for the end of file
    private boolean eof() {
        return pos >= input.length();
    }
}
