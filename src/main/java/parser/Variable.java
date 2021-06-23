package parser;

public class Variable extends Factor{
    public Variable(ASTNode parent, String label, ASTNodeTypes astNodeType) {
        super(parent, null, ASTNodeTypes.VARIABLE);
    }
}
