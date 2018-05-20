package com.dodles.gdx.dodleengine.scenegraph.graphics.nanovg;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.DodlesSpriteBatch;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AbstractAtlasGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AtlasGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.gurella.engine.graphics.vector.AffineTransform;
import com.gurella.engine.graphics.vector.Canvas;
import static com.gurella.engine.graphics.vector.Canvas.obtain;
import com.gurella.engine.graphics.vector.CanvasFlags;
import com.gurella.engine.graphics.vector.FastPools;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders NanoVgGraphics commands using a framebuffer atlas.
 */
public class NanoVgAtlasGraphicsRenderer extends AbstractAtlasGraphicsRenderer<NanoVgGraphics> implements AtlasGraphicsRenderer {
    private static Canvas canvas;
    
    private ArrayList<NanoVgGraphics> graphics = new ArrayList<NanoVgGraphics>();
    private int graphicsIndex = 0;
    
    @Override
    public final void draw(DodlesSpriteBatch batch, float parentAlpha, Vector2 drawOffset) {
        draw(batch, parentAlpha, 0, drawOffset);
    }
    
    @Override
    public final void drawNew(DodlesSpriteBatch batch, float parentAlpha, Vector2 offsetPoint) {
        draw(batch, parentAlpha, graphicsIndex, offsetPoint);
    }
    
    @Override
    public final void appendGraphics(Graphics g) {
        graphics.add((NanoVgGraphics) g);
        dispose();
    }
    
    @Override
    protected final List<NanoVgGraphics> getGraphics() {
        return graphics;
    }
    
    @Override
    protected final int getGraphicsIndex() {
        return graphicsIndex;
    }

    @Override
    public final void commit() {
        graphicsIndex = graphics.size();
    }
    
    @Override
    public final void dispose() {
    }
    
    private void draw(DodlesSpriteBatch batch, float parentAlpha, int startIndex, Vector2 offsetPoint) {
        // TODO: can we chain together atlas draws?
        batch.end();
        
        if (canvas == null) {
            //canvas = obtain(FrameBufferAtlasManager.ATLAS_SIZE, FrameBufferAtlasManager.ATLAS_SIZE, CanvasFlags.CanvasFlag.debug, CanvasFlags.CanvasFlag.antiAlias, CanvasFlags.CanvasFlag.stencilStrokes);
            canvas = obtain(FrameBufferAtlasManager.ATLAS_SIZE, FrameBufferAtlasManager.ATLAS_SIZE, CanvasFlags.CanvasFlag.antiAlias, CanvasFlags.CanvasFlag.stencilStrokes);
        }
        
        canvas.clear();
        
        AffineTransform camera = FastPools.obtainAffineTransform();
        camera.set(1, 0, 0, -1, 0, FrameBufferAtlasManager.ATLAS_SIZE);
        camera.translate(offsetPoint.x, -offsetPoint.y);
        canvas.setTransform(camera);
        FastPools.free(camera);

        for (int i = startIndex; i < getGraphics().size(); i++) {
            getGraphics().get(i).draw(canvas);
        }

        canvas.render();
        batch.begin();
    }
}
