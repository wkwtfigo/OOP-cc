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

        // Рекурсивно обрабатываем подвыражения
        if (expr instanceof ConstructorInvocationNode) {
            ConstructorInvocationNode cons = (ConstructorInvocationNode) expr;
            if (cons.arguments != null) {
                for (int i = 0; i < cons.arguments.size(); i++) {
                    cons.arguments.set(i, foldConstantExpression(cons.arguments.get(i)));
                }
            }
            if (cons.arguments.size() == 1) {
                ExpressionNode arg = cons.arguments.get(0);
                if (arg instanceof IntLiteralNode && "Integer".equals(cons.className)) {
                    return new IntLiteralNode(((IntLiteralNode) arg).value);
                } else if (arg instanceof RealLiteralNode && "Double".equals(cons.className)) {
                    return new RealLiteralNode(((RealLiteralNode) arg).value);
                } else if (arg instanceof BoolLiteralNode && "Boolean".equals(cons.className)) {
                    return new BoolLiteralNode(((BoolLiteralNode) arg).value);
                }
            }
        } else if (expr instanceof MemberAccessNode) {
            MemberAccessNode ma = (MemberAccessNode) expr;
            ma.target = foldConstantExpression(ma.target);
            if (ma.member instanceof ExpressionNode) {
                ma.member = foldConstantExpression((ExpressionNode) ma.member);
            }

            if ((ma.target instanceof IntLiteralNode ||
                    ma.target instanceof RealLiteralNode ||
                    ma.target instanceof BoolLiteralNode) &&
                    ma.member instanceof MethodInvocationNode) {

                MethodInvocationNode mi = (MethodInvocationNode) ma.member;

                if (ma.target instanceof IntLiteralNode)
                    mi.target = new IntLiteralNode(((IntLiteralNode) ma.target).value);
                else if (ma.target instanceof RealLiteralNode)
                    mi.target = new RealLiteralNode(((RealLiteralNode) ma.target).value);
                else if (ma.target instanceof BoolLiteralNode)
                    mi.target = new BoolLiteralNode(((BoolLiteralNode) ma.target).value);

                return foldConstantExpression(mi);
            }
        } else if (expr instanceof MethodInvocationNode) {
            MethodInvocationNode mi = (MethodInvocationNode) expr;
            mi.target = foldConstantExpression(mi.target);
            if (mi.arguments != null) {
                for (int i = 0; i < mi.arguments.size(); i++) {
                    mi.arguments.set(i, foldConstantExpression(mi.arguments.get(i)));
                }
            }

            Object val = evaluateConstantExpression(mi);
            if (val instanceof Integer)
                return new IntLiteralNode((Integer) val);
            if (val instanceof Double)
                return new RealLiteralNode((Double) val);
            if (val instanceof Boolean)
                return new BoolLiteralNode((Boolean) val);
        } else if (expr instanceof IntLiteralNode || expr instanceof RealLiteralNode
                || expr instanceof BoolLiteralNode) {
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

        if (expr instanceof ConstructorInvocationNode) {
            ConstructorInvocationNode constructor = (ConstructorInvocationNode) expr;
            if (constructor.arguments != null) {
                return constructor.arguments.stream().allMatch(this::isConstantExpression);
            }
        }

        if (expr instanceof MemberAccessNode) {
            MemberAccessNode memberAccess = (MemberAccessNode) expr;
            return isConstantExpression(memberAccess.target)
                    && (memberAccess.member instanceof ExpressionNode
                            ? isConstantExpression((ExpressionNode) memberAccess.member)
                            : true);
        }

        if (expr instanceof MethodInvocationNode) {
            MethodInvocationNode methodCall = (MethodInvocationNode) expr;
            ExpressionNode foldedTarget = methodCall.target;
            if (!isConstantExpression(foldedTarget))
                return false;
            if (methodCall.arguments != null) {
                for (ExpressionNode arg : methodCall.arguments) {
                    if (!isConstantExpression(arg))
                        return false;
                }
            }
            return true;
        }

        return false;
    }

    /**
     * Evaluate constant expression at compile time
     */
    private Object evaluateConstantExpression(ExpressionNode expr) {
        if (expr instanceof IntLiteralNode)
            return ((IntLiteralNode) expr).value;

        if (expr instanceof RealLiteralNode)
            return ((RealLiteralNode) expr).value;

        if (expr instanceof BoolLiteralNode)
            return ((BoolLiteralNode) expr).value;

        if (expr instanceof MethodInvocationNode) {
            MethodInvocationNode mi = (MethodInvocationNode) expr;
            Object targetVal = evaluateConstantExpression(mi.target);
            Object argVal = mi.arguments.isEmpty() ? null : evaluateConstantExpression(mi.arguments.get(0));

            if (targetVal instanceof Integer) {
                int intVal = (Integer) targetVal;
                if (argVal instanceof Integer) {
                    int a = (Integer) argVal;
                    switch (mi.methodName) {
                        case "Plus":
                            return intVal + a;
                        case "Minus":
                            return intVal - a;
                        case "Mult":
                            return intVal * a;
                        case "Div":
                            return a != 0 ? intVal / a : 0;
                        case "Rem":
                            return a != 0 ? intVal % a : 0;
                        case "Less":
                            return intVal < a;
                        case "LessEqual":
                            return intVal <= a;
                        case "Greater":
                            return intVal > a;
                        case "GreaterEqual":
                            return intVal >= a;
                        case "Equal":
                            return intVal == a;
                    }
                } else if (argVal instanceof Double) {
                    double t = (double) intVal;
                    double a = (Double) argVal;
                    switch (mi.methodName) {
                        case "Plus":
                            return t + a;
                        case "Minus":
                            return t - a;
                        case "Mult":
                            return t * a;
                        case "Div":
                            return t / a;
                        case "Less":
                            return t < a;
                        case "LessEqual":
                            return t <= a;
                        case "Greater":
                            return t > a;
                        case "GreaterEqual":
                            return t >= a;
                        case "Equal":
                            return t == a;
                    }
                }
            }

            // Real operations
            if (targetVal instanceof Double) {
                double t = (Double) targetVal;
                double a = argVal instanceof Double ? (Double) argVal
                        : (argVal instanceof Integer ? (Integer) argVal : 0.0);
                switch (mi.methodName) {
                    case "Plus":
                        return t + a;
                    case "Minus":
                        return t - a;
                    case "Mult":
                        return t * a;
                    case "Div":
                        return a != 0.0 ? t / a : 0.0;
                    case "Less":
                        return t < a;
                    case "LessEqual":
                        return t <= a;
                    case "Greater":
                        return t > a;
                    case "GreaterEqual":
                        return t >= a;
                    case "Equal":
                        return t == a;
                }
            }

            // Boolean operations
            if (targetVal instanceof Boolean) {
                boolean t = (Boolean) targetVal;
                boolean a = argVal instanceof Boolean ? (Boolean) argVal : false;
                switch (mi.methodName) {
                    case "And":
                        return t && a;
                    case "Or":
                        return t || a;
                    case "Xor":
                        return t ^ a;
                    case "Equal":
                        return t == a;
                }
            }

            // Unary operators
            if (mi.methodName.equals("UnaryMinus") && targetVal instanceof Integer) {
                return -((Integer) targetVal);
            }
            if (mi.methodName.equals("UnaryMinus") && targetVal instanceof Double) {
                return -((Double) targetVal);
            }
            if (mi.methodName.equals("Not") && targetVal instanceof Boolean) {
                return !((Boolean) targetVal);
            }

            // Conversions
            if (mi.methodName.equals("toReal") && targetVal instanceof Integer) {
                return ((Integer) targetVal).doubleValue();
            }
            if (mi.methodName.equals("toInteger") && targetVal instanceof Double) {
                return ((Double) targetVal).intValue();
            }
            if (mi.methodName.equals("toBoolean") && targetVal instanceof Integer) {
                return ((Integer) targetVal) != 0;
            }
            if (mi.methodName.equals("toInteger") && targetVal instanceof Boolean) {
                return ((Boolean) targetVal) ? 1 : 0;
            }

        }
        return null;
    }

    /**
     * Combined Optimization: Unreachable Code Elimination + Remove Unused Variables
     * Выполняет обе оптимизации за один проход по AST
     */
    private void combinedOptimization(ASTNode node) {
        collectUsedIdentifiers(node);
        eliminateUnreachableCodeWithVarCleanup(node);
    }

    private void eliminateUnreachableCodeWithVarCleanup(ASTNode node) {
        if (node instanceof ProgramNode) {
            ProgramNode program = (ProgramNode) node;
            if (program.classes != null) {
                for (ClassDeclNode classDecl : program.classes) {
                    eliminateUnreachableCodeWithVarCleanup(classDecl);
                }
            }
        } else if (node instanceof ClassDeclNode) {
            ClassDeclNode classDecl = (ClassDeclNode) node;
            if (classDecl.members != null) {
                for (MemberNode member : classDecl.members) {
                    eliminateUnreachableCodeWithVarCleanup(member);
                }
            }
        } else if (node instanceof MethodDeclNode) {
            MethodDeclNode methodDecl = (MethodDeclNode) node;
            inReturnContext = false;
            if (methodDecl.body != null) {
                eliminateUnreachableCodeWithVarCleanup(methodDecl.body);
            }
        } else if (node instanceof ConstructorDeclNode) {
            ConstructorDeclNode constructor = (ConstructorDeclNode) node;
            inReturnContext = false;
            if (constructor.body != null) {
                eliminateUnreachableCodeWithVarCleanup(constructor.body);
            }
        } else if (node instanceof MethodBodyNode) {
            MethodBodyNode body = (MethodBodyNode) node;
            if (body.isArrow) {
                inReturnContext = true;
            } else if (body.body != null) {
                eliminateUnreachableCodeWithVarCleanup(body.body);
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

                    // Пропускаем неиспользуемые объявления переменных
                    if (enableRemoveUnusedVariables && element instanceof VarDeclNode) {
                        VarDeclNode varDecl = (VarDeclNode) element;
                        if (!usedVariables.contains(varDecl.varName)) {
                            if (!hasSideEffects(varDecl.initializer)) {
                                continue; // безопасно удалить
                            }
                        }
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
                                            for (BodyElementNode elseElement : ifStmt.elseBody.elements) {
                                                if (!(enableRemoveUnusedVariables &&
                                                        elseElement instanceof VarDeclNode &&
                                                        !usedVariables.contains(((VarDeclNode) elseElement).varName))) {
                                                    newElements.add(elseElement);
                                                }
                                            }
                                        }
                                    } else {
                                        newElements.remove(newElements.size() - 1); // Удаляем if полностью
                                    }
                                }
                            }
                        }
                    } else if (element instanceof WhileLoopNode) {
                        WhileLoopNode whileLoop = (WhileLoopNode) element;
                        if (isConstantExpression(whileLoop.condition)) {
                            Object conditionValue = evaluateConstantExpression(whileLoop.condition);
                            if (conditionValue instanceof Boolean && !(Boolean) conditionValue) {
                                newElements.remove(newElements.size() - 1);
                            } else if (conditionValue instanceof Boolean && (Boolean) conditionValue) {
                                continue;
                            }
                        }
                    }
                }
                body.elements = newElements;

                for (BodyElementNode element : newElements) {
                    if (element instanceof WhileLoopNode) {
                        eliminateUnreachableCodeWithVarCleanup(((WhileLoopNode) element).body);
                    } else if (element instanceof IfStatementNode) {
                        IfStatementNode ifStmt = (IfStatementNode) element;
                        eliminateUnreachableCodeWithVarCleanup(ifStmt.thenBody);
                        if (ifStmt.elseBody != null) {
                            eliminateUnreachableCodeWithVarCleanup(ifStmt.elseBody);
                        }
                    }
                }
            }
        } else if (node instanceof IfStatementNode) {
            IfStatementNode ifStmt = (IfStatementNode) node;
            if (ifStmt.thenBody != null)
                eliminateUnreachableCodeWithVarCleanup(ifStmt.thenBody);
            if (ifStmt.elseBody != null)
                eliminateUnreachableCodeWithVarCleanup(ifStmt.elseBody);
        } else if (node instanceof WhileLoopNode) {
            WhileLoopNode whileLoop = (WhileLoopNode) node;
            if (whileLoop.body != null)
                eliminateUnreachableCodeWithVarCleanup(whileLoop.body);
        }
    }

    // Метод collectUsedIdentifiers остается без изменений
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

    private boolean hasSideEffects(ASTNode node) {
        if (node == null)
            return false;

        // --- Literals and identifiers ---
        if (node instanceof IntLiteralNode)
            return false;
        if (node instanceof RealLiteralNode)
            return false;
        if (node instanceof BoolLiteralNode)
            return false;
        if (node instanceof IdentifierNode)
            return false;

        // --- Print has side effects ---
        if (node instanceof PrintNode)
            return true;

        // --- Assignments always have side effects ---
        if (node instanceof AssignmentNode)
            return true;

        // --- Member access: check nested nodes ---
        if (node instanceof MemberAccessNode) {
            MemberAccessNode ma = (MemberAccessNode) node;
            return hasSideEffects(ma.target)
                    || (ma.member instanceof ASTNode && hasSideEffects((ASTNode) ma.member));
        }

        // --- Method invocation ---
        if (node instanceof MethodInvocationNode) {
            MethodInvocationNode mi = (MethodInvocationNode) node;

            // numeric/boolean operations are pure
            if (isPureMethod(mi.methodName)) {
                // but evaluate args anyway (they may contain side effects!)
                if (hasSideEffects(mi.target))
                    return true;
                for (ExpressionNode arg : mi.arguments) {
                    if (hasSideEffects(arg))
                        return true;
                }
                return false; // pure
            }

            // other methods → assumed to have side effects
            return true;
        }

        // --- Constructor invocation ---
        if (node instanceof ConstructorInvocationNode) {
            ConstructorInvocationNode ci = (ConstructorInvocationNode) node;

            // For safety: always assume constructors have side effects
            for (ExpressionNode arg : ci.arguments) {
                if (hasSideEffects(arg))
                    return true;
            }
            return true;
        }

        // --- If and While ---
        if (node instanceof IfStatementNode) {
            IfStatementNode ifs = (IfStatementNode) node;
            return hasSideEffects(ifs.condition)
                    || hasSideEffects(ifs.thenBody)
                    || (ifs.elseBody != null && hasSideEffects(ifs.elseBody));
        }

        if (node instanceof WhileLoopNode) {
            WhileLoopNode w = (WhileLoopNode) node;
            return true; // loops always considered side-effects
        }

        // --- Bodies ---
        if (node instanceof BodyNode) {
            BodyNode b = (BodyNode) node;
            for (BodyElementNode elem : b.elements) {
                if (elem instanceof ASTNode && hasSideEffects((ASTNode) elem))
                    return true;
            }
            return false;
        }

        if (node instanceof MethodBodyNode) {
            MethodBodyNode mb = (MethodBodyNode) node;
            if (mb.isArrow)
                return hasSideEffects(mb.arrowExpression);
            return hasSideEffects(mb.body);
        }

        // default
        return true; // be conservative
    }

    // Pure built-in methods
    private boolean isPureMethod(String name) {
        return name.equals("Plus")
                || name.equals("Minus")
                || name.equals("Mult")
                || name.equals("Div")
                || name.equals("Or")
                || name.equals("And")
                || name.equals("Not");
    }

    public ProgramNode optimize(ProgramNode program) {
        if (enableConstantFolding) {
            constantFolding(program);
        }
        if (enableUnreachableCodeElimination || enableRemoveUnusedVariables) {
            combinedOptimization(program);
        }
        return (ProgramNode) program.accept(this);
    }

    // ====== PROGRAM AND CLASSES ======

    @Override
    public ASTNode visit(ProgramNode n) {
        if (n.classes == null) {
            return new ProgramNode(new ArrayList<>());
        }

        List<ClassDeclNode> out = new ArrayList<>();
        for (ClassDeclNode c : n.classes) {
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
