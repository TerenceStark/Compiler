package parser.ast;


import lexer.Token;
import lexer.TokenType;
import parser.common.PeekTokenIterator;

public class Factor extends ASTNode {
    public Factor(Token token) {
        super();
        this.lexeme = token;
        this.label = token.get_value();
    }

    public static ASTNode parse(PeekTokenIterator it) {
        var token = it.peek();
        var type = token.get_type();

        if(type == TokenType.VARIABLE) {
            it.next();
            return new Variable(token);
        } else if(token.isScalar()){
            it.next();
            return new Scalar(token);
        }
        return null;
    }
}
