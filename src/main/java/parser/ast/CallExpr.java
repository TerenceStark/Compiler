package parser.ast;

import parser.common.ParseException;
import parser.common.PeekTokenIterator;

public class CallExpr extends Expr {
    public CallExpr() {
        super();
        this.label = "call";
        this.type = ASTNodeTypes.CALL_EXPR;

    }

    public static ASTNode parse(ASTNode factor, PeekTokenIterator it) throws ParseException {
        var expr = new CallExpr();
        expr.addChild(factor);

        it.nextMatch("(");
        ASTNode p = null;
        while((p = Expr.parse(it)) != null) {
            expr.addChild(p);
            if(!it.peek().get_value().equals(")")) {
                it.nextMatch(",");
            }
        }
        it.nextMatch(")");
        return expr;
    }
}
