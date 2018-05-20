package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;

/**
 * Asset manager wrapper to simplify and consolidate asset loading.  All assets
 * should be loaded through this system, Gdx.file should not be used directly!
 */
@PerDodleEngine
public class AssetProvider {
    private AssetManager manager;
    
    @Inject
    public AssetProvider() {
        manager = new AssetManager();
        manager.setLoader(String.class, new StringLoader(new InternalFileHandleResolver()));
    }
    
    /**
     * Loads all assets into memory.
     */
    public final void loadAssets() {
        ArrayList allAssets = new ArrayList();
        allAssets.addAll(Arrays.asList(BitmapFontAssets.values()));
        allAssets.addAll(Arrays.asList(SkinAssets.values()));
        allAssets.addAll(Arrays.asList(StringAssets.values()));
        allAssets.addAll(Arrays.asList(TextureAssets.values()));
        allAssets.addAll(Arrays.asList(TextureAtlasAssets.values()));
        allAssets.addAll(Arrays.asList(SpeechAssets.values()));
        allAssets.addAll(Arrays.asList(SpineAssets.values()));
        
        for (Object asset : allAssets) {
            ((LoadableAsset) asset).load(manager);
        }
        
        // TODO: make this async?
        manager.finishLoading();
    }
    
    /**
     * Returns the requested font asset.
     */
    public final BitmapFont getFont(BitmapFontAssets asset) {
        return asset.getFont(manager);
    }
    
    /**
     * Returns the requested skin asset.
     */
    public final Skin getSkin(SkinAssets asset) {
        return asset.getSkin(manager);
    }
    
    /**
     * Returns the requested string asset.
     */
    public final String getString(StringAssets asset) {
        return asset.getString(manager);
    }
    
    /**
     * Returns the requested texture asset.
     */
    public final Texture getTexture(TextureAssets asset) {
        return asset.getTexture(manager);
    }

    /**
     * Returns the requested Texture Atlas Asset.
     */
    public final TextureAtlas getTextureAtlas(TextureAtlasAssets asset) {
        return asset.getTextureAtlas(manager);
    }

    /**
     * Returns the JSON spine definition.
     */
    public final String getString(SpineAssets asset) {
        return asset.getString(manager);
    }

    /**
     * Returns the texture atlas.
     */
    public final TextureAtlas getTextureAtlas(SpineAssets asset) {
        return asset.getTextureAtlas(manager);
    }
    
    /**
     * Returns the sound.
     */
    public final Sound getSound(SpeechAssets asset) {
        return asset.getSound(manager);
    }
    
    /**
     * Returns the phoneme json mapping for the SpeechAsset.
     */
    public final String getPhonemeJson(SpeechAssets asset) {
        return asset.getPhonemeJson(manager);
    }
}
