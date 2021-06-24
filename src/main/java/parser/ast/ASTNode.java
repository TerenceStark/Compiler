package parser.ast;

import lexer.Token;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

@Data
public abstract class ASTNode {

    /* 树 */
    protected ArrayList<ASTNode> children = new ArrayList<>();
    protected ASTNode parent;

    /* 关键信息 */
    protected Token lexeme;// 词法单元
    protected String label;// 备注(标签)
    protected ASTNodeTypes astNodeType;// 类型

    public ASTNode(ASTNode parent, Token lexeme, ASTNodeTypes binaryExpr) {
        this.parent = parent;
        this.lexeme = lexeme;
        this.label = lexeme.get_value();
        this.astNodeType = binaryExpr;
    }

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

    public ASTNode getChild(int index) {
        if (index >= this.children.size()) {
            return null;
        }
        return this.children.get(index);
    }

    public void print(int indent) {
        System.out.println(StringUtils.leftPad(" ", indent * 2) + label);
        for (var child : children) {
            child.print(indent + 1);
        }
    }
}
