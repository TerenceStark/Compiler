package lexer;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Token {
    TokenType _type;
    String _value;

    public boolean isVariable() {
        return _type == TokenType.VARIABLE;
    }

    private boolean isScala() {
        return _type == TokenType.INTEGER || _type == TokenType.FLOAT || _type == TokenType.STRING || _type == TokenType.BOOLEAN;
    }
}
