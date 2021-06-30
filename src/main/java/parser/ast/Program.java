package parser.ast;

import parser.common.ParseException;
import parser.common.PeekTokenIterator;

public class Program extends Block {
    public Program() {
        super();
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        var block = new Program();
        ASTNode stmt = null;
        while( (stmt = Stmt.parseStmt(it)) != null) {
            block.addChild(stmt);
        }
        return block;
    }
}
