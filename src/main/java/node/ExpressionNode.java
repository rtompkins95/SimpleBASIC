package node;

import java.util.Objects;

public class ExpressionNode extends Node {

    private final Node node;

    public Node getNode() {
        return node;
    }

    public ExpressionNode(Node node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return String.format("ExpressionNode: {%s}", node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionNode that = (ExpressionNode) o;
        return Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }
}