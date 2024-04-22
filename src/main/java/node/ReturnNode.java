package node;

public class ReturnNode extends StatementNode {
    @Override
    public StatementNode interpret(StatementVisitor statementVisitor) {
        return statementVisitor.returnStatement(this);
    }
    @Override
    public String toString() {
        return "ReturnNode";
    }
}