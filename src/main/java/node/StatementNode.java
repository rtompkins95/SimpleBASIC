package node;

public abstract class StatementNode extends Node {

    private StatementNode next;

    public void accept(StatementVisitor visitor) {}

    public abstract StatementNode interpret(StatementVisitor visit);

    public void setNext(StatementNode curr) {
        this.next = curr;
    }

    public StatementNode getNext() {
        return next;
    }
}
