package com.dodles.gdx.dodleengine.editor;

import com.badlogic.gdx.Gdx;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;

import java.util.Stack;

import javax.inject.Inject;

/**
 * Manages the editor peel stack.
 */
@PerDodleEngine
public class PeelStackManager {
    private final Stack<Runnable> peels = new Stack<Runnable>();
    private final EngineEventManager eventManager;

    @Inject
    public PeelStackManager(EngineEventManager eventManager) {
        this.eventManager = eventManager;
        reset();
    }
    
    /**
     * Returns the current size of the externally visible stack.
     */
    public final int size() {
        return peels.size();
    }
    
    /**
     * Resets the stack to empty.
     */
    public final void reset() {
        peels.clear();
        eventManager.fireEvent(EngineEventType.PEEL_STACK_CHANGED);
    }
    
    /**
     * Pushes a new frame on the stack with an OK and cancel action, optionally in the front of the current layer.
     */
    public final void push(Runnable ok) {
        peels.add(ok);
        eventManager.fireEvent(EngineEventType.PEEL_STACK_CHANGED);
    }
    
    /**
     * Pops the top frame off the stack, optionally not firing the stack changed event.
     */
    public final void pop() {
        if (size() > 0) {
            Gdx.app.postRunnable(peels.pop());
            eventManager.fireEvent(EngineEventType.PEEL_STACK_CHANGED);
        }
    }
    
    /**
     * Pops all frames off of the peel stack, optionally trying to cancel if available.
     */
    public final void popAll() {
        while (size() > 0) {
            Gdx.app.postRunnable(peels.pop());
        }
        eventManager.fireEvent(EngineEventType.PEEL_STACK_CHANGED);
    }
}

