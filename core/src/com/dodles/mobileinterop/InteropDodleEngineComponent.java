package com.dodles.mobileinterop;

import com.dodles.gdx.dodleengine.DodleEngine;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import dagger.Component;

/**
 * Kernel for injection of Dodle Engine and dependencies.
 */
@PerDodleEngine
@Component(modules = InteropDodleEngineModule.class)
public interface InteropDodleEngineComponent {
    /**
     * Instantiates the DodleEngine.
     */
    DodleEngine engine();
}
