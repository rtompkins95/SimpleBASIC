import java.util.List;
import java.util.Objects;

public class InputNode extends StatementNode {

    private StringNode promptNode;
    private final List<VariableNode> variables;

    public InputNode(StringNode promptNode, List<VariableNode> variables) {
        this.promptNode = promptNode;
        this.variables = variables;
    }

    @Override
    public void accept(StatementVisitor visitor) {}

    public StringNode getPrompt() {
        return this.promptNode;
    }

    public List<VariableNode> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return String.format("InputNode(%s)", variables);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputNode inputNode = (InputNode) o;
        return Objects.equals(variables, inputNode.variables) &&
                Objects.equals(promptNode, inputNode.promptNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }
}