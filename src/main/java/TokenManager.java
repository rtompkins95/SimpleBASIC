import java.util.LinkedList;
import java.util.Optional;
public class TokenManager {

    private LinkedList<Token> tokens;
    private int currentTokenIndex;

    public TokenManager(LinkedList<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    /**
     * Returns the token at the specified index relative to the current token index,
     * or an empty Optional if the index is out of range.
     *
     * @param j the relative index of the token to peek
     * @return an Optional containing the token at the specified index, or an empty Optional if the index is out of range
     */
    Optional<Token> peek(int j) {
        int peekIndex = currentTokenIndex + j;
        if (peekIndex < tokens.size()) {
            return Optional.of(tokens.get(peekIndex));
        } else {
            return Optional.empty();
        }
    }


    /**
     * Checks if there are more tokens to be processed.
     *
     * @return true if there are more tokens to be processed, false otherwise.
     */
    boolean moreTokens() {
        return currentTokenIndex < tokens.size();
    }


    /**
     * Matches the first token in the list and removes it if it has the specified token type.
     *
     * @param t the TokenType to match
     * @return an Optional containing the token if it matches and is removed, or an empty Optional otherwise
     */
    Optional<Token> matchAndRemove(Token.TokenType t) {
        if (tokens.isEmpty()) {
            return Optional.empty();
        }

        Token head = tokens.getFirst();
        if (head.getTokenType().equals(t)) {
            tokens.removeFirst();
            return Optional.of(head);
        }
        return Optional.empty();
    }
}
