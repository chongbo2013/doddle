package com.dodles.gdx.dodleengine.input;

/**
 * Interface for handling touch input.
 */
public interface TouchInputHandler extends InputHandler {
    /**
     * Handles a touch start event.
     */
    void handleTouchStart(InteractionData startData, int pointer);
    
    /**
     * Handles a touch move event.
     */
    void handleTouchMove(InteractionData moveData, int pointer);
    
    /**
     * Handles a touch end event.
     */
    void handleTouchEnd(InteractionData endData, int pointer);

    /**
     * Handles canceling of a touch event.
     */
    void handleTouchCancel();
}
