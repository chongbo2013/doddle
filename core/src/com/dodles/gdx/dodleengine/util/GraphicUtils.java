package com.dodles.gdx.dodleengine.util;

import com.badlogic.gdx.math.Vector2;
/**
 * Some helper methods for graphic work
 * for getMin() getMax() if you set your excludePos to -1, the routine will find the min/max value
 * for all elements of the Vector2 c array.
 * @author marknickel
 *
 */
public final class GraphicUtils {

    private GraphicUtils() {
    }

    /**
     * helper to find the minimum X value from the list of Corners excluding the 
     * current corner -- no lodash in Java.
     * @param c
     * @param excludePos
     * @return
     */
    public static float getMin(Vector2[] c, int size, int excludePos, char property) {
        float minValue = 0f;
        float cornerValue = 0f;
        
        //pick first value that isn't excluded
        for (int i = 0; i < size; i++) {
            if (i != excludePos) {
                //CHECKSTYLE.OFF: AvoidInlineConditionals - short and appropriate use of inlining
                minValue = property == 'x' ? c[i].x : c[i].y;
                //CHECKSTYLE.ON: AvoidInlineConditionals
                break;
            }
        }
        
        for (int i = 0; i < size; i++) {
            //CHECKSTYLE.OFF: AvoidInlineConditionals - short and appropriate use of inlining
            cornerValue = property == 'x' ? c[i].x : c[i].y;
            //CHECKSTYLE.ON: AvoidInlineConditionals
            if ((i != excludePos) && (cornerValue < minValue)) {
                minValue = cornerValue;
            }
        }
        return minValue;
    }

    /**
     * helper to find the minimum X value from the list of Corners excluding the 
     * current corner -- no lodash in Java.
     * @param c
     * @param excludePos
     * @return
     */
    public static float getMax(Vector2[] c, int size, int excludePos, char property) {
        float maxValue = 0f;
        float cornerValue = 0f;
        
        //pick first value that isn't excluded
        for (int i = 0; i < size; i++) {
            if (i != excludePos) {
                //CHECKSTYLE.OFF: AvoidInlineConditionals - short and appropriate use of inlining
                maxValue = property == 'x' ? c[i].x : c[i].y;
                //CHECKSTYLE.ON: AvoidInlineConditionals
                break;
            }
        }
        
        for (int i = 0; i < size; i++) {
            //CHECKSTYLE.OFF: AvoidInlineConditionals - short and appropriate use of inlining
            cornerValue = property == 'x' ? c[i].x : c[i].y;
            //CHECKSTYLE.ON: AvoidInlineConditionals
            if ((i != excludePos) && (cornerValue > maxValue)) {
                maxValue = cornerValue;
            }
        }
        return maxValue;
    }
}
