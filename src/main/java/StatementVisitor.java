/**
 * This implements a visitor pattern to do pre-processing before the interpreter executes statements
 * For Assignment statements, it builds a symbol table of all variable names
 * For DATA statements, it stores the parameters in a list
 * For While statements, it adds the labels to the symbol table
 * For LabeledStatements, it maps the labels to code blocks in the symbol table
 * For GoToNodes, it adds the labels to the symbol table
 */
public interface StatementVisitor {
    void visit(LabeledStatementNode labeledStatementNode);
    void visit(DataNode dataNode);
    void visit(WhileNode whileNode);

    StatementNode assignmentStatement(AssignmentNode assignmentNode);
    StatementNode readStatement(ReadNode readNode);
    StatementNode inputStatement(InputNode inputNode);
    StatementNode printStatement(PrintNode printNode);
    StatementNode ifStatement(IfNode ifNode);
    StatementNode goSubStatement(GoSubNode goSubNode);
    StatementNode goToStatement(GoToNode goToNode);
    StatementNode returnStatement(ReturnNode returnNode);
    StatementNode forStatement(ForNode forNode);
    StatementNode whileStatement(WhileNode whileNode);
    StatementNode nextStatement(NextNode nextNode);
    StatementNode labeledStatement(LabeledStatementNode labeledStatementNode);
    StatementNode endStatement(EndNode endNode);
}
