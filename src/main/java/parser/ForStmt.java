package parser;

public class ForStmt extends Stmt{
    public ForStmt(ASTNode parent, String label, ASTNodeTypes astNodeType) {
        super(parent, "for", ASTNodeTypes.FOR_STMT);
    }
}
