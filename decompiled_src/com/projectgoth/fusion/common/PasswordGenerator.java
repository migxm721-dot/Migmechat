/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.PasswordVerifier;
import java.security.SecureRandom;
import java.util.Vector;

public class PasswordGenerator {
    public static final String version = "1.1";
    private static final int DEFAULT_PASSWORD_LENGTH = 8;
    public static final char[] NUMBERS_AND_LETTERS_ALPHABET = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public static final char[] SYMBOLS_ALPHABET = new char[]{'!', '\"', '#', '$', '%', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'};
    public static final char[] PRINTABLE_ALPHABET = new char[]{'!', '\"', '#', '$', '%', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~'};
    public static final char[] LOWERCASE_LETTERS_ALPHABET = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    public static final char[] LOWERCASE_LETTERS_AND_NUMBERS_ALPHABET = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public static final char[] LETTERS_ALPHABET = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    public static final char[] UPPERCASE_LETTERS_ALPHABET = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final char[] NONCONFUSING_ALPHABET = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9'};
    protected SecureRandom rand;
    protected int repetition = -1;
    protected char[] alphabet;
    protected char[] firstAlphabet;
    protected char[] lastAlphabet;
    private Vector<Requirement> requirements = null;
    private Vector<PasswordVerifier> verifiers = null;
    private boolean[] touched = null;
    private int[] available = null;

    public PasswordGenerator() {
        this(new SecureRandom(), NONCONFUSING_ALPHABET);
    }

    public PasswordGenerator(SecureRandom rand) {
        this(rand, NONCONFUSING_ALPHABET);
    }

    public PasswordGenerator(char[] alphabet) {
        this(new SecureRandom(), alphabet);
    }

    public PasswordGenerator(SecureRandom rand, char[] alphabet) {
        this.rand = rand;
        this.alphabet = alphabet;
    }

    public static void main(String[] args) throws Exception {
        PasswordGenerator passwordGenerator = new PasswordGenerator();
        passwordGenerator.setAlphabet(NONCONFUSING_ALPHABET);
        passwordGenerator.setFirstAlphabet(null);
        passwordGenerator.setLastAlphabet(null);
        passwordGenerator.setMaxRepetition(0);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; ++i) {
            passwordGenerator.getPass(10);
        }
        long end = System.currentTimeMillis();
        System.out.println("done in " + (end - start) / 1000L);
    }

    public void addRequirement(char[] alphabet, int num) {
        if (this.requirements == null) {
            this.requirements = new Vector();
        }
        this.requirements.add(new Requirement(alphabet, num));
    }

    public void setAlphabet(char[] alphabet) {
        if (alphabet == null) {
            throw new NullPointerException("Null alphabet");
        }
        if (alphabet.length == 0) {
            throw new ArrayIndexOutOfBoundsException("No characters in alphabet");
        }
        this.alphabet = alphabet;
    }

    public void setRandomGenerator(SecureRandom rand) {
        this.rand = rand;
    }

    public void setFirstAlphabet(char[] alphabet) {
        this.firstAlphabet = (char[])(alphabet == null || alphabet.length == 0 ? null : alphabet);
    }

    public void setLastAlphabet(char[] alphabet) {
        this.lastAlphabet = (char[])(alphabet == null || alphabet.length == 0 ? null : alphabet);
    }

    public void setMaxRepetition(int rep) {
        this.repetition = rep - 1;
    }

    public char[] getPassChars(char[] pass) {
        boolean verified = false;
        while (!verified) {
            int i;
            int length = pass.length;
            for (i = 0; i < length; ++i) {
                char[] useAlph = this.alphabet;
                if (i == 0 && this.firstAlphabet != null) {
                    useAlph = this.firstAlphabet;
                } else if (i == length - 1 && this.lastAlphabet != null) {
                    useAlph = this.lastAlphabet;
                }
                int size = PasswordGenerator.avoidRepetition(useAlph, pass, i, this.repetition, useAlph.length);
                pass[i] = useAlph[this.rand.nextInt(size)];
            }
            if (this.requirements != null) {
                this.applyRequirements(pass);
            }
            verified = true;
            for (i = 0; verified && this.verifiers != null && i < this.verifiers.size(); ++i) {
                verified = this.verifiers.elementAt(i).verify(pass);
            }
        }
        return pass;
    }

    public void addVerifier(PasswordVerifier verifier) {
        if (this.verifiers == null) {
            this.verifiers = new Vector();
        }
        this.verifiers.add(verifier);
    }

    private void applyRequirements(char[] pass) {
        int size = this.requirements.size();
        if (size > 0) {
            int length = pass.length;
            if (this.touched == null || this.touched.length < length) {
                this.touched = new boolean[length];
            }
            if (this.available == null || this.available.length < length) {
                this.available = new int[length];
            }
            for (int i = 0; i < length; ++i) {
                this.touched[i] = false;
            }
            for (int reqNum = 0; reqNum < size; ++reqNum) {
                Requirement req = this.requirements.elementAt(reqNum);
                int reqUsedInd = req.alphabet.length;
                int fufilledInd = 0;
                int availableInd = 0;
                for (int i = 0; i < length; ++i) {
                    if (PasswordGenerator.arrayContains(req.alphabet, pass[i]) && fufilledInd < req.num) {
                        ++fufilledInd;
                        this.touched[i] = true;
                        if (this.repetition < 0 || (reqUsedInd -= PasswordGenerator.moveto(req.alphabet, reqUsedInd, pass[i])) >= 0) continue;
                        reqUsedInd = req.alphabet.length;
                        continue;
                    }
                    if (this.touched[i]) continue;
                    this.available[availableInd] = i;
                    ++availableInd;
                }
                int toDo = req.num - fufilledInd;
                for (int i = 0; i < toDo && availableInd > 0; ++i) {
                    char passChar;
                    int slot = this.rand.nextInt(availableInd);
                    pass[this.available[slot]] = passChar = req.alphabet[this.rand.nextInt(reqUsedInd)];
                    this.touched[this.available[slot]] = true;
                    this.available[slot] = this.available[--availableInd];
                    if (this.repetition < 0 || (reqUsedInd -= PasswordGenerator.moveto(req.alphabet, reqUsedInd, passChar)) >= 0) continue;
                    reqUsedInd = req.alphabet.length;
                }
            }
        }
    }

    private static boolean arrayContains(char[] alph, char c) {
        for (int i = 0; i < alph.length; ++i) {
            if (alph[i] != c) continue;
            return true;
        }
        return false;
    }

    private static int avoidRepetition(char[] alph, char[] pass, int passSize, int repetition, int alphSize) {
        if (repetition > -1) {
            int repPos = 0;
            while ((repPos = PasswordGenerator.findRep(pass, repPos, passSize, repetition)) != -1) {
                alphSize -= PasswordGenerator.moveto(alph, alphSize, pass[repPos + repetition]);
                ++repPos;
            }
            if (alphSize == 0) {
                alphSize = alph.length;
            }
        }
        return alphSize;
    }

    private static int findRep(char[] pass, int start, int end, int length) {
        for (int i = start; i < end - length; ++i) {
            boolean onTrack = true;
            for (int j = 0; onTrack && j < length; ++j) {
                if (pass[i + j] == pass[end - length + j]) continue;
                onTrack = false;
            }
            if (!onTrack) continue;
            return i;
        }
        return -1;
    }

    private static int moveto(char[] alph, int numGood, char c) {
        int count = 0;
        for (int i = 0; i < numGood; ++i) {
            if (alph[i] != c) continue;
            char temp = alph[--numGood];
            alph[numGood] = alph[i];
            alph[i] = temp;
            ++count;
        }
        return count;
    }

    public char[] getPassChars(int length) {
        return this.getPassChars(new char[length]);
    }

    public char[] getPassChars() {
        return this.getPassChars(8);
    }

    public String getPass(int length) {
        return new String(this.getPassChars(new char[length]));
    }

    public String getPass() {
        return this.getPass(8);
    }

    private class Requirement {
        private char[] alphabet;
        private int num;

        private Requirement(char[] alphabet, int num) {
            this.alphabet = alphabet;
            this.num = num;
        }
    }
}

