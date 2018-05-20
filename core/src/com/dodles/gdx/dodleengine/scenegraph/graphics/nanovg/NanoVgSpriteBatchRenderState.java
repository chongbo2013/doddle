package com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.DodlesSpriteBatch;
import com.dodles.gdx.dodleengine.scenegraph.graphics.SpriteBatchRenderState;
import com.gurella.engine.graphics.vector.Canvas;

/**
 * Render state for NanoVG rendering.
 */
public class NanoVgSpriteBatchRenderState implements SpriteBatchRenderState {
    private Canvas canvas;
    private Vector2 drawOffset;
    
    public NanoVgSpriteBatchRenderState(Canvas canvas, Vector2 drawOffset) {
        this.canvas = canvas;
        this.drawOffset = drawOffset;
    }
    
    /**
     * Returns the draw offset of the current drawing operation.
     */
    public final Vector2 getDrawOffset() {
        return drawOffset;
    }
    
    @Override
    public final void finish(DodlesSpriteBatch batch) {
        canvas.render();
        canvas.clear();
        batch.begin();
    }
}
