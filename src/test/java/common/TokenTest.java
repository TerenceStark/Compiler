package common;

import Common.PeekIterator;
import lexer.LexicalException;
import lexer.Token;
import lexer.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TokenTest {

    void assertToken(Token token, String value, TokenType tokenType) {
        assertEquals(tokenType, token.get_type());
        assertEquals(value, token.get_value());
    }

    @Test
    public void testMakeVarOrKeyword() {
        var it1 = new PeekIterator<Character>("if abc".chars().mapToObj(c -> (char) c));
        var it2 = new PeekIterator<Character>("true abc".chars().mapToObj(c -> (char) c));

        var token1 = Token.makeVarOrKeyword(it1);
        var token2 = Token.makeVarOrKeyword(it2);

        /*assertEquals(TokenType.VARIABLE, token1.get_type());
        assertEquals("if", token1.get_value());*/
        assertToken(token1, "if", TokenType.KEYWORD);
        assertToken(token2, "true", TokenType.BOOLEAN);
        it1.next();
        it2.next();
        var token3 = Token.makeVarOrKeyword(it1);
        var token4 = Token.makeVarOrKeyword(it2);

        assertToken(token3, "abc", TokenType.VARIABLE);
        assertToken(token4, "abc", TokenType.VARIABLE);
    }

    @Test
    public void testMakeString() throws LexicalException {
        String[] tests = {
                "\"TerenceStark\"",
                "'Fernando Torres'",
                "\"     \"",
                "'     '"
        };

        for (String test : tests) {
            var it1 = new PeekIterator<Character>(test.chars().mapToObj(c -> (char) c));
            var token1 = Token.makeString(it1);
            System.out.println(token1);
            assertToken(token1, test, TokenType.STRING);
        }

        /*var it1 = new PeekIterator<Character>("\"TerenceStark\"".chars().mapToObj(c -> (char) c));
        var token1 = Token.makeString(it1);
        System.out.println(token1);
        assertToken(token1,"\"TerenceStark\"",TokenType.STRING);*/
    }

    @Test
    public void testMakeOperator() throws LexicalException {
        String[] tests = {
                "++dddd",
                "=--1",
                "-=;",
                "/=g",
                "+ xx abc",
                "&&!",
                "**",
                "==mmm"
        };

        String[] results = {"++", "=", "-=", "/=", "+", "&&", "*", "=="};
        int i = 0;
        for (String test : tests) {
            var it1 = new PeekIterator<Character>(test.chars().mapToObj(c -> (char) c));
            var token1 = Token.makeOperator(it1);
            assertToken(token1,results[i++],TokenType.OPERATOR);
        }
    }

}
