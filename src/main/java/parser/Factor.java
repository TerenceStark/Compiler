package parser;

public abstract class Factor extends ASTNode{
    public Factor(ASTNode parent, String label, ASTNodeTypes astNodeType) {
        super(parent, label, astNodeType);
    }
}
