public class ReturnNode extends StatementNode {
    @Override
    public void accept(StatementVisitor visitor) {}
    @Override
    public String toString() {
        return "ReturnNode";
    }
}