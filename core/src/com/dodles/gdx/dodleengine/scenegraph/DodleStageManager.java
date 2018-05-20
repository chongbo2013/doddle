package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.StageRenderer;
import com.dodles.gdx.dodleengine.animation.PlaybackSettings;
import com.dodles.gdx.dodleengine.editor.overlays.Overlay;
import com.dodles.gdx.dodleengine.scenegraph.graphics.AtlasGraphicsRenderer;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRenderer;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Manages the stage containing all Dodle actors.
 */
@PerDodleEngine
public class DodleStageManager implements StageRenderer, Disposable {
    /**
     * Defines the available display modes.
     */
    public enum DisplayMode {
        NORMAL,
        ANIMATION,
        SHOW_OBJECT_MANIPULATION_OVERLAY,
        SHOW_OBJECT_OUTLINE,
        BLANK
    }
    
    private final BatchShapeRasterizer bsRasterizer;
    private final CameraManager cameraManager;
    private final PlaybackSettings playbackSettings;
    private final ArrayList<Overlay> overlays = new ArrayList<Overlay>();
    
    private Stage stage;
    private Group scaledGroup;
    private RootGroup dodleGroup;
    private RootGroup drawGroup;
    private RootGroup cameraGroup;
    
    private DisplayMode displayMode = DisplayMode.NORMAL;
    
    @Inject
    public DodleStageManager(
            BatchShapeRasterizer bsRasterizer,
            CameraManager cm,
            PlaybackSettings playbackSettings
    ) {
        this.bsRasterizer = bsRasterizer;
        this.cameraManager = cm;
        this.playbackSettings = playbackSettings;
    }
    
    @Override
    public final void resize(int width, int height) {        
        if (stage != null) {
            stage.getViewport().update(width, height, true);
            
            cameraManager.resetGlobalViewport(width, height);
        }
    }
    
    @Override
    public final boolean actWhenOverloaded() {
        return false;
    }
    
    @Override
    public final void act(float deltaTime) {
        stage.act(deltaTime * playbackSettings.getPlaySpeed());
    }
    
     @Override
    public final void draw() {
        ArrayList<Shape> shapesToRasterize = new ArrayList<Shape>();
        findShapesToRasterize(drawGroup, shapesToRasterize);
        findShapesToRasterize(dodleGroup, shapesToRasterize);
        
        if (!shapesToRasterize.isEmpty()) {
            //long startTime = System.nanoTime();
            bsRasterizer.rasterize(shapesToRasterize);
            //com.badlogic.gdx.Gdx.app.log("rastertime", "" + (System.nanoTime() - startTime) / 1000000);
        }
        
        stage.draw();
    }
    
    /**
     * Registers an overlay with the stage manager.
     */
    public final void registerOverlay(Overlay overlay) {
        overlays.add(overlay);
    }
    
    /**
     * Resets all overlays to their default state.
     */
    public final void resetOverlays() {
        for (Overlay overlay : overlays) {
            overlay.reset();
        }
    }
    
    @Override
    public final void initStage(Batch batch, int width, int height) {        
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(true);
        
        stage = new Stage(new ScreenViewport(camera), batch);
        stage.setDebugAll(false);
        scaledGroup = new BaseGroup();
        stage.addActor(scaledGroup);
        
        //CHECKSTYLE.OFF: InnerAssignment - Save trees
        scaledGroup.addActor(dodleGroup = new RootGroup(RootGroup.RootGroupId.ROOT_GROUP_DODLE));
        scaledGroup.addActor(drawGroup = new RootGroup(RootGroup.RootGroupId.ROOT_GROUP_DRAW));
        scaledGroup.addActor(cameraGroup = new RootGroup(RootGroup.RootGroupId.ROOT_GROUP_CAMERA));
        //CHECKSTYLE.ON: InnerAssignment
        
        cameraManager.init(scaledGroup, cameraGroup, width, height);
    }
    
    /**
     * Returns the root stage of the active DodleEngine.
     */
    public final Stage getStage() {
        return stage;
    }
    
    /**
     * Returns the RootGroup containing the active Dodle.
     */
    public final RootGroup getDodleGroup() {
        return dodleGroup;
    }
    
    /**
     * Returns the RootGroup used for drawing the current stroke.
     */
    public final RootGroup getDrawGroup() {
        return drawGroup;
    }
    
    /**
     * A group in dodle coordinates that should be used for drawing elements
     * that aren't actually in a dodle (like editor overlays).
     */
    public final Group getScaledGroup() {
        return scaledGroup;
    }
    
    /**
     * Resets to normal display mode.
     */
    public final void setDisplayMode() {
        setDisplayMode(DisplayMode.NORMAL);
    }
    
    /**
     * Changes the display mode.
     */
    public final void setDisplayMode(DisplayMode mode) {
        displayMode = mode;
        
        dodleGroup.setVisible(mode != DisplayMode.BLANK);
        
        cameraManager.hideOutsideDodle(mode == DisplayMode.ANIMATION);
        
        updateStateUi();
    }

    /**
     * Returns the current display mode
     */
    public final DisplayMode getDisplayMode() {
        return displayMode;
    }
    
    /**
     * Updates the overlays necessary for the current state.
     */
    public final void updateStateUi() {
        for (Overlay overlay : overlays) {
            overlay.update(displayMode);
        }
    }

    @Override
    public final void dispose() {
        stage.dispose();
    }
    
    private void findShapesToRasterize(DodlesActor current, ArrayList<Shape> result) {        
        if (current instanceof BaseGroup) {    
            if (current instanceof BaseDodlesViewGroup) {
                for (Object view : ((BaseDodlesViewGroup) current).getViews()) {
                    findShapesToRasterize((BaseDodlesGroup) view, result);
                }
            } else {
                BaseGroup bg = (BaseGroup) current;
                for (Actor actor : bg.getChildren()) {
                    if (actor instanceof DodlesActor) {
                        findShapesToRasterize((DodlesActor) actor, result);
                    }
                }
            }
        } else if (current instanceof Shape) {
            for (GraphicsRenderer renderer : ((Shape) current).getRenderers()) {
                if (renderer instanceof AtlasGraphicsRenderer && !((AtlasGraphicsRenderer) renderer).getNewOffsetsToDrawTo().isEmpty()) {
                    result.add((Shape) current);
                    return;
                }
            }
        }
    }
}
