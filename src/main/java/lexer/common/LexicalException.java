package lexer.common;

import lombok.Getter;

@Getter
public class LexicalException extends Exception {

    private String message;

    public LexicalException(String _message) {
        this.message = _message;
    }

    public LexicalException(char c) {
        message = String.format("Unexpected Exception %c", c);
    }
}
