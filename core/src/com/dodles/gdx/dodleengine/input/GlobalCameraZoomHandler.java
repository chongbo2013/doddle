package com.dodles.gdx.dodleengine.input;

import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.DodleEngineConfig;

import javax.inject.Inject;

/**
 * A common handler that uses input to move the global camera (viewport).
 */
public class GlobalCameraZoomHandler implements ZoomInputHandler {
    private CameraManager cameraManager;
    private DodleEngineConfig engineConfig;
    private float prevScale;

    @Inject
    public GlobalCameraZoomHandler(DodleEngineConfig engineConfig, CameraManager cameraManager) {
        this.engineConfig = engineConfig;
        this.cameraManager = cameraManager;
    }

    @Override
    public final void handleZoomStart(ZoomInteractionData startData) {
        if (engineConfig.hasOption(DodleEngineConfig.Options.USER_MOVE_VIEWPORT)) {
            prevScale = startData.getScale();
        }
    }

    @Override
    public final void handleZoomMove(ZoomInteractionData moveData) {
        if (engineConfig.hasOption(DodleEngineConfig.Options.USER_MOVE_VIEWPORT)) {
            cameraManager.scaleGlobalViewport((moveData.getScale() - prevScale) + 1, moveData.getGlobalPoint());
            prevScale = moveData.getScale();
        }
    }

    @Override
    public void handleZoomEnd(ZoomInteractionData endData) {
    }
}
