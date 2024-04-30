package it.polimi.progettotiw.purehtml.util;

/**
 * This is a util class. It contains methods to check parameter's validity through the execution of the application
 */
public class ParameterChecker {
    public static boolean checkString(String s) {
        return s!=null && s.length()>0;
    }
}