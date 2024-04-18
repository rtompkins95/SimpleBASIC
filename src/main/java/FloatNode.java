import java.util.Objects;

public class FloatNode extends Node {

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private final float value;

    public FloatNode(float value) {
        this.value = value;
    }

    public float getFloat() {
        return this.value;
    }
    @Override
    public String toString() {
        return String.format("FloatNode(%f)", value);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatNode floatNode = (FloatNode) o;
        return Float.compare(value, floatNode.value) == 0;
    }
}
