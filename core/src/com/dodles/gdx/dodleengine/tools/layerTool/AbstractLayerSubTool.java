package com.dodles.gdx.dodleengine.tools.layerTool;

import com.badlogic.gdx.graphics.Texture;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.tools.AbstractTool;

/**
 * Common layerTool functionality.
 */
public abstract class AbstractLayerSubTool extends AbstractTool {
    private final AssetProvider assetProvider;

    public AbstractLayerSubTool(AssetProvider assetProvider) {
        super(assetProvider);
        this.assetProvider = assetProvider;
    }

    /**
     * Returns the layerTool bar icons texture.
     */
    @Override
    protected final Texture getToolBarIconsTexture() {
        return assetProvider.getTexture(TextureAssets.EDITOR_LAYER_TOOL_TOOLBARICONS);
    }
}
