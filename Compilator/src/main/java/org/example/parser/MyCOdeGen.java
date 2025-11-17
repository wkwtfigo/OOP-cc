package org.example.parser;

/* 
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.TypeInfo;

public class MyCodeGen implements ASTVisitor{

    // Собираемый Jasmin-код для текущего класса
    private StringBuilder jasminCode;

    // Текущее имя класса, для удобства генерации
    private String currentClassName;

    // Папка, куда будут записываться .j файлы
    private final String outputDir;
    private int labelCounter = 0;

    // Информация обо всех классах в программе
    private final Map<String, ClassInfo> classInfoMap = new HashMap<>();
    // AST-узлы классов, чтобы потом к ним вернуться при генерации
    private final Map<String, ClassDeclNode> classAstNodes = new HashMap<>();

    // -------------------------
    // Внутренние структуры для информации о классах
    // -------------------------
    private static class ClassInfo {
        final String name;
        final String superName; // internal name: java/lang/Object и т.п.
        final Map<String, FieldInfo> fields = new HashMap<>();
        final Map<String, MethodDeclNode> methods = new HashMap<>();
        final java.util.List<ConstructorDeclNode> constructors = new java.util.ArrayList<>();
        final java.util.List<ConstructorDeclNode> constructorsList = new java.util.ArrayList<>();

        ClassInfo(String name, String superName) {
            this.name = name;
            this.superName = superName;
        }
    }

    private static class FieldInfo {
        final String name;
        final String descriptor;

        FieldInfo(String name, String descriptor) {
            this.name = name;
            this.descriptor = descriptor;
        }
    }

    private String newLabel(String base) {
        return base + "_" + (labelCounter++);
    }

    public MyCodeGen() {
        this("out");
    }

    public MyCodeGen(String outputDir) {
        this.outputDir = outputDir;
    }

    private void emit(String line) {
        jasminCode.append(line).append("\n");
    }

    private void saveFile(String className) {
        try {
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File out = new File(dir, className + ".j");
            try (FileWriter w = new FileWriter(out)) {
                w.write(jasminCode != null ? jasminCode.toString() : "");
            }
            System.out.println("Generated: " + out.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing Jasmin file: " + e.getMessage());
        }
    }

    @Override
    public void visit(ProgramNode node) {
        if (node == null || node.classes == null) {
            return;
        }

        // 1-й проход: регистрируем классы
        for (ClassDeclNode classDecl : node.classes) {
            registerClass(classDecl);
        }

        // 2-й проход: генерируем код по каждому классу
        for (ClassDeclNode classDecl : node.classes) {
            generateClass(classDecl);
        }
    }

    // Первый проход: просто собираем информацию о классах
    private void registerClass(ClassDeclNode node) {
        if (node == null) return;

        ClassInfo info = new ClassInfo(node.className,
                                    node.extendsClass == null ? "java/lang/Object"
                                                                : node.extendsClass.replace('.', '/'));
        classInfoMap.put(node.className, info);
        classAstNodes.put(node.className, node);

        if (node.members == null) return;

        for (MemberNode member : node.members) {
            if (member instanceof VarDeclNode v) {
                // поле
                String desc = descriptorForTypeNode(v.type);
                info.fields.put(v.varName, new FieldInfo(v.varName, desc));
            } else if (member instanceof MethodDeclNode m) {
                info.methods.put(m.header.methodName, m);
            } else if (member instanceof ConstructorDeclNode c) {
                info.constructors.add(c);
            }
        }
    }

    // Второй проход: генерация Jasmin по классу
    private void generateClass(ClassDeclNode node) {
        if (node == null) return;

        currentClassName = node.className;
        jasminCode = new StringBuilder();

        ClassInfo info = classInfoMap.get(node.className);

        // Заголовок класса
        emit(".class public " + node.className);
        String superInternal = (info != null ? info.superName : "java/lang/Object");
        emit(".super " + superInternal);
        emit("");

        // Поля
        if (info != null) {
            for (FieldInfo f : info.fields.values()) {
                emit(".field public " + f.name + " " + f.descriptor);
            }
        }
        emit("");

        // Конструктор(ы)
        if (info != null && !info.constructors.isEmpty()) {
            for (ConstructorDeclNode ctor : info.constructors) {
                generateConstructor(ctor, superInternal);
            }
        } else {
            // Если конструкторов нет — генерируем дефолтный с инициализацией полей
            generateDefaultConstructorWithInitializers(node, superInternal);
        }

        // Методы
        if (info != null) {
            for (MethodDeclNode m : info.methods.values()) {
                m.accept(this); // пока используем твой visit(MethodDeclNode)
            }
        }

        saveFile(node.className);
    }

    @Override
    public void visit(ClassDeclNode node) {
        // Классы обрабатываем через visit(ProgramNode) → registerClass/generateClass
    }

    private void generateDefaultConstructorWithInitializers(ClassDeclNode node) {
        String superName = (node.extendsClass == null || node.extendsClass.isEmpty())
                ? "java/lang/Object"
                : node.extendsClass.replace('.', '/');
        generateDefaultConstructorWithInitializers(node, superName);
    }

    private void generateDefaultConstructor() {
        ConstructorSignature signature = new ConstructorSignature(Collections.emptyList(), false);
        emit(".method public <init>()V");
        emitLimits();
        MethodContext ctx = new MethodContext(currentClassInfo, "<init>", VOID_TYPE, true);
        methodStack.push(ctx);
        localVarScopes.push(new HashMap<>());

        emit("    aload_0");
        emit("    invokespecial " + internalName(currentClassInfo.superName) + "/<init>()V");
        emitFieldInitializers();
        emit("    return");

        localVarScopes.pop();
        methodStack.pop();
        emit(".end method");
        emit("");
    }

    private void generateConstructor(ConstructorDeclNode constructor) {
        List<TypeInfo> paramTypes = resolveParameterTypes(constructor.parameters);
        String descriptor = buildDescriptor(paramTypes, VOID_TYPE);
        emit(".method public <init>" + descriptor);
        emitLimits();

        MethodContext ctx = new MethodContext(currentClassInfo, "<init>", VOID_TYPE, true);
        methodStack.push(ctx);
        localVarScopes.push(new HashMap<>());
        registerParameters(constructor.parameters, paramTypes, ctx);

        emit("    aload_0");
        emit("    invokespecial " + internalName(currentClassInfo.superName) + "/<init>()V");
        emitFieldInitializers();

        if (constructor.body != null) {
            constructor.body.accept(this);
        }
        emit("    return");

        localVarScopes.pop();
        methodStack.pop();
        emit(".end method");
        emit("");
    }

    private void generateMethod(MethodDeclNode methodDecl) {
        List<TypeInfo> paramTypes = resolveParameterTypes(methodDecl.header.parameters);
        TypeInfo returnType = resolveReturnType(methodDecl.header.returnType);
        String descriptor = buildDescriptor(paramTypes, returnType);
        emit(".method public " + methodDecl.header.methodName + descriptor);
        emitLimits();

        MethodContext ctx = new MethodContext(currentClassInfo, methodDecl.header.methodName, returnType, false);
        methodStack.push(ctx);
        localVarScopes.push(new HashMap<>());
        registerParameters(methodDecl.header.parameters, paramTypes, ctx);

        if (methodDecl.body != null) {
            if (methodDecl.body.isArrow && methodDecl.body.arrowExpression != null) {
                TypeInfo exprType = generateExpression(methodDecl.body.arrowExpression);
                emitReturn(exprType, ctx.returnType);
            } else if (!methodDecl.body.isArrow && methodDecl.body.body != null) {
                methodDecl.body.body.accept(this);
                if (ctx.returnType.isVoid()) {
                    emit("    return");
                } else {
                    emitDefaultReturn(ctx.returnType);
                }
            }
        } else if (ctx.returnType.isVoid()) {
            emit("    return");
        } else {
            emitDefaultReturn(ctx.returnType);
        }
        
        localVarScopes.pop();
        methodStack.pop();
        emit(".end method");
        emit("");
    }

    rivate void maybeGenerateProgramEntry(List<MethodDeclNode> methods) {
        boolean hasStart = methods.stream()
                .anyMatch(m -> "start".equals(m.header.methodName)
                        && (m.header.parameters == null || m.header.parameters.isEmpty())
                        && (m.header.returnType == null));
        if (!"Main".equals(currentClassName) || !hasStart) {
            return;
        }
        emit(".method public static main([Ljava/lang/String;)V");
        emit("    .limit stack 4");
        emit("    .limit locals 1");
        emit("    new " + currentClassName);
        emit("    dup");
        emit("    invokespecial " + currentClassName + "/<init>()V");
        emit("    invokevirtual " + currentClassName + "/start()V");
        emit("    return");
        emit(".end method");
        emit("");
    }

    private void generateDefaultConstructorWithInitializers(ClassDeclNode node, String superInternal) {
        emit(".method public <init>()V");
        emit("    .limit stack 16");
        emit("    .limit locals 1");

        emit("    aload_0");
        emit("    invokespecial " + superInternal + "/<init>()V");

        if (node.members != null) {
            for (MemberNode member : node.members) {
                if (!(member instanceof VarDeclNode v)) continue;
                if (v.initializer == null) continue;

                emit("    aload_0");
                generateExpression(v.initializer);
                String ownerInternal = currentClassName.replace('.', '/');
                String desc = descriptorForTypeNode(v.type);
                emit("    putfield " + ownerInternal + "/" + v.varName + " " + desc);
            }
        }

        emit("    return");
        emit(".end method");
        emit("");
    }

    // -------------------------
    // Expressions
    // -------------------------
    private void generateExpression(ExpressionNode expr) {
        if (expr == null) {
            emit("    aconst_null");
            return;
        }
        if (expr instanceof IntLiteralNode) {
            int val = ((IntLiteralNode) expr).value;
            if (val >= -1 && val <= 5) {
                emit("    iconst_" + (val == -1 ? "m1" : val));
            } else if (val >= -128 && val <= 127) {
                emit("    bipush " + val);
            } else if (val >= -32768 && val <= 32767) {
                emit("    sipush " + val);
            } else {
                emit("    ldc " + val);
            }
            emitBoxInteger();
            return;
        }
        if (expr instanceof RealLiteralNode) {
            double val = ((RealLiteralNode) expr).value;
            if (val == 0d) {
                emit("    dconst_0");
            } else if (val == 1d) {
                emit("    dconst_1");
            } else {
                emit("    ldc2_w " + val);
            }
            emitBoxDouble();
            return;
        }
        if (expr instanceof BoolLiteralNode) {
            boolean b = ((BoolLiteralNode) expr).value;
            emit("    iconst_" + (b ? "1" : "0"));
            emitBoxBoolean();
            return;
        }
        if (expr instanceof ThisNode) {
            emit("    aload_0");
            return;
        }
        if (expr instanceof IdentifierNode) {
            String name = ((IdentifierNode) expr).name;
            if (localVars != null && localVars.containsKey(name)) {
                int idx = localVars.get(name);
                emitLoadVar(idx);
                return;
            } else {
                // field access on this
                emit("    aload_0");
                String owner = currentClassName.replace('.', '/');
                String desc = "Ljava/lang/Object;"; // could improve by remembering field types
                emit("    getfield " + owner + "/" + name + " " + desc);
                return;
            }
        }
        if (expr instanceof MemberAccessNode) {
            MemberAccessNode access = (MemberAccessNode) expr;
            // read field on target: generate target (objectref) then getfield
            generateExpression(access.target);
            if (access.member instanceof IdentifierNode) {
                String memberName = ((IdentifierNode) access.member).name;
                String owner = "java/lang/Object"; // best effort fallback
                String desc = "Ljava/lang/Object;";
                emit("    getfield " + owner + "/" + memberName + " " + desc);
                return;
            } else {
                // member is something else (like method invocation) — handle later
                emit("    aconst_null");
                return;
            }
        }
        if (expr instanceof ConstructorInvocationNode) {
            ConstructorInvocationNode ci = (ConstructorInvocationNode) expr;
            switch (ci.className) {
                case "Integer":
                    if (ci.arguments == null || ci.arguments.isEmpty()) {
                        emit("    iconst_0");
                    } else {
                        generateExpression(ci.arguments.get(0));
                        // if arg produced boxed Integer, we didn't unbox — leave as is (simple case)
                    }
                    emitBoxInteger();
                    return;
                case "Real":
                    if (ci.arguments == null || ci.arguments.isEmpty()) {
                        emit("    dconst_0");
                    } else {
                        generateExpression(ci.arguments.get(0));
                    }
                    emitBoxDouble();
                    return;
                case "Boolean":
                    if (ci.arguments == null || ci.arguments.isEmpty()) {
                        emit("    iconst_0");
                    } else {
                        generateExpression(ci.arguments.get(0));
                    }
                    emitBoxBoolean();
                    return;
                case "List":
                case "Array":
                    emit("    new java/util/ArrayList");
                    emit("    dup");
                    emit("    invokespecial java/util/ArrayList/<init>()V");
                    return;
                default:
                    emit("    aconst_null");
                    return;
            }
        }
        if (expr instanceof MethodInvocationNode) {
            // method calls not yet implemented — placeholder
            emit("    aconst_null");
            return;
        }

        // fallback
        emit("    aconst_null");
    }

    // -------------------------
    // Helpers: descriptors & boxing
    // -------------------------
    private String descriptorForTypeNode(ASTNode typeNode) {
        // Very simple mapping: common builtin types -> runtime class; otherwise object
        if (typeNode == null) {
            return "Ljava/lang/Object;";
        }
        if (typeNode instanceof TypeNode) {
            String n = ((TypeNode) typeNode).name;
            switch (n) {
                case "Integer": return "Ljava/lang/Integer;";
                case "Real": return "Ljava/lang/Double;";
                case "Boolean": return "Ljava/lang/Boolean;";
                case "List": return "Ljava/util/ArrayList;";
                case "Array": return "Ljava/util/ArrayList;";
                default: return "Ljava/lang/Object;";
            }
        }
        if (typeNode instanceof GenericTypeNode) {
            GenericTypeNode g = (GenericTypeNode) typeNode;
            switch (g.baseType) {
                case "List": return "Ljava/util/ArrayList;";
                case "Array": return "Ljava/util/ArrayList;";
                default: return "Ljava/lang/Object;";
            }
        }
        return "Ljava/lang/Object;";
    }

    private void emitBoxInteger() {
        emit("    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;");
    }
    private void emitBoxDouble() {
        emit("    invokestatic java/lang/Double/valueOf(D)Ljava/lang/Double;");
    }
    private void emitBoxBoolean() {
        emit("    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;");
    }


    @Override
    public void visit(VarDeclNode node) {
        // 1. Генерируем expression
        if (node.initializer != null) {
            node.initializer.accept(this);
        } else {
            emit("    aconst_null");
        }

        // 2. Выдаём индекс
        int index = currentLocalIndex++;
        localVars.put(node.varName, index);

        // 3. Сохраняем
        emit("    astore " + index);
    }


    // =======================
    // METHOD DECLARATION
    // =======================

    // локальные переменные метода
    private int currentLocalIndex;
    private Map<String, Integer> localVars;

    @Override
    public void visit(MethodDeclNode node) {
        MethodHeaderNode header = node.header;

        // Начало метода
        emit(".method public " + header.methodName + methodDescriptor(header));

        emit("    .limit stack 64");
        emit("    .limit locals 64");

        // Подготовка контекста локальных переменных
        currentLocalIndex = 0;
        localVars = new HashMap<>();

        // Зарегистрировать "this"
        localVars.put("this", currentLocalIndex++);

        // Зарегистрировать параметры
        if (header.parameters != null) {
            for (ParamDeclNode p : header.parameters) {
                localVars.put(p.paramName, currentLocalIndex++);
            }
        }

        // ТЕЛО МЕТОДА
        MethodBodyNode body = node.body;
        if (body != null) {
            if (body.isArrow && body.arrowExpression != null) {
                generateExpression(body.arrowExpression);
                emit("    areturn");
            } else if (!body.isArrow && body.body != null) {
                // full body: walk body elements
                body.body.accept(this);

                // if method has no explicit return and return type is void -> return
                if (header.returnType == null) {
                    emit("    return");
                } else {
                    // non-void with no return -> return null
                    emit("    aconst_null");
                    emit("    areturn");
                }
            } else {
                // no body
                if (header.returnType == null) {
                    emit("    return");
                } else {
                    emit("    aconst_null");
                    emit("    areturn");
                }
            }
        } else {
            if (header.returnType == null) {
                emit("    return");
            } else {
                emit("    aconst_null");
                emit("    areturn");
            }
        }

        emit(".end method");
        emit("");
    }


    // -------------------------
    // Method descriptor helper
    // -------------------------
    private String methodDescriptor(MethodHeaderNode header) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        // аргументы
        if (header.parameters != null) {
            for (ParamDeclNode p : header.parameters) {
                sb.append(descriptorForTypeNode(p.paramType));
            }
        }

        sb.append(")");

        // возвращаемый тип
        if (header.returnType == null) {
            sb.append("V");
        } else {
            // пока упрощённо — Object-like типы
            sb.append(descriptorForTypeName(header.returnType));
        }

        return sb.toString();
    }

    // для returnType, который хранится как строка
    private String descriptorForTypeName(String name) {
        switch (name) {
            case "Integer": return "Ljava/lang/Integer;";
            case "Real": return "Ljava/lang/Double;";
            case "Boolean": return "Ljava/lang/Boolean;";
            case "List": return "Ljava/util/ArrayList;";
            case "Array": return "Ljava/util/ArrayList;";
            default: return "Ljava/lang/Object;";
        }
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

    // -------------------------
    // Body and statements
    // -------------------------
    @Override
    public void visit(BodyNode node) {
        if (node == null || node.elements == null) return;
        for (BodyElementNode el : node.elements) {
            if (el instanceof ASTNode) {
                ((ASTNode) el).accept(this);
            }
        }
    }

    @Override
    public void visit(AssignmentNode node) {
        if (node == null) return;

        // right-hand side value will be on stack after generateExpression
        // need to handle left-side cases

        // If left is IdentifierNode
        if (node.left instanceof IdentifierNode) {
            String name = ((IdentifierNode) node.left).name;
            // evaluate RHS first
            generateExpression(node.right);

            // local?
            if (localVars != null && localVars.containsKey(name)) {
                int idx = localVars.get(name);
                emitStoreVar(idx);
                return;
            } else {
                // field of current object: value on stack, then aload_0 swap putfield
                emit("    aload_0");
                emit("    swap");
                String owner = currentClassName.replace('.', '/');
                // we need a descriptor: try to find declared field type? For simplicity use Object
                String desc = "Ljava/lang/Object;";
                // If class fields were declared we could lookup their types — we haven't stored that mapping; keep Object
                emit("    putfield " + owner + "/" + name + " " + desc);
                return;
            }
        }

        // If left is MemberAccessNode (e.g., obj.field)
        if (node.left instanceof MemberAccessNode) {
            MemberAccessNode access = (MemberAccessNode) node.left;
            // Expect access.member to be IdentifierNode
            if (access.member instanceof IdentifierNode) {
                // generate target object (push objectref)
                generateExpression(access.target);
                // generate RHS value (push value)
                generateExpression(node.right);
                // Now stack: target, value -> putfield expects objectref then value (ok)
                IdentifierNode memberId = (IdentifierNode) access.member;
                // owner class unknown at compile time: we try to infer from target being Identifier (local or field)
                String ownerInternal = "java/lang/Object"; // fall back
                if (access.target instanceof IdentifierNode) {
                    // if target is a local variable and declared as some type, we could know owner — skipped now
                }
                String desc = "Ljava/lang/Object;";
                emit("    putfield " + ownerInternal + "/" + memberId.name + " " + desc);
                return;
            }
        }

        // fallback: do nothing
    }

    @Override
    public void visit(WhileLoopNode node) {

        String begin = newLabel("while_begin");
        String end = newLabel("while_end");

        emit(begin + ":");

        node.condition.accept(this);
        emit("    ifeq " + end);

        visitBody(node.body);

        emit("    goto " + begin);
        emit(end + ":");
    }


    @Override
    public void visit(IfStatementNode node) {

        String elseLabel = newLabel("else");
        String endLabel = newLabel("endif");

        // cond
        node.condition.accept(this);

        // ifeq → jump if false
        emit("    ifeq " + elseLabel);

        // then-body
        visitBody(node.thenBody);

        // goto end
        emit("    goto " + endLabel);

        // else:
        emit(elseLabel + ":");

        if (node.elseBody != null) {
            visitBody(node.elseBody);
        }

        emit(endLabel + ":");
    }


    @Override
    public void visit(ReturnNode node) {
        if (node == null) return;
        if (node.expression == null) {
            emit("    return");
        } else {
            generateExpression(node.expression);
            emit("    areturn");
        }
    }

    @Override
    public void visit(PrintNode node) {
        if (node == null) return;
        emit("    getstatic java/lang/System/out Ljava/io/PrintStream;");
        generateExpression(node.expression);
        emit("    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V");
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

    private void visitBody(BodyNode body) {
        for (BodyElementNode elem : body.elements) {
            ((ASTNode)elem).accept(this);
        }
    }

    // -------------------------
    // Helpers: load/store locals
    // -------------------------
    private void emitLoadVar(int idx) {
        if (idx >= 0 && idx <= 3) {
            emit("    aload_" + idx);
        } else {
            emit("    aload " + idx);
        }
    }

    private void emitStoreVar(int idx) {
        if (idx >= 0 && idx <= 3) {
            emit("    astore_" + idx);
        } else {
            emit("    astore " + idx);
        }
    }

    private void emitFields(ClassInfo info) {
        for (FieldInfo field : info.orderedFields) {
            emit(".field public " + field.name + " " + descriptorForType(field.type));
        }
    }

}
*/