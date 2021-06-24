package parser.ast;

public class FunctionDefineStmt extends Stmt{
    public FunctionDefineStmt(ASTNode parent, String label, ASTNodeTypes astNodeType) {
        super(parent, "func", ASTNodeTypes.FUNCTION_DEFINE_STMT);
    }
}
