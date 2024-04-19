import java.util.*;

/**
 * This implements a visitor pattern to do pre-processing before the interpreter executes statements
 * For Assignment statements, it builds a symbol table of all variable names
 * For DATA statements, it stores the parameters in a list
 * For While statements, it adds the labels to the symbol table
 * For LabeledStatements, it maps the labels to code blocks in the symbol table
 * For GoToNodes, it adds the labels to the symbol table
 */
public class StatementVisitorImpl implements StatementVisitor {

    private final Queue<Node> dataNodes = new LinkedList<>();

    private final Map<String, Integer> intVariables = new HashMap<>();
    private final Map<String, Float> floatVariables = new HashMap<>();
    private final Map<String, String> stringVariables = new HashMap<>();

    private final Map<String, LabeledStatementNode> labels = new HashMap<>();

    private final Set<String> whileLabels = new HashSet<>();

    private final Set<String> goToLabels = new HashSet<>();

    public Map<String, Integer> getIntVariables() {
        return intVariables;
    }

    public Map<String, Float> getFloatVariables() {
        return floatVariables;
    }

    public Map<String, String> getStringVariables() {
        return stringVariables;
    }

    public Queue<Node> getDataNodes() {
        return dataNodes;
    }

    public Map<String, LabeledStatementNode> getLabels() {
        return labels;
    }

    public Set<String> getWhileLabels() {
        return whileLabels;
    }

    public void visit(LabeledStatementNode labeledStatementNode) {
        labels.put(labeledStatementNode.getLabel(), labeledStatementNode);
    }

    public void visit(AssignmentNode assignmentNode) {}

    public void visit(DataNode dataNode){
        this.dataNodes.addAll(dataNode.getData());
    }

    public void visit(WhileNode whileNode){
        this.whileLabels.add(whileNode.getLabel());
    }

    public void visit(GoToNode goToNode) {
        goToLabels.add(goToNode.getLabel());
    }
}
