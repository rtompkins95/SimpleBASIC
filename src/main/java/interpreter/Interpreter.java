package interpreter;

import node.*;

import java.util.*;


public class Interpreter implements StatementVisitor {
    private final ProgramNode programNode;
    private final Map<String, LabeledStatementNode> labels = new HashMap<>();
    private final Queue<Node> dataQueue = new LinkedList<>();

    private final Stack<StatementNode> stack = new Stack<>();

    private final Map<String, Integer> intVariables = new HashMap<>();
    private final Map<String, String> stringVariables = new HashMap<>();
    private final Map<String, Float> floatVariables = new HashMap<>();

    private final Set<String> whileLabels = new HashSet<>();

    private final Scanner scanner = new Scanner(System.in);

    private boolean testMode = false;
    private List<String> testInput = new ArrayList<>();
    private final List<String> output = new ArrayList<>();

    private boolean isDone = false;

    public Interpreter(ProgramNode programNode) {
        this.programNode = programNode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public void setTestInput(List<String> input) {
        this.testInput = new ArrayList<>(input);
    }

    public List<String> getTestInput() {
        return this.testInput;
    }

    public List<String> getOutput() {
        return output;
    }

    private void visitStatements() {
        List<StatementNode> statements = programNode.getStatements();
        StatementNode prev = null;
        for (StatementNode curr : statements) {
            curr.accept(this);

            if (prev != null) {
                prev.setNext(curr);
            }

            prev = curr;
        }
    }

    // Interprets the program
    public void interpret() {
        if (programNode.getStatements().isEmpty()) {
            return;
        }

        visitStatements();

        StatementNode curr = programNode.getStatements().get(0);
        StatementNode next;
        while (!isDone && curr != null) {
            next = curr.interpret(this);
            curr = next;
        }
    }

    public StatementNode assignmentStatement(AssignmentNode assignmentNode) {
        String name = assignmentNode.getVariableNode().getName();
        Object value = evaluate(assignmentNode.getValue());
        InterpreterDataType type = assignmentNode.getVariableNode().getType();

        if (type == InterpreterDataType.INTEGER && value instanceof Integer) {
            intVariables.put(name, (Integer) value);
        } else if (type == InterpreterDataType.FLOAT && value instanceof Float) {
            floatVariables.put(name, (Float) value);
        } else if (type == InterpreterDataType.STRING && value instanceof String) {
            stringVariables.put(name, (String) value);
        } else {
            throw new IllegalArgumentException(String.format("Cannot assign '%s' to variable '%s' with type '%s'", value, name, type));
        }
        return assignmentNode.getNext();
    }

    // Prints the prompt. Reads data and sets the variable(s)
    // If in test mode, reads from the test input list
    public StatementNode inputStatement(InputNode inputNode) {
        if (!testMode) {
            System.out.print(inputNode.getPrompt().getValue());
        }
        for (VariableNode variableNode : inputNode.getVariables()) {
            String name = variableNode.getName();
            InterpreterDataType type = variableNode.getType();
            String inputValue = testMode ? testInput.remove(0) : scanner.nextLine();
            switch (type) {
                case INTEGER:
                    intVariables.put(name, Integer.parseInt(inputValue));
                    break;
                case FLOAT:
                    floatVariables.put(name, Float.parseFloat(inputValue));
                    break;
                case STRING:
                    stringVariables.put(name, inputValue);
                    break;
            }
        }
        return inputNode.getNext();
    }

    public StatementNode readStatement(ReadNode readNode) {
        for (VariableNode variableNode: readNode.getVariables()) {
            if (dataQueue.isEmpty()) {
                throw new IllegalStateException("Cannot read from empty DATA queue");
            }

            String name = variableNode.getName();
            InterpreterDataType type = variableNode.getType();
            Node value = dataQueue.poll();

            if (type == InterpreterDataType.STRING && value instanceof StringNode) {
                stringVariables.put(name, ((StringNode) value).getValue());
            } else if (type == InterpreterDataType.FLOAT && value instanceof FloatNode) {
                floatVariables.put(name, ((FloatNode) value).getFloat());
            } else if (type == InterpreterDataType.INTEGER && value instanceof IntegerNode) {
                intVariables.put(name, ((IntegerNode) value).getInt());
            } else {
                throw new IllegalArgumentException(String.format("Cannot assign value '%s' to variable '%s' of type '%s'", value, name, type));
            }
        }
        return readNode.getNext();
    }

    public StatementNode printStatement(PrintNode printNode) {
        for (Node arg: printNode.getParameters()) {
            if (testMode) {
                output.add(evaluate(arg).toString());
            } else {
                System.out.print(evaluate(arg));
            }
        }
        System.out.println();
        return printNode.getNext();
    }

    public StatementNode ifStatement(IfNode ifNode) {
        String label = ifNode.getLabel();

        // Jump to this statement if any of the conditions are true
        LabeledStatementNode labeledStatement = labels.get(label);

        return evaluateBoolean(ifNode.getCondition()) ? labeledStatement : ifNode.getNext();
    }

    public boolean evaluateBoolean(BooleanExpressionNode booleanExpressionNode) {
        Object left = evaluate(booleanExpressionNode.getLeft());
        Object right = evaluate(booleanExpressionNode.getRight());
        BooleanExpressionNode.OPERATOR operator = booleanExpressionNode.getOperator();

        if (!isInteger(left, right) && !isNumeric(left, right)) {
            System.out.println(left.getClass());
            System.out.println(right.getClass());
            throw new RuntimeException(String.format("Unsupported comparison %s with operands: '%s' and '%s'", operator, left, right));
        }

        if (operator == BooleanExpressionNode.OPERATOR.LESSTHAN) {
            if (isInteger(left, right)) {
                return (Integer) left < (Integer) right;
            } else if (isNumeric(left, right)) {
                return ((Number) left).floatValue() < ((Number) right).floatValue();
            }
        } else if (operator == BooleanExpressionNode.OPERATOR.LESSTHANEQUALTO) {
            if (isInteger(left, right)) {
                return (Integer) left <= (Integer) right;
            } else if (isNumeric(left, right)) {
                return ((Number) left).floatValue() <= ((Number) right).floatValue();
            }
        } else if (operator == BooleanExpressionNode.OPERATOR.GREATERTHAN) {
            if (isInteger(left, right)) {
                return (Integer) left > (Integer) right;
            } else if (isNumeric(left, right)) {
                return ((Number) left).floatValue() > ((Number) right).floatValue();
            }
        } else if (operator == BooleanExpressionNode.OPERATOR.GREATERTHANEQUALTO) {
            if (isInteger(left, right)) {
                return (Integer) left >= (Integer) right;
            } else if (isNumeric(left, right)) {
                return ((Number) left).floatValue() >= ((Number) right).floatValue();
            }
        } else if (operator == BooleanExpressionNode.OPERATOR.NOTEQUALS) {
            if (isInteger(left, right)) {
                return !left.equals(right);
            } else if (isNumeric(left, right)) {
                return ((Number) left).floatValue() != ((Number) right).floatValue();
            }
        } else if (operator == BooleanExpressionNode.OPERATOR.EQUALS) {
            if (isInteger(left, right)) {
                return left.equals(right);
            } else if (isNumeric(left, right)) {
                return ((Number) left).floatValue() == ((Number) right).floatValue();
            }
        } else {
            throw new RuntimeException(String.format("Invalid boolean comparison: %s", operator));
        }
        return false;
    }

    public StatementNode goSubStatement(GoSubNode goSubNode) {
        stack.push(goSubNode.getNext());
        return labels.get(goSubNode.getLabel());
    }

    public StatementNode goToStatement(GoToNode goToNode) {
        if (!labels.containsKey(goToNode.getLabel())) {
            throw new RuntimeException(String.format("No matching labeled statement '%s' in 'GOTO' statement", goToNode.getLabel()));
        }
        return labels.get(goToNode.getLabel());
    }

    public StatementNode returnStatement(ReturnNode returnNode) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("'RETURN' statement without matching 'GOSUB'");
        }
        return stack.pop();
    }

    public StatementNode labeledStatement(LabeledStatementNode labeledStatementNode) {
        // Check if this label marks the end of a while loop
        if (whileLabels.contains(labeledStatementNode.getLabel())) {
            return stack.pop();
        }

        StatementNode statementNode = labeledStatementNode.getStatementNode();
        if (statementNode == null) {
            return labeledStatementNode.getNext();
        }

        // Evaluate the inner statement null
        statementNode.interpret(this);

        return labeledStatementNode.getNext();
    }

    public StatementNode nextStatement(NextNode nextNode) {
        if (stack.empty()) {
            throw new RuntimeException("NEXT statement must have matching FOR loop declaration");
        }
        if (nextNode.getVariable() == null) {
            throw new RuntimeException("NEXT statement must reference the iterator in a matching FOR loop declaration");
        }

        StatementNode forNode = stack.peek();
        if (!(forNode instanceof ForNode)) {
            throw new RuntimeException("NEXT statement must have matching FOR loop declaration");
        }

        String forNodeVariable = ((ForNode) forNode).getVariable().getName();
        String nextNodeVariable = nextNode.getVariable().getName();
        if (!forNodeVariable.equals(nextNodeVariable)) {
            throw new RuntimeException(String.format("'NEXT %s' does not match FOR loop iterator: '%s'", nextNodeVariable, forNodeVariable));
        }
        return stack.pop();
    }

    public StatementNode endStatement(EndNode endNode) {
        isDone = true;
        return endNode.getNext();
    }

    public StatementNode forStatement(ForNode forNode) {
        VariableNode variableNode = forNode.getVariable();
        String counterName = variableNode.getName();
        boolean firstIteration = false;

        // Initialize the counter variable one the first iteration
        if (!intVariables.containsKey(variableNode.getName())) {
            firstIteration = true;
            Integer startValue = (Integer) evaluate(forNode.getInitialValue());
            intVariables.put(counterName, startValue);
        }

        Integer counter = intVariables.get(counterName);
        Integer limit = (Integer) evaluate(forNode.getLimit());
        Integer step = (Integer) evaluate(forNode.getIncrement());

        // Keep iterating, increment counter and push the ForNode onto the stack
        if (counter < limit) {
            stack.push(forNode);
            intVariables.put(counterName, firstIteration ? counter: counter + step);
            return forNode.getNext();
        }

        // Otherwise, end the loop, skip to the first NEXT statement
        StatementNode curr = forNode;
        while (!(curr instanceof NextNode)) {
            curr = curr.getNext();
        }
        return curr.getNext();
    }

    public StatementNode whileStatement(WhileNode whileNode) {
        boolean shouldContinue = (Boolean) evaluate(whileNode.getCondition());
        if (shouldContinue) {
            stack.push(whileNode);
            return whileNode.getNext();
        }

        // Otherwise, end the loop, skip to end label and return its next node
        StatementNode curr = whileNode;
        while (true) {
            if (curr instanceof LabeledStatementNode) {
                LabeledStatementNode labeledStatementNode = (LabeledStatementNode) curr;
                if (whileLabels.contains(labeledStatementNode.getLabel())) {
                    break;
                }
            }
            curr = curr.getNext();
        }
        return curr.getNext();
    }

    public void visit(LabeledStatementNode labeledStatementNode) {
        labels.put(labeledStatementNode.getLabel(), labeledStatementNode);
    }

    public void visit(DataNode dataNode){
        this.dataQueue.addAll(dataNode.getData());
    }

    public void visit(WhileNode whileNode){
        this.whileLabels.add(whileNode.getLabel());
    }

    private Object evaluate(Node node) {

        if (node instanceof IntegerNode) {
            IntegerNode integerNode = (IntegerNode) node;
            return integerNode.getInt();
        }

        if (node instanceof FloatNode) {
            FloatNode floatNode = (FloatNode) node;
            return floatNode.getFloat();
        }

        if (node instanceof StringNode) {
            StringNode stringNode = (StringNode) node;
            return stringNode.getValue();
        }

        if (node instanceof BooleanExpressionNode) {
            return evaluateBoolean((BooleanExpressionNode) node);
        }

        // Evaluates the variable and returns the value
        if (node instanceof VariableNode) {
            VariableNode variableNode = (VariableNode) node;
            String name = variableNode.getName();
            InterpreterDataType type = variableNode.getType();
            if (type == InterpreterDataType.INTEGER && intVariables.containsKey(name)) {
                return intVariables.get(name);
            } else if (type == InterpreterDataType.FLOAT && floatVariables.containsKey(name)) {
                return floatVariables.get(name);
            } else if (type == InterpreterDataType.STRING && stringVariables.containsKey(name)) {
                return stringVariables.get(name);
            } else {
                throw new IllegalArgumentException(String.format("Variable '%s' is not defined", name));
            }
        }

        // Evaluates the function and returns the value
        if (node instanceof FunctionNode) {
            // evaluate parameters and call the right function based on name
            FunctionNode functionNode = (FunctionNode) node;
            BuiltInFunctions.FUNCTION functionName = functionNode.getFunctionName();
            List<Node> parameters = functionNode.getParameters();

            if (functionName == BuiltInFunctions.FUNCTION.RANDOM) {
                return random(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.RANDOMF) {
                return randomf(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.LEFT$) {
                return left$(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.RIGHT$) {
                return right$(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.MID$) {
                return mid$(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.NUM$) {
                return num$(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.VAL) {
                return val(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.VALF) {
                return valf(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.POW) {
                return pow(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.POWF) {
                return powf(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.INT) {
                return _int(parameters);
            }
            if (functionName == BuiltInFunctions.FUNCTION.FLOAT) {
                return _float(parameters);
            }
        }

        // Evaluates the math operation and returns the value
        if (node instanceof MathOpNode) {
            MathOpNode mathOpNode = (MathOpNode) node;
            Object left = evaluate(mathOpNode.getLeft());
            Object right = evaluate(mathOpNode.getRight());

            if (!isNumeric(left, right)) {
                throw new IllegalArgumentException(String.format("Illegal math operation for arguments: \n%s\n%s\n", left, right));
            }

            if (mathOpNode.getOperator() == MathOpNode.OPERATION.ADD) {
                if (isInteger(left, right)) {
                    return (Integer) left + (Integer) right;
                } else if (isNumeric(left, right)) {
                    return ((Number) left).floatValue() + ((Number) right).floatValue();
                }
            } else if (mathOpNode.getOperator() == MathOpNode.OPERATION.SUBTRACT) {
                if (isInteger(left, right)) {
                    return (Integer) left - (Integer) right;
                } else if (isNumeric(left, right)) {
                    return ((Number) left).floatValue() - ((Number) right).floatValue();
                }
            } else if (mathOpNode.getOperator() == MathOpNode.OPERATION.MULTIPLY) {
                if (isInteger(left, right)) {
                    return (Integer) left * (Integer) right;
                } else if (isNumeric(left, right)) {
                    return ((Number) left).floatValue() * ((Number) right).floatValue();
                }
            } else if (mathOpNode.getOperator() == MathOpNode.OPERATION.DIVIDE) {
                if (isInteger(left, right)) {
                    return (Integer) left / (Integer) right;
                } else if (isNumeric(left, right)) {
                    return ((Number) left).floatValue() / ((Number) right).floatValue();
                }
            }
        }

        // Evaluates the expression and returns the value
        if (node instanceof ExpressionNode) {
            ExpressionNode expressionNode = (ExpressionNode) node;
            return evaluate(expressionNode.getNode());
        }
        // Evaluates the term and returns the value
        if (node instanceof TermNode) {
            TermNode termNode = (TermNode) node;
            return evaluate(termNode.getNode());
        }
        // Evaluates the factor and returns the value
        if (node instanceof FactorNode)  {
            FactorNode factorNode = (FactorNode) node;
            return evaluate(factorNode.getNode());
        }
        throw new RuntimeException(String.format("Unsupported node: %s", node));
    }

    private Integer random(List<Node> parameters) {
        if (!parameters.isEmpty() && parameters.size() != 2) {
            throw new RuntimeException("RANDOM() expects zero or two parameters: (min, max)");
        } else if (parameters.size() == 2) {
            int min = (Integer) evaluate(parameters.get(0));
            int max = (Integer) evaluate(parameters.get(1));
            return BuiltInFunctions.RANDOM(min, max);
        }
        return BuiltInFunctions.RANDOM();
    }

    private Float randomf(List<Node> parameters) {
        if (!parameters.isEmpty() && parameters.size() != 2) {
            throw new RuntimeException("RANDOMF() expects zero or two parameters: (min, max)");
        } else if (parameters.size() == 2) {
            float min = (Float) evaluate(parameters.get(0));
            float max = (Float) evaluate(parameters.get(1));
            return BuiltInFunctions.RANDOMF(min, max);
        }
        return BuiltInFunctions.RANDOMF();
    }

    private String left$(List<Node> parameters) {
        Object str = evaluate(parameters.get(0));
        Object n = evaluate(parameters.get(1));
        if (!(str instanceof String && n instanceof Integer)) {
            throw new RuntimeException(String.format("Cannot use values: %s and %s for builtin function LEFT$(string, integer)", str, n));
        }
        return BuiltInFunctions.LEFT$((String) str, (Integer) n);
    }

    private String right$(List<Node> parameters) {
        Object str = evaluate(parameters.get(0));
        Object n = evaluate(parameters.get(1));
        if (!(str instanceof String && n instanceof Integer)) {
            throw new RuntimeException(String.format("Cannot use values: %s and %s for builtin function RIGHT$(string, integer)", str, n));
        }
        return BuiltInFunctions.RIGHT$((String) str, (Integer) n);
    }

    private String mid$(List<Node> parameters) {
        Object str = evaluate(parameters.get(0));
        Object start = evaluate(parameters.get(1));
        Object count = evaluate(parameters.get(2));
        if (!(str instanceof String && start instanceof Integer && count instanceof Integer)) {
            throw new RuntimeException(String.format("Cannot use values: %s, %s, %s for builtin function MID$(string, integer, integer)", str, start, count));
        }

        return BuiltInFunctions.MID$((String) str, (Integer) start, (Integer) count);
    }

    private String num$(List<Node> parameters) {
        Object num = evaluate(parameters.get(0));
        if (!(num instanceof Number)) {
            throw new RuntimeException(String.format("Cannot use value: %s for builtin function NUM$(integer/float)", num));
        }
        return BuiltInFunctions.NUM$((Number) num);
    }

    private Integer val(List<Node> parameters) {
        Object str = evaluate(parameters.get(0));
        if (!(str instanceof String)) {
            throw new RuntimeException(String.format("Cannot use value: %s for builtin function: ", str) +  "VAL(string)");
        }
        return BuiltInFunctions.VAL((String) str);
    }

    private Float valf(List<Node> parameters) {
        Object str = evaluate(parameters.get(0));
        if (!(str instanceof String)) {
            throw new RuntimeException(String.format("Cannot use value: %s for builtin function: ", str) +  "VAL%(string)");
        }
        return BuiltInFunctions.VALF((String) str);
    }

    private Integer pow(List<Node> parameters) {
        Object a = evaluate(parameters.get(0));
        Object b = evaluate(parameters.get(1));
        if (!(a instanceof Integer && b instanceof Integer)) {
            throw new RuntimeException(String.format("Cannot use values: %s and %s for builtin function POW$(integer, integer)", a, b));
        }
        return BuiltInFunctions.POW((Integer) a, (Integer) b);
    }

    private Float powf(List<Node> parameters) {
        Object a = evaluate(parameters.get(0));
        Object b = evaluate(parameters.get(1));
        if (!(a instanceof Float && b instanceof Float)) {
            throw new RuntimeException(String.format("Cannot use values: %s and %s for builtin function POWF(float, float)", a, b));
        }
        return BuiltInFunctions.POWF((Float) a, (Float) b);
    }

    private Integer _int(List<Node> parameters) {
        Object n = evaluate(parameters.get(0));
        if (!(n instanceof Number)) {
            throw new RuntimeException(String.format("Cannot use value: %s for builtin INT(int/float)", n));
        }
        return BuiltInFunctions.INT((Number) n);
    }

    private Float _float(List<Node> parameters) {
        Object n = evaluate(parameters.get(0));
        if (!(n instanceof Number)) {
            throw new RuntimeException(String.format("Cannot use value: %s for builtin FLOAT(int/float)", n));
        }
        return BuiltInFunctions.FLOAT((Number) n);
    }

    // Checks if both arguments are integers
    private boolean isInteger(Object left, Object right) {
        return left instanceof Integer && right instanceof Integer;
    }

    // Checks if both arguments are numeric
    private boolean isNumeric(Object left, Object right) {
        return (left instanceof Float || left instanceof Integer) &&
                (right instanceof Float || right instanceof Integer);
    }

    public Map<String, Integer> getIntVariables() {
        return intVariables;
    }

    public Map<String, String> getStringVariables() {
        return stringVariables;
    }

    public Map<String, Float> getFloatVariables() {
        return floatVariables;
    }

    public Map<String, LabeledStatementNode> getLabels() {
        return labels;
    }

    public Queue<Node> getDataQueue() {
        return dataQueue;
    }
}
