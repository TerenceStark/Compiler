package parser.ast;

import parser.common.ParseException;
import parser.common.PeekTokenIterator;

public class FunctionArgs extends ASTNode {
    public FunctionArgs() {
        super();
        this.label = "args";
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {

        var args = new FunctionArgs();
        while (it.peek().isType()) {
            var type = it.next();
            var variable = (Variable) Factor.parse(it);
            variable.setTypeLexeme(type);
            args.addChild(variable);
            if (!it.peek().get_value().equals(")")) {
                it.nextMatch(",");
            }
        }
        return args;
    }

}
