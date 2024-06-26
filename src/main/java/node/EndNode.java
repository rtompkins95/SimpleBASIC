package node;

public class EndNode extends StatementNode {
    @Override
    public void accept(StatementVisitor visitor) {}
    @Override
    public String toString() {
        return "EndNode()";
    }

    @Override
    public StatementNode interpret(StatementVisitor statementVisitor) {
        return statementVisitor.endStatement(this);
    }
}