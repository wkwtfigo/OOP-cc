package org.example.parser;

import java.util.*;

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
    
    // Built-in classes that don't need declaration checking
    // TODO: обсудить с Лизой
    private static final Set<String> BUILTIN_CLASSES = Set.of(
        "Integer", "Real", "Boolean", "String", "List", "Object", "AnyValue"
    );
    
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
        String typeName;
        int declarationOrder;
        boolean isInitialized;
        
        VariableInfo(String name, String typeName, int order) {
            this.name = name;
            this.typeName = typeName;
            this.declarationOrder = order;
            this.isInitialized = false;
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
        
        MethodInfo(String name, String returnType, List<ParamDeclNode> parameters, int order) {
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
     * Check if a variable is declared before use
     */
    private void checkVariableUsage(String varName, ASTNode usageNode) {
        VariableInfo varInfo = lookupVariable(varName);
        if (varInfo == null) {
            reportError("Variable '" + varName + "' is used before declaration");
            return;
        }
        
        // Check declaration order within the same scope
        Integer varOrder = declarationOrders.get(varInfo);
        Integer useOrder = declarationOrders.get(usageNode);
        
        if (varOrder != null && useOrder != null && varOrder > useOrder) {
            reportError("Variable '" + varName + "' is used before declaration");
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
        // Since classes are processed first, this check is mainly for forward references
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
        if (methodInfo == null) { //TODO
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
     */
    private Integer extractArrayLength(ExpressionNode expr) {
        // If it's a constructor invocation like List[Integer](...), we might be able to determine length
        // For now, return null to indicate we can't determine at compile time
        return null;
    }
    
    /**
     * Extract array index from an expression (if it's a compile-time constant)
     */
    private Integer extractConstantIndex(ExpressionNode expr) {
        if (expr instanceof IntLiteralNode) {
            return ((IntLiteralNode) expr).value;
        }
        return null;
    }
    
    /**
     * Check array bounds (if array access syntax exists)
     */
    private void checkArrayBounds(ExpressionNode arrayExpr, ExpressionNode indexExpr) {
        Integer index = extractConstantIndex(indexExpr);
        if (index != null) {
            if (index < 0) {
                reportError("Array index must be non-negative, found: " + index);
            }
            // If we can determine array length at compile time, check bounds
            Integer length = extractArrayLength(arrayExpr);
            if (length != null && index >= length) {
                reportError("Array index out of bounds: index " + index + " >= length " + length);
            }
        }
    }
    
    @Override
    public void visit(ProgramNode node) {
        // First pass: collect all class declarations
        if (node.classes != null) {
            currentDeclarationOrder = 0;
            for (ClassDeclNode classDecl : node.classes) {
                ClassInfo classInfo = new ClassInfo(classDecl.className, classDecl.extendsClass, currentDeclarationOrder++);
                classes.put(classDecl.className, classInfo);
                declarationOrders.put(classDecl, classInfo.declarationOrder);
            }
            
            // Second pass: process classes and check declarations
            for (ClassDeclNode classDecl : node.classes) {
                currentDeclarationOrder = 0; // Reset for each class scope
                classDecl.accept(this);
            }
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
                    VariableInfo varInfo = new VariableInfo(
                        varDecl.varName, 
                        varDecl.typeName, 
                        currentDeclarationOrder
                    );
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
                        currentDeclarationOrder
                    );
                    classInfo.methods.put(methodDecl.header.methodName, methodInfo);
                    declarationOrders.put(methodDecl, currentDeclarationOrder);
                    declarationOrders.put(methodInfo, currentDeclarationOrder);
                    currentDeclarationOrder++;
                } else if (member instanceof ConstructorDeclNode) {
                    ConstructorDeclNode constructorDecl = (ConstructorDeclNode) member;
                    ConstructorInfo constructorInfo = new ConstructorInfo(
                        constructorDecl.parameters,
                        currentDeclarationOrder
                    );
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
                    currentDeclarationOrder
                );
                variableScopes.peek().put(param.paramName, paramInfo);
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
        // MethodBodyNode is visited after MethodDeclNode, so we should already have
        // the method parameters in the scope from visit(MethodDeclNode)
        // But we need to ensure scope is set up
        
        if (node.isArrow && node.arrowExpression != null) {
            declarationOrders.put(node.arrowExpression, currentDeclarationOrder++);
            node.arrowExpression.accept(this);
        } else if (!node.isArrow && node.body != null) {
            // First pass: collect variable declarations
            if (node.body.elements != null) {
                currentDeclarationOrder = 0;
                for (BodyElementNode element : node.body.elements) {
                    if (element instanceof VarDeclNode) {
                        VarDeclNode varDecl = (VarDeclNode) element;
                        VariableInfo varInfo = new VariableInfo(
                            varDecl.varName,
                            varDecl.typeName,
                            currentDeclarationOrder
                        );
                        variableScopes.peek().put(varDecl.varName, varInfo);
                        declarationOrders.put(varDecl, currentDeclarationOrder);
                        declarationOrders.put(varInfo, currentDeclarationOrder);
                        currentDeclarationOrder++;
                    }
                }
                
                // Second pass: visit all elements to check usage
                currentDeclarationOrder = 0;
                for (BodyElementNode element : node.body.elements) {
                    declarationOrders.put(element, currentDeclarationOrder++);
                    if (element instanceof ASTNode) {
                        ((ASTNode) element).accept(this);
                    }
                }
            }
        }
    }
    
    @Override
    public void visit(ParamDeclNode node) {
        // Parameters are added to scope in visit(MethodDeclNode) or visit(ConstructorDeclNode)
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
                    currentDeclarationOrder
                );
                variableScopes.peek().put(param.paramName, paramInfo);
                declarationOrders.put(param, currentDeclarationOrder);
                declarationOrders.put(paramInfo, currentDeclarationOrder);
                currentDeclarationOrder++;
            }
        }
        
        // Visit body
        if (node.body != null) {
            // First pass: collect variable declarations
            if (node.body.elements != null) {
                currentDeclarationOrder = 0;
                for (BodyElementNode element : node.body.elements) {
                    if (element instanceof VarDeclNode) {
                        VarDeclNode varDecl = (VarDeclNode) element;
                        VariableInfo varInfo = new VariableInfo(
                            varDecl.varName,
                            varDecl.typeName,
                            currentDeclarationOrder
                        );
                        variableScopes.peek().put(varDecl.varName, varInfo);
                        declarationOrders.put(varDecl, currentDeclarationOrder);
                        declarationOrders.put(varInfo, currentDeclarationOrder);
                        currentDeclarationOrder++;
                    }
                }
                
                // Second pass: visit all elements to check usage
                currentDeclarationOrder = 0;
                for (BodyElementNode element : node.body.elements) {
                    declarationOrders.put(element, currentDeclarationOrder++);
                    if (element instanceof ASTNode) {
                        ((ASTNode) element).accept(this);
                    }
                }
            }
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
        // Check that variable is declared before assignment
        checkVariableUsage(node.varName, node);
        
        // Check expression
        if (node.expression != null) {
            declarationOrders.put(node.expression, currentDeclarationOrder++);
            node.expression.accept(this);
        }
    }
    
    @Override
    public void visit(WhileLoopNode node) {
        if (node.condition != null) {
            declarationOrders.put(node.condition, currentDeclarationOrder++);
            node.condition.accept(this);
        }
        if (node.body != null) {
            variableScopes.push(new HashMap<>());
            currentDeclarationOrder = 0; // Reset for loop body
            node.body.accept(this);
            variableScopes.pop();
        }
    }
    
    @Override
    public void visit(IfStatementNode node) {
        if (node.condition != null) {
            declarationOrders.put(node.condition, currentDeclarationOrder++);
            node.condition.accept(this);
        }
        if (node.thenBody != null) {
            variableScopes.push(new HashMap<>());
            currentDeclarationOrder = 0; // Reset for then body
            node.thenBody.accept(this);
            variableScopes.pop();
        }
        if (node.elseBody != null) {
            variableScopes.push(new HashMap<>());
            currentDeclarationOrder = 0; // Reset for else body
            node.elseBody.accept(this);
            variableScopes.pop();
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
        } else if (node.member instanceof MethodInvocationNode) {
            // Method call on an object - don't check member as variable
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
                checkArrayBounds(node.target, indexExpr);
            }
        } else {
            // Other types - visit normally
            if (node.member != null) {
                declarationOrders.put(node.member, currentDeclarationOrder++);
                node.member.accept(this);
            }
        }
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
        // Check that variable is declared before use
        checkVariableUsage(node.name, node);
        
        // Check for potential array access patterns
        // If this identifier is used in an array context, we'll handle bounds checking
        // For now, we just track usage
        declarationOrders.put(node, currentDeclarationOrder++);
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
}

