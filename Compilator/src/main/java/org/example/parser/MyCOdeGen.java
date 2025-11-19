package org.example.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCodeGen implements ASTVisitor{

    // Accumulates Jasmin code for the class currently being generated.
    private StringBuilder jasminCode;

    // Name of the class currently being generated.
    private String currentClassName;

    // Target output directory where generated {@code .j} files are written.
    private final String outputDir;

    // Counter for generating unique label names for branching instructions.
    private int labelCounter = 0;

    // Global map with collected information about all classes in the program.
    private final Map<String, ClassInfo> classInfoMap = new HashMap<>();

    // Map from class name to its AST node.
    private final Map<String, ClassDeclNode> classAstNodes = new HashMap<>();

    // Map of local variable names to their type names
    private Map<String, String> localVarTypes; 

    // -------------------------
    // Map of local variable names to their type names
    // -------------------------

    /**
     * Internal description of a user-defined class.
     * <p>
     * Contains the class name, super class (in internal JVM form),
     * declared fields, overloadable methods (grouped by name) and declared constructors.
     */
    private static class ClassInfo {
        /**
         * Source-level class name (e.g. {@code "Main"}).
         */
        final String name;
        /**
         * Superclass name in internal JVM form.
         * For example: {@code "java/lang/Object"}, {@code "Base"}, {@code "a/b/C"}.
         */
        final String superName;
        /**
         * Map of field name to {@link FieldInfo} for all fields declared in the class.
         */
        final Map<String, FieldInfo> fields = new HashMap<>();
        /**
         * Map of method name to list of method declarations representing overloads.
         */
        final Map<String, List<MethodDeclNode>> methods = new HashMap<>();
        /**
         * Map of method name to list of method declarations representing overloads.
         */
        final java.util.List<ConstructorDeclNode> constructors;

        ClassInfo(String name, String superName) {
            this.constructors = new java.util.ArrayList<>();
            this.name = name;
            this.superName = superName;
        }
    }

    /**
     * List of all constructors declared in this class.
     */
    private static class FieldInfo {
        /**
         * Internal description of a field: its name and JVM descriptor.
         */
        final String name;
        /**
         * JVM descriptor string describing the field type.
         * For example: {@code "Ljava/lang/Integer;"}, {@code "Ljava/util/ArrayList;"}.
         */
        final String descriptor;

        FieldInfo(String name, String descriptor) {
            this.name = name;
            this.descriptor = descriptor;
        }
    }

    /**
     * Creates a new unique label name based on a prefix and an incrementing counter.
     *
     * @param base base label prefix (e.g. {@code "if_true"})
     * @return unique label string (e.g. {@code "if_true_0"})
     */
    private String newLabel(String base) {
        return base + "_" + (labelCounter++);
    }

    /**
     * Constructs a generator that writes output into the default directory {@code "out"}.
     */
    public MyCodeGen() {
        this("out");
    }

    /**
     * Constructs a generator that writes {@code .j} files into the specified directory.
     *
     * @param outputDir directory where Jasmin files will be generated
     */
    public MyCodeGen(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Appends a single line of Jasmin code to the internal buffer,
     * automatically adding a trailing newline.
     *
     * @param line Jasmin source line to emit
     */
    private void emit(String line) {
        jasminCode.append(line).append("\n");
    }

    /**
     * Persists the current {@link #jasminCode} buffer into a {@code .j} file
     * under {@link #outputDir}.
     *
     * @param className name of the class, used as the file base name
     */
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

    /**
     * Entry point of the generator for a whole program.
     * This method executes two passes:
     *     Registers every class declaration and collects structural info.
     *     Generates Jasmin code for every class.
     * @param node root {@link ProgramNode} of the AST
     */
    @Override
    public void visit(ProgramNode node) {
        if (node == null || node.classes == null) {
            return;
        }

        // 1st pass: register classes
        for (ClassDeclNode classDecl : node.classes) {
            registerClass(classDecl);
        }

        // 2nd pass: generate code for each class
        for (ClassDeclNode classDecl : node.classes) {
            generateClass(classDecl);
        }
    }

    /**
     * First pass: collect information about a single class.
     * This method:
     *     Creates a {@link ClassInfo} entry for the class.
     *     Registers every field with its descriptor.
     *     Groups methods by name to support overloading.
     *     Stores constructor declarations.
     *
     * @param node class declaration AST node
     */
    private void registerClass(ClassDeclNode node) {
        if (node == null) return;

        ClassInfo info = new ClassInfo(node.className,
                                    node.extendsClass == null ? "java/lang/Object"
                                                                : node.extendsClass.replace('.', '/'));
        classInfoMap.put(node.className, info);
        classAstNodes.put(node.className, node);

        if (node.members == null) return;

        for (MemberNode member : node.members) {
            switch (member) {
                case VarDeclNode v -> {
                    String desc;
                    if (v.type != null) {
                        desc = descriptorForTypeNode(v.type);
                    } else {
                        String tn = inferTypeNameFromInitializer(v.initializer);
                        if (tn != null) {
                            desc = descriptorForTypeName(tn);
                        } else {
                            desc = "Ljava/lang/Object;";
                        }
                    }
                    info.fields.put(v.varName, new FieldInfo(v.varName, desc));
                }
                case MethodDeclNode m -> info.methods
                        .computeIfAbsent(m.header.methodName, k -> new ArrayList<>())
                        .add(m);
                case ConstructorDeclNode c -> info.constructors.add(c);
                default -> {
                }
            }
        }
    }

    /**
     * Second pass: generate Jasmin code for a single class.
     * <p>
     * This emits:
     * <ul>
     *     {@code .class} and {@code .super} headers
     *     Field declarations
     *     Constructors (user-defined or default)
     *     Method implementations
     *     {@code main} method (entry point) if appropriate
     * </ul>
     *
     * @param node class declaration AST node
     */
    private void generateClass(ClassDeclNode node) {
        if (node == null) return;

        currentClassName = node.className;
        jasminCode = new StringBuilder();

        ClassInfo info = classInfoMap.get(node.className);

        // class header
        emit(".class public " + node.className);
        String superInternal = (info != null ? info.superName : "java/lang/Object");
        emit(".super " + superInternal);
        emit("");

        // fields
        if (info != null) {
            for (FieldInfo f : info.fields.values()) {
                emit(".field public " + f.name + " " + f.descriptor);
            }
        }
        emit("");

        // constructor(s)
        if (info != null && !info.constructors.isEmpty()) {
            for (ConstructorDeclNode ctor : info.constructors) {
                generateConstructor(ctor, superInternal);
            }
        } else {
            // If no constructors present, generate a default one with field initializers
            generateDefaultConstructorWithInitializers(node, superInternal);
        }

        // methods
        if (info != null) {
            for (java.util.List<MethodDeclNode> list : info.methods.values()) {
                for (MethodDeclNode m : list) {
                    m.accept(this);
                }
            }

            maybeGenerateProgramEntry(info);
        }
        saveFile(node.className);
    }

    /**
     * Optionally generates a static {@code main} entry point that calls
     * parameterless {@code start()} on {@code Main}, if such a method exists.
     *
     * @param info {@link ClassInfo} for the current class
     */
    private void maybeGenerateProgramEntry(ClassInfo info) {
        if (!"Main".equals(currentClassName)) {
            return;
        }

        boolean hasStart = false;

        for (java.util.List<MethodDeclNode> list : info.methods.values()) {
            for (MethodDeclNode m : list) {
                MethodHeaderNode h = m.header;
                if (!"start".equals(h.methodName)) {
                    continue;
                }
                boolean noParams = (h.parameters == null || h.parameters.isEmpty());
                boolean isVoid   = (h.returnType == null);

                if (noParams && isVoid) {
                    hasStart = true;
                    break;
                }
            }
            if (hasStart) break;
        }

        if (!hasStart) {
            return;
        }

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

    /**
     * {@inheritDoc}
     * <p>
     * Class declarations are processed via {@link #visit(ProgramNode)} and the
     * two-pass mechanism, so this method is intentionally empty.
     *
     * @param node class declaration node
     */
    @Override
    public void visit(ClassDeclNode node) {}

    /**
     * Emits a user-defined constructor.
     * This method:
     *     Initializes the local variable context including {@code this} and parameters.
     *     Invokes {@code super()} on the superclass.
     *     Initializes fields with initializers (if present).
     *     Emits the constructor body.
     *
     * @param ctor          constructor declaration AST node
     * @param superInternal internal JVM name of the superclass
     */
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

        // Initialize fields with initializers as in the default constructor
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

        // Constructor body
        if (ctor.body != null) {
            ctor.body.accept(this);
        }

        emit("    return");
        emit(".end method");
        emit("");
    }

     /**
     * Generates a default parameterless constructor when a class does not
     * explicitly declare any constructors.
     * 
     * The constructor calls {@code super()} and applies field initializers.
     *
     * @param node          class AST node
     * @param superInternal internal JVM name of the superclass
     */
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

    /**
     * Generates bytecode to construct a user-defined class instance for a
     * {@link ConstructorInvocationNode}.
     * 
     * It attempts to select a matching constructor by number of arguments.
     * If no matching constructor is found, it falls back to a generic
     * descriptor assuming {@code Object}-typed parameters.
     *
     * @param ci constructor invocation AST node
     */
    private void generateUserConstructor(ConstructorInvocationNode ci) {
        String className = ci.className;
        String ownerInternal = className.replace('.', '/');

        int argCount = (ci.arguments == null ? 0 : ci.arguments.size());
        ConstructorDeclNode decl = findConstructor(className, argCount);

        String descriptor;
        if (decl != null) {
            descriptor = constructorDescriptor(decl);
        } else {
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

    /**
     * Generates bytecode for evaluating an expression and leaving its result
     * as a reference (boxed object) on the operand stack.
     *
     * @param expr expression AST node to generate
     */
    private void generateExpression(ExpressionNode expr) {
        if (expr == null) {
            emit("    aconst_null");
            return;
        }
        if (expr instanceof IntLiteralNode intLiteralNode) {
            int val = intLiteralNode.value;
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
        if (expr instanceof RealLiteralNode realLiteralNode) {
            double val = realLiteralNode.value;
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
        if (expr instanceof IdentifierNode identifierNode) {
            String name = identifierNode.name;
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
        if (expr instanceof MemberAccessNode access) {

            // a.Method(...)
            if (access.member instanceof MethodInvocationNode mi) {
                generateMethodCallOnObject(access.target, mi);
                return;
            }

            // a.field
            if (access.member instanceof IdentifierNode id) {
                generateObjectFieldAccess(access.target, id.name);
                return;
            }

            generateExpression(access.target);
            emit("    aconst_null");
            return;
        }
        if (expr instanceof ConstructorInvocationNode ci) {

            switch (ci.className) {
                case "Integer":
                    if (ci.arguments == null || ci.arguments.isEmpty()) {
                        // Integer() -> Integer(0)
                        emit("    iconst_0");
                        emitBoxInteger();
                    } else {
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
                    }
                    return;

                case "Boolean":
                    if (ci.arguments == null || ci.arguments.isEmpty()) {
                        // Boolean() -> Boolean(false)
                        emit("    iconst_0");
                        emitBoxBoolean();
                    } else {
                        generateExpression(ci.arguments.get(0));
                    }
                    return;

                case "List":
                    emit("    new java/util/ArrayList");
                    emit("    dup");
                    emit("    invokespecial java/util/ArrayList/<init>()V");

                    if (ci.arguments != null) {
                        for (ExpressionNode argExpr : ci.arguments) {
                            emit("    dup");               // ..., list, list
                            generateExpression(argExpr);   // ..., list, list, elem
                            emit("    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z");
                            emit("    pop");
                        }
                    }
                    return;
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

        if (expr instanceof MethodInvocationNode methodInvocationNode) {
            generateMethodCallOnObject(new ThisNode(), methodInvocationNode);
            return;
        }

        // fallback
        emit("    aconst_null");
    }

    /**
     * Generates bytecode for an object field access: {@code target.fieldName}.
     * 
     * Attempts to infer the static type of {@code target} to get the correct
     * owner and descriptor; falls back to {@code Object} if unknown.
     *
     * @param target    expression that evaluates to the object
     * @param fieldName name of the field being accessed
     */
    private void generateObjectFieldAccess(ExpressionNode target, String fieldName) {
        // 1. Push objectref
        generateExpression(target);

        // 2. Try to infer object type
        String ownerInternal = "java/lang/Object";
        String desc = "Ljava/lang/Object;";

        String typeName = null;

        if (target instanceof IdentifierNode id) {
            // local variable: look up in localVarTypes
            if (localVarTypes != null) {
                typeName = localVarTypes.get(id.name); 
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
                    ownerInternal = ci.name.replace('.', '/');
                    desc = f.descriptor;
                }
            }
        }

        // 3. Emit getfield
        emit("    getfield " + ownerInternal + "/" + fieldName + " " + desc);
    }

    /**
     * Generates a method call on an arbitrary expression, such as {@code a.Method(...)}.
     * 
     * This method recognizes several built-in methods for core types
     * ({@code Integer}, {@code Real}, {@code Boolean}, collections) and translates
     * them directly to JVM instructions. For other methods, it falls back to
     * {@link #invokeMethodOnObject(ExpressionNode, MethodInvocationNode)}.
     *
     * @param targetExpr target object expression
     * @param call       method invocation AST node
     */
    private void generateMethodCallOnObject(ExpressionNode targetExpr, MethodInvocationNode call) {
        String methodName = call.methodName;

        ExpressionNode arg = (call.arguments != null && !call.arguments.isEmpty())
        ? call.arguments.get(0)
        : null;

        if (isListExpr(targetExpr)) {
            if ("append".equals(methodName)) {
                generateListAppend(targetExpr, arg);
                return;
            }
            if ("head".equals(methodName)) {
                generateListHead(targetExpr);
                return;
            }
            if ("tail".equals(methodName)) {
                generateListTail(targetExpr);
                return;
            }
        }

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

        boolean hasResult = invokeMethodOnObject(targetExpr, call);
        if (hasResult) {
            // for standalone statement calls, the result may be POP'ed
        }
    }

    // =======================
    // Conversions Integer / Real / Boolean
    // =======================

    /**
     * Generates code to convert {@code Integer} object to boxed {@code Double}.
     * <p>
     * Stack before: {@code [..., obj]} where {@code obj} is a {@code java/lang/Integer}.<br>
     * Stack after: {@code [..., java/lang/Double]}.
     *
     * @param target expression yielding an {@code Integer}
     */
    private void generateIntegerToReal(ExpressionNode target) {
        // this : Integer -> Double
        generateExpression(target);
        emit("    checkcast java/lang/Integer");
        emit("    invokevirtual java/lang/Integer/intValue()I");
        emit("    i2d");
        emitBoxDouble(); // -> java/lang/Double
    }

    /**
     * Generates code to convert {@code Integer} object into boxed {@code Boolean}
     * using rule: 0 → false, non-zero → true.
     *
     * @param target expression yielding an {@code Integer}
     */
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

    /**
     * Generates code to convert {@code Boolean} object into boxed {@code Integer}
     * using rule: false → 0, true → 1.
     *
     * @param target expression yielding a {@code Boolean}
     */
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

    /**
     * Generates code to convert {@code Real} (Double) into boxed {@code Boolean},
     * using rule: 0.0 → false, otherwise true.
     *
     * @param target expression yielding a numeric value
     */
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

    /**
     * Converts an expression of type {@code Boolean} (boxed) into a primitive
     * {@code boolean} ({@code Z} - primitive boolean) on the stack.
     *
     * @param expr expression representing a Boolean object
     */
    private void generateBooleanAsZ(ExpressionNode expr) {
        generateExpression(expr);
        emit("    checkcast java/lang/Boolean");
        emit("    invokevirtual java/lang/Boolean/booleanValue()Z");
    }

    /**
     * Generates boolean OR operation on two expressions and boxes the result.
     *
     * @param left  left operand expression (Boolean)
     * @param right right operand expression (Boolean); if {@code null}, 0 is used
     */
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

    /**
     * Generates boolean AND operation on two expressions and boxes the result.
     *
     * @param left  left operand expression (Boolean)
     * @param right right operand expression (Boolean); if {@code null}, 0 is used
     */
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

    /**
     * Generates boolean XOR operation and boxes the result.
     *
     * @param left  left operand expression (Boolean)
     * @param right right operand expression (Boolean); if {@code null}, 0 is used
     */
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

    /**
     * Generates boolean NOT operation and boxes the result.
     *
     * @param expr operand expression (Boolean)
     */
    private void generateBooleanNot(ExpressionNode expr) {
        generateBooleanAsZ(expr);
        emit("    iconst_1");
        emit("    ixor");       // инверсия бита 0/1
        emitBoxBoolean();
    }

    /**
     * Generates bytecode that converts an arbitrary value (either {@code Integer}
     * or {@code Double}) into a primitive double value on the stack.
     * <p>
     * Stack before: {@code [..., obj]} where {@code obj} is either {@code Integer} or {@code Double}.<br>
     * Stack after: {@code [..., d]} where {@code d} is {@code double}.
     *
     * @param expr expression yielding a numeric object
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

    /**
     * Converts a Real (Double) object into an {@code Integer} object by truncating.
     *
     * @param target expression yielding numeric value
     */
    private void generateRealToInteger(ExpressionNode target) {
        generateRealAsDouble(target);      // D
        emit("    d2i");                   // I
        emitBoxInteger();                  // -> java/lang/Integer
    }

    /**
     * Unary minus for real numbers ({@code -a}) with result boxed as {@code Double}.
     *
     * @param target expression yielding numeric value
     */
    private void generateRealUnaryMinus(ExpressionNode target) {
        generateRealAsDouble(target);      // D
        emit("    dneg");                 // D
        emitBoxDouble();                  // -> java/lang/Double
    }

    /**
     * Real addition: {@code left + right}, result boxed as {@code Double}.
     *
     * @param left  left operand
     * @param right right operand
     */
    private void generateRealPlus(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dadd");
        emitBoxDouble();
    }

    /**
     * Real subtraction: {@code left - right}, result boxed as {@code Double}.
     *
     * @param left  left operand
     * @param right right operand
     */
    private void generateRealMinus(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dsub");
        emitBoxDouble();
    }

    /**
     * Real multiplication: {@code left * right}, result boxed as {@code Double}.
     *
     * @param left  left operand
     * @param right right operand
     */
    private void generateRealMult(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    dmul");
        emitBoxDouble();
    }

    /**
     * Real division: {@code left / right}, result boxed as {@code Double}.
     *
     * @param left  left operand
     * @param right right operand
     */
    private void generateRealDiv(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    ddiv");
        emitBoxDouble();
    }

    /**
     * Real remainder: {@code left % right}, result boxed as {@code Double}.
     *
     * @param left  left operand
     * @param right right operand
     */
    private void generateRealRem(ExpressionNode left, ExpressionNode right) {
        generateRealAsDouble(left);
        generateRealAsDouble(right);
        emit("    drem");
        emitBoxDouble();
    }

    /**
     * Real less-than comparison: {@code left < right}, result boxed {@code Boolean}.
     *
     * @param left  left operand
     * @param right right operand
     */
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

    /**
     * Real less-than-or-equal comparison: {@code left <= right}, result boxed {@code Boolean}.
     *
     * @param left  left operand
     * @param right right operand
     */
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

    /**
     * Real greater-than comparison: {@code left > right}, result boxed {@code Boolean}.
     *
     * @param left  left operand
     * @param right right operand
     */
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

    /**
     * Real greater-than-or-equal comparison: {@code left >= right}, result boxed {@code Boolean}.
     *
     * @param left  left operand
     * @param right right operand
     */
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

    /**
     * Real equality comparison: {@code left == right}, result boxed {@code Boolean}.
     *
     * @param left  left operand
     * @param right right operand
     */
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

    // =======================
    // Integer operations
    // =======================

    /**
     * Integer greater-than comparison: {@code left > right}, result boxed {@code Boolean}.
     *
     * @param left  left operand
     * @param right right operand; if {@code null}, 0 is used
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
     * Integer less-than comparison: {@code left < right}, result boxed {@code Boolean}.
     *
     * @param left  left operand
     * @param right right operand; if {@code null}, 0 is used
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
     * Integer less-than-or-equal comparison: {@code left <= right}, result boxed {@code Boolean}.
     *
     * @param left  left operand
     * @param right right operand; if {@code null}, 0 is used
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
     * Integer greater-than-or-equal comparison: {@code left >= right}, result boxed {@code Boolean}.
     *
     * @param left  left operand
     * @param right right operand; if {@code null}, 0 is used
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
     * Integer equality comparison: {@code left == right}, result boxed {@code Boolean}.
     *
     * @param left  left operand
     * @param right right operand; if {@code null}, 0 is used
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
     * Integer addition: {@code left + right}, result boxed {@code Integer}.
     *
     * @param left  left operand
     * @param right right operand; if {@code null}, 0 is used
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
     * Integer subtraction: {@code left - right}, result boxed {@code Integer}.
     *
     * @param left  left operand
     * @param right right operand; if {@code null}, 0 is used
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
     * Integer multiplication: {@code left * right}, result boxed {@code Integer}.
     *
     * @param left  left operand
     * @param right right operand; if {@code null}, 1 is used
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
     * Integer division: {@code left / right}, result boxed {@code Integer}.
     *
     * @param left  left operand
     * @param right right operand; if {@code null}, 1 is used
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
     * Integer remainder: {@code left % right}, result boxed {@code Integer}.
     *
     * @param left  left operand
     * @param right right operand; if {@code null}, 1 is used (result is effectively 0)
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
     * Invokes a method on {@code this} object using hierarchical resolution
     * and overload selection.
     * <p>
     * This is used for method calls that appear as standalone statements
     * (e.g. {@code foo();}) where the implicit target is {@code this}.
     *
     * @param node method invocation AST node
     * @return {@code true} if method returns a value, {@code false} if void
     */
    private boolean invokeMethodOnThis(MethodInvocationNode node) {
        String methodName = node.methodName;

        // load this
        emit("    aload_0");

        // аргументы
        if (node.arguments != null) {
            for (ExpressionNode arg : node.arguments) {
                generateExpression(arg);
            }
        }

        String ownerInternal;
        String descriptor;
        boolean isVoid;

        ResolvedMethod rm = resolveMethodInHierarchy(currentClassName, methodName, node);

        if (rm != null) {
            ownerInternal = rm.ownerInternal;
            MethodDeclNode decl = rm.decl;
            descriptor = methodDescriptor(decl.header);
            isVoid = (decl.header.returnType == null);
        } else {
            ownerInternal = currentClassName.replace('.', '/');
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

        emit("    invokevirtual " + ownerInternal + "/" + methodName + descriptor);
        return !isVoid;
    }

    /**
     * Invokes a method on an arbitrary target object using hierarchical
     * resolution and overload selection.
     * 
     * This method generates:
     * 
     *     target object reference
     *     argument expressions
     *     virtual call to the best matching overload, or a fallback
     *         {@code Object}-typed descriptor
     * 
     *
     * @param targetExpr target expression (object)
     * @param node       method invocation node
     * @return {@code true} if method returns a value, {@code false} if void
     */
    private boolean invokeMethodOnObject(ExpressionNode targetExpr, MethodInvocationNode node) {
        String methodName = node.methodName;

        // 1) objectref
        generateExpression(targetExpr);

        // 2) аргументы
        if (node.arguments != null) {
            for (ExpressionNode arg : node.arguments) {
                generateExpression(arg);
            }
        }

        // 3) infer тип targetExpr
        String typeName = null;
        if (targetExpr instanceof IdentifierNode id && localVarTypes != null) {
            typeName = localVarTypes.get(id.name);
        } else if (targetExpr instanceof ThisNode) {
            typeName = currentClassName;
        }

        String ownerInternal;
        String descriptor;
        boolean isVoid;

        ResolvedMethod rm = null;
        if (typeName != null) {
            rm = resolveMethodInHierarchy(typeName, methodName, node);
        }

        if (rm != null) {
            ownerInternal = rm.ownerInternal;
            MethodDeclNode decl = rm.decl;
            descriptor = methodDescriptor(decl.header);
            isVoid = (decl.header.returnType == null);
        } else {
            if (typeName != null && classInfoMap.containsKey(typeName)) {
                ownerInternal = typeName.replace('.', '/');
            } else {
                ownerInternal = currentClassName.replace('.', '/');
            }

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

        emit("    invokevirtual " + ownerInternal + "/" + methodName + descriptor);
        return !isVoid;
    }


    // -------------------------
    // Helpers: descriptors & boxing
    // -------------------------

    /**
     * Computes a JVM descriptor string for a type AST node.
     * 
     * Built-in mapping:
     * 
     *     {@code Integer → Ljava/lang/Integer;}
     *     {@code Real → Ljava/lang/Double;}
     *     {@code Boolean → Ljava/lang/Boolean;}
     *     {@code List/Array → Ljava/util/ArrayList;}
     *     other → {@code Ljava/lang/Object;}
     * 
     *
     * @param typeNode type AST node
     * @return JVM descriptor for the type
     */
    private String descriptorForTypeNode(ASTNode typeNode) {
        if (typeNode == null) {
            return "Ljava/lang/Object;";
        }
        if (typeNode instanceof TypeNode) {
            String n = ((TypeNode) typeNode).name;
            switch (n) {
                case "Integer": return "Ljava/lang/Integer;";
                case "Real":    return "Ljava/lang/Double;";
                case "Boolean": return "Ljava/lang/Boolean;";
                case "List":    return "Ljava/util/ArrayList;";
                case "Array":   return "Ljava/util/ArrayList;";
                default:
                    // ВСЕ остальные — это либо пользовательские классы (Test, Main),
                    // либо что-то ещё объектное → генерим LName;
                    return "L" + n.replace('.', '/') + ";";
            }
        }
        if (typeNode instanceof GenericTypeNode) {
            GenericTypeNode g = (GenericTypeNode) typeNode;
            switch (g.baseType) {
                case "List":  return "Ljava/util/ArrayList;";
                case "Array": return "Ljava/util/ArrayList;";
                default:
                    return "L" + g.baseType.replace('.', '/') + ";";
            }
        }
        return "Ljava/lang/Object;";
    }

    /**
     * Emits boxing call to {@code Integer.valueOf(I)}.
     */
    private void emitBoxInteger() {
        emit("    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;");
    }

    /**
     * Emits boxing call to {@code Double.valueOf(D)}.
     */
    private void emitBoxDouble() {
        emit("    invokestatic java/lang/Double/valueOf(D)Ljava/lang/Double;");
    }

    /**
     * Emits boxing call to {@code Boolean.valueOf(Z)}.
     */
    private void emitBoxBoolean() {
        emit("    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;");
    }


    /**
     * Visits a variable declaration, generates code to evaluate its initializer
     * and store it into a new local slot.
     * 
     * Also updates {@link #localVarTypes} with the inferred type of the variable.
     *
     * @param node variable declaration node
     */
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

    /**
     * Returns a high-level type name (e.g. {@code "Integer"}, {@code "Real"})
     * corresponding to the given type AST node.
     *
     * @param typeNode type AST node
     * @return type name used by the language, or {@code null} if unknown
     */
    private String typeNameForTypeNode(ASTNode typeNode) {
        if (typeNode instanceof TypeNode t) {
            return t.name;
        }
        if (typeNode instanceof GenericTypeNode g) {
            return g.baseType;
        }
        if (typeNode instanceof ConstructorInvocationNode ci) {
            return ci.className;
        }
        return null;
    }

    // =======================
    // METHOD DECLARATION
    // =======================

    /**
     * Current index for the next local variable slot within a method or constructor.
     */
    private int currentLocalIndex;

    /**
     * Map from local variable name to its index in the local variable table.
     */
    private Map<String, Integer> localVars;

    /**
     * Emits Jasmin code for a method declaration.
     * <p>
     * This method:
     * <ul>
     *     <li>Emits the method header with JVM descriptor.</li>
     *     <li>Initializes the local variables context, including {@code this} and parameters.</li>
     *     <li>Generates code for arrow bodies or block bodies, and injects a default
     *         return if needed.</li>
     * </ul>
     *
     * @param node method declaration node
     */
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


    /**
     * Builds a JVM method descriptor for the given method header.
     * <p>
     * Parameters are mapped via {@link #descriptorForTypeNode(ASTNode)} and
     * the return type uses {@link #descriptorForTypeName(String)}. A missing
     * return type indicates {@code void}.
     *
     * @param header method header AST node
     * @return JVM method descriptor (e.g. {@code (Ljava/lang/Integer;)V})
     */
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

    /**
     * Computes a JVM descriptor string for a type name stored as plain string.
     *
     * @param name logical type name (e.g. {@code "Integer"}, {@code "Real"})
     * @return JVM descriptor string
     */
    private String descriptorForTypeName(String name) {
        switch (name) {
            case "Integer": return "Ljava/lang/Integer;";
            case "Real":    return "Ljava/lang/Double;";
            case "Boolean": return "Ljava/lang/Boolean;";
            case "List":    return "Ljava/util/ArrayList;";
            case "Array":   return "Ljava/util/ArrayList;";
            default:
                return "L" + name.replace('.', '/') + ";";
        }
    }

    /**
     * Builds a JVM constructor descriptor for a constructor declaration.
     *
     * @param ctor constructor declaration node
     * @return descriptor with {@code V} return type
     */
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

    /**
     * Searches for a constructor by the number of parameters in the given class.
     *
     * @param className name of the class
     * @param argCount  number of arguments
     * @return matching constructor declaration or {@code null} if none matches
     */
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

    /**
     * Checks whether an expression is known to represent a {@code Real} value.
     *
     * @param e expression
     * @return {@code true} if the expression is statically recognized as Real
     */
    private boolean isRealExpr(ExpressionNode e) {
        return "Real".equals(inferExprTypeName(e));
    }

    /**
     * Checks whether an expression is known to represent an {@code Integer} value.
     *
     * @param e expression
     * @return {@code true} if the expression is statically recognized as Integer
     */
    private boolean isIntegerExpr(ExpressionNode e) {
        return "Integer".equals(inferExprTypeName(e));
    }

    /**
     * Checks whether an expression is known to represent a {@code Boolean} value.
     *
     * @param e expression
     * @return {@code true} if the expression is statically recognized as Boolean
     */
    private boolean isBooleanExpr(ExpressionNode e) {
        return "Boolean".equals(inferExprTypeName(e));
    }

    /**
     * Determines whether a binary numeric operation should be treated as Real
     * based on its operands.
     *
     * @param target left operand
     * @param arg    right operand
     * @return {@code true} if either operand is Real
     */
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

     /**
     * Visits a block body node, sequentially visiting all contained elements.
     *
     * @param node body node
     */
    @Override
    public void visit(BodyNode node) {
        if (node == null || node.elements == null) return;
        for (BodyElementNode el : node.elements) {
            if (el instanceof ASTNode aSTNode) {
                aSTNode.accept(this);
            }
        }
    }

    /**
     * Generates code for assignment statements, including:
     *     Simple local assignment: {@code x = rhs;}
     *     Field assignment on {@code this}: {@code this.x = rhs;}
     *     Object field assignment: {@code obj.field = rhs;}
     *
     * @param node assignment AST node
     */
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
            // 1) objectref
            generateExpression(access.target);
            // 2) value
            generateExpression(node.right);

            // По умолчанию — Object, на всякий случай
            String ownerInternal = "java/lang/Object";
            String desc = "Ljava/lang/Object;";

            // Пытаемся вывести реальный тип target
            String typeName = null;

            if (access.target instanceof IdentifierNode tId) {
                // сначала смотрим в локальные переменные
                if (localVarTypes != null) {
                    typeName = localVarTypes.get(tId.name);  // например, "A"
                }

                // special case: this
                if (typeName == null && "this".equals(tId.name)) {
                    typeName = currentClassName;
                }
            } else if (access.target instanceof ThisNode) {
                typeName = currentClassName;
            }

            if (typeName != null) {
                ClassInfo ci = classInfoMap.get(typeName);
                if (ci != null) {
                    FieldInfo f = ci.fields.get(memberId.name);
                    if (f != null) {
                        ownerInternal = ci.name.replace('.', '/'); // A -> A
                        desc = f.descriptor;    
                    }
                }
            }

            emit("    putfield " + ownerInternal + "/" + memberId.name + " " + desc);
        }
    }

    /**
     * Generates code for a while loop:
     * where {@code condition} is expected to be a {@code Boolean} object.
     *
     * @param node while-loop AST node
     */
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

    /**
     * Generates code for an if statement with optional else:
     * where {@code condition} is expected to be a {@code Boolean} object.
     *
     * @param node if-statement AST node
     */
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

    /**
     * Generates code for a return statement. If the expression is null,
     * emits {@code return}; otherwise emits {@code areturn} of the expression.
     *
     * @param node return statement node
     */
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

    /**
     * Generates code for a {@code print} statement that calls
     * {@code System.out.println(Object)}.
     *
     * @param node print statement node
     */
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

    /**
     * Visits a method invocation used as a standalone statement.
     * 
     * It calls {@link #invokeMethodOnThis(MethodInvocationNode)} and discards the result
     * if the method is non-void.
     *
     * @param node method invocation node
     */
    @Override
    public void visit(MethodInvocationNode node) {
        boolean hasResult = invokeMethodOnThis(node);
        if (hasResult) {
            emit("    pop");
        }
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

    /**
     * Helper that visits a body node (block) sequentially, if not null.
     *
     * @param body body node
     */
    private void visitBody(BodyNode body) {
        if (body == null || body.elements == null) return;
        for (BodyElementNode elem : body.elements) {
            ((ASTNode) elem).accept(this);
        }
    }

    // -------------------------
    // Helpers: load/store locals
    // -------------------------

    /**
     * Emits a proper Jasmin instruction for loading a reference local variable
     * by its index (using short forms {@code aload_0..3} where possible).
     *
     * @param idx index of local variable
     */
    private void emitLoadVar(int idx) {
        if (idx >= 0 && idx <= 3) {
            emit("    aload_" + idx);
        } else {
            emit("    aload " + idx);
        }
    }

    /**
     * Emits a proper Jasmin instruction for storing a reference local variable
     * by its index (using short forms {@code astore_0..3} where possible).
     *
     * @param idx index of local variable
     */
    private void emitStoreVar(int idx) {
        if (idx >= 0 && idx <= 3) {
            emit("    astore_" + idx);
        } else {
            emit("    astore " + idx);
        }
    }

    /**
     * Infers a high-level type name from a variable initializer expression.
     *
     * @param init initializer expression
     * @return inferred type name (e.g. {@code "Integer"}, {@code "Real"}), or {@code null} if unknown
     */
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

    /**
     * Represents a resolved method along the inheritance chain: the class where
     * it was found and the declaration node.
     */
    private static class ResolvedMethod {
        /**
         * Internal JVM owner name for the class that declares the method.
         */
        final String ownerInternal;

        /**
         * The selected {@link MethodDeclNode}.
         */
        final MethodDeclNode decl;

        ResolvedMethod(String ownerInternal, MethodDeclNode decl) {
            this.ownerInternal = ownerInternal;
            this.decl = decl;
        }
    }

    // =======================
    // Method lookup with overload resolution
    // =======================

    /**
     * Resolves a method by name and argument types by walking up the inheritance
     * hierarchy starting from {@code className}.
     * <p>
     * Uses {@link #selectOverload(ClassInfo, String, MethodInvocationNode)} to
     * choose the best overload in each class.
     *
     * @param className starting class name
     * @param methodName method name
     * @param call       method invocation for which to resolve the target
     * @return {@link ResolvedMethod} or {@code null} if none found
     */
    private ResolvedMethod resolveMethodInHierarchy(String className,
                                                    String methodName,
                                                    MethodInvocationNode call) {
        String cur = className;
        while (cur != null) {
            ClassInfo ci = classInfoMap.get(cur);
            if (ci == null) {
                break;
            }

            // пробуем подобрать подходящую перегрузку в этом классе
            MethodDeclNode m = selectOverload(ci, methodName, call);
            if (m != null) {
                String ownerInternal = ci.name.replace('.', '/');
                return new ResolvedMethod(ownerInternal, m);
            }

            String superName = ci.superName;
            if (superName == null || "java/lang/Object".equals(superName)) {
                break;
            }
            cur = superName;
        }
        return null;
    }

    /**
     * Determines whether an expression is considered a list expression.
     * <p>
     * Recognizes:
     * <ul>
     *     <li>{@code List(...)} constructor invocations</li>
     *     <li>Identifiers whose inferred type is {@code "List"}</li>
     * </ul>
     *
     * @param e expression
     * @return {@code true} if expression is a list
     */
    private boolean isListExpr(ExpressionNode e) {
        if (e == null) return false;

        if (e instanceof ConstructorInvocationNode ci && "List".equals(ci.className)) {
            return true;
        }

        if (e instanceof IdentifierNode id && localVarTypes != null) {
            String t = localVarTypes.get(id.name); // "List", "Integer", ...
            if ("List".equals(t)) return true;
        }

        return false;
    }

    /**
     * Generates code for {@code list.append(elem)} semantics:
     * <pre>
     * list.append(x)  →  list.add(x); return list;
     * </pre>
     *
     * @param listExpr list expression
     * @param elemExpr element expression
     */
    private void generateListAppend(ExpressionNode listExpr, ExpressionNode elemExpr) {
        // list на стек
        generateExpression(listExpr);                      // ..., list
        emit("    dup");                                   // ..., list, list

        // элемент
        if (elemExpr != null) {
            generateExpression(elemExpr);                  // ..., list, list, elem
        } else {
            emit("    aconst_null");     
        }

        // вызов add(Object)Z
        emit("    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z");
        emit("    pop");            

    }

    /**
     * Generates code for {@code list.head()} which returns the first element.
     *
     * @param listExpr list expression
     */
    private void generateListHead(ExpressionNode listExpr) {
        generateExpression(listExpr);                      // ..., list
        emit("    iconst_0");
        emit("    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;");
    }

    /**
     * Generates code for {@code list.tail()} which returns a new list containing
     * all elements except the first.
     * <p>
     * Implementation uses:
     * <pre>
     * list.tail() ≈ new ArrayList(list.subList(1, list.size()))
     * </pre>
     *
     * @param listExpr list expression
     */
    private void generateListTail(ExpressionNode listExpr) {
        // tail: новый список с элементами [1..size-1]
        // new ArrayList( list.subList(1, list.size()) )

        // шаг 1: получить subList(1, size)
        generateExpression(listExpr);                      // ..., list
        emit("    dup");                                   // ..., list, list
        emit("    invokevirtual java/util/ArrayList/size()I"); // ..., list, size
        emit("    iconst_1");                              // ..., list, size, 1
        emit("    swap");                                  // ..., list, 1, size
        emit("    invokevirtual java/util/ArrayList/subList(II)Ljava/util/List;");
        // сейчас на стеке: ..., subList

        // шаг 2: сохранить subList во временный локал
        int tmp = currentLocalIndex++;
        emitStoreVar(tmp);

        // шаг 3: new ArrayList(subList)
        emit("    new java/util/ArrayList");
        emit("    dup");
        emitLoadVar(tmp);                                  // ..., newArr, newArr, subList
        emit("    invokespecial java/util/ArrayList/<init>(Ljava/util/Collection;)V");
        // результат на стеке: новый ArrayList — это и будет tail
    }

    /**
     * Selects the best overload for a method given a {@link ClassInfo} and a
     * method call.
     * 
     * Current strategy:
     * 
     *     Match by parameter count.
     *     For single-argument methods, try to match the argument's inferred
     *         type exactly.
     *     Otherwise, return the first viable candidate.
     *
     * @param ci         class info
     * @param methodName name of method
     * @param call       method invocation node
     * @return a matching method declaration or {@code null} if none found
     */
    private MethodDeclNode selectOverload(ClassInfo ci, String methodName, MethodInvocationNode call) {
        if (ci == null) return null;

        java.util.List<MethodDeclNode> list = ci.methods.get(methodName);
        if (list == null || list.isEmpty()) return null;

        int argCount = (call.arguments == null ? 0 : call.arguments.size());

        String argType = null;
        if (argCount == 1) {
            argType = inferExprTypeName(call.arguments.get(0));
        }

        MethodDeclNode best = null;

        for (MethodDeclNode m : list) {
            int paramCount = (m.header.parameters == null ? 0 : m.header.parameters.size());
            if (paramCount != argCount) continue;

            if (argCount == 0) {
                return m; 
            }

            if (argCount == 1) {
                String paramType = typeNameForTypeNode(m.header.parameters.get(0).paramType);
                if (argType != null && argType.equals(paramType)) {
                    return m;
                }
                if (best == null) best = m;
            }
        }

        return (best != null ? best : list.get(0));
    }

    /**
     * Tries to infer a logical type name for an expression
     *
     * @param e expression node
     * @return inferred logical type name or {@code null} if unknown
     */
    private String inferExprTypeName(ExpressionNode e) {
        if (e == null) return null;

        if (e instanceof ConstructorInvocationNode ci) {
            return ci.className;               // Integer(...), Real(...), Boolean(...)
        }
        if (e instanceof IntLiteralNode)  return "Integer";
        if (e instanceof RealLiteralNode) return "Real";
        if (e instanceof BoolLiteralNode) return "Boolean";

        if (e instanceof IdentifierNode id) {
            if (localVarTypes != null) {
                String t = localVarTypes.get(id.name);
                if (t != null) return t;
            }
            ClassInfo info = classInfoMap.get(currentClassName);
            if (info != null) {
                FieldInfo f = info.fields.get(id.name);
                if (f != null) {
                    if ("Ljava/lang/Integer;".equals(f.descriptor)) return "Integer";
                    if ("Ljava/lang/Double;".equals(f.descriptor))  return "Real";
                    if ("Ljava/lang/Boolean;".equals(f.descriptor)) return "Boolean";
                    // пользовательский класс:
                    return info.name;
                }
            }
            return null;
        }

        // <<< НОВОЕ: поле obj.field >>>
        if (e instanceof MemberAccessNode ma && ma.member instanceof IdentifierNode fieldId) {
            String ownerType = inferExprTypeName(ma.target); // тип obj
            if (ownerType == null) return null;
            ClassInfo ci = classInfoMap.get(ownerType);
            if (ci == null) return null;
            FieldInfo f = ci.fields.get(fieldId.name);
            if (f == null) return null;

            // по descriptor поля решаем тип
            if ("Ljava/lang/Integer;".equals(f.descriptor)) return "Integer";
            if ("Ljava/lang/Double;".equals(f.descriptor))  return "Real";
            if ("Ljava/lang/Boolean;".equals(f.descriptor)) return "Boolean";
            // пользовательский класс
            return ci.name;
        }

        return null;
    }

}
