import interpreter.Interpreter;
import lexer.Lexer;
import lexer.Token;
import node.ProgramNode;
import parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Shell {

    private static final Scanner scanner = new Scanner(System.in);

    public Shell() {}

    public void run() {
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

    private void printOptions() {
        System.out.println("HELP           Print help options");
        System.out.println("RUN            Compile and run the current program");
        System.out.println("NEW            Start a new program discarding the current one");
        System.out.println("OUTPUT         Output the current program");
        System.out.println("SAVE           Save current program to current directory as 'output.txt'");
        System.out.println("SAVE [file]    Save current program to current directory as 'file'");
        System.out.println("LOAD [file]    Load a file");
        System.out.println("EXIT           Quit the interpreter");
    }

    private void runProgram(List<String> program) {
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

    private List<String> loadFile(String fileName) {
        try {
            Path myPath = Paths.get(fileName);
            String program = new String(Files.readAllBytes(myPath));
            return new ArrayList<>(Arrays.asList(program.split("\n")));
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return new ArrayList<>();
        }
    }

    private void writeToFile(String fileName, String contents) {
        try {
            Path filePath = Paths.get(fileName);
            Files.write(filePath, String.join("\n", contents).getBytes());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private LinkedList<Token> runLexerOnText(String text, Lexer lexer) throws IOException {
        Path tempFilePath = Files.createTempFile("interactive_program", ".txt");
        Files.writeString(tempFilePath, text);
        return lexer.lex(tempFilePath.toString());
    }

    private void printBanner() {
        System.out.println("======================================================================");
    }
}
