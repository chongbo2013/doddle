package com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.DodlesSpriteBatch;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AbstractGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRenderer;
import com.gurella.engine.graphics.vector.AffineTransform;
import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.CanvasFlags;

import java.util.ArrayList;
import java.util.List;

import static com.gurella.engine.graphics.vector.Canvas.obtain;
import com.gurella.engine.graphics.vector.FastPools;

/**
 * Renders NanoVgGraphics commands directly to the screen.
 */
public class NanoVgDirectGraphicsRenderer extends AbstractGraphicsRenderer<NanoVgGraphics> implements GraphicsRenderer {
    private ArrayList<NanoVgGraphics> graphics = new ArrayList<NanoVgGraphics>();
    private static Canvas canvas;
    
    @Override
    public final void draw(DodlesSpriteBatch batch, float parentAlpha, Vector2 drawOffset) {
        if (batch.isDrawing()) {
            batch.end();
        }
        
        Matrix4 originalTransform = batch.getTransformMatrix();            
        Vector2 lastDrawOffset = null;

        if (batch.getState() instanceof NanoVgSpriteBatchRenderState) {
            lastDrawOffset = ((NanoVgSpriteBatchRenderState) batch.getState()).getDrawOffset();
        }

        if (lastDrawOffset == null || lastDrawOffset.dst(drawOffset) > 0.1f) {
            batch.finishState();
            
            if (batch.isDrawing()) {
                batch.end();
            }
            
            if (canvas == null || canvas.getWidth() != Gdx.graphics.getWidth() || canvas.getHeight() != Gdx.graphics.getHeight()) {
                if (canvas != null) {
                    canvas.dispose();
                }

                //canvas = obtain(CanvasFlags.CanvasFlag.debug, CanvasFlags.CanvasFlag.antiAlias, CanvasFlags.CanvasFlag.stencilStrokes);
                canvas = obtain(CanvasFlags.CanvasFlag.antiAlias, CanvasFlags.CanvasFlag.stencilStrokes);
            }
            
            batch.setState(new NanoVgSpriteBatchRenderState(canvas, drawOffset.cpy()));
            
            AffineTransform camera = FastPools.obtainAffineTransform();
            AffineTransform batchXform = FastPools.obtainAffineTransform();
            float[] otv = originalTransform.val;
            batchXform.set(otv[0], otv[1], otv[4], otv[5], otv[12], otv[13]);

            camera.translate(drawOffset.x, drawOffset.y);
            camera.mul(batchXform);

            canvas.setTransform(camera);

            FastPools.free(camera);
            FastPools.free(batchXform);
        }
        
        for (NanoVgGraphics curGraphics : graphics) {
            curGraphics.draw(canvas);
        }
    }
    
    @Override
    protected final List<NanoVgGraphics> getGraphics() {
        return graphics;
    }

    @Override
    public final void appendGraphics(Graphics g) {
        graphics.add((NanoVgGraphics) g);
    }

    @Override
    public final void dispose() {
    }
}
