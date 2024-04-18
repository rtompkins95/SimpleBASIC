import java.util.Objects;

public class WhileNode extends StatementNode {
    private final BooleanExpressionNode condition;
    private final String label;

    public WhileNode(BooleanExpressionNode condition, String label) {
        this.condition = condition;
        this.label = label;
    }

    public BooleanExpressionNode getCondition() {
        return condition;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return String.format("WhileNode(%s, %s)", condition, label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhileNode that = (WhileNode) o;
        return this.condition.equals(that.condition) && this.label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, label);
    }

    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}