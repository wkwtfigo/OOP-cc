package org.example.parser;

public class MyCOdeGen implements ASTVisitor{
  private static StringBuilder jasminCode = new StringBuilder();

  @Override
  public void visit(ProgramNode node) {


    for (ClassDeclNode classNode : node.classes) {
        classNode.accept(this);
    }
  }

  @Override
  public void visit(ClassDeclNode node) {

  jasminCode.append(".class ").append(node.className).append("\n");
    String parent = node.extendsClass;
    if (parent.isEmpty()) {
    jasminCode.append(".super java/lang/Object\n");

  } else {
    jasminCode.append(".super ").append(parent.replace('.', '/')).append("\n");
  }
    for (MemberNode member : node.members) {
        member.accept(this);
    }
  }

  @Override
  public void visit(VarDeclNode node) {

  }

  @Override
  public void visit(MethodDeclNode node) {

  }

  @Override
  public void visit(MethodHeaderNode node) {

  }

  @Override
  public void visit(MethodBodyNode node) {

  }

  @Override
  public void visit(ParamDeclNode node) {

  }

  @Override
  public void visit(ConstructorDeclNode node) {

  }

  @Override
  public void visit(BodyNode node) {

  }

  @Override
  public void visit(AssignmentNode node) {

  }

  @Override
  public void visit(WhileLoopNode node) {

  }

  @Override
  public void visit(IfStatementNode node) {

  }

  @Override
  public void visit(ReturnNode node) {

  }

  @Override
  public void visit(PrintNode node) {

  }

  @Override
  public void visit(MemberAccessNode node) {

  }

  @Override
  public void visit(ConstructorInvocationNode node) {

  }

  @Override
  public void visit(MethodInvocationNode node) {

  }

  @Override
  public void visit(IdentifierNode node) {

  }

  @Override
  public void visit(ThisNode node) {

  }

  @Override
  public void visit(IntLiteralNode node) {

  }

  @Override
  public void visit(RealLiteralNode node) {

  }

  @Override
  public void visit(BoolLiteralNode node) {

  }

  @Override
  public void visit(TypeNode node) {

  }

  @Override
  public void visit(GenericTypeNode node) {

  }
}
