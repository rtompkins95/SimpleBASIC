import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// TokenTest: a collection of unit tests to validate the functionalities in the Token class.
class TokenTest {
   // Test toString() for a Token object with a value
   @Test
   void testToStringWithValue() {
      Token token = new Token(Token.TokenType.WORD, "hello", 1, 1);
      String expected = "WORD(hello)";
      assertEquals(expected, token.toString(), "Failed: Token with value toString.");
    }
}