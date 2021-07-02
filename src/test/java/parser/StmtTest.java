package parser;

import lexer.common.LexicalException;
import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.ast.*;
import parser.common.ParseException;
import parser.common.ParserUtils;
import parser.common.PeekTokenIterator;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StmtTest {

    private PeekTokenIterator createTokenIt(String src) throws LexicalException {
        var lexer = new Lexer();
        var tokens = lexer.analyse(src.chars().mapToObj(c -> (char) c));
        return new PeekTokenIterator(tokens.stream());
    }

    @Test
    public void declare() throws LexicalException, ParseException {
        var iterator = createTokenIt("var i = 100 * 2");
        var stmt = DeclareStmt.parse(iterator);
        stmt.print(0);
        assertEquals(ParserUtils.toPostfixExpression(stmt), "i 100 2 * =");
    }

    @Test
    public void assign() throws LexicalException, ParseException {
        var iterator = createTokenIt("i = 100 * 2");
        var stmt = AssignStmt.parse(iterator);
        stmt.print(0);
        assertEquals(ParserUtils.toPostfixExpression(stmt), "i 100 2 * =");
    }

    @Test
    public void ifStmt() throws LexicalException, ParseException {
        var iterator = createTokenIt("if(a){\n" +
                "a = 1\n" +
                "}"
        );

        var stmt = (IfStmt) IfStmt.parse(iterator);
        var expr = (Variable) stmt.getChild(0);
        var block = (Block) stmt.getChild(1);
        var assignStmt = (AssignStmt) block.getChild(0);

        assertEquals("a", expr.getLexeme().get_value());
        assertEquals("=", assignStmt.getLexeme().get_value());
    }

    @Test
    public void ifElseStmt0() throws LexicalException, ParseException {
        var it = createTokenIt("if(a) {\n" +
                "a = 1\n" +
                "} else {\n" +
                "a = 2\n" +
                "a = a * 3" +
                "}"
        );
        var stmt = (IfStmt) IfStmt.parse(it);
        var expr = (Variable) stmt.getChild(0);
        var block = (Block) stmt.getChild(1);
        var assignStmt = (AssignStmt) block.getChild(0);
        var elseBlock = (Block) stmt.getChild(2);
        var assignStmt2 = (AssignStmt) elseBlock.getChild(0);

        assertEquals("a", expr.getLexeme().get_value());
        assertEquals("=", assignStmt.getLexeme().get_value());
        assertEquals("=", assignStmt2.getLexeme().get_value());
        assertEquals(2, elseBlock.getChildren().size());
    }

    @Test
    public void ifElseStmt1() throws LexicalException, ParseException {
        var iterator = createTokenIt("if(a){\n" +
                "a = 1\n" +
                "} else if (b) {\n" +
                "a = 2\n" +
                "}"
        );
        var stmt = (IfStmt) IfStmt.parse(iterator);
        var expr1 = (Variable) stmt.getChild(0);
        var block1 = (Block) stmt.getChild(1);
        var assignStmt = (AssignStmt) block1.getChild(0);
        var tail = (IfStmt) stmt.getChild(2);
        var expr2 = (Variable) tail.getChild(0);
        var block2 = (Block) tail.getChild(1);
        var assignStmt2 = (AssignStmt) block2.getChild(0);

        assertEquals("a", expr1.getLexeme().get_value());
        assertEquals("=", assignStmt.getLexeme().get_value());
        assertEquals(2, tail.getChildren().size());
        assertEquals("b", expr2.getLexeme().get_value());
        assertEquals("=", assignStmt2.getLexeme().get_value());
    }

    @Test
    public void function() throws FileNotFoundException, UnsupportedEncodingException, LexicalException, ParseException {
        var tokens = Lexer.fromFile("./example/function.ts");
        var functionStmt = (FunctionDeclareStmt) Stmt.parseStmt(new PeekTokenIterator(tokens.stream()));
        functionStmt.print(0);

        var args = functionStmt.getArgs();
        assertEquals("a", args.getChild(0).getLexeme().get_value());
        assertEquals("b", args.getChild(1).getLexeme().get_value());

        var type = functionStmt.getFuncType();
        assertEquals("int", type);

        var functionVariable = functionStmt.getFunctionVariable();
        assertEquals("add", functionVariable.getLexeme().get_value());

        var block = functionStmt.getBlock();
        assertEquals(true, block.getChild(0) instanceof ReturnStmt);

        System.out.println(ParserUtils.toPostfixExpression(functionStmt));
        System.out.println(ParserUtils.toBFSString(functionStmt,4));
        System.out.println(ParserUtils.toBFSString(functionStmt.getArgs(),3));
        System.out.println(ParserUtils.toBFSString(functionStmt.getBlock(),2));

    }

    @Test
    public void function1() throws FileNotFoundException, UnsupportedEncodingException, LexicalException, ParseException {
        /*var it = createTokenIt("func fact(int n)  int {\n" +
                "  if(n == 0) {\n" +
                "    return 1\n" +
                "  }\n" +
                "  return fact(n - 1) * n\n" +
                "}");*/

        var tokens = Lexer.fromFile("./example/recursion.ts");
        var functionStmt = (FunctionDeclareStmt) Stmt.parseStmt(new PeekTokenIterator(tokens.stream()));
        functionStmt.print(0);
        System.out.println(ParserUtils.toPostfixExpression(functionStmt));
        System.out.println(ParserUtils.toBFSString(functionStmt,4));
        System.out.println(ParserUtils.toBFSString(functionStmt.getArgs(),2));
        System.out.println(ParserUtils.toBFSString(functionStmt.getBlock(),3));
        assertEquals("func fact args block", ParserUtils.toBFSString(functionStmt, 4));
        assertEquals("args n", ParserUtils.toBFSString(functionStmt.getArgs(), 2));
        assertEquals("block if return", ParserUtils.toBFSString(functionStmt.getBlock(), 3));
    }

}
