public class ReturnNode extends StatementNode {
    @Override
    public String toString() {
        return "ReturnNode";
    }

    @Override
    public void accept(StatementVisitor visitor) {}
}