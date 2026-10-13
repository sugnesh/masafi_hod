package com.engear.vansales.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtilities {
    public static final String CRLF = "\r\n";
    public static final String LF = "\n";

    public static int indexOf(String inputString, String[] searchPatterns, int start) {
        int minIndex = inputString.length();
        for (String indexOf : searchPatterns) {
            int foundIndex = inputString.indexOf(indexOf, start);
            if (foundIndex >= 0 && foundIndex < minIndex) {
                minIndex = foundIndex;
            }
        }
        if (minIndex == inputString.length()) {
            return -1;
        }
        return minIndex;
    }

    public static String[] split(String input, String delimiter) {
        List<String> tokens = new ArrayList();
        int startOfToken = 0;
        int nextDelimiter = 0;
        while (nextDelimiter >= 0) {
            nextDelimiter = input.indexOf(delimiter, startOfToken);
            if (nextDelimiter >= 0) {
                tokens.add(input.substring(startOfToken, nextDelimiter));
                startOfToken = nextDelimiter + delimiter.length();
            } else if (startOfToken <= input.length() - 1) {
                tokens.add(input.substring(startOfToken));
            }
        }
        return (String[]) tokens.toArray(new String[0]);
    }

    public static int countSubstringOccurences(String stringToSearch, String substring) {
        int position = 0;
        int count = 0;
        while (position >= 0) {
            position = stringToSearch.indexOf(substring, position);
            if (position >= 0) {
                count++;
                position += substring.length();
            }
        }
        return count;
    }

    public static String stripQuotes(String str) {
        if (str == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch != '\"') {
                result.append(ch);
            }
        }
        return result.toString();
    }

    public static String padWithChar(String initialString, char padding, int lengthWithPadding, boolean padInFront) {
        StringBuffer sb = new StringBuffer(initialString);
        int amountToPad = lengthWithPadding - initialString.length();
        for (int i = 0; i < amountToPad; i++) {
            if (padInFront) {
                sb.insert(0, padding);
            } else {
                sb.append(padding);
            }
        }
        return sb.toString();
    }

    public static boolean doesPrefixExistInArray(String[] prefixes, String string) {
        if (string == null || prefixes == null || string.length() == 0 || prefixes.length == 0) {
            return false;
        }
        int i = 0;
        while (i < prefixes.length) {
            if (prefixes[i] != null && prefixes[i].length() != 0 && string.toUpperCase().startsWith(prefixes[i].toUpperCase())) {
                return true;
            }
            i++;
        }
        return false;
    }
}
