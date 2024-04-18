public class GoSubNode extends StatementNode {
    private final String label;

    public GoSubNode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return String.format("GoSubNode(%s)", label);
    }

    @Override
    public void accept(StatementVisitor visitor) {
    }
}