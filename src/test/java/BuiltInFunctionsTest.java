import node.BuiltInFunctions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuiltInFunctionsTest {

    @Test
    public void testRandom() {
        int result1 = BuiltInFunctions.RANDOM();
        int result2 = BuiltInFunctions.RANDOM(0, 20);
        assertTrue(result2 >= 0 && result2 <= 20);
        // As RANDOM generates a random number, we can only check if it's within the expected range
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

    @Test
    public void testPow() {
        int result = BuiltInFunctions.POW(2, 3);
        assertEquals(8, result);

        int result2 = BuiltInFunctions.POW(5, 0);
        assertEquals(1, result2);

        int result3 = BuiltInFunctions.POW(2, -1);
        assertEquals(0, result3);
    }

    @Test
    public void testPowF() {
        float result = BuiltInFunctions.POWF(2f, 3f);
        assertEquals(8f, result);

        float result2 = BuiltInFunctions.POWF(5, 0);
        assertEquals(1f, result2);

        float result3 = BuiltInFunctions.POWF(2, -1);
        assertEquals(0.5f, result3);
    }

    @Test
    public void _int() {
        int i = BuiltInFunctions.INT(5.2);
        assertEquals(5, i);

        int j = BuiltInFunctions.INT(0.2f);
        assertEquals(0, j);
    }

    @Test
    public void _float() {
        float i = BuiltInFunctions.FLOAT(5);
        assertEquals(5F, i);

        float j = BuiltInFunctions.INT(2);
        assertEquals(2f, j);
    }
}