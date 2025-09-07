public enum TokenType {
    // Keywords
    CLASS, EXTENDS, IS, END, VAR, METHOD, THIS, WHILE, LOOP,
    IF, THEN, ELSE, RETURN, TRUE, FALSE, PRINT,

    // Identifiers
    ID,

    // Literals
    INT_LIT, REAL_LIT, //BOOL_LIT

    // Operators
    ASSIGN,   // :=
    ARROW,    // =>

    // Separators
    DOT, COLON, COMMA,
    LPAR, RPAR, LBRACK, RBRACK, LBRACE, RBRACE,

    // Errors on lexer level
    EOF, ERROR
}
