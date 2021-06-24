package parser.ast;

import parser.common.PeekTokenIterator;

public class Scalar extends Factor {
    public Scalar(ASTNode parent, PeekTokenIterator peekTokenIterator) {
        super(parent, peekTokenIterator);
    }
}
