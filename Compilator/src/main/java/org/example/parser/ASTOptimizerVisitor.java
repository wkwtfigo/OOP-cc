package org.example.parser;

public interface ASTOptimizerVisitor {
    ASTNode visit(ProgramNode node);

    ASTNode visit(ClassDeclNode node);

    ASTNode visit(VarDeclNode node);

    ASTNode visit(MethodDeclNode node);

    ASTNode visit(MethodHeaderNode node);

    ASTNode visit(MethodBodyNode node);

    ASTNode visit(ParamDeclNode node);

    ASTNode visit(ConstructorDeclNode node);

    ASTNode visit(BodyNode node);

    ASTNode visit(AssignmentNode node);

    ASTNode visit(WhileLoopNode node);

    ASTNode visit(IfStatementNode node);

    ASTNode visit(ReturnNode node);

    ASTNode visit(PrintNode node);

    ASTNode visit(MemberAccessNode node);

    ASTNode visit(ConstructorInvocationNode node);

    ASTNode visit(MethodInvocationNode node);

    ASTNode visit(IdentifierNode node);

    ASTNode visit(ThisNode node);

    ASTNode visit(IntLiteralNode node);

    ASTNode visit(RealLiteralNode node);

    ASTNode visit(BoolLiteralNode node);

    ASTNode visit(TypeNode node);

    ASTNode visit(GenericTypeNode node);
}
