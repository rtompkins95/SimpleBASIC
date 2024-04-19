import java.util.Objects;

public class AssignmentNode extends StatementNode {

    private final VariableNode variableNode;

    private final Node value;

    public AssignmentNode(VariableNode variableNode, Node value) {
        this.variableNode = variableNode;
        this.value = value;
    }

    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("AssignmentNode(%s = %s)", variableNode, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentNode that = (AssignmentNode) o;
        return Objects.equals(variableNode, that.variableNode) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableNode, value);
    }

    public VariableNode getVariableNode() {
        return variableNode;
    }

    public Node getValue() {
        return value;
    }
}
