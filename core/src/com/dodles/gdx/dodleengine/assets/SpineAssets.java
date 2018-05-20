package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Defines all string assets in the dodle engine.
 */
public enum SpineAssets implements LoadableAsset {
    SPINE_SPEEDY("spine/speedy.json", "spine/speedy.atlas"),
    SPINE_SPINEBOY("spine/spineboy.json", "spine/spineboy.atlas"),
    SPINE_ALIEN("spine/alien.json", "spine/alien.atlas"),
    SPINE_SNAKE("spine/snake.json", "spine/snake.atlas"),
    SPINE_SIMPLEBOY("spine/simpleBoy.json", "spine/simpleBoy.atlas"),
    SPINE_HERO("spine/hero.json", "spine/hero.atlas"),
    SPINE_DRAGON("spine/dragon.json", "spine/dragon.atlas"),
    SPINE_POWERUP("spine/powerup.json", "spine/powerup.atlas");



    private final String jsonPath;
    private final String atlasPath;

    public static final SpineAssets SELECTED = SPINE_SIMPLEBOY;
    public static final float SCALE = 1f;

    SpineAssets(String jsonPath, String atlasPath) {
        this.jsonPath = jsonPath;
        this.atlasPath = atlasPath;
    }

    @Override
    public void load(AssetManager manager) {
        manager.load(jsonPath, String.class);
        manager.load(atlasPath, TextureAtlas.class);
    }

    /**
     * Returns the asset materialized as a texture.
     */
    public TextureAtlas getTextureAtlas(AssetManager manager) {
        TextureAtlas result = manager.get(atlasPath, TextureAtlas.class);

        for (Texture t : result.getTextures()) {
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        return result;
    }

    /**
     * Returns the asset materialized as a string.
     */
    public String getString(AssetManager manager) {
        return manager.get(jsonPath, String.class);
    }
}
