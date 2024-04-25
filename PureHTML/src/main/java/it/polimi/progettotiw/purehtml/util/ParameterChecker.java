package it.polimi.progettotiw.purehtml.util;

public class ParameterChecker {
    public static boolean checkString(String s) {
        return s!=null && s.length()>0;
    }
}