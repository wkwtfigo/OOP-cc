package org.example.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Optimizer - оптимизация AST через Visitor без мутации исходного дерева
 */
public class Optimizer implements ASTVisitor {

    private Set<String> usedVars = new HashSet<>();
    private ASTNode result; // хранит результат после visit
    private boolean isCollectingPhase = false;

    public ASTNode getResult() {
        return result;
    }

    // ============================================================
    // Сбор используемых идентификаторов
    // ============================================================
    public void collectUsedIdentifiers(ASTNode node) {
        isCollectingPhase = true;
        usedVars.clear(); // Очищаем перед сбором
        if (node != null) node.accept(this);
        isCollectingPhase = false;
    }

    // ============================================================
    // Оптимизация AST
    // ============================================================
    public ASTNode optimize(ASTNode node) {
        isCollectingPhase = false;
        if (node != null) node.accept(this);
        return result;
    }

    // --------------------- Program ---------------------
    @Override
    public void visit(ProgramNode node) {
        if (isCollectingPhase) {
            // Фаза сбора: просто собираем идентификаторы
            for (ClassDeclNode c : node.classes) {
                c.accept(this);
            }
            result = node; // Не важно что возвращаем в фазе сбора
        } else {
            // Фаза оптимизации: создаем новые узлы
            List<ClassDeclNode> newClasses = new ArrayList<>();
            for (ClassDeclNode c : node.classes) {
                c.accept(this);
                newClasses.add((ClassDeclNode) result);
            }
            result = new ProgramNode(newClasses);
        }
    }

    // --------------------- Class ---------------------
    @Override
    public void visit(ClassDeclNode node) {
        List<MemberNode> newMembers = new ArrayList<>();
        for (MemberNode m : node.members) {
            m.accept(this);
            newMembers.add((MemberNode) result);
        }
        result = new ClassDeclNode(node.className, node.extendsClass, newMembers);
    }

    // --------------------- Variable ---------------------
    @Override
    public void visit(VarDeclNode node) {
        if (isCollectingPhase) {
            // В фазе сбора: собираем идентификаторы из инициализатора
            if (node.initializer != null) {
                node.initializer.accept(this);
            }
            result = node;
        } else {
            // В фазе оптимизации: удаляем неиспользуемые переменные
            if (!usedVars.contains(node.varName)) {
                System.out.println("Removed unused variable: " + node.varName);
                result = null;
            } else {
                ASTNode newType = null;
                ExpressionNode newInit = null;
                if (node.type != null) {
                    node.type.accept(this);
                    newType = result;
                }
                if (node.initializer != null) {
                    node.initializer.accept(this);
                    newInit = (ExpressionNode) result;
                }
                result = new VarDeclNode(node.varName, newType, newInit, node.declType);
            }
        }
    }

    // --------------------- Method ---------------------
    @Override
    public void visit(MethodDeclNode node) {
        node.header.accept(this);
        MethodHeaderNode newHeader = (MethodHeaderNode) result;

        node.body.accept(this);
        MethodBodyNode newBody = (MethodBodyNode) result;

        result = new MethodDeclNode(newHeader, newBody);
    }

    @Override
    public void visit(MethodHeaderNode node) {
        List<ParamDeclNode> newParams = new ArrayList<>();
        if (node.parameters != null) {
            for (ParamDeclNode p : node.parameters) {
                p.accept(this);
                newParams.add((ParamDeclNode) result);
            }
        }
        result = new MethodHeaderNode(node.methodName, newParams, node.returnType);
    }

    @Override
    public void visit(MethodBodyNode node) {
        if (node.isArrow) {
            if (node.arrowExpression != null) {
                node.arrowExpression.accept(this);
                result = new MethodBodyNode((ExpressionNode) result, true);
            } else {
                result = new MethodBodyNode((ExpressionNode) null, true);
            }
        } else {
            if (node.body != null) {
                node.body.accept(this);
                result = new MethodBodyNode((BodyNode) result, false);
            } else {
                result = new MethodBodyNode(new BodyNode(new ArrayList<>()), false);
            }
        }
    }

    @Override
    public void visit(ParamDeclNode node) {
        ASTNode newType = null;
        if (node.paramType != null) {
            node.paramType.accept(this);
            newType = result;
        }
        result = new ParamDeclNode(node.paramName, newType);
    }

    @Override
    public void visit(ConstructorDeclNode node) {
        List<ParamDeclNode> newParams = new ArrayList<>();
        if (node.parameters != null) {
            for (ParamDeclNode p : node.parameters) {
                p.accept(this);
                newParams.add((ParamDeclNode) result);
            }
        }
        node.body.accept(this);
        BodyNode newBody = (BodyNode) result;
        result = new ConstructorDeclNode(newParams, newBody);
    }

    // --------------------- Body ---------------------
    @Override
    public void visit(BodyNode node) {
        List<BodyElementNode> newElems = new ArrayList<>();
        boolean unreachable = false;

        for (BodyElementNode e : node.elements) {
            if (unreachable) continue;

            ((ASTNode) e).accept(this);

            if (result != null) {
                if (result instanceof BodyNode bNode) {
                    // разворачиваем внутренние элементы
                    newElems.addAll(bNode.elements);
                    // проверка на ReturnNode внутри
                    for (BodyElementNode inner : bNode.elements) {
                        if (inner instanceof ReturnNode) unreachable = true;
                    }
                } else {
                    newElems.add((BodyElementNode) result);
                    if (result instanceof ReturnNode) unreachable = true;
                }
            }
        }

        result = new BodyNode(newElems);
    }

    // --------------------- Statements ---------------------
    @Override
    public void visit(AssignmentNode node) {
        node.left.accept(this);
        ExpressionNode newLeft = (ExpressionNode) result;
        node.right.accept(this);
        ExpressionNode newRight = (ExpressionNode) result;
        result = new AssignmentNode(newLeft, newRight);
    }

    @Override
    public void visit(IfStatementNode node) {
        node.condition.accept(this);
        ExpressionNode newCond = (ExpressionNode) result;

        node.thenBody.accept(this);
        BodyNode newThen = (BodyNode) result;

        BodyNode newElse = null;
        if (node.elseBody != null) {
            node.elseBody.accept(this);
            newElse = (BodyNode) result;
        }

        // оптимизация константного условия
        if (newCond instanceof BoolLiteralNode boolLit) {
            result = boolLit.value ? newThen : (newElse != null ? newElse : new BodyNode(new ArrayList<>()));
            System.out.println("Optimized IfStatementNode with constant condition: " + boolLit.value);
        } else {
            result = new IfStatementNode(newCond, newThen, newElse);
        }
    }

    @Override
    public void visit(WhileLoopNode node) {
        node.condition.accept(this);
        ExpressionNode newCond = (ExpressionNode) result;

        node.body.accept(this);
        BodyNode newBody = (BodyNode) result;

        if (newCond instanceof BoolLiteralNode b && !b.value) {
            result = new BodyNode(new ArrayList<>());
            System.out.println("Removed unreachable WhileLoopNode");
        } else {
            result = new WhileLoopNode(newCond, newBody);
        }
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.expression != null) {
            node.expression.accept(this);
        }
        result = new ReturnNode((ExpressionNode) result);
    }

    @Override
    public void visit(PrintNode node) {
        node.expression.accept(this);
        result = new PrintNode((ExpressionNode) result);
    }

    @Override
    public void visit(MemberAccessNode node) {
        node.target.accept(this);
        ExpressionNode newTarget = (ExpressionNode) result;

        node.member.accept(this);
        ExpressionNode newMember = (ExpressionNode) result;

        result = new MemberAccessNode(newTarget, newMember);
    }

    @Override
    public void visit(ConstructorInvocationNode node) {
        List<ExpressionNode> newArgs = new ArrayList<>();
        if (node.arguments != null) {
            for (ExpressionNode arg : node.arguments) {
                arg.accept(this);
                newArgs.add((ExpressionNode) result);
            }
        }
        result = new ConstructorInvocationNode(node.className, newArgs);
    }

    @Override
    public void visit(MethodInvocationNode node) {
        if (node.target != null) node.target.accept(this);
        ExpressionNode newTarget = (ExpressionNode) result;

        List<ExpressionNode> newArgs = new ArrayList<>();
        if (node.arguments != null) {
            for (ExpressionNode arg : node.arguments) {
                arg.accept(this);
                newArgs.add((ExpressionNode) result);
            }
        }

        result = new MethodInvocationNode(newTarget, newArgs);
    }

    // --------------------- Primary expressions ---------------------
    @Override
    public void visit(IdentifierNode node) {
        usedVars.add(node.name);
        result = node;
    }

    @Override
    public void visit(ThisNode node) {
        result = node;
    }

    @Override
    public void visit(IntLiteralNode node) {
        result = node;
    }

    @Override
    public void visit(RealLiteralNode node) {
        result = node;
    }

    @Override
    public void visit(BoolLiteralNode node) {
        result = node;
    }

    // --------------------- Types ---------------------
    @Override
    public void visit(TypeNode node) {
        result = node;
    }

    @Override
    public void visit(GenericTypeNode node) {
        if (node.parameter != null) node.parameter.accept(this);
        result = new GenericTypeNode(node.baseType, (TypeNode) result);
    }
}
