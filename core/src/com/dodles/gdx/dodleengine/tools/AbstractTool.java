package com.dodles.gdx.dodleengine.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;

/**
 * Common tool functionality.
 */
public abstract class AbstractTool implements Comparable<Tool> {
    private final AssetProvider assetProvider;
    
    public AbstractTool(AssetProvider assetProvider) {
        this.assetProvider = assetProvider;
    }
    
    /**
     * Returns the default row of the tool in the tool interface.
     */
    public abstract int getRow();
    
    /**
     * Returns the order of the tool in it's row.
     */
    public abstract int getOrder();
  
    /**
     * Implements Comparable<Tool>.
     */
    public final int compareTo(Tool otherTool) {
        Integer thisOrder = new Integer(getOrder());
        Integer thatOrder = new Integer(otherTool.getOrder());
        
        return thisOrder.compareTo(thatOrder);
    }

    /**
     * Returns the active asset provider.
     */
    protected final AssetProvider getAssetProvider() {
        return assetProvider;
    }

    /**
     * Returns the tool bar icons texture.
     */
    // CHECKSTYLE.OFF: DesignForExtension // Turned off because we want to be able to extend this class anyway for the layertools
    protected Texture getToolBarIconsTexture() {
        return assetProvider.getTexture(TextureAssets.EDITOR_TOOL_TOOLBARICONS);
    }
    // CHECKSTYLE.ON: DesignForExtension

    /**
     * Returns the tool bar icons texture atlas.
     */
    // CHECKSTYLE.OFF: DesignForExtension // Turned off because we want to be able to extend this class anyway for the layertools
    protected TextureAtlas getToolBarIconsTextureAtlas() {
        return assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_TOOLBARICONS_ATLAS);
    }
    // CHECKSTYLE.ON: DesignForExtension
}
