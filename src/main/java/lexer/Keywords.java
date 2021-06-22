package lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Keywords {
    static String[] keywords = {
            "if",
            "else",
            "while",
            "for",
            "break",
            "func",
            "return",
            "var",
    };

    static HashSet<String> hashSet = new HashSet<>(Arrays.asList(keywords));

    public static boolean isKeyword(String word){
        return hashSet.contains(word);
    }
}
