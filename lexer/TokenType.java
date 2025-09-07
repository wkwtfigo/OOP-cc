public enum TokenType {
    // Keywords
    CLASS, EXTENDS, IS, END, VAR, METHOD, THIS, WHILE, LOOP,
    IF, THEN, ELSE, RETURN, TRUE, FALSE, PRINT,

    // Identifiers
    ID, // [A-Za-z_][A-Za-z0-9_]*,

    // Literals
    INT_LIT, REAL_LIT, //BOOL_LIT

    // Operators
    ASSIGN,   // :=
    ARROW,    // =>

    // Separators
    DOT, COLON, COMMA,
    LPAR, RPAR, LBRACK, RBRACK, LBRACE, RBRACE, LT, RT,

    // Errors on lexer level
    EOF, ERROR
}
