package lexer;

import lexer.common.AlphabetHelper;
import lexer.common.LexicalException;
import lexer.common.PeekIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public class Lexer {
    public ArrayList<Token> analyse(PeekIterator<Character> iterator) throws LexicalException {
        var tokens = new ArrayList<Token>();

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
            if(c == '/') {
                if(lookahead == '/') {
                    while(iterator.hasNext() && (c = iterator.next()) != '\n') {};
                    continue;
                }
                else if(lookahead == '*') {
                    iterator.next();//多读一个* 避免/*/通过
                    boolean valid = false;
                    while(iterator.hasNext()) {
                        char p = iterator.next();
                        if(p == '*' && iterator.peek() == '/') {
                            iterator.next();
                            valid = true;
                            break;
                        }
                    }
                    if(!valid) {
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

    public ArrayList<Token> analyse(Stream source) throws LexicalException {
        var it = new PeekIterator<Character>(source, (char) 0);//char 0 end mark
        return this.analyse(it);
    }

    /**
     * 从源代码文件加载并解析
     *
     * @param src
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws LexicalException
     */
    public static ArrayList<Token> fromFile(String src) throws FileNotFoundException, UnsupportedEncodingException, LexicalException {
        var file = new File(src);
        var fileStream = new FileInputStream(file);
        var inputStreamReader = new InputStreamReader(fileStream, "UTF-8");

        var br = new BufferedReader(inputStreamReader);


        /**
         * 利用BufferedReader每次读取一行
         */
        var it = new Iterator<Character>() {
            private String line = null;
            private int cursor = 0;

            private void readLine() throws IOException {
                if (line == null || cursor == line.length()) {
                    line = br.readLine();
                    cursor = 0;
                }
            }

            @Override
            public boolean hasNext() {
                try {
                    readLine();
                    return line != null;
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public Character next() {
                try {
                    readLine();
                    return line != null ? line.charAt(cursor++) : null;
                } catch (IOException e) {
                    return null;
                }
            }
        };

        var peekIt = new PeekIterator<Character>(it, '\0');

        var lexer = new Lexer();
        return lexer.analyse(peekIt);

    }
}
