package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.dodles.gdx.dodleengine.scenegraph.graphics.SpriteBatchRenderState;

/**
 * Extends the libgdx sprite batch to allow extra functionality we need for dodles.
 */
public class DodlesSpriteBatch extends SpriteBatch {
    private int srcRGB;
    private int dstRGB;
    private int srcAlpha;
    private int dstAlpha;
    private SpriteBatchRenderState renderState;
    
    public DodlesSpriteBatch() {
        super();
    }
    
    /**
     * Allows setting seperate blending functions for RGB and alpha.
     */
    public final void setBlendFunctionSeparate(int pSrcRGB, int pDstRGB, int pSrcAlpha, int pDstAlpha) {
        srcRGB = pSrcRGB;
        dstRGB = pDstRGB;
        srcAlpha = pSrcAlpha;
        dstAlpha = pDstAlpha;
        
        this.setBlendFunction(-1, -1);
    }
    
    /**
     * Returns the source RGB blending function.
     */
    public final int getSrcRGB() {
        return srcRGB;
    }
    
    /**
     * Returns the destination RGB blending function.
     */
    public final int getDstRGB() {
        return dstRGB;
    }
    
    /**
     * Returns the source alpha blending function.
     */
    public final int getSrcAlpha() {
        return srcAlpha;
    }
    
    /**
     * Returns the destination alpha blending function.
     */
    public final int getDstAlpha() {
        return dstAlpha;
    }
    
    @Override
    public final void flush() {
        finishState();
        
        if (this.getBlendSrcFunc() == -1) {
            Gdx.gl.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
        }
        
        super.flush();
    }
    
    @Override
    public final void end() {
        finishState();
        
        if (isDrawing()) {
            super.end();
        }
    }
    
    @Override
    public final void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        finishState();
        super.draw(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
    }
    
    @Override
    public final void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        finishState();
        super.draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
    }
    
    @Override
    public final void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        finishState();
        super.draw(texture, x, y, srcX, srcY, srcWidth, srcHeight);
    }
    
    @Override
    public final void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        finishState();
        super.draw(texture, x, y, width, height, u, v, u2, v2);
    }
    
    @Override
    public final void draw(Texture texture, float x, float y, float width, float height) {
        finishState();
        super.draw(texture, x, y, width, height);
    }
    
    @Override
    public final void draw(Texture texture, float[] spriteVertices, int offset, int count) {
        finishState();
        super.draw(texture, spriteVertices, offset, count);
    }
    
    @Override
    public final void draw(TextureRegion region, float x, float y, float width, float height) {
        finishState();
        super.draw(region, x, y, width, height);
    }
    
    @Override
    public final void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        finishState();
        super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }
    
    @Override
    public final void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        finishState();
        super.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation, clockwise);
    }
    
    @Override
    public final void draw(TextureRegion region, float width, float height, Affine2 transform) {
        finishState();
        super.draw(region, width, height, transform);
    }
    
    /**
     * If there's an open render state attached to this batch, finish it and clear it out.
     */
    public final void finishState() {
        if (renderState != null) {
            renderState.finish(this);
            renderState = null;
        }
    }
    
    /**
     * Returns the current render state for the batch.
     */
    public final SpriteBatchRenderState getState() {
        return renderState;
    }
    
    /**
     * Sets a new render state for the batch.
     */
    public final void setState(SpriteBatchRenderState pRenderState) {
        finishState();
        renderState = pRenderState;
    }
}
