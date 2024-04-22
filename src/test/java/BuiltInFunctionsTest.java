import node.BuiltInFunctions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuiltInFunctionsTest {

    @Test
    public void testRandom() {
        int result = BuiltInFunctions.RANDOM();
        // As RANDOM generates a random number, we can only check if it's within the expected range
        assertTrue(result >= 0 && result <= Integer.MAX_VALUE);
    }

    @Test
    public void testLeft$() {
        String result = BuiltInFunctions.LEFT$("Hello, World!", 5);
        assertEquals("Hello", result);
    }

    @Test
    public void testRight$() {
        String result = BuiltInFunctions.RIGHT$("Hello, World!", 6);
        assertEquals("World!", result);
    }

    @Test
    public void testMid$() {
        String result = BuiltInFunctions.MID$("Hello, World!", 7, 5);
        assertEquals("World", result);
    }

    @Test
    public void testNum$() {
        String result = BuiltInFunctions.NUM$(123);
        assertEquals("123", result);
    }

    @Test
    public void testVal() {
        int result = BuiltInFunctions.VAL("123");
        assertEquals(123, result);
    }

    @Test
    public void testVal$() {
        float result = BuiltInFunctions.VALF("123.45");
        assertEquals(123.45f, result);
    }
}