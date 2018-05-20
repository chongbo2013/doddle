package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Defines all bitmap assets in the dodle engine.
 */
public enum BitmapFontAssets implements LoadableAsset {
    ARIAL("fonts/arial_115.fnt", "Arial"),
    ARIAL_BLACK("fonts/arial_black_115.fnt", "Arial Black"),
    COMIC_SANS("fonts/comic_sans_ms_115.fnt", "Comic Sans"),
    COURIER_NEW("fonts/courier_new_115.fnt", "Courier New"),
    GEORGIA("fonts/georgia_115.fnt", "Georgia"),
    IMPACT("fonts/impact_115.fnt", "Impact"),
    LUCIDA_CONSOLE("fonts/lucida_console_115.fnt", "Lucida Console"),
    LUCIDA_SANS("fonts/lucida_sans_115.fnt", "Lucida Sans"),
    PALATINO_LINOTYPE("fonts/palatino_linotype_115.fnt", "Palatino Linotype"),
    TAHOMA("fonts/tahoma_115.fnt", "Tahoma"),
    TIMES_NEW_ROMAN("fonts/times_new_roman_115.fnt", "Times New Roman"),
    TREBUCHET("fonts/trebuchet_ms_115.fnt", "Trebuchet"),
    VERDANA("fonts/verdana_115.fnt", "Verdana");

    private final String path;
    private final String displayName;
    
    BitmapFontAssets(String path, String displayName) {
        this.path = path;
        this.displayName = displayName;
    }
    
    /**
     * Returns the display name of the font.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Returns the asset materialized as a bitmap font.
     */
    public BitmapFont getFont(AssetManager manager) {
        BitmapFont bmpFont = manager.get(path, BitmapFont.class);
        Texture fontTexture = bmpFont.getRegion().getTexture();
        
        if (fontTexture.getMagFilter() != Texture.TextureFilter.Linear || fontTexture.getMinFilter() != Texture.TextureFilter.Linear) {
            fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        
        return bmpFont;
    }

    @Override
    public void load(AssetManager manager) {
        manager.load(path, BitmapFont.class);
    }
}
