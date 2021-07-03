# Compiler Experiment : Simple Compiler

## Author：软件81金之航

## StuId：2183411101



## 一.词法分析  Lexer 

将字符流转成符号流。输入：源代码（字符流)输出：符号流

词法分析器根据词法规则识别出源程序中的各个符号（token），每个记号代表一类单词（lexeme）。源程序中常见的记号可以归为几大类：关键字、标识符、标量和特殊符号。词法分析器的输入是源程序，输出是识别的符号流。词法分析器的任务是把源文件的字符流转换成记号流。本质上它查看连续的字符然后把它们识别为“单词”。

词法分析器的目标：
给定程序语言(L)以及所有L支持的词汇，从中找出这些词汇并为他们标注词性。
如果源代码中有语言(L)不支持的词汇，报错并提示用户。

词法和语法的区别？
词法就是构词的方法（例如:有哪些词性?有哪些字母?有哪些词语?)
语法就是造句的方法（这个句子是如何构成的？）

编译器制作过程中我们通常用正则表达式来表述词法，然后用状态机来实现正则表达式。

正则描述词法
通常的我们可以通过正则表达式描述：类词法单元（符号)
关键词可以这样描述(if l else l return l for...)
整数可以表示为[+-]?[0-9]+
运算符号可以描述为(+|-I*\/^|&/||)

Example:

```java
var a = ( 1 + 4 ) * 5   --->   var : Keyword
                               a   : Variable
                               =   : Operator
                               (   : Bracket
                               1   : Integer
                               +   : Operator                           
                               4   : Integer
                               )   : Bracket
                               *   : Operator
                               5   : Integer
```



## 1.PeekIterator

流:随着时间推移逐渐产生的可用数据序列。
作用:抽象出像工厂流水线一样处理数据的标准过程。

一般情况，流需要提供获取下一个数据的接口(next、hasNext方法)，next方法读取到一个数据后，这个数据就相当于流过去了，因此无法重复读取。这里实现了next、hasNext、peek和putBack方法。

```java
public class PeekIterator<T> implements Iterator<T> {//泛型接口T
    private static int CACHE_SIZE = 10;
    private Iterator<T> it;
    private LinkedList<T> queueCache = new LinkedList<T>();  //队列实现，先进先出
    private LinkedList<T> stackPutBacks = new LinkedList<T>();
    private T _endToken = null;

    public PeekIterator(Stream<T> stream) {
        it = stream.iterator();
    }

    public PeekIterator(Iterator<T> _it, T endToke) {
        this.it = _it;
        this._endToken = endToke;
    }

    public PeekIterator(Stream<T> stream, T endToken) {
        it = stream.iterator();
        this._endToken = endToken;
    }

    @Override
    public boolean hasNext() {  //判断流中是否有下一个字符
        return this.stackPutBacks.size() > 0 || it.hasNext() || _endToken != null;
    }

    @Override
    public T next() {  //取出流中的下一个字符
        T val;
        if (stackPutBacks.size() > 0) {
            val = this.stackPutBacks.pop();
        } else {
            if (!this.it.hasNext()) {
                T temp = _endToken;
                _endToken = null;
                return temp;
            }
            val = it.next();
        }
        while (queueCache.size() > CACHE_SIZE - 1) {
            queueCache.poll();
        }
        queueCache.add(val);
        return val;
    }

    public T peek() {  //获得流中的下一个字符，不取出
        if (this.stackPutBacks.size() > 0) {
            return this.stackPutBacks.getFirst();
        }
        if (!it.hasNext()) {
            return _endToken;
        }
        T val = next();
        this.putBack();
        return val;
    }

    //cache: A -> B ->C -> D in queueCache
    //putBack: D -> into stackPutBacks
    public void putBack() {  //放回一个字符
        if (this.queueCache.size() > 0) {
            this.stackPutBacks.push(this.queueCache.pollLast());
        }
    }
}
```



## 2.TokenType

一个枚举类，包含语言中的各种符号类型：

```java
package lexer;
public enum TokenType {
    KEYWORD,  //关键词
    VARIABLE, //变量
    OPERATOR, //操作符
    BRACKET,  //括号
    STRING,   //字符串	
    INTEGER,  //整型
    FLOAT,    //浮点数
    BOOLEAN,  //布尔型
}
```



## 3.Token

符号类，包括Token的Type和Value，可以通过makeVarOrKeyword(), makeString(), makeOperator() , makeNumber()这四个方法从PeekIterator 这个源程序的字符流中（迭代器）中获取相应Token，此外还有判断TokenType的方法。

<img src="C:\Users\jin0805\AppData\Roaming\Typora\typora-user-images\image-20210625001915959.png" alt="image-20210625001915959" style="zoom:60%;" />



### (1)AlphabetHelper

一个帮助类，判断字符的类型。

```java
public class AlphabetHelper {
    static Pattern patternLetter = Pattern.compile("^[a-zA-Z]$");
    static Pattern patternNumber = Pattern.compile("^[0-9]$");
    static Pattern patternLiteral = Pattern.compile("^[_a-zA-Z0-9]$");
    static Pattern patternOperator = Pattern.compile("^[+\\-*/><=!&|^%,]$");

    public static boolean isLetter(char c) {
        return patternLetter.matcher(c + "").matches();
    }

    public static boolean isNumber(char c) {
        return patternNumber.matcher(c + "").matches();
    }

    public static boolean isLiteral(char c) {
        return patternLiteral.matcher(c + "").matches();
    }

    public static boolean isOperator(char c) {
        return patternOperator.matcher(c + "").matches();
    }
}
```



### (2)MakeVarOrKeyWord:

makeVarOrKeyword 从Iterator里读取字符，进行判断，并返回关键词类型Keyword Token或者变量类型Variable Token

```
关键词和变量名都以字母下划线开头，但又有所区别。
正则表示: [_a-zA-z ][_a-zA-z0 -9]*
```

对应状态机如下：

![image-20210625001359671](C:\Users\jin0805\AppData\Roaming\Typora\typora-user-images\image-20210625001359671.png)

```java
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
```



### (3)makeString:

makeString 从Iterator里读取字符（形如'abc', "abc"），进行判断，并返回字符串类型String Token
对应状态机：

![image-20210625001242937](C:\Users\jin0805\AppData\Roaming\Typora\typora-user-images\image-20210625001242937.png)

```java

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
```



### (4)makeOperator

```
makeOperator从Iterator里读取字符，进行判断，并返回操作符类型Operator Token
static Pattern patternOperator = Pattern.compile("^[+-\\\\*/><=!&|^%]$");
Token类型分为UNARY操作符（比如：= , - , * , / ）
           Binary操作符(比如：>= , == , ++ , -= , != , || , && )
```

对应状态机：

<img src="C:\Users\jin0805\AppData\Roaming\Typora\typora-user-images\image-20210625001544617.png" alt="image-20210625001544617" style="zoom: 67%;" />

<img src="C:\Users\jin0805\AppData\Roaming\Typora\typora-user-images\image-20210625001734899.png" alt="image-20210625001734899" style="zoom:67%;" />

```java
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
```



### (5)makeNumber

makeNumber 从Iterator里读取字符，进行判断，并返回数值类型Token
对应状态机：

![image-20210702162824111](C:\Users\jin0805\AppData\Roaming\Typora\typora-user-images\image-20210702162824111.png)

```java
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
```



## 4.Lexer

完整的词法分析器对应状态机：

![image-20210625001328363](C:\Users\jin0805\AppData\Roaming\Typora\typora-user-images\image-20210625001328363.png)

```java
public class Lexer {
    public ArrayList<Token> analyse(PeekIterator<Character> iterator) throws LexicalException {
        var tokens = new ArrayList<Token>();

        while (iterator.hasNext()) {
            char c = iterator.next();

            if (c == 0) {
                break;
            }

            char lookahead = iterator.peek();  //must after if(c==0){break;}
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

    public ArrayList<Token> analyse(Stream source) throws LexicalException {
        var it = new PeekIterator<Character>(source, (char)0);//char 0 end mark
        return this.analyse(it);
    }
}
```

词法分析Example1：

```java
@Test
public void test_function() throws LexicalException {
     var source = "func foo(a, b){\n" +
                "print(a +  b)\n" + "foo(c,d)" +
                "}\n" +
                "foo(-100.0, 100)";
     var lexer = new Lexer();
     var tokens = lexer.analyse(source.chars().mapToObj(x -> (char) x));
     System.out.println(tokens.toString());
}
```

输出 Output1：

```java
[Token(_type=KEYWORD, _value=func), 
Token(_type=VARIABLE, _value=foo), 
Token(_type=BRACKET, _value=(), 
Token(_type=VARIABLE, _value=a), 
Token(_type=OPERATOR, _value=,), 
Token(_type=VARIABLE, _value=b), 
Token(_type=BRACKET, _value=)), 
Token(_type=BRACKET, _value={), 
Token(_type=VARIABLE, _value=print), 
Token(_type=BRACKET, _value=(), 
Token(_type=VARIABLE, _value=a), 
Token(_type=OPERATOR, _value=+), 
Token(_type=VARIABLE, _value=b), 
Token(_type=BRACKET, _value=)), 
Token(_type=VARIABLE, _value=foo), 
Token(_type=BRACKET, _value=(), 
Token(_type=VARIABLE, _value=c), 
Token(_type=OPERATOR, _value=,), 
Token(_type=VARIABLE, _value=d), 
Token(_type=BRACKET, _value=)), 
Token(_type=BRACKET, _value=}), 
Token(_type=VARIABLE, _value=foo),
Token(_type=BRACKET, _value=(), 
Token(_type=FLOAT, _value=-100.0), 
Token(_type=OPERATOR, _value=,), 
Token(_type=INTEGER, _value=100), 
Token(_type=BRACKET, _value=))]
```