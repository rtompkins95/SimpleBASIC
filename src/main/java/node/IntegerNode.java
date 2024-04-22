package node;

import java.util.Objects;

public class IntegerNode extends Node {

    private final int value;

    public IntegerNode(int value) {
        this.value = value;
    }

    public int getInt() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.format("IntegerNode(%d)", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerNode that = (IntegerNode) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
