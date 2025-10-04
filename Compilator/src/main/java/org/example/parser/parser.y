%language "Java"
%define api.package "org.example.parser"
%define api.parser.class {Parser}
%define api.parser.public
%define parse.error verbose
%define api.value.type {Object}

%{
import java.util.*;
import org.example.lexer.Token;
import org.example.parser.*;
%}

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

%right TOK_ASSIGN
%left TOK_DOT
%left TOK_LPAR TOK_RPAR

%start program

%%

program
    : TOK_EOF { $$ = new ProgramNode(null); }
    | class_list TOK_EOF { $$ = new ProgramNode((List<ClassDeclNode>)$1); }
    ;

class_list
    : class_declaration { $$ = new ArrayList<ClassDeclNode>(); ((List<ClassDeclNode>)$$).add((ClassDeclNode)$1); }
    | class_list class_declaration { ((List<ClassDeclNode>)$1).add((ClassDeclNode)$2); $$ = $1; }
    ;

class_declaration
    : TOK_CLASS TOK_TYPE_ID optional_extends TOK_IS member_list TOK_END
        { $$ = new ClassDeclNode(((Token)$2).getLexeme(), $3 == null ? null : (String)$3, (List<MemberNode>)$5); }
    ;

optional_extends
    : /* empty */ { $$ = null; }
    | TOK_EXTENDS TOK_TYPE_ID { $$ = ((Token)$2).getLexeme(); }
    ;

member_list
    : /* empty */ { $$ = new ArrayList<MemberNode>(); }
    | member_list member_declaration { ((List<MemberNode>)$1).add((MemberNode)$2); $$ = $1; }
    ;

member_declaration
    : variable_declaration { $$ = (MemberNode)$1; } 
    | method_declaration { $$ = (MemberNode)$1; } 
    | constructor_declaration { $$ = (MemberNode)$1; } 
    ;

variable_declaration
    : TOK_VAR TOK_ID TOK_COLON expression { $$ = new VarDeclNode(((Token)$2).getLexeme(), null, (ExpressionNode)$4, VarDeclType.COLON); }
    | TOK_VAR TOK_ID TOK_ASSIGN expression { $$ = new VarDeclNode(((Token)$2).getLexeme(), null, (ExpressionNode)$4, VarDeclType.ASSIGN); }
    | TOK_VAR TOK_ID TOK_IS expression { $$ = new VarDeclNode(((Token)$2).getLexeme(), null, (ExpressionNode)$4, VarDeclType.IS); }
    ;

method_declaration
    : method_header method_body
        { $$ = new MethodDeclNode((MethodHeaderNode)$1, (MethodBodyNode)$2); }
    ;

method_header
    : TOK_METHOD TOK_ID TOK_LPAR parameter_list_opt TOK_RPAR optional_return_type
      { $$ = new MethodHeaderNode(((Token)$2).getLexeme(), (List<ParamDeclNode>)$4, $6 == null ? null : ((Token)$6).getLexeme()); }
    ;

method_body
    : TOK_IS body TOK_END { $$ = new MethodBodyNode((BodyNode)$2, false); }
    | TOK_ARROW expression { $$ = new MethodBodyNode((ExpressionNode)$2, true); }
    ;

parameter_list_opt
    : /* empty */ { $$ = new ArrayList<ParamDeclNode>(); }
    | parameter_list { $$ = $1; }
    ;

parameter_list
    : parameter_declaration
        { $$ = new ArrayList<ParamDeclNode>(); ((List<ParamDeclNode>)$$).add((ParamDeclNode)$1); }
    | parameter_list TOK_COMMA parameter_declaration
        { ((List<ParamDeclNode>)$1).add((ParamDeclNode)$3); $$ = $1; }
    ;

parameter_declaration
    : TOK_ID TOK_COLON TOK_TYPE_ID { $$ = new ParamDeclNode(((Token)$1).getLexeme(), ((Token)$3).getLexeme()); }
    ;

optional_return_type
    : /* empty */ { $$ = null; }
    | TOK_COLON TOK_TYPE_ID { $$ = ((Token)$2).getLexeme(); }
    ;

constructor_declaration
    : TOK_THIS TOK_LPAR parameter_list_opt TOK_RPAR TOK_IS body TOK_END
        { $$ = new ConstructorDeclNode((List<ParamDeclNode>)$3, (BodyNode)$6); }
    ;

body
    : /* empty */ { $$ = new BodyNode(null); }
    | body_element_list { $$ = new BodyNode((List<BodyElementNode>)$1); }
    ;

body_element_list
    : body_element
        { $$ = new ArrayList<BodyElementNode>(); ((List<BodyElementNode>)$$).add((BodyElementNode)$1); }
    | body_element_list body_element
        { ((List<BodyElementNode>)$1).add((BodyElementNode)$2); $$ = $1; }
    ;

body_element
    : statement { $$ = (BodyElementNode)$1; }
    | variable_declaration { $$ = (BodyElementNode)$1; }
    ;

statement
    : assignment { $$ = (StatementNode)$1; }
    | while_loop { $$ = (StatementNode)$1; }
    | if_statement { $$ = (StatementNode)$1; }
    | return_statement { $$ = (StatementNode)$1; }
    | print_statement { $$ = (StatementNode)$1; }
    ;

assignment
    : TOK_ID TOK_ASSIGN expression { $$ = new AssignmentNode(((Token)$1).getLexeme(), (ExpressionNode)$3); }
    ;

while_loop
    : TOK_WHILE expression TOK_LOOP body TOK_END { $$ = new WhileLoopNode((ExpressionNode)$2, (BodyNode)$4); }
    ;

if_statement
    : TOK_IF expression TOK_THEN body optional_else TOK_END
        { $$ = new IfStatementNode((ExpressionNode)$2, (BodyNode)$4, (BodyNode)$5); }
    ;

optional_else
    : /* empty */ { $$ = null; }
    | TOK_ELSE body { $$ = (BodyNode)$2; }
    ;

return_statement
    : TOK_RETURN return_expression_opt { $$ = new ReturnNode((ExpressionNode)$2); }
    ;

return_expression_opt
    : /* empty */ { $$ = null; }
    | expression { $$ = $1; }
    ;

print_statement
    : TOK_PRINT expression { $$ = new PrintNode((ExpressionNode)$2); }
    ;

expression
    : primary { $$ = $1; }
    | constructor_invocation { $$ = $1; }
    | method_invocation { $$ = $1; }
    | expression TOK_DOT method_invocation
        { $$ = new MemberAccessNode((ExpressionNode)$1, (MethodInvocationNode)$3); }
    | expression TOK_DOT TOK_ID { $$ = new MemberAccessNode((ExpressionNode)$1, new IdentifierNode(((Token)$3).getLexeme())); }
    ;

primary
    : TOK_ID        { $$ = new IdentifierNode(((Token)$1).getLexeme()); }
    | TOK_INT_LIT   { $$ = new IntLiteralNode(Integer.parseInt(((Token)$1).getLexeme())); }
    | TOK_REAL_LIT  { $$ = new RealLiteralNode(Double.parseDouble(((Token)$1).getLexeme())); }
    | TOK_BOOL_LIT  { $$ = new BoolLiteralNode(Boolean.parseBoolean(((Token)$1).getLexeme())); }
    | TOK_THIS      { $$ = new ThisNode(); }
    ;

constructor_invocation
    : TOK_TYPE_ID TOK_LPAR argument_list_opt TOK_RPAR
        { $$ = new ConstructorInvocationNode(((Token)$1).getLexeme(), (List<ExpressionNode>)$3); }
    ;

method_invocation
    : TOK_ID TOK_LPAR argument_list_opt TOK_RPAR
        { $$ = new MethodInvocationNode(new IdentifierNode(((Token)$1).getLexeme()), (List<ExpressionNode>)$3); }

argument_list_opt
    : /* empty */ { $$ = new ArrayList<ExpressionNode>(); }
    | argument_list { $$ = $1; }
    ;

argument_list
    : expression
        { $$ = new ArrayList<ExpressionNode>(); ((List<ExpressionNode>)$$).add((ExpressionNode)$1); }
    | argument_list TOK_COMMA expression    
        { ((List<ExpressionNode>)$1).add((ExpressionNode)$3); $$ = $1; }
    ;

%%
