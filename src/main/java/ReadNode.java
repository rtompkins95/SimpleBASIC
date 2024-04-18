import java.util.List;
import java.util.Objects;

public class ReadNode extends StatementNode {
    private final List<VariableNode> variables;

    public ReadNode(List<VariableNode> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return String.format("ReadNode(%s)", variables);
    }

    public List<VariableNode> getVariables() {
        return variables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReadNode readNode = (ReadNode) o;
        return Objects.equals(variables, readNode.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }

    @Override
    public void accept(StatementVisitor visitor) {
    }
}