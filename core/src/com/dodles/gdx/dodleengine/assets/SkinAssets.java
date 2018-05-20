package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.kotcrab.vis.ui.VisUI;

/**
 * Defines all skin assets in the dodle engine.
 */
public enum SkinAssets implements LoadableAsset {
    // TODO: remove EDITOR_UI_SKIN, EDITOR_UI_HEADER_SKIN, UI_SKIN
    EDITOR_UI_SKIN("editor-ui/editor-ui.json", "editor-ui/editor-ui.atlas"),
    EDITOR_UI_HEADER_SKIN("editor-ui-header/header.json", "editor-ui-header/header.atlas"),
    UI_SKIN("skin/uiskin.json", "skin/uiskin.atlas"),
    UI("editor/ui/ui.json", "editor/ui/ui.atlas"),
    DEMO("demo/demo.json", "demo/demo.atlas");
    
    private final String path;
    private final String atlas;
    
    SkinAssets(String path, String atlas) {
        this.path = path;
        this.atlas = atlas;
    }
    
    /**
     * Returns the asset materialized as a skin.
     */
    public Skin getSkin(AssetManager manager) {
        Skin skin = manager.get(path, Skin.class);
        
        if (!VisUI.isLoaded()) {
            VisUI.load(skin);

            Class<BitmapFont> bitmapFontClass;
            ObjectMap<String, BitmapFont> fontObjectMap = null;
            try {
                bitmapFontClass = (Class<BitmapFont>) Class.forName("com.badlogic.gdx.graphics.g2d.BitmapFont");
                fontObjectMap = skin.getAll(bitmapFontClass);
            } catch (Exception ignored) {}
            if (fontObjectMap != null) {
                for (String fontName : fontObjectMap.keys()) {
                    BitmapFont font = skin.getFont(fontName);
                    font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                    font.getData().setScale(font.getData().scaleX * DensityManager.getDensity().getScale());
                }
            }
        }
        
        return skin;
    }

    @Override
    public void load(AssetManager manager) {
        manager.load(path, Skin.class, new SkinLoader.SkinParameter(atlas));
    }
}
