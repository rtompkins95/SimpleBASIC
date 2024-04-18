public class BooleanExpressionNode extends Node {

    public enum OPERATOR {
        GREATERTHAN,
        GREATERTHANEQUALTO,
        LESSTHAN,
        LESSTHANEQUALTO,
        NOTEQUALS,
        EQUALS
    }

    private final ExpressionNode left;
    private final OPERATOR operator;
    private final ExpressionNode right;

    public BooleanExpressionNode(ExpressionNode left, OPERATOR operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public OPERATOR getOperator() {
        return operator;
    }

    public ExpressionNode getRight() {
        return right;
    }

    @Override
    public String toString() {
        return String.format("BooleanExpressionNode(%s %s %s)", left, operator, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BooleanExpressionNode) {
            BooleanExpressionNode other = (BooleanExpressionNode) obj;
            return left.equals(other.left) && operator.equals(other.operator) && right.equals(other.right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return left.hashCode() + operator.hashCode() + right.hashCode();
    }
}