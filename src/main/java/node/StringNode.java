package node;

import java.util.Objects;

public class StringNode extends Node {

    private final String value;

    public StringNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("StringNode(%s)", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringNode stringNode = (StringNode) o;
        return this.value.equals(stringNode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
