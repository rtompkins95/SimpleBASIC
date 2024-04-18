
public abstract class StatementNode extends Node {

    private StatementNode next;

    public abstract void accept(StatementVisitor visitor);

    public void setNext(StatementNode curr) {
        this.next = curr;
    }

    public StatementNode getNext() {
        return next;
    }
}
