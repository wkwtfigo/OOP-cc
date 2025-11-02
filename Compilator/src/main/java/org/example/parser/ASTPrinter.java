package org.example.parser;

import java.util.List;

/**
 * ASTPrinter - реализация ASTVisitor для красивого вывода AST
 */
public class ASTPrinter implements ASTVisitor {
    private int indentLevel = 0;
    private StringBuilder output = new StringBuilder();
    
    private void printIndent() {
        for (int i = 0; i < indentLevel; i++) {
            output.append("  ");
        }
    }
    
    private void printLine(String line) {
        printIndent();
        output.append(line).append("\n");
    }
    
    private void printWithIndent(String prefix, String content) {
        printIndent();
        output.append(prefix).append(content).append("\n");
    }
    
    private void printList(List<?> list, String prefix) {
        if (list == null || list.isEmpty()) {
            printWithIndent(prefix, "[]");
            return;
        }
        
        printWithIndent(prefix, "[");
        indentLevel++;
        for (int i = 0; i < list.size(); i++) {
            printWithIndent("[" + i + "]: ", "");
            indentLevel++;
            ((ASTNode) list.get(i)).accept(this);
            indentLevel--;
        }
        indentLevel--;
        printWithIndent("", "]");
    }
    
    @Override
    public void visit(ProgramNode node) {
        printLine("Program:");
        indentLevel++;
        if (node.classes != null) {
            printList(node.classes, "Classes: ");
        } else {
            printLine("Classes: []");
        }
        indentLevel--;
    }
    
    @Override
    public void visit(ClassDeclNode node) {
        printWithIndent("Class: ", node.className);
        indentLevel++;
        if (node.extendsClass != null) {
            printWithIndent("Extends: ", node.extendsClass);
        }
        if (node.members != null) {
            printList(node.members, "Members: ");
        } else {
            printLine("Members: []");
        }
        indentLevel--;
    }
    
    @Override
    public void visit(VarDeclNode node) {
        printWithIndent("VarDecl: ", node.varName);
        indentLevel++;
        if (node.type != null) {
            printWithIndent("Type: ", "");
            indentLevel++;
            node.type.accept(this);  // рекурсивно печатаем TypeNode или GenericTypeNode
            indentLevel--;
        }
        printWithIndent("DeclType: ", node.declType.toString());
        if (node.initializer != null) {
            printWithIndent("Initializer: ", "");
            indentLevel++;
            node.initializer.accept(this);
            indentLevel--;
        }
        indentLevel--;
    }
    
    @Override
    public void visit(MethodDeclNode node) {
        printLine("MethodDecl:");
        indentLevel++;
        if (node.header != null) {
            printWithIndent("Header: ", "");
            indentLevel++;
            node.header.accept(this);
            indentLevel--;
        }
        if (node.body != null) {
            printWithIndent("Body: ", "");
            indentLevel++;
            node.body.accept(this);
            indentLevel--;
        }
        indentLevel--;
    }
    
    @Override
    public void visit(MethodHeaderNode node) {
        printWithIndent("MethodHeader: ", node.methodName);
        indentLevel++;
        if (node.returnType != null) {
            printWithIndent("ReturnType: ", node.returnType);
        }
        if (node.parameters != null) {
            printList(node.parameters, "Parameters: ");
        } else {
            printLine("Parameters: []");
        }
        indentLevel--;
    }
    
    @Override
    public void visit(MethodBodyNode node) {
        printWithIndent("MethodBody (isArrow: ", String.valueOf(node.isArrow) + ")");
        indentLevel++;
        if (node.isArrow && node.arrowExpression != null) {
            printWithIndent("ArrowExpression: ", "");
            indentLevel++;
            node.arrowExpression.accept(this);
            indentLevel--;
        } else if (!node.isArrow && node.body != null) {
            printWithIndent("Body: ", "");
            indentLevel++;
            node.body.accept(this);
            indentLevel--;
        }
        indentLevel--;
    }
    
    @Override
    public void visit(ParamDeclNode node) {
        printWithIndent("Param: ", node.paramName + " : ");
        indentLevel++;
        if (node.paramType != null) {
            node.paramType.accept(this);  // рекурсивно печатаем TypeNode или GenericTypeNode
        }
        indentLevel--;
}
    
    @Override
    public void visit(ConstructorDeclNode node) {
        printLine("Constructor:");
        indentLevel++;
        if (node.parameters != null) {
            printList(node.parameters, "Parameters: ");
        } else {
            printLine("Parameters: []");
        }
        if (node.body != null) {
            printWithIndent("Body: ", "");
            indentLevel++;
            node.body.accept(this);
            indentLevel--;
        }
        indentLevel--;
    }
    
    @Override
    public void visit(BodyNode node) {
        printLine("Body:");
        indentLevel++;
        if (node.elements != null) {
            printList(node.elements, "Elements: ");
        } else {
            printLine("Elements: []");
        }
        indentLevel--;
    }
    
    @Override
    public void visit(AssignmentNode node) {
        printWithIndent("Assignment:", "");
        indentLevel++;

        // Левый операнд (может быть IdentifierNode или MemberAccessNode)
        printWithIndent("Left:", "");
        indentLevel++;
        if (node.left != null) {
            node.left.accept(this);
        }
        indentLevel--;

        // Правый операнд (выражение)
        printWithIndent("Right:", "");
        indentLevel++;
        if (node.right != null) {
            node.right.accept(this);
        }
        indentLevel--;

        indentLevel--;
    }

    @Override
    public void visit(TypeNode node) {
        printWithIndent("Type: ", node.name);
    }

    @Override
    public void visit(GenericTypeNode node) {
        printLine("GenericType: " + node.baseType);
        indentLevel++;
        if (node.parameter != null) {
            printWithIndent("Parameter: ", "");
            indentLevel++;
            node.parameter.accept(this);
            indentLevel--;
        } else {
            printWithIndent("Parameter: ", "null");
        }
        indentLevel--;
    }

    
    @Override
    public void visit(WhileLoopNode node) {
        printLine("WhileLoop:");
        indentLevel++;
        if (node.condition != null) {
            printWithIndent("Condition: ", "");
            indentLevel++;
            node.condition.accept(this);
            indentLevel--;
        }
        if (node.body != null) {
            printWithIndent("Body: ", "");
            indentLevel++;
            node.body.accept(this);
            indentLevel--;
        }
        indentLevel--;
    }
    
    @Override
    public void visit(IfStatementNode node) {
        printLine("IfStatement:");
        indentLevel++;
        if (node.condition != null) {
            printWithIndent("Condition: ", "");
            indentLevel++;
            node.condition.accept(this);
            indentLevel--;
        }
        if (node.thenBody != null) {
            printWithIndent("ThenBody: ", "");
            indentLevel++;
            node.thenBody.accept(this);
            indentLevel--;
        }
        if (node.elseBody != null) {
            printWithIndent("ElseBody: ", "");
            indentLevel++;
            node.elseBody.accept(this);
            indentLevel--;
        }
        indentLevel--;
    }
    
    @Override
    public void visit(ReturnNode node) {
        printLine("Return:");
        indentLevel++;
        if (node.expression != null) {
            printWithIndent("Expression: ", "");
            indentLevel++;
            node.expression.accept(this);
            indentLevel--;
        }
        indentLevel--;
    }
    
    @Override
    public void visit(PrintNode node) {
        printLine("Print:");
        indentLevel++;
        if (node.expression != null) {
            printWithIndent("Expression: ", "");
            indentLevel++;
            node.expression.accept(this);
            indentLevel--;
        }
        indentLevel--;
    }
    
    @Override
    public void visit(MemberAccessNode node) {
        printLine("MemberAccess:");
        indentLevel++;
        if (node.target != null) {
            printWithIndent("Target: ", "");
            indentLevel++;
            node.target.accept(this);
            indentLevel--;
        }
        if (node.member != null) {
            printWithIndent("Member: ", "");
            indentLevel++;
            node.member.accept(this);
            indentLevel--;
        }
        indentLevel--;
    }
    
    @Override
    public void visit(ConstructorInvocationNode node) {
        printWithIndent("ConstructorInvocation: ", node.className);
        indentLevel++;
        if (node.arguments != null) {
            printList(node.arguments, "Arguments: ");
        } else {
            printLine("Arguments: []");
        }
        indentLevel--;
    }
    
    @Override
    public void visit(MethodInvocationNode node) {
        printLine("MethodInvocation:");
        indentLevel++;
        if (node.target != null) {
            printWithIndent("Target: ", "");
            indentLevel++;
            node.target.accept(this);
            indentLevel--;
        }
        if (node.arguments != null) {
            printList(node.arguments, "Arguments: ");
        } else {
            printLine("Arguments: []");
        }
        indentLevel--;
    }
    
    @Override
    public void visit(IdentifierNode node) {
        printWithIndent("Identifier: ", node.name);
    }
    
    @Override
    public void visit(ThisNode node) {
        printLine("This");
    }
    
    @Override
    public void visit(IntLiteralNode node) {
        printWithIndent("IntLiteral: ", String.valueOf(node.value));
    }
    
    @Override
    public void visit(RealLiteralNode node) {
        printWithIndent("RealLiteral: ", String.valueOf(node.value));
    }
    
    @Override
    public void visit(BoolLiteralNode node) {
        printWithIndent("BoolLiteral: ", String.valueOf(node.value));
    }
    
    public String getOutput() {
        return output.toString();
    }
    
    public void reset() {
        output = new StringBuilder();
        indentLevel = 0;
    }
}
