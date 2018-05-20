package com.dodles.mobileinterop;

import com.dodles.gdx.dodleengine.events.DefaultEventBus;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.util.NumberFormatter;
import com.dodles.gdx.dodleengine.util.PixmapFactory;
import com.dodles.gdx.dodleengine.util.ScreenGrabber;
import dagger.Module;
import dagger.Provides;

/**
 * Module that provides dependencies for android/ios interop.
 */
// CHECKSTYLE.OFF: HideUtilityClassConstructor - needed for dagger
@Module
public class InteropDodleEngineModule {
    /**
     * Provides the default screengrabber.
     */
    @Provides static ScreenGrabber provideScreenGrabber(PixmapScreenGrabber psg) {
        return psg;
    }
    
    /**
     * Provides the default PixmapFactory.
     */
    @Provides static PixmapFactory providePixmapFactory(DefaultPixmapFactory dpf) {
        return dpf;
    }
    
    /**
     * Provides the JRE-based number formatter.
     */
    @Provides static NumberFormatter provideNumberFormatter(JreNumberFormatter jnf) {
        return jnf;
    }

    /**
     * Provides the event bus.
     */
    @Provides static EventBus provideEventBus(DefaultEventBus eventBus) {
        return eventBus;
    }
}
// CHECKSTYLE.ON: HideUtilityClassConstructor