package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;

import javax.inject.Inject;

/**
 * Crayon particle brush.
 */
@PerDodleEngine
public class CrayonBrush extends AbstractParticleBrush {
    public static final String BRUSH_NAME = "crayon";
    private EditorState editorState;
    
    @Inject
    public CrayonBrush(AssetProvider assetProvider, BrushRegistry brushRegistry, CommandFactory commandFactory, EditorState editorState) {
        super(assetProvider, commandFactory);
        this.editorState = editorState;
        brushRegistry.registerBrush(this);
    }
    
    @Override
    public final String getName() {
        return BRUSH_NAME;
    }
    
    @Override
    public final int getOrder() {
        return 6;
    }

    @Override
    public final TextureRegion getActiveIcon() {
        return new TextureRegion(getBrushIconsTexture(), 209, 2, 103, 102);
    }

    @Override
    public final TextureRegion getInactiveIcon() {
        return new TextureRegion(getBrushIconsTexture(), 314, 1, 101, 103);
    }
    
    @Override
    protected final StrokeConfig getDefaultStrokeConfig() {
        StrokeConfig dsg = new StrokeConfig();
        dsg.setColor(editorState.getStrokeConfig().getColor());
        dsg.setSize(editorState.getStrokeConfig().getSize());
        dsg.setOpacity(editorState.getStrokeConfig().getOpacity());
        return dsg;
    }
    
    @Override
    protected final float getParticleSize() {
        return 2;
    }

    @Override
    protected final float getParticleDensity() {
        return 0.3f;
    }

    @Override
    protected final float getMinTransparency() {
        return 0.25f;
    }

    @Override
    protected final float getMaxTransparency() {
        return 0.75f;
    }

    @Override
    protected final float getStreakFactor() {
        return 0.8f;
    }
}
