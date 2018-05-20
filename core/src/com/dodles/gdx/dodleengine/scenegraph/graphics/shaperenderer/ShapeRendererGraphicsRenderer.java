package com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.DodlesSpriteBatch;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AbstractAtlasGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AtlasGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders ShapeRendererGraphics commands.
 */
public class ShapeRendererGraphicsRenderer extends AbstractAtlasGraphicsRenderer<ShapeRendererGraphics> implements AtlasGraphicsRenderer {
    private ArrayList<ShapeRendererGraphics> graphics = new ArrayList<ShapeRendererGraphics>();
    private int graphicsIndex = 0;
    private ShapeRenderer shapeRenderer;

    @Override
    public final void draw(DodlesSpriteBatch batch, float parentAlpha, Vector2 drawOffset) {
        draw(batch, parentAlpha, 0, drawOffset);
    }
    
    @Override
    public final void drawNew(DodlesSpriteBatch batch, float parentAlpha, Vector2 offsetPoint) {
        draw(batch, parentAlpha, graphicsIndex, offsetPoint);
    }
    
    @Override
    public final void commit() {
        graphicsIndex = graphics.size();
    }
    
    @Override
    public final void appendGraphics(Graphics g) {
        graphics.add((ShapeRendererGraphics) g);
    }
    
    @Override
    protected final List<ShapeRendererGraphics> getGraphics() {
        return graphics;
    }

    @Override
    public final void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
    }
    
    @Override
    protected final int getGraphicsIndex() {
        return graphicsIndex;
    }
    
    private void draw(DodlesSpriteBatch batch, float parentAlpha, int startIndex, Vector2 offsetPoint) {
        batch.end();
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        
        if (batch.getBlendSrcFunc() >= 0) {
            Gdx.gl.glBlendFunc(batch.getBlendSrcFunc(), batch.getBlendDstFunc());
        } else {
            DodlesSpriteBatch dsb = (DodlesSpriteBatch) batch;
            Gdx.gl.glBlendFuncSeparate(dsb.getSrcRGB(), dsb.getDstRGB(), dsb.getSrcAlpha(), dsb.getDstAlpha());
        }
        
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix().cpy().translate(offsetPoint.x, offsetPoint.y, 0));
        // TODO: How are we going to group graphics calls together with different shape types?
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (int i = startIndex; i < getGraphics().size(); i++) {
            getGraphics().get(i).draw(shapeRenderer);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }
}
