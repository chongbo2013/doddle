package com.dodles.gdx.dodleengine.scenegraph.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;

/**
 * A single slice of the pixmap atlas.
 */
public class FrameBufferAtlasSlice {
    private final FrameBuffer atlas;
    private int sliceNum;
    private TextureRegion region;

    public FrameBufferAtlasSlice(FrameBuffer atlas, int sliceNum) {
        this.atlas = atlas;
        this.sliceNum = sliceNum;
        
        Vector2 atlasOffset = getAtlasOffsetPoint();

        region = new TextureRegion(atlas.getColorBufferTexture(), (int) atlasOffset.x + FrameBufferAtlasManager.BORDER, (int) atlasOffset.y + FrameBufferAtlasManager.BORDER, AtlasOffset.TILE_SIZE, AtlasOffset.TILE_SIZE);
        region.flip(false, true);
    }

    /**
     * Returns the full atlas.
     */
    public final void drawToSlice(FrameBufferConsumer consumer) {
        FrameBufferStack.instance().begin(atlas);
        consumer.draw(atlas);
        FrameBufferStack.instance().end();
    }
    
    /**
     * Returns the region containing the slice in the atlas.
     */
    public final TextureRegion getRegion() {
        return region;
    }

    /**
     * Returns the point in the atlas where this slice is located.
     */
    public final Vector2 getAtlasOffsetPoint() {
        int column = sliceNum % FrameBufferAtlasManager.PAGES_PER_ROW;
        int row = sliceNum / FrameBufferAtlasManager.PAGES_PER_ROW;
        return new Vector2(column * FrameBufferAtlasManager.PAGE_SIZE, row * FrameBufferAtlasManager.PAGE_SIZE);
    }
    
    /**
     * Interface implementation necessary to draw to slices.
     */
    public interface FrameBufferConsumer {
        /**
         * Draw to the slice using the given framebuffer.
         */
        void draw(FrameBuffer buffer);
    }
}
