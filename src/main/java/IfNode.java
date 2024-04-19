public class IfNode extends StatementNode {
    private final BooleanExpressionNode condition;
    private final String label;

    public IfNode(BooleanExpressionNode condition, String label) {
        this.condition = condition;
        this.label = label;
    }

    @Override
    public void accept(StatementVisitor visitor) {}

    public BooleanExpressionNode getCondition() {
        return condition;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return String.format("IfNode(%s, %s)", condition, label);
    }
}