public class GoToNode extends StatementNode {
    private final String label;

    public GoToNode(String label) {
        this.label = label;
    }

    @Override
    public StatementNode interpret(StatementVisitor statementVisitor) {
        return statementVisitor.goToStatement(this);
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return String.format("GoToNode(%s)", label);
    }
}