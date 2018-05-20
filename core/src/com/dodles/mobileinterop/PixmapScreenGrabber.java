package com.dodles.mobileinterop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.util.ScreenGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.inject.Inject;

/**
 * Pixmap screengrabber implementation.
 */
@PerDodleEngine
public class PixmapScreenGrabber implements ScreenGrabber {
    @Inject
    public PixmapScreenGrabber() {
    }
    
    @Override
    public final String takeBase64Screenshot(Rectangle cropBounds) {
        int y = (int) (Gdx.graphics.getBackBufferHeight() - cropBounds.height - cropBounds.y);
        byte[] pixels = ScreenUtils.getFrameBufferPixels((int) cropBounds.x, y, (int) cropBounds.width, (int) cropBounds.height, false);

        Pixmap pixmap = new Pixmap((int) cropBounds.width, (int) cropBounds.height, Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        
        PixmapIO.PNG pngWriter = new PixmapIO.PNG();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            pngWriter.write(baos, pixmap);
            pngWriter.dispose();
            pixmap.dispose();
        } catch (IOException ioe) {
            throw new GdxRuntimeException("couldn't take screenshot :(");
        }
        
        return new String(Base64Coder.encode(baos.toByteArray()));
    }
}
