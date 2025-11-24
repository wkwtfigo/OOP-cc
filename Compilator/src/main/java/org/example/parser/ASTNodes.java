package org.example.parser;

import java.util.List;

// Base nodes
abstract class ASTNode {
    public abstract void accept(ASTVisitor visitor);

    public abstract ASTNode accept(ASTOptimizerVisitor visitor);
}

abstract class ExpressionNode extends ASTNode {
}

abstract class StatementNode extends ASTNode implements BodyElementNode {
}

interface BodyElementNode {
}

abstract class MemberNode extends ASTNode {
}

// Program
class ProgramNode extends ASTNode {
    public List<ClassDeclNode> classes;

    public ProgramNode(List<ClassDeclNode> classes) {
        this.classes = classes;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
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

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

// Variable declaration
enum VarDeclType {
    COLON, ASSIGN, IS
}

class VarDeclNode extends MemberNode implements BodyElementNode {
    public String varName;
    public ASTNode type;
    public ExpressionNode initializer;
    public VarDeclType declType;

    public VarDeclNode(String name, ASTNode type, ExpressionNode initializer, VarDeclType declType) {
        this.varName = name;
        this.type = type;
        this.initializer = initializer;
        this.declType = declType;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
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

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class MethodHeaderNode extends ASTNode {
    public String methodName;
    public List<ParamDeclNode> parameters;
    public ASTNode returnType;

    public MethodHeaderNode(String name, List<ParamDeclNode> params, ASTNode returnType) {
        this.methodName = name;
        this.parameters = params;
        this.returnType = returnType;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
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

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

// Parameter declaration
class ParamDeclNode extends ASTNode {
    public String paramName;
    public ASTNode paramType; // TypeNode или GenericTypeNode

    public ParamDeclNode(String name, ASTNode type) {
        this.paramName = name;
        this.paramType = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
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

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

// Body and statements
class BodyNode extends ASTNode {
    public List<BodyElementNode> elements;

    public BodyNode(List<BodyElementNode> elements) {
        this.elements = elements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class TypeNode extends ASTNode {
    public String name;

    public TypeNode(String name) {
        this.name = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class GenericTypeNode extends TypeNode {
    public String baseType; // "Array" или "List"
    public TypeNode parameter; // например, Integer

    public GenericTypeNode(String baseType, TypeNode parameter) {
        super(baseType);
        this.baseType = baseType;
        this.parameter = parameter;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

// Statements
class AssignmentNode extends StatementNode {
    public ExpressionNode left; // левый операнд может быть IdentifierNode или MemberAccessNode
    public ExpressionNode right; // выражение справа

    public AssignmentNode(ExpressionNode left, ExpressionNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class WhileLoopNode extends StatementNode {
    public ExpressionNode condition;
    public BodyNode body;

    public WhileLoopNode(ExpressionNode cond, BodyNode body) {
        this.condition = cond;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
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

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class ReturnNode extends StatementNode {
    public ExpressionNode expression;

    public ReturnNode(ExpressionNode expr) {
        this.expression = expr;

    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class PrintNode extends StatementNode {
    public ExpressionNode expression;

    public PrintNode(ExpressionNode expr) {
        this.expression = expr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
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

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class ConstructorInvocationNode extends ExpressionNode {
    public String className;
    public List<ExpressionNode> arguments;

    public ConstructorInvocationNode(String className, List<ExpressionNode> args) {
        this.className = className;
        this.arguments = args;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class MethodInvocationNode extends ExpressionNode {
    public ExpressionNode target;
    public String methodName;
    public List<ExpressionNode> arguments;

    public MethodInvocationNode(ExpressionNode target, String methodName, List<ExpressionNode> args) {
        this.target = target;
        this.methodName = methodName;
        this.arguments = args;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

// Primary expressions
class IdentifierNode extends ExpressionNode {
    public String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class ThisNode extends ExpressionNode {

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class IntLiteralNode extends ExpressionNode {
    public Integer value;

    public IntLiteralNode(Integer value) {
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class RealLiteralNode extends ExpressionNode {
    public Double value;

    public RealLiteralNode(Double value) {
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}

class BoolLiteralNode extends ExpressionNode {
    public Boolean value;

    public BoolLiteralNode(Boolean value) {
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(ASTOptimizerVisitor visitor) {
        return visitor.visit(this);
    }
}