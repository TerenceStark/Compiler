package parser.common;

import lexer.Token;

public class ParseException extends Exception{
    private String msg;

    public ParseException(String msg) {
        this.msg = msg;
    }

    public ParseException(Token token) {
        this.msg = String.format("Syntax Error, unexpected token %s", token.get_value());
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
