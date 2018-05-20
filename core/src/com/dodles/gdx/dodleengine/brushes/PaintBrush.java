package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;

import javax.inject.Inject;

/**
 * "Paint" brush.
 */
@PerDodleEngine
public class PaintBrush extends AbstractLineBrush {
    public static final String BRUSH_NAME = "paint";
    private EditorState editorState;
    
    @Inject
    public PaintBrush(AssetProvider assetProvider, BrushRegistry brushRegistry, CommandFactory commandFactory, EditorState editorState) {
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
        return 3;
    }
    
    @Override
    public final TextureRegion getActiveIcon() {
        return new TextureRegion(getBrushIconsTexture(), 107, 123, 100, 100);
    }

    @Override
    public final TextureRegion getInactiveIcon() {
        return new TextureRegion(getBrushIconsTexture(), 211, 123, 100, 100);
    }

    @Override
    public final StrokeConfig getDefaultStrokeConfig() {
        StrokeConfig dsg = new StrokeConfig();
        dsg.setColor(editorState.getStrokeConfig().getColor());
        dsg.setSize(3f);
        return dsg;
    }
}
