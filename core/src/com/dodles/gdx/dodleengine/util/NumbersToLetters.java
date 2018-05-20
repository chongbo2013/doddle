package com.dodles.gdx.dodleengine.util;

/**
 * Util class to convert a number to Letters like "A", "B", "AA".
 * Used to name the Layers.
 * http://stackoverflow.com/questions/25796592/program-to-convert-numbers-to-letters.
 */
public final class NumbersToLetters {

    private NumbersToLetters() {

    }

    /**
     * convert a number to an Alphabetic representation.  "A", "AAB", etc.
     * @param num
     * @return
     */
    public static String numberToLetters(Integer num) {
        int quotient;
        int remainder;
        String result = "";

        quotient = num;
        while (quotient >= 0) {
            remainder = quotient % 26;
            result = (char) (remainder + 65) + result;
            quotient = (int) Math.floor(quotient / 26) - 1;
        }
        return result;
    }
}
