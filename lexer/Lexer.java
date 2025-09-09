import java.util.Map;

public class Lexer {
    private final String input;
    private int pos = 0;
    private int line = 1;
    private int col = 1;

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
            Map.entry("true", TokenType.TOK_TRUE),
            Map.entry("false", TokenType.TOK_FALSE),
            Map.entry("print", TokenType.TOK_PRINT)
    );

    public Lexer(String input) {
        this.input = input;
    }

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

        throw new RuntimeException("Unexpected character '" + c + "' at " + line + ":" + col);
    }

    private Token identifier() {
        int startCol = col;
        StringBuilder sb = new StringBuilder();

        while (!eof() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            sb.append(consume());
        }
        String lexeme = sb.toString();

        TokenType type = KEYWORDS.getOrDefault(lexeme, TokenType.TOK_ID);

        return new Token(type, lexeme, new Location(line, startCol));
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
            return new Token(TokenType.TOK_REAL_LIT, sb.toString(), new Location(line, startCol));
        }
        return new Token(TokenType.TOK_INT_LIT, sb.toString(), new Location(line, startCol));
    }

    // for simple symbols - : ( )
    private Token makeSimple(TokenType type) {
        char c = consume();
        return new Token(type, String.valueOf(c), new Location(line, col - 1));
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
