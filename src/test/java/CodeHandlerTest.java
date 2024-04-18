import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// CodeHandlerTest class contains unit tests for testing various functionalities of the CodeHandler class.
public class CodeHandlerTest {

    // Tests the peek method in CodeHandler. Verifies if the method correctly returns the character
    // at the specified index from the beginning of the file content.
    @Test
    public void testPeek() throws IOException {
        Path path = Files.createTempFile("test.txt", ".txt");
        Files.writeString(path, "Hello, World!");

        CodeHandler handler = new CodeHandler(path.toString());
        assertEquals('H', handler.peek(0), "Peek at position 0 should return 'H'");
        assertEquals('e', handler.peek(1), "Peek at position 1 should return 'e'");

        Files.delete(path);
    }

    // Tests the peekString method in CodeHandler. Verifies if the method correctly returns a substring
    // of a specified length from the beginning of the file content.
    @Test
    public void testPeekString() throws IOException {
        Path path = Files.createTempFile("test.txt", ".txt");
        Files.writeString(path, "Hello, World!");

        CodeHandler handler = new CodeHandler(path.toString());
        assertEquals("Hello", handler.peekString(5), "PeekString with length 5 should return 'Hello'");

        Files.delete(path);
    }

    // Tests the getChar method in CodeHandler. Verifies if the method correctly returns the current
    // character and then advances the index.
    @Test
    public void testGetChar() throws IOException {
        Path path = Files.createTempFile("test.txt", ".txt");
        Files.writeString(path, "Hello, World!");

        CodeHandler handler = new CodeHandler(path.toString());
        assertEquals('H', handler.getChar(), "First call of getChar should return 'H'");
        assertEquals('e', handler.getChar(), "Second call of getChar should return 'e'");

        Files.delete(path);
    }

    // Tests the swallow method in CodeHandler. Verifies if the method correctly advances the index
    // by the specified number of characters.
    @Test
    public void testSwallow() throws IOException {
        Path path = Files.createTempFile("test.txt", ".txt");
        Files.writeString(path, "Hello, World!");

        CodeHandler handler = new CodeHandler(path.toString());
        handler.swallow(7);
        assertEquals('W', handler.peek(0), "After swallowing 7 characters, the next character should be 'W'");

        Files.delete(path);
    }

    // Tests the isDone method in CodeHandler. Verifies if the method correctly identifies when the end
    // of the file content has been reached.
    @Test
    public void testIsDone() throws IOException {
        Path path = Files.createTempFile("test.txt", ".txt");
        Files.writeString(path, "Hello, World!");

        CodeHandler handler = new CodeHandler(path.toString());
        handler.swallow(13);
        assertTrue(handler.isDone(), "isDone should return true after processing all characters");

        Files.delete(path);
    }

    // Tests the remainder method in CodeHandler. Verifies if the method correctly returns the unprocessed
    // portion of the file content.
    @Test
    public void testRemainder() throws IOException {
        Path path = Files.createTempFile("test.txt", ".txt");
        Files.writeString(path, "Hello, World!");

        CodeHandler handler = new CodeHandler(path.toString());
        handler.swallow(7);
        assertEquals("World!", handler.remainder(), "Remainder after swallowing 7 characters should be 'World!'");

        Files.delete(path);
    }
}