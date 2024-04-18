import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The Basic class is the entry point for the lexer program. It processes
 * a file to tokenize its content and prints out the tokens.
 */
public class Basic {

    private static final Scanner scanner = new Scanner(System.in);

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
            runInteractiveInterpreter();
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

    private static void runInteractiveInterpreter() {
        System.out.println("SimpleBASIC (1.0)");
        printOptions();

        List<String> program = new ArrayList<>();

        String input;
        while (true) {
            System.out.print(">>> ");
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("RUN")) {
                runProgram(program);
            } else if (input.equalsIgnoreCase("HELP")) {
                printOptions();
            } else if (input.equalsIgnoreCase("EXIT")) {
                System.exit(0);
            } else if (input.equalsIgnoreCase("NEW")) {
                System.out.println("Current program discarded");
                program.clear();
            } else if (input.equalsIgnoreCase("OUTPUT")) {
                printBanner();
                System.out.println(String.join("\n", program));
                printBanner();
            } else if (input.split(" ")[0].equalsIgnoreCase("SAVE")) {
                String[] saveCommand = input.split(" ");

                String fileName = saveCommand.length > 1 ? saveCommand[1] : "output.txt";
                System.out.printf("Writing to file '%s'%n", fileName);

                writeToFile(fileName, String.join("\n", program));
            } else if (input.split(" ")[0].equalsIgnoreCase("LOAD")) {
                String[] loadCommand = input.split(" ");
                if (loadCommand.length <= 1) {
                    System.err.println("Expected a filename after LOAD command");
                    continue;
                }
                String fileName = loadCommand[1];
                System.out.printf("Loading file: '%s'%n", fileName);

                program = loadFile(fileName);
            } else if (!input.isBlank()) {
                program.add(input);
            }
        }
    }

    private static void printOptions() {
        System.out.println("HELP           Print help options");
        System.out.println("RUN            Compile and run the current program");
        System.out.println("NEW            Start a new program discarding the current one");
        System.out.println("OUTPUT         Output the current program");
        System.out.println("SAVE           Save current program to current directory as 'output.txt'");
        System.out.println("SAVE [file]    Save current program to current directory as 'file'");
        System.out.println("LOAD [file]    Load a file");
        System.out.println("EXIT           Quit the interpreter");
    }

    private static void runProgram(List<String> program) {
        LinkedList<Token> tokens;
        try {
            tokens = runLexerOnText(String.join("\n", program), new Lexer());

            Parser parser = new Parser(tokens);
            ProgramNode programNode = parser.parse();

            Interpreter interpreter = new Interpreter(programNode);
            interpreter.interpret();

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static List<String> loadFile(String fileName) {
        try {
            Path myPath = Paths.get(fileName);
            String program = new String(Files.readAllBytes(myPath));
            return new ArrayList<>(Arrays.asList(program.split("\n")));
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return new ArrayList<>();
        }
    }

    private static void writeToFile(String fileName, String contents) {
        try {
            Path filePath = Paths.get(fileName);
            Files.write(filePath, String.join("\n", contents).getBytes());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private static LinkedList<Token> runLexerOnText(String text, Lexer lexer) throws IOException {
        Path tempFilePath = Files.createTempFile("interactive_program", ".txt");
        Files.writeString(tempFilePath, text);
        return lexer.lex(tempFilePath.toString());
    }

    private static void printBanner() {
        System.out.println("======================================================================");
    }
}
