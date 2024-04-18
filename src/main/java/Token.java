import java.util.Objects;

/**
 * Token class - represents a token for the lexer.
 * A token is a component of the source code that has a specific meaning in the programming language.
 * It consists of a token type, value (if applicable), line number, and position.
 */
// Token class - represents a token for the lexer
public class Token {

    /**
     * Enumerates the types of tokens that can be identified during lexical analysis.
     * Each type represents a distinct category of token found in the source code.
     */
    public enum TokenType {
        WORD, NUMBER, IF, PRINT, READ, INPUT, DATA, GOSUB, GOTO, FOR, TO, STEP, NEXT, RETURN, THEN, FUNCTION, WHILE,
        END, ENDOFLINE, STRINGLITERAL, GREATERTHANEQUALTO, LESSTHANEQUALTO, NOTEQUALS, EQUALS, LESSTHAN, GREATERTHAN,
        LPAREN, RPAREN, PLUS, MINUS, MULTIPLY, DIVIDE, LABEL, COMMA, FUNCTIONNAME
    }
    /**
     * Represents a single token in the source code.
     * Each token has a specific type, line number, and position.
     */
    private final TokenType tokenType; // as defined in TokenType enum.
    /**
     * Represents a token in the source code.
     */
    private String val; // Value of the token. It can be null for tokens where value is not applicable, like ENDOFLINE.
    /**
     * Represents line number of token.
     */
    private final int lineNo; // Line number in the source code where the token is located.
    /**
     * Represents the position within its line of a token, as an index.
     * The position indicates the location of the token within a line of source code.
     */
    private final int position; // Position within its line, as an index.

    /**
     * Represents a token without value in the source code.
     */
    public Token(TokenType tokenType, int lineNo, int position) {
        this.tokenType = tokenType;
        this.lineNo = lineNo;
        this.position = position;
    }

    /**
     * Overloaded constructor to create a Token instance with specific value.
     * This will be used in the case where our token carries a value, like identifiers or literals.
     */
    public Token(TokenType tokenType, String val, int lineNo, int position) {
        this(tokenType, lineNo, position);
        this.val = val;
    }

    /** Returns a string representation of the Token object.
    * The format is "TokenType" or "TokenType(Value)" if there is a value inside the token.
    */
    @Override
    public String toString() {
        if (val != null) {
            return String.format("%s(%s)", tokenType, val);
        } else {
            return String.format("%s(No Value)", tokenType);
        }
    }

    /**
     * Verbose output for testing
     * @return
     */
    public String toString2Debug() {
        if (val != null) {
            return String.format("%s(%s) Line: %d Position: %d", tokenType, val, lineNo, position);
        } else {
            return String.format("%s Line: %d Position: %d", tokenType, lineNo, position);
        }
    }

    /**
     * Compares this Token object with the specified object for equality.
     *
     * @param o the object to be compared for equality with this Token
     * @return {@code true} if the specified object is equal to this Token, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return lineNo == token.lineNo &&
                position == token.position &&
                tokenType == token.tokenType &&
                Objects.equals(val, token.val);
    }

    /**
     * Overrides the hashCode method from the Object class.
     * Returns a hash code value for the Token object.
     *
     * @return the hash code value for the Token object
     */
    @Override
    public int hashCode() {
        return Objects.hash(tokenType, val, lineNo, position);
    }

    // Getter for TokenType
    // Return the token's type
    public TokenType getTokenType() {
        return this.tokenType;
    }
    // Getter for position, returns tokens position
    public int getPosition() {
        return this.position;
    }
    // Getter for line number, returns tokens line number
    public int getLineNo() {
        return this.lineNo;
    }

    public String getVal() {
        return this.val;
    }

}


