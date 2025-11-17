package org.example.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Generates Jasmin assembly from the parsed AST.
 */
public class CodeGenerator implements ASTVisitor {

    private static final int STACK_LIMIT = 128;
    private static final int LOCALS_LIMIT = 128;

    private static final Map<String, String> BUILTIN_RUNTIME = Map.of(
            "Integer", "java/lang/Integer",
            "Real", "java/lang/Double",
            "Boolean", "java/lang/Boolean",
            "List", "java/util/ArrayList",
            "Array", "java/util/ArrayList"
    );

    private static final TypeInfo OBJECT_TYPE = TypeInfo.simple("java/lang/Object");
    private static final TypeInfo VOID_TYPE = TypeInfo.simple("void");
    private static final TypeInfo INTEGER_TYPE = TypeInfo.simple("Integer");
    private static final TypeInfo REAL_TYPE = TypeInfo.simple("Real");
    private static final TypeInfo BOOLEAN_TYPE = TypeInfo.simple("Boolean");

    private final String outputDir;
    private final Map<String, ClassInfo> classInfoMap = new LinkedHashMap<>();
    private final Map<String, ClassDeclNode> classAstNodes = new HashMap<>();
    private final Deque<MethodContext> methodStack = new ArrayDeque<>();
    private final Deque<Map<String, VarInfo>> localVarScopes = new ArrayDeque<>();

    private StringBuilder output;
    private String currentClassName;
    private ClassInfo currentClassInfo;
    private int labelCounter = 0;
    
    public CodeGenerator(String outputDir) {
        this.outputDir = outputDir;
        registerBuiltinClasses();
    }

    @Override
    public void visit(ProgramNode node) {
        if (node == null || node.classes == null) {
            return;
        }

        for (ClassDeclNode classDecl : node.classes) {
            registerClass(classDecl);
        }

        for (ClassDeclNode classDecl : node.classes) {
            generateClass(classDecl);
        }
    }
    
    private void registerBuiltinClasses() {
        ClassInfo objectInfo = new ClassInfo("java/lang/Object", null, true);
        classInfoMap.put(objectInfo.name, objectInfo);

        ClassInfo classInfo = new ClassInfo("Class", "java/lang/Object", true);
        classInfoMap.put(classInfo.name, classInfo);

        ClassInfo anyValue = new ClassInfo("AnyValue", "Class", true);
        classInfoMap.put(anyValue.name, anyValue);

        ClassInfo anyRef = new ClassInfo("AnyRef", "Class", true);
        classInfoMap.put(anyRef.name, anyRef);

        ClassInfo integerInfo = new ClassInfo("Integer", "AnyValue", true);
        addBuiltinIntegerMethods(integerInfo);
        classInfoMap.put(integerInfo.name, integerInfo);

        ClassInfo realInfo = new ClassInfo("Real", "AnyValue", true);
        addBuiltinRealMethods(realInfo);
        classInfoMap.put(realInfo.name, realInfo);

        ClassInfo booleanInfo = new ClassInfo("Boolean", "AnyValue", true);
        addBuiltinBooleanMethods(booleanInfo);
        classInfoMap.put(booleanInfo.name, booleanInfo);

        ClassInfo listInfo = new ClassInfo("List", "AnyRef", true);
        addBuiltinListMethods(listInfo);
        classInfoMap.put(listInfo.name, listInfo);

        ClassInfo arrayInfo = new ClassInfo("Array", "AnyRef", true);
        addBuiltinArrayMethods(arrayInfo);
        classInfoMap.put(arrayInfo.name, arrayInfo);
    }

    private void addBuiltinIntegerMethods(ClassInfo info) {
        info.addMethod(new MethodSignature("Plus",
                List.of(INTEGER_TYPE), INTEGER_TYPE, true));
        info.addMethod(new MethodSignature("Plus",
                List.of(REAL_TYPE), REAL_TYPE, true));
        info.addMethod(new MethodSignature("Minus",
                List.of(INTEGER_TYPE), INTEGER_TYPE, true));
        info.addMethod(new MethodSignature("Minus",
                List.of(REAL_TYPE), REAL_TYPE, true));
        info.addMethod(new MethodSignature("Mult",
                List.of(INTEGER_TYPE), INTEGER_TYPE, true));
        info.addMethod(new MethodSignature("Mult",
                List.of(REAL_TYPE), REAL_TYPE, true));
        info.addMethod(new MethodSignature("Div",
                List.of(INTEGER_TYPE), INTEGER_TYPE, true));
        info.addMethod(new MethodSignature("Div",
                List.of(REAL_TYPE), REAL_TYPE, true));
        info.addMethod(new MethodSignature("Rem",
                List.of(INTEGER_TYPE), INTEGER_TYPE, true));
        info.addMethod(new MethodSignature("Less",
                List.of(INTEGER_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("Less",
                List.of(REAL_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("LessEqual",
                List.of(INTEGER_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("LessEqual",
                List.of(REAL_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("Greater",
                List.of(INTEGER_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("GreaterEqual",
                List.of(INTEGER_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("Equal",
                List.of(INTEGER_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("toReal",
                List.of(), REAL_TYPE, true));
    }

    private void addBuiltinRealMethods(ClassInfo info) {
        info.addMethod(new MethodSignature("Plus",
                List.of(REAL_TYPE), REAL_TYPE, true));
        info.addMethod(new MethodSignature("Plus",
                List.of(INTEGER_TYPE), REAL_TYPE, true));
        info.addMethod(new MethodSignature("Minus",
                List.of(REAL_TYPE), REAL_TYPE, true));
        info.addMethod(new MethodSignature("Mult",
                List.of(REAL_TYPE), REAL_TYPE, true));
        info.addMethod(new MethodSignature("Div",
                List.of(REAL_TYPE), REAL_TYPE, true));
        info.addMethod(new MethodSignature("Less",
                List.of(REAL_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("LessEqual",
                List.of(REAL_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("Greater",
                List.of(REAL_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("GreaterEqual",
                List.of(REAL_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("Equal",
                List.of(REAL_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("toInteger",
                List.of(), INTEGER_TYPE, true));
    }

    private void addBuiltinBooleanMethods(ClassInfo info) {
        info.addMethod(new MethodSignature("And",
                List.of(BOOLEAN_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("Or",
                List.of(BOOLEAN_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("Xor",
                List.of(BOOLEAN_TYPE), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("Not",
                List.of(), BOOLEAN_TYPE, true));
        info.addMethod(new MethodSignature("toInteger",
                List.of(), INTEGER_TYPE, true));
    }

    private void addBuiltinListMethods(ClassInfo info) {
        TypeInfo typeParam = TypeInfo.typeParameter("T");
        info.addMethod(new MethodSignature("append",
                List.of(typeParam), TypeInfo.generic("List", typeParam), true));
        info.addMethod(new MethodSignature("head",
                List.of(), typeParam, true));
        info.addMethod(new MethodSignature("tail",
                List.of(), TypeInfo.generic("List", typeParam), true));
        info.addMethod(new MethodSignature("Length",
                List.of(), INTEGER_TYPE, true));
    }

    private void addBuiltinArrayMethods(ClassInfo info) {
        TypeInfo typeParam = TypeInfo.typeParameter("T");
        info.addMethod(new MethodSignature("Length",
                List.of(), INTEGER_TYPE, true));
        info.addMethod(new MethodSignature("get",
                List.of(INTEGER_TYPE), typeParam, true));
        info.addMethod(new MethodSignature("set",
                List.of(INTEGER_TYPE, typeParam), typeParam, true));
        info.addMethod(new MethodSignature("toList",
                List.of(), TypeInfo.generic("List", typeParam), true));
    }

    private void registerClass(ClassDeclNode node) {
        if (node == null) {
            return;
        }
        String superName = node.extendsClass != null ? node.extendsClass : "java/lang/Object";
        ClassInfo info = new ClassInfo(node.className, superName, false);
        ClassInfo previousClassInfo = currentClassInfo;
        String previousClassName = currentClassName;
        currentClassName = node.className;
        currentClassInfo = info;

        if (node.members != null) {
            for (MemberNode member : node.members) {
                if (member instanceof VarDeclNode) {
                    VarDeclNode varDecl = (VarDeclNode) member;
                    TypeInfo fieldType = resolveType(varDecl.type);
                    if (fieldType == null && varDecl.initializer != null) {
                        fieldType = inferExpressionType(varDecl.initializer, null);
                    }
                    if (fieldType == null) {
                        fieldType = OBJECT_TYPE;
                    }
                    FieldInfo fieldInfo = new FieldInfo(varDecl.varName, fieldType, varDecl.initializer);
                    info.addField(fieldInfo);
                } else if (member instanceof MethodDeclNode) {
                    MethodDeclNode methodDecl = (MethodDeclNode) member;
                    MethodSignature signature = createSignatureForMethod(methodDecl);
                    info.addMethod(signature);
                } else if (member instanceof ConstructorDeclNode) {
                    ConstructorDeclNode constructorDecl = (ConstructorDeclNode) member;
                    ConstructorSignature signature = createSignatureForConstructor(constructorDecl);
                    info.addConstructor(signature);
                }
            }
        }

        if (info.constructors.isEmpty()) {
            info.addConstructor(new ConstructorSignature(Collections.emptyList(), false));
        }

        classInfoMap.put(info.name, info);
        classAstNodes.put(info.name, node);

        currentClassName = previousClassName;
        currentClassInfo = previousClassInfo;
    }

    private MethodSignature createSignatureForMethod(MethodDeclNode methodDecl) {
        List<TypeInfo> params = resolveParameterTypes(methodDecl.header.parameters);
        TypeInfo returnType = resolveReturnType(methodDecl.header.returnType);
        return new MethodSignature(methodDecl.header.methodName, params, returnType, false);
    }

    private ConstructorSignature createSignatureForConstructor(ConstructorDeclNode constructorDecl) {
        List<TypeInfo> params = resolveParameterTypes(constructorDecl.parameters);
        return new ConstructorSignature(params, false);
    }

    private List<TypeInfo> resolveParameterTypes(List<ParamDeclNode> params) {
        if (params == null) {
            return Collections.emptyList();
        }
        List<TypeInfo> types = new ArrayList<>();
        for (ParamDeclNode param : params) {
            TypeInfo type = resolveType(param.paramType);
            if (type == null) {
                type = OBJECT_TYPE;
            }
            types.add(type);
        }
        return types;
    }

    private TypeInfo resolveReturnType(String returnType) {
        if (returnType == null) {
            return VOID_TYPE;
        }
        return resolveType(new TypeNode(returnType));
    }

    private TypeInfo resolveType(ASTNode typeNode) {
        if (typeNode == null) {
            return null;
        }
        if (typeNode instanceof TypeNode) {
            String name = ((TypeNode) typeNode).name;
            return TypeInfo.simple(name);
        }
        if (typeNode instanceof GenericTypeNode) {
            GenericTypeNode gtn = (GenericTypeNode) typeNode;
            TypeInfo param = resolveType(gtn.parameter);
            return TypeInfo.generic(gtn.baseType, param);
        }
        return null;
    }

    private void generateClass(ClassDeclNode node) {
        if (node == null) {
            return;
        }
        output = new StringBuilder();
        currentClassName = node.className;
        currentClassInfo = classInfoMap.get(currentClassName);
        if (currentClassInfo == null) {
            return;
        }

        emit(".class public " + currentClassName);
        emit(".super " + internalName(currentClassInfo.superName));
        emit("");

        emitFields(currentClassInfo);
        emit("");

        List<ConstructorDeclNode> constructors = new ArrayList<>();
        List<MethodDeclNode> methods = new ArrayList<>();
        if (node.members != null) {
            for (MemberNode member : node.members) {
                if (member instanceof ConstructorDeclNode) {
                    constructors.add((ConstructorDeclNode) member);
                } else if (member instanceof MethodDeclNode) {
                    methods.add((MethodDeclNode) member);
                }
            }
        }

        if (constructors.isEmpty()) {
            generateDefaultConstructor();
        } else {
            for (ConstructorDeclNode constructor : constructors) {
                generateConstructor(constructor);
            }
        }

        for (MethodDeclNode method : methods) {
            generateMethod(method);
        }

        maybeGenerateProgramEntry(methods);
        saveJasminFile(currentClassName);
    }

    private void emitFields(ClassInfo info) {
        for (FieldInfo field : info.orderedFields) {
            emit(".field public " + field.name + " " + descriptorForType(field.type));
        }
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

    private void maybeGenerateProgramEntry(List<MethodDeclNode> methods) {
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
    
    private void registerParameters(List<ParamDeclNode> params, List<TypeInfo> paramTypes, MethodContext ctx) {
        if (params == null) {
            return;
        }
        for (int i = 0; i < params.size(); i++) {
            ParamDeclNode param = params.get(i);
            TypeInfo type = paramTypes.get(i);
            VarInfo varInfo = new VarInfo(param.paramName, type, ctx.nextLocalIndex++);
            localVarScopes.peek().put(param.paramName, varInfo);
        }
    }
    
    private void emitFieldInitializers() {
        for (FieldInfo field : currentClassInfo.orderedFields) {
            if (field.initializer == null) {
                continue;
            }
            emit("    aload_0");
            TypeInfo valueType = generateExpression(field.initializer);
            emit("    putfield " + internalName(fieldOwner(field.name)) + "/" + field.name + " " + descriptorForType(field.type));
        }
    }

    private String fieldOwner(String fieldName) {
        ClassInfo info = currentClassInfo;
        while (info != null) {
            if (info.fields.containsKey(fieldName)) {
                return info.name;
            }
            info = classInfoMap.get(info.superName);
        }
        return currentClassName;
    }

    private void emitLimits() {
        emit("    .limit stack " + STACK_LIMIT);
        emit("    .limit locals " + LOCALS_LIMIT);
    }

    private void emit(String line) {
        output.append(line).append("\n");
    }

    private String internalName(String name) {
        if (name == null) {
            return "java/lang/Object";
        }
        return name.replace('.', '/');
    }

    private String descriptorForType(TypeInfo type) {
        if (type == null) {
            return "Ljava/lang/Object;";
        }
        if (type.isTypeParameter) {
            return "Ljava/lang/Object;";
        }
        if (type.isVoid()) {
            return "V";
        }
        String runtime = BUILTIN_RUNTIME.getOrDefault(type.baseName, type.baseName);
        return "L" + runtime + ";";
    }

    private String buildDescriptor(List<TypeInfo> params, TypeInfo returnType) {
        StringBuilder sb = new StringBuilder("(");
        for (TypeInfo type : params) {
            sb.append(descriptorForType(type));
        }
        sb.append(")");
        sb.append(descriptorForType(returnType));
        return sb.toString();
    }

    @Override
    public void visit(ClassDeclNode node) {
        // handled in visit(ProgramNode)
    }
    
    @Override
    public void visit(MethodDeclNode node) {
        // handled manually
    }

    @Override
    public void visit(VarDeclNode node) {
        handleLocalVarDecl(node);
    }
    
    @Override
    public void visit(MethodHeaderNode node) {
        // Обрабатывается в visit(MethodDeclNode)
    }
    
    @Override
    public void visit(MethodBodyNode node) {
        // handled manually
    }
    
    @Override
    public void visit(ParamDeclNode node) {
        // handled elsewhere
    }
    
    @Override
    public void visit(ConstructorDeclNode node) {
        // handled manually
    }
    
    @Override
    public void visit(BodyNode node) {
        if (node == null || node.elements == null) {
            return;
        }
        for (BodyElementNode element : node.elements) {
            if (element instanceof ASTNode) {
                ((ASTNode) element).accept(this);
            }
        }
    }

    private void handleLocalVarDecl(VarDeclNode node) {
        if (methodStack.isEmpty()) {
            return;
        }
        TypeInfo type = resolveType(node.type);
        if (type == null && node.initializer != null) {
            type = inferExpressionType(node.initializer, methodStack.peek());
        }
        if (type == null) {
            type = OBJECT_TYPE;
        }
        VarInfo varInfo = new VarInfo(node.varName, type, methodStack.peek().nextLocalIndex++);
        localVarScopes.peek().put(node.varName, varInfo);
        if (node.initializer != null) {
            TypeInfo exprType = generateExpression(node.initializer);
            emitStoreVar(exprType, varInfo.index);
        }
    }
    
    @Override
    public void visit(AssignmentNode node) {
        if (node.right == null) {
            return;
        }
        TypeInfo valueType = generateExpression(node.right);
        
        if (node.left instanceof IdentifierNode) {
            IdentifierNode id = (IdentifierNode) node.left;
            VarInfo local = lookupLocalVar(id.name);
            if (local != null) {
                emitStoreVar(valueType, local.index);
                return;
            }
            FieldBinding binding = resolveFieldBinding(currentClassName, id.name);
            if (binding != null) {
                emit("    aload_0");
                emit("    swap");
                emit("    putfield " + internalName(binding.ownerName) + "/" + binding.field.name + " " + descriptorForType(binding.field.type));
            }
            return;
        }

        if (node.left instanceof MemberAccessNode) {
            MemberAccessNode access = (MemberAccessNode) node.left;
            if (!(access.member instanceof IdentifierNode)) {
                return;
            }
            TypeInfo targetType = generateExpression(access.target);
            emit("    swap");
            IdentifierNode member = (IdentifierNode) access.member;
            FieldBinding binding = resolveFieldBinding(targetType.baseName, member.name);
            if (binding != null) {
                emit("    putfield " + internalName(binding.ownerName) + "/" + binding.field.name + " " + descriptorForType(binding.field.type));
            }
        }
    }
    
    @Override
    public void visit(WhileLoopNode node) {
        String start = newLabel();
        String body = newLabel();
        String end = newLabel();

        emit(start + ":");
        emitCondition(node.condition, body, end);
        emit(body + ":");
        if (node.body != null) {
            node.body.accept(this);
        }
        emit("    goto " + start);
        emit(end + ":");
    }
    
    @Override
    public void visit(IfStatementNode node) {
        String thenLabel = newLabel();
        String elseLabel = newLabel();
        String endLabel = newLabel();

        emitCondition(node.condition, thenLabel, elseLabel);
        emit(thenLabel + ":");
        if (node.thenBody != null) {
            node.thenBody.accept(this);
        }
        emit("    goto " + endLabel);
        emit(elseLabel + ":");
        if (node.elseBody != null) {
            node.elseBody.accept(this);
        }
        emit(endLabel + ":");
    }
    
    @Override
    public void visit(ReturnNode node) {
        MethodContext ctx = methodStack.peek();
        if (node.expression == null) {
            emit("    return");
            return;
        }
        TypeInfo exprType = generateExpression(node.expression);
        emitReturn(exprType, ctx.returnType);
    }
    
    @Override
    public void visit(PrintNode node) {
        emit("    getstatic java/lang/System/out Ljava/io/PrintStream;");
        generateExpression(node.expression);
        emit("    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V");
    }
    
    @Override
    public void visit(MemberAccessNode node) {
        if (node == null) {
            return;
        }
        TypeInfo targetType = generateExpression(node.target);
        if (node.member instanceof IdentifierNode) {
            IdentifierNode fieldName = (IdentifierNode) node.member;
            FieldBinding binding = resolveFieldBinding(targetType.baseName, fieldName.name);
            if (binding != null) {
                emit("    getfield " + internalName(binding.ownerName) + "/" + binding.field.name + " " + descriptorForType(binding.field.type));
            }
            return;
        }
        if (node.member instanceof MethodInvocationNode) {
            emitMethodInvocationForStackTarget(targetType, (MethodInvocationNode) node.member);
        }
    }
    
    private TypeInfo emitMethodInvocationForStackTarget(TypeInfo targetType, MethodInvocationNode methodNode) {
        TypeInfo builtin = tryEmitBuiltinCall(targetType, methodNode);
        if (builtin != null) {
            return builtin;
        }

        List<TypeInfo> argTypes = new ArrayList<>();
        if (methodNode.arguments != null) {
            for (ExpressionNode arg : methodNode.arguments) {
                argTypes.add(generateExpression(arg));
            }
        }
        String methodName = extractMethodName(methodNode);
        MethodSignature signature = resolveMethod(targetType.baseName, methodName, argTypes);
        String descriptor = signature != null ? signature.descriptor() : buildGenericDescriptor(argTypes.size());
        emit("    invokevirtual " + internalName(targetType.baseName) + "/" + methodName + descriptor);
        return signature != null ? applyTypeParameters(signature.returnType, targetType) : OBJECT_TYPE;
    }

    private String buildGenericDescriptor(int argCount) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < argCount; i++) {
            sb.append("Ljava/lang/Object;");
        }
        sb.append(")Ljava/lang/Object;");
        return sb.toString();
    }

    private TypeInfo tryEmitBuiltinCall(TypeInfo targetType, MethodInvocationNode methodNode) {
        if (targetType == null) {
            return null;
        }
        switch (targetType.baseName) {
            case "Integer":
                return emitIntegerBuiltin(methodNode);
            case "Real":
                return emitRealBuiltin(methodNode);
            case "Boolean":
                return emitBooleanBuiltin(methodNode);
            case "List":
                return emitListBuiltin(targetType, methodNode);
            default:
                return null;
        }
    }

    private TypeInfo emitIntegerBuiltin(MethodInvocationNode methodNode) {
        String methodName = extractMethodName(methodNode);
        ExpressionNode arg = methodNode.arguments != null && !methodNode.arguments.isEmpty()
                ? methodNode.arguments.get(0) : null;
        switch (methodName) {
            case "Plus":
                return emitNumericBinaryOp(arg, true, "iadd", "dadd");
            case "Minus":
                return emitNumericBinaryOp(arg, true, "isub", "dsub");
            case "Mult":
                return emitNumericBinaryOp(arg, true, "imul", "dmul");
            case "Div":
                return emitNumericBinaryOp(arg, true, "idiv", "ddiv");
            case "Rem":
                return emitIntegerRemainder(arg);
            case "Less":
                return emitComparison(arg, "if_icmplt", "dcmpl", ComparisonOp.LT);
            case "LessEqual":
                return emitComparison(arg, "if_icmple", "dcmpl", ComparisonOp.LE);
            case "Greater":
                return emitComparison(arg, "if_icmpgt", "dcmpl", ComparisonOp.GT);
            case "GreaterEqual":
                return emitComparison(arg, "if_icmpge", "dcmpl", ComparisonOp.GE);
            case "Equal":
                return emitComparison(arg, "if_icmpeq", "dcmpg", ComparisonOp.EQ);
            case "toReal":
                emitUnboxToInt(INTEGER_TYPE);
                emit("    i2d");
                emitBoxDouble();
                return REAL_TYPE;
            default:
                return null;
        }
    }

    private TypeInfo emitRealBuiltin(MethodInvocationNode methodNode) {
        String methodName = extractMethodName(methodNode);
        ExpressionNode arg = methodNode.arguments != null && !methodNode.arguments.isEmpty()
                ? methodNode.arguments.get(0) : null;
        switch (methodName) {
            case "Plus":
                return emitNumericBinaryOp(arg, false, null, "dadd");
            case "Minus":
                return emitNumericBinaryOp(arg, false, null, "dsub");
            case "Mult":
                return emitNumericBinaryOp(arg, false, null, "dmul");
            case "Div":
                return emitNumericBinaryOp(arg, false, null, "ddiv");
            case "Less":
                return emitComparison(arg, null, "dcmpl", ComparisonOp.LT);
            case "LessEqual":
                return emitComparison(arg, null, "dcmpl", ComparisonOp.LE);
            case "Greater":
                return emitComparison(arg, null, "dcmpl", ComparisonOp.GT);
            case "GreaterEqual":
                return emitComparison(arg, null, "dcmpl", ComparisonOp.GE);
            case "Equal":
                return emitComparison(arg, null, "dcmpg", ComparisonOp.EQ);
            case "toInteger":
                emitUnboxToDouble(REAL_TYPE);
                emit("    d2i");
                emitBoxInteger();
                return INTEGER_TYPE;
            default:
                return null;
        }
    }

    private TypeInfo emitBooleanBuiltin(MethodInvocationNode methodNode) {
        String methodName = extractMethodName(methodNode);
        ExpressionNode arg = methodNode.arguments != null && !methodNode.arguments.isEmpty()
                ? methodNode.arguments.get(0) : null;
        switch (methodName) {
            case "Not":
                emitUnboxToBoolean();
                emit("    iconst_1");
                emit("    ixor");
                emitBoxBoolean();
                return BOOLEAN_TYPE;
            case "And":
                emitLogicalBinaryOp(arg, "iand");
                return BOOLEAN_TYPE;
            case "Or":
                emitLogicalBinaryOp(arg, "ior");
                return BOOLEAN_TYPE;
            case "Xor":
                emitLogicalBinaryOp(arg, "ixor");
                return BOOLEAN_TYPE;
            case "toInteger":
                emitUnboxToBoolean();
                emitBoxIntegerFromBoolean();
                return INTEGER_TYPE;
            default:
                return null;
        }
    }

    private TypeInfo emitListBuiltin(TypeInfo targetType, MethodInvocationNode methodNode) {
        String methodName = extractMethodName(methodNode);
        List<ExpressionNode> args = methodNode.arguments != null ? methodNode.arguments : Collections.emptyList();
        switch (methodName) {
            case "append": {
                emit("    dup");
                TypeInfo argType = generateExpression(args.get(0));
                emit("    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z");
                emit("    pop");
                return targetType;
            }
            case "head": {
                emit("    iconst_0");
                emit("    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;");
                return targetType.genericArgument != null ? targetType.genericArgument : OBJECT_TYPE;
            }
            case "tail": {
                emit("    invokestatic org/example/runtime/BuiltinSupport/tail(Ljava/util/ArrayList;)Ljava/util/ArrayList;");
                return targetType;
            }
            case "Length": {
                emit("    invokevirtual java/util/ArrayList/size()I");
                emitBoxInteger();
                return INTEGER_TYPE;
            }
            default:
                return null;
        }
    }

    private TypeInfo emitNumericBinaryOp(ExpressionNode argExpr, boolean allowIntResult,
                                         String intOp, String doubleOp) {
        TypeInfo leftType = INTEGER_TYPE;
        emitUnboxToNumeric(leftType, allowIntResult);
        TypeInfo rightType = generateExpression(argExpr);
        emitConvertForNumericOperand(rightType, allowIntResult);

        boolean useDouble = !allowIntResult || isRealType(rightType);
        if (useDouble) {
            if (allowIntResult) {
                emit("    i2d");
                emitSwapDoubleOperands();
            }
            emit(doubleOp);
            emitBoxDouble();
            return REAL_TYPE;
        } else {
            emit(intOp);
            emitBoxInteger();
            return INTEGER_TYPE;
        }
    }

    private TypeInfo emitIntegerRemainder(ExpressionNode argExpr) {
        emitUnboxToInt(INTEGER_TYPE);
        TypeInfo rightType = generateExpression(argExpr);
        emitUnboxToInt(rightType);
        emit("    irem");
        emitBoxInteger();
        return INTEGER_TYPE;
    }

    private void emitSwapDoubleOperands() {
        // placeholder for more advanced handling if needed later
    }

    private void emitConvertForNumericOperand(TypeInfo type, boolean allowInt) {
        if (!allowInt || isRealType(type)) {
            emitUnboxToDouble(type);
        } else {
            emitUnboxToInt(type);
        }
    }

    private TypeInfo emitComparison(ExpressionNode argExpr, String intOpcode, String doubleOpcode, ComparisonOp op) {
        boolean useDouble = false;
        emitUnboxToNumeric(INTEGER_TYPE, true);
        TypeInfo rightType = generateExpression(argExpr);
        if (isRealType(rightType)) {
            useDouble = true;
            emitUnboxToDouble(rightType);
        } else {
            emitUnboxToInt(rightType);
        }

        String trueLabel = newLabel();
        String endLabel = newLabel();

        if (useDouble) {
            emit(doubleOpcode);
            switch (op) {
                case LT:
                    emit("    iflt " + trueLabel);
                    break;
                case LE:
                    emit("    ifle " + trueLabel);
                    break;
                case GT:
                    emit("    ifgt " + trueLabel);
                    break;
                case GE:
                    emit("    ifge " + trueLabel);
                    break;
                case EQ:
                    emit("    ifeq " + trueLabel);
                    break;
            }
        } else {
            emit("    " + intOpcode + " " + trueLabel);
        }

        emit("    iconst_0");
        emit("    goto " + endLabel);
        emit(trueLabel + ":");
        emit("    iconst_1");
        emit(endLabel + ":");
        emitBoxBoolean();
        return BOOLEAN_TYPE;
    }

    private void emitLogicalBinaryOp(ExpressionNode argExpr, String opcode) {
        emitUnboxToBoolean();
        TypeInfo rightType = generateExpression(argExpr);
        emitUnboxToBoolean(rightType);
        emit("    " + opcode);
        emitBoxBoolean();
    }
    
    @Override
    public void visit(ConstructorInvocationNode node) {
        generateConstructorInvocation(node);
    }
    
    @Override
    public void visit(MethodInvocationNode node) {
        emit("    aload_0");
        emitMethodInvocationForStackTarget(TypeInfo.simple(currentClassName), node);
    }
    
    @Override
    public void visit(IdentifierNode node) {
        generateIdentifier(node);
    }
    
    @Override
    public void visit(ThisNode node) {
        emit("    aload_0");
    }
    
    @Override
    public void visit(IntLiteralNode node) {
        generateIntLiteral(node.value);
    }
    
    @Override
    public void visit(RealLiteralNode node) {
        generateRealLiteral(node.value);
    }
    
    @Override
    public void visit(BoolLiteralNode node) {
        generateBoolLiteral(node.value);
    }
    
    @Override
    public void visit(TypeNode node) {
    }
    
    @Override
    public void visit(GenericTypeNode node) {
    }
    
    private void saveJasminFile(String className) {
        try {
            File dir = new File(outputDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("Unable to create " + outputDir);
            }
            File file = new File(dir, className + ".j");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(output.toString());
            }
            System.out.println("Generated: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing Jasmin file: " + e.getMessage());
        }
    }
    
    public void compileJasminFiles() {
            File dir = new File(outputDir);
            if (!dir.exists()) {
                return;
            }
        File[] files = dir.listFiles((d, name) -> name.endsWith(".j"));
        if (files == null) {
                return;
        }
        for (File file : files) {
            System.out.println("To compile: java -jar jasmin.jar " + file.getAbsolutePath());
        }
    }

    // --- Helper methods for expressions ---

    private TypeInfo generateExpression(ExpressionNode expr) {
        if (expr == null) {
            emit("    aconst_null");
            return OBJECT_TYPE;
        }
        if (expr instanceof IntLiteralNode) {
            generateIntLiteral(((IntLiteralNode) expr).value);
            return INTEGER_TYPE;
        }
        if (expr instanceof RealLiteralNode) {
            generateRealLiteral(((RealLiteralNode) expr).value);
            return REAL_TYPE;
        }
        if (expr instanceof BoolLiteralNode) {
            generateBoolLiteral(((BoolLiteralNode) expr).value);
            return BOOLEAN_TYPE;
        }
        if (expr instanceof ThisNode) {
            emit("    aload_0");
            return TypeInfo.simple(currentClassName);
        }
        if (expr instanceof IdentifierNode) {
            return generateIdentifier((IdentifierNode) expr);
        }
        if (expr instanceof ConstructorInvocationNode) {
            return generateConstructorInvocation((ConstructorInvocationNode) expr);
        }
        if (expr instanceof MethodInvocationNode) {
            emit("    aload_0");
            return emitMethodInvocationForStackTarget(TypeInfo.simple(currentClassName), (MethodInvocationNode) expr);
        }
        if (expr instanceof MemberAccessNode) {
            return generateMemberAccess((MemberAccessNode) expr);
        }
        emit("    aconst_null");
        return OBJECT_TYPE;
    }

    private TypeInfo generateMemberAccess(MemberAccessNode node) {
        TypeInfo targetType = generateExpression(node.target);
        if (node.member instanceof IdentifierNode) {
            IdentifierNode member = (IdentifierNode) node.member;
            FieldBinding binding = resolveFieldBinding(targetType.baseName, member.name);
            if (binding != null) {
                emit("    getfield " + internalName(binding.ownerName) + "/" + binding.field.name + " " + descriptorForType(binding.field.type));
                return binding.field.type;
            }
            emit("    pop");
            emit("    aconst_null");
            return OBJECT_TYPE;
        }
        if (node.member instanceof MethodInvocationNode) {
            return emitMethodInvocationForStackTarget(targetType, (MethodInvocationNode) node.member);
        }
        return OBJECT_TYPE;
    }

    private TypeInfo generateIdentifier(IdentifierNode node) {
        VarInfo local = lookupLocalVar(node.name);
        if (local != null) {
            emitLoadVar(local);
            return local.type;
        }
        FieldBinding binding = resolveFieldBinding(currentClassName, node.name);
        if (binding != null) {
            emit("    aload_0");
            emit("    getfield " + internalName(binding.ownerName) + "/" + binding.field.name + " " + descriptorForType(binding.field.type));
            return binding.field.type;
        }
        emit("    aconst_null");
        return OBJECT_TYPE;
    }

    private VarInfo lookupLocalVar(String name) {
        for (Map<String, VarInfo> scope : localVarScopes) {
            VarInfo info = scope.get(name);
            if (info != null) {
                return info;
            }
        }
        return null;
    }

    private FieldBinding resolveFieldBinding(String className, String fieldName) {
        ClassInfo info = classInfoMap.get(className);
        while (info != null) {
            FieldInfo field = info.fields.get(fieldName);
            if (field != null) {
                return new FieldBinding(info.name, field);
            }
            info = classInfoMap.get(info.superName);
        }
        return null;
    }

    private TypeInfo generateConstructorInvocation(ConstructorInvocationNode node) {
        if (node == null) {
            emit("    aconst_null");
            return OBJECT_TYPE;
        }
        if (isBuiltinNumericClass(node.className)) {
            return emitBuiltinConstructor(node);
        }
        emit("    new " + node.className);
        emit("    dup");
        List<TypeInfo> args = new ArrayList<>();
        if (node.arguments != null) {
            for (ExpressionNode arg : node.arguments) {
                args.add(generateExpression(arg));
            }
        }
        ConstructorSignature signature = resolveConstructor(node.className, args);
        String descriptor = signature != null ? buildDescriptor(signature.parameterTypes, VOID_TYPE)
                : buildDescriptor(args, VOID_TYPE);
        emit("    invokespecial " + node.className + "/<init>" + descriptor);
        return TypeInfo.simple(node.className);
    }

    private ConstructorSignature resolveConstructor(String className, List<TypeInfo> argTypes) {
        ClassInfo info = classInfoMap.get(className);
        if (info == null) {
            return null;
        }
        for (ConstructorSignature signature : info.constructors) {
            if (matches(signature.parameterTypes, argTypes)) {
                return signature;
            }
        }
        return null;
    }

    private boolean matches(List<TypeInfo> expected, List<TypeInfo> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }
        for (int i = 0; i < expected.size(); i++) {
            if (!matchesType(expected.get(i), actual.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesType(TypeInfo expected, TypeInfo actual) {
        if (expected == null || expected.isTypeParameter) {
            return true;
        }
        if (actual == null) {
            return false;
        }
        return Objects.equals(expected.baseName, actual.baseName);
    }

    private MethodSignature resolveMethod(String className, String methodName, List<TypeInfo> argTypes) {
        ClassInfo info = classInfoMap.get(className);
        while (info != null) {
            List<MethodSignature> signatures = info.methods.get(methodName);
            if (signatures != null) {
                for (MethodSignature signature : signatures) {
                    if (matches(signature.parameterTypes, argTypes)) {
                        return signature;
                    }
                }
            }
            info = classInfoMap.get(info.superName);
        }
        return null;
    }

    private TypeInfo emitBuiltinConstructor(ConstructorInvocationNode node) {
        ExpressionNode arg = node.arguments != null && !node.arguments.isEmpty()
                ? node.arguments.get(0) : null;
        switch (node.className) {
            case "Integer":
                if (arg == null) {
                    emit("    iconst_0");
                } else {
                    TypeInfo argType = generateExpression(arg);
                    emitUnboxToInt(argType);
                }
                emitBoxInteger();
                return INTEGER_TYPE;
            case "Real":
                if (arg == null) {
                    emit("    dconst_0");
                } else {
                    TypeInfo argType = generateExpression(arg);
                    emitUnboxToDouble(argType);
                }
                emitBoxDouble();
                return REAL_TYPE;
            case "Boolean":
                if (arg == null) {
                    emit("    iconst_0");
                } else {
                    TypeInfo argType = generateExpression(arg);
                    emitUnboxToBoolean(argType);
                }
                emitBoxBoolean();
                return BOOLEAN_TYPE;
            case "List":
                emit("    new java/util/ArrayList");
                emit("    dup");
                emit("    invokespecial java/util/ArrayList/<init>()V");
                if (arg != null) {
                    emit("    dup");
                    generateExpression(arg);
                    emit("    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z");
                    emit("    pop");
                }
                return TypeInfo.generic("List", inferExpressionType(arg));
            case "Array":
                emit("    aload_0");
                if (arg != null) {
                    TypeInfo argType = generateExpression(arg);
                    emitUnboxToInt(argType);
                } else {
                    emit("    iconst_0");
                }
                emit("    invokestatic org/example/runtime/BuiltinSupport/createArray(I)Ljava/util/ArrayList;");
                return TypeInfo.generic("Array", inferExpressionType(arg));
            default:
                emit("    aconst_null");
                return OBJECT_TYPE;
        }
    }

    private void generateIntLiteral(Integer value) {
        if (value == null) {
            emit("    aconst_null");
                return;
        }
        int val = value;
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
    }

    private void generateRealLiteral(Double value) {
        if (value == null) {
            emit("    aconst_null");
            return;
        }
        double val = value;
        if (val == 0d) {
            emit("    dconst_0");
        } else if (val == 1d) {
            emit("    dconst_1");
        } else {
            emit("    ldc2_w " + val);
        }
        emitBoxDouble();
    }


    private void generateBoolLiteral(Boolean value) {
        if (value == null) {
            emit("    aconst_null");
            return;
        }
        emit("    iconst_" + (value ? "1" : "0"));
        emitBoxBoolean();
    }

    private void emitUnboxToNumeric(TypeInfo type, boolean allowInt) {
        if (!allowInt || isRealType(type)) {
            emitUnboxToDouble(type);
        } else {
            emitUnboxToInt(type);
        }
    }

    private void emitUnboxToInt(TypeInfo type) {
        emit("    checkcast java/lang/Number");
        emit("    invokevirtual java/lang/Number/intValue()I");
    }

    private void emitUnboxToDouble(TypeInfo type) {
        emit("    checkcast java/lang/Number");
        emit("    invokevirtual java/lang/Number/doubleValue()D");
    }

    private void emitUnboxToBoolean() {
        emit("    checkcast java/lang/Boolean");
        emit("    invokevirtual java/lang/Boolean/booleanValue()Z");
    }

    private void emitUnboxToBoolean(TypeInfo type) {
        emitUnboxToBoolean();
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

    private void emitBoxIntegerFromBoolean() {
        emitBoxInteger();
    }

    private void emitLoadVar(VarInfo var) {
        if (var.index <= 3) {
            emit("    aload_" + var.index);
        } else {
            emit("    aload " + var.index);
        }
    }

    private void emitStoreVar(TypeInfo type, int index) {
        if (index <= 3) {
            emit("    astore_" + index);
        } else {
            emit("    astore " + index);
        }
    }

    private void emitReturn(TypeInfo actualType, TypeInfo expectedType) {
        if (expectedType.isVoid()) {
            emit("    return");
            return;
        }
        emit("    areturn");
    }

    private void emitDefaultReturn(TypeInfo expectedType) {
        if (expectedType.isVoid()) {
            emit("    return");
        } else {
            emit("    aconst_null");
            emit("    areturn");
        }
    }

    private void emitCondition(ExpressionNode condition, String trueLabel, String falseLabel) {
        TypeInfo condType = generateExpression(condition);
        emitBooleanUnboxing(condType);
        emit("    ifne " + trueLabel);
        emit("    goto " + falseLabel);
    }

    private void emitBooleanUnboxing(TypeInfo type) {
        emitUnboxToBoolean(type);
    }

    private boolean isBuiltinNumericClass(String className) {
        return "Integer".equals(className) || "Real".equals(className)
                || "Boolean".equals(className) || "List".equals(className) || "Array".equals(className);
    }

    private boolean isRealType(TypeInfo type) {
        return type != null && "Real".equals(type.baseName);
    }

    private TypeInfo inferExpressionType(ExpressionNode expr) {
        MethodContext ctx = methodStack.isEmpty() ? null : methodStack.peek();
        return inferExpressionType(expr, ctx);
    }

    private TypeInfo inferExpressionType(ExpressionNode expr, MethodContext ctx) {
        if (expr == null) {
            return OBJECT_TYPE;
        }
        if (expr instanceof IntLiteralNode) {
            return INTEGER_TYPE;
        }
        if (expr instanceof RealLiteralNode) {
            return REAL_TYPE;
        }
        if (expr instanceof BoolLiteralNode) {
            return BOOLEAN_TYPE;
        }
        if (expr instanceof ThisNode) {
            return TypeInfo.simple(currentClassName);
        }
        if (expr instanceof IdentifierNode) {
            VarInfo local = lookupLocalVar(((IdentifierNode) expr).name);
            if (local != null) {
                return local.type;
            }
            FieldBinding binding = resolveFieldBinding(currentClassName, ((IdentifierNode) expr).name);
            if (binding != null) {
                return binding.field.type;
            }
            return OBJECT_TYPE;
        }
        if (expr instanceof ConstructorInvocationNode) {
            ConstructorInvocationNode node = (ConstructorInvocationNode) expr;
            if ("List".equals(node.className) && node.arguments != null && !node.arguments.isEmpty()) {
                return TypeInfo.generic("List", inferExpressionType(node.arguments.get(0), ctx));
            }
            return TypeInfo.simple(node.className);
        }
        if (expr instanceof MethodInvocationNode) {
            List<TypeInfo> args = inferArgumentTypes(((MethodInvocationNode) expr).arguments, ctx);
            MethodSignature signature = resolveMethod(currentClassName,
                    extractMethodName((MethodInvocationNode) expr), args);
            return signature != null ? applyTypeParameters(signature.returnType, TypeInfo.simple(currentClassName)) : OBJECT_TYPE;
        }
        if (expr instanceof MemberAccessNode) {
            MemberAccessNode access = (MemberAccessNode) expr;
            TypeInfo target = inferExpressionType(access.target, ctx);
            if (access.member instanceof IdentifierNode) {
                FieldBinding binding = resolveFieldBinding(target.baseName, ((IdentifierNode) access.member).name);
                return binding != null ? applyTypeParameters(binding.field.type, target) : OBJECT_TYPE;
            }
            if (access.member instanceof MethodInvocationNode) {
                List<TypeInfo> args = inferArgumentTypes(((MethodInvocationNode) access.member).arguments, ctx);
                MethodSignature signature = resolveMethod(target.baseName,
                        extractMethodName((MethodInvocationNode) access.member), args);
                return signature != null ? applyTypeParameters(signature.returnType, target) : OBJECT_TYPE;
            }
        }
        return OBJECT_TYPE;
    }

    private List<TypeInfo> inferArgumentTypes(List<ExpressionNode> args, MethodContext ctx) {
        if (args == null) {
            return Collections.emptyList();
        }
        List<TypeInfo> result = new ArrayList<>();
        for (ExpressionNode arg : args) {
            result.add(inferExpressionType(arg, ctx));
        }
        return result;
    }

    private TypeInfo applyTypeParameters(TypeInfo declaredType, TypeInfo ownerType) {
        if (declaredType == null) {
            return OBJECT_TYPE;
        }
        if (!declaredType.isTypeParameter) {
            if (declaredType.genericArgument != null && ownerType != null) {
                return TypeInfo.generic(declaredType.baseName, applyTypeParameters(declaredType.genericArgument, ownerType));
            }
            return declaredType;
        }
        if (ownerType != null && ownerType.genericArgument != null) {
            return ownerType.genericArgument;
        }
        return OBJECT_TYPE;
    }

    private String extractMethodName(MethodInvocationNode node) {
        if (node.target instanceof IdentifierNode) {
            return ((IdentifierNode) node.target).name;
        }
        return "unknown";
    }

    private String newLabel() {
        return "L" + (labelCounter++);
    }

    private enum ComparisonOp {
        LT, LE, GT, GE, EQ
    }

    private static final class MethodContext {
        final ClassInfo owner;
        final String methodName;
        final TypeInfo returnType;
        final boolean constructor;
        int nextLocalIndex = 1;

        MethodContext(ClassInfo owner, String methodName, TypeInfo returnType, boolean constructor) {
            this.owner = owner;
            this.methodName = methodName;
            this.returnType = returnType;
            this.constructor = constructor;
        }
    }

    private static final class VarInfo {
        final String name;
        final TypeInfo type;
        final int index;

        VarInfo(String name, TypeInfo type, int index) {
            this.name = name;
            this.type = type;
            this.index = index;
        }
    }

    private static final class FieldInfo {
        final String name;
        final TypeInfo type;
        final ExpressionNode initializer;

        FieldInfo(String name, TypeInfo type, ExpressionNode initializer) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }
    }

    private static final class FieldBinding {
        final String ownerName;
        final FieldInfo field;

        FieldBinding(String ownerName, FieldInfo field) {
            this.ownerName = ownerName;
            this.field = field;
        }
    }

    private static final class MethodSignature {
        final String name;
        final List<TypeInfo> parameterTypes;
        final TypeInfo returnType;
        final boolean builtin;

        MethodSignature(String name, List<TypeInfo> parameterTypes, TypeInfo returnType, boolean builtin) {
            this.name = name;
            this.parameterTypes = parameterTypes;
            this.returnType = returnType;
            this.builtin = builtin;
        }

        String descriptor() {
            StringBuilder sb = new StringBuilder("(");
            for (TypeInfo type : parameterTypes) {
                sb.append(type.descriptor());
            }
            sb.append(")");
            sb.append(returnType.descriptor());
            return sb.toString();
        }
    }

    private static final class ConstructorSignature {
        final List<TypeInfo> parameterTypes;
        final boolean builtin;

        ConstructorSignature(List<TypeInfo> parameterTypes, boolean builtin) {
            this.parameterTypes = parameterTypes;
            this.builtin = builtin;
        }
    }

    private static final class ClassInfo {
        final String name;
        final String superName;
        final boolean external;
        final Map<String, FieldInfo> fields = new LinkedHashMap<>();
        final Map<String, List<MethodSignature>> methods = new HashMap<>();
        final List<ConstructorSignature> constructors = new ArrayList<>();
        final List<FieldInfo> orderedFields = new ArrayList<>();

        ClassInfo(String name, String superName, boolean external) {
            this.name = name;
            this.superName = superName;
            this.external = external;
        }

        void addField(FieldInfo field) {
            fields.put(field.name, field);
            orderedFields.add(field);
        }

        void addMethod(MethodSignature signature) {
            methods.computeIfAbsent(signature.name, k -> new ArrayList<>()).add(signature);
        }

        void addConstructor(ConstructorSignature signature) {
            constructors.add(signature);
        }
    }

    private static final class TypeInfo {
        final String baseName;
        final TypeInfo genericArgument;
        final boolean isTypeParameter;

        private TypeInfo(String baseName, TypeInfo genericArgument, boolean isTypeParameter) {
            this.baseName = baseName;
            this.genericArgument = genericArgument;
            this.isTypeParameter = isTypeParameter;
        }

        static TypeInfo simple(String name) {
            return new TypeInfo(name, null, false);
        }

        static TypeInfo generic(String name, TypeInfo argument) {
            return new TypeInfo(name, argument, false);
        }

        static TypeInfo typeParameter(String name) {
            return new TypeInfo(name, null, true);
        }

        boolean isVoid() {
            return "void".equals(baseName);
        }

        String descriptor() {
            if (isTypeParameter) {
                return "Ljava/lang/Object;";
            }
            if (isVoid()) {
                return "V";
            }
            String runtime = BUILTIN_RUNTIME.getOrDefault(baseName, baseName);
            return "L" + runtime + ";";
        }
    }
}
