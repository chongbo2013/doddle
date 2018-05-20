package com.dodles.gdx.dodleengine.input;

/**
 * Interface for handling zoom input.
 */
public interface ZoomInputHandler extends InputHandler {
    /**
     * Handles a zoom start event.
     */
    void handleZoomStart(ZoomInteractionData startData);
    
    /**
     * Handles a zoom move event.
     */
    void handleZoomMove(ZoomInteractionData moveData);
    
    /**
     * Handles a zoom end event.
     */
    void handleZoomEnd(ZoomInteractionData endData);
}