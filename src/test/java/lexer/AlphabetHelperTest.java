package lexer;

import lexer.common.AlphabetHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlphabetHelperTest {

    @Test
    public void test() {
        assertEquals(true, AlphabetHelper.isLetter('a'));
        assertEquals(true, AlphabetHelper.isLetter('Z'));
        assertEquals(false, AlphabetHelper.isLetter('_'));
        assertEquals(true, AlphabetHelper.isNumber('0'));
        assertEquals(true, AlphabetHelper.isNumber('9'));
        assertEquals(false, AlphabetHelper.isNumber('c'));
        assertEquals(true, AlphabetHelper.isLiteral('_'));
        assertEquals(true, AlphabetHelper.isLiteral('a'));
        assertEquals(false, AlphabetHelper.isLiteral('*'));
        assertEquals(true, AlphabetHelper.isOperator('*'));
        assertEquals(true, AlphabetHelper.isOperator('/'));
        assertEquals(true, AlphabetHelper.isOperator('|'));
        assertEquals(true, AlphabetHelper.isOperator('='));
        assertEquals(true, AlphabetHelper.isOperator('%'));
        assertEquals(true, AlphabetHelper.isOperator('<'));
        assertEquals(true, AlphabetHelper.isOperator('^'));
        assertEquals(true, AlphabetHelper.isOperator('&'));
        assertEquals(false, AlphabetHelper.isOperator('s'));
    }
}
