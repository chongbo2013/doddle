package com.dodles.mobileinterop;

import com.dodles.gdx.dodleengine.util.NumberFormatter;
import java.text.DecimalFormat;
import javax.inject.Inject;

/**
 * Implements NumberFormatter using JRE classes.
 */
public class JreNumberFormatter implements NumberFormatter {
    @Inject
    public JreNumberFormatter() {
    }
    
    @Override
    public final String decimalFormat(String format, float value) {
        return new DecimalFormat(format).format(value);
    }    
}
