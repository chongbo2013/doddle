package com.dodles.gdx.dodleengine.editor.utils;


/**
 * A collection of utility functions for the UI Classes.
 */
public final class DodlesUIUtil {

    private DodlesUIUtil() { }

    /**
     * Computes new Dimension that is constrained by the maximum width and height, but conforms to the aspect ratio as
     * specified by the original width and height.
     * @param originalWidth
     * @param originalHeight
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Dimensions computeDimensionsWithAspectRatio(
            float originalWidth,
            float originalHeight,
            float maxWidth,
            float maxHeight
    ) {
        float drawableAspectRatio = originalWidth / originalHeight;
        float paramAspectRatio = maxWidth / maxHeight;
        Dimensions result = new Dimensions();

        if (paramAspectRatio >= drawableAspectRatio) {
            // too wide (or equal): match the height!
            float newWidth = maxHeight * drawableAspectRatio;
            result.setDimensions(newWidth, maxHeight);
        } else {
            // too tall: match the width!
            float newHeight = maxWidth / drawableAspectRatio;
            result.setDimensions(maxWidth, newHeight);
        }

        return result;
    }

}
