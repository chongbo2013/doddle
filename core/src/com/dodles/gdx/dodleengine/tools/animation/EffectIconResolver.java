package com.dodles.gdx.dodleengine.tools.animation;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.EffectType;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import javax.inject.Inject;

/**
 * Central location for spaghetti code to resolve effect icons.
 */
@PerDodleEngine
public class EffectIconResolver {
    private final AssetProvider assetProvider;

    @Inject
    public EffectIconResolver(AssetProvider assetProvider) {
        this.assetProvider = assetProvider;
    }
    
    /**
     * Returns the icon for the given effect type and name.
     */
    public final AtlasRegion getIcon(EffectType effectType, String effectName) {
        TextureAtlas atlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);
        
        switch (effectType) {
            case ANGLE:
            case EMOTION:
                atlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_PHASE_ICONS_ATLAS);
                break;
                
            default:
                break;
        }
        
        return atlas.findRegion(effectType.name() + "_" + effectName);
    }
}
