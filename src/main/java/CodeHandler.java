import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * CodeHandler class to manage and navigate through the content of a code document.
 */
public class CodeHandler {

    private final String document; // Stores the content of the code document.
    private int index; // Current position within the code document for navigation and processing.

    /**
     * Constructs a CodeHandler by reading and storing the content of the file specified by filename.
     *
     * @param filename the name of the file to read and store the content from.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public CodeHandler(String filename) throws IOException {
        Path myPath = Paths.get(filename);
        document = new String(Files.readAllBytes(myPath));
    }

    /**
     * Returns the character at the current position offset by a given number of characters.
     *
     * @param i the number of characters to offset by
     * @return the character at the current position offset by the given number of characters
     */
    public char peek(int i) {
        if (i + index >= document.length()){ // If trying to peek past the end of the document
            return ' '; // Return a space as a placeholder
        }
        return document.charAt(index + i);
    }

    /**
     * Returns a substring from the current position to a specified length ahead.
     *
     * @param i the length of the substring to return
     * @return the substring from the current position to the specified length ahead
     */
    public String peekString(int i) {
        return document.substring(index, index + i);
    }

    /**
     * Returns the current character at the index and advances the index by one.
     *
     * @return the current character at the index
     */
    public char getChar() {
        return document.charAt(index++);
    }

    /**
     * Advances the current position within the document by a specified number of characters.
     *
     * @param i the number of characters to advance the position by
     */
    public void swallow(int i) {
        index += i;
    }

    /**
     * Checks if the current position has reached or exceeded the end of the document.
     *
     * @return true if the current position has reached or exceeded the end of the document, false otherwise.
     */
    public boolean isDone() {
        return index >= document.length();
    }

    /**
     * Returns the remaining part of the document from the current position to the end.
     *
     * @return the remaining part of the document from the current position to the end
     */
    public String remainder() {
        return document.substring(index);
    }
}