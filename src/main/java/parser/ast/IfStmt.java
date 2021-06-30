package parser.ast;

import parser.common.ParseException;
import parser.common.PeekTokenIterator;

public class IfStmt extends Stmt {
    public IfStmt() {
        super(ASTNodeTypes.IF_STMT, "if");
    }

    public static ASTNode parse(PeekTokenIterator iterator) throws ParseException {
        return parseIF(iterator);
    }

    // IfStmt -> If(Expr) Block Tail
    public static ASTNode parseIF(PeekTokenIterator iterator) throws ParseException {
        var lexeme = iterator.nextMatch("if");
        iterator.nextMatch("(");
        var ifStmt = new IfStmt();
        ifStmt.setLexeme(lexeme);
        var expr = Expr.parse(iterator);
        ifStmt.addChild(expr);
        iterator.nextMatch(")");
        var block = Block.parse(iterator);
        ifStmt.addChild(block);

        var tail = parseTail(iterator);
        if (tail != null) {
            ifStmt.addChild(tail);
        }
        return ifStmt;

    }

    // Tail -> else {Block} | else IFStmt | ε
    public static ASTNode parseTail(PeekTokenIterator iterator) throws ParseException {
        if (!iterator.hasNext() || !iterator.peek().get_value().equals("else")) {
            return null;
        }
        iterator.nextMatch("else");
        var lookahead = iterator.peek();

        if (lookahead.get_value().equals("{")) {
            return Block.parse(iterator);
        } else if (lookahead.get_value().equals("if")) {
            return parseIF(iterator);
        } else {
            return null;
        }

    }

    public ASTNode getExpr() {
        return this.getChild(0);
    }

    public ASTNode getBlock() {
        return this.getChild(1);
    }

    public ASTNode getElseBlock() {

        var block = this.getChild(2);
        if (block instanceof Block) {
            return block;
        }
        return null;
    }

    public ASTNode getElseIfStmt() {
        var ifStmt = this.getChild(2);
        if (ifStmt instanceof IfStmt) {
            return ifStmt;
        }
        return null;
    }

}
