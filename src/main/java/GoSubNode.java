public class GoSubNode extends StatementNode {
    private final String label;

    public GoSubNode(String label) {
        this.label = label;
    }

    @Override
    public void accept(StatementVisitor visitor) {}

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return String.format("GoSubNode(%s)", label);
    }
}