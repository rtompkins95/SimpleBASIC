public class GoToNode extends StatementNode {
    private String label;

    public GoToNode(String label) {
        this.label = label;
    }


    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return String.format("GoToNode(%s)", label);
    }

    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }
}