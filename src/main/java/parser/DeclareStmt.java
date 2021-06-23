package parser;

public class DeclareStmt extends Stmt{
    public DeclareStmt(ASTNode parent, String label, ASTNodeTypes astNodeType) {
        super(parent, "declare", ASTNodeTypes.DECLARE_STMT);
    }
}
