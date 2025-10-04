// org/example/parser/ASTVisitor.java
package org.example.parser;

public interface ASTVisitor {
  void visit(ProgramNode node);
  void visit(ClassDeclNode node);
  void visit(VarDeclNode node);
  void visit(MethodDeclNode node);
  void visit(MethodHeaderNode node);
  void visit(MethodBodyNode node);
  void visit(ParamDeclNode node);
  void visit(ConstructorDeclNode node);
  void visit(BodyNode node);
  void visit(AssignmentNode node);
  void visit(WhileLoopNode node);
  void visit(IfStatementNode node);
  void visit(ReturnNode node);
  void visit(PrintNode node);
  void visit(MemberAccessNode node);
  void visit(ConstructorInvocationNode node);
  void visit(MethodInvocationNode node);
  void visit(IdentifierNode node);
  void visit(ThisNode node);
  void visit(IntLiteralNode node);
  void visit(RealLiteralNode node);
  void visit(BoolLiteralNode node);
  void visit(TypeNode node);
  void visit(GenericTypeNode node);
}
