package com.dodles.gdx.dodleengine.editor.overlays;

import com.dodles.gdx.dodleengine.PerDodleEngine;
import javax.inject.Inject;

/**
 * Eager loads overlays.
 */
@PerDodleEngine
public class OverlayLoader {
    
    @Inject
    public OverlayLoader(SelectedActorOverlay sao) {
    }
}
