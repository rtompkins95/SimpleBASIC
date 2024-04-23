package node;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BuiltInFunctions {

    public enum FUNCTION {
        RANDOM,
        RANDOMF,
        LEFT$,
        RIGHT$,
        MID$,
        NUM$,
        VAL,
        VALF,
        POW,
        POWF,
        INT,
        FLOAT
    }

    public static Map<String, FUNCTION> functionMap = new HashMap<>() {{
        put("RANDOM", FUNCTION.RANDOM);
        put("RANDOM%", FUNCTION.RANDOMF);
        put("LEFT$", FUNCTION.LEFT$);
        put("RIGHT$", FUNCTION.RIGHT$);
        put("MID$", FUNCTION.MID$);
        put("NUM$", FUNCTION.NUM$);
        put("VAL", FUNCTION.VAL);
        put("VAL%", FUNCTION.VALF);
        put("POW", FUNCTION.POW);
        put("POW%", FUNCTION.POWF);
        put("INT", FUNCTION.INT);
        put("FLOAT", FUNCTION.FLOAT);
    }};

    private static Random random = new Random();

    // Generates a random integer value between 0 and Integer.MAX_VALUE.
    public static int RANDOM() {
        return random.nextInt() * Integer.MAX_VALUE;
    }

    public static int RANDOM(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static float RANDOMF() {
        return random.nextFloat() * Float.MAX_VALUE;
    }

    public static float RANDOMF(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    // Returns the leftmost n characters of a string.
    public static String LEFT$(String str, int n) {
        return str.substring(0, Math.min(n, str.length()));
    }

    // Returns the rightmost n characters of a string.
    public static String RIGHT$(String str, int n) {
        return str.substring(Math.max(0, str.length() - n));
    }

    // returns the characters of the string, starting from the 2nd argument and taking the 3rd argument as the count
    public static String MID$(String str, int start, int count) {
        return str.substring(start, Math.min(start + count, str.length()));
    }
    // converts a number to a string
    public static String NUM$(Number num) {
        return num.toString();
    }

    // Converts a string to an integer.
    public static int VAL(String str) {
        return Integer.parseInt(str);
    }

    // Converts a string to a float.
    public static float VALF(String str) {
        return Float.parseFloat(str);
    }

    public static int POW(int a, int b) {
        return (int) Math.pow(a, b);
    }

    public static float POWF(float a, float b) {
        return (float) Math.pow(a, b);
    }

    public static int INT(Number n) {
        return n.intValue();
    }

    public static float FLOAT(Number n) {
        return n.floatValue();
    }
}