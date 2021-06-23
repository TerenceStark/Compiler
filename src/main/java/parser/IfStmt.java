package parser;

public class IfStmt extends Stmt{
    public IfStmt(ASTNode parent, String label, ASTNodeTypes astNodeType) {
        super(parent, "if", ASTNodeTypes.IF_STMT);
    }
}
