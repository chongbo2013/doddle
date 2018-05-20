package com.dodles.gdx.dodleengine.util;

import com.badlogic.gdx.graphics.Pixmap;

/**
 * Cross-platform factory for creating Pixmaps.
 */
public interface PixmapFactory {
    /**
     * Creates a pixmap from a base-64 string.
     */
    Pixmap createPixmapFromBase64String(String base64);
}
