package parser;

import lexer.Token;
import lombok.Data;

import java.util.ArrayList;

@Data
public abstract class ASTNode {

    protected ArrayList<ASTNode> children = new ArrayList<>();
    protected ASTNode parent;

    protected Token token;
    protected String label;
    protected ASTNodeTypes astNodeType;

    public void addChild(ASTNode node) {
        node.parent = this;
        children.add(node);
    }

    public ASTNode() {
    }

    public ASTNode(ASTNode parent) {
        this.parent = parent;
    }

    public ASTNode(ASTNode parent, String label, ASTNodeTypes astNodeType) {
        this.parent = parent;
        this.label = label;
        this.astNodeType = astNodeType;
    }
}
