package parser.ast;

import lexer.Token;
import parser.common.ExprHOF;
import parser.common.ParseException;
import parser.common.PeekTokenIterator;
import parser.common.PriorityTable;

import javax.swing.*;
import java.util.Objects;

public class Expr extends ASTNode {
    public Expr() {
    }

    private static PriorityTable table = new PriorityTable();

    public Expr(ASTNode parent) {
        super(parent);
    }

    public Expr(ASTNode parent, Token lexeme, ASTNodeTypes astNodeTypes) {
        super(parent, lexeme, astNodeTypes);
    }

    // left:E(k) -> E(k) op(k) E(k+1) | E(k+1)
    // right:
    //    E(k) -> E(k+1) E_(k)
    //       var e = new Expr(); e.left = E(k+1); e.op = op(k); e.right = E(k+1) E_(k)
    //    E_(k) -> op(k) E(k+1) E_(k) | ε
    // 最高优先级处理:
    //    E(t) -> F E_(k) | U E_(k)
    //    E_(t) -> op(t) E(t) E_(t) | ε
    private static ASTNode E(ASTNode parent, int k, PeekTokenIterator iterator) throws ParseException {
        if (k < table.size() - 1)
            return combine(parent, iterator, () -> E(parent, k + 1, iterator), () -> E_(parent, k, iterator));
        else
            return race(
                    iterator,
                    () -> combine(parent, iterator, () -> U(parent, iterator), () -> E_(parent, k, iterator)),
                    () -> combine(parent, iterator, () -> F(parent, iterator), () -> E_(parent, k, iterator))
            );
    }

    private static ASTNode E_(ASTNode parent, int k, PeekTokenIterator iterator) throws ParseException {
        var token = iterator.peek();
        var value = token.get_value();

        if (table.get(k).contains(value)) {
            Expr expr = new Expr(parent, iterator.matchNext(value), ASTNodeTypes.BINARY_EXPR);
            expr.addChild(Objects.requireNonNull(combine(parent, iterator,
                    () -> E(parent, k + 1, iterator),
                    () -> E_(parent, k, iterator)
                    ))
            );
        }
        return null;
    }

    private static ASTNode U(ASTNode parent, PeekTokenIterator iterator) throws ParseException {
        var token = iterator.peek();
        var value = token.get_value();
        ASTNode expr = null;
        if (value.equals("(")) {
            iterator.matchNext("(");
            expr = E(parent, 0, iterator);
            iterator.matchNext(")");
            return expr;
        } else if (value.equals("++") || value.equals("--") || value.equals("!")) {
            var t = iterator.peek();
            iterator.matchNext(value);
            Expr unaryExpr = new Expr(parent, t, ASTNodeTypes.UNARY_EXPR);
            unaryExpr.addChild(E(unaryExpr, 0, iterator));
            return unaryExpr;
        }
        return null;
    }

    private static ASTNode F(ASTNode parent, PeekTokenIterator iterator) {
        var token = iterator.peek();
        if (token.isVariable()) {
            return new Variable(parent, iterator);
        } else {
            return new Scalar(parent, iterator);
        }
    }


    private static ASTNode combine(ASTNode parent, PeekTokenIterator iterator, ExprHOF aFunc, ExprHOF bFunc) throws ParseException {
        var a = aFunc.hoc();
        if (a == null) {
            return iterator.hasNext() ? bFunc.hoc() : null;
        }
        var b = bFunc.hoc();
        if (b == null) {
            return iterator.hasNext() ? aFunc.hoc() : null;
        }

        Expr expr = new Expr(parent, b.lexeme, ASTNodeTypes.BINARY_EXPR);
        //expr.astNodeType = ASTNodeTypes.BINARY_EXPR;
        //expr.lexeme = b.lexeme;
        //expr.label = b.label;
        expr.addChild(a);
        expr.addChild(b.getChild(0));
        return expr;
    }

    private static ASTNode race(PeekTokenIterator iterator, ExprHOF aFunc, ExprHOF bFunc) throws ParseException {
        if (!iterator.hasNext()) return null;
        var a = aFunc.hoc();
        if (a != null) return a;
        else return (bFunc.hoc());
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        return E(null, 0, it);
    }

}
