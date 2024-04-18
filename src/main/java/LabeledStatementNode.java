import java.util.Objects;

public class LabeledStatementNode extends StatementNode {
    private String label;
    private StatementNode statementNode;

    public LabeledStatementNode(String label, StatementNode statementNode) {
        this.label = label;
        this.statementNode = statementNode;
    }

    // Getter method for label
    public String getLabel() {
        return this.label;
    }

    // Getter method for statementNode
    public StatementNode getStatementNode() {
        return this.statementNode;
    }

    @Override
    public String toString() {
        return String.format("LabeledStatementNode(%s, %s)", label, statementNode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabeledStatementNode that = (LabeledStatementNode) o;
        return this.label.equals(that.label) && this.statementNode.equals(that.statementNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, statementNode);
    }

    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}