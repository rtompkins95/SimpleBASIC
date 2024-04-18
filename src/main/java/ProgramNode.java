import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProgramNode extends Node {

    private final List<Node> expressions;
    private StatementsNode statementsNode;

    public ProgramNode() {
        this.expressions = new ArrayList<>();
        this.statementsNode = new StatementsNode();
    }

    public void addExpression(Node expression) {
        expressions.add(expression);
    }

    public void addStatements(StatementsNode statementsNode) {
        this.statementsNode = statementsNode;
    }

    public List<Node> getExpressions() {
        return expressions;
    }

    public List<StatementNode> getStatements() {
        return this.statementsNode.getStatements();
    }

    @Override
    public String toString() {
        if (statementsNode != null && !statementsNode.getStatements().isEmpty()) {
            return String.format("ProgramNode: {%s}", statementsNode);
        }
        return "ProgramNode: {" +
                expressions.stream()
                        .map(Node::toString)
                        .collect(Collectors.joining(", ")) +
                "}";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramNode that = (ProgramNode) o;
        return Objects.equals(expressions, that.expressions) &&
                Objects.equals(statementsNode, that.statementsNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressions);
    }
}