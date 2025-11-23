package org.example.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Semantic checker that validates the AST for semantic errors:
 * - Declarations before usage
 * - Array bounds checking
 */
public class SemanticChecker implements ASTVisitor {
    private List<String> errors = new ArrayList<>();
    private int errorCount = 0;

    // Symbol tables
    private Map<String, ClassInfo> classes = new HashMap<>();
    private Stack<Map<String, VariableInfo>> variableScopes = new Stack<>();
    private Stack<MethodInfo> currentMethodScope = new Stack<>();
    private Stack<ClassInfo> currentClassScope = new Stack<>();

    // For tracking declaration order within each scope
    private int currentDeclarationOrder = 0;
    private Map<Object, Integer> declarationOrders = new IdentityHashMap<>();

    // Track the current body element being processed (for order comparison)
    private BodyElementNode currentBodyElement = null;

    // Built-in classes that don't need declaration checking
    // TODO: обсудить с Лизой
    private static final Set<String> BUILTIN_CLASSES = Set.of(
        "Integer", "Real", "Boolean", "List", "AnyValue", "Class", "AnyRef",
        "Array");

    /**
     * Information about a class
     */
    private static class ClassInfo {
        String name;
        String extendsClass;
        Map<String, VariableInfo> fields = new HashMap<>();
        Map<String, MethodInfo> methods = new HashMap<>();
        List<ConstructorInfo> constructors = new ArrayList<>();
        int declarationOrder;

        ClassInfo(String name, String extendsClass, int order) {
            this.name = name;
            this.extendsClass = extendsClass;
            this.declarationOrder = order;
        }
    }

    /**
     * Information about a variable
     */
    private static class VariableInfo {
        String name;
        ASTNode type; // Can be TypeNode or GenericTypeNode
        int declarationOrder;
        boolean isInitialized;
        Integer arraySize; // Size of array/list if determinable at compile time

        VariableInfo(String name, ASTNode type, int order) {
        this.name = name;
        this.type = type;
        this.declarationOrder = order;
        this.isInitialized = false;
        this.arraySize = null;
        }
    }

    /**
     * Information about a method
     */
    private static class MethodInfo {
        String name;
        String returnType;
        List<ParamDeclNode> parameters;
        int declarationOrder;

        MethodInfo(
            String name, String returnType, List<ParamDeclNode> parameters,
            int order) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.declarationOrder = order;
        }
    }

    /**
     * Information about a constructor
     */
    private static class ConstructorInfo {
        List<ParamDeclNode> parameters;
        int declarationOrder;

        ConstructorInfo(List<ParamDeclNode> parameters, int order) {
        this.parameters = parameters;
        this.declarationOrder = order;
        }
    }

    /**
     * Get all errors found during semantic checking
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Get the number of errors found
     */
    public int getErrorCount() {
        return errorCount;
    }

    /**
     * Print all errors to System.out
     */
    public void printErrors() {
        for (String error : errors) {
        System.out.println(error);
        }
    }

    /**
     * Report a semantic error
     */
    private void reportError(String message) {
        errors.add("Semantic error: " + message);
        errorCount++;
    }

    /**
     * Look up a variable in the current scope chain
     */
    private VariableInfo lookupVariable(String name) {
        // Check current scope first
        for (int i = variableScopes.size() - 1; i >= 0; i--) {
        Map<String, VariableInfo> scope = variableScopes.get(i);
        if (scope.containsKey(name)) {
            return scope.get(name);
        }
        }

        // Check class fields
        if (!currentClassScope.isEmpty()) {
        ClassInfo classInfo = currentClassScope.peek();
        if (classInfo.fields.containsKey(name)) {
            return classInfo.fields.get(name);
        }
        }

        return null;
    }

    /**
     * Look up a class
     */
    private ClassInfo lookupClass(String name) {
        return classes.get(name);
    }

    /**
     * Look up a method in the current class
     */
    private MethodInfo lookupMethod(String methodName) {
        if (!currentClassScope.isEmpty()) {
        ClassInfo classInfo = currentClassScope.peek();
        if (classInfo.methods.containsKey(methodName)) {
            return classInfo.methods.get(methodName);
        }
        }
        return null;
    }

    /**
     * Check if a variable is a class field
     */
    private boolean isClassField(String varName) {
        if (!currentClassScope.isEmpty()) {
        ClassInfo classInfo = currentClassScope.peek();
        return classInfo.fields.containsKey(varName);
        }
        return false;
    }

    /**
     * Check if a variable is declared before use
     */
    private void checkVariableUsage(String varName, ASTNode usageNode) {
        VariableInfo varInfo = lookupVariable(varName);
        if (varInfo == null) {
            reportError("Variable '" + varName + "' is used before declaration");
            return;
        }

        // Поля класса можно всегда
        if (isClassField(varName)) {
            return;
        }

        if (!variableScopes.isEmpty()) {
            Map<String, VariableInfo> currentScope = variableScopes.peek();
            VariableInfo inCurrentScope = currentScope.get(varName);

            // Если этой переменной нет в вершине стэка, значит она из внешнего блока
            // (параметр метода, переменная внешнего тела, поле и т.п.)
            // Для них проверку порядка не делаем.
            if (inCurrentScope != varInfo) {
                return;
            }
        }

        Integer varDeclOrder = varInfo.declarationOrder;

        Integer bodyElementOrder = null;
        if (currentBodyElement != null) {
            bodyElementOrder = declarationOrders.get(currentBodyElement);
        }

        if (varDeclOrder != null && bodyElementOrder != null) {
            if (varDeclOrder > bodyElementOrder) {
                reportError("Variable '" + varName + "' is used before declaration");
            }
        }
    }

    /**
     * Check if a class is declared before use
     */
    private void checkClassUsage(String className, ASTNode usageNode) {
        // Skip built-in classes
        if (BUILTIN_CLASSES.contains(className)) {
        return;
        }

        ClassInfo classInfo = lookupClass(className);
        if (classInfo == null) {
        reportError("Class '" + className + "' is used before declaration");
        return;
        }

        // For classes, we need to check if the class is declared before its usage
        // Since classes are processed first, this check is mainly for forward
        // references
        Integer classOrder = classInfo.declarationOrder;
        Integer useOrder = declarationOrders.get(usageNode);

        // Only check if both orders are available
        if (useOrder != null && classOrder > useOrder) {
        reportError("Class '" + className + "' is used before declaration");
        }
    }

    /**
     * Check if a method is declared before use
     */
    private void checkMethodUsage(String methodName, ASTNode usageNode) {
        // Skip built-in methods (they don't need checking)
        // Also, methods can call themselves recursively, so we allow that

        MethodInfo methodInfo = lookupMethod(methodName);
        if (methodInfo == null) { // TODO
        // Don't report error - might be a method on an object type (like Integer.Plus)
        // or a built-in method
        System.out.println("нет такого метода... Или он базовый " + methodName);
        return;
        }

        // Check declaration order within the same class
        // Allow recursive calls (method calling itself)
        // But check if method is used before its declaration in the class
        Integer methodOrder = methodInfo.declarationOrder;
        Integer useOrder = declarationOrders.get(usageNode);

        if (methodOrder != null && useOrder != null && methodOrder > useOrder) {
        reportError("Method '" + methodName + "' is called before declaration");
        }
    }

    /**
     * Extract array length from an expression (if it's a compile-time constant)
     * Looks for patterns like:
     * - List[Type](IntLiteral) - where IntLiteral is the size
     * - List[Type](Integer(n)) - where Integer(n) is the size
     * - Array[Type](IntLiteral) - where IntLiteral is the size
     * - Array[Type](Integer(n)) - where Integer(n) is the size
     * - variable identifier that was initialized with a known size
     */
    private Integer extractArrayLength(ExpressionNode expr) {
        // If it's an identifier, check if it's a variable with known array size
        if (expr instanceof IdentifierNode) {
        String varName = ((IdentifierNode) expr).name;
        VariableInfo varInfo = lookupVariable(varName);
        if (varInfo != null && varInfo.arraySize != null) {
            return varInfo.arraySize;
        }
        }

        // If it's a constructor invocation like List[Type](size) or Array[Type](size)
        if (expr instanceof ConstructorInvocationNode) {
        ConstructorInvocationNode cons = (ConstructorInvocationNode) expr;
        // Check if arguments contain an integer value (IntLiteral or Integer(...))
        if (cons.arguments != null && cons.arguments.size() > 0) {
            ExpressionNode firstArg = cons.arguments.get(0);
            return extractIntegerValue(firstArg);
        }
        }

        // If it's member access, try to get the target
        if (expr instanceof MemberAccessNode) {
        MemberAccessNode memberAccess = (MemberAccessNode) expr;
        return extractArrayLength(memberAccess.target);
        }

        return null;
    }

    /**
     * Extract integer value from an expression (if it's a compile-time constant)
     * Supports:
     * - IntLiteralNode (e.g., 12)
     * - ConstructorInvocationNode with Integer (e.g., Integer(12))
     */
    private Integer extractIntegerValue(ExpressionNode expr) {
        if (expr instanceof IntLiteralNode) {
        return ((IntLiteralNode) expr).value;
        }

        // Check for Integer(...) constructor
        if (expr instanceof ConstructorInvocationNode) {
        ConstructorInvocationNode cons = (ConstructorInvocationNode) expr;
        if ("Integer".equals(cons.className)) {
            // Integer constructor should have one argument with an integer value
            if (cons.arguments != null && cons.arguments.size() > 0) {
            ExpressionNode firstArg = cons.arguments.get(0);
            // Recursively extract value (supports nested Integer(Integer(...)) or
            // IntLiteral)
            return extractIntegerValue(firstArg);
            }
        }
        }

        return null;
    }

    /**
     * Extract array index from an expression (if it's a compile-time constant)
     */
    private Integer extractConstantIndex(ExpressionNode expr) {
        return extractIntegerValue(expr);
    }

    /**
     * Check array bounds (if array access syntax exists)
     */
    private void checkArrayBounds(
        ExpressionNode arrayExpr, ExpressionNode indexExpr) {
        Integer index = extractConstantIndex(indexExpr);
        if (index != null) {
        if (index < 0) {
            reportError("Array index must be non-negative, found: " + index);
        }
        // If we can determine array length at compile time, check bounds
        Integer length = extractArrayLength(arrayExpr);
        if (length != null && index >= length) {
            reportError(
                "Array index out of bounds: index " + index + " >= length " + length);
        }
        }
    }

    @Override
    public void visit(ProgramNode node) {
        // Сначала добавляем стандартную библиотеку
        initBuiltins();

        // First pass: collect all class declarations
        if (node.classes != null) {
            currentDeclarationOrder = 0;
            for (ClassDeclNode classDecl : node.classes) {
                ClassInfo classInfo = new ClassInfo(
                    classDecl.className, classDecl.extendsClass,
                    currentDeclarationOrder++);
                classes.put(classDecl.className, classInfo);
                declarationOrders.put(classDecl, classInfo.declarationOrder);
            }

            // Second pass: process classes and check declarations
            for (ClassDeclNode classDecl : node.classes) {
                currentDeclarationOrder = 0; // Reset for each class scope
                classDecl.accept(this);
            }
        }

        // --- Проверка наличия класса main ---
        if (!classes.containsKey("Main")) {
            reportError("Entry point error: class 'Main' not found");
        }
    }


    @Override
    public void visit(ClassDeclNode node) {
        ClassInfo classInfo = classes.get(node.className);
        currentClassScope.push(classInfo);

        // Check extends class is declared
        if (node.extendsClass != null) {
        checkClassUsage(node.extendsClass, node);
        }

        // Enter new variable scope for class fields
        variableScopes.push(new HashMap<>());

        // First pass: collect all declarations
        if (node.members != null) {
            currentDeclarationOrder = 0;
            for (MemberNode member : node.members) {
                if (member instanceof VarDeclNode) {
                    VarDeclNode varDecl = (VarDeclNode) member;

                    VariableInfo varInfo = createVariableInfoFromVarDecl(varDecl, currentDeclarationOrder);

                    classInfo.fields.put(varDecl.varName, varInfo);
                    variableScopes.peek().put(varDecl.varName, varInfo);
                    declarationOrders.put(varDecl, currentDeclarationOrder);
                    declarationOrders.put(varInfo, currentDeclarationOrder);
                    currentDeclarationOrder++;
                } else if (member instanceof MethodDeclNode) {
                    MethodDeclNode methodDecl = (MethodDeclNode) member;
                    MethodInfo methodInfo = new MethodInfo(
                        methodDecl.header.methodName,
                        methodDecl.header.returnType,
                        methodDecl.header.parameters,
                        currentDeclarationOrder);
                    classInfo.methods.put(methodDecl.header.methodName, methodInfo);
                    declarationOrders.put(methodDecl, currentDeclarationOrder);
                    declarationOrders.put(methodInfo, currentDeclarationOrder);
                    currentDeclarationOrder++;
                    } else if (member instanceof ConstructorDeclNode) {
                    ConstructorDeclNode constructorDecl = (ConstructorDeclNode) member;
                    ConstructorInfo constructorInfo = new ConstructorInfo(
                        constructorDecl.parameters,
                        currentDeclarationOrder);
                    classInfo.constructors.add(constructorInfo);
                    declarationOrders.put(constructorDecl, currentDeclarationOrder);
                    currentDeclarationOrder++;
                }
            }
        }

        // Second pass: visit members to check usage
        if (node.members != null) {
        currentDeclarationOrder = 0;
        for (MemberNode member : node.members) {
            declarationOrders.put(member, currentDeclarationOrder++);
            member.accept(this);
        }
        }

        variableScopes.pop();
        currentClassScope.pop();
    }

    @Override
    public void visit(VarDeclNode node) {
        // Variable declaration - already handled in ClassDeclNode visit
        // Just check the initializer expression
        if (node.initializer != null) {
        node.initializer.accept(this);
        }
    }

    @Override
    public void visit(MethodDeclNode node) {
        // Enter new scope for method body (parameters will be added here)
        variableScopes.push(new HashMap<>());
        currentDeclarationOrder = 0;

        // Add method parameters to scope BEFORE visiting body
        if (node.header != null && node.header.parameters != null) {
        for (ParamDeclNode param : node.header.parameters) {
            VariableInfo paramInfo = new VariableInfo(
                param.paramName,
                param.paramType,
                currentDeclarationOrder);
            variableScopes.peek()
                .put(param.paramName, paramInfo);
            declarationOrders.put(param, currentDeclarationOrder);
            declarationOrders.put(paramInfo, currentDeclarationOrder);
            currentDeclarationOrder++;
        }
        }

        // Now visit the body - parameters are already in scope
        if (node.body != null) {
        node.body.accept(this);
        }

        variableScopes.pop();
    }

    @Override
    public void visit(MethodHeaderNode node) {
        // Method header doesn't need checking here - handled in MethodDeclNode
    }

    @Override
    public void visit(MethodBodyNode node) {
        if (node.isArrow && node.arrowExpression != null) {
            declarationOrders.put(node.arrowExpression, currentDeclarationOrder++);
            node.arrowExpression.accept(this);
        } else if (!node.isArrow && node.body != null) {
            analyzeBodyWithNewScope(node.body);
        }
    }

    @Override
    public void visit(ParamDeclNode node) {
        // Parameters are added to scope in visit(MethodDeclNode) or
        // visit(ConstructorDeclNode)
        // This method is called when visiting the parameter list itself
        // The actual adding to scope happens in the parent node visitor
    }

    @Override
    public void visit(ConstructorDeclNode node) {
        // Enter new scope for constructor
        variableScopes.push(new HashMap<>());

        // Add parameters to scope
        currentDeclarationOrder = 0;
        if (node.parameters != null) {
        for (ParamDeclNode param : node.parameters) {
            VariableInfo paramInfo = new VariableInfo(
                param.paramName,
                param.paramType,
                currentDeclarationOrder);
            variableScopes.peek()
                .put(param.paramName, paramInfo);
            declarationOrders.put(param, currentDeclarationOrder);
            declarationOrders.put(paramInfo, currentDeclarationOrder);
            currentDeclarationOrder++;
        }
        }

        if (node.body != null) {
            analyzeBodyWithNewScope(node.body);
        }

        variableScopes.pop();
    }

    @Override
    public void visit(BodyNode node) {
        // Body is visited within method/constructor context
        if (node.elements != null) {
        for (BodyElementNode element : node.elements) {
            if (element instanceof ASTNode) {
            ((ASTNode) element).accept(this);
            }
        }
        }
    }

    @Override
    public void visit(AssignmentNode node) {
        // Check left side (variable to assign to)
        if (node.left != null) {
        declarationOrders.put(node.left, currentDeclarationOrder++);
        node.left.accept(this);
        }

        // Check right side (expression to assign)
        if (node.right != null) {
        declarationOrders.put(node.right, currentDeclarationOrder++);
        node.right.accept(this);
        }
    }

    @Override
    public void visit(WhileLoopNode node) {
        if (node.condition != null) {
            declarationOrders.put(node.condition, currentDeclarationOrder++);
            node.condition.accept(this);
        }
        if (node.body != null) {
            analyzeBodyWithNewScope(node.body);
        }
    }

    @Override
    public void visit(IfStatementNode node) {
        if (node.condition != null) {
            declarationOrders.put(node.condition, currentDeclarationOrder++);
            node.condition.accept(this);
        }
        if (node.thenBody != null) {
            analyzeBodyWithNewScope(node.thenBody);
        }
        if (node.elseBody != null) {
            analyzeBodyWithNewScope(node.elseBody);
        }
    }


    @Override
    public void visit(ReturnNode node) {
        if (node.expression != null) {
            declarationOrders.put(node.expression, currentDeclarationOrder++);
            node.expression.accept(this);
        }
    }

    @Override
    public void visit(PrintNode node) {
        if (node.expression != null) {
            declarationOrders.put(node.expression, currentDeclarationOrder++);
            node.expression.accept(this);
        }
    }

    @Override
    public void visit(MemberAccessNode node) {
        // Visit target first (e.g., 'n' in 'n.LessEqual(...)')
        if (node.target != null) {
            declarationOrders.put(node.target, currentDeclarationOrder++);
            node.target.accept(this);
        }

        // Member can be either:
        // 1. IdentifierNode - field access (e.g., 'obj.field')
        // 2. MethodInvocationNode - method call (e.g., 'obj.method(args)')

        if (node.member instanceof IdentifierNode) {
            // Field access - check if field exists (might be a built-in method)
            // Don't check as variable - it's a field/method name
            if (!checkClassField(node))
                reportError("У класса нет поля " + ((IdentifierNode) node.member).name);
            } else if (node.member instanceof MethodInvocationNode) {
            // Method call on an object - don't check member as variable
            if (!checkMethodExistence(node)) {
                ExpressionNode method = ((MethodInvocationNode) node.member).target;
                String methodName = ((IdentifierNode) method).name;
                reportError(
                    "У класса нет метода " + methodName);
            }

            MethodInvocationNode methodInv = (MethodInvocationNode) node.member;

            // Check arguments
            if (methodInv.arguments != null) {
                for (ExpressionNode arg : methodInv.arguments) {
                declarationOrders.put(arg, currentDeclarationOrder++);
                arg.accept(this);
                }
            }

            // Check for array access pattern: arr.get(i) or similar
            if (methodInv.arguments != null && methodInv.arguments.size() == 1) {
                ExpressionNode indexExpr = methodInv.arguments.get(0);
                // Check if this is a get() method call
                if (methodInv.target instanceof IdentifierNode) {
                String methodName = ((IdentifierNode) methodInv.target).name;
                if ("get".equals(methodName)) {
                    checkArrayBounds(node.target, indexExpr);
                }
                }
            }
        } else {
            // Other types - visit normally
            if (node.member != null) {
                declarationOrders.put(node.member, currentDeclarationOrder++);
                node.member.accept(this);
            }
        }
    }

    private boolean checkMethodExistence(MemberAccessNode node) {
        if (!(node.member instanceof MethodInvocationNode m)) {
            return false;
        }

        String className = inferExprType(node.target);
        if (className == null) {
            return false;
        }

        if (BUILTIN_CLASSES.contains(className)) {
            return true;
        }

        ClassInfo classInfo = lookupClass(className);
        if (classInfo == null) {
            return false;
        }

        ExpressionNode methodTarget = m.target;
        if (!(methodTarget instanceof IdentifierNode methodId)) {
            return false;
        }

        String methodName = methodId.name;
        return hasMethodInHierarchy(className, methodName);
    }

    private boolean checkClassField(MemberAccessNode node) {
        if (node.target instanceof IdentifierNode obj &&
            node.member instanceof IdentifierNode fieldId) {

            String varName = obj.name;
            VariableInfo classVarInfo = lookupVariable(varName);
            if (classVarInfo == null) {
                return false;
            }

            String className = resolveTypeName(classVarInfo.type);
            if (className == null) {
                return false;
            }

            if (BUILTIN_CLASSES.contains(className)) {
                return true;
            }

            ClassInfo classInfo = lookupClass(className);
            if (classInfo == null) {
                return false;
            }

            String fieldName = fieldId.name;
            return classInfo.fields.containsKey(fieldName);
        }

        return false;
    }

    @Override
    public void visit(ConstructorInvocationNode node) {
        // Check that class is declared
        checkClassUsage(node.className, node);

        // Check arguments
        if (node.arguments != null) {
            for (ExpressionNode arg : node.arguments) {
                declarationOrders.put(arg, currentDeclarationOrder++);
                arg.accept(this);
            }
        }
    }

    @Override
    public void visit(MethodInvocationNode node) {
        // MethodInvocationNode is used for standalone method calls (not member access)
        // e.g., 'method(args)' not 'obj.method(args)'

        // Check target expression (the method name)
        if (node.target != null) {
            // If target is an identifier, it's a method name - don't check as variable
            if (node.target instanceof IdentifierNode) {
                String methodName = ((IdentifierNode) node.target).name;
                // Check method usage but don't visit the identifier as variable
                checkMethodUsage(methodName, node);
            } else {
                // If target is not an identifier, visit it normally (might be an expression)
                declarationOrders.put(node.target, currentDeclarationOrder++);
                node.target.accept(this);
            }
        }

        // Check arguments
        if (node.arguments != null) {
            for (ExpressionNode arg : node.arguments) {
                declarationOrders.put(arg, currentDeclarationOrder++);
                arg.accept(this);
            }
        }
    }

    @Override
    public void visit(IdentifierNode node) {
        // Set usage order BEFORE checking (so checkVariableUsage can compare orders)
        declarationOrders.put(node, currentDeclarationOrder++);

        // Check that variable is declared before use
        checkVariableUsage(node.name, node);
    }

    @Override
    public void visit(ThisNode node) {
        // 'this' is always valid within a class
        if (currentClassScope.isEmpty()) {
            reportError("'this' used outside of a class");
        }
    }

    @Override
    public void visit(IntLiteralNode node) {
        // Literals don't need checking
    }

    @Override
    public void visit(RealLiteralNode node) {
        // Literals don't need checking
    }

    @Override
    public void visit(BoolLiteralNode node) {
        // Literals don't need checking
    }

    @Override
    public void visit(TypeNode node) {

    }

    @Override
    public void visit(GenericTypeNode node) {

    }

    // Вытянуть имя класса из узла типа/конструктора
    private String resolveTypeName(ASTNode typeNode) {
        if (typeNode == null) return null;

        // Простой тип: Integer, Real, Boolean, Main и т.д.
        if (typeNode instanceof TypeNode t) {
            return t.name; 
        }

        // Обобщённый тип: List[T], Array[Integer] и т.п.
        if (typeNode instanceof GenericTypeNode g) {
            return g.baseType;
        }

        // Конструктор типа: Integer(3), Real(2.3)
        if (typeNode instanceof ConstructorInvocationNode c) {
            return c.className;
        }

        return null;
    }

    private void initBuiltins() {
        // Integer
        ClassInfo integerInfo = new ClassInfo("Integer", "AnyValue", -1);

        // Методы Integer – только имена нужны для проверки существования
        integerInfo.methods.put("toReal",
            new MethodInfo("toReal", "Real", List.of(), -1));
        integerInfo.methods.put("toBoolean",
            new MethodInfo("toBoolean", "Boolean", List.of(), -1));

        integerInfo.methods.put("UnaryMinus",
            new MethodInfo("UnaryMinus", "Integer", List.of(), -1));

        // Plus
        integerInfo.methods.put("Plus",
            new MethodInfo("Plus", "Integer", List.of(/* p: Integer */), -1));
        // вторая перегрузка с Real, тип результата Real
        integerInfo.methods.put("Plus_real",
            new MethodInfo("Plus", "Real", List.of(/* p: Real */), -1));

        // Minus
        integerInfo.methods.put("Minus",
            new MethodInfo("Minus", "Integer", List.of(), -1));
        integerInfo.methods.put("Minus_real",
            new MethodInfo("Minus", "Real", List.of(), -1));

        // Mult
        integerInfo.methods.put("Mult",
            new MethodInfo("Mult", "Integer", List.of(), -1));
        integerInfo.methods.put("Mult_real",
            new MethodInfo("Mult", "Real", List.of(), -1));

        // Div
        integerInfo.methods.put("Div",
            new MethodInfo("Div", "Integer", List.of(), -1));
        integerInfo.methods.put("Div_real",
            new MethodInfo("Div", "Real", List.of(), -1));

        integerInfo.methods.put("Rem",
            new MethodInfo("Rem", "Integer", List.of(), -1));

        // Relations
        integerInfo.methods.put("Less",
            new MethodInfo("Less", "Boolean", List.of(), -1));
        integerInfo.methods.put("Less_real",
            new MethodInfo("Less", "Boolean", List.of(), -1));

        integerInfo.methods.put("LessEqual",
            new MethodInfo("LessEqual", "Boolean", List.of(), -1));
        integerInfo.methods.put("LessEqual_real",
            new MethodInfo("LessEqual", "Boolean", List.of(), -1));

        integerInfo.methods.put("Greater",
            new MethodInfo("Greater", "Boolean", List.of(), -1));
        integerInfo.methods.put("Greater_real",
            new MethodInfo("Greater", "Boolean", List.of(), -1));

        integerInfo.methods.put("GreaterEqual",
            new MethodInfo("GreaterEqual", "Boolean", List.of(), -1));
        integerInfo.methods.put("GreaterEqual_real",
            new MethodInfo("GreaterEqual", "Boolean", List.of(), -1));

        integerInfo.methods.put("Equal",
            new MethodInfo("Equal", "Boolean", List.of(), -1));
        integerInfo.methods.put("Equal_real",
            new MethodInfo("Equal", "Boolean", List.of(), -1));

        // Поля Min / Max
        integerInfo.fields.put("Min", new VariableInfo("Min", null, -1));
        integerInfo.fields.put("Max", new VariableInfo("Max", null, -1));

        classes.putIfAbsent("Integer", integerInfo);

        // === Real ===
        ClassInfo realInfo = new ClassInfo("Real", "AnyValue", -1);

        realInfo.methods.put("toInteger",
            new MethodInfo("toInteger", "Integer", List.of(), -1));

        realInfo.methods.put("UnaryMinus",
            new MethodInfo("UnaryMinus", "Real", List.of(), -1));

        realInfo.methods.put("Plus",
            new MethodInfo("Plus", "Real", List.of(), -1));
        realInfo.methods.put("Plus_int",
            new MethodInfo("Plus", "Real", List.of(), -1));

        realInfo.methods.put("Minus",
            new MethodInfo("Minus", "Real", List.of(), -1));
        realInfo.methods.put("Minus_int",
            new MethodInfo("Minus", "Real", List.of(), -1));

        realInfo.methods.put("Mult",
            new MethodInfo("Mult", "Real", List.of(), -1));
        realInfo.methods.put("Mult_int",
            new MethodInfo("Mult", "Real", List.of(), -1));

        realInfo.methods.put("Div",
            new MethodInfo("Div", "Real", List.of(), -1));
        realInfo.methods.put("Div_int",
            new MethodInfo("Div", "Real", List.of(), -1));

        realInfo.methods.put("Rem",
            new MethodInfo("Rem", "Real", List.of(), -1));

        realInfo.methods.put("Less",
            new MethodInfo("Less", "Boolean", List.of(), -1));
        realInfo.methods.put("Less_int",
            new MethodInfo("Less", "Boolean", List.of(), -1));

        realInfo.methods.put("LessEqual",
            new MethodInfo("LessEqual", "Boolean", List.of(), -1));
        realInfo.methods.put("LessEqual_int",
            new MethodInfo("LessEqual", "Boolean", List.of(), -1));

        realInfo.methods.put("Greater",
            new MethodInfo("Greater", "Boolean", List.of(), -1));
        realInfo.methods.put("Greater_int",
            new MethodInfo("Greater", "Boolean", List.of(), -1));

        realInfo.methods.put("GreaterEqual",
            new MethodInfo("GreaterEqual", "Boolean", List.of(), -1));
        realInfo.methods.put("GreaterEqual_int",
            new MethodInfo("GreaterEqual", "Boolean", List.of(), -1));

        realInfo.methods.put("Equal",
            new MethodInfo("Equal", "Boolean", List.of(), -1));
        realInfo.methods.put("Equal_int",
            new MethodInfo("Equal", "Boolean", List.of(), -1));

        realInfo.fields.put("Min", new VariableInfo("Min", null, -1));
        realInfo.fields.put("Max", new VariableInfo("Max", null, -1));
        realInfo.fields.put("Epsilon", new VariableInfo("Epsilon", null, -1));

        classes.putIfAbsent("Real", realInfo);

        // === Boolean ===
        ClassInfo boolInfo = new ClassInfo("Boolean", "AnyValue", -1);

        boolInfo.methods.put("toInteger",
            new MethodInfo("toInteger", "Integer", List.of(), -1));

        boolInfo.methods.put("Or",
            new MethodInfo("Or", "Boolean", List.of(), -1));
        boolInfo.methods.put("And",
            new MethodInfo("And", "Boolean", List.of(), -1));
        boolInfo.methods.put("Xor",
            new MethodInfo("Xor", "Boolean", List.of(), -1));
        boolInfo.methods.put("Not",
            new MethodInfo("Not", "Boolean", List.of(), -1));

        classes.putIfAbsent("Boolean", boolInfo);

        // === Array[T] ===
        ClassInfo arrayInfo = new ClassInfo("Array", "AnyRef", -1);

        arrayInfo.methods.put("toList",
            new MethodInfo("toList", "List", List.of(), -1));
        arrayInfo.methods.put("Length",
            new MethodInfo("Length", "Integer", List.of(), -1));
        arrayInfo.methods.put("get",
            new MethodInfo("get", null, List.of(), -1)); // возвращает T — пока без проверки
        arrayInfo.methods.put("set",
            new MethodInfo("set", "Array", List.of(), -1));

        classes.putIfAbsent("Array", arrayInfo);

        // === List[T] ===
        ClassInfo listInfo = new ClassInfo("List", "AnyRef", -1);

        listInfo.methods.put("append",
            new MethodInfo("append", "List", List.of(), -1));
        listInfo.methods.put("head",
            new MethodInfo("head", null, List.of(), -1)); // T
        listInfo.methods.put("tail",
            new MethodInfo("tail", "List", List.of(), -1));

        classes.putIfAbsent("List", listInfo);

        // Пустые заглушки для AnyValue / AnyRef / Class, если нужно
        classes.putIfAbsent("AnyValue", new ClassInfo("AnyValue", null, -1));
        classes.putIfAbsent("AnyRef", new ClassInfo("AnyRef", null, -1));
        classes.putIfAbsent("Class", new ClassInfo("Class", null, -1));
    }

    private VariableInfo createVariableInfoFromVarDecl(VarDeclNode varDecl, int order) {
        ASTNode typeNode = varDecl.type;

        // Если явного типа нет — пытаемся вывести его из инициализатора
        if (typeNode == null) {
            if (varDecl.initializer instanceof ConstructorInvocationNode) {
                // var a is Integer(5)
                typeNode = (ConstructorInvocationNode) varDecl.initializer;
            } else if (varDecl.initializer instanceof IntLiteralNode) {
                // var a is 5
                typeNode = new TypeNode("Integer");
            } else if (varDecl.initializer instanceof RealLiteralNode) {
                // var r is 1.5
                typeNode = new TypeNode("Real");
            } else if (varDecl.initializer instanceof BoolLiteralNode) {
                // var b is true
                typeNode = new TypeNode("Boolean");
            } else if (varDecl.initializer instanceof ExpressionNode expr) {
                String inferred = inferExprType(expr);
                if (inferred != null) {
                    typeNode = new TypeNode(inferred);
                }
            }
        }

        VariableInfo varInfo = new VariableInfo(varDecl.varName, typeNode, order);

        // Попробуем вытащить размер массива/списка из инициализатора
        if (varDecl.initializer instanceof ConstructorInvocationNode cons &&
            cons.arguments != null && !cons.arguments.isEmpty()) {

            ExpressionNode firstArg = cons.arguments.get(0);
            varInfo.arraySize = extractIntegerValue(firstArg);
        }

        return varInfo;
    }

    private boolean hasMethodInHierarchy(String className, String methodName) {
        while (className != null) {
            ClassInfo info = lookupClass(className);
            if (info == null) {
                return false;
            }

            if (info.methods.containsKey(methodName)) {
                return true;
            }

            className = info.extendsClass; // поднимаемся к родителю
        }
        return false;
    }

    private String inferExprType(ExpressionNode e) {
        if (e == null) return null;

        // x, a, p и т.п.
        if (e instanceof IdentifierNode id) {
            VariableInfo var = lookupVariable(id.name);
            if (var != null) return resolveTypeName(var.type);
            return null;
        }

        // Integer(3), Real(1.5), Test(), ...
        if (e instanceof ConstructorInvocationNode c) {
            return c.className;
        }

        // obj.field  или  obj.method(...)
        if (e instanceof MemberAccessNode ma) {
            // obj.field
            if (ma.member instanceof IdentifierNode fieldId) {
                String ownerClass = inferExprType(ma.target); // тип obj
                if (ownerClass == null) return null;

                ClassInfo info = lookupClass(ownerClass);
                if (info == null) return null;

                VariableInfo fieldInfo = info.fields.get(fieldId.name);
                if (fieldInfo == null) return null;

                return resolveTypeName(fieldInfo.type);
            }

            // obj.method(...)
            if (ma.member instanceof MethodInvocationNode mm) {
                String ownerClass = inferExprType(ma.target); // тип obj
                if (ownerClass == null) return null;

                MethodInfo mi = findMethodInClassHierarchy(ownerClass, mm);
                if (mi != null) {
                    return mi.returnType; // "Integer", "Real", "Boolean", "Test", ...
                }
            }
        }

        // standalone вызов: f(...), g(...), fib(...), внутри класса
        if (e instanceof MethodInvocationNode call) {
            if (call.target instanceof IdentifierNode mid) {
                if (!currentClassScope.isEmpty()) {
                    ClassInfo current = currentClassScope.peek();
                    MethodInfo mi = findMethodInClassHierarchy(current.name, call);
                    if (mi != null) {
                        return mi.returnType;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Ищет MethodInfo по имени метода в данном классе и его родителях.
     * Учитывает, что в builtins ключ в map может быть "Plus_real",
     * а logical-имя метода внутри MethodInfo — просто "Plus".
     */
    private MethodInfo findMethodInClassHierarchy(String className, MethodInvocationNode call) {
        String methodName = null;

        if (call.target instanceof IdentifierNode mid) {
            methodName = mid.name;
        } else {
            return null;
        }

        while (className != null) {
            ClassInfo info = lookupClass(className);
            if (info == null) break;

            // 1) прямое совпадение по ключу (например "toBoolean", "Plus")
            MethodInfo direct = info.methods.get(methodName);
            if (direct != null) {
                return direct;
            }

            // 2) запасной вариант: ищем по logical-имени метода
            // (на случай ключей "Plus_real", "Plus_int" и т.п.)
            for (MethodInfo cand : info.methods.values()) {
                if (methodName.equals(cand.name)) {
                    return cand;
                }
            }

            // поднимаемся по иерархии
            className = info.extendsClass;
        }

        return null;
    }

    private void analyzeBodyWithNewScope(BodyNode body) {
        if (body == null || body.elements == null) return;

        variableScopes.push(new HashMap<>());
        currentDeclarationOrder = 0;

        // 1) собрать объявления var
        for (BodyElementNode element : body.elements) {
            if (element instanceof VarDeclNode varDecl) {
                VariableInfo varInfo = createVariableInfoFromVarDecl(varDecl, currentDeclarationOrder);
                variableScopes.peek().put(varDecl.varName, varInfo);
                declarationOrders.put(varDecl, currentDeclarationOrder);
                declarationOrders.put(varInfo, currentDeclarationOrder);
                currentDeclarationOrder++;
            }
        }

        // 2) проверить все элементы
        currentDeclarationOrder = 0;
        for (BodyElementNode element : body.elements) {
            declarationOrders.put(element, currentDeclarationOrder++);
            currentBodyElement = element;
            if (element instanceof ASTNode ast) {
                ast.accept(this);
            }
            currentBodyElement = null;
        }

        variableScopes.pop();
    }

}