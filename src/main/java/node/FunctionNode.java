package node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionNode extends Node {
    private final BuiltInFunctions.FUNCTION functionName;
    private List<Node> parameters;

    public FunctionNode(String functionName) {
        this.functionName = BuiltInFunctions.functionMap.get(functionName);
        this.parameters = new ArrayList<>();
    }

    public FunctionNode(BuiltInFunctions.FUNCTION functionName) {
        this.functionName = functionName;
        this.parameters = new ArrayList<>();
    }

    public void setParameters(List<Node> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return String.format("FunctionNode(%s, %s)", functionName, parameters);
    }

    public BuiltInFunctions.FUNCTION getFunctionName() {
        return functionName;
    }

    public List<Node> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionNode that = (FunctionNode) o;
        return Objects.equals(functionName, that.functionName) &&
                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName, parameters);
    }
}
