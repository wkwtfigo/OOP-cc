%language "Java"
%define api.package "org.example.parser"
%define api.parser.class {Parser}
%define api.value.type {Object}

%token TOK_CLASS TOK_EXTENDS TOK_IS
%token TOK_END TOK_VAR TOK_METHOD TOK_THIS
%token TOK_WHILE TOK_LOOP TOK_IF TOK_THEN
%token TOK_ELSE TOK_RETURN TOK_PRINT
%token TOK_ID TOK_TYPE_ID
%token TOK_INT_LIT TOK_REAL_LIT TOK_BOOL_LIT
%token TOK_ASSIGN
%token TOK_ARROW
%token TOK_DOT TOK_COLON TOK_COMMA
%token TOK_LPAR TOK_RPAR
%token TOK_LBRACK TOK_RBRACK
%token TOK_LBRACE TOK_RBRACE
%token TOK_LT TOK_RT
%token TOK_EOF

%left TOK_DOT

%start program

%%

program
    : class_list TOK_EOF
    ;

class_list
    : /* empty */
    | class_list class_declaration
    ;

class_declaration
    : TOK_CLASS TOK_TYPE_ID optional_extends TOK_IS member_list TOK_END
    ;

optional_extends
    : /* empty */
    | TOK_EXTENDS TOK_TYPE_ID
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
    : TOK_VAR TOK_ID TOK_COLON type_expression
    ;

type_expression
    : TOK_TYPE_ID
    | TOK_TYPE_ID TOK_LPAR argument_list_opt TOK_RPAR
    ;

method_declaration
    : method_header optional_method_body
    ;

method_header
    : TOK_METHOD TOK_ID parameter_list_opt optional_return_type
    ;

optional_method_body
    : /* empty */
    | method_body
    ;

method_body
    : TOK_IS body TOK_END
    | TOK_ARROW expression
    ;

parameter_list_opt
    : /* empty */
    | parameter_list
    ;

parameter_list
    : parameter_declaration
    | parameter_list TOK_COMMA parameter_declaration
    ;

parameter_declaration
    : TOK_ID TOK_COLON TOK_TYPE_ID
    ;

optional_return_type
    : /* empty */
    | TOK_COLON TOK_TYPE_ID
    ;

constructor_declaration
    : TOK_THIS TOK_LPAR parameter_list_opt TOK_RPAR TOK_IS body TOK_END
    ;

body
    : /* empty */
    | body_element_list
    ;

body_element_list
    : body_element
    | body_element_list body_element
    ;

body_element
    : statement
    | variable_declaration
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
    : TOK_RETURN return_expression_opt
    ;

return_expression_opt
    : /* empty */
    | expression
    ;

    
print_statement
    : TOK_PRINT expression
    ;

expression
    : primary
    | constructor_invocation
    | method_invocation
    | expression TOK_DOT method_invocation
    ;

primary
    : TOK_INT_LIT
    | TOK_REAL_LIT
    | TOK_BOOL_LIT
    | TOK_THIS
    ;

constructor_invocation
    : TOK_TYPE_ID TOK_LPAR argument_list_opt TOK_RPAR
    ;

method_invocation
    : TOK_ID TOK_LPAR argument_list_opt TOK_RPAR
    ;

argument_list_opt
    : /* empty */
    | argument_list
    ;

argument_list
    : expression
    | argument_list TOK_COMMA expression
    ;

%%
