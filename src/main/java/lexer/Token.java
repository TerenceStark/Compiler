package lexer;

import lexer.Common.AlphabetHelper;
import lexer.Common.LexicalException;
import lexer.Common.PeekIterator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {
    TokenType _type;
    String _value;

    public boolean isVariable() {
        return this._type == TokenType.VARIABLE;
    }

    public boolean isScalar() {
        return this._type == TokenType.INTEGER || this._type == TokenType.FLOAT || this._type == TokenType.STRING || this._type == TokenType.BOOLEAN;
    }

    public boolean isNumber() {
        return this._type == TokenType.INTEGER || this._type == TokenType.FLOAT;
    }

    public boolean isOperator() {
        return this._type == TokenType.OPERATOR;
    }

    public boolean isType() {
        return this._value.equals("bool")
                || this._value.equals("int")
                || this._value.equals("float")
                || this._value.equals("void")
                || this._value.equals("string");

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
            //System.out.println("state:" + state + " lookahead:" + lookahead);
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


    //Number
    public static Token makeNumber(PeekIterator<Character> iterator) throws LexicalException {
        int state = 0;
        StringBuilder s = new StringBuilder();

        while (iterator.hasNext()) {
            char lookahead = iterator.peek();
            //System.out.println("lookahead:" + lookahead + " state:" + state);
            switch (state) {
                case 0:
                    if (lookahead == '0') state = 1;
                    else if (AlphabetHelper.isNumber(lookahead)) state = 2;
                    else if (lookahead == '-' || lookahead == '+') state = 3;
                    else if (lookahead == '.') state = 5;
                    break;
                case 1:
                    if (lookahead == '0') state = 1;
                    else if (AlphabetHelper.isNumber(lookahead)) state = 2;
                    else if (lookahead == '.') state = 4;
                    else return new Token(TokenType.INTEGER, String.valueOf(s));
                    break;
                case 2:
                    if (AlphabetHelper.isNumber(lookahead)) state = 2;
                    else if (lookahead == '.') state = 4;
                    else return new Token(TokenType.INTEGER, String.valueOf(s));
                    break;
                case 3:
                    if (AlphabetHelper.isNumber(lookahead)) state = 2;
                    else if (lookahead == '.') state = 5;
                    break;
                case 4:
                    if (lookahead == '.') throw new LexicalException(lookahead);
                    else if (AlphabetHelper.isNumber(lookahead)) state = 6;
                    else return new Token(TokenType.FLOAT, String.valueOf(s));
                    break;
                case 5:
                    if (AlphabetHelper.isNumber(lookahead)) state = 6;
                    else throw new LexicalException(lookahead);
                    break;
                case 6:
                    if (AlphabetHelper.isNumber(lookahead)) state = 6;
                    else if (lookahead == '.') throw new LexicalException(lookahead);
                    else return new Token(TokenType.FLOAT, String.valueOf(s));
            }//end of switch
            iterator.next();
            s.append(lookahead);
        }//end of while
        throw new LexicalException("Unexpected Error");
    }

}
