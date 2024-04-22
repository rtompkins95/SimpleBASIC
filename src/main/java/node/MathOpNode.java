package node;

import java.util.Objects;

public class MathOpNode extends Node {
    public enum OPERATION {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    private final Node left;
    private final Node right;

    private final OPERATION operation;

    public MathOpNode(OPERATION operator, Node left, Node right) {
        this.operation = operator;
        this.left = left;
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public OPERATION getOperator() {
        return operation;
    }

    @Override
    public String toString() {
        return String.format("MathOpNode(%s %s %s)", left, operation, right);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MathOpNode that = (MathOpNode) o;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right) && operation == that.operation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, operation);
    }
}