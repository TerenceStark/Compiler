package parser.ast;

import parser.common.ParseException;
import parser.common.PeekTokenIterator;

public class Block extends Stmt {

    public Block() {
        super(ASTNodeTypes.BLOCK, "block");
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        var block = new Block();
        it.nextMatch("{");
        ASTNode stmt = null;
        while( (stmt = Stmt.parseStmt(it)) != null) {
            block.addChild(stmt);
        }
        it.nextMatch("}");
        return block;
    }

}
