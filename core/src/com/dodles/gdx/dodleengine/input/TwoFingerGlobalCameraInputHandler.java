package com.dodles.gdx.dodleengine.input;

import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.DodleEngineConfig;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;

import javax.inject.Inject;

/**
 * A common handler that uses input to move the global camera (viewport).
 */
public class TwoFingerGlobalCameraInputHandler implements WheelInputHandler, TwoFingerPanInputHandler {
    private CameraManager cameraManager;
    private DodleEngineConfig engineConfig;
    private Vector2 prevPanPoint;
    private boolean isZooming = false;

    @Inject
    public TwoFingerGlobalCameraInputHandler(DodleEngineConfig engineConfig, CameraManager cameraManager, EngineEventManager eventManager) {
        this.engineConfig = engineConfig;
        this.cameraManager = cameraManager;

        eventManager.addListener(new EngineEventListener(EngineEventType.DISPLAY_TRANSFORM_OVERLAY) {
            @Override
            public void listen(EngineEventData data) {
                String param = data.getFirstStringParam();

                isZooming = false;

                if (param != null) {
                    if (param.equals("zoom")) {
                        isZooming = true;
                    }
                }
            }
        });
    }

    @Override
    public final void handleWheel(WheelInteractionData wheelData) {
        if (engineConfig.hasOption(DodleEngineConfig.Options.USER_MOVE_VIEWPORT)) {
            cameraManager.scaleGlobalViewport(wheelData.getScale(), wheelData.getGlobalPoint());
        }
    }

    @Override
    public final void handlePanStart(InteractionData startData) {
        if (engineConfig.hasOption(DodleEngineConfig.Options.USER_MOVE_VIEWPORT) && startData.getNumPointers() == 2 && !isZooming) {
            prevPanPoint = startData.getGlobalPoint().cpy();
        }
    }

    @Override
    public final void handlePanMove(InteractionData moveData) {
        if (engineConfig.hasOption(DodleEngineConfig.Options.USER_MOVE_VIEWPORT) && moveData.getNumPointers() == 2 && !isZooming) {
            cameraManager.panGlobalViewport(prevPanPoint, moveData.getGlobalPoint());
            prevPanPoint = moveData.getGlobalPoint().cpy();
        }
    }

    @Override
    public final void handlePanEnd(InteractionData endData) {
        prevPanPoint = null;
    }
}
