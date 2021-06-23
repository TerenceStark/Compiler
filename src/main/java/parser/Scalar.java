package parser;

public class Scalar extends Factor {
    public Scalar(ASTNode parent, String label, ASTNodeTypes astNodeType) {
        super(parent, null, ASTNodeTypes.SCALAR);
    }
}
