import java.util.Objects;

public class ForNode extends StatementNode {
    private VariableNode variable;
    private Node initialValue;
    private Node limit;
    private Node increment;

    public ForNode(VariableNode variable, Node initialValue, Node limit, Node increment) {
        this.variable = variable;
        this.initialValue = initialValue;
        this.limit = limit;
        this.increment = increment;
    }

    public VariableNode getVariable() {
        return variable;
    }

    public Node getInitialValue() {
        return initialValue;
    }

    public Node getLimit() {
        return limit;
    }

    public Node getIncrement() {
        return increment;
    }

    @Override
    public String toString() {
        return String.format("ForNode(variable=%s, initialValue=%s, limit=%s, increment=%s)", variable, initialValue, limit, increment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForNode forNode = (ForNode) o;
        return variable.equals(forNode.variable) &&
                initialValue.equals(forNode.initialValue) &&
                limit.equals(forNode.limit) &&
                increment.equals(forNode.increment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, initialValue, limit, increment);
    }

    @Override
    public void accept(StatementVisitor visitor) {
    }
}