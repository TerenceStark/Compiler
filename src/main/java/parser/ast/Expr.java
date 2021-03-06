package parser.ast;

import lexer.Token;
import parser.common.ExprHOF;
import parser.common.ParseException;
import parser.common.PeekTokenIterator;
import parser.common.PriorityTable;

import java.util.Objects;

public class Expr extends ASTNode {

    private static PriorityTable table = new PriorityTable();

    public Expr() {
        super();
    }

    public Expr(ASTNodeTypes type, Token lexeme) {
        super();
        this.type = type;
        this.label = lexeme.get_value();
        this.lexeme = lexeme;
    }

    // left:E(k) -> E(k) op(k) E(k+1) | E(k+1)
    // right:
    //    E(k) -> E(k+1) E_(k)
    //       var e = new Expr(); e.left = E(k+1); e.op = op(k); e.right = E(k+1) E_(k)
    //    E_(k) -> op(k) E(k+1) E_(k) | ε
    // 最高优先级处理:
    //    E(t) -> F E_(k) | U E_(k)
    //    E_(t) -> op(t) E(t) E_(t) | ε

    private static ASTNode E(int k, PeekTokenIterator it) throws ParseException {
        if (k < table.size() - 1) {
            return combine(it, () -> E(k + 1, it), () -> E_(k, it));
        } else {
            return race(
                    it,
                    () -> combine(it, () -> F(it), () -> E_(k, it)),
                    () -> combine(it, () -> U(it), () -> E_(k, it))
            );
        }
    }

    //E_(k) -> op(k) E(k+1) E_(k) | ε
    private static ASTNode E_(int k, PeekTokenIterator it) throws ParseException {
        var token = it.peek();
        var value = token.get_value();

        if (table.get(k).contains(value)) {
            var expr = new Expr(ASTNodeTypes.BINARY_EXPR, it.nextMatch(value));
            expr.addChild(Objects.requireNonNull(combine(it,
                    () -> E(k + 1, it),
                    () -> E_(k, it)
            )));
            return expr;
        }
        return null;
    }

    //    E(t) -> F E_(k) | U E_(k)  最高优先级处理
    private static ASTNode U(PeekTokenIterator it) throws ParseException {
        var token = it.peek();
        var value = token.get_value();

        if (value.equals("(")) {
            it.nextMatch("(");
            var expr = E(0, it);
            it.nextMatch(")");
            return expr;
        } else if (value.equals("++") || value.equals("--") || value.equals("!")) {
            var t = it.peek();
            it.nextMatch(value);
            Expr unaryExpr = new Expr(ASTNodeTypes.UNARY_EXPR, t);
            unaryExpr.addChild(E(0, it));
            return unaryExpr;
        }
        return null;
    }

    //    E(t) -> F E_(k) | U E_(k)  最高优先级处理
    private static ASTNode F(PeekTokenIterator it) throws ParseException {
        var factor = Factor.parse(it);
        if (factor == null) {
            return null;
        }
        if (it.hasNext() && it.peek().get_value().equals("(")) {
            return CallExpr.parse(factor, it);
        }
        return factor;
    }

    private static ASTNode combine(PeekTokenIterator it, ExprHOF aFunc, ExprHOF bFunc) throws ParseException {
        var a = aFunc.hoc();
        if (a == null) {
            return it.hasNext() ? bFunc.hoc() : null;
        }
        var b = it.hasNext() ? bFunc.hoc() : null;
        if (b == null) {
            return a;
        }

        Expr expr = new Expr(ASTNodeTypes.BINARY_EXPR, b.lexeme);
        expr.addChild(a);
        expr.addChild(b.getChild(0));
        return expr;

    }

    private static ASTNode race(PeekTokenIterator it, ExprHOF aFunc, ExprHOF bFunc) throws ParseException {
        if (!it.hasNext()) {
            return null;
        }
        var a = aFunc.hoc();
        if (a != null) {
            return a;
        }
        return bFunc.hoc(); //a == null
    }


    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        return E(0, it);
    }
}
