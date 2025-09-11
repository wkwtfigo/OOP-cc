public class Token {
    private  final TokenType type;
    private final String lexeme;
    private final Location location;

    public Token(TokenType type, String lexeme, Location location) {
        this.type = type;
        this.lexeme = lexeme;
        this.location = location;
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
}
