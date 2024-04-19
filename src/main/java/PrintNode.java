import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrintNode extends StatementNode {

    private List<Node> parameters = new ArrayList<>();

    public PrintNode() {}

    public PrintNode(List<Node> arguments) {
        this.parameters = arguments;
    }

    @Override
    public void accept(StatementVisitor visitor) {}

    public void addNode(Node node) {
        parameters.add(node);
    }

    public List<Node> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return String.format("PrintNode(%s)", parameters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrintNode printNode = (PrintNode) o;
        return Objects.equals(parameters, printNode.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameters);
    }
}