package com.dodles.gdx.dodleengine.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesGroup;

/**
 * A widget that renders a DodlesActor within it's bounds.
 */
public class ActorPreviewWidget extends Widget {
    private final BaseDodlesGroup actor;
    private final Color bgColor;
    
    private Table expandoTable = null;
    private ShapeRenderer shapeRenderer;
    private float expandoProgress = 0;
    
    public ActorPreviewWidget(BaseDodlesGroup actor, boolean allowExpandoMode) {
        this(actor, allowExpandoMode, null);
    }
    
    public ActorPreviewWidget(BaseDodlesGroup actor, boolean allowExpandoMode, Color bgColor) {
        this.bgColor = bgColor;
        this.actor = actor;
        
        if (allowExpandoMode) {
            this.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return handleTouchDown(x, y);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    handleTouchUp();
                }
            });
        }
    }
    
    @Override
    public final void act(float delta) {
        if (expandoTable != null) {
            if (expandoProgress < 1) {
                expandoProgress += delta;
            }

            if (expandoProgress > 1) {
                expandoProgress = 1;
            }

            setExpandoPosition();
        }
    }
    
    @Override
    public final void draw(Batch batch, float parentAlpha) {
        if (bgColor != null) {
            batch.end();
        
            if (shapeRenderer == null) {
                shapeRenderer = new ShapeRenderer();
            }

            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            shapeRenderer.setColor(bgColor);
            shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
            
            shapeRenderer.end();
            batch.begin();
        }
        
        Vector3 batchTranslation = new Vector3();
        batch.getTransformMatrix().getTranslation(batchTranslation);
        
        boolean prevVisible = actor.isVisible();
        actor.setVisible(true);
        
        // Invert to y-down coordinate system
        Affine2 transform = new Affine2();
        transform.scale(1, -1);
        transform.translate(0, -Gdx.graphics.getHeight());
        
        // Apply any batch translation to our transform
        transform.translate(batchTranslation.x, Gdx.graphics.getHeight() - batchTranslation.y);
        
        // position the widget appropriately
        transform.translate(getX(), -getY() - getHeight());
        
        // transform according to drawbounds
        Rectangle drawBounds = actor.getDrawBounds();
        float widthRatio = getWidth() / drawBounds.width;
        float heightRatio = getHeight() / drawBounds.height;
        float scale = Math.min(widthRatio, heightRatio);
        float xAdj = (getWidth() - drawBounds.width * scale) / 2;
        float yAdj = (getHeight() - drawBounds.height * scale) / 2;

        transform.translate(xAdj, yAdj);
        transform.scale(scale, scale);  
        transform.translate(-drawBounds.x, -drawBounds.y);
        
        actor.setForceTransform(true);
        actor.setWorldTransformOverride(transform);

        actor.draw(batch, parentAlpha);
        
        // Reset actor to normal
        actor.setVisible(prevVisible);        
        actor.setWorldTransformOverride(null);
        actor.setForceTransform(false);
    }
    
    private boolean handleTouchDown(float x, float y) {
        if (x <= 0 || x >= getWidth() || y <= 0 || y >= getHeight()) {
            return false;
        }
        
        expandoProgress = 0;
        expandoTable = new Table();
        expandoTable.add(new ActorPreviewWidget(actor, false, Color.WHITE)).expand().fill();
        getStage().addActor(this.expandoTable);
        
        setExpandoPosition();
        
        return true;
    }
    
    private void handleTouchUp() {
        this.expandoTable.remove();
        this.expandoTable = null;
    }
    
    private void setExpandoPosition() {                
        float easedProgress = Interpolation.pow3Out.apply(expandoProgress);
        Vector2 topLeft = this.localToStageCoordinates(new Vector2(getX(), getY()));
        topLeft.x = getX(); // I am extremely confused as to why we need to calculate Y but not X...
        float x = topLeft.x - topLeft.x * easedProgress;
        float y = topLeft.y - topLeft.y * easedProgress;
        expandoTable.setPosition(x, y);
        
        float width = (Gdx.graphics.getWidth() - getWidth()) * easedProgress + getWidth();
        float height = (Gdx.graphics.getHeight() - getHeight()) * easedProgress + getHeight();
        expandoTable.setSize(width, height);
    }
}
