package parser.common;

import lexer.common.PeekIterator;
import lexer.Token;
import lexer.TokenType;

import java.util.stream.Stream;

public class PeekTokenIterator extends PeekIterator<Token> {

    public PeekTokenIterator(Stream<Token> stream) {
        super(stream);
    }

    public Token nextMatch(String value) throws ParseException {
        var token = this.next();
        if (!token.get_value().equals(value)) {
            throw new ParseException(token);
        }
        return token;
    }

    public Token nextMatch(TokenType type) throws ParseException {
        var token = this.next();
        if (!token.get_type().equals(type)) {
            throw new ParseException(token);
        }
        return token;
    }
}
