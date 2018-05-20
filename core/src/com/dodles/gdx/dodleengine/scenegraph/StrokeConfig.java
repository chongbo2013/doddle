package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.assets.BitmapFontAssets;

/**
 * Engine "stroke" configuration.
 */
public class StrokeConfig {
    private Color color = new Color(0f, 0f, 0f, 1f);
    private Color fill = new Color(0f, 0f, 0f, 1f);
    private float size = 20;
    private BitmapFontAssets font = BitmapFontAssets.ARIAL;
    
    public StrokeConfig() {
    }
    
    public StrokeConfig(JsonValue json) {
        if (json.has(StrokeConfigKey.SIZE.get())) {
            size = json.getInt(StrokeConfigKey.SIZE.get());
        }
        
        try {
            font = BitmapFontAssets.valueOf(json.getString(StrokeConfigKey.FONT.get()));
        } catch (IllegalArgumentException iae) {
            font = BitmapFontAssets.ARIAL;
        }

        if (json.has(StrokeConfigKey.COLOR.get())) {
            color = Color.valueOf(json.getString(StrokeConfigKey.COLOR.get()).substring(1, 7) + "FF");
            color.a = json.getFloat(StrokeConfigKey.OPACITY.get());

            if (color.a > 1) {
                color.a = 1;
            }
        }
        
        if (json.has(StrokeConfigKey.FILL.get())) {
            fill = Color.valueOf(json.getString(StrokeConfigKey.FILL.get()).substring(1, 7) + "FF");
            fill.a = json.getFloat(StrokeConfigKey.OPACITY.get());

            if (fill.a > 1) {
                fill.a = 1;
            }
        }
    }
    
    /**
     * Returns the color of the stroke (INCLUDING OPACITY!).
     */
    public final Color getColor() {
        return color;
    }
    
    /**
     * Sets the color of the stroke.
     */
    public final void setColor(Color newColor) {
        color.r = newColor.r;
        color.g = newColor.g;
        color.b = newColor.b;
        color.a = newColor.a;
    }

    /**
     * Returns the color of the stroke (INCLUDING OPACITY!).
     */
    public final Color getFill() {
        return fill;
    }

    /**
     * Sets the color of the stroke.
     */
    public final void setFill(Color newColor) {
        fill.r = newColor.r;
        fill.g = newColor.g;
        fill.b = newColor.b;
        fill.a = newColor.a;
    }
    
    /**
     * Returns the opacity component of the color.
     */
    public final float getOpacity() {
        return color.a;
    }
    
    /**
     * Sets the opacity component of the color.
     */
    public final void setOpacity(float opacity) {
        color.a = opacity;
        fill.a = color.a;
    }
    
    /**
     * Returns the size of the stroke.
     */
    public final float getSize() {
        return size;
    }
    
    /**
     * Sets the size of the stroke.
     */
    public final void setSize(float size) {
        this.size = size;
    }
    
    /**
     * Returns the name of the current font.
     */
    public final BitmapFontAssets getFont() {
        return font;
    }
    
    /**
     * Sets the name of the current font.
     */
    public final void setFont(BitmapFontAssets newFont) {
        font = newFont;
    }
    
    /**
     * Writes the stroke configuration to the json document.
     */
    public final void writeConfig(Json json) {
        json.writeValue(StrokeConfigKey.COLOR.get(), "#" + color.toString().substring(0, 6));
        json.writeValue(StrokeConfigKey.FILL.get(), "#" + fill.toString().substring(0, 6));
        json.writeValue(StrokeConfigKey.SIZE.get(), size);
        json.writeValue(StrokeConfigKey.OPACITY.get(), color.a);
        json.writeValue(StrokeConfigKey.FONT.get(), font.name());
    }
    
    /**
     * Creates a copy of the strokeconfig.
     */
    public final StrokeConfig cpy() {
        StrokeConfig result = new StrokeConfig();
        result.color = color.cpy();
        result.fill = fill.cpy();
        result.size = size;
        result.font = font;
        return result;
    }
}
