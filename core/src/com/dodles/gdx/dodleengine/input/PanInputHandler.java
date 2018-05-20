package com.dodles.gdx.dodleengine.input;

/**
 * Interface for handling Pan gestures.
 */
public interface PanInputHandler extends InputHandler {
    /**
     * Handles a pan start event.
     */
    void handlePanStart(InteractionData startData);
    
    /**
     * Handles a pan move event.
     */
    void handlePanMove(InteractionData moveData);
    
    /**
     * Handles a pan end event.
     */
    void handlePanEnd(InteractionData endData);
}
