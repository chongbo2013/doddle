package com.dodles.gdx.dodleengine.editor.full.dodleoverlay;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

/**
 * An overlay that displays over the main dodle viewing window, but sticks to the UI layout, not the dodle.
 */
public interface FullEditorDodleOverlay {
    /**
     * Returns the name of the overlay.
     */
    String getName();
    
    /**
     * Initializes the dodle overlay.
     */
    void initialize(Stack dodleOverlayStack, Skin skin);
}
