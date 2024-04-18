public class BuiltInFunctions {


    // Generates a random integer value between 0 and Integer.MAX_VALUE.
    public static int RANDOM() {
        return (int) (Math.random() * Integer.MAX_VALUE);
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
}