package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Defines all texture assets in the dodle engine.
 */
public enum TextureAtlasAssets implements LoadableAsset {
    EDITOR_TOOL_ANIMATIONICONS_ATLAS("editor/tool/animation/animationicons.atlas"),
    EDITOR_TOOL_TOOLBARICONS_ATLAS("editor/tool/toolbaricons2.atlas"),
    EDITOR_HEADER_ICONS_ATLAS("editor/headericons2.atlas"),
    EDITOR_PHASE_ICONS_ATLAS("editor/phaseicons.atlas"),
    UI_SKIN_ATLAS("skin/uiskin.atlas"),
    UI("editor/ui/ui.atlas"),
    DEMO("demo/demo.atlas");

    private final String path;

    TextureAtlasAssets(String path) {
        this.path = path;
    }

    /**
     * get path.
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the asset materialized as a texture.
     */
    public TextureAtlas getTextureAtlas(AssetManager manager) {
        TextureAtlas result = manager.get(path, TextureAtlas.class);

        for (Texture t : result.getTextures()) {
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        return result;
    }

    @Override
    public void load(AssetManager manager) {
        manager.load(path, TextureAtlas.class);
    }
}
