package lexer;

import lexer.Common.AlphabetHelper;
import lexer.Common.LexicalException;
import lexer.Common.PeekIterator;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Lexer {
    public ArrayList<Token> analyse(Stream source) throws LexicalException {
        var tokens = new ArrayList<Token>();
        var iterator = new PeekIterator<Character>(source, (char) 0);//(char)0 end_mark

        while (iterator.hasNext()) {
            char c = iterator.next();

            if (c == 0) {
                break;
            }

            char lookahead = iterator.peek();//must after if(c==0){break;}
            //System.out.println("c:"+c+"lookahead:"+lookahead);
            if (c == ' ' || c == '\n') {
                continue;
            }//unnecessary action

            //delete annotation
            if (c == '/') {
                if (lookahead == '/') {
                    while (iterator.hasNext() && (c = iterator.next()) != '\n') {
                    }
                    ;
                    continue;
                } else if (lookahead == '*') {
                    iterator.next();
                    boolean valid = false;
                    while (iterator.hasNext()) {
                        char p = iterator.next();
                        if (p == '*' && iterator.peek() == '/') {
                            iterator.next();
                            valid = true;
                            break;
                        }
                    }
                    if (!valid) {
                        throw new LexicalException("comments not match");
                    }
                    continue;
                }
            }

            if (c == '(' || c == ')' || c == '{' || c == '}') {
                tokens.add(new Token(TokenType.BRACKET, c + ""));
                continue;
            }

            if (c == '"' || c == '\'') {
                iterator.putBack();
                tokens.add(Token.makeString(iterator));
                continue;
            }

            if (AlphabetHelper.isLetter(c)) {
                iterator.putBack();
                tokens.add(Token.makeVarOrKeyword(iterator));
                continue;
            }

            if (AlphabetHelper.isNumber(c)) {
                iterator.putBack();
                tokens.add(Token.makeNumber(iterator));
                continue;
            }

            // + - .
            // 1+2, +1, -1*-2 3.5
            if ((c == '+' || c == '-' || c == '.') && AlphabetHelper.isNumber(lookahead)) {
                var lastToken = tokens.size() == 0 ? null : tokens.get(tokens.size() - 1);

                if (lastToken == null || lastToken.isOperator() || !lastToken.isNumber()) {
                    iterator.putBack();
                    tokens.add(Token.makeNumber(iterator));
                    continue;
                }
            }

            if (AlphabetHelper.isOperator(c)) {
                iterator.putBack();
                tokens.add(Token.makeOperator(iterator));
                continue;
            }

            throw new LexicalException(c);
        }//end while
        return tokens;
    }//end analyse

}
