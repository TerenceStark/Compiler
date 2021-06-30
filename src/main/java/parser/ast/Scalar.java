package parser.ast;

import lexer.Token;

public class Scalar extends Factor{
    public Scalar(Token token) {
        super(token);
        this.type = ASTNodeTypes.SCALAR;
    }
}
