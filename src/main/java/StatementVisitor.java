public interface StatementVisitor {
    void visit(LabeledStatementNode labeledStatementNode);
    void visit(AssignmentNode assignmentNode);
    void visit(DataNode dataNode);
    void visit(WhileNode whileNode);
    void visit(GoToNode goToNode);
}
