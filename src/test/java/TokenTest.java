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
   
//   // Test toString() for a Token object without a value
//   @Test
//   void testToStringWithoutValue() {
//      Token token = new Token(Token.TokenType.ENDOFLINE, 2, 20);
//      String expected = "ENDOFLINE";
//      assertEquals(expected, token.toString(), "Failed: Token without value toString.");
//   }
}