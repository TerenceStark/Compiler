package parser;

import parser.ast.*;
import parser.common.ParseException;
import parser.common.PeekTokenIterator;

public class SimpleParser {

    // Expr -> digit + Expr | digit
    // digit -> 0|1|2|3|4|5|...|9
    public static ASTNode parse(PeekTokenIterator iterator) throws ParseException {
        var expr = new Expr();
        var scalar = new Scalar(expr, iterator);
        if (!iterator.hasNext()) {
            return scalar;
        }

        expr.setLexeme(iterator.peek());
        iterator.matchNext("+");
        expr.setLabel("+");
        expr.addChild(scalar);
        expr.setAstNodeType(ASTNodeTypes.BINARY_EXPR);
        var rightNode = parse(iterator);
        expr.addChild(rightNode);
        return expr;
    }
}

