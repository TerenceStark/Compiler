package lexer;

import Common.AlphabetHelper;
import Common.PeekIterator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {
    TokenType _type;
    String _value;

    public boolean isVariable() {
        return _type == TokenType.VARIABLE;
    }

    private boolean isScalar() {
        return _type == TokenType.INTEGER || _type == TokenType.FLOAT || _type == TokenType.STRING || _type == TokenType.BOOLEAN;
    }

    //make var or keyword
    public static Token makeVarOrKeyword(PeekIterator<Character> iterator) {
        String s = "";

        while (iterator.hasNext()) {
            var lookahead = iterator.peek();
            if (AlphabetHelper.isLiteral(lookahead)) {
                s += lookahead;
            } else {
                break;
            }
            iterator.next();
        }

        if (Keywords.isKeyword(s)) {
            return new Token(TokenType.KEYWORD, s);
        }
        if (s.equals("true") || s.equals("false")) {
            return new Token(TokenType.BOOLEAN, s);
        }

        return new Token(TokenType.VARIABLE, s);
    }

    //String
    public static Token makeString(PeekIterator<Character> iterator) throws LexicalException {
        StringBuilder s = new StringBuilder();
        int state = 0;

        while (iterator.hasNext()) {
            char lookahead = iterator.next();
            switch (state) {
                case 0:
                    if (lookahead == '\'') {
                        state = 1;
                    } else if (lookahead == '"') {
                        state = 2;
                    }
                    s.append(lookahead);
                    break;

                case 1:
                    if (lookahead == '\'') {
                        return new Token(TokenType.STRING, String.valueOf(s.append(lookahead)));
                    } else {
                        s.append(lookahead);
                        break;
                    }

                case 2:
                    if (lookahead == '"') {
                        return new Token(TokenType.STRING, String.valueOf(s.append(lookahead)));
                    } else {
                        s.append(lookahead);
                        break;
                    }
            }
        }
        throw new LexicalException("Unexpected Error");
    }

    //Operator
    // static Pattern patternOperator = Pattern.compile("^[+-\\\\*/><=!&|^%]$");
    public static Token makeOperator(PeekIterator<Character> iterator) throws LexicalException {

        int state = 0;
        while (iterator.hasNext()) {

            char lookahead = iterator.next();
            System.out.println("state:" + state + " lookahead:" + lookahead);
            switch (state) {
                case 0:
                    switch (lookahead) {
                        case '+':
                            state = 1;
                            break;
                        case '-':
                            state = 2;
                            break;
                        case '*':
                            state = 3;
                            break;
                        case '/':
                            state = 4;
                            break;
                        case '>':
                            state = 5;
                            break;
                        case '<':
                            state = 6;
                            break;
                        case '=':
                            state = 7;
                            break;
                        case '!':
                            state = 8;
                            break;
                        case '&':
                            state = 9;
                            break;
                        case '|':
                            state = 10;
                            break;
                        case '^':
                            state = 11;
                            break;
                        case '%':
                            state = 12;
                            break;
                        case ',':
                            return new Token(TokenType.OPERATOR, ",");
                        case ';':
                            return new Token(TokenType.OPERATOR, ";");
                    }
                    break;
                case 1:
                    if (lookahead == '+') {
                        return new Token(TokenType.OPERATOR, "++");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "+=");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "+");
                case 2:
                    if (lookahead == '-') {
                        return new Token(TokenType.OPERATOR, "--");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "-=");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "-");
                case 3:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "*=");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "*");
                case 4:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "/=");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "/");
                case 5:
                    if (lookahead == '>') {
                        return new Token(TokenType.OPERATOR, ">>");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, ">=");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, ">");
                case 6:
                    if (lookahead == '<') {
                        return new Token(TokenType.OPERATOR, "<<");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "<=");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "<");
                case 7:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "==");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "=");
                case 8:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "!=");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "!");
                case 9:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "&=");
                    } else if (lookahead == '&') {
                        return new Token(TokenType.OPERATOR, "&&");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "&");
                case 10:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "|=");
                    } else if (lookahead == '|') {
                        return new Token(TokenType.OPERATOR, "||");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "|");
                case 11:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "^=");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "^");
                case 12:
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "%=");
                    }
                    iterator.putBack();
                    return new Token(TokenType.OPERATOR, "%");
            }
        }
        throw new LexicalException("Unexpected error");
    }
}
