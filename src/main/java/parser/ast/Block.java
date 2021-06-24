package parser.ast;

public class Block extends Stmt {
    public Block(ASTNode parent) {
        super(parent, "block", ASTNodeTypes.BLOCK);
    }
}
