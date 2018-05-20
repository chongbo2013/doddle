package com.dodles.gdx.dodleengine.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.RootGroup;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Translates gestures to DodleEngine listeners.
 */
@PerDodleEngine
public class DodleEngineGestureListener implements GestureDetector.GestureListener, InputProcessor {
    private final AnimationManager animationManager;
    private final ToolRegistry toolRegistry;
    private final DodleStageManager stageManager;
    
    private Vector2 lastMousePosition;
    private RotationInteractionData lastRotationData;
    private ZoomInteractionData lastZoomData;
    private boolean isPanning = false;
    private boolean isPinching = false;
    private int numFingers = 0;

    @Inject
    public DodleEngineGestureListener(
            AnimationManager animationManager,
            ToolRegistry toolRegistry,
            DodleStageManager stageManager
    ) {
        this.animationManager = animationManager;
        this.toolRegistry = toolRegistry;
        this.stageManager = stageManager;
    }
    
    @Override
    public final boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public final boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public final boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public final boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public final boolean pan(float x, float y, float deltaX, float deltaY) {
        RootGroup group = stageManager.getDodleGroup();
        
        for (InputHandler handler : getActiveToolHandlers()) {
            if (handler instanceof PanInputHandler) {
                PanInputHandler pih = (PanInputHandler) handler;
                
                if (!isPanning) {
                    pih.handlePanStart(new InteractionData(group, x - deltaX, y - deltaY, numFingers));
                }
                
                pih.handlePanMove(new InteractionData(group, x, y, numFingers));
            }
        }
        
        isPanning = true;        
        return false;
    }

    @Override
    public final boolean panStop(float x, float y, int pointer, int button) {
        RootGroup group = stageManager.getDodleGroup();
        
        for (InputHandler handler : getActiveToolHandlers()) {
            if (handler instanceof PanInputHandler) {
                PanInputHandler pih = (PanInputHandler) handler;        
                pih.handlePanEnd(new InteractionData(group, x, y));
            }
        }
        
        isPanning = false;
        return false;
    }

    @Override
    public final boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public final boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        Vector2 initialAvg = new Vector2((initialPointer1.x + initialPointer2.x) / 2f, (initialPointer1.y + initialPointer2.y) / 2);
        Vector2 avgPoint = new Vector2((pointer1.x + pointer2.x) / 2f, (pointer1.y + pointer2.y) / 2);

        float initialAngle = new Vector2(initialPointer2.x - initialPointer1.x, initialPointer2.y - initialPointer1.y).angle();
        float curAngle = new Vector2(pointer2.x - pointer1.x, pointer2.y - pointer1.y).angle();
        
        float initialDist = initialPointer1.dst(initialPointer2);
        float curDist = pointer1.dst(pointer2);
        
        RootGroup group = stageManager.getDodleGroup();
        
        for (InputHandler handler : getActiveToolHandlers()) {
            if (handler instanceof RotationInputHandler) {
                RotationInputHandler rih = (RotationInputHandler) handler;
                
                if (!isPinching) {
                    rih.handleRotationStart(new RotationInteractionData(group, initialAvg.x, initialAvg.y, 0));
                }
                
                lastRotationData = new RotationInteractionData(group, avgPoint.x, avgPoint.y, curAngle - initialAngle);
                rih.handleRotationMove(lastRotationData);
            }
            
            if (handler instanceof ZoomInputHandler) {
                ZoomInputHandler zih = (ZoomInputHandler) handler;
                
                if (!isPinching) {
                    zih.handleZoomStart(new ZoomInteractionData(group, initialAvg.x, initialAvg.y, 1));
                }
                
                lastZoomData = new ZoomInteractionData(group, avgPoint.x, avgPoint.y, curDist / initialDist);
                zih.handleZoomMove(lastZoomData);
            }
        }
        
        isPinching = true;
        
        return false;
    }

    /**
     * Not sure why this isn't seen as an override... :/
     */
    public final void pinchStop() {
        for (InputHandler handler : getActiveToolHandlers()) {
            if (handler instanceof RotationInputHandler) {
                RotationInputHandler rih = (RotationInputHandler) handler;
                rih.handleRotationEnd(lastRotationData);
            }
            
            if (handler instanceof ZoomInputHandler) {
                ZoomInputHandler zih = (ZoomInputHandler) handler;
                zih.handleZoomEnd(lastZoomData);
            }
        }
        
        isPinching = false;
    }

    @Override
    public final boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public final boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public final boolean keyTyped(char character) {
        return false;
    }

    @Override
    public final boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            numFingers++;

            for (InputHandler handler : getActiveToolHandlers()) {
                if (handler instanceof TouchInputHandler) {
                    TouchInputHandler tih = (TouchInputHandler) handler;
                    tih.handleTouchStart(new InteractionData(stageManager.getDodleGroup(), screenX, screenY), pointer);
                }

                if (numFingers == 2 && hasTwoFingerPanHandler()) {
                    if (handler instanceof TwoFingerPanInputHandler) {
                        RootGroup group = stageManager.getDodleGroup();

                        TwoFingerPanInputHandler pih = (TwoFingerPanInputHandler) handler;
                        pih.handlePanStart(new InteractionData(group, screenX, screenY, numFingers));
                    }

                    if (handler instanceof TouchInputHandler) {
                        TouchInputHandler tih = (TouchInputHandler) handler;
                        tih.handleTouchCancel();
                    }
                }
            }
        }

        return false;
    }

    @Override
    public final boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            numFingers--;

            for (InputHandler handler : getActiveToolHandlers()) {
                if (numFingers > 1 && hasTwoFingerPanHandler()) {
                    if (handler instanceof TwoFingerPanInputHandler) {
                        RootGroup group = stageManager.getDodleGroup();
                        TwoFingerPanInputHandler pih = (TwoFingerPanInputHandler) handler;

                        pih.handlePanEnd(new InteractionData(group, screenX, screenY));
                    }
                }

                if (handler instanceof TouchInputHandler) {
                    TouchInputHandler tih = (TouchInputHandler) handler;
                    tih.handleTouchEnd(new InteractionData(stageManager.getDodleGroup(), screenX, screenY), pointer);
                }
            }
        }

        return false;
    }

    @Override
    public final boolean touchDragged(int screenX, int screenY, int pointer) {
        for (InputHandler handler : getActiveToolHandlers()) {
            if (numFingers > 1 && hasTwoFingerPanHandler()) {
                if (handler instanceof TwoFingerPanInputHandler) {
                    RootGroup group = stageManager.getDodleGroup();

                    TwoFingerPanInputHandler pih = (TwoFingerPanInputHandler) handler;
                    pih.handlePanMove(new InteractionData(group, screenX, screenY, numFingers));
                }
            } else {
                if (handler instanceof TouchInputHandler) {
                    TouchInputHandler tih = (TouchInputHandler) handler;
                    tih.handleTouchMove(new InteractionData(stageManager.getDodleGroup(), screenX, screenY), pointer);
                }
            }
        }
        
        return false;
    }

    @Override
    public final boolean mouseMoved(int screenX, int screenY) {
        lastMousePosition = new Vector2(screenX, screenY);
        return false;
    }

    @Override
    public final boolean scrolled(int amount) {
        for (InputHandler handler : getActiveToolHandlers()) {
            if (handler instanceof WheelInputHandler) {
                WheelInteractionData wid = new WheelInteractionData(stageManager.getDodleGroup(), lastMousePosition.x, lastMousePosition.y, amount);
                ((WheelInputHandler) handler).handleWheel(wid);
            }
        }
        
        return false;
    }

    private boolean hasTwoFingerPanHandler() {
        boolean result = false;
        for (InputHandler handler : getActiveToolHandlers()) {
            if (handler instanceof TwoFingerPanInputHandler) {
                result = true;
            }
        }

        return result;
    }
    
    private List<InputHandler> getActiveToolHandlers() {
        ArrayList<InputHandler> result = new ArrayList<InputHandler>();
        
        Tool activeTool = toolRegistry.getActiveTool();
        
        if (activeTool != null) {
            List<InputHandler> toolInputHandlers = activeTool.getInputHandlers();
            if (toolInputHandlers != null) {
                result.addAll(toolInputHandlers);
            }
        }
        
        result.addAll(animationManager.getInputHandlers());
        
        return result;
    }
    
}
