package com.dodles.gdx.dodleengine.util;

import com.badlogic.gdx.math.Rectangle;

/**
 * Interface for implementing screenshots on various platforms.
 */
public interface ScreenGrabber {
    /**
     * Takes a screenshot and returns the result in Base64.
     */
    String takeBase64Screenshot(Rectangle cropBounds);
}
