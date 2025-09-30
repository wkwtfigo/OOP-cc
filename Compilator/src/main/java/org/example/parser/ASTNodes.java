package org.example.parser;

import java.util.List;

// Base nodes
abstract class ASTNode {}

abstract class ExpressionNode extends ASTNode {}

abstract class StatementNode extends ASTNode {}

interface BodyElementNode{}

abstract class MemberNode extends ASTNode {}

// Program
class ProgramNode extends ASTNode {
    public List<ClassDeclNode> classes;
    public ProgramNode(List<ClassDeclNode> classes) { this.classes = classes; }
}

// Class declaration
class ClassDeclNode extends MemberNode {
    public String className;
    public String extendsClass;
    public List<MemberNode> members;
    public ClassDeclNode(String name, String extendsClass, List<MemberNode> members) {
        this.className = name; 
        this.extendsClass = extendsClass; 
        this.members = members;
    }
}

// Variable declaration
enum VarDeclType { COLON, ASSIGN, IS }

class VarDeclNode extends MemberNode implements BodyElementNode {
    public String varName;
    public String typeName; // can be null
    public ExpressionNode initializer;
    public VarDeclType declType;
    public VarDeclNode(String name, String type, ExpressionNode init, VarDeclType declType) {
        this.varName = name; 
        this.typeName = type; 
        this.initializer = init; 
        this.declType = declType;
    }
}

// Method declaration
class MethodDeclNode extends MemberNode {
    public MethodHeaderNode header;
    public MethodBodyNode body;
    public MethodDeclNode(MethodHeaderNode header, MethodBodyNode body) {
        this.header = header; 
        this.body = body;
    }
}

class MethodHeaderNode extends ASTNode {
    public String methodName;
    public List<ParamDeclNode> parameters;
    public String returnType;
    public MethodHeaderNode(String name, List<ParamDeclNode> params, String returnType) {
        this.methodName = name; 
        this.parameters = params; 
        this.returnType = returnType;
    }
}

class MethodBodyNode extends ASTNode {
    public BodyNode body; // for full body
    public ExpressionNode arrowExpression; // for arrow syntax
    public boolean isArrow;
    
    // Constructor for full body
    public MethodBodyNode(BodyNode body, boolean isArrow) { 
        this.body = body; 
        this.isArrow = isArrow; 
    }
    
    // Constructor for arrow expression
    public MethodBodyNode(ExpressionNode expr, boolean isArrow) { 
        this.arrowExpression = expr; 
        this.isArrow = isArrow; 
    }
}

// Parameter declaration
class ParamDeclNode extends ASTNode {
    public String paramName;
    public String paramType;
    public ParamDeclNode(String name, String type) { 
        this.paramName = name; 
        this.paramType = type; 
    }
}

// Constructor declaration
class ConstructorDeclNode extends MemberNode {
    public List<ParamDeclNode> parameters;
    public BodyNode body;
    public ConstructorDeclNode(List<ParamDeclNode> params, BodyNode body) {
        this.parameters = params; 
        this.body = body;
    }
}

// Body and statements
class BodyNode extends ASTNode {
    public List<BodyElementNode> elements;
    public BodyNode(List<BodyElementNode> elements) { 
        this.elements = elements; 
    }
}

// Statements
class AssignmentNode extends StatementNode {
    public String varName;
    public ExpressionNode expression;
    public AssignmentNode(String name, ExpressionNode expr) { 
        this.varName = name; 
        this.expression = expr; 
    }
}

class WhileLoopNode extends StatementNode {
    public ExpressionNode condition;
    public BodyNode body;
    public WhileLoopNode(ExpressionNode cond, BodyNode body) { 
        this.condition = cond; 
        this.body = body; 
    }
}

class IfStatementNode extends StatementNode {
    public ExpressionNode condition;
    public BodyNode thenBody;
    public BodyNode elseBody;
    public IfStatementNode(ExpressionNode cond, BodyNode thenBody, BodyNode elseBody) {
        this.condition = cond; 
        this.thenBody = thenBody; 
        this.elseBody = elseBody;
    }
}

class ReturnNode extends StatementNode {
    public ExpressionNode expression;
    public ReturnNode(ExpressionNode expr) { 
        this.expression = expr; 
    }
}

class PrintNode extends StatementNode {
    public ExpressionNode expression;
    public PrintNode(ExpressionNode expr) { 
        this.expression = expr; 
    }
}

// Expressions
class MemberAccessNode extends ExpressionNode {
    public ExpressionNode target;
    public ExpressionNode member;
    public MemberAccessNode(ExpressionNode target, ExpressionNode member) {
        this.target = target; 
        this.member = member;
    }
}

class ConstructorInvocationNode extends ExpressionNode {
    public String className;
    public List<ExpressionNode> arguments;
    public ConstructorInvocationNode(String className, List<ExpressionNode> args) {
        this.className = className; 
        this.arguments = args;
    }
}

class MethodInvocationNode extends ExpressionNode {
    public ExpressionNode target;
    public List<ExpressionNode> arguments;
    public MethodInvocationNode(ExpressionNode target, List<ExpressionNode> args) {
        this.target = target; 
        this.arguments = args;
    }
}

// Primary expressions
class IdentifierNode extends ExpressionNode {
    public String name;
    public IdentifierNode(String name) { 
        this.name = name; 
    }
}

class ThisNode extends ExpressionNode {}

class IntLiteralNode extends ExpressionNode {
    public Integer value;
    public IntLiteralNode(Integer value) { 
        this.value = value; 
    }
}

class RealLiteralNode extends ExpressionNode {
    public Double value;
    public RealLiteralNode(Double value) { 
        this.value = value; 
    }
}

class BoolLiteralNode extends ExpressionNode {
    public Boolean value;
    public BoolLiteralNode(Boolean value) { 
        this.value = value; 
    }
}