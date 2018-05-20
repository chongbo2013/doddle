package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

/**
 * Defines all speech assets in the dodle engine.
 */
public enum SpeechAssets implements LoadableAsset {
    FAILURE_TO_COMMUNICATE("audio/failureToCommunicate.mp3", "audio/failureToCommunicate.json", "Failure Communicate", 5.825f),
    MAKE_HIM_AN_OFFER("audio/makeHimAnOffer.mp3", "audio/makeHimAnOffer.json", "Make Offer", 4),
    MAKE_MY_DAY("audio/makeMyDay.mp3", "audio/makeMyDay.json", "Make my Day", 3.2f),
    MAY_THE_FORCE_BE_WITH_YOU("audio/mayTheForceBeWithYou.mp3", "audio/mayTheForceBeWithYou.json", "May the Force", 2),
    NOT_IN_KANSAS_ANYMORE("audio/notInKansasAnymore.mp3", "audio/notInKansasAnymore.json", "Not in Kansas", 5.55f);
    
    private final String mp3Path;
    private final String jsonPath;
    private final String displayName;
    private final float length;
    
    SpeechAssets(String mp3Path, String jsonPath, String displayName, float length) {
        this.mp3Path = mp3Path;
        this.jsonPath = jsonPath;
        this.displayName = displayName;
        this.length = length;
    }
    
    /**
     * Returns the asset materialized as a sound.
     */
    public Sound getSound(AssetManager manager) {
        return manager.get(mp3Path, Sound.class);
    }
    
    /**
     * Returns the JSON phonemes for the sound.
     */
    public String getPhonemeJson(AssetManager manager) {
        return manager.get(jsonPath, String.class);
    }

    /**
     * Returns the display name for the sound.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the length of the audio clip.
     */
    public float getLength() {
        return length;
    }

    @Override
    public void load(AssetManager manager) {
        manager.load(mp3Path, Sound.class);
        manager.load(jsonPath, String.class);
    }
}
