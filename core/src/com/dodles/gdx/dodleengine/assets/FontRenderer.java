package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;

/**
 * Wraps fonts to provide a usable interface and hide the fact that we've only
 * really got one font behind the scenes.
 */
public class FontRenderer {
    private static final int MAX_FONT_SIZE = 115;
    private BitmapFont font;
    private BitmapFontCache cachedText;
    private Color color = Color.BLACK;
    private float fontSize = 20;
    private String text;
    private float x;
    private float y;
    private boolean flipY;
    private Rectangle bounds;
    
    public FontRenderer(BitmapFont bmpFont) {
        this.font = bmpFont;
    }
    
    /**
     * Calculates the scale that the font should be at for the given font size.
     */
    public static float calculateFontScale(float fontSize) {
        return (float) fontSize / (float) MAX_FONT_SIZE;
    }
    
    /**
     * Returns the color of the font.
     */
    public final Color getColor() {
        return color;
    }
    
    /**
     * Sets the color of the font.
     */
    public final void setColor(Color newColor) {
        color = newColor;
        updateCache();
    }
    
    /**
     * Returns the size of the font.
     */
    public final float getFontSize() {
        return fontSize;
    }
    
    /**
     * Returns the X coordinate that the font will be rendered at.
     * @return 
     */
    public final float getX() {
        return x;
    }
    
    /**
     * Returns the Y coordinate that the font will be rendered at.
     */
    public final float getY() {
        return y;
    }
    
    /**
     * Returns a value indicating whether the font will be rendered flipped on the Y axis.
     */
    public final boolean getFlipY() {
        return flipY;
    }
    
    /**
     * Sets whether the font will be rendered flipped on the Y axis.
     */
    public final void setFlipY(boolean newFlipY) {
        flipY = newFlipY;
    }
    
    /**
     * Sets the size of the font.
     */
    public final void setFontSize(float newFontSize) {
        fontSize = newFontSize;
        updateCache();
    }
    
    /**
     * Returns the boundaries of the rendered text.
     */
    public final Rectangle getDrawBounds() {
        return bounds;
    }
    
    /**
     * Sets the text to render.
     */
    public final void setText(String newText, float newX, float newY) {
        if (newText == null) {
            cachedText = null;
            bounds = null;
        } else {
            cachedText = new BitmapFontCache(font, false);
            text = newText;
            x = newX;
            y = newY;
            updateCache();
        }
    }
    
    /**
     * Renders the font to the batch.
     */
    public final void draw(Batch batch) {
        if (cachedText != null) {
            cachedText.draw(batch);
        }
    }
    
    /**
     * Updates the cached font glyphs.
     */
    private void updateCache() {        
        if (cachedText != null) {
            float xScale = (calculateFontScale(fontSize));
            float yScale = xScale;
            
            if (flipY) {
                yScale = -yScale;
            }
            
            font.getData().setScale(xScale, yScale);
            cachedText.setColor(color);
            cachedText.setText(text, x, y);
            
            float height = cachedText.getLayouts().get(0).height;
            float width = 0;

            if (flipY) {
                height = -height;
            }

            for (GlyphLayout layout : cachedText.getLayouts()) {
                width += layout.width;
            }

            bounds = new Rectangle(x, y - height / 3, width, height * 1.666666f);
        }
    }
}
