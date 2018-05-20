package com.dodles.gdx.dodleengine.scenegraph.graphics.direct;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.DodlesSpriteBatch;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AbstractAtlasGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AtlasGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders commands direct to the batch.
 */
public class DirectGraphicsRenderer extends AbstractAtlasGraphicsRenderer<DirectGraphics> implements AtlasGraphicsRenderer {
    private ArrayList<DirectGraphics> graphics = new ArrayList<DirectGraphics>();
    private int graphicsIndex = 0;

    @Override
    public final void draw(DodlesSpriteBatch batch, float parentAlpha, Vector2 drawOffset) {
        draw(batch, parentAlpha, 0, drawOffset);
    }
    
    @Override
    public final void drawNew(DodlesSpriteBatch batch, float parentAlpha, Vector2 offset) {
        draw(batch, parentAlpha, graphicsIndex, offset);
    }
    
    @Override
    public final void appendGraphics(Graphics g) {
        graphics.add((DirectGraphics) g);
    }

    @Override
    public final void commit() {
        graphicsIndex = graphics.size();
    }

    @Override
    public final void dispose() {
    }
    
    @Override
    protected final int getGraphicsIndex() {
        return graphicsIndex;
    }

    @Override
    protected final List<DirectGraphics> getGraphics() {
        return graphics;
    }
    
    private void draw(DodlesSpriteBatch batch, float parentAlpha, int startIndex, Vector2 offsetPoint) {
        batch.finishState();
        
        Matrix4 originalTransform = batch.getTransformMatrix().cpy();
        batch.setTransformMatrix(batch.getTransformMatrix().translate(offsetPoint.x, offsetPoint.y, 0));
        
        for (int i = startIndex; i < getGraphics().size(); i++) {
            getGraphics().get(i).draw(batch, parentAlpha);
        }
        
        batch.setTransformMatrix(originalTransform);
    }
}
