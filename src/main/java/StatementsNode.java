import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatementsNode extends Node {

    private List<StatementNode> statements = new ArrayList<>();

    public StatementsNode() {}

    public void addStatement(StatementNode node) {
        statements.add(node);
    }

    public List<StatementNode> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        return String.format("StatementsNode(%s)", statements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatementsNode that = (StatementsNode) o;
        return Objects.equals(statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statements);
    }
}
