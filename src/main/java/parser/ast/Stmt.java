package parser.ast;

import parser.common.ParseException;
import parser.common.PeekTokenIterator;

/**
 * 语句
 */
public abstract class Stmt extends ASTNode {
    public Stmt(ASTNodeTypes _type, String _label) {
        super(_type, _label);
    }

    public static ASTNode parseStmt(PeekTokenIterator it) throws ParseException {
        if (!it.hasNext()) {
            return null;
        }
        var token = it.next();
        var lookahead = it.peek();
        it.putBack();

        if (token.isVariable() && lookahead != null && lookahead.get_value().equals("=")) {
            return AssignStmt.parse(it);
        } else if (token.get_value().equals("var")) {
            return DeclareStmt.parse(it);
        } else if (token.get_value().equals("func")) {
            return FunctionDeclareStmt.parse(it);
        } else if (token.get_value().equals("return")) {
            return ReturnStmt.parse(it);
        } else if (token.get_value().equals("if")) {
            return IfStmt.parse(it);
        } else if (token.get_value().equals("{")) {
            return Block.parse(it);
        } else {
            return Expr.parse(it);
        }
    }

}
