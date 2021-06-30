package lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Keywords {
    static String[] keywords = {
            "var",
            "int",
            "float",
            "bool",
            "void",
            "string",
            "if",
            "else",
            "for",
            "while",
            "break",
            "func",
            "return"
    };

    static HashSet<String> hashSet = new HashSet<>(Arrays.asList(keywords));

    public static boolean isKeyword(String word){
        return hashSet.contains(word);
    }
}
