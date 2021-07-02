package translator;

import lexer.common.LexicalException;
import org.junit.jupiter.api.Test;
import parser.Parser;
import parser.common.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StaticTableTest {

    @Test
    public void staticTest() throws LexicalException, ParseException {
        var source = "if(a) { a = 1 } else { b = a + 1 * 5 }";
        var astNode = Parser.parse(source);
        var translator = new Translator();
        var program =  translator.translate(astNode);

        assertEquals(2, program.getStaticSymbolTable().size());
    }
}
