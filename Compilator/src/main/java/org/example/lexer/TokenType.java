package org.example.lexer;

public enum TokenType {
    // Keywords
    TOK_CLASS, TOK_EXTENDS, TOK_IS, 
    TOK_END, TOK_VAR, TOK_METHOD, TOK_THIS, 
    TOK_WHILE, TOK_LOOP, TOK_IF, TOK_THEN, 
    TOK_ELSE, TOK_RETURN, TOK_PRINT,

    // Identifiers
    TOK_ID, // other 
    TOK_TYPE_ID, // Integer, Real, Boolean

    // Literals
    TOK_INT_LIT, TOK_REAL_LIT, TOK_BOOL_LIT,

    // Operators
    TOK_ASSIGN,   // :=
    TOK_ARROW,    // =>

    // Separators
    TOK_DOT, TOK_COLON, TOK_COMMA,
    TOK_LPAR, TOK_RPAR, // ()
    TOK_LBRACK, TOK_RBRACK, // []
    TOK_LBRACE, TOK_RBRACE,  // {}
    TOK_LT, TOK_RT, // <> - for generics

    // End of file
    TOK_EOF
}
