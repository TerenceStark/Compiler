package parser.ast;

import lexer.TokenType;
import parser.common.PeekTokenIterator;

public abstract class Factor extends ASTNode {
    public Factor(ASTNode parent,PeekTokenIterator peekTokenIterator) {
        super(parent);
        var token = peekTokenIterator.next();
        var type = token.get_type();
        if (type == TokenType.VARIABLE) {
            this.astNodeType = ASTNodeTypes.VARIABLE;
        } else {
            this.astNodeType = ASTNodeTypes.SCALAR;
        }
        this.label = token.get_value();
        this.lexeme = token;
    }
}
