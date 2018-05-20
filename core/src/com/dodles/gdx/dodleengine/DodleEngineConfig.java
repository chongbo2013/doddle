package com.dodles.gdx.dodleengine;

import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import java.util.EnumSet;
import java.util.Set;
import javax.inject.Inject;

/**
 * Configuration for the dodle engine.
 */
@PerDodleEngine
public class DodleEngineConfig {
    
    /**
     * Dodle engine options.
     */
    public enum Options {        
        /**
         * Enables the inline editor interface.
         */
        INLINE_EDITOR,
        
        /**
         * Enables the full editor interface.
         */
        FULL_EDITOR,
        
        /**
         * Enables drawing the dodles header in the full editor.
         */
        FULL_EDITOR_DODLES_HEADER,
        
        /**
         * Enables drawing an empty header to push things down (iOS hack).
         */
        IOS_EMPTY_HEADER,
        
        /**
         * The end user can move the viewport.
         */
        USER_MOVE_VIEWPORT,

        /**
         * engine is in render animation mode.
         */
        RENDER_ANIMATION
    }
    
    private final EngineEventManager eventManager;
    
    private EnumSet<Options> selectedOptions = EnumSet.noneOf(Options.class);
    
    @Inject
    public DodleEngineConfig(EngineEventManager eventManager) {
        this.eventManager = eventManager;
    }
    
    /**
     * Returns all current enabled engine options.
     */
    public final EnumSet<Options> getOptions() {
        return selectedOptions;
    }
    
    /**
     * Returns true if the given option is active.
     */
    public final boolean hasOption(Options option) {
        return selectedOptions.contains(option);
    }
    
    /**
     * Sets the active engine options.
     */
    public final void setOptions(Set<Options> newOptions) {
        selectedOptions = EnumSet.copyOf(newOptions);
        
        eventManager.fireEvent(EngineEventType.ENGINE_CONFIG_CHANGED);
    }
}
