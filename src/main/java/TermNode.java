import java.util.Objects;


public class TermNode extends Node {
    private final Node node;

    public TermNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return this.node;
    }

    @Override
    public String toString() {
        return String.format("TermNode: {%s}", this.node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermNode termNode = (TermNode) o;
        return Objects.equals(node, termNode.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }
}