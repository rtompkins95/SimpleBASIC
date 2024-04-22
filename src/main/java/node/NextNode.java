package node;

public class NextNode extends StatementNode {
    private final VariableNode variable;

    public NextNode(VariableNode variable) {
        this.variable = variable;
    }

    @Override
    public StatementNode interpret(StatementVisitor statementVisitor) {
        return statementVisitor.nextStatement(this);
    }

    public VariableNode getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return String.format("NextNode(variable=%s)", variable);
    }
}