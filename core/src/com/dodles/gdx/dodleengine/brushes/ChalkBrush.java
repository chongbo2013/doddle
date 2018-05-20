package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;

import javax.inject.Inject;

/**
 * "Chalk" brush.
 */
@PerDodleEngine
public class ChalkBrush extends AbstractParticleBrush {
    public static final String BRUSH_NAME = "chalk";
    private EditorState editorState;
    
    @Inject
    public ChalkBrush(AssetProvider assetProvider, BrushRegistry brushRegistry, CommandFactory commandFactory, EditorState editorState) {
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
        return 7;
    }
    
    @Override
    public final TextureRegion getActiveIcon() {
        return new TextureRegion(getBrushIconsTexture(), 3, 2, 101, 102);
    }

    @Override
    public final TextureRegion getInactiveIcon() {
        return new TextureRegion(getBrushIconsTexture(), 106, 0, 102, 104);
    }
    
    
    @Override
    protected final float getParticleSize() {
        return 0.75f;
    }

    @Override
    protected final float getParticleDensity() {
        return 0.1f;
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
        return 0.7f;
    }

    @Override
    protected final StrokeConfig getDefaultStrokeConfig() {
        StrokeConfig dsg = new StrokeConfig();
        dsg.setColor(editorState.getStrokeConfig().getColor());
        dsg.setSize(editorState.getStrokeConfig().getSize());
        dsg.setOpacity(editorState.getStrokeConfig().getOpacity());
        return dsg;
    }
}
