import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;



/**
 * The LexerTest class is responsible for testing the lexer functionality.
 */
public class LexerTest {

    Lexer lexer = new Lexer();

    /**
     * Runs the lexer on the given text and returns a list of tokens identified in the source code.
     *
     * @param text The text to be lexed.
     * @return A LinkedList of tokens identified in the source code.
     * @throws IOException If an I/O error occurs while reading the text.
     */
    private LinkedList<Token> runLexerOnText(String text) throws IOException {
        Path tempFilePath = Files.createTempFile("hello_world5.bas", ".txt");
        Files.writeString(tempFilePath, text);
        return lexer.lex(tempFilePath.toString());
    }


    /**
     *
     * This method is a unit test for the testMultiLineStrings functionality.
     * It tests the ability of the Lexer to tokenize a string containing multiple lines.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testMultiLineStrings() throws IOException {
        String s = "\"Hello\\nWorld\\n12345\"";
        String expected = "Hello\nWorld\n12345";
        LinkedList<Token> tokens = runLexerOnText(s);
        assertEquals(tokens.size(), 2);
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, expected, 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, s.length()), tokens.get(1));
    }

    /**
     * Tests the Lexer's ability to tokenize a string containing words followed by numbers.
     * Verifies if words and numbers are correctly identified and tokenized.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testWordsThenNumbers() throws IOException {
        LinkedList<Token> tokens = runLexerOnText("Hello 12345");
        assertEquals(new Token(Token.TokenType.WORD, "Hello", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.NUMBER, "12345", 1, 6), tokens.get(1));
    }

    /**
     * Tests the Lexer's ability to tokenize a string containing numbers followed by words.
     * Verifies if numbers and words are correctly identified and tokenized in sequence.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testNumbersThenWords() throws IOException {
        LinkedList<Token> tokens = runLexerOnText("12345 Hello");
        assertEquals(new Token(Token.TokenType.NUMBER, "12345", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.WORD, "Hello", 1, 6), tokens.get(1));
    }

    /**
     * Tests the method numberWithNewline().
     * It tests if the method correctly tokenizes a string with a number followed by a newline character.
     * It verifies that the tokens returned have the correct token type, value, line number, and position.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testNumberWithNewline() throws IOException {
        LinkedList<Token> tokens = runLexerOnText("12345\n");
        assertEquals(new Token(Token.TokenType.NUMBER, "12345", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 5), tokens.get(1));
    }

    /**
     * Runs the lexer on the given text and returns a list of tokens identified in the source code.
     *
     * @throws IOException If an I/O error occurs while reading the text.
     */
    @Test
    public void testNumberWithDecimal() throws IOException {
        LinkedList<Token> tokens = runLexerOnText("123.45");
        assertEquals(new Token(Token.TokenType.NUMBER, "123.45", 1, 0), tokens.get(0));
    }

    /**
     * Tests the {@code testInvalidNumber} method in the {@code LexerTest} class.
     * It verifies that the method throws an {@code IllegalStateException} when given invalid number inputs.
     *
     * @throws IllegalStateException if the method does not throw an exception for one or more test scenarios.
     */
    @Test
    public void testInvalidNumber() throws IllegalStateException {
        assertThrows(IllegalStateException.class, () -> runLexerOnText("123_345"));
        assertThrows(IllegalStateException.class, () -> runLexerOnText("123.34.55"));
        assertThrows(IllegalStateException.class, () -> runLexerOnText("123:"));
    }

    /**
     * Tests the method numberWithNewlines().
     * It verifies that the method correctly tokenizes a string with a number followed by a newline character.
     * It checks whether the tokens returned have the correct token type, value, line number, and position.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testNumberWithNewlines() throws IOException {
        LinkedList<Token> tokens = runLexerOnText("12345 \n 54321");
        assertEquals(new Token(Token.TokenType.NUMBER, "12345", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 6), tokens.get(1));
        assertEquals(new Token(Token.TokenType.NUMBER, "54321", 2, 1), tokens.get(2));
    }

    /**
     * Unit test method for testing the functionality of the testStringLiteral method.
     * The testStringLiteral method tests the ability of the Lexer to correctly tokenize a string literal within the source code.
     *
     * @throws IOException If an I/O error occurs while reading the text.
     */
    @Test
    public void testStringLiteral() throws IOException {
        String s = "\"\\\"Hello World\\\"\"";
        String expected = "\"Hello World\"";
        LinkedList<Token> tokens = runLexerOnText(s);
        assertEquals(tokens.size(), 2);
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, expected, 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, s.length()), tokens.get(1));
    }

    /**
     * Test the method `testStringLiteralEscapedQuotes`.
     * The method tests the functionality of the lexer to correctly tokenize a string literal with escaped quotes within the source code.
     *
     * @throws IOException If an I/O error occurs while reading the text.
     */
    @Test
    public void testStringLiteralEscapedQuotes() throws IOException {
        String s = "\"\\\"Hello there\\\" this is a string\"";
        String expected = "\"Hello there\" this is a string";
        LinkedList<Token> tokens = runLexerOnText(s);
        assertEquals(tokens.size(), 2);
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, expected, 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, s.length()), tokens.get(1));
    }

    /**
     * This method tests the functionality of the `testEmptyStringLiteral` method. It verifies that the lexer correctly identifies and tokenizes an empty string literal in the source
     * code.
     *
     * @throws IOException if an I/O error occurs while reading the text.
     */
    @Test
    public void testEmptyStringLiteral() throws IOException {
        LinkedList<Token> tokens = runLexerOnText("\"\"");
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "", 1, 0), tokens.get(0));
    }

    /**
     * Unit test for the {@code testSubStringIsEmptyString} method in the {@code LexerTest} class.
     * This method tests the ability of the Lexer to correctly tokenize a substring that represents an empty string literal.
     * It verifies that the tokens returned have the correct token type, value, line number, and position.
     *
     * @throws IOException If an I/O error occurs while reading the text.
     */
    @Test
    public void testSubStringIsEmptyString() throws IOException {
        LinkedList<Token> tokens = runLexerOnText("\"\\\"Hello world\\\"\\\"\\\"\"");

        String expected = "\"Hello world\"\"\"";
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, expected, 1, 0), tokens.get(0));
    }

    /**
     * This method is a unit test for the `testStringLiteralWithoutMatchingQuote` functionality.
     * It tests the ability of the Lexer to correctly throw an `IllegalStateException` when encountering a string literal without a matching quote.
     *
     * @throws IllegalStateException If the method does not throw an exception for one or more test scenarios.
     */
    @Test
    public void testStringLiteralWithoutMatchingQuote() throws IllegalStateException {
        assertThrows(IllegalStateException.class, () -> runLexerOnText("\\\""));
        assertThrows(IllegalStateException.class, () -> runLexerOnText("abcd \\\"Hello there"));
        assertThrows(IllegalStateException.class, () -> runLexerOnText("abcd \\\"Hello there\"\""));
        assertThrows(IllegalStateException.class, () -> runLexerOnText("Hello \\\""));
    }

    /**
     * Test the processSymbol method by checking if the lexer correctly tokenizes various symbols.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testProcessSymbol() throws IOException {
        String text = "<= >= <> = < > ( ) + - * ///";
        LinkedList<Token> tokens = runLexerOnText(text);
        assertEquals(new Token(Token.TokenType.LESSTHANEQUALTO, "<=", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.GREATERTHANEQUALTO, ">=", 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.NOTEQUALS, "<>", 1, 6), tokens.get(2));
        assertEquals(new Token(Token.TokenType.EQUALS, "=", 1, 9), tokens.get(3));
        assertEquals(new Token(Token.TokenType.LESSTHAN, "<", 1, 11), tokens.get(4));
        assertEquals(new Token(Token.TokenType.GREATERTHAN, ">", 1, 13), tokens.get(5));
        assertEquals(new Token(Token.TokenType.LPAREN, "(", 1, 15), tokens.get(6));
        assertEquals(new Token(Token.TokenType.RPAREN, ")", 1, 17), tokens.get(7));
        assertEquals(new Token(Token.TokenType.PLUS, "+", 1, 19), tokens.get(8));
        assertEquals(new Token(Token.TokenType.MINUS, "-", 1, 21), tokens.get(9));
        assertEquals(new Token(Token.TokenType.MULTIPLY, "*", 1, 23), tokens.get(10));
        assertEquals(new Token(Token.TokenType.DIVIDE, "/", 1, 25), tokens.get(11));
    }

    public void testProcessLabels() throws IOException {
        // TODO
        String text = "PRINT INPUT READ DATA.... ETC";
    }


    /**
     * This method is a unit test for the testNumberInParentheses functionality.
     * It tests the ability of the Lexer to correctly tokenize a string containing a number enclosed in parentheses.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testNumberInParantheses() throws IOException {
        String text = "(4 + 2)\n";
        LinkedList<Token> tokens = runLexerOnText(text);
        assertEquals(new Token(Token.TokenType.LPAREN, "(", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.NUMBER, "4", 1, 1), tokens.get(1));
        assertEquals(new Token(Token.TokenType.PLUS, "+", 1, 3), tokens.get(2));
        assertEquals(new Token(Token.TokenType.NUMBER, "2", 1, 5), tokens.get(3));
        assertEquals(new Token(Token.TokenType.RPAREN, ")", 1, 6), tokens.get(4));
    }

    /**
     * This method is a unit test for the comparisonStatement functionality.
     * It tests the ability of the method to tokenize a given text and compare the tokens with the expected tokens.
     *
     * @throws IOException if an I/O error occurs while reading the text.
     */
    @Test
    public void comparisonStatement() throws IOException {
        String text = "4 <= 4.5\n";
        LinkedList<Token> tokens = runLexerOnText(text);
        assertEquals(new Token(Token.TokenType.NUMBER, "4", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.LESSTHANEQUALTO, "<=", 1, 2), tokens.get(1));
        assertEquals(new Token(Token.TokenType.NUMBER, "4.5", 1, 5), tokens.get(2));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 8), tokens.get(3));
    }

    /**
     * Test the functionality of the `testLabels` method in the `LexerTest` class.
     * It verifies that the method correctly tokenizes a string and compares the generated tokens with the expected ones.
     *
     * @throws IOException if an I/O error occurs while reading the text
     */
    @Test
    public void testLabels() throws IOException {
        String text = "Hello: World: Testing testing";
        LinkedList<Token> tokens = runLexerOnText(text);
        assertEquals(new Token(Token.TokenType.LABEL, "Hello:", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.LABEL, "World:", 1, 7), tokens.get(1));
    }

    /**
     * This method is a unit test for the functionality of the method testNumbersWithParanthesisAndOperatorsWithNoSpaces().
     * It tests if the lexer correctly tokenizes a given text containing numbers, parentheses, and operators without spaces.
     * It verifies if the tokens returned have the correct token type, value, line number, and position.
     *
     * @throws IOException If an I/O error occurs while reading the text.
     */
    @Test
    public void testNumbersWithParanthesisAndOperatorsWithNoSpaces() throws IOException {
        String text = "(4+5)";
        LinkedList<Token> tokens = runLexerOnText(text);
        assertEquals(new Token(Token.TokenType.LPAREN, "(", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.NUMBER, "4", 1, 1), tokens.get(1));
        assertEquals(new Token(Token.TokenType.PLUS, "+", 1, 2), tokens.get(2));
        assertEquals(new Token(Token.TokenType.NUMBER, "5", 1, 3), tokens.get(3));
        assertEquals(new Token(Token.TokenType.RPAREN, ")", 1, 4), tokens.get(4));
    }

    /**
     * Tests the functionality of the method testInvalidSymbol.
     * It verifies that the method throws an IllegalStateException when given invalid symbol inputs.
     *
     * @throws IllegalStateException if the method does not throw an exception for one or more test scenarios.
     */
    @Test
    public void testInvalidSymbol() throws IllegalStateException {
        assertThrows(IllegalStateException.class, () -> runLexerOnText("InvalidSymbol:: World: Testing testing"));
        assertThrows(IllegalStateException.class, () -> runLexerOnText(":: World: Testing testing"));
    }

    @Test
    public void testHelloWorldFromFile() throws IOException {
        LinkedList<Token> tokens =  lexer.lex("src/test/resources/hello_world.bas");
        assertEquals(tokens.size(), 7);
        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.PRINT, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello, World!", 1, 9), tokens.get(2));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 24), tokens.get(3));
        assertEquals(new Token(Token.TokenType.NUMBER, "20", 2, 0), tokens.get(4));
        assertEquals(new Token(Token.TokenType.END, 2, 3), tokens.get(5));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 2, 6), tokens.get(6));
    }

    /**
     * This method is a unit test for the testHelloWorld functionality.
     * It tests the ability of the lexer to correctly tokenize a given text and compare the tokens with the expected tokens.
     *
     * @throws IOException if an I/O error occurs while reading the text.
     */
    @Test
    public void testHelloWorld() throws IOException {
        LinkedList<Token> tokens =  runLexerOnText("10 PRINT \"Hello, World!\"\n" +
                "20 END");
        assertEquals(tokens.size(), 7);
        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.PRINT, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello, World!", 1, 9), tokens.get(2));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 24), tokens.get(3));
        assertEquals(new Token(Token.TokenType.NUMBER, "20", 2, 0), tokens.get(4));
        assertEquals(new Token(Token.TokenType.END, 2, 3), tokens.get(5));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 2, 6), tokens.get(6));
    }

    @Test
    public void testForLoopFromFile() throws IOException {
        LinkedList<Token> tokens =  lexer.lex("src/test/resources/for_loop.bas");

        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.FOR, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.WORD, "I", 1, 7), tokens.get(2));
        assertEquals(new Token(Token.TokenType.EQUALS, "=", 1, 9), tokens.get(3));
        assertEquals(new Token(Token.TokenType.NUMBER, "1", 1, 11), tokens.get(4));
        assertEquals(new Token(Token.TokenType.TO, 1, 13), tokens.get(5));
        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 16), tokens.get(6));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 18), tokens.get(7));
    }

    /**
     * Tests the functionality of the testForLoop method.
     * It verifies that the method correctly tokenizes a given text containing a FOR loop and compares the tokens with the expected tokens.
     *
     * @throws IOException if an I/O error occurs while reading the text.
     */
    @Test
    public void testForLoop() throws IOException {
        LinkedList<Token> tokens =  runLexerOnText("10 FOR I = 1 TO 10\n" +
                "20 PRINT I\n" +
                "30 NEXT I\n" +
                "40 END");

        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.FOR, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.WORD, "I", 1, 7), tokens.get(2));
        assertEquals(new Token(Token.TokenType.EQUALS, "=", 1, 9), tokens.get(3));
        assertEquals(new Token(Token.TokenType.NUMBER, "1", 1, 11), tokens.get(4));
        assertEquals(new Token(Token.TokenType.TO, 1, 13), tokens.get(5));
        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 16), tokens.get(6));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 18), tokens.get(7));
    }

    /**
     * This method is a unit test for the {@code testPrintStatement} functionality.
     * It tests the ability of the method to tokenize a given text and compare the tokens with the expected tokens.
     *
     * @throws IOException If an I/O error occurs while reading the text.
     */
    @Test
    public void testPrintStatement() throws IOException {
        LinkedList<Token> tokens = runLexerOnText("PRINT \"Hello World\"");
        assertEquals(new Token(Token.TokenType.PRINT, 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello World", 1, 6), tokens.get(1));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, "PRINT \"Hello World\"".length()), tokens.get(2));
    }

    @Test
    public void testFileWithStringLiteralAndCarriageReturn() throws IOException {
        LinkedList<Token> tokens =  lexer.lex("src/test/resources/hello_world2.bas");
        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.PRINT, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello, World!", 1, 9), tokens.get(2));
    }

    /**
     * This method is a unit test to verify the functionality of the testStringLiteralAndCarriageReturn method.
     * It tests the ability of the Lexer to correctly tokenize a string literal containing a carriage return ("\r").
     *
     * @throws IOException If an I/O error occurs while reading the text.
     */
    @Test
    public void testStringLiteralAndCarriageReturn() throws IOException {
        LinkedList<Token> tokens =  runLexerOnText("10 PRINT \"Hello, \\rWorld!\"\n" +
                "20 END");
        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.PRINT, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello, World!", 1, 9), tokens.get(2));
    }

    @Test
    public void testFileWithMultiLineStringLiteral() throws IOException {
        LinkedList<Token> tokens =  lexer.lex("src/test/resources/hello_world3.bas");
        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.PRINT, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello,\n\n\nWorld!", 1, 9), tokens.get(2));
    }

    /**
     * Tests the functionality of the method testMultiLineStringLiteral.
     *
     * @throws IOException If an I/O error occurs while reading the text.
     */
    @Test
    public void testMultiLineStringLiteral() throws IOException {
        LinkedList<Token> tokens =  runLexerOnText("10 PRINT \"Hello,\\n\\n\\nWorld!\"\n" +
                "20 END");
        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.PRINT, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello,\n\n\nWorld!", 1, 9), tokens.get(2));
    }

    @Test
    public void testFileWithEscapedQuotesInLineStringLiteral() throws IOException {
        LinkedList<Token> tokens =  lexer.lex("src/test/resources/hello_world4.bas");

        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.PRINT, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello, \"Hi\" World!", 1, 9), tokens.get(2));
    }

    /**
     * Test case for the method testEscapedQuotesInLineStringLiteral.
     *
     * @throws IOException If an I/O error occurs while reading the text.
     */
    @Test
    public void testEscapedQuotesInLineStringLiteral() throws IOException {
        LinkedList<Token> tokens =  runLexerOnText("10 PRINT \"Hello, \\\"Hi\\\" World!\" + \"Hello World\"\n" +
                "20 END");

        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.PRINT, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello, \"Hi\" World!", 1, 9), tokens.get(2));
    }

    @Test
    public void testFileWithConcatenation() throws IOException {
        LinkedList<Token> tokens =  lexer.lex("src/test/resources/hello_world5.bas");

        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.PRINT, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello", 1, 9), tokens.get(2));
        assertEquals(new Token(Token.TokenType.PLUS, "+", 1, 17), tokens.get(3));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "World", 1, 19), tokens.get(4));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 26), tokens.get(5));
    }

    @Test
    public void testFilePrintList() {
        LinkedList<Token> tokens =  lexer.lex("src/test/resources/print_list.txt");

        assertEquals(new Token(Token.TokenType.PRINT, 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.WORD, "F%", 1, 6), tokens.get(1));
        assertEquals(new Token(Token.TokenType.COMMA, 1, 8), tokens.get(2));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "DEG F = ", 1, 10), tokens.get(3));
        assertEquals(new Token(Token.TokenType.COMMA, 1, 20), tokens.get(4));
        assertEquals(new Token(Token.TokenType.WORD, "C%", 1, 21), tokens.get(5));
        assertEquals(new Token(Token.TokenType.COMMA, 1, 23), tokens.get(6));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "DEG C", 1, 25), tokens.get(7));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 32), tokens.get(8));
    }

    /**
     * This method is a unit test for the `testConcatenation` method.
     * It verifies that the `runLexerOnText` method correctly tokenizes the input text and returns the expected tokens.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    public void testConcatenation() throws IOException {
        String text = "10 PRINT \"Hello\" + \"World\"";
        LinkedList<Token> tokens =  runLexerOnText(text);

        assertEquals(new Token(Token.TokenType.NUMBER, "10", 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.PRINT, 1, 3), tokens.get(1));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "Hello", 1, 9), tokens.get(2));
        assertEquals(new Token(Token.TokenType.PLUS, "+", 1, 17), tokens.get(3));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "World", 1, 19), tokens.get(4));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 26), tokens.get(5));
    }

    @Test
    public void testCommaParsing() throws IOException {
        String text = "PRINT F%, \"DEG F = \",C%, \"DEG C\"";
        LinkedList<Token> tokens =  runLexerOnText(text);

        assertEquals(new Token(Token.TokenType.PRINT, 1, 0), tokens.get(0));
        assertEquals(new Token(Token.TokenType.WORD, "F%", 1, 6), tokens.get(1));
        assertEquals(new Token(Token.TokenType.COMMA, 1, 8), tokens.get(2));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "DEG F = ", 1, 10), tokens.get(3));
        assertEquals(new Token(Token.TokenType.COMMA, 1, 20), tokens.get(4));
        assertEquals(new Token(Token.TokenType.WORD, "C%", 1, 21), tokens.get(5));
        assertEquals(new Token(Token.TokenType.COMMA, 1, 23), tokens.get(6));
        assertEquals(new Token(Token.TokenType.STRINGLITERAL, "DEG C", 1, 25), tokens.get(7));
        assertEquals(new Token(Token.TokenType.ENDOFLINE, 1, 32), tokens.get(8));
    }
}
