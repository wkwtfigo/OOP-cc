package org.example.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    private Map<String, String> localVarTypes; 

    // -------------------------
    // Внутренние структуры для информации о классах
    // -------------------------
    private static class ClassInfo {
        final String name;
        final String superName; // internal name: java/lang/Object и т.п.
        final Map<String, FieldInfo> fields = new HashMap<>();
        final Map<String, MethodDeclNode> methods = new HashMap<>();
        final java.util.List<ConstructorDeclNode> constructors = new java.util.ArrayList<>();

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
                String desc;
                if (v.type != null) {
                    desc = descriptorForTypeNode(v.type);
                } else {
                    String tn = inferTypeNameFromInitializer(v.initializer); // Integer, Real, ...
                    if (tn != null) {
                        desc = descriptorForTypeName(tn); // уже есть helper
                    } else {
                        desc = "Ljava/lang/Object;";
                    }
                }
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

            // Попытаться сгенерировать точку входа main, если это класс Main
            maybeGenerateProgramEntry(info.methods.values());
        }
        saveFile(node.className);
    }

    /**
     * Генерирует public static void main(String[] args) для класса Main,
     * если в нём есть метод start() без параметров и без возвращаемого типа.
     */
    private void maybeGenerateProgramEntry(Iterable<MethodDeclNode> methods) {
        // Нас интересует только класс Main
        if (!"Main".equals(currentClassName)) {
            return;
        }

        boolean hasStart = false;

        for (MethodDeclNode m : methods) {
            MethodHeaderNode h = m.header;
            if (!"start".equals(h.methodName)) {
                continue;
            }
            // параметры отсутствуют или пустой список
            boolean noParams = (h.parameters == null || h.parameters.isEmpty());
            // returnType == null → void (в твоей модели)
            boolean isVoid = (h.returnType == null);

            if (noParams && isVoid) {
                hasStart = true;
                break;
            }
        }

        if (!hasStart) {
            return;
        }

        // Генерация public static main([Ljava/lang/String;)V
        String ownerInternal = currentClassName.replace('.', '/');

        emit(".method public static main([Ljava/lang/String;)V");
        emit("    .limit stack 4");
        emit("    .limit locals 1");

        emit("    new " + ownerInternal);
        emit("    dup");
        emit("    invokespecial " + ownerInternal + "/<init>()V");
        emit("    invokevirtual " + ownerInternal + "/start()V");

        emit("    return");
        emit(".end method");
        emit("");
    }

    @Override
    public void visit(ClassDeclNode node) {
        // Классы обрабатываем через visit(ProgramNode) → registerClass/generateClass
    }

    // Явный конструктор из исходного кода
    private void generateConstructor(ConstructorDeclNode ctor, String superInternal) {
        String descriptor = constructorDescriptor(ctor);

        emit(".method public <init>" + descriptor);
        emit("    .limit stack 64");
        emit("    .limit locals 64");

        currentLocalIndex = 0;
        localVars = new HashMap<>();
        localVarTypes = new HashMap<>();

        localVars.put("this", currentLocalIndex++);
        localVarTypes.put("this", currentClassName);

        if (ctor.parameters != null) {
            for (ParamDeclNode p : ctor.parameters) {
                localVars.put(p.paramName, currentLocalIndex++);
                localVarTypes.put(p.paramName, typeNameForTypeNode(p.paramType));
            }
        }

        // super()
        emit("    aload_0");
        emit("    invokespecial " + superInternal + "/<init>()V");

        // >>> ДОБАВИТЬ: инициализация полей, как в default-конструкторе <<<
        ClassDeclNode clazz = classAstNodes.get(currentClassName);
        if (clazz != null && clazz.members != null) {
            ClassInfo ci = classInfoMap.get(currentClassName);
            String ownerInternal = currentClassName.replace('.', '/');

            for (MemberNode member : clazz.members) {
                if (member instanceof VarDeclNode v && v.initializer != null) {
                    emit("    aload_0");
                    generateExpression(v.initializer);

                    // берём тип поля из ClassInfo, там уже правильный descriptor
                    String desc = "Ljava/lang/Object;";
                    if (ci != null) {
                        FieldInfo f = ci.fields.get(v.varName);
                        if (f != null) {
                            desc = f.descriptor;
                        }
                    }
                    emit("    putfield " + ownerInternal + "/" + v.varName + " " + desc);
                }
            }
        }

        // тело конструктора
        if (ctor.body != null) {
            ctor.body.accept(this);
        }

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

    private void generateUserConstructor(ConstructorInvocationNode ci) {
        String className = ci.className;
        String ownerInternal = className.replace('.', '/');

        int argCount = (ci.arguments == null ? 0 : ci.arguments.size());
        ConstructorDeclNode decl = findConstructor(className, argCount);

        String descriptor;
        if (decl != null) {
            descriptor = constructorDescriptor(decl);
        } else {
            // fallback: считаем, что все аргументы — Object
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < argCount; i++) {
                sb.append("Ljava/lang/Object;");
            }
            sb.append(")V");
            descriptor = sb.toString();
        }

        // new className
        emit("    new " + ownerInternal);
        emit("    dup");

        // аргументы
        if (ci.arguments != null) {
            for (ExpressionNode arg : ci.arguments) {
                generateExpression(arg);
            }
        }

        // вызов конструктора
        emit("    invokespecial " + ownerInternal + "/<init>" + descriptor);
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
                ClassInfo ci = classInfoMap.get(currentClassName);
                String desc = "Ljava/lang/Object;";
                if (ci != null) {
                    FieldInfo f = ci.fields.get(name);
                    if (f != null) {
                        desc = f.descriptor;
                    }
                }
                emit("    getfield " + owner + "/" + name + " " + desc);
                return;
            }
        }
        if (expr instanceof MemberAccessNode) {
            MemberAccessNode access = (MemberAccessNode) expr;

            // a.Method(...)
            if (access.member instanceof MethodInvocationNode mi) {
                // метод на объекте a
                generateMethodCallOnObject(access.target, mi);
                return;
            }

            // a.field
            if (access.member instanceof IdentifierNode id) {
                generateObjectFieldAccess(access.target, id.name);
                return;
            }

            // fallback: просто вернём target и null (на всякий случай)
            generateExpression(access.target);
            emit("    aconst_null");
            return;
        }
        if (expr instanceof ConstructorInvocationNode) {
            ConstructorInvocationNode ci = (ConstructorInvocationNode) expr;

            switch (ci.className) {
                case "Integer":
                    if (ci.arguments == null || ci.arguments.isEmpty()) {
                        // Integer() -> Integer(0)
                        emit("    iconst_0");
                        emitBoxInteger();
                    } else {
                        // Аргумент уже возвращает Integer (или совместимый Object),
                        // НИЧЕГО дополнительно не боксируем
                        generateExpression(ci.arguments.get(0));
                    }
                    return;

                case "Real":
                    if (ci.arguments == null || ci.arguments.isEmpty()) {
                        // Real() -> Real(0.0)
                        emit("    dconst_0");
                        emitBoxDouble();
                    } else {
                        generateExpression(ci.arguments.get(0));
                        // без дополнительного emitBoxDouble()
                    }
                    return;

                case "Boolean":
                    if (ci.arguments == null || ci.arguments.isEmpty()) {
                        // Boolean() -> Boolean(false)
                        emit("    iconst_0");
                        emitBoxBoolean();
                    } else {
                        generateExpression(ci.arguments.get(0));
                        // без emitBoxBoolean()
                    }
                    return;

                case "List":
                case "Array":
                    emit("    new java/util/ArrayList");
                    emit("    dup");
                    emit("    invokespecial java/util/ArrayList/<init>()V");
                    return;

                default:
                    // Пользовательский класс
                    generateUserConstructor(ci);
                    return;
            }
        }

        if (expr instanceof MethodInvocationNode) {
            generateMethodCallOnObject(new ThisNode(), (MethodInvocationNode) expr);
            return;
        }

        // fallback
        emit("    aconst_null");
    }

    private void generateObjectFieldAccess(ExpressionNode target, String fieldName) {
        // 1. На стек кладём objectref (например, c1)
        generateExpression(target);

        // 2. Пытаемся понять тип объекта
        String ownerInternal = "java/lang/Object";
        String desc = "Ljava/lang/Object;";

        String typeName = null;

        if (target instanceof IdentifierNode id) {
            // локальная переменная: смотрим в localVarTypes
            if (localVarTypes != null) {
                typeName = localVarTypes.get(id.name);  // напр. "ConstructorExample"
            }
        } else if (target instanceof ThisNode) {
            // this.field
            typeName = currentClassName;
        }

        if (typeName != null) {
            ClassInfo ci = classInfoMap.get(typeName);
            if (ci != null) {
                FieldInfo f = ci.fields.get(fieldName);
                if (f != null) {
                    ownerInternal = ci.name.replace('.', '/'); // ConstructorExample -> ConstructorExample
                    desc = f.descriptor;                      // Ljava/lang/Integer; и т.п.
                }
            }
        }

        // 3. Собственно доступ к полю
        emit("    getfield " + ownerInternal + "/" + fieldName + " " + desc);
    }

    /**
     * Вызов a.Method(...), где a – произвольное выражение (Integer, Real и т.п.).
     * Здесь мы распознаём встроенные методы и генерим прямой JVM-код.
     */
    private void generateMethodCallOnObject(ExpressionNode targetExpr, MethodInvocationNode call) {
        String methodName = call.methodName;

        ExpressionNode arg = (call.arguments != null && !call.arguments.isEmpty())
        ? call.arguments.get(0)
        : null;

        boolean realCtx = isRealContext(targetExpr, arg);

        // ----- Conversions -----

        // Integer.toReal()
        if ("toReal".equals(methodName)) {
            generateIntegerToReal(targetExpr);
            return;
        }

        // Integer.toBoolean() или Real.toBoolean()
        if ("toBoolean".equals(methodName)) {
            if (isRealExpr(targetExpr)) {
                generateRealToBoolean(targetExpr);
            } else if (isIntegerExpr(targetExpr)) {
                generateIntegerToBoolean(targetExpr);
            } else {
                // предполагаем, что уже Boolean – просто вернуть как есть
                generateExpression(targetExpr);
            }
            return;
        }

        // Real.toInteger() или Boolean.toInteger()
        if ("toInteger".equals(methodName)) {
            if (isRealExpr(targetExpr)) {
                generateRealToInteger(targetExpr);
            } else if (isBooleanExpr(targetExpr)) {
                generateBooleanToInteger(targetExpr);
            } else {
                // предполагаем, что уже Integer – просто вернуть как есть
                generateExpression(targetExpr);
            }
            return;
        }

        // ----- Boolean operators -----
        if ("Or".equals(methodName)) {
            generateBooleanOr(targetExpr, arg);
            return;
        }
        if ("And".equals(methodName)) {
            generateBooleanAnd(targetExpr, arg);
            return;
        }
        if ("Xor".equals(methodName)) {
            generateBooleanXor(targetExpr, arg);
            return;
        }
        if ("Not".equals(methodName)) {
            generateBooleanNot(targetExpr);
            return;
        }

        // ----- Real-specific -----
        if ("UnaryMinus".equals(methodName) && realCtx) {
            generateRealUnaryMinus(targetExpr);
            return;
        }
        // ----- Relations -----
        if ("Less".equals(methodName)) {
            if (realCtx) {
                generateRealLess(targetExpr, arg);
            } else {
                generateIntegerLess(targetExpr, arg);
            }
            return;
        }
        if ("LessEqual".equals(methodName)) {
            if (realCtx) {
                generateRealLessEqual(targetExpr, arg);
            } else {
                generateIntegerLessEqual(targetExpr, arg);
            }
            return;
        }
        if ("Greater".equals(methodName)) {
            if (realCtx) {
                generateRealGreater(targetExpr, arg);
            } else {
                generateIntegerGreater(targetExpr, arg);
            }
            return;
        }
        if ("GreaterEqual".equals(methodName)) {
            if (realCtx) {
                generateRealGreaterEqual(targetExpr, arg);
            } else {
                generateIntegerGreaterEqual(targetExpr, arg);
            }
            return;
        }
        if ("Equal".equals(methodName)) {
            if (realCtx) {
                generateRealEqual(targetExpr, arg);
            } else {
                generateIntegerEqual(targetExpr, arg);
            }
            return;
        }

        // ----- Arithmetic -----
        if ("Plus".equals(methodName)) {
            if (realCtx) {
                generateRealPlus(targetExpr, arg);
            } else {
                generateIntegerPlus(targetExpr, arg);
            }
            return;
        }
        if ("Minus".equals(methodName)) {
            if (realCtx) {
                generateRealMinus(targetExpr, arg);
            } else {
                generateIntegerMinus(targetExpr, arg);
            }
            return;
        }
        if ("Mult".equals(methodName)) {
            if (realCtx) {
                generateRealMult(targetExpr, arg);
            } else {
                generateIntegerMult(targetExpr, arg);
            }
            return;
        }
        if ("Div".equals(methodName)) {
            if (realCtx) {
                generateRealDiv(targetExpr, arg);
            } else {
                generateIntegerDiv(targetExpr, arg);
            }
            return;
        }
        if ("Rem".equals(methodName)) {
            if (realCtx) {
                generateRealRem(targetExpr, arg);
            } else {
                generateIntegerRem(targetExpr, arg);
            }
            return;
        }

        // TODO: сюда позже добавим Real <-> Integer перегрузки явно, если понадобится

        // Если метод не распознали – пока сваливаемся в invokeMethodOnThis (старое поведение):
        boolean hasResult = invokeMethodOnThis(call);
        if (hasResult) {
            // если это standalone statement (visit(MethodInvocationNode)), мы можем его поп-нуть
            // (POP вызывается там, а не здесь)
        }
    }

    // =======================
    // Conversions Integer / Real / Boolean
    // =======================

    private void generateIntegerToReal(ExpressionNode target) {
        // this : Integer -> Double
        generateExpression(target);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");
        emit("    i2d");
        emitBoxDouble(); // -> java/lang/Double
    }

    private void generateIntegerToBoolean(ExpressionNode target) {
        // this : Integer -> Boolean (0 -> false, !=0 -> true)
        generateExpression(target);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        String trueLabel = newLabel("int_to_bool_true");
        String endLabel  = newLabel("int_to_bool_end");

        emit("    ifne " + trueLabel);  // !=0 -> true
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");

        emitBoxBoolean();
    }

    private void generateBooleanToInteger(ExpressionNode target) {
        // this : Boolean -> Integer (false->0, true->1)
        generateExpression(target);
        emit("    checkcast java/lang/Boolean");
        emit("    invokevirtual java/lang/Boolean/booleanValue()Z");

        String trueLabel = newLabel("bool_to_int_true");
        String endLabel  = newLabel("bool_to_int_end");

        emit("    ifne " + trueLabel);  // true
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");

        emitBoxInteger();
    }

    private void generateRealToBoolean(ExpressionNode target) {
        // this : Real -> Boolean (0.0 -> false, иначе true)
        generateRealAsDouble(target);   // D
        emit("    dconst_0");
        emit("    dcmpl");              // cmp != 0 -> not equal to 0.0

        String trueLabel = newLabel("real_to_bool_true");
        String endLabel  = newLabel("real_to_bool_end");

        emit("    ifne " + trueLabel);  // !=0 => true
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");

        emitBoxBoolean();
    }

    // =======================
    // Boolean operations
    // =======================

    private void generateBooleanAsZ(ExpressionNode expr) {
        // Object(Boolean) -> Z на стеке
        generateExpression(expr);
        emit("    checkcast java/lang/Boolean");
        emit("    invokevirtual java/lang/Boolean/booleanValue()Z");
    }

    private void generateBooleanOr(ExpressionNode left, ExpressionNode right) {
        generateBooleanAsZ(left);

        if (right != null) {
            generateBooleanAsZ(right);
        } else {
            emit("    iconst_0");
        }

        emit("    ior");
        emitBoxBoolean();
    }

    private void generateBooleanAnd(ExpressionNode left, ExpressionNode right) {
        generateBooleanAsZ(left);

        if (right != null) {
            generateBooleanAsZ(right);
        } else {
            emit("    iconst_0");
        }

        emit("    iand");
        emitBoxBoolean();
    }

    private void generateBooleanXor(ExpressionNode left, ExpressionNode right) {
        generateBooleanAsZ(left);

        if (right != null) {
            generateBooleanAsZ(right);
        } else {
            emit("    iconst_0");
        }

        emit("    ixor");
        emitBoxBoolean();
    }

    private void generateBooleanNot(ExpressionNode expr) {
        generateBooleanAsZ(expr);
        emit("    iconst_1");
        emit("    ixor");       // инверсия бита 0/1
        emitBoxBoolean();
    }



    /**
     * Генерирует на стеке double (D) из произвольного значения,
     * которое в рантайме должно быть либо Integer, либо Double.
     *
     * Stack before:  [ ..., obj ]
     * Stack after:   [ ..., d ]
     */
    private void generateRealAsDouble(ExpressionNode expr) {
        generateExpression(expr); // кладём Object

        String intLabel = newLabel("real_from_int");
        String endLabel = newLabel("real_from_end");

        // dup obj
        emit("    dup");
        // obj instanceof Integer ?
        emit("    instanceof java/lang/Integer");
        emit("    ifne " + intLabel);

        // --- branch for Double ---
        emit("    checkcast java/lang/Double");
        emit("    invokevirtual java/lang/Double/doubleValue()D");
        emit("    goto " + endLabel);

        // --- branch for Integer ---
        emit(intLabel + ":");
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");
        emit("    i2d");

        emit(endLabel + ":");
    }

    private void generateRealToInteger(ExpressionNode target) {
        generateRealAsDouble(target);      // D
        emit("    d2i");                   // I
        emitBoxInteger();                  // -> java/lang/Integer
    }

    private void generateRealUnaryMinus(ExpressionNode target) {
        generateRealAsDouble(target);      // D
        emit("    dneg");                 // D
        emitBoxDouble();                  // -> java/lang/Double
    }

    private void generateRealPlus(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dadd");
        emitBoxDouble();
    }

    private void generateRealMinus(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dsub");
        emitBoxDouble();
    }

    private void generateRealMult(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dmul");
        emitBoxDouble();
    }

    private void generateRealDiv(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    ddiv");
        emitBoxDouble();
    }

    private void generateRealRem(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    drem");
        emitBoxDouble();
    }

    private void generateRealLess(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dcmpl");  // cmp = (left ? right)
        String trueLabel = newLabel("real_lt_true");
        String endLabel  = newLabel("real_lt_end");
        emit("    iflt " + trueLabel);
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emitBoxBoolean();
    }

    private void generateRealLessEqual(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dcmpl");
        String trueLabel = newLabel("real_le_true");
        String endLabel  = newLabel("real_le_end");
        emit("    ifle " + trueLabel);
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emitBoxBoolean();
    }

    private void generateRealGreater(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dcmpl");
        String trueLabel = newLabel("real_gt_true");
        String endLabel  = newLabel("real_gt_end");
        emit("    ifgt " + trueLabel);
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emitBoxBoolean();
    }

    private void generateRealGreaterEqual(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dcmpl");
        String trueLabel = newLabel("real_ge_true");
        String endLabel  = newLabel("real_ge_end");
        emit("    ifge " + trueLabel);
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emitBoxBoolean();
    }

    private void generateRealEqual(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dcmpl");
        String trueLabel = newLabel("real_eq_true");
        String endLabel  = newLabel("real_eq_end");
        emit("    ifeq " + trueLabel);
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emitBoxBoolean();
    }



    /**
     * a.Greater(b) : Boolean   (a > b)
     */
    private void generateIntegerGreater(ExpressionNode left, ExpressionNode right) {
        generateExpression(left);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        if (right != null) {
            generateExpression(right);
            emit("    checkcast java/lang/Integer");
            emit("    invokevirtual java/lang/Integer/intValue()I");
        } else {
            emit("    iconst_0");
        }

        String trueLabel = newLabel("int_gt_true");
        String endLabel  = newLabel("int_gt_end");

        emit("    if_icmpgt " + trueLabel);
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emit("    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;");
    }

    /**
     * a.Less(b) : Boolean   (a < b)
     */
    private void generateIntegerLess(ExpressionNode left, ExpressionNode right) {
        generateExpression(left);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        if (right != null) {
            generateExpression(right);
            emit("    checkcast java/lang/Integer");
            emit("    invokevirtual java/lang/Integer/intValue()I");
        } else {
            emit("    iconst_0");
        }

        String trueLabel = newLabel("int_lt_true");
        String endLabel  = newLabel("int_lt_end");

        emit("    if_icmplt " + trueLabel);
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emit("    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;");
    }

    /**
     * a.LessEqual(b) : Boolean   (a <= b)
     */
    private void generateIntegerLessEqual(ExpressionNode left, ExpressionNode right) {
        generateExpression(left);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        if (right != null) {
            generateExpression(right);
            emit("    checkcast java/lang/Integer");
            emit("    invokevirtual java/lang/Integer/intValue()I");
        } else {
            emit("    iconst_0");
        }

        String trueLabel = newLabel("int_le_true");
        String endLabel  = newLabel("int_le_end");

        emit("    if_icmple " + trueLabel);
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emit("    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;");
    }

    /**
     * a.GreaterEqual(b) : Boolean   (a >= b)
     */
    private void generateIntegerGreaterEqual(ExpressionNode left, ExpressionNode right) {
        generateExpression(left);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        if (right != null) {
            generateExpression(right);
            emit("    checkcast java/lang/Integer");
            emit("    invokevirtual java/lang/Integer/intValue()I");
        } else {
            emit("    iconst_0");
        }

        String trueLabel = newLabel("int_ge_true");
        String endLabel  = newLabel("int_ge_end");

        emit("    if_icmpge " + trueLabel);
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emit("    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;");
    }

    /**
     * a.Equal(b) : Boolean   (a == b)
     */
    private void generateIntegerEqual(ExpressionNode left, ExpressionNode right) {
        generateExpression(left);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        if (right != null) {
            generateExpression(right);
            emit("    checkcast java/lang/Integer");
            emit("    invokevirtual java/lang/Integer/intValue()I");
        } else {
            emit("    iconst_0");
        }

        String trueLabel = newLabel("int_eq_true");
        String endLabel  = newLabel("int_eq_end");

        emit("    if_icmpeq " + trueLabel);
        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emit("    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;");
    }


    /**
     * a.Plus(b) : Integer  (a + b)
     */
    private void generateIntegerPlus(ExpressionNode left, ExpressionNode right) {
        generateExpression(left);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        if (right != null) {
            generateExpression(right);
            emit("    checkcast java/lang/Integer");
            emit("    invokevirtual java/lang/Integer/intValue()I");
        } else {
            emit("    iconst_0");
        }

        emit("    iadd");
        emitBoxInteger();
    }

    /**
     * a.Minus(b) : Integer  (a - b)
     */
    private void generateIntegerMinus(ExpressionNode left, ExpressionNode right) {
        generateExpression(left);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        if (right != null) {
            generateExpression(right);
            emit("    checkcast java/lang/Integer");
            emit("    invokevirtual java/lang/Integer/intValue()I");
        } else {
            emit("    iconst_0");
        }

        emit("    isub");
        emitBoxInteger();
    }

    /**
     * a.Mult(b) : Integer  (a * b)
     */
    private void generateIntegerMult(ExpressionNode left, ExpressionNode right) {
        generateExpression(left);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        if (right != null) {
            generateExpression(right);
            emit("    checkcast java/lang/Integer");
            emit("    invokevirtual java/lang/Integer/intValue()I");
        } else {
            emit("    iconst_1"); // умножать на 1
        }

        emit("    imul");
        emitBoxInteger();
    }

    /**
     * a.Div(b) : Integer  (a / b)
     */
    private void generateIntegerDiv(ExpressionNode left, ExpressionNode right) {
        generateExpression(left);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        if (right != null) {
            generateExpression(right);
            emit("    checkcast java/lang/Integer");
            emit("    invokevirtual java/lang/Integer/intValue()I");
        } else {
            emit("    iconst_1"); // делить на 1
        }

        emit("    idiv");
        emitBoxInteger();
    }

    /**
     * a.Rem(b) : Integer  (a % b)
     */
    private void generateIntegerRem(ExpressionNode left, ExpressionNode right) {
        generateExpression(left);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");

        if (right != null) {
            generateExpression(right);
            emit("    checkcast java/lang/Integer");
            emit("    invokevirtual java/lang/Integer/intValue()I");
        } else {
            emit("    iconst_1"); // %1 == 0, но пусть будет
        }

        emit("    irem");
        emitBoxInteger();
    }

    /**
     * Вызов метода текущего класса на this.
     * @return true, если метод возвращает значение (не void)
     */
    private boolean invokeMethodOnThis(MethodInvocationNode node) {
        String methodName = node.methodName;

        // грузим this
        emit("    aload_0");

        // аргументы
        if (node.arguments != null) {
            for (ExpressionNode arg : node.arguments) {
                generateExpression(arg);
            }
        }

        MethodDeclNode decl = findMethodInCurrentClass(methodName);
        String descriptor;
        boolean isVoid;

        if (decl != null) {
            descriptor = methodDescriptor(decl.header);
            isVoid = (decl.header.returnType == null);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            if (node.arguments != null) {
                for (int i = 0; i < node.arguments.size(); i++) {
                    sb.append("Ljava/lang/Object;");
                }
            }
            sb.append(")Ljava/lang/Object;");
            descriptor = sb.toString();
            isVoid = false;
        }

        String ownerInternal = currentClassName.replace('.', '/');
        emit("    invokevirtual " + ownerInternal + "/" + methodName + descriptor);

        return !isVoid;
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
            generateExpression(node.initializer);
        } else {
            emit("    aconst_null");
        }

        // 2. Выдаём индекс
        int index = currentLocalIndex++;
        localVars.put(node.varName, index);

        if (localVarTypes != null) {
            String typeName = typeNameForTypeNode(node.type); // сейчас даст null
            if (typeName == null) {
                typeName = inferTypeNameFromInitializer(node.initializer);
            }
            if (typeName != null) {
                localVarTypes.put(node.varName, typeName);
            }
        }

        // 3. Сохраняем
        emit("    astore " + index);
    }

    private String typeNameForTypeNode(ASTNode typeNode) {
        if (typeNode instanceof TypeNode t) {
            return t.name;          // "Integer", "Real", "Boolean", ...
        }
        if (typeNode instanceof GenericTypeNode g) {
            return g.baseType;      // "List", "Array", ...
        }
        if (typeNode instanceof ConstructorInvocationNode ci) {
            return ci.className;
        }
        return null;
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
        localVarTypes = new HashMap<>();

        // Зарегистрировать "this"
        localVars.put("this", currentLocalIndex++);
        localVarTypes.put("this", currentClassName);

        // Зарегистрировать параметры
        if (header.parameters != null) {
            for (ParamDeclNode p : header.parameters) {
                localVars.put(p.paramName, currentLocalIndex++);
                if (localVarTypes != null) {
                    localVarTypes.put(p.paramName, typeNameForTypeNode(p.paramType));
                }
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

    private String constructorDescriptor(ConstructorDeclNode ctor) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (ctor.parameters != null) {
            for (ParamDeclNode p : ctor.parameters) {
                sb.append(descriptorForTypeNode(p.paramType));
            }
        }
        sb.append(")V");
        return sb.toString();
    }

    private MethodDeclNode findMethodInCurrentClass(String name) {
        ClassInfo info = classInfoMap.get(currentClassName);
        if (info == null) return null;
        return info.methods.get(name);
    }

    private ConstructorDeclNode findConstructor(String className, int argCount) {
        ClassInfo info = classInfoMap.get(className);
        if (info == null) return null;

        for (ConstructorDeclNode ctor : info.constructors) {
            int paramCount = (ctor.parameters == null ? 0 : ctor.parameters.size());
            if (paramCount == argCount) {
                return ctor;
            }
        }
        return null;
    }

    private boolean isRealExpr(ExpressionNode e) {
        if (e == null) return false;
        if (e instanceof RealLiteralNode) return true;
        if (e instanceof ConstructorInvocationNode ci && "Real".equals(ci.className)) return true;

        if (e instanceof IdentifierNode id) {
            // локальная переменная
            if (localVarTypes != null) {
                String t = localVarTypes.get(id.name);
                if ("Real".equals(t)) return true;
            }
            // поле класса
            ClassInfo info = classInfoMap.get(currentClassName);
            if (info != null) {
                FieldInfo f = info.fields.get(id.name);
                if (f != null && "Ljava/lang/Double;".equals(f.descriptor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isIntegerExpr(ExpressionNode e) {
        if (e == null) return false;
        if (e instanceof IntLiteralNode) return true;
        if (e instanceof ConstructorInvocationNode ci && "Integer".equals(ci.className)) return true;

        if (e instanceof IdentifierNode id) {
            if (localVarTypes != null) {
                String t = localVarTypes.get(id.name);
                if ("Integer".equals(t)) return true;
            }
            ClassInfo info = classInfoMap.get(currentClassName);
            if (info != null) {
                FieldInfo f = info.fields.get(id.name);
                if (f != null && "Ljava/lang/Integer;".equals(f.descriptor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBooleanExpr(ExpressionNode e) {
        if (e == null) return false;
        if (e instanceof BoolLiteralNode) return true;
        if (e instanceof ConstructorInvocationNode ci && "Boolean".equals(ci.className)) return true;

        if (e instanceof IdentifierNode id) {
            if (localVarTypes != null) {
                String t = localVarTypes.get(id.name);
                if ("Boolean".equals(t)) return true;
            }
            ClassInfo info = classInfoMap.get(currentClassName);
            if (info != null) {
                FieldInfo f = info.fields.get(id.name);
                if (f != null && "Ljava/lang/Boolean;".equals(f.descriptor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isRealContext(ExpressionNode target, ExpressionNode arg) {
        if (isRealExpr(target)) return true;
        if (isRealExpr(arg)) return true;
        return false;
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

        // 1) x = rhs;
        if (node.left instanceof IdentifierNode id) {
            String name = id.name;

            // вычисляем rhs один раз
            generateExpression(node.right);

            // локальная переменная?
            if (localVars != null && localVars.containsKey(name)) {
                int idx = localVars.get(name);
                emitStoreVar(idx);
                return;
            } else {
                // поле текущего объекта: this.name = rhs;
                emit("    aload_0");   // ..., value, this
                emit("    swap");      // ..., this, value

                String owner = currentClassName.replace('.', '/');
                String desc = "Ljava/lang/Object;";

                // если есть информация о поле — использовать её
                ClassInfo ci = classInfoMap.get(currentClassName);
                if (ci != null) {
                    FieldInfo f = ci.fields.get(name);
                    if (f != null) {
                        desc = f.descriptor;
                    }
                }

                emit("    putfield " + owner + "/" + name + " " + desc);
                return;
            }
        }

        // 2) obj.field = rhs;
        if (node.left instanceof MemberAccessNode access && access.member instanceof IdentifierNode memberId) {
            // objectref
            generateExpression(access.target);
            // value
            generateExpression(node.right);

            // В идеале здесь надо знать реальный класс ownerInternal.
            // Пока оставляем Object или можно позже доработать по типам.
            String ownerInternal = "java/lang/Object";
            String desc = "Ljava/lang/Object;";

            emit("    putfield " + ownerInternal + "/" + memberId.name + " " + desc);
            return;
        }

        // fallback: ничего не делаем
    }

    @Override
    public void visit(WhileLoopNode node) {

        String begin = newLabel("while_begin");
        String end = newLabel("while_end");

        emit(begin + ":");

        // cond: тоже Boolean-объект
        generateExpression(node.condition);
        emit("    checkcast java/lang/Boolean");
        emit("    invokevirtual java/lang/Boolean/booleanValue()Z");
        emit("    ifeq " + end);

        visitBody(node.body);

        emit("    goto " + begin);
        emit(end + ":");
    }

    @Override
    public void visit(IfStatementNode node) {

        String elseLabel = newLabel("else");
        String endLabel = newLabel("endif");

        // cond: ожидаем объект Boolean
        generateExpression(node.condition);
        emit("    checkcast java/lang/Boolean");
        emit("    invokevirtual java/lang/Boolean/booleanValue()Z");

        // ifeq → прыгаем, если false (0)
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
        // treat as this.Method(...)
        MethodInvocationNode copy = node;
        generateMethodCallOnObject(new ThisNode(), copy);
        emit("    pop");
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
        if (body == null || body.elements == null) return;
        for (BodyElementNode elem : body.elements) {
            ((ASTNode) elem).accept(this);
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

    private String inferTypeNameFromInitializer(ExpressionNode init) {
        if (init == null) return null;

        if (init instanceof ConstructorInvocationNode ci) {
            // var c1: ConstructorExample()  →  "ConstructorExample"
            return ci.className;
        }
        if (init instanceof IntLiteralNode)   return "Integer";
        if (init instanceof RealLiteralNode)  return "Real";
        if (init instanceof BoolLiteralNode)  return "Boolean";
        return null;
    }
}
