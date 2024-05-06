package it.polimi.progettotiw.purehtml.util;

/**
 * This is a util class. It contains methods to check parameter's validity through the execution of the application.
 */
public class ParameterChecker {
    /**
     * This class checks if the string is not null and if it is not an empty string
     * @param s the string to be checked
     * @return true if the string is valid, false if the string is null or if the string is an empty string
     */
    public static boolean checkString(String s) {
        return s!=null && !s.isEmpty();
    }
}