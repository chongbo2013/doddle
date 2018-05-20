package com.dodles.gdx.dodleengine.scenegraph.graphics;

import com.badlogic.gdx.math.Vector2;

/**
 * Describes the offset to draw in an atlas at that can be stored in a hash collection for easy access.
 */
public class AtlasOffset {
    public static final int TILE_SIZE = FrameBufferAtlasManager.PAGE_SIZE - FrameBufferAtlasManager.BORDER * 2;
    public static final int RENDERED_SIZE = FrameBufferAtlasManager.PAGE_SIZE - FrameBufferAtlasManager.BORDER * 2;
    
    private int x;
    private int y;
    private String key;

    
    public AtlasOffset(Vector2 point) {
        this(point.x, point.y);
    }

    public AtlasOffset(float dodleX, float dodleY) {
        float tempX = dodleX / (float) RENDERED_SIZE;
        float tempY = dodleY / (float) RENDERED_SIZE;
        
        x = (int) tempX;
        y = (int) tempY;
        
        if (tempX < 0) {
            x--;
        }
        
        if (tempY < 0) {
            y--;
        }
    }

    /**
     * Returns the x offset of the start of the pixmap in dodle space.
     */
    public final float getXOffset() {
        return x * RENDERED_SIZE;
    }
    
    /**
     * Returns the X offset of the pixmap.
     */
    public final int getX() {
        return x;
    }
    
    /**
     * Returns the Y offset of the pixmap.
     */
    public final int getY() {
        return y;
    }

    /**
     * Returns a String key for this offset to use in hashmaps.
     */
    public final String getKey() {
        if (key == null) {
            key = "" + x + y;
        }
        
        return key;
    }

    /**
     * Returns the y offset of the start of the pixmap in dodle space.
     */
    public final float getYOffset() {
        return y * RENDERED_SIZE;
    }
    
    /**
     * Returns the ratio of pixmap size to rendered size.
     */
    public final float getRatio() {
        return TILE_SIZE / RENDERED_SIZE;
    }
    
    /**
     * Returns the X coordinate in the pixmap that maps to the X coordinate in dodle space.
     */
    public final float getRenderX(float dodleX) {
        return (dodleX - getXOffset()) * getRatio();
    }
    
    /**
     * Returns the Y coordinate in the pixmap that maps to the Y coordinate in dodle space.
     */
    public final float getRenderY(float dodleY) {
        return (dodleY - getYOffset()) * getRatio();
    }
    
    @Override
    public final int hashCode() {
        return x ^ y;
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof AtlasOffset)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        AtlasOffset other = (AtlasOffset) obj;

        return x == other.x && y == other.y;
    }
}
