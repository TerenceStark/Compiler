package parser;

public abstract class Stmt extends ASTNode {
    public Stmt(ASTNode parent, String label, ASTNodeTypes astNodeType) {
        super(parent, label, astNodeType);
    }
}
