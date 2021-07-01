package parser;

import lexer.common.LexicalException;
import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.common.ParseException;
import parser.common.PeekTokenIterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleParserTest {
    @Test
    public void test() throws LexicalException, java.text.ParseException, ParseException {
        var source = "1+2+3+4+5".chars().mapToObj(x -> (char) x);
        var lexer = new Lexer();
        var it = new PeekTokenIterator(lexer.analyse(source).stream());
        var expr = SimpleParser.parse(it);

        assertEquals(2, expr.getChildren().size());

        var v1 = expr.getChild(0);
        assertEquals("1", v1.getLexeme().get_value());
        assertEquals("+", expr.getLexeme().get_value());

        var e2 = expr.getChild(1);
        var v2 = e2.getChild(0);
        assertEquals("2", v2.getLexeme().get_value());
        assertEquals("+", e2.getLexeme().get_value());

        var e3 = e2.getChild(1);
        var v3 = e3.getChild(0);
        assertEquals("3", v3.getLexeme().get_value());
        assertEquals("+", e3.getLexeme().get_value());

        var e4 = e3.getChild(1);
        var v4 = e4.getChild(0);
        assertEquals("4", v4.getLexeme().get_value());
        assertEquals("+", e4.getLexeme().get_value());

        var v5 = e4.getChild(1);
        assertEquals("5", v5.getLexeme().get_value());

        expr.print(0);
    }
}
