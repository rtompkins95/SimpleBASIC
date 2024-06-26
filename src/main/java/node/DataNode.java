package node;

import java.util.List;
import java.util.Objects;

public class DataNode extends StatementNode {
    private final List<Node> data;

    public DataNode(List<Node> data) {
        this.data = data;
    }

    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public StatementNode interpret(StatementVisitor statementVisitor) {
        return this.getNext();
    }

    @Override
    public String toString() {
        return String.format("DataNode(%s)", data);
    }

    public List<Node> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataNode dataNode = (DataNode) o;
        return Objects.equals(data, dataNode.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}