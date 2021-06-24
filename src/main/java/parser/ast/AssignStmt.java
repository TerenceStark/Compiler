package parser.ast;

public class AssignStmt extends Stmt{
    public AssignStmt(ASTNode parent, String label, ASTNodeTypes astNodeType) {
        super(parent, "assign",ASTNodeTypes.ASSIGN_STMT);
    }
}
