package com.dodles.gdx.dodleengine.input;

/**
 * Interface for handling rotation input.
 */
public interface RotationInputHandler extends InputHandler {
    /**
     * Handles a rotation start event.
     */
    void handleRotationStart(RotationInteractionData startData);
    
    /**
     * Handles a rotation move event.
     */
    void handleRotationMove(RotationInteractionData moveData);
    
    /**
     * Handles a rotation end event.
     */
    void handleRotationEnd(RotationInteractionData endData);
}
