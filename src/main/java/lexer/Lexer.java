package lexer;

import java.io.IOException;
import java.util.*;

/**
 * The Lexer class is responsible for lexing the source code and converting it into a list of tokens.
 * It provides methods for processing words, numbers, symbols, string literals, and handling whitespace and newlines.
 * The lexer initializes itself with a source code file and provides a method for lexing the file and returning a list of tokens.
 */
public class Lexer {

    // Handles the reading and navigation of the source code.
    private CodeHandler handler;

    // Tracks the current line number in the source code.
    private int lineNo;

    // Tracks the current position within the current line.
    private int position;

    // Represents a HashMap that stores known words and their corresponding token types.
    private final HashMap<String, Token.TokenType> knownWords;


    // Represents a HashMap that stores one-character symbols and their corresponding TokenType.
    private final HashMap<String, Token.TokenType> oneCharacterSymbols;

    // Stores a mapping of two-character symbols to their corresponding TokenTypes.
    private final HashMap<String, Token.TokenType> twoCharacterSymbols;

    private final Set<Character> validEscapeCharacters = new HashSet<>(Arrays.asList('n', '\"', 'r'));

    private final String OS = System.getProperty("os.name").toLowerCase();

    private final boolean isWindows = OS.contains("win");
    private final boolean isUnix = OS.contains("nix") || OS.contains("nux") || OS.contains("aix");

    private final boolean isMac = OS.contains("mac");

    private final String LINE_SEPARATOR = (isUnix || isMac) ? "\n": "\r\n";

    /**
     * Constructor for the Lexer. Initializes the lexer with the source code file.
     */
    public Lexer() {
        knownWords = new HashMap<>();
        populateKnownWords();
        oneCharacterSymbols = new HashMap<>();
        populateOneCharacterSymbols();
        twoCharacterSymbols = new HashMap<>();
        populateTwoCharacterSymbols();
    }

    /**
     * Populates the twoCharacterSymbols HashMap with predefined two-character symbols and their corresponding token types.
     * The keys represent the symbol characters, and the values represent the corresponding TokenType enum.
     */
    private void populateTwoCharacterSymbols() {
        twoCharacterSymbols.put("<=", Token.TokenType.LESSTHANEQUALTO);
        twoCharacterSymbols.put(">=", Token.TokenType.GREATERTHANEQUALTO);
        twoCharacterSymbols.put("<>", Token.TokenType.NOTEQUALS);
    }

    /**
     * Populates the oneCharacterSymbols HashMap with predefined one-character symbols and their corresponding token types.
     * The keys represent the symbol characters, and the values represent the corresponding TokenType enum.
     */
    private void populateOneCharacterSymbols() {
        oneCharacterSymbols.put("=", Token.TokenType.EQUALS);
        oneCharacterSymbols.put("<", Token.TokenType.LESSTHAN);
        oneCharacterSymbols.put(">", Token.TokenType.GREATERTHAN);
        oneCharacterSymbols.put("(", Token.TokenType.LPAREN);
        oneCharacterSymbols.put(")", Token.TokenType.RPAREN);
        oneCharacterSymbols.put("+", Token.TokenType.PLUS);
        oneCharacterSymbols.put("-", Token.TokenType.MINUS);
        oneCharacterSymbols.put("*", Token.TokenType.MULTIPLY);
        oneCharacterSymbols.put("/", Token.TokenType.DIVIDE);
        oneCharacterSymbols.put(",", Token.TokenType.COMMA);
    }

    /**
     * Populates the knownWords HashMap with predefined words and their corresponding token types.
     *
     * This method adds predefined words and their corresponding token types to the knownWords HashMap.
     * The keys represent the words, and the values represent the TokenType enum for the word.
     */
    private void populateKnownWords() {
        knownWords.put("if", Token.TokenType.IF);
        knownWords.put("print", Token.TokenType.PRINT);
        knownWords.put("read", Token.TokenType.READ);
        knownWords.put("input", Token.TokenType.INPUT);
        knownWords.put("data", Token.TokenType.DATA);
        knownWords.put("gosub", Token.TokenType.GOSUB);
        knownWords.put("goto", Token.TokenType.GOTO);
        knownWords.put("for", Token.TokenType.FOR);
        knownWords.put("to", Token.TokenType.TO);
        knownWords.put("step", Token.TokenType.STEP);
        knownWords.put("next", Token.TokenType.NEXT);
        knownWords.put("return", Token.TokenType.RETURN);
        knownWords.put("then", Token.TokenType.THEN);
        knownWords.put("while", Token.TokenType.WHILE);
        knownWords.put("end", Token.TokenType.END);
        knownWords.put("random", Token.TokenType.FUNCTIONNAME);
        knownWords.put("random%", Token.TokenType.FUNCTIONNAME);
        knownWords.put("left$", Token.TokenType.FUNCTIONNAME);
        knownWords.put("right$", Token.TokenType.FUNCTIONNAME);
        knownWords.put("mid$", Token.TokenType.FUNCTIONNAME);
        knownWords.put("num$", Token.TokenType.FUNCTIONNAME);
        knownWords.put("val", Token.TokenType.FUNCTIONNAME);
        knownWords.put("val%", Token.TokenType.FUNCTIONNAME);
        knownWords.put("pow", Token.TokenType.FUNCTIONNAME);
        knownWords.put("pow%", Token.TokenType.FUNCTIONNAME);
        knownWords.put("int", Token.TokenType.FUNCTIONNAME);
        knownWords.put("float", Token.TokenType.FUNCTIONNAME);
    }

    /**
     * Process a word token.
     *
     * This method reads characters from the source code and constructs a word token
     * by appending valid characters to a StringBuilder. It checks if the constructed token
     * is present in the knownWords map. If it is, it creates a Token object with the corresponding
     * TokenType, line number, and position. If the token is not known, it creates a Token object
     * with TokenType.WORD and the word as its value.
     *
     * @return A Token object representing the processed word.
     * @throws IllegalStateException If an unrecognized word start is encountered.
     */
    private Token processWord() {
        char head = handler.peek(0);
        if (!Character.isLetter(head)) { // Checking for a Letter only at beginning
            throw new IllegalStateException(String.format("Unrecognized word start: %c%nLine: %d%nPosition: %d%n", head, lineNo, position));
        }

        // Building the token with a starting letter
        StringBuilder wordBuilder = new StringBuilder();
        wordBuilder.append(handler.getChar());
        position++;

        // Continue to append the remaining characters to form the complete token.
        wordBuilder.append(scanWordSuffix());

        // Convert the tokenBuilder to a String and check if it's in the knownWords map.
        String token = wordBuilder.toString();

        // Check if the next character is an opening parenthesis
        if (!handler.isDone() && handler.peek(0) == '(') {
            // If the token is a known function name, return a FUNCTIONNAME token
            return new Token(Token.TokenType.FUNCTIONNAME, token, lineNo, position - wordBuilder.length());
        } else {
            if (knownWords.containsKey(token.toLowerCase())) {
                // Return a Token with corresponding TokenType, lineNo, position.
                return new Token(knownWords.get(token.toLowerCase()), lineNo,
                        position - wordBuilder.length());
            } else if (token.charAt(token.length() - 1) == ':') {
                return new Token(Token.TokenType.LABEL, token, lineNo,
                        position - wordBuilder.length());
            } else {
                // If not, create a new WORD Token with the word as its value.
                return new Token(Token.TokenType.WORD, token, lineNo,
                        position - wordBuilder.length());
            }
        }
    }

    private boolean isValidWordChar(char c) {
        return (Character.isLetterOrDigit(c) || c == '_' || c == '$' || c == '%' || c == ':');
    }

    /**
     * Appends valid token characters to the given tokenBuilder.
     *
     * @return A StringBuilder containing the appended valid token characters.
     */
    private StringBuilder scanWordSuffix() {
        StringBuilder wordSuffix = new StringBuilder();
        while (!handler.isDone() && isValidWordChar(handler.peek(0))) {
            char c = handler.peek(0);

            // Append the character and update position
            wordSuffix.append(c);
            advancePosition();

            // If a token has ended with $, %, or : , stop appending more characters
            if (c == '$' || c == '%' || c == ':') {
                break; // breaking after appending $, %, or :
            }
        }
        return wordSuffix;
    }

    /**
     * Processes a sequence of numerical characters into a Token object.
     * Handles decimals (accepting only one per number).
     * The process stops when it encounters a whitespace or any non-numeric character.
     *
     * @return A Token object representing the processed number.
     *
     * @throws IllegalStateException If an invalid character for a Number token is encountered.
     */
    private Token processNumber() {
        StringBuilder numberBuilder = new StringBuilder();
        boolean decimalFound = false;
        while (!handler.isDone() && !Character.isWhitespace(handler.peek(0))) {
            char c = handler.peek(0);

            // Only accept one decimal
            if (c == '.' && decimalFound) {
                throw new IllegalStateException(
                        String.format("Invalid character for Number token: " +
                        "'%c'%nLine: %d%nPosition: %d%n", c, lineNo, position));
            }

            // If not letter, character, or decimal, break
            if ((!Character.isLetterOrDigit(c) &&  c != '.')) {
                break;
            }

            numberBuilder.append(c);
            if (c == '.') {
                decimalFound = true;
            }
            advancePosition();
        }
        return new Token(Token.TokenType.NUMBER, numberBuilder.toString(), lineNo,
                position - numberBuilder.length());
    }

    /**
     * Handles whitespace characters in the source code.
     * If the current character is a space character, it advances the position by one.
     * If the current character is a newline character, it adds an ENDOFLINE token to the list of tokens
     * and updates the line number and position values accordingly.
     * If the current character is a carriage return character, it does nothing.
     *
     * @param curChar The current character being analyzed.
     * @param tokens   The list of tokens where the ENDOFLINE token will be added if necessary.
     */
    private void handleWhitespace(char curChar, LinkedList<Token> tokens) {
        if (Character.isSpaceChar(curChar)) {
            position++;
        } else if (curChar == '\n') { // If current character is a newline character add a ENDOFLINE token to token list.
            tokens.add(new Token(Token.TokenType.ENDOFLINE, lineNo, position));
            lineNo++;
            position = 0;
        } else if (curChar == '\r') {
            // Do nothing.
        }
        handler.swallow(1);
    }

    /**
     * Handles Newlines in Windows ("\r\n"), Unix, and Mac
     * @param handler
     * @return
     */
    private boolean isEndOfLine(CodeHandler handler) {
        if (isUnix || isMac) {
            return handler.peek(0) == '\n';
        } else if (isWindows) {
            return ("" + handler.peek(0) + handler.peek(1)).equals(System.lineSeparator());
        }
        return false;
    }

    /**
     * This method handles a string literal token in the source code. It reads characters until it encounters
     * a closing double quote ("). It takes care of escape sequences and constructs the string literal value.
     * If it encounters an unterminated string literal, it throws a RuntimeException.
     *
     * @return a Token object representing the string literal
     * @throws IllegalStateException if an invalid escaped character is encountered or if the string literal is unterminated
     */
    private Token HandleStringLiteral() {
        StringBuilder stringLiteralBuilder = new StringBuilder();

        // Don't append the opening quote to get the correct string representation in the Token
        advancePosition();

        int size = 1; // track the size of the original string, including literal backslashes
        boolean quoteIsOpen = false; // track the state of escaped quotes that must have a matching end quote
        boolean escapeNext = false; // when we encounter a backslash, we expect the next char to be a control character ('\n', '\r')
        while (!handler.isDone()) {
            char c = handler.peek(0);

            if (escapeNext && !validEscapeCharacters.contains(c)) {
                // Only allow escaped '\\' followed by chars 'n' and '\"'
                throw new IllegalStateException(
                        String.format("Invalid escaped character: '%c' in string '%s'%nLine: %d%nPosition: %d%n",
                                c, stringLiteralBuilder, lineNo, position)
                );
            }

            // Break after finding the ending quote, do not append
            if (!escapeNext && c == '\"' || isEndOfLine(handler)) {
                size++;
                advancePosition();
                break;
            }

            if (!escapeNext && c == '\\') {
                // don't append the literal byte, but increment size
                size++;
                escapeNext = true;
                advancePosition();
                continue;
            } else if (escapeNext && c == 'r') {
                // ignore carriage return completely, don't increment
                size++;
                escapeNext = false;
                advancePosition();
                continue;
            } else if (escapeNext && c == 'n') {
                // Found a valid new line
                escapeNext = false;
                c = '\n';
            } else if (escapeNext && c == '\"') {
                // Inner quote found, track opening/closing quotes for validation
                quoteIsOpen = !quoteIsOpen;
                escapeNext = false;
            }

            stringLiteralBuilder.append(c);
            advancePosition();
        }

        if (quoteIsOpen || escapeNext) {
            throw new IllegalStateException(
                    String.format("Unterminated string literal: '%s'%nLine: %d%nPosition: %d%n",
                            stringLiteralBuilder, lineNo, position)
            );
        }

        return new Token(
                Token.TokenType.STRINGLITERAL,
                stringLiteralBuilder.toString(),
                lineNo,
                position - stringLiteralBuilder.length() - size // use the original bytes to correct for the position
        );
    }

    /**
     * This method processes a symbol character and returns a Token object representing the symbol.
     * It checks if the symbol is a two-character symbol or a one-character symbol
     * and returns the corresponding TokenType and symbol value.
     * This method can throw a RuntimeException if the symbol character is not recognized.
     *
     * @return a Token object representing the symbol
     * @throws IllegalStateException if the symbol character is not recognized
     */
    private Token processSymbol() {
        StringBuilder symbolBuilder = new StringBuilder();
        if (twoCharacterSymbols.containsKey((handler.peek(0) + "" + handler.peek(1)).toLowerCase())) {
            symbolBuilder.append(handler.getChar());
            symbolBuilder.append(handler.getChar());
            position += 2;
            return new Token(twoCharacterSymbols.get(symbolBuilder.toString()), symbolBuilder.toString(), lineNo, position - symbolBuilder.length());
        } else if (oneCharacterSymbols.containsKey((handler.peek(0) + "").toLowerCase())) {
            symbolBuilder.append(handler.getChar());
            position++;
            return new Token(oneCharacterSymbols.get(symbolBuilder.toString()), symbolBuilder.toString(), lineNo, position - symbolBuilder.length());
        } else {
            throw new IllegalStateException(
                    String.format("Undetermined symbol: %c%nLine: %d%nPosition: %d%n", handler.peek(0), lineNo, position)
            );
        }
    }

    private Token handleComma() {
        Token commaToken = new Token(Token.TokenType.COMMA, lineNo, position);
        advancePosition();
        return commaToken;
    }

    /**
     * Performs the lexing of the source code. It reads the source code character by character,
     * identifying and collecting tokens.
     *
     * @param filename The name of the source code file to be lexed.
     * @return A LinkedList of tokens identified in the source code.
     * @throws RuntimeException If an IOException occurs while reading the file.
     */
    public LinkedList<Token> lex(String filename) {
        try {
            handler = new CodeHandler(filename);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filename, e);
        }

        // Stores the identified tokens.
        LinkedList<Token> tokens = new LinkedList<>();
        lineNo = 1;
        position = 0;

        while (!handler.isDone()) {
            char c = handler.peek(0);
            if (Character.isWhitespace(c)) {
                handleWhitespace(c, tokens);
            } else if (c == ',') {
                // Commas can separate valid words and strings in a print statement
                tokens.add(handleComma());
            } else if (Character.isLetter(c)) {
                tokens.add(processWord());
            } else if (Character.isDigit(c) || (c == '.' && Character.isDigit(handler.peek(1)))) {
                tokens.add(processNumber());
            } else if (c == '\"') {
                tokens.add(HandleStringLiteral());
            } else {
                tokens.add(processSymbol());
            }
        }

        // If the token list is not empty and the last token is not an end of line token,
        // add an end of line token at the current line number and character position to the list of tokens.
        if (!tokens.isEmpty() && tokens.getLast().getTokenType() != Token.TokenType.ENDOFLINE) {
            tokens.add(new Token(Token.TokenType.ENDOFLINE, lineNo, position));
        }

        return tokens;
    }

    private void advancePosition() {
        handler.swallow(1);
        position++;
    }
}