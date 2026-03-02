/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.eliza;

public class EString {
    static final String num = "0123456789";

    public static int amatch(String str, String pat) {
        int count = 0;
        int i = 0;
        int j = 0;
        while (i < str.length() && j < pat.length()) {
            char p = pat.charAt(j);
            if (p == '*' || p == '#') {
                return count;
            }
            if (str.charAt(i) != p) {
                return -1;
            }
            ++i;
            ++j;
            ++count;
        }
        return count;
    }

    public static int findPat(String str, String pat) {
        int count = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (EString.amatch(str.substring(i), pat) >= 0) {
                return count;
            }
            ++count;
        }
        return -1;
    }

    public static int findNum(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (num.indexOf(str.charAt(i)) == -1) {
                return count;
            }
            ++count;
        }
        return count;
    }

    static boolean matchA(String str, String pat, String[] matches) {
        int i = 0;
        int j = 0;
        int pos = 0;
        while (pos < pat.length() && j < matches.length) {
            int n;
            char p = pat.charAt(pos);
            if (p == '*') {
                n = pos + 1 == pat.length() ? str.length() - i : EString.findPat(str.substring(i), pat.substring(pos + 1));
                if (n < 0) {
                    return false;
                }
                matches[j++] = str.substring(i, i + n);
                i += n;
                ++pos;
                continue;
            }
            if (p == '#') {
                n = EString.findNum(str.substring(i));
                matches[j++] = str.substring(i, i + n);
                i += n;
                ++pos;
                continue;
            }
            n = EString.amatch(str.substring(i), pat.substring(pos));
            if (n <= 0) {
                return false;
            }
            i += n;
            pos += n;
        }
        return i >= str.length() && pos >= pat.length();
    }

    public static boolean match(String str, String pat, String[] matches) {
        return EString.matchA(str, pat, matches);
    }

    public static String translate(String str, String src, String dest) {
        if (src.length() != dest.length()) {
            // empty if block
        }
        for (int i = 0; i < src.length(); ++i) {
            str = str.replace(src.charAt(i), dest.charAt(i));
        }
        return str;
    }

    public static String compress(String s) {
        String dest = "";
        if (s.length() == 0) {
            return s;
        }
        char c = s.charAt(0);
        for (int i = 1; i < s.length(); ++i) {
            if (c != ' ' || s.charAt(i) != ' ' && s.charAt(i) != ',' && s.charAt(i) != '.') {
                dest = c != ' ' && s.charAt(i) == '?' ? dest + c + " " : dest + c;
            }
            c = s.charAt(i);
        }
        dest = dest + c;
        return dest;
    }

    public static String trim(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ' ') continue;
            return s.substring(i);
        }
        return "";
    }

    public static String pad(String s) {
        if (s.length() == 0) {
            return " ";
        }
        char first = s.charAt(0);
        char last = s.charAt(s.length() - 1);
        if (first == ' ' && last == ' ') {
            return s;
        }
        if (first == ' ' && last != ' ') {
            return s + " ";
        }
        if (first != ' ' && last == ' ') {
            return " " + s;
        }
        if (first != ' ' && last != ' ') {
            return " " + s + " ";
        }
        return s;
    }

    public static int count(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != c) continue;
            ++count;
        }
        return count;
    }
}

