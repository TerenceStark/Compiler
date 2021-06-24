package parser.ast;

import parser.common.PeekTokenIterator;

public class Variable extends Factor{
    public Variable(ASTNode parent, PeekTokenIterator peekTokenIterator) {
        super(parent, peekTokenIterator);
    }
}
