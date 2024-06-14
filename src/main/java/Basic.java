import interpreter.Interpreter;
import lexer.Lexer;
import lexer.Token;
import node.ProgramNode;
import parser.Parser;

import java.util.*;

/**
 * Takes an input file and interprets a SimpleBASIC program or runs the shell in interactive mode
 */
public class Basic {

    private static boolean DEBUG = false;

    /**
     * Starting point to initiate the tokenization process.
     *
     * @param args Command line arguments
     *             - Expects a single argument: the file name which has to be tokenized.
     */
    public static void main(String[] args) {

        // Validate that exactly one argument (filename) is provided
        if (args.length < 1) {
            System.out.println("Usage: java -jar app.jar [filename] [-interactive] [-i] [-debug] [-d]");
            System.exit(1); //  Exiting with an error status
        }

        Set<String> arguments = new HashSet<>(List.of(args));
        if (arguments.contains("-interactive") || arguments.contains("-i")) {
            Shell shell = new Shell();
            shell.run();
        } else if (arguments.contains("-debug") || arguments.contains("-d")) {
            DEBUG = true;
        }

        // Perform lexical analysis on the file and store the resulting tokens
        Lexer lexer = new Lexer();
        LinkedList<Token> tokens = lexer.lex(args[0]);

        // Iterating through each of the tokens and printing their string representation
        if (DEBUG) {
            printBanner();
            System.out.println("TOKENS");
            printBanner();
            for (Token token : tokens) {
                System.out.println(token.toString());
            }
        }

        // Parse the tokens, create an AST, and return the root
        Parser parser = new Parser(tokens);
        ProgramNode program = parser.parse();

        if (DEBUG) {
            printBanner();
            System.out.println("AST");
            printBanner();
            System.out.println(program);
            printBanner();
        }

        Interpreter interpreter = new Interpreter(program);
        interpreter.interpret();
    }

    private static void printBanner() {
        System.out.println("======================================================================");
    }
}
