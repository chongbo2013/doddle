package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.zindex;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;
import javax.inject.Inject;

/**
 * Tool that sends the selected actor down.
 */
@PerDodleEngine
public class ZIndexDownLayerSubTool extends AbstractZIndexLayerSubTool implements LayerSubTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".Backward";

    @Inject
    public ZIndexDownLayerSubTool(AssetProvider assetProvider, CommandFactory commandFactory, CommandManager commandManager, LayerSubToolRegistry layerSubToolRegistry, ObjectManager objectManager) {
        super(assetProvider, commandFactory, commandManager, layerSubToolRegistry, objectManager, false);
    }

    @Override
    public final String getButtonStyleName() {
        return null;
    }
    
    @Override
    public final int getRow() {
        return 2;
    }

    @Override
    public final int getOrder() {
        return 3;
    }

    @Override
    public final String getName() {
        return TOOL_NAME;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 49, 144, 45, 46);
    }
}