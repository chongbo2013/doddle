package com.dodles.gdx.dodleengine.editor.overlays;

import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager.DisplayMode;

/**
 * An Overlay that displays information over the dodle while in the editor.
 */
public interface Overlay {
    /**
     * Requests the overlay to re-render.
     */
    void update(DisplayMode displayMode);
    
    /**
     * Resets the overlay to it's initial state.
     */
    void reset();
}
