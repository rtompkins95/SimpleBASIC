import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrintNode extends StatementNode {

    private List<Node> arguments = new ArrayList<>();

    public PrintNode() {}

    public PrintNode(List<Node> arguments) {
        this.arguments = arguments;
    }

    public void addNode(Node node) {
        arguments.add(node);
    }

    public List<Node> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return String.format("PrintNode(%s)", arguments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrintNode printNode = (PrintNode) o;
        return Objects.equals(arguments, printNode.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arguments);
    }

    @Override
    public void accept(StatementVisitor visitor) {
    }
}