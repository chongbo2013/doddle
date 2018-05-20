package com.dodles.gdx.dodleengine.scenegraph.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.scenegraph.DodlesSpriteBatch;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasSlice.FrameBufferConsumer;

import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;

/**
 * Manages writing to frame buffer atlases.
 */
@PerDodleEngine
public class FrameBufferAtlasManager {
    public static final int ATLAS_SIZE = 2048;
    public static final int BORDER = 1;
    public static final int PAGE_SIZE = 128;
    public static final int PAGES_PER_ROW = ATLAS_SIZE / PAGE_SIZE;
    public static final int PAGES_PER_ATLAS = PAGES_PER_ROW * PAGES_PER_ROW;
    
    private ArrayList<FrameBuffer> atlases = new ArrayList<FrameBuffer>();
    private HashMap<String, HashMap<String, FrameBufferAtlasSlice>> slices = new HashMap<String, HashMap<String, FrameBufferAtlasSlice>>();
    private ArrayList<FrameBufferAtlasSlice> freeSlices = new ArrayList<FrameBufferAtlasSlice>();
    private int curAtlas = 0;
    private int curSlice = 0;
    private DodlesSpriteBatch spriteBatch;
    
    @Inject
    public FrameBufferAtlasManager() {
    }
    
    /**
     * Returns the slice for the given actor and offset.
     */
    public final FrameBufferAtlasSlice getAtlasSlice(String actorID, AtlasOffset offset) {
        if (actorID == null) {
            throw new GdxRuntimeException("Actor ID must not be null!");
        }
        
        HashMap<String, FrameBufferAtlasSlice> actorSlices = slices.get(actorID);

        if (actorSlices != null) {
            return actorSlices.get(offset.getKey());
        }

        return null;
    }
    
    /**
     * Returns the slice for the given actor and offset if it exists, otherwise creates a new slice.
     */
    public final FrameBufferAtlasSlice getOrCreateAtlasSlice(String actorID, AtlasOffset offset, final Color color) {
        FrameBufferAtlasSlice slice = getAtlasSlice(actorID, offset);
        
        if (slice == null) {
            if (freeSlices.isEmpty()) {                
                if (atlases.size() == curAtlas) {
                    FrameBuffer newBuffer = new FrameBuffer(Format.RGBA8888, ATLAS_SIZE, ATLAS_SIZE, false, true);
                    newBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                    newBuffer.getColorBufferTexture().setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
                    atlases.add(newBuffer);
                }

                FrameBuffer atlas = atlases.get(curAtlas);
                slice = new FrameBufferAtlasSlice(atlas, curSlice);

                curSlice++;

                if (curSlice == PAGES_PER_ATLAS) {
                    curAtlas++;
                    curSlice = 0;
                }
            } else {
                slice = freeSlices.remove(0);
            }
            
            clearSlice(slice, color);
            addSlice(actorID, offset, slice);
        }
        
        return slice;
    }
    
    /**
     * Returns the slice to the pool so it can be used for another drawing.
     */
    public final void freeSlice(String actorID, AtlasOffset offset) {
        FrameBufferAtlasSlice slice = removeSlice(actorID, offset);
        
        if (slice != null) {
            freeSlices.add(slice);
        }
    }
    
    /**
     * Returns the shared spritebatch to use for rendering framebuffer tiles.
     */
    public final DodlesSpriteBatch getSharedSpriteBatch() {
        if (spriteBatch == null) {
            spriteBatch = new DodlesSpriteBatch();
            spriteBatch.enableBlending();
            
            // We need to use seperate blending functions here when drawing to the framebuffer.
            // Just using normal src alpha/ 1 - src alpha causes overwrites instead of blending
            // when drawing to the framebuffer.  I still don't quite understand why. :/
            spriteBatch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
        }
        
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, FrameBufferAtlasManager.ATLAS_SIZE, FrameBufferAtlasManager.ATLAS_SIZE);
        
        return spriteBatch;
    }
    
    /**
     * Resets the atlases.
     */
    public final void reset() {
        for (FrameBuffer atlas : atlases) {
            atlas.dispose();
        }
        
        if (spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
        
        atlases.clear();
        slices.clear();
        freeSlices.clear();
        curAtlas = 0;
        curSlice = 0;
    }

    private void addSlice(String actorID, AtlasOffset offset, FrameBufferAtlasSlice slice) {
        HashMap<String, FrameBufferAtlasSlice> actorSlices = slices.get(actorID);

        if (actorSlices == null) {
            actorSlices = new HashMap<String, FrameBufferAtlasSlice>();
            slices.put(actorID, actorSlices);
        }

        actorSlices.put(offset.getKey(), slice);
    }

    private FrameBufferAtlasSlice removeSlice(String actorID, AtlasOffset offset) {
        HashMap<String, FrameBufferAtlasSlice> actorSlices = slices.get(actorID);

        if (actorSlices != null) {
            return actorSlices.remove(offset.getKey());
        }

        return null;
    }
    
    private void clearSlice(final FrameBufferAtlasSlice slice, final Color color) {
        slice.drawToSlice(new FrameBufferConsumer() {
            @Override
            public void draw(FrameBuffer buffer) {
                Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
                Gdx.gl.glScissor((int) slice.getAtlasOffsetPoint().x, (int) slice.getAtlasOffsetPoint().y, PAGE_SIZE, PAGE_SIZE);
                Gdx.gl.glClearColor(color.r, color.g, color.b, 0f);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
                Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
            }
        });
    }
}
