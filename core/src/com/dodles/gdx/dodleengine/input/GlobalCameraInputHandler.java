package com.dodles.gdx.dodleengine.input;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.DodleEngineConfig;
import javax.inject.Inject;

/**
 * A common handler that uses input to move the global camera (viewport).
 */
public class GlobalCameraInputHandler implements WheelInputHandler, PanInputHandler {
    private CameraManager cameraManager;
    private DodleEngineConfig engineConfig;
    private Vector2 prevPanPoint;
    private float prevScale;
    
    @Inject
    public GlobalCameraInputHandler(DodleEngineConfig engineConfig, CameraManager cameraManager) {
        this.engineConfig = engineConfig;
        this.cameraManager = cameraManager;
    }

    @Override
    public final void handleWheel(WheelInteractionData wheelData) {
        if (engineConfig.hasOption(DodleEngineConfig.Options.USER_MOVE_VIEWPORT)) {
            cameraManager.scaleGlobalViewport(wheelData.getScale(), wheelData.getGlobalPoint());
        }
    }

    @Override
    public final void handlePanStart(InteractionData startData) {
        if (engineConfig.hasOption(DodleEngineConfig.Options.USER_MOVE_VIEWPORT)) {
            prevPanPoint = startData.getGlobalPoint().cpy();
        }
    }

    @Override
    public final void handlePanMove(InteractionData moveData) {
        if (engineConfig.hasOption(DodleEngineConfig.Options.USER_MOVE_VIEWPORT)) {
            cameraManager.panGlobalViewport(prevPanPoint, moveData.getGlobalPoint());
            prevPanPoint = moveData.getGlobalPoint().cpy();
        }
    }

    @Override
    public final void handlePanEnd(InteractionData endData) {
        prevPanPoint = null;
    }
}
