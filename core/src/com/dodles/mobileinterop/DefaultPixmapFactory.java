package com.dodles.mobileinterop;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Base64Coder;
import com.dodles.gdx.dodleengine.util.PixmapFactory;

import javax.inject.Inject;

/**
 * Non-HTML implementation of PixmapFactory.
 */
public class DefaultPixmapFactory implements PixmapFactory {
    @Inject
    public DefaultPixmapFactory() {
    }

    @Override
    public final Pixmap createPixmapFromBase64String(String base64) {
        byte[] decodedBytes = Base64Coder.decode(base64);
        return new Pixmap(decodedBytes, 0, decodedBytes.length);
    }
}
