package Common;

import java.util.regex.Pattern;

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
