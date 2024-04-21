import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

    Lexer lexer = new Lexer();

    private LinkedList<Token> lexTokens(String text) throws IOException {
        Path tempFilePath = Files.createTempFile("temp.bas", ".txt");
        Files.writeString(tempFilePath, text);
        return lexer.lex(tempFilePath.toString());
    }

    private ProgramNode parseStatements(String text) throws IOException {
        return new Parser(lexTokens(text)).parse();
    }

    // Tests the ability to store labels and data
    @Test
    public void testDataAndLabelProcessing() throws IOException {
        // Create a mock ProgramNode with some DATA statements and labels
        ProgramNode programNode = new ProgramNode();
        StatementsNode statementsNode = new StatementsNode();

        List<Node> data =  new ArrayList<>();
        data.add(new IntegerNode(1));
        data.add(new IntegerNode(2));
        data.add(new IntegerNode(3));

        DataNode dataNode = new DataNode(data);
        statementsNode.addStatement(dataNode);

        List<Node> printList = new ArrayList<>();
        printList.add(new StringNode("Hello World"));
        PrintNode printNode = new PrintNode(printList);
        statementsNode.addStatement(printNode);

        LabeledStatementNode labeledStatementNode = new LabeledStatementNode(
                "label:", statementsNode.getStatements().get(1));
        statementsNode.addStatement(labeledStatementNode);
        programNode.addStatements(statementsNode);

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(programNode);
        interpreter.interpret();

        // Check that the data and labels were correctly stored
        Map<String, LabeledStatementNode> labels = interpreter.getLabels();
        Queue<Node> dataQueue = interpreter.getDataQueue();

        assertEquals(1, labels.size());
        assertTrue(labels.containsKey("label:"));
        assertEquals(labeledStatementNode, labels.get("label:"));

        assertEquals(3, dataQueue.size());
        assertEquals(dataNode.getData(), new ArrayList<>(dataQueue));
    }

    // Tests the ability to store data from a file
    @Test
    public void testDataProcessingFromFile()  throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/data.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();

        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.interpret();

        Queue<Node> dataQueue = interpreter.getDataQueue();

        assertEquals(3, dataQueue.size());

        Node node1 = dataQueue.poll();
        Node node2 = dataQueue.poll();
        Node node3 = dataQueue.poll();

        assertInstanceOf(IntegerNode.class, node1);
        assertEquals(10, ((IntegerNode) node1).getInt());

        assertInstanceOf(StringNode.class, node2);
        assertEquals("mphipps", ((StringNode) node2).getValue());

        assertInstanceOf(FloatNode.class, node3);
        assertEquals(10.0f, ((FloatNode) node3).getFloat());
    }

    // Tests the ability to store labels from a file
    @Test
    public void testLabelProcessingFromFile()  throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/label.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.setTestMode(true);
        interpreter.interpret();

        Map<String, LabeledStatementNode> labels = interpreter.getLabels();

        LabeledStatementNode labeledStatementNode = new LabeledStatementNode(
                "beginning", new PrintNode(List.of(new StringNode("Hello!"))));

        assertTrue(labels.containsKey("beginning"));
        assertEquals(labeledStatementNode, labels.get("beginning"));
    }

    // Tests the ability to store variables assigned to integers, floats, and strings
    @Test
    public void testVariableStorage() throws IOException {
        // Create a mock ProgramNode with some variable assignments
        ProgramNode programNode = new ProgramNode();
        StatementsNode statementsNode = new StatementsNode();

        AssignmentNode intAssignment = new AssignmentNode(
                new VariableNode("x"), new IntegerNode(5));
        AssignmentNode floatAssignment = new AssignmentNode(
                new VariableNode("y%"), new FloatNode(5.0f));
        AssignmentNode stringAssignment = new AssignmentNode(
                new VariableNode("z$"), new StringNode("Hello World"));
        statementsNode.addStatement(intAssignment);
        statementsNode.addStatement(floatAssignment);
        statementsNode.addStatement(stringAssignment);

        programNode.addStatements(statementsNode);

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(programNode);
        interpreter.interpret();

        // Check that the variables were correctly stored
        Map<String, Integer> intVariables = interpreter.getIntVariables();
        Map<String, Float> floatVariables = interpreter.getFloatVariables();
        Map<String, String> stringVariables = interpreter.getStringVariables();

        assertEquals(1, intVariables.size());
        assertEquals(1, floatVariables.size());
        assertEquals(1, stringVariables.size());
        assertEquals(intVariables.get("x"), 5);
        assertEquals(floatVariables.get("y%"), 5.0f);
        assertEquals(stringVariables.get("z$"), "Hello World");

    }

    // Test the ability to store variables from a file
    @Test
    public void testVariableStorageFromFile() throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/variable_storage.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.interpret();

        Map<String, Integer> intVariables = interpreter.getIntVariables();
        Map<String, Float> floatVariables = interpreter.getFloatVariables();
        Map<String, String> stringVariables = interpreter.getStringVariables();


        assertEquals(1, intVariables.size());
        assertEquals(1, floatVariables.size());
        assertEquals(1, stringVariables.size());
        assertEquals(intVariables.get("x"), 5);
        assertEquals(floatVariables.get("y%"), 5.0f);
        assertEquals(stringVariables.get("z$"), "Hello World");
    }

    // Tests the ability to store variables assigned to strings
    @Test
    public void testStringVariableStorage() throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/string_storage.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.interpret();

        Map<String, String> stringVariables = interpreter.getStringVariables();

        assertEquals(1, stringVariables.size());
        assertEquals("Eric", stringVariables.get("name$"));
    }

    // Tests the ability to store variables assigned to integers
    @Test
    public void testIntVariableStorage() throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/int_storage.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.interpret();

        Map<String, Integer> intVariables = interpreter.getIntVariables();

        assertEquals(4, intVariables.size());
        assertEquals(28, intVariables.get("age"));
        assertEquals(28, intVariables.get("age2"));
        assertEquals(20, intVariables.get("age3"));
        assertEquals(25, intVariables.get("age4"));
    }

    // Tests the ability to store variables assigned to function calls
    @Test
    public void testFunctionsFromFile() throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/function_storage.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.interpret();

        Map<String, Integer> intVariables = interpreter.getIntVariables();
        Map<String, Float> floatVariables = interpreter.getFloatVariables();
        Map<String, String> stringVariables = interpreter.getStringVariables();

        // Testing RANDOM() function
        assertTrue(intVariables.containsKey("randomNumber"));
        assertNotNull(intVariables.get("randomNumber"));

        // Testing LEFT$() function
        assertTrue(stringVariables.containsKey("leftString$"));
        assertEquals("HEL", stringVariables.get("leftString$"));

        // Testing RIGHT$() function
        assertTrue(stringVariables.containsKey("rightString$"));
        assertEquals("LO", stringVariables.get("rightString$"));

        // Testing MID$() function
        assertTrue(stringVariables.containsKey("midString$"));
        assertEquals("ban", stringVariables.get("midString$"));

        // Testing NUM$() function
        assertTrue(stringVariables.containsKey("numToString$"));
        assertEquals("5", stringVariables.get("numToString$"));

        // Testing VAL() function
        assertTrue(intVariables.containsKey("stringToNum"));
        assertEquals(5, intVariables.get("stringToNum"));

        // Testing VAL%() function
        assertTrue(floatVariables.containsKey("stringToFloat%"));
        assertEquals(5.0f, floatVariables.get("stringToFloat%"));


    }

    // Tests the ability to perform math operations
    @Test
    public void testMathOperations() throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/math_operations.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.interpret();

        Map<String, Integer> intVariables = interpreter.getIntVariables();
        Map<String, Float> floatVariables = interpreter.getFloatVariables();

        assertEquals(5.0f, floatVariables.get("floatVar%"));
        assertEquals(5, intVariables.get("intVar"));
        assertEquals(5, intVariables.get("intVar"));
        assertEquals(10.0f, floatVariables.get("floatVar2%"));
        assertEquals(25.0f, floatVariables.get("floatVar3%"));
        assertEquals(1.0f, floatVariables.get("floatVar4%"));
        assertEquals(10.0f, floatVariables.get("floatVar5%"));
        assertEquals(0.0f, floatVariables.get("floatVar6%"));
        assertEquals(41, intVariables.get("intVar2"));
        assertEquals(9, intVariables.get("intVar3"));
    }

    // Tests the ability to perform math operations with functions
    @Test
    public void testFunctionMathOperations() throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/function_math_ops.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();
        List<String> expectedPrint = Arrays.asList("10", "10");

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.setTestMode(true);
        interpreter.interpret();

        Map<String, Integer> intVariables = interpreter.getIntVariables();
        Map<String, Float> floatVariables = interpreter.getFloatVariables();

        assertEquals(10, intVariables.get("valPlusInt"));
        assertEquals(10, intVariables.get("valPlusVal"));
        assertTrue(floatVariables.isEmpty());

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testInput() {
        // Create an instance of Interpreter
        Interpreter interpreter = new Interpreter(new ProgramNode());

        // Set the test mode to true
        interpreter.setTestMode(true);

        // Prepare the test input
        List<String> testInput = Arrays.asList("10", "20.5", "Hello");
        interpreter.setTestInput(testInput);

        // Prepare the variables for the InputNode
        VariableNode intVar = new VariableNode("intVar");
        VariableNode floatVar = new VariableNode("floatVar%");
        VariableNode stringVar = new VariableNode("stringVar$");
        List<VariableNode> variables = Arrays.asList(intVar, floatVar, stringVar);

        // Create an InputNode and call the input method
        InputNode inputNode = new InputNode(new StringNode("Enter values:"), variables);
        interpreter.inputStatement(inputNode);

        // Check if the variables have the expected values
        assertEquals(Integer.valueOf(10), interpreter.getIntVariables().get("intVar"));
        assertEquals(Float.valueOf(20.5f), interpreter.getFloatVariables().get("floatVar%"));
        assertEquals("Hello", interpreter.getStringVariables().get("stringVar$"));
    }

    // Tests the ability to print strings, integers, and floats
    @Test
    public void testPrint() {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/test_print.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();
        List<String> expectedPrint = Arrays.asList("hello", "5", "5.0", "5", "5.0", "hello");

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    // Tests the ability to print strings, integers, and floats
    @Test
    public void testGoSub() {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/go_sub.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();
        List<String> expectedPrint = List.of("22");

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testIfStatementWithMultipleBranches() {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/if_statement_two_labels.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();
        List<String> expectedPrint = List.of("start", "myLabel", "exiting myLabel");

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testGoTo() {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/goto.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();
        List<String> expectedPrint = List.of("Hello", "myLabel");

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testIf() {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/if_statement.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();
        List<String> expectedPrint = List.of("y is small");

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testEvaluateBoolean() {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/if_statement.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();
        List<String> expectedPrint = List.of("y is small");

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testWhileLoop() {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/while_loop.bas");
        ProgramNode actualProgram = new Parser(tokens).parse();
        List<String> expectedPrint = List.of("0", "1", "2", "3", "done");

        // Run Interpreter on it
        Interpreter interpreter = new Interpreter(actualProgram);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testIfWithLabels() throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/if_statement_one_label.bas");
        ProgramNode program = new Parser(tokens).parse();
        List<String> expectedPrint = List.of("x is small", "HELLO");

        // Check that the first statement in the program is a WhileNode
        assertInstanceOf(AssignmentNode.class, program.getStatements().get(0));
        assertInstanceOf(IfNode.class, program.getStatements().get(1));
        assertInstanceOf(LabeledStatementNode.class, program.getStatements().get(2));
        assertInstanceOf(PrintNode.class, ((LabeledStatementNode) program.getStatements().get(2)).getStatementNode());

        Interpreter interpreter = new Interpreter(program);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testReadData() throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/read_and_data.bas");
        ProgramNode program = new Parser(tokens).parse();
        List<String> expectedPrint = List.of("6", "6.0", "Hello", "World", "!");

        // Check that the first statement in the program is a WhileNode
        assertInstanceOf(DataNode.class, program.getStatements().get(0));
        assertInstanceOf(ReadNode.class, program.getStatements().get(1));
        assertInstanceOf(AssignmentNode.class, program.getStatements().get(2));
        assertInstanceOf(ReadNode.class, program.getStatements().get(3));
        assertInstanceOf(AssignmentNode.class, program.getStatements().get(4));
        assertInstanceOf(ReadNode.class, program.getStatements().get(5));
        assertInstanceOf(PrintNode.class, program.getStatements().get(6));
        assertInstanceOf(PrintNode.class, program.getStatements().get(7));
        assertInstanceOf(PrintNode.class, program.getStatements().get(8));
        assertInstanceOf(PrintNode.class, program.getStatements().get(9));
        assertInstanceOf(PrintNode.class, program.getStatements().get(10));

        Interpreter interpreter = new Interpreter(program);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testReadDataAndPrint() throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/data_test_read_and_print.bas");
        ProgramNode program = new Parser(tokens).parse();
        List<String> expectedPrint = List.of("10", "mphipps", "10.0");

        // Check that the first statement in the program is a WhileNode
        assertInstanceOf(DataNode.class, program.getStatements().get(0));
        assertInstanceOf(ReadNode.class, program.getStatements().get(1));
        assertInstanceOf(PrintNode.class, program.getStatements().get(2));

        Interpreter interpreter = new Interpreter(program);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testForLoopAndGoSub() throws IOException {
        LinkedList<Token> tokens = lexer.lex("src/test/resources/for_loop_2.bas");
        ProgramNode program = new Parser(tokens).parse();
        List<String> expectedPrint = List.of("0.0", "37.77778", "100.0", "10.0", "-17.777779");

        // Check that the first statement in the program is a WhileNode
        assertInstanceOf(AssignmentNode.class, program.getStatements().get(0));
        assertInstanceOf(ForNode.class, program.getStatements().get(1));
        assertInstanceOf(ReadNode.class, program.getStatements().get(2));
        assertInstanceOf(GoSubNode.class, program.getStatements().get(3));
        assertInstanceOf(PrintNode.class, program.getStatements().get(4));
        assertInstanceOf(NextNode.class, program.getStatements().get(5));
        assertInstanceOf(EndNode.class, program.getStatements().get(6));
        assertInstanceOf(LabeledStatementNode.class, program.getStatements().get(7));
        assertInstanceOf(ReturnNode.class, program.getStatements().get(8));
        assertInstanceOf(DataNode.class, program.getStatements().get(9));

        Interpreter interpreter = new Interpreter(program);
        interpreter.setTestMode(true);
        interpreter.interpret();

        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }

    @Test
    public void testInterpretPrint() throws IOException {
        ProgramNode program = parseStatements("PRINT \"HELLO\"");
        List<String> expectedPrint = List.of("HELLO");

        Interpreter interpreter = new Interpreter(program);
        interpreter.setTestMode(true);
        interpreter.interpret();
        List<String> actualPrint = interpreter.getOutput();
        assertEquals(expectedPrint, actualPrint);
    }
}