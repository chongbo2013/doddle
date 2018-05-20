package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Defines all texture assets in the dodle engine.
 */
public enum TextureAssets implements LoadableAsset {
    EDITOR_OVERLAYICONS("editor/overlayicons.png"),
    EDITOR_TOOL_BRUSHICONS("editor/tool/brushicons.png"),
    EDITOR_TOOL_TOOLBARICONS("editor/tool/toolbaricons.png"),
    EDITOR_LAYER_TOOL_TOOLBARICONS("editor/tool/layertool/layertoolicons.png"),
    EDITOR_LAYER_TOOL_OPACITY_SLIDER("editor/tool/layertool/opacityslider.png"),
    EDITOR_LAYER_TOOL_SIZE_SLIDER("editor/tool/layertool/sizeslider.png"),
    EDITOR_HEADER_ICONS("editor/headericons.png"),
    EDITOR_HUE_SLIDER("editor/hueslider.png"),
    EDITOR_SATURATION_SLIDER("editor/saturationslider.png"),
    EDITOR_VALUE_SLIDER("editor/valueslider.png"),
    EDITOR_NO_COLOR_BUTTON("editor/nocolor.png"),
    EDITOR_WHITE_COLOR_BUTTON("editor/whitecolor.png"),
    EDITOR_WHITE_COLOR_ROUND_BUTTON("editor/whitecolorround.png"),
    EDITOR_WHITE_COLOR_ROUND_BUTTON_SELECTED("editor/whitecolorround_selected.png"),
    BUILDINGS_PNG("buildings.png"),
    PEEL_PNG("editor/peel.png"),
    ROTATE_PNG("editor/rotate.png"),
    ZOOM_PNG("editor/zoom.png"),
    PLAY_PNG("editor/phases/play.png"),
    SETTINGS_PNG("editor/phases/settings.png"),
    UPGRADE_PNG("editor/phases/upgrade.png");

    private final String path;

    // TODO: need asset files for each density type  LDPI,MDPI,HDPI, etc
    // We'll also need to figure out how to handle cachebusting if we use a wildcard
    // in the name above, the real name of the file needs to be in the code for
    // cachebusting to be able to find/replace. :(
    TextureAssets(String path) {
        this.path = path; //.replace("#", ""/*DensityManager.getName()*/);
    }

    /**
     * Returns the asset materialized as a texture.
     */
    public Texture getTexture(AssetManager manager) {

        Texture result = manager.get(path, Texture.class);

        if (result.getMagFilter() != Texture.TextureFilter.Linear || result.getMinFilter() != Texture.TextureFilter.Linear) {
            result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        return result;
    }

    @Override
    public void load(AssetManager manager) {
        manager.load(path, Texture.class);
    }
}
