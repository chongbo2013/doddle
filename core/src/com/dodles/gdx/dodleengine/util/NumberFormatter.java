package com.dodles.gdx.dodleengine.util;

/**
 * Interface to allow using custom GWT code for formatting since the JRE classes aren't available there. :(
 */
public interface NumberFormatter {
    /**
     * Formats a number according to decimal formatting rules.
     */
    String decimalFormat(String format, float value);
}
