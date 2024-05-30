package parser;

import lexer.Token;
import lexer.TokenManager;
import node.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This class parses a list of tokens and outputs a ProgramNode with a list of Statements representing the abstract syntax tree
 *   for SimpleBASIC
 *
 * @return ProgramNode
 */
public class Parser {
    /**
     * The TokenManager class manages the token stream. It keeps track
     * of the current position in the token list and provides methods to access and manipulate the tokens.
     */
    private final TokenManager tokenManager;

    /**
     * Parser class that is responsible for parsing a list of tokens and
     * generating an Abstract Syntax Tree (AST).
     */
    public Parser(LinkedList<Token> tokens) {
        this.tokenManager = new TokenManager(tokens);
    }

    /**
     * This method is used to accept separators in the input tokens.
     * It checks if the next token is of type "ENDOFLINE" and continues to remove and match the "ENDOFLINE" tokens
     * until a non-"ENDOFLINE" token is encountered.
     *
     * @return true if any "ENDOFLINE" tokens are found and matched, false otherwise.
     */
    public boolean acceptSeparators() {
        boolean found = false;
        while (peekAndMatch(Token.TokenType.ENDOFLINE)) {
            tokenManager.matchAndRemove(Token.TokenType.ENDOFLINE);
            found = true;
        }
        return found;
    }

    /**
     * Parses a list of tokens and generates an Abstract Syntax Tree (AST).
     *
     * @return The ProgramNode representing the root of the AST.
     */
    public ProgramNode parse() {
        ProgramNode program = new ProgramNode();
        program.addStatements(statements());
        return program;
    }

    /**
     * Parses expressions from the token stream.
     * It creates a new ProgramNode and then repeatedly calls the expression() method to parse individual expressions.
     * It continues parsing expressions as long as there are more tokens and the next token is a separator (i.e., an ENDOFLINE token).
     *
     * @return A ProgramNode representing the root of the Abstract Syntax Tree (AST) for the parsed expressions.
     */
    public ProgramNode parseExpressions() {
        ProgramNode program = new ProgramNode();
        do {
            program.addExpression(expression());
        } while (acceptSeparators() && tokenManager.moreTokens());
        return program;
    }

    /**
    * Parses all statements from the token stream.
    * If the statement() method returns null, it stops parsing and returns the StatementsNode.
    *
    * @return A StatementsNode representing all parsed statements.
    */
    public StatementsNode statements() {
        StatementsNode statements = new StatementsNode();
        StatementNode statementNode;
        while ((statementNode = statement()) != null) {
            statements.addStatement(statementNode);
            acceptSeparators();
        }
        return statements;
    }

    /**
     * Parses a statement from the token stream.
     * It checks the type of the next token and calls the corresponding method to parse and create the corresponding statement node.
     * If the next token does not match any statement type, it returns null.
     *
     * @return The parsed StatementNode representing the statement, or null if the next token does not match any statement type.
     */
    public StatementNode statement() {
        if (peekAndMatch(Token.TokenType.LABEL)) {
            String label = tokenManager.matchAndRemove(Token.TokenType.LABEL).get().getVal();

            // Only store the string without the ":" to simplify lookup
            label = label.substring(0, label.length() - 1);

            // This label is used in a while statement and has no statement after colon
            if (peekAndMatch(Token.TokenType.ENDOFLINE)) {
                return new LabeledStatementNode(label, null);
            }

            StatementNode statementNode = statement();
            return new LabeledStatementNode(label, statementNode);
        } else if (peekAndMatch(Token.TokenType.READ)) {
            return readStatement();
        } else if (peekAndMatch(Token.TokenType.DATA)) {
            return dataStatement();
        } else if (peekAndMatch(Token.TokenType.PRINT)) {
            return printStatement();
        } else if (peekAndMatch(Token.TokenType.INPUT)) {
            return inputStatement();
        } else if (peekAndMatch(Token.TokenType.GOTO)) {
            return goToStatement();
        } else if (peekAndMatch(Token.TokenType.GOSUB)) {
            return goSubStatement();
        } else if (peekAndMatch(Token.TokenType.RETURN)) {
            return returnStatement();
        } else if (peekAndMatch(Token.TokenType.WORD)) {
            return assignment();
        } else if (peekAndMatch(Token.TokenType.FOR)) {
            return forStatement();
        } else if (peekAndMatch(Token.TokenType.NEXT)) {
            return nextStatement();
        } else if (peekAndMatch(Token.TokenType.ENDOFLINE)) {
            tokenManager.matchAndRemove(Token.TokenType.ENDOFLINE);
            return null;
        } else if (peekAndMatch(Token.TokenType.IF)) {
            return ifStatement();
        } else if (peekAndMatch(Token.TokenType.WHILE)) {
            return whileStatement();
        } else if (peekAndMatch(Token.TokenType.END)) {
            return endStatement();
        } else {
            return null;
        }
    }

    /**
     * Parses a while statement from the token stream.
     * The method follows these steps:
     * 1. Checks if the next token is a WHILE token. If it is not, it throws an IllegalArgumentException.
     * 2. Calls the booleanExpression() method to parse the condition of the while statement.
     * 3. Checks if the next token is a LABEL token. If it is not, it throws an IllegalArgumentException.
     * 4. Retrieves the label identifier from the LABEL token and assigns it to the label variable.
     * 5. Creates a new WhileNode with the parsed condition and label, and returns it as a StatementNode.
     *
     * @return A StatementNode representing the parsed while statement.
     * @throws IllegalArgumentException If the next token is not a WHILE token or a LABEL token.
     */
    public StatementNode whileStatement() {
        if (!matchAndRemove(Token.TokenType.WHILE)) {
            throw illegalArgumentException("WHILE", "Expected 'WHILE' token");
        }

        BooleanExpressionNode condition = booleanExpression();

        // Check if the next token is the end label of the while loop
        if (!peekAndMatch(Token.TokenType.WORD)) {
            throw illegalArgumentException("WHILE", "Expected a 'WORD' token at the end of 'WHILE' loop");
        }

        // Remove the end label from the token stream, store it as the end label
        String endLabel = tokenManager.matchAndRemove(Token.TokenType.WORD).get().getVal();

        return new WhileNode(condition, endLabel);
    }

    /**
     * Parses an end statement from the token stream.
     * The method follows these steps:
     * 1. Checks if the next token is an END token. If it is not, it throws an IllegalArgumentException.
     * 2. Creates a new EndNode and returns it.
     *
     * @return An EndNode representing the parsed end statement.
     * @throws IllegalArgumentException If the next token is not an END token.
     */
    public StatementNode endStatement() {
        if (!matchAndRemove(Token.TokenType.END)) {
            throw illegalArgumentException("END", "Expected 'END' token");
        }
        return new EndNode();
    }


    /**
     * Parses an if statement from the token stream.
     * The method follows these steps:
     * 1. Checks if the next token is an IF token. If it is not, it throws an IllegalArgumentException.
     * 2. Calls the booleanExpression() method to parse the condition of the if statement.
     * 3. Checks if the next token is a THEN token. If it is not, it throws an IllegalArgumentException.
     * 4. Checks if the next token is a WORD token. If it is not, it throws an IllegalArgumentException.
     * 5. Retrieves the label identifier from the WORD token and assigns it to the label variable.
     * 6. Creates a new IfNode with the parsed condition and label, and returns it as a StatementNode.
     *
     * @return A StatementNode representing the parsed if statement.
     * @throws IllegalArgumentException If the next token is not an IF token, a THEN token, or a WORD token.
     */
    public StatementNode ifStatement() {
        if (!matchAndRemove(Token.TokenType.IF)) {
            throw illegalArgumentException("IF", "Expected 'IF' token");
        }
        BooleanExpressionNode condition = booleanExpression();
        if (!matchAndRemove(Token.TokenType.THEN)) {
            throw illegalArgumentException("IF", "Expected 'THEN' token");
        }
        if (!peekAndMatch(Token.TokenType.WORD)) {
            throw illegalArgumentException("IF", "Expected a label identifier after 'THEN' token");
        }
        String label = tokenManager.matchAndRemove(Token.TokenType.WORD).get().getVal();
        return new IfNode(condition, label);
    }

    public GoToNode goToStatement() {
        if (!matchAndRemove(Token.TokenType.GOTO)) {
            throw illegalArgumentException("GOTO", "Expected 'GOTO' token");
        }
        if (!peekAndMatch(Token.TokenType.WORD)) {
            throw illegalArgumentException("GOTO", "Expected label after 'GOTO' token");
        }
        return new GoToNode(tokenManager.matchAndRemove(Token.TokenType.WORD).get().getVal());
    }

    /**
     * Parses a boolean expression node.
     *
     * @return a BooleanExpressionNode representing the parsed boolean expression
     * @throws IllegalArgumentException if a boolean operator is not found
     */
    public BooleanExpressionNode booleanExpression() {
        ExpressionNode left = (ExpressionNode) expression();
        BooleanExpressionNode.OPERATOR operator;
        if (matchAndRemove(Token.TokenType.GREATERTHAN)) {
            operator = BooleanExpressionNode.OPERATOR.GREATERTHAN;
        } else if (matchAndRemove(Token.TokenType.GREATERTHANEQUALTO)) {
            operator = BooleanExpressionNode.OPERATOR.GREATERTHANEQUALTO;
        } else if (matchAndRemove(Token.TokenType.LESSTHAN)) {
            operator = BooleanExpressionNode.OPERATOR.LESSTHAN;
        } else if (matchAndRemove(Token.TokenType.LESSTHANEQUALTO)) {
            operator = BooleanExpressionNode.OPERATOR.LESSTHANEQUALTO;
        } else if (matchAndRemove(Token.TokenType.NOTEQUALS)) {
            operator = BooleanExpressionNode.OPERATOR.NOTEQUALS;
        } else if (matchAndRemove(Token.TokenType.EQUALS)) {
            operator = BooleanExpressionNode.OPERATOR.EQUALS;
        } else {
            throw illegalArgumentException("Boolean expression", "Expected a boolean operator ('>', '>=', '<', '<=', '<>', '=')");
        }
        ExpressionNode right = (ExpressionNode) expression();
        return new BooleanExpressionNode(left, operator, right);
    }

    /**
     * Parses a for loop statement.
     *
     * @return The constructed ForNode representing the for loop.
     * @throws IllegalArgumentException if a number is not found after "STEP" keyword.
     */
    public StatementNode forStatement() {
        if(!matchAndRemove(Token.TokenType.FOR)) {
            throw illegalArgumentException("FOR", "Expected 'FOR' token");
        }

        Optional<Token> wordTokenOpt = tokenManager.matchAndRemove(Token.TokenType.WORD);
        VariableNode variableNode;
        if (wordTokenOpt.isPresent()) {
            variableNode = new VariableNode(wordTokenOpt.get().getVal());
        } else {
            throw illegalArgumentException("FOR", "Expected a 'VARIABLE' name");
        }

        if (!matchAndRemove(Token.TokenType.EQUALS)) {
            throw illegalArgumentException("FOR", "Expected an 'EQUALS' token");
        }

        FactorNode initialValue = (FactorNode) factor();

        if (!matchAndRemove(Token.TokenType.TO)) {
            throw illegalArgumentException("FOR", "Expected 'TO' token");
        }

        Node limit = factor();

        // default to 1
        FactorNode increment = new FactorNode(new IntegerNode(1));
        if (matchAndRemove(Token.TokenType.STEP)) {
            if (!peekAndMatch(Token.TokenType.NUMBER)) {
                throw illegalArgumentException("FOR", "Expected 'NUMBER' after 'STEP'");
            }
            increment = number(tokenManager.matchAndRemove(Token.TokenType.NUMBER).get(), false);
        }
        return new ForNode(variableNode, initialValue, limit, increment);
    }

    /**
     * Retrieves the next statement in the program.
     *
     * @return The next statement node.
     * @throws IllegalArgumentException If a variable identifier is not found after the "NEXT" keyword.
     */
    public StatementNode nextStatement() {
        if(!matchAndRemove(Token.TokenType.NEXT)) {
            throw illegalArgumentException("NEXT", "Expected 'NEXT' token");
        }
        if (!peekAndMatch(Token.TokenType.WORD)) {
            throw illegalArgumentException("NEXT", "Expected a variable identifier after 'NEXT'");
        }
        VariableNode variable = (VariableNode) factor();
        return new NextNode(variable);
    }

    /**
     * Parses a GOSUB statement by matching and removing the GOSUB token, and then
     * parsing the label identifier. If the label identifier is not found, an
     * IllegalArgumentException is thrown.
     *
     * @return a new GosubNode representing the GOSUB statement with the parsed label identifier
     * @throws IllegalArgumentException if a label identifier is not found after the GOSUB token
     */
    public StatementNode goSubStatement() {
        if (!matchAndRemove(Token.TokenType.GOSUB)) {
            throw illegalArgumentException("GOSUB", "Expected 'GOSUB' token");
        }
        if (!peekAndMatch(Token.TokenType.WORD)) {
            throw illegalArgumentException("GOSUB", "Expected a label identifier after 'GOSUB' token");
        }
        String label = tokenManager.matchAndRemove(Token.TokenType.WORD).get().getVal();
        return new GoSubNode(label);
    }

    /**
     * Parses a return statement node.
     * The return command is used with a matching gosub command, to return program
     *   flow back to the main program at the end of the sub procedure.
     * If a return command is used without a matching gosub beforehand, the program flow will crash.
     *
     * @return A StatementNode object representing a return statement.
     * @throws IllegalArgumentException if the RETURN statement is not followed by an end of line token.
     */
    public StatementNode returnStatement() {
        if(!matchAndRemove(Token.TokenType.RETURN)) {
            throw illegalArgumentException("RETURN", "'RETURN' must be the only token in a RETURN statement");
        }
        if (!peekAndMatch(Token.TokenType.ENDOFLINE)) {
            throw illegalArgumentException("RETURN", "'RETURN' must be the only token in a RETURN statement");
        }
        return new ReturnNode();
    }

    /**
     * Parses an input statement from the token stream.
     * It first checks if the next token is an INPUT token. If it is, it creates a new InputNode.
     * It then calls the factor() method to parse a list of variables to be input and adds the returned variables to the InputNode.
     * Finally, it returns the InputNode.
     *
     * @return An InputNode representing the parsed input statement.
     */
    public StatementNode inputStatement() {
        if (!peekAndMatch(Token.TokenType.INPUT)) {
            throw illegalArgumentException("INPUT", "Expected 'INPUT' token");
        } else {
            matchAndRemove(Token.TokenType.INPUT);
        }

        // First argument should be a string literal
        StringNode promptNode;
        if (!peekAndMatch(Token.TokenType.STRINGLITERAL)) {
            throw new IllegalArgumentException(String.format("Invalid 'INPUT' statement with token: %s", peek()));
        } else {
            promptNode = new StringNode(tokenManager.matchAndRemove(Token.TokenType.STRINGLITERAL).get().getVal());
        }

        List<VariableNode> inputs = new ArrayList<>();
        while (!peekAndMatch(Token.TokenType.ENDOFLINE)) {
            if (peekAndMatch(Token.TokenType.COMMA)) {
                matchAndRemove(Token.TokenType.COMMA);
                continue;
            }
            if (peekAndMatch(Token.TokenType.WORD)) {
                inputs.add(new VariableNode(tokenManager.matchAndRemove(Token.TokenType.WORD).get().getVal()));
            } else {
                throw illegalArgumentException("INPUT", "Expected 'WORD' tokens separated by 'COMMA' tokens");
            }
        }
        return new InputNode(promptNode, inputs);
    }


    /**
     * Parses a read statement from the token stream.
     * It first checks if the next token is a READ token. If it is, it creates a new ReadNode.
     * It then calls the factor() method to parse a list of variables to be read and adds the returned variables to the ReadNode.
     * Finally, it returns the ReadNode.
     *
     * @return A ReadNode representing the parsed read statement.
     */
    public StatementNode readStatement() {
        if (!matchAndRemove(Token.TokenType.READ)) {
            throw illegalArgumentException("READ", "Expected 'READ' token");
        }
        List<VariableNode> variables = new ArrayList<>();
        if (!peekAndMatch(Token.TokenType.WORD)) {
            throw illegalArgumentException("READ", "Expected a 'VARIABLE' identifier after 'READ' token");
        }
        do {
            Node node = factor();
            if (node instanceof VariableNode) {
                variables.add((VariableNode) node);
            } else {
                throw illegalArgumentException("READ", "Expected a 'VARIABLE' identifier token");
            }
        } while (matchAndRemove(Token.TokenType.COMMA));
        return new ReadNode(variables);
    }

    /**
     * Parses a data statement from the token stream.
     * It first checks if the next token is a DATA token. If it is, it creates a new DataNode.
     * It then calls the expression() method to parse a list of data values and adds the returned values to the DataNode.
     * Finally, it returns the DataNode.
     *
     * @return A DataNode representing the parsed data statement.
     */
    public StatementNode dataStatement() {
        if (!matchAndRemove(Token.TokenType.DATA)) {
            throw illegalArgumentException("DATA", "Expected 'DATA' token");
        }
        List<Node> data = new ArrayList<>();
        if (!peekAndMatch(Token.TokenType.STRINGLITERAL) && !peekAndMatch(Token.TokenType.NUMBER)) {
            throw illegalArgumentException("DATA", "Expected one or more 'NUMBER' or 'STRINGLITERAL' tokens");
        }
        do {
            Node node = factor();

            // Unwrap the FactorNode to get the actual inner node
            if (node instanceof FactorNode) {
                node = ((FactorNode) node).getNode();
            }
            if (node instanceof IntegerNode || node instanceof FloatNode || node instanceof StringNode) {
                data.add(node);
            } else {
                throw illegalArgumentException("DATA", "Expected a valid Factor");
            }
        } while (matchAndRemove(Token.TokenType.COMMA));
        return new DataNode(data);
    }

    /**
     * Parses a print statement from the token stream.
     * It first checks if the next token is a PRINT token. If it is, it creates a new PrintNode.
     * It then calls the printList() method to parse a list of nodes to be printed and adds the returned nodes to the PrintNode.
     * Finally, it returns the PrintNode.
     *
     * @return A PrintNode representing the parsed print statement.
     */
    public PrintNode printStatement() {
        if (!matchAndRemove(Token.TokenType.PRINT)) {
            throw illegalArgumentException("PRINT", "Expected 'PRINT' token");
        }
        PrintNode printNode = new PrintNode();

        // Call the printList method and add the returned nodes to the PrintNode
        List<Node> nodes = printList();
        for (Node node : nodes) {
            printNode.addNode(node);
        }
        return printNode;
    }

/**
     * Parses a list of nodes to be printed from the token stream.
     * It creates a new list of nodes and repeatedly calls the expression() method to parse individual nodes.
     * It continues parsing nodes as long as there are more tokens and the next token is not an ENDOFLINE token.
     *
     * @return A list of nodes to be printed.
     */
public List<Node> printList() {
    List<Node> nodes = new ArrayList<>();
    while (!peekAndMatch(Token.TokenType.ENDOFLINE)) {
        if (peekAndMatch(Token.TokenType.STRINGLITERAL)) {
            nodes.add(new StringNode(tokenManager.matchAndRemove(Token.TokenType.STRINGLITERAL).get().getVal()));
        } else {
            nodes.add(expression());
        }
        if (peekAndMatch(Token.TokenType.COMMA)) {
            matchAndRemove(Token.TokenType.COMMA);
        } else if (!peekAndMatch(Token.TokenType.ENDOFLINE)) {
            throw illegalArgumentException("PRINT", "Multiple arguments must be comma separated");

        }
    }
    return nodes;
}

    /**
     * Parses an assignment statement from the token stream.
     * It first calls the factor() method to parse a variable name.
     * If the next token is an EQUALS token, it calls the expression() method to parse the expression to be assigned to the variable.
     * If the next token is not an EQUALS token, it returns null.
     *
     * @return An AssignmentNode representing the parsed assignment statement, or null if the next token is not an EQUALS token.
     */
    public AssignmentNode assignment() {
        VariableNode variableNode = (VariableNode) factor();
        if (!matchAndRemove(Token.TokenType.EQUALS)) {
            throw illegalArgumentException("Assignment", "Statements starting with a variable must be in an assignment statement");
        }
        return new AssignmentNode(variableNode, expression());
    }

    /**
     * Parses an expression according to the grammar:
     *
     * Expression: TERM {+|- TERM} | functionInvocation
     * Term: FACTOR {*|/ FACTOR}
     * Factor: number | ( EXPRESSION )
     * @return an ExpressionNode
     */
    public Node expression() {
        Node term;
        if (peekAndMatch(Token.TokenType.FUNCTIONNAME)) {
            term = functionInvocation();
        } else {
            term = term();
        }
        while (true) {
            if (matchAndRemove(Token.TokenType.PLUS)) {
                term = new MathOpNode(MathOpNode.OPERATION.ADD, term, term());
            } else if (matchAndRemove(Token.TokenType.MINUS)) {
                term = new MathOpNode(MathOpNode.OPERATION.SUBTRACT, term, term());
            } else if (matchAndRemove(Token.TokenType.MULTIPLY)) {
                term = new MathOpNode(MathOpNode.OPERATION.MULTIPLY, term, term());
            } else if (matchAndRemove(Token.TokenType.DIVIDE)) {
                term = new MathOpNode(MathOpNode.OPERATION.DIVIDE, term, term());
            } else {
                break;
            }
        }
        return new ExpressionNode(term);
    }


    /**
     * Parses and constructs a FunctionNode representing a built-in function
     *
     * @return the constructed FunctionNode with the parsed function name and parameters,
     *         or null if the next token is not a function name
     * @throws IllegalArgumentException if the expected LPAREN or RPAREN tokens are missing
     */
    public FunctionNode functionInvocation() {
        if (!peekAndMatch(Token.TokenType.FUNCTIONNAME)) {
            throw illegalArgumentException("BUILTIN FUNCTION", "Unknown built-in function");
        }
        String functionName = tokenManager.matchAndRemove(Token.TokenType.FUNCTIONNAME).get().getVal();

        if (!BuiltInFunctions.functionMap.containsKey(functionName)) {
            throw illegalArgumentException("BUILTIN FUNCTION", "Unknown built-in function");
        }
        if (!matchAndRemove(Token.TokenType.LPAREN)) {
            throw illegalArgumentException("BUILTIN FUNCTION", "Expected 'LPAREN' token");
        }
        List<Node> parameters = parameterList();
        if (!matchAndRemove(Token.TokenType.RPAREN)) {
            throw illegalArgumentException("BUILTIN FUNCTION", "Expected 'RPAREN' token");
        }
        FunctionNode functionNode = new FunctionNode(BuiltInFunctions.functionMap.get(functionName));
        functionNode.setParameters(parameters);
        return functionNode;
    }

    /**
     * Retrieves a list of nodes representing the parameters of a method.
     *
     * @return A list of Node objects representing the parameters of a method.
     * @throws IllegalArgumentException if a comma or RPAREN is missing.
     */
    public List<Node> parameterList() {
        List<Node> parameters = new ArrayList<>();
        while (!peekAndMatch(Token.TokenType.RPAREN)) {
            if (peekAndMatch(Token.TokenType.STRINGLITERAL)) {
                parameters.add(new StringNode(tokenManager.matchAndRemove(Token.TokenType.STRINGLITERAL).get().getVal()));
            } else {
                parameters.add(expression());
            }
            if (peekAndMatch(Token.TokenType.COMMA)) {
                matchAndRemove(Token.TokenType.COMMA);
            } else if (!peekAndMatch(Token.TokenType.RPAREN)) {
                throw new IllegalArgumentException(
                        String.format("Invalid token %s in parameter list expression: Expected a 'COMMA' or 'RPAREN'", peek())
                );
            }
        }
        return parameters;
    }

    /**
     * Parses and generates a TermNode from the given tokens. A TermNode represents a term in the grammar.
     * It evaluates the input tokens and creates a binary expression tree.
     *
     *
     * @return The TermNode representing the root of the term tree.
     */
    public TermNode term() {
        Node factor = factor();
        while (tokenManager.moreTokens()) {
            if (matchAndRemove(Token.TokenType.MULTIPLY)) {
                factor = new MathOpNode(MathOpNode.OPERATION.MULTIPLY, factor, factor());
            } else if (matchAndRemove(Token.TokenType.DIVIDE)) {
                factor = new MathOpNode(MathOpNode.OPERATION.DIVIDE, factor, factor());
            } else {
                break;
            }
        }
        return new TermNode(factor);
    }

    /**
     * Parses and generates a FactorNode from the given tokens. A FactorNode represents a factor in the grammar.
     * It evaluates the input tokens and creates a binary expression tree.
     *
     * @return The FactorNode representing the root of the factor tree.
     */
    public Node factor() {
        if (peekAndMatch(Token.TokenType.FUNCTIONNAME)) {
            return functionInvocation();
        }
        Optional<Token> wordTokenOpt = tokenManager.matchAndRemove(Token.TokenType.WORD);
        if (wordTokenOpt.isPresent()) {
            return new VariableNode(wordTokenOpt.get().getVal());
        }

        Optional<Token> stringLiteralOpt = tokenManager.matchAndRemove(Token.TokenType.STRINGLITERAL);
        if (stringLiteralOpt.isPresent()) {
            return new StringNode(stringLiteralOpt.get().getVal());
        }

        boolean isNegative = matchAndRemove(Token.TokenType.MINUS);

        if (matchAndRemove(Token.TokenType.LPAREN)) {
            ExpressionNode innerExpr = (ExpressionNode) expression();
            if (!matchAndRemove(Token.TokenType.RPAREN)) {
                throw illegalArgumentException("FACTOR", "Mismatched parentheses");
            }

            if (isNegative) {
                // wrap the original expression in a new Term (-1 * expr) to conform to the original grammar
                innerExpr = new ExpressionNode(
                        new TermNode(
                                new MathOpNode(
                                        MathOpNode.OPERATION.MULTIPLY,
                                        new FactorNode(new IntegerNode(-1)),
                                        new FactorNode(innerExpr)
                                )
                        )
                );
            }
            return new FactorNode(innerExpr);
        }

        Optional<Token> numberTokenOpt = tokenManager.matchAndRemove(Token.TokenType.NUMBER);
        if (numberTokenOpt.isEmpty()) {
            throw illegalArgumentException("FACTOR", "Expected a 'NUMBER', 'FUNCTIONNAME', or 'MATHOP' token");
        }

        return number(numberTokenOpt.get(), isNegative);
    }

    private FactorNode number(Token numberToken, boolean isNegative) {
        if (numberToken.getVal().contains(".")) {
            float val = Float.parseFloat(numberToken.getVal());
            FloatNode floatNode = isNegative ? new FloatNode(-val) : new FloatNode(val);
            return new FactorNode(floatNode);
        }
        else {
            int val = Integer.parseInt(numberToken.getVal());
            IntegerNode integerNode = isNegative ? new IntegerNode(-val) : new IntegerNode(val);
            return new FactorNode(integerNode);
        }
    }

    /**
     * Checks if the next token in the token list has the specified TokenType,
     * removes the token from the list if it matches, and returns true.
     * If the token does not match or there are no more tokens in the list, it returns false.
     *
     * @param type The TokenType to match against.
     * @return true if the next token matches the specified TokenType and is successfully removed, false otherwise.
     */
    private boolean matchAndRemove(Token.TokenType type) {
        return tokenManager.matchAndRemove(type).isPresent();
    }

    /**
     * Checks if the next token in the token list has the specified TokenType, without removing it from the list.
     * If the token matches and there are more tokens in the list, it returns true. Otherwise, it returns false.
     *
     * @param type The TokenType to match against.
     * @return true if the next token matches the specified TokenType, false otherwise.
     */
    private boolean peekAndMatch(Token.TokenType type) {
        Optional<Token> tokenOpt = tokenManager.peek(0);
        return tokenOpt.isPresent() && tokenOpt.get().getTokenType() == type;
    }

    /**
     * Wrapper method to peek the next token (Used for logging)
     * @return Token or null
     */
    private Token peek() {
        return tokenManager.peek(0).orElse(null);
    }

    private IllegalArgumentException illegalArgumentException(String statementType, String message) {
        return new IllegalArgumentException(
                String.format("Invalid token %s in '%s' statement: %s", peek(), statementType, message)
        );
    }
}

