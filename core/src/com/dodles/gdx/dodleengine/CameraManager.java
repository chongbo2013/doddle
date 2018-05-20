package com.dodles.gdx.dodleengine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.editor.EditorInterface;
import com.dodles.gdx.dodleengine.editor.EditorInterfaceManager;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.RootGroup;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import com.dodles.gdx.dodleengine.scenegraph.SceneCamera;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.Transform;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer.ShapeRendererRectangleGraphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Manages Camera movement in the scene (both global viewport and the Dodle viewing window).
 */
@PerDodleEngine
public class CameraManager {
    private final EditorInterfaceManager interfaceManager;
    
    private static final float SHORT_CURTAIN = (float) Math.pow(2, 25);
    private static final float LONG_CURTAIN = (float) Math.pow(2, 26);
    private static final float MIN_ZOOM = 0.2f;
    private static final float MAX_ZOOM = 3f;
    
    private float globalScale = 1;
    private float defaultScale;
    private float globalWidth;
    private float globalHeight;
    private Group scaledGroup;
    private HashMap<String, SceneCamera> sceneCameras = new HashMap<String, SceneCamera>();
    private Shape curtain;
    private Shape dodleBorder;
    
    @Inject
    public CameraManager(EditorInterfaceManager interfaceManager) {
        this.interfaceManager = interfaceManager;
    }
    
    /**
     * Initializes the camera manager with the root groups.
     */
    public final void init(Group newScaledGroup, RootGroup cameraGroup, float width, float height) {        
        sceneCameras.clear();
        
        this.scaledGroup = newScaledGroup;
        
        curtain = new Shape("curtain", "N/A");
        
        curtain.addGenerator(new GraphicsGenerator() {
            @Override
            public List<Graphics> generateGraphics(Shape shape) {
                ArrayList<Graphics> result = new ArrayList<Graphics>();
                
                result.add(new ShapeRendererRectangleGraphics(SHORT_CURTAIN * -1, SHORT_CURTAIN * -1, SHORT_CURTAIN, LONG_CURTAIN, Color.BLACK));
                result.add(new ShapeRendererRectangleGraphics(DodleEngine.DODLE_SIDE, SHORT_CURTAIN * -1, SHORT_CURTAIN, LONG_CURTAIN, Color.BLACK));
                result.add(new ShapeRendererRectangleGraphics(0, 0, DodleEngine.DODLE_SIDE, SHORT_CURTAIN * -1, Color.BLACK));
                result.add(new ShapeRendererRectangleGraphics(0, DodleEngine.DODLE_SIDE, DodleEngine.DODLE_SIDE, SHORT_CURTAIN, Color.BLACK));

                return result;
            }
        });
        
        dodleBorder = new Shape("dodleBorder", "N/A");
        
        dodleBorder.addGenerator(new GraphicsGenerator() {
            @Override
            public List<Graphics> generateGraphics(Shape shape) {
                ArrayList<Graphics> result = new ArrayList<Graphics>();
                int lineLength = 25;
                int lineWidth = 4;
                Color color = Color.BLACK.cpy();
                color.a = 0.3f;
                
                for (int i = lineLength / 2; i < DodleEngine.DODLE_SIDE; i += lineLength * 2) {
                    result.add(new ShapeRendererRectangleGraphics(-lineWidth, i, lineWidth, lineLength, color));
                    result.add(new ShapeRendererRectangleGraphics(DodleEngine.DODLE_SIDE + lineWidth, i, lineWidth, lineLength, color));
                    result.add(new ShapeRendererRectangleGraphics(i, -lineWidth, lineLength, lineWidth, color));
                    result.add(new ShapeRendererRectangleGraphics(i, DodleEngine.DODLE_SIDE + lineWidth, lineLength, lineWidth, color));
                }
                
                return result;
            }
        });
        
        
        cameraGroup.addActor(curtain);
        cameraGroup.addActor(dodleBorder);
        
        hideOutsideDodle(false);
        
        resetGlobalViewport(width, height);
    }
    
    /**
     * Returns the global viewport scale.
     */
    public final float getGlobalViewportScale() {
        return globalScale;
    }
    
    /**
     * Scales the global viewport.
     */
    public final void scaleGlobalViewport(float scaleAdj, Vector2 globalZoomPoint) {
        scaleGlobalViewport(scaleAdj, globalZoomPoint, 0);
    }
    
    /**
     * Scales the global viewport, taking duration seconds.
     */
    public final void scaleGlobalViewport(float scaleAdj, Vector2 globalZoomPoint, float duration) {
        globalScale *= scaleAdj;
        globalScale = (float) Math.min(MAX_ZOOM * defaultScale, Math.max(MIN_ZOOM * defaultScale, globalScale));
        
        float scaleChange = globalScale - scaledGroup.getScaleX();

        if (globalZoomPoint == null) {
            globalZoomPoint = scaledGroup.localToStageCoordinates(new Vector2(DodleEngine.DODLE_SIDE / 2, DodleEngine.DODLE_SIDE / 2));
        }

        Vector2 containerZoomPoint = scaledGroup.stageToLocalCoordinates(globalZoomPoint.cpy());
        animateScaledGroupMove(
            scaledGroup.getX() + containerZoomPoint.x * -1 * scaleChange,
            scaledGroup.getY() + containerZoomPoint.y * -1 * scaleChange,
            globalScale,
            globalScale,
            duration,
            null
        );
    }
    
    /**
     * Pans the global viewport.
     */
    public final void panGlobalViewport(Vector2 prevPoint, Vector2 curPoint) {
        panGlobalViewport(prevPoint, curPoint, 0);
    }
    
    /**
     * Pans the global viewport, taking duration seconds.
     */
    public final void panGlobalViewport(Vector2 prevPoint, Vector2 curPoint, float duration) {
        animateScaledGroupMove(
            scaledGroup.getX() + curPoint.x - prevPoint.x,
            scaledGroup.getY() + curPoint.y - prevPoint.y,
            scaledGroup.getScaleX(),
            scaledGroup.getScaleY(),
            duration,
            null
        );
    }
    
    /**
     * Toggles whether the "curtain" is down and hiding things outside the dodle.
     */
    public final void hideOutsideDodle(boolean hide) {
        curtain.setVisible(hide);
        dodleBorder.setVisible(!hide);
    }
    
    /**
     * Returns the camera for the given scene.
     */
    public final SceneCamera getSceneCamera(Scene scene) {
        SceneCamera result = sceneCameras.get(scene.getName());
        
        if (result == null) {
            result = new SceneCamera(scene);
            sceneCameras.put(scene.getName(), result);
        }
        
        return result;
    }
    
    /**
     * Resets the camera manager.
     */
    public final void reset() {
        resetGlobalViewport();
        sceneCameras.clear();
    }
    
    /**
     * Resets the global viewport to the origin of the dodle.
     */
    public final Rectangle resetGlobalViewport() {
        return resetGlobalViewport(0);
    }
    
    /**
     * Resets the global viewport to the origin of the dodle, taking duration seconds.
     */
    public final Rectangle resetGlobalViewport(float duration) {
        return resetGlobalViewport(globalWidth, globalHeight, duration);
    }
    
    /**
     * Resets the global viewport to the origin of the dodle for the new stage width / height.
     */
    public final Rectangle resetGlobalViewport(float stageWidth, float stageHeight) {
        return resetGlobalViewport(stageWidth, stageHeight, 0);
    }
    
    /**
     * Resets the global viewport to the origin of the dodle for the new stage width / height, taking duration seconds.
     */
    public final Rectangle resetGlobalViewport(float stageWidth, float stageHeight, float duration) {
        globalWidth = stageWidth;
        globalHeight = stageHeight;
        
        EditorInterface.Padding padding = interfaceManager.getInterfacePadding();
                
        stageHeight -= (padding.getBottom() + padding.getTop());
        stageWidth -= (padding.getLeft() + padding.getRight());
        
        float widthRatio = stageWidth / DodleEngine.DODLE_SIDE;
        float heightRatio = stageHeight / DodleEngine.DODLE_SIDE;
        globalScale = Math.min(widthRatio, heightRatio);
        defaultScale = globalScale;
        
        float scaledSide = DodleEngine.DODLE_SIDE * globalScale; 
        float x = ((stageWidth - scaledSide) / 2) + padding.getLeft();
        float y = ((stageHeight - scaledSide) / 2) + padding.getTop();
        animateScaledGroupMove(x, y, globalScale, globalScale, duration, null);
        
        return new Rectangle(x, y, scaledSide, scaledSide);
    }

    /**
     * Focus on a specific object, or reset the viewport.
     */
    public final Transform focus(DodlesActor renderer) {
        Transform originalTransform = new Transform();
        originalTransform.setX(scaledGroup.getX());
        originalTransform.setY(scaledGroup.getY());
        originalTransform.setScaleX(scaledGroup.getScaleX());
        originalTransform.setScaleY(scaledGroup.getScaleY());
        
        if (renderer != null) {
            Actor actor = (Actor) renderer;
            Vector2 globalCoords = new Vector2(actor.getOriginX(), actor.getOriginY());
            globalCoords = actor.localToStageCoordinates(globalCoords);
            
            final EditorInterface.Padding interfacePadding = interfaceManager.getInterfacePadding();
            final Rectangle dodleBounds = CommonActorOperations.getDodleBounds(renderer);
            final float centerX = globalWidth / 2 + interfacePadding.getLeft() / 2 - interfacePadding.getRight() / 2;
            final float centerY = globalHeight / 2 + interfacePadding.getTop() / 2 - interfacePadding.getBottom() / 2;
            
            animateScaledGroupMove(
                scaledGroup.getX() + (centerX - globalCoords.x),
                scaledGroup.getY() + (centerY - globalCoords.y),
                globalScale,
                globalScale,
                .25f,
                new Runnable() {
                    @Override
                    public void run() {
                        float zoomPadding = DensityManager.getDensity().getScale() * 100;
                        float width = globalWidth - interfacePadding.getLeft() - interfacePadding.getRight() - zoomPadding;
                        float height = globalHeight - interfacePadding.getTop() - interfacePadding.getBottom() - zoomPadding;
                        float scale = Math.min(width / (dodleBounds.width * globalScale), height / (dodleBounds.height * globalScale));
                        scaleGlobalViewport(scale, new Vector2(centerX, centerY), .25f);
                    }
                }
            );
        } else {
            resetGlobalViewport(.25f);
        }
        
        return originalTransform;
    }
    
    /**
     * Move the viewport focus to the given transform.
     */
    public final void focus(Transform transform) {
        globalScale = transform.getScaleX();
        animateScaledGroupMove(transform, .25f, null);
    }
    
    private void animateScaledGroupMove(Transform transform, float duration, Runnable runnable) {
        animateScaledGroupMove(transform.getX(), transform.getY(), transform.getScaleX(), transform.getScaleY(), duration, null);
    }
    
    private void animateScaledGroupMove(float x, float y, float scaleX, float scaleY, float duration, Runnable runnable) {
        if (duration <= 0) {
            scaledGroup.setPosition(x, y);
            scaledGroup.setScaleX(scaleX);
            scaledGroup.setScaleY(scaleY);
        } else {
            MoveToAction mta = Actions.moveTo(x, y, duration);
            ScaleToAction sta = Actions.scaleTo(scaleX, scaleY, duration);
            ParallelAction pa = Actions.parallel(mta, sta);
            Action finalAction = pa;

            if (runnable != null) {
                finalAction = Actions.sequence(pa, Actions.run(runnable));
            }

            scaledGroup.addAction(finalAction);
        }
    }
}
