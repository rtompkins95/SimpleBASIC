public class NextNode extends StatementNode {
    private final VariableNode variable;

    public NextNode(VariableNode variable) {
        this.variable = variable;
    }

    @Override
    public void accept(StatementVisitor visitor) {}

    public VariableNode getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return String.format("NextNode(variable=%s)", variable);
    }
}