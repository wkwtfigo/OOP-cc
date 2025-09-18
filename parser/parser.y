%language "Java"
%define api.package "myparser"
%define api.parser.class {MyParser}
%define api.value.type {Object}

%code imports {
    import java.util.*;
}

%token TOK_CLASS, TOK_EXTENDS, TOK_IS, 
%token TOK_END, TOK_VAR, TOK_METHOD, TOK_THIS, 
%token TOK_WHILE, TOK_LOOP, TOK_IF, TOK_THEN, 
%token TOK_ELSE, TOK_RETURN, TOK_PRINT,
%token TOK_ID,
%token TOK_INT_LIT, TOK_REAL_LIT, TOK_BOOL_LIT,
%token TOK_ASSIGN,
%token TOK_ARROW,
%token TOK_DOT, TOK_COLON, TOK_COMMA,
%token TOK_LPAR, TOK_RPAR,
%token TOK_LBRACK, TOK_RBRACK,
%token TOK_LBRACE, TOK_RBRACE,
%token TOK_LT, TOK_RT,
%token TOK_EOF

%start program

%%

program
    : class_list
    ;

class_list
    : /* empty */
    | class_list class_declaration
    ;

class_declaration
    : TOK_CLASS class_name optional_extends TOK_IS TOK_LBRACE member_list TOK_RBRACE TOK_END
    ;

class_name
    : TOK_ID optional_generic_params
    ;

optional_generic_params
    : /* empty */
    | TOK_LBRACK type_list TOK_RBRACK
    ;

type_list
    : TOK_ID
    | type_list TOK_COMMA TOK_ID
    ;

optional_extends
    : /* empty */
    | TOK_EXTENDS TOK_ID
    ;

member_list
    : /* empty */
    | member_list member_declaration
    ;

member_declaration
    : variable_declaration
    | method_declaration
    | constructor_declaration
    ;

variable_declaration
    : TOK_VAR TOK_ID TOK_COLON expression
    ;

method_declaration
    : method_header optional_method_body
    ;

method_header
    : TOK_METHOD TOK_ID optional_parameters optional_return_type
    ;

optional_method_body
    : /* empty */
    | method_body
    ;

method_body
    : TOK_IS body TOK_END
    | TOK_ARROW expression
    ;

optional_parameters
    : /* empty */
    | parameters
    ;

parameters
    : TOK_LPAR parameter_list TOK_RPAR
    | TOK_LPAR TOK_RPAR
    ;

parameter_list
    : parameter_declaration
    | parameter_list TOK_COMMA parameter_declaration
    ;

parameter_declaration
    : TOK_ID TOK_COLON class_name
    ;

optional_return_type
    : /* empty */
    | TOK_COLON class_name
    ;

constructor_declaration
    : TOK_THIS optional_parameters TOK_IS body TOK_END
    ;

body
    : /* empty */
    | body statement
    | body variable_declaration
    ;

statement
    : assignment
    | while_loop
    | if_statement
    | return_statement
    | print_statement
    ;

assignment
    : TOK_ID TOK_ASSIGN expression
    ;

while_loop
    : TOK_WHILE expression TOK_LOOP body TOK_END
    ;

if_statement
    : TOK_IF expression TOK_THEN body optional_else TOK_END
    ;

optional_else
    : /* empty */
    | TOK_ELSE body
    ;

return_statement
    : TOK_RETURN optional_expression
    ;

optional_expression
    : /* empty */
    | expression
    ;

expression
    : primary
    | constructor_invocation
    | function_call
    | expression TOK_DOT expression
    ;

print_statement
    : TOK_PRINT expression
    ;

primary
    : TOK_INT_LIT
    | TOK_REAL_LIT
    | TOK_BOOL_LIT
    | TOK_ID
    | TOK_THIS
    ;

constructor_invocation
    : class_name optional_arguments
    ;

function_call
    : expression optional_arguments
    ;

optional_arguments
    : /* empty */
    | arguments
    ;

arguments
    : TOK_LPAR TOK_RPAR
    | TOK_LPAR arg_list TOK_RPAR
    ;

arg_list
    : expression
    | arg_list TOK_COMMA expression
    ;

%%

void yyerror(String s) {
    System.err.println("Syntax error: " + s);
}
