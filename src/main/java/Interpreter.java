import java.util.*;


public class Interpreter {
    private final ProgramNode programNode;
    private final StatementVisitorImpl statementVisitor;
    private Map<String, LabeledStatementNode> labels;
    private Queue<Node> dataQueue;

    private Stack<StatementNode> stack = new Stack<>();

    private Map<String, Integer> intVariables = new HashMap<>();
    private Map<String, String> stringVariables = new HashMap<>();
    private Map<String, Float> floatVariables = new HashMap<>();

    private Set<String> whileLabels = new HashSet<>();

    private final Scanner scanner = new Scanner(System.in);

    private boolean testMode = false;
    private List<String> testInput = new ArrayList<>();
    private final List<String> output = new ArrayList<>();

    private boolean isDone = false;

    public Interpreter(ProgramNode programNode) {
        this.programNode = programNode;
        this.statementVisitor = new StatementVisitorImpl();
    }

    // Sets the test mode
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    // Sets the input
    public void setTestInput(List<String> input) {
        this.testInput = new ArrayList<>(input);
    }

    public List<String> getTestInput() {
        return this.testInput;
    }

    // Gets the output
    public List<String> getOutput() {
        return output;
    }

    // Visits the statements in the program
    private void visitStatements() {
        List<StatementNode> statements = programNode.getStatements();
        StatementNode prev = null;
        for (StatementNode curr : statements) {
            curr.accept(statementVisitor);

            if (prev != null) {
                prev.setNext(curr);
            }

            prev = curr;
        }

        intVariables = statementVisitor.getIntVariables();
        floatVariables = statementVisitor.getFloatVariables();
        stringVariables = statementVisitor.getStringVariables();
        dataQueue = statementVisitor.getDataNodes();
        labels = statementVisitor.getLabels();
        whileLabels = statementVisitor.getWhileLabels();
    }

    // Interprets the program
    public void interpret() {
        if (programNode.getStatements().isEmpty()) {
            return;
        }

        visitStatements();

        StatementNode curr = programNode.getStatements().get(0);
        StatementNode next;
        while (!isDone) {
            next = interpretStatement(curr);
            curr = next;
        }
    }

    private StatementNode interpretStatement(StatementNode statement) {
        if (statement instanceof AssignmentNode) {
            return assignment((AssignmentNode) statement);
        } else if (statement instanceof ReadNode) {
            // read data from the data queue and assign it to variables
            return read((ReadNode) statement);
        } else if (statement instanceof InputNode) {
            // read data from the data queue and assign it to variables
            return input((InputNode) statement);
        } else if (statement instanceof PrintNode) {
            // read data from the data queue and assign it to variables
            return print((PrintNode) statement);
        } else if (statement instanceof IfNode) {
            return ifStatement((IfNode) statement);
        } else if (statement instanceof GoSubNode) {
            return goSubStatement((GoSubNode) statement);
        } else if (statement instanceof GoToNode) {
            return goToStatement((GoToNode) statement);
        } else if (statement instanceof ReturnNode) {
            return returnStatement((ReturnNode) statement);
        } else if (statement instanceof ForNode) {
            return forStatement((ForNode) statement);
        } else if (statement instanceof WhileNode) {
            return whileStatement((WhileNode) statement);
        } else if (statement instanceof NextNode) {
            return nextStatement((NextNode) statement);
        } else if (statement instanceof LabeledStatementNode) {
            return labeledStatement((LabeledStatementNode) statement);
        } else if (statement instanceof EndNode) {
            return endStatement((EndNode) statement);
        }

        return new EndNode();
    }

    // Evaluates assignment statements and assigns the value to the variable
    private StatementNode assignment(AssignmentNode assignmentNode) {
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
            throw new IllegalArgumentException(String.format("Cannot assign %s to variable '%s' with type %s", value, name, type));
        }
        return assignmentNode.getNext();
    }

    // Prints the prompt. Reads data and sets the variable(s)
    // If in test mode, reads from the test input list
    public StatementNode input(InputNode inputNode) {
        if (testMode) {
            for (VariableNode variableNode : inputNode.getVariables()) {
                String name = variableNode.getName();
                InterpreterDataType type = variableNode.getType();
                System.out.print(testInput.get(0));
                String inputValue = testInput.remove(0); // Get and remove the first element from the test input list
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
        } else {
            System.out.print(inputNode.getPrompt().getValue());
            for (VariableNode variableNode : inputNode.getVariables()) {
                String name = variableNode.getName();
                InterpreterDataType type = variableNode.getType();
                switch (type) {
                    case INTEGER:
                        intVariables.put(name, Integer.parseInt(scanner.nextLine()));
                        break;
                    case FLOAT:
                        floatVariables.put(name, Float.parseFloat(scanner.nextLine()));
                        break;
                    case STRING:
                        stringVariables.put(name, scanner.nextLine());
                        break;
                }
            }
        }
        return inputNode.getNext();
    }

    // Reads and removes from internal collection. Updates variable(s)
    private StatementNode read(ReadNode readNode) {
        for (VariableNode variableNode: readNode.getVariables()) {
            if (dataQueue.isEmpty()) {
                throw new IllegalStateException("Cannot read from empty DATA queue");
            }

            String name = variableNode.getName();
            InterpreterDataType type = variableNode.getType();
            Node value = dataQueue.poll();

            if (type == InterpreterDataType.STRING  && value instanceof StringNode) {
                stringVariables.put(name, ((StringNode) value).getValue());
            } else if (type == InterpreterDataType.FLOAT && value instanceof FloatNode) {
                floatVariables.put(name, ((FloatNode) value).getFloat());
            } else if (type == InterpreterDataType.INTEGER && value instanceof IntegerNode) {
                intVariables.put(name, ((IntegerNode) value).getInt());
            } else {
                throw new IllegalArgumentException(String.format("Cannot assign value: %s to variable %s of type: %s", value, name, type));
            }
        }
        return readNode.getNext();
    }

    // Prints each data item in the PrintNode
    public StatementNode print(PrintNode printNode) {
        for (Node arg: printNode.getArguments()) {
            if (testMode) {
                output.add(evaluate(arg).toString());
            } else {
                System.out.print(evaluate(arg));
            }
        }
        System.out.println();
        return printNode.getNext();
    }

    private StatementNode ifStatement(IfNode ifNode) {
        String label = ifNode.getLabel();

        // Jump to this statement if any of the conditions are true
        StatementNode labeledStatement = labels.get(label).getStatementNode();

        return evaluateBoolean(ifNode.getCondition()) ? labeledStatement : ifNode.getNext();
    }

    private boolean evaluateBoolean(BooleanExpressionNode booleanExpressionNode) {
        Object left = evaluate(booleanExpressionNode.getLeft());
        Object right = evaluate(booleanExpressionNode.getRight());
        BooleanExpressionNode.OPERATOR operator = booleanExpressionNode.getOperator();

        if (!isInteger(left, right) || !isNumeric(left, right)) {
            throw new RuntimeException(String.format("Unsupported comparison for operands: '%s' and '%s'", left, right));
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
            throw new RuntimeException(String.format("Unsupported comparison: %s", operator));
        }
        return false;
    }

    private StatementNode goSubStatement(GoSubNode goSubNode) {
        stack.push(goSubNode.getNext());
        return labels.get(goSubNode.getLabel());
    }

    private StatementNode goToStatement(GoToNode goToNode) {
        if (!labels.containsKey(goToNode.getLabel())) {
            throw new RuntimeException(String.format("No labeled statement for GOTO: %s", goToNode.getLabel()));
        }
        return labels.get(goToNode.getLabel());
    }

    private StatementNode returnStatement(ReturnNode returnNode) {
        return stack.pop();
    }

    private StatementNode labeledStatement(LabeledStatementNode labeledStatementNode) {
        // Check if this label marks the end of a while loop
        if (whileLabels.contains(labeledStatementNode.getLabel())) {
            return stack.pop();
        }

        // TODO this should maybe throw an error
        StatementNode statementNode = labeledStatementNode.getStatementNode();
        if (statementNode == null) {
            return labeledStatementNode.getNext();
        }

        // Evaluate the inner statement null
        interpretStatement(statementNode);

        return labeledStatementNode.getNext();
    }

    private StatementNode nextStatement(NextNode nextNode) {
        return stack.pop();
    }

    private StatementNode endStatement(EndNode endNode) {
        isDone = true;
        return endNode.getNext();
    }

    private StatementNode forStatement(ForNode forNode) {
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

    private StatementNode whileStatement(WhileNode whileNode) {
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

    // Evaluates the node and returns the value
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
            String functionName = functionNode.getFunctionName();
            List<Node> parameters = functionNode.getParameters();
            if (functionName.equals("RANDOM")) {
                return BuiltInFunctions.RANDOM();
            }
            if (functionName.equals("LEFT$")) {
                String str = (String) evaluate(parameters.get(0));
                int n = (Integer) evaluate(parameters.get(1));
                return BuiltInFunctions.LEFT$(str, n);
            }
            if (functionName.equals("RIGHT$")) {
                String str = (String) evaluate(parameters.get(0));
                int n = (Integer) evaluate(parameters.get(1));
                return BuiltInFunctions.RIGHT$(str, n);
            }
            if (functionName.equals("MID$")) {
                String str = (String) evaluate(parameters.get(0));
                int start = (Integer) evaluate(parameters.get(1));
                int count = (Integer) evaluate(parameters.get(2));
                return BuiltInFunctions.MID$(str, start, count);
            }
            if (functionName.equals("NUM$")) {
                Number num = (Number) evaluate(parameters.get(0));
                return BuiltInFunctions.NUM$(num);
            }
            if (functionName.equals("VAL")) {
                String str = (String) evaluate(parameters.get(0));
                return BuiltInFunctions.VAL(str);
            }
            if (functionName.equals("VAL%")) {
                String str = (String) evaluate(parameters.get(0));
                return BuiltInFunctions.VALF(str);
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
