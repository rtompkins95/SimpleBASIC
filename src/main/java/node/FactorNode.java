package node;

import java.util.Objects;

public class FactorNode extends Node {

    private final Node node;

    public FactorNode(Node node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return String.format("FactorNode: {%s}", node);
    }

    public Node getNode() {
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactorNode that = (FactorNode) o;
        return Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }
}