public enum TokenType {
    // Keywords
    TOK_CLASS, TOK_EXTENDS, TOK_IS, TOK_END, TOK_VAR, TOK_METHOD, TOK_THIS, TOK_WHILE, TOK_LOOP,
    TOK_IF, TOK_THEN, TOK_ELSE, TOK_RETURN, TOK_TRUE, TOK_FALSE, TOK_PRINT,

    // Identifiers
    TOK_ID, // [A-Za-z_][A-Za-z0-9_]*,

    // Literals
    TOK_INT_LIT, TOK_REAL_LIT, //BOOL_LIT

    // Operators
    TOK_ASSIGN,   // :=
    TOK_ARROW,    // =>

    // Separators
    TOK_DOT, TOK_COLON, TOK_COMMA,
    TOK_LPAR, TOK_RPAR, // ()
    TOK_LBRACK, TOK_RBRACK, // []
    TOK_LBRACE, TOK_RBRACE,  // {}
    TOK_LT, TOK_RT, // <> - for generics

    // Errors on lexer level
    TOK_EOF, TOK_ERROR
}
