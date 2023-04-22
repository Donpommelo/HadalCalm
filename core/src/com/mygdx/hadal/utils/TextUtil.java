package com.mygdx.hadal.utils;

public class TextUtil {

    public static String removeNonAlphaNumeric(String str) {
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }


}
