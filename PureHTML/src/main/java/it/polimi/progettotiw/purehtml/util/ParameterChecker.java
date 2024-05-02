package it.polimi.progettotiw.purehtml.util;

import java.util.regex.Pattern;

/**
 * This is a util class. It contains methods to check parameter's validity through the execution of the application
 */
public class ParameterChecker {
    public static boolean checkString(String s) {
        return s!=null && s.length()>0;
    }

    public static boolean containsInvalidCharacters(String input) {
        Pattern invalidCharactersPattern = Pattern.compile("[\\\\\n\t\r:/?&=#+\\s]");
        return invalidCharactersPattern.matcher(input).find();
    }
}