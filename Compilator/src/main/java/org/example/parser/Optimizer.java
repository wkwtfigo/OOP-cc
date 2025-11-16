package org.example.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Optimizer implements ASTOptimizerVisitor {

    // Flags for optimization management
    private boolean enableConstantFolding = true;
    private boolean enableUnreachableCodeElimination = true;

    private boolean enableRemoveUnusedVariables = true;
    private Set<String> usedVariables = new HashSet<>();

    //
    private boolean inReturnContext = false;

    /**
     * Optimization 1: Constant Expression Simplification (Constant Folding)
     * Simplify constant expressions during compilation
     */
    private void constantFolding(ASTNode node) {
        if (node instanceof ProgramNode) {
            ProgramNode program = (ProgramNode) node;
            if (program.classes != null) {
                for (ClassDeclNode classDecl : program.classes) {
                    constantFolding(classDecl);
                }
            }
        } else if (node instanceof ClassDeclNode) {
            ClassDeclNode classDecl = (ClassDeclNode) node;
            if (classDecl.members != null) {
                for (MemberNode member : classDecl.members) {
                    constantFolding(member);
                }
            }
        } else if (node instanceof MethodDeclNode) {
            MethodDeclNode methodDecl = (MethodDeclNode) node;
            if (methodDecl.body != null) {
                constantFolding(methodDecl.body);
            }
        } else if (node instanceof ConstructorDeclNode) {
            ConstructorDeclNode constructor = (ConstructorDeclNode) node;
            if (constructor.body != null) {
                constantFolding(constructor.body);
            }
        } else if (node instanceof MethodBodyNode) {
            MethodBodyNode body = (MethodBodyNode) node;
            if (body.isArrow && body.arrowExpression != null) {
                body.arrowExpression = foldConstantExpression(body.arrowExpression);
            } else if (!body.isArrow && body.body != null) {
                constantFolding(body.body);
            }
        } else if (node instanceof BodyNode) {
            BodyNode body = (BodyNode) node;
            if (body.elements != null) {
                for (int i = 0; i < body.elements.size(); i++) {
                    BodyElementNode element = body.elements.get(i);
                    if (element instanceof VarDeclNode) {
                        VarDeclNode varDecl = (VarDeclNode) element;
                        if (varDecl.initializer != null) {
                            varDecl.initializer = foldConstantExpression(varDecl.initializer);
                        }
                    } else if (element instanceof AssignmentNode) {
                        AssignmentNode assignment = (AssignmentNode) element;
                        assignment.right = foldConstantExpression(assignment.right);
                        if (assignment.left instanceof ExpressionNode) {
                            assignment.left = foldConstantExpression((ExpressionNode) assignment.left);
                        }
                    } else if (element instanceof WhileLoopNode) {
                        WhileLoopNode whileLoop = (WhileLoopNode) element;
                        whileLoop.condition = foldConstantExpression(whileLoop.condition);
                        constantFolding(whileLoop.body);
                    } else if (element instanceof IfStatementNode) {
                        IfStatementNode ifStmt = (IfStatementNode) element;
                        ifStmt.condition = foldConstantExpression(ifStmt.condition);
                        constantFolding(ifStmt.thenBody);
                        if (ifStmt.elseBody != null) {
                            constantFolding(ifStmt.elseBody);
                        }
                    } else if (element instanceof ReturnNode) {
                        ReturnNode returnNode = (ReturnNode) element;
                        if (returnNode.expression != null) {
                            returnNode.expression = foldConstantExpression(returnNode.expression);
                        }
                    } else if (element instanceof PrintNode) {
                        PrintNode printNode = (PrintNode) element;
                        printNode.expression = foldConstantExpression(printNode.expression);
                    }
                }
            }
        }
    }

    /**
     * Fold constant expressions to their simplified form
     */
    private ExpressionNode foldConstantExpression(ExpressionNode expr) {
        if (expr == null)
            return null;

        // Рекурсивно сворачиваем подвыражения
        if (expr instanceof MemberAccessNode) {
            MemberAccessNode memberAccess = (MemberAccessNode) expr;
            memberAccess.target = foldConstantExpression(memberAccess.target);
            if (memberAccess.member instanceof ExpressionNode) {
                memberAccess.member = foldConstantExpression((ExpressionNode) memberAccess.member);
            }
        } else if (expr instanceof MethodInvocationNode) {
            MethodInvocationNode methodCall = (MethodInvocationNode) expr;
            methodCall.target = foldConstantExpression(methodCall.target);
            if (methodCall.arguments != null) {
                for (int i = 0; i < methodCall.arguments.size(); i++) {
                    methodCall.arguments.set(i, foldConstantExpression(methodCall.arguments.get(i)));
                }
            }
        } else if (expr instanceof ConstructorInvocationNode) {
            ConstructorInvocationNode constructor = (ConstructorInvocationNode) expr;
            if (constructor.arguments != null) {
                for (int i = 0; i < constructor.arguments.size(); i++) {
                    constructor.arguments.set(i, foldConstantExpression(constructor.arguments.get(i)));
                }
            }

            String className = constructor.className;
            if (constructor.arguments.size() == 1 && constructor.arguments.get(0) instanceof IntLiteralNode) {
                if ("Integer".equals(className)) {
                    int value = ((IntLiteralNode) constructor.arguments.get(0)).value;
                    return new IntLiteralNode(value);
                }
            } else if (constructor.arguments.size() == 1 && constructor.arguments.get(0) instanceof RealLiteralNode) {
                if ("Double".equals(className)) {
                    double value = ((RealLiteralNode) constructor.arguments.get(0)).value;
                    return new RealLiteralNode(value);
                }
            } else if (constructor.arguments.size() == 1 && constructor.arguments.get(0) instanceof BoolLiteralNode) {
                if ("Boolean".equals(className)) {
                    boolean value = ((BoolLiteralNode) constructor.arguments.get(0)).value;
                    return new BoolLiteralNode(value);
                }
            }
        }

        if (expr instanceof IntLiteralNode || expr instanceof RealLiteralNode || expr instanceof BoolLiteralNode) {
            return expr;
        }

        return expr;
    }

    /**
     * Check if expression is constant (can be evaluated at compile time)
     */
    private boolean isConstantExpression(ExpressionNode expr) {
        if (expr instanceof IntLiteralNode || expr instanceof BoolLiteralNode || expr instanceof RealLiteralNode) {
            return true;
        }

        // Проверяем операции с константами
        if (expr instanceof MethodInvocationNode) {
            MethodInvocationNode methodCall = (MethodInvocationNode) expr;
            // Проверяем только встроенные методы для констант
            if (methodCall.target instanceof IdentifierNode) {
                String methodName = ((IdentifierNode) methodCall.target).name;
                if (methodCall.arguments != null && methodCall.arguments.size() == 1) {
                    ExpressionNode arg = methodCall.arguments.get(0);
                    if (isConstantExpression(arg)) {
                        // Проверяем арифметические операции для Integer
                        if (methodName.equals("Plus") || methodName.equals("Minus") ||
                                methodName.equals("Mult") || methodName.equals("Div") ||
                                methodName.equals("Less") || methodName.equals("LessEqual") ||
                                methodName.equals("Greater") || methodName.equals("GreaterEqual") ||
                                methodName.equals("Equal")) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Evaluate constant expression at compile time
     */
    private Object evaluateConstantExpression(ExpressionNode expr) {
        if (expr instanceof IntLiteralNode) {
            return ((IntLiteralNode) expr).value;
        } else if (expr instanceof BoolLiteralNode) {
            return ((BoolLiteralNode) expr).value;
        } else if (expr instanceof RealLiteralNode) {
            return ((RealLiteralNode) expr).value;
        }

        if (expr instanceof MethodInvocationNode) {
            MethodInvocationNode methodCall = (MethodInvocationNode) expr;

            if (methodCall.target instanceof IdentifierNode && methodCall.arguments != null
                    && methodCall.arguments.size() == 1) {
                String methodName = ((IdentifierNode) methodCall.target).name;
                ExpressionNode arg = methodCall.arguments.get(0);

                Object leftValue = evaluateConstantExpression(methodCall.target);
                Object rightValue = evaluateConstantExpression(arg);

                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    int left = (Integer) leftValue;
                    int right = (Integer) rightValue;

                    switch (methodName) {
                        case "Plus":
                            return left + right;
                        case "Minus":
                            return left - right;
                        case "Mult":
                            return left * right;
                        case "Div":
                            return right != 0 ? left / right : 0;
                        case "Less":
                            return left < right;
                        case "LessEqual":
                            return left <= right;
                        case "Greater":
                            return left > right;
                        case "GreaterEqual":
                            return left >= right;
                        case "Equal":
                            return left == right;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Optimization 2: Unreachable Code Elimination
     * Remove code that will never be executed
     */
    private void eliminateUnreachableCode(ASTNode node) {
        if (node instanceof ProgramNode) {
            ProgramNode program = (ProgramNode) node;
            if (program.classes != null) {
                for (ClassDeclNode classDecl : program.classes) {
                    eliminateUnreachableCode(classDecl);
                }
            }
        } else if (node instanceof ClassDeclNode) {
            ClassDeclNode classDecl = (ClassDeclNode) node;
            if (classDecl.members != null) {
                for (MemberNode member : classDecl.members) {
                    eliminateUnreachableCode(member);
                }
            }
        } else if (node instanceof MethodDeclNode) {
            MethodDeclNode methodDecl = (MethodDeclNode) node;
            inReturnContext = false;
            if (methodDecl.body != null) {
                eliminateUnreachableCode(methodDecl.body);
            }
        } else if (node instanceof ConstructorDeclNode) {
            ConstructorDeclNode constructor = (ConstructorDeclNode) node;
            inReturnContext = false;
            if (constructor.body != null) {
                eliminateUnreachableCode(constructor.body);
            }
        } else if (node instanceof MethodBodyNode) {
            MethodBodyNode body = (MethodBodyNode) node;
            if (body.isArrow) {
                inReturnContext = true;
            } else if (body.body != null) {
                eliminateUnreachableCode(body.body);
            }
        } else if (node instanceof BodyNode) {
            BodyNode body = (BodyNode) node;
            if (body.elements != null) {
                List<BodyElementNode> newElements = new ArrayList<>();
                boolean foundUnreachable = false;

                for (BodyElementNode element : body.elements) {
                    if (foundUnreachable) {
                        continue;
                    }

                    newElements.add(element);

                    // Если это return statement, отмечаем что следующий код недостижим
                    if (element instanceof ReturnNode) {
                        foundUnreachable = true;
                    }
                    // Если это if с константным условием, можем оптимизировать ветки
                    else if (element instanceof IfStatementNode) {
                        IfStatementNode ifStmt = (IfStatementNode) element;
                        if (isConstantExpression(ifStmt.condition)) {
                            Object conditionValue = evaluateConstantExpression(ifStmt.condition);
                            if (conditionValue instanceof Boolean) {
                                if ((Boolean) conditionValue) {
                                    // Условие всегда true - оставляем только then ветку
                                    ifStmt.elseBody = null;
                                } else {
                                    // Условие всегда false - оставляем только else ветку, если есть
                                    if (ifStmt.elseBody != null) {
                                        // Заменяем if на содержимое else ветки
                                        newElements.remove(newElements.size() - 1); // Удаляем if
                                        if (ifStmt.elseBody.elements != null) {
                                            newElements.addAll(ifStmt.elseBody.elements);
                                        }
                                    } else {
                                        newElements.remove(newElements.size() - 1); // Удаляем if полностью
                                    }
                                }
                            }
                        }
                    }
                    // Если это while с константным false условием, удаляем весь цикл
                    else if (element instanceof WhileLoopNode) {
                        WhileLoopNode whileLoop = (WhileLoopNode) element;
                        if (isConstantExpression(whileLoop.condition)) {
                            Object conditionValue = evaluateConstantExpression(whileLoop.condition);
                            if (conditionValue instanceof Boolean && !(Boolean) conditionValue) {
                                newElements.remove(newElements.size() - 1); // Удаляем цикл
                            } else if (conditionValue instanceof Boolean && (Boolean) conditionValue) {
                                continue;
                            }
                        }
                    }
                }

                // Заменяем элементы на оптимизированный список
                body.elements = newElements;

                // Рекурсивно обрабатываем оставшиеся элементы
                for (BodyElementNode element : newElements) {
                    if (element instanceof WhileLoopNode) {
                        eliminateUnreachableCode(((WhileLoopNode) element).body);
                    } else if (element instanceof IfStatementNode) {
                        IfStatementNode ifStmt = (IfStatementNode) element;
                        eliminateUnreachableCode(ifStmt.thenBody);
                        if (ifStmt.elseBody != null) {
                            eliminateUnreachableCode(ifStmt.elseBody);
                        }
                    }
                }
            }
        }
    }

    /**
     * Optimization 3: Remove Unused Variables
     * Remove variables that are declared but never used
     */
    private void removeUnusedVariables(ASTNode node) {
        // Фаза 1: сбор всех используемых идентификаторов
        collectUsedIdentifiers(node);

        // Фаза 2: удаление неиспользуемых переменных
        removeUnusedVarDeclarations(node);
    }

    /**
     * Phase 1: Collect all used variable names
     */
    private void collectUsedIdentifiers(ASTNode node) {
        if (node == null)
            return;

        if (node instanceof IdentifierNode) {
            String varName = ((IdentifierNode) node).name;
            usedVariables.add(varName);
        }

        // Рекурсивно обходим дочерние узлы
        if (node instanceof ProgramNode) {
            ProgramNode program = (ProgramNode) node;
            if (program.classes != null) {
                for (ClassDeclNode classDecl : program.classes) {
                    collectUsedIdentifiers(classDecl);
                }
            }
        } else if (node instanceof ClassDeclNode) {
            ClassDeclNode classDecl = (ClassDeclNode) node;
            if (classDecl.members != null) {
                for (MemberNode member : classDecl.members) {
                    collectUsedIdentifiers(member);
                }
            }
        } else if (node instanceof MethodDeclNode) {
            MethodDeclNode methodDecl = (MethodDeclNode) node;
            if (methodDecl.body != null) {
                collectUsedIdentifiers(methodDecl.body);
            }
        } else if (node instanceof ConstructorDeclNode) {
            ConstructorDeclNode constructor = (ConstructorDeclNode) node;
            if (constructor.body != null) {
                collectUsedIdentifiers(constructor.body);
            }
        } else if (node instanceof MethodBodyNode) {
            MethodBodyNode body = (MethodBodyNode) node;
            if (body.isArrow && body.arrowExpression != null) {
                collectUsedIdentifiers(body.arrowExpression);
            } else if (!body.isArrow && body.body != null) {
                collectUsedIdentifiers(body.body);
            }
        } else if (node instanceof BodyNode) {
            BodyNode body = (BodyNode) node;
            if (body.elements != null) {
                for (BodyElementNode element : body.elements) {
                    if (element instanceof ASTNode) {
                        collectUsedIdentifiers((ASTNode) element);
                    }
                }
            }
        } else if (node instanceof AssignmentNode) {
            AssignmentNode assignment = (AssignmentNode) node;
            if (assignment.left != null)
                collectUsedIdentifiers(assignment.left);
            if (assignment.right != null)
                collectUsedIdentifiers(assignment.right);
        } else if (node instanceof WhileLoopNode) {
            WhileLoopNode whileLoop = (WhileLoopNode) node;
            if (whileLoop.condition != null)
                collectUsedIdentifiers(whileLoop.condition);
            if (whileLoop.body != null)
                collectUsedIdentifiers(whileLoop.body);
        } else if (node instanceof IfStatementNode) {
            IfStatementNode ifStmt = (IfStatementNode) node;
            if (ifStmt.condition != null)
                collectUsedIdentifiers(ifStmt.condition);
            if (ifStmt.thenBody != null)
                collectUsedIdentifiers(ifStmt.thenBody);
            if (ifStmt.elseBody != null)
                collectUsedIdentifiers(ifStmt.elseBody);
        } else if (node instanceof ReturnNode) {
            ReturnNode returnNode = (ReturnNode) node;
            if (returnNode.expression != null)
                collectUsedIdentifiers(returnNode.expression);
        } else if (node instanceof PrintNode) {
            PrintNode printNode = (PrintNode) node;
            if (printNode.expression != null)
                collectUsedIdentifiers(printNode.expression);
        } else if (node instanceof MemberAccessNode) {
            MemberAccessNode memberAccess = (MemberAccessNode) node;
            if (memberAccess.target != null)
                collectUsedIdentifiers(memberAccess.target);
            if (memberAccess.member instanceof ASTNode) {
                collectUsedIdentifiers((ASTNode) memberAccess.member);
            }
        } else if (node instanceof MethodInvocationNode) {
            MethodInvocationNode methodCall = (MethodInvocationNode) node;
            if (methodCall.target != null)
                collectUsedIdentifiers(methodCall.target);
            if (methodCall.arguments != null) {
                for (ExpressionNode arg : methodCall.arguments) {
                    collectUsedIdentifiers(arg);
                }
            }
        } else if (node instanceof ConstructorInvocationNode) {
            ConstructorInvocationNode constructor = (ConstructorInvocationNode) node;
            if (constructor.arguments != null) {
                for (ExpressionNode arg : constructor.arguments) {
                    collectUsedIdentifiers(arg);
                }
            }
        } else if (node instanceof VarDeclNode) {
            VarDeclNode varDecl = (VarDeclNode) node;
            if (varDecl.initializer != null) {
                collectUsedIdentifiers(varDecl.initializer);
            }
        }
    }

    /**
     * Phase 2: Remove unused variable declarations
     */
    private void removeUnusedVarDeclarations(ASTNode node) {
        if (node instanceof ProgramNode) {
            ProgramNode program = (ProgramNode) node;
            if (program.classes != null) {
                for (ClassDeclNode classDecl : program.classes) {
                    removeUnusedVarDeclarations(classDecl);
                }
            }
        } else if (node instanceof ClassDeclNode) {
            ClassDeclNode classDecl = (ClassDeclNode) node;
            if (classDecl.members != null) {
                for (MemberNode member : classDecl.members) {
                    removeUnusedVarDeclarations(member);
                }
            }
        } else if (node instanceof MethodDeclNode) {
            MethodDeclNode methodDecl = (MethodDeclNode) node;
            if (methodDecl.body != null) {
                removeUnusedVarDeclarations(methodDecl.body);
            }
        } else if (node instanceof ConstructorDeclNode) {
            ConstructorDeclNode constructor = (ConstructorDeclNode) node;
            if (constructor.body != null) {
                removeUnusedVarDeclarations(constructor.body);
            }
        } else if (node instanceof MethodBodyNode) {
            MethodBodyNode body = (MethodBodyNode) node;
            if (!body.isArrow && body.body != null) {
                removeUnusedVarDeclarations(body.body);
            }
        } else if (node instanceof BodyNode) {
            BodyNode body = (BodyNode) node;
            if (body.elements != null) {
                List<BodyElementNode> elementsToRemove = new ArrayList<>();

                for (BodyElementNode element : body.elements) {
                    if (element instanceof VarDeclNode) {
                        VarDeclNode varDecl = (VarDeclNode) element;
                        if (!usedVariables.contains(varDecl.varName)) {
                            elementsToRemove.add(element);
                        }
                    }
                }

                body.elements.removeAll(elementsToRemove);

                // Рекурсивно обрабатываем оставшиеся элементы
                for (BodyElementNode element : body.elements) {
                    if (element instanceof ASTNode) {
                        removeUnusedVarDeclarations((ASTNode) element);
                    }
                }
            }
        } else if (node instanceof IfStatementNode) {
            IfStatementNode ifStmt = (IfStatementNode) node;
            if (ifStmt.thenBody != null)
                removeUnusedVarDeclarations(ifStmt.thenBody);
            if (ifStmt.elseBody != null)
                removeUnusedVarDeclarations(ifStmt.elseBody);
        } else if (node instanceof WhileLoopNode) {
            WhileLoopNode whileLoop = (WhileLoopNode) node;
            if (whileLoop.body != null)
                removeUnusedVarDeclarations(whileLoop.body);
        }
    }

    public ProgramNode optimize(ProgramNode program) {
        if (enableConstantFolding) {
            constantFolding(program);
        }
        if (enableUnreachableCodeElimination) {
            eliminateUnreachableCode(program);
        }
        if (enableRemoveUnusedVariables) {
            removeUnusedVariables(program);
        }
        return (ProgramNode) program.accept(this);
    }

    // ====== PROGRAM AND CLASSES ======

    @Override
    public ASTNode visit(ProgramNode n) {
        List<ClassDeclNode> out = new ArrayList<>();
        for (var c : n.classes) {
            out.add((ClassDeclNode) c.accept(this));
        }
        return new ProgramNode(out);
    }

    @Override
    public ASTNode visit(ClassDeclNode n) {
        List<MemberNode> out = new ArrayList<>();
        for (MemberNode m : n.members) {
            out.add((MemberNode) m.accept(this));
        }
        return new ClassDeclNode(n.className, n.extendsClass, out);
    }

    // ====== METHODS, CONSTRUCTORS ======

    @Override
    public ASTNode visit(MethodDeclNode n) {
        MethodHeaderNode header = (MethodHeaderNode) n.header.accept(this);
        MethodBodyNode body = (MethodBodyNode) n.body.accept(this);
        return new MethodDeclNode(header, body);
    }

    @Override
    public ASTNode visit(MethodHeaderNode n) {
        List<ParamDeclNode> params = new ArrayList<>();
        for (ParamDeclNode p : n.parameters) {
            params.add((ParamDeclNode) p.accept(this));
        }
        return new MethodHeaderNode(n.methodName, params, n.returnType);
    }

    @Override
    public ASTNode visit(MethodBodyNode n) {
        if (n.isArrow) {
            return new MethodBodyNode(
                    (ExpressionNode) n.arrowExpression.accept(this),
                    true);
        }
        return new MethodBodyNode(
                (BodyNode) n.body.accept(this),
                false);
    }

    @Override
    public ASTNode visit(ConstructorDeclNode n) {
        List<ParamDeclNode> params = new ArrayList<>();
        for (ParamDeclNode p : n.parameters) {
            params.add((ParamDeclNode) p.accept(this));
        }
        BodyNode body = (BodyNode) n.body.accept(this);
        return new ConstructorDeclNode(params, body);
    }

    @Override
    public ASTNode visit(ParamDeclNode n) {
        return new ParamDeclNode(
                n.paramName,
                (ASTNode) n.paramType.accept(this));
    }

    // ====== BODY AND STATEMENTS ======

    @Override
    public ASTNode visit(BodyNode n) {
        List<BodyElementNode> out = new ArrayList<>();
        boolean reachable = true;

        for (BodyElementNode el : n.elements) {
            if (!reachable)
                continue;

            ASTNode rewritten = ((ASTNode) el).accept(this);

            if (rewritten instanceof BodyElementNode) {
                out.add((BodyElementNode) rewritten);
            } else {
                if (rewritten instanceof BodyNode) {
                    BodyNode bodyResult = (BodyNode) rewritten;
                    out.addAll(bodyResult.elements);
                }
            }

            if (el instanceof ReturnNode)
                reachable = false;
        }

        return new BodyNode(out);
    }

    @Override
    public ASTNode visit(VarDeclNode n) {
        ExpressionNode init = n.initializer == null
                ? null
                : (ExpressionNode) n.initializer.accept(this);

        return new VarDeclNode(n.varName, n.type, init, n.declType);
    }

    @Override
    public ASTNode visit(AssignmentNode n) {
        return new AssignmentNode(
                (ExpressionNode) n.left.accept(this),
                (ExpressionNode) n.right.accept(this));
    }

    @Override
    public ASTNode visit(IfStatementNode n) {
        ExpressionNode cond = (ExpressionNode) n.condition.accept(this);

        // constant folding for if
        if (cond instanceof BoolLiteralNode b) {
            if (b.value) {
                return n.thenBody.accept(this);
            } else {
                return n.elseBody != null
                        ? n.elseBody.accept(this)
                        : new BodyNode(new ArrayList<>());
            }
        }

        BodyNode thenB = (BodyNode) n.thenBody.accept(this);
        BodyNode elseB = n.elseBody != null ? (BodyNode) n.elseBody.accept(this) : null;

        return new IfStatementNode(cond, thenB, elseB);
    }

    @Override
    public ASTNode visit(WhileLoopNode n) {
        ExpressionNode cond = (ExpressionNode) n.condition.accept(this);

        // if `while false` → remove
        if (cond instanceof BoolLiteralNode b && !b.value) {
            return new BodyNode(new ArrayList<>()); // пустой блок
        }

        BodyNode body = (BodyNode) n.body.accept(this);
        return new WhileLoopNode(cond, body);
    }

    @Override
    public ASTNode visit(ReturnNode n) {
        return new ReturnNode(n.expression == null ? null : (ExpressionNode) n.expression.accept(this));
    }

    @Override
    public ASTNode visit(PrintNode n) {
        return new PrintNode(
                (ExpressionNode) n.expression.accept(this));
    }

    // ====== EXPRESSIONS ======

    @Override
    public ASTNode visit(ConstructorInvocationNode n) {
        List<ExpressionNode> args = new ArrayList<>();
        for (ExpressionNode e : n.arguments) {
            args.add((ExpressionNode) e.accept(this));
        }
        return new ConstructorInvocationNode(n.className, args);
    }

    @Override
    public ASTNode visit(MethodInvocationNode n) {
        ExpressionNode target = (ExpressionNode) n.target.accept(this);
        List<ExpressionNode> args = new ArrayList<>();
        for (ExpressionNode e : n.arguments) {
            args.add((ExpressionNode) e.accept(this));
        }
        return new MethodInvocationNode(target, n.methodName, args);
    }

    @Override
    public ASTNode visit(MemberAccessNode n) {
        ExpressionNode target = (ExpressionNode) n.target.accept(this);
        ExpressionNode member = (ExpressionNode) n.member.accept(this);
        return new MemberAccessNode(target, member);
    }

    @Override
    public ASTNode visit(IdentifierNode n) {
        return n;
    }

    @Override
    public ASTNode visit(ThisNode n) {
        return n;
    }

    @Override
    public ASTNode visit(IntLiteralNode n) {
        return n;
    }

    @Override
    public ASTNode visit(RealLiteralNode n) {
        return n;
    }

    @Override
    public ASTNode visit(BoolLiteralNode n) {
        return n;
    }

    @Override
    public ASTNode visit(TypeNode n) {
        return n;
    }

    @Override
    public ASTNode visit(GenericTypeNode n) {
        return n;
    }

}
