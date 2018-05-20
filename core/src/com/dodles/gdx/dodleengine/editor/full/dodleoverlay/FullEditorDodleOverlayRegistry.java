package com.dodles.gdx.dodleengine.editor.full.dodleoverlay;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.dodles.gdx.dodleengine.PerDodleEngine;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * A registry for dodle overlays.
 */
@PerDodleEngine
public class FullEditorDodleOverlayRegistry {
    private final HashMap<String, FullEditorDodleOverlay> overlayMap = new HashMap<String, FullEditorDodleOverlay>();
    
    @Inject
    public FullEditorDodleOverlayRegistry() {   
    }
    
    /**
     * Registers the given overlay.
     */
    public final void registerOverlay(FullEditorDodleOverlay overlay) {
        overlayMap.put(overlay.getName(), overlay);
    }
    
    /**
     * Initializes all registered overlays.
     */
    public final void initializeOverlays(Stack dodleOverlayStack, Skin skin) {
        for (FullEditorDodleOverlay overlay : overlayMap.values()) {
            overlay.initialize(dodleOverlayStack, skin);
        }
    }
}
