package com.dodles.gdx.dodleengine.editor.overlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SkinAssets;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.PeelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.scenegraph.BaseGroup;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager.DisplayMode;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.Layer;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.scenegraph.RootGroup;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer.ShapeRendererCircleGraphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.shaperenderer.ShapeRendererRectangleGraphics;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.spine.SpineLayerSubTool;
import com.dodles.gdx.dodleengine.tools.nullTool.NullTool;

import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * An overlay that handles single and multi actor selection.
 */
@PerDodleEngine
public class SelectedActorOverlay extends BaseGroup implements Overlay {
    private final AssetProvider assetProvider;
    private final CameraManager cameraManager;
    private final FullEditorViewState editorViewState;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStack;
    private final DodleStageManager stageManager;
    private final EngineEventManager eventManager;
    private final EventBus eventBus;
    private final PeelStackManager peelStackManager;
    private final ToolRegistry toolRegistry;

//    private CornerImage edit;
    private CornerImage drill;
    private CornerImage peel;
//    private CornerImage phases;
    private CornerImage effects;
//    private CornerImage skeletons;
    private CornerImage options;

    private Group singleSelectGroup;
    private Shape multiSelectShape;
    private Shape dragBoundsShape;
    private Shape dottedLineShape;
    private Rectangle dragRect;
    private boolean toggled;
    private Texture overlayIcons;
    private DisplayMode displayMode;

    @Inject
    public SelectedActorOverlay(
            AssetProvider assetProvider,
            CameraManager cameraManager,
            DodleStageManager stageManager,
            FullEditorViewState editorViewState,
            ObjectManager objectManager,
            OkCancelStackManager okCancelStack,
            EngineEventManager eventManager,
            EventBus eventBus,
            PeelStackManager peelStackManager,
            ToolRegistry toolRegistry
    ) {
        // Subsystem References
        this.assetProvider = assetProvider;
        this.cameraManager = cameraManager;
        this.editorViewState = editorViewState;
        this.objectManager = objectManager;
        this.okCancelStack = okCancelStack;
        this.stageManager = stageManager;
        this.eventManager = eventManager;
        this.eventBus = eventBus;
        this.peelStackManager = peelStackManager;
        this.toolRegistry = toolRegistry;

        // Register Overlay
        stageManager.registerOverlay(this);
    }

    /**
     * Sets the current drag rectangle for live selection.
     */
    public final void setDragRectangle(Rectangle newDragRect) {
        dragRect = newDragRect;
    }

    @Override
    public final void update(DodleStageManager.DisplayMode pDisplayMode) {
        displayMode = pDisplayMode;

        //if (edit == null) {
        if (effects == null) {
            initializeOverlay();
        }

        singleSelectGroup.setVisible(false);
        multiSelectShape.setVisible(false);
        dottedLineShape.setVisible(false);

        if (displayMode == DisplayMode.SHOW_OBJECT_MANIPULATION_OVERLAY || displayMode == DisplayMode.SHOW_OBJECT_OUTLINE) {
            List<DodlesActor> selectedActors = objectManager.getSelectedActors();
            int numActors = selectedActors.size();

            if (numActors == 1 && displayMode != DisplayMode.SHOW_OBJECT_OUTLINE) {
                DodlesActor selectedActor = selectedActors.get(0);
                updateSingleSelect(selectedActor);
            } else if (numActors > 0) {
                multiSelectShape.regenerate();
                multiSelectShape.setVisible(true);
            }
        }

        dragBoundsShape.setVisible(dragRect != null);

        if (dragRect != null) {
            dragBoundsShape.regenerate();
        }

        if (!singleSelectGroup.isVisible()) {
            toggleOffCornerButtons();
        }
    }

    @Override
    public final void reset() {
        dragRect = null;
    }

    private void initializeOverlay() {

        Skin skin = assetProvider.getSkin(SkinAssets.UI);

        RootGroup overlayHost = stageManager.getDrawGroup();

        multiSelectShape = new Shape("multiselect-" + UUID.uuid(), "N/A");
        multiSelectShape.addGenerator(new MultiSelectGraphicsGenerator());
        this.addActor(multiSelectShape);

        dragBoundsShape = new Shape("dragbounds-" + UUID.uuid(), "N/A");
        dragBoundsShape.addGenerator(new DragBoundsGraphicsGenerator());
        this.addActor(dragBoundsShape);

        dottedLineShape = new Shape("dottedline-" + UUID.uuid(), "N/A");
        dottedLineShape.addGenerator(new DottedLineGraphicsGenerator());
        this.addActor(dottedLineShape);

        singleSelectGroup = new BaseGroup();

        overlayIcons = assetProvider.getTexture(TextureAssets.EDITOR_OVERLAYICONS);

        ClickListener editEvent = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                DodlesActor selected = objectManager.getSelectedActor();

                if (selected instanceof Phase) {
                    // TODO: what to do for non-phases?
                    editorViewState.pushState(objectManager);
                    Phase selectedPhase = (Phase) selected;
                    DodlesGroup selectedGroup = (DodlesGroup) selectedPhase.getParent();

                    objectManager.setActiveLayer(selectedGroup);
                    objectManager.setNewObjectGroup(selectedGroup);
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, NullTool.TOOL_NAME);
                }

                if (((DodlesGroup) selected).getParent() instanceof Layer) {
                    editorViewState.pushState(objectManager);
                    Layer selectedLayer = (Layer) ((DodlesGroup) selected).getParent();
                    Scene selectedScene = (Scene) selectedLayer.getParent();

                    objectManager.setActiveLayer(selectedScene);
                    objectManager.setNewObjectGroup(((DodlesGroup) selected));
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, NullTool.TOOL_NAME);
                }
            }
        };

        ClickListener drillEvent =  new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                objectManager.drill();
            }
        };

        ClickListener phaseEvent =  new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!editorViewState.getState().equals(PhaseLayerSubTool.TOOL_NAME)) {
                    DodlesActor selectedActor = objectManager.getSelectedActor();

                    if (selectedActor instanceof DodlesGroup) {
                        // TODO: Currently only supporting a single direct entry into the phase tool...
                        editorViewState.pushState(objectManager);
                        objectManager.setActiveLayer((DodlesGroup) selectedActor);
                        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, PhaseLayerSubTool.TOOL_NAME);
                    }
                }
            }
        };

        ClickListener peelEvent = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                final ArrayList<String> ids = objectManager.peel(objectManager.getSelectedActor().getName());
                peelStackManager.push(new Runnable() {
                    @Override
                    public void run() {
                        objectManager.unpeel(ids);
                    }
                });
            }
        };

        ClickListener skeletonEvent = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SpineLayerSubTool.TOOL_NAME);
            }
        };

        ClickListener effectsEvent = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (effects.isChecked() && !effects.isDisabled()) {
                    eventBus.publish(EventTopic.EDITOR, EventType.SELECTED_ACTOR_EFFECTS_BUTTON_PRESSED);
                    effects.setDisabled(true);
                }
            }
        };

        ClickListener toggleEvent = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                toggled = !toggled;
                setVisibleObjectMenu();
            }
        };

        ClickListener optionsEvent = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (options.isChecked() && !options.isDisabled()) {
                    options.setDisabled(true);
                    toolRegistry.setActiveTool(LayerTool.TOOL_NAME);
                    okCancelStack.push(new OkCancelStackFrame("More", true, false) {
                        @Override
                        public void execute() {
                            toolRegistry.deactivateTool(LayerTool.TOOL_NAME);
                            options.setDisabled(false);
                            options.toggle();
                        }
                    });
                }
            }
        };

        effects = setUpCornerButton(skin, "effects", true, effectsEvent);
        drill = setUpCornerButton(skin, "drill", false, drillEvent);
        peel = setUpCornerButton(skin, "peel", false, peelEvent);
        options = setUpCornerButton(skin, "options", true, optionsEvent);

        this.addActor(singleSelectGroup);

        overlayHost.addActor(this);
    }

    public void toggleEffectsOff() {
        effects.setDisabled(false);
        if (effects.isChecked()) {
            effects.toggle();
        }
    }

    private CornerImage setUpCornerButton(Skin skin, String name, boolean setChecked, ClickListener clickEvent) {
        TextureRegion unselectedRegion = skin.getRegion("overlay_" + name + "_unselected");
        TextureRegion selectedRegion = skin.getRegion("overlay_" + name + "_selected");

        CornerImage image;
        if (setChecked) {
            image = new CornerImage(unselectedRegion, selectedRegion, selectedRegion);
        }
        else {
            image = new CornerImage(unselectedRegion, selectedRegion);
        }

        image.addListener(clickEvent);
        singleSelectGroup.addActor(image);

        return image;
    }

    private void setVisibleObjectMenu() {
        /*edit.setVisible(!toggled);
        effects.setVisible(toggled);
        drill.setVisible(!toggled);
        skeletons.setVisible(toggled);
        peel.setVisible(!toggled);
        phases.setVisible(toggled);*/
    }

    private void toggleOffCornerButtons() {
        options.setDisabled(false);
        effects.setDisabled(false);
        peel.setDisabled(false);
        drill.setDisabled(false);

        if (options.isChecked()) {
            options.toggle();
        }
        if (effects.isChecked()) {
            effects.toggle();
        }
        if (peel.isChecked()) {
            peel.toggle();
        }
        if (drill.isChecked()) {
            drill.toggle();
        }
    }

    private void updateSingleSelect(DodlesActor actor) {
        Rectangle rect = CommonActorOperations.getDodleBounds(actor);

        singleSelectGroup.setVisible(true);

        float pad = 70;
        float minWidth = effects.getWidth() + drill.getWidth() + pad;
        float minHeight = effects.getHeight() + peel.getHeight() + pad;

        Viewport viewport = stageManager.getStage().getViewport();

        Vector2 stageMinCoordinates = new Vector2(viewport.getScreenX(), viewport.getScreenY());
        Vector2 dodleMinCoordinates = CommonActorOperations.getRootGroup(actor).stageToLocalCoordinates(stageMinCoordinates);
        float minX = dodleMinCoordinates.x;
        float minY = dodleMinCoordinates.y;
        Vector2 stageWidthHeight = new Vector2(viewport.getWorldWidth(), viewport.getWorldHeight());
        Vector2 dodleWidthHeight = CommonActorOperations.getRootGroup(actor).stageToLocalCoordinates(stageWidthHeight);
        float maxX = dodleWidthHeight.x;
        float maxY = dodleWidthHeight.y;

        zoomToFitActor(actor, maxX, minX, maxY, minY);

        // Enforce minimum width and height for overlay

        if (rect.width < minWidth) {
            float xDelta = (minWidth - rect.width) / 2;
            rect.x -= xDelta;
            rect.width += xDelta * 2;
        }

        if (rect.height < minHeight) {
            float yDelta = (minHeight - rect.height) / 2;
            rect.y -= yDelta;
            rect.height += yDelta * 2;
        }

        // Keep corner buttons on screen

        if (rect.x < minX) {
            rect.width -= minX - rect.x;
            rect.x = minX;

            if (rect.width < minWidth) {
                rect.width = minWidth;
            }
        }

        if (rect.x + rect.width > maxX) {
            float over = (rect.x + rect.width) - maxX;
            rect.width -= over;

            if (rect.width < minWidth) {
                rect.x -= minWidth - rect.width;
                rect.width = minWidth;
            }
        }

        if (rect.y < minY) {
            rect.height -= minY - rect.y;
            rect.y = minY;

            if (rect.height < minHeight) {
                rect.height = minHeight;
            }
        }

        if (rect.y + rect.height > maxY) {
            float over = (rect.y + rect.height) - maxY;
            rect.height -= over;

            if (rect.height < minHeight) {
                rect.y -= minHeight - rect.height;
                rect.height = minHeight;
            }
        }

        // Ensure that overlay is on the top...
        this.setZIndex(this.getParent().getChildren().size);

        setSingleSelectScalePosition(rect);

        dottedLineShape.regenerate();
        dottedLineShape.setVisible(true);

        setVisibleObjectMenu();
    }

    private void setSingleSelectScalePosition(Rectangle rect) {
        float scale = 1;

        /*if (stageManager.getScaledGroup().getScaleX() > 1) {
            scale = 1 / stageManager.getScaledGroup().getScaleX();
        }*/

        //edit.setScale(scale);
        //edit.setPosition(rect.x, rect.y);
        effects.setScale(scale);
        effects.setPosition(rect.x, rect.y);

        drill.setScale(scale);
        drill.setPosition(rect.x + rect.width - drill.getWidth() * scale, rect.y);
        //skeletons.setScale(scale);
        //skeletons.setPosition(rect.x + rect.width - skeletons.getWidth() * scale, rect.y);

        peel.setScale(scale);
        peel.setPosition(rect.x, rect.y + rect.height - peel.getHeight() * scale);
        //phases.setScale(scale);
        //phases.setPosition(rect.x, rect.y + rect.height - phases.getHeight() * scale);

        options.setScale(scale);
        options.setPosition(rect.x + rect.width - options.getWidth() * scale, rect.y + rect.height - options.getHeight() * scale);
    }

    //TODO: Move zooming to more appropriate place (like an input handler)
    private void zoomToFitActor(DodlesActor actor, float maxX, float minX, float maxY, float minY) {
        Rectangle rect = CommonActorOperations.getDodleBounds(actor);

        float rectMidX = rect.x + rect.width/2;
        float rectMidY = rect.y + rect.height/2;

        boolean zoomRight = rectMidX > maxX;
        boolean zoomLeft = rectMidX < minX;
        boolean zoomDown = rectMidY > maxY;
        boolean zoomUp = rectMidY < minY;

        if (zoomRight || zoomLeft || zoomDown || zoomUp) {
            float scaleX = 1;
            float scaleY = 1;

            float zoomX = maxX/2;
            float zoomY = maxY/2;

            if (zoomRight) {
                zoomX = 0;
                scaleX = maxX / rectMidX;
            }
            else if (zoomLeft) {
                zoomX = maxX;
                scaleX = maxX / (maxX - (rectMidX - minX));
            }

            if (zoomDown) {
                zoomY = 0;
                scaleY = maxY / rectMidY;
            }
            else if (zoomUp) {
                zoomY = maxY;
                scaleY = maxY / (maxY - (rectMidY - minY));
            }

            float scale = Math.min(scaleX, scaleY);
            Vector2 zoomPoint = new Vector2(zoomX, zoomY);
            zoomPoint = CommonActorOperations.getRootGroup(actor).localToStageCoordinates(zoomPoint);
            cameraManager.scaleGlobalViewport(scale, zoomPoint);
        }
    }

    private List<Graphics> drawBoundsRect(Rectangle rect) {
        ArrayList<Graphics> result = new ArrayList<Graphics>();

        float scale = 1;

        if (stageManager.getScaledGroup().getScaleX() > 1) {
            scale = 1 / stageManager.getScaledGroup().getScaleX();
        }

        float tickLength = 20 * scale;
        float tickHeight = 4 * scale;
        float right = rect.x + rect.width;
        float bottom = rect.y + rect.height;

        // Top Left
        result.add(new ShapeRendererRectangleGraphics(rect.x, rect.y, tickLength, tickHeight, Color.BLACK));
        result.add(new ShapeRendererRectangleGraphics(rect.x, rect.y, tickHeight, tickLength, Color.BLACK));

        // Top Right
        result.add(new ShapeRendererRectangleGraphics(right + tickHeight, rect.y, -tickLength, tickHeight, Color.BLACK));
        result.add(new ShapeRendererRectangleGraphics(right, rect.y, tickHeight, tickLength, Color.BLACK));

        // Bottom Right
        result.add(new ShapeRendererRectangleGraphics(right + tickHeight, bottom, -tickLength, tickHeight, Color.BLACK));
        result.add(new ShapeRendererRectangleGraphics(right, bottom + tickHeight, tickHeight, -tickLength, Color.BLACK));

        // Bottom Left
        result.add(new ShapeRendererRectangleGraphics(rect.x, bottom, tickLength, tickHeight, Color.BLACK));
        result.add(new ShapeRendererRectangleGraphics(rect.x, bottom + tickHeight, tickHeight, -tickLength, Color.BLACK));

        return result;
    }

    private List<Graphics> drawDottedLine() {
        ArrayList<Graphics> result = new ArrayList<Graphics>();

        float dotRadius = 3;
        int baseDotSpace = 23;
        Color color = Color.valueOf("#E2E2E2");

        float width = drill.getX() - effects.getX() - effects.getWidth();
        float height = peel.getY() - effects.getY() - effects.getHeight();

        int numHorizontalDots = ((int) width / baseDotSpace) - 1;
        int numVerticalDots = ((int) height / baseDotSpace) - 1;
        float horizontalDotSpace = width / (numHorizontalDots + 1);
        float verticalDotSpace = height / (numVerticalDots + 1);

        float x = effects.getX() + effects.getWidth();
        float y = effects.getY();

        // Top
        for (int i = 0; i < numHorizontalDots; i++) {
            x += horizontalDotSpace;
            result.add(new ShapeRendererCircleGraphics(x - dotRadius, y + dotRadius * 2, dotRadius, color));
        }

        x = drill.getX() + drill.getWidth();
        y = drill.getY() + drill.getHeight();

        // Right
        for (int i = 0; i < numVerticalDots; i++) {
            y += verticalDotSpace;
            result.add(new ShapeRendererCircleGraphics(x - dotRadius * 2, y - dotRadius, dotRadius, color));
        }

        x = options.getX();
        y = options.getY() + options.getHeight();

        // Bottom
        for (int i = 0; i < numHorizontalDots; i++) {
            x -= horizontalDotSpace;
            result.add(new ShapeRendererCircleGraphics(x - dotRadius, y - dotRadius * 2, dotRadius, color));
        }

        x = peel.getX();
        y = peel.getY();

        // Left
        for (int i = 0; i < numVerticalDots; i++) {
            y -= verticalDotSpace;
            result.add(new ShapeRendererCircleGraphics(x + dotRadius * 2, y - dotRadius, dotRadius, color));
        }

        return result;
    }

    /**
     * Generates graphics for the multi-select boxes.
     */
    private class MultiSelectGraphicsGenerator implements GraphicsGenerator {
        @Override
        public List<Graphics> generateGraphics(Shape shape) {
            ArrayList<Graphics> result = new ArrayList<Graphics>();
            List<DodlesActor> selectedActors = objectManager.getSelectedActors();

            if (selectedActors.size() > 1 || displayMode == DisplayMode.SHOW_OBJECT_OUTLINE) {
                for (DodlesActor actor : selectedActors) {
                    result.addAll(drawBoundsRect(CommonActorOperations.getDodleBounds(actor)));
                }
            }

            return result;
        }
    }

    /**
     * Generates graphics for the drag bounds box.
     */
    private class DragBoundsGraphicsGenerator implements GraphicsGenerator {
        @Override
        public List<Graphics> generateGraphics(Shape shape) {
            if (dragRect != null) {
                return drawBoundsRect(dragRect);
            }

            return new ArrayList<Graphics>();
        }
    }

    /**
     * Generates graphics for the dotted line rectangle
     */
    private class DottedLineGraphicsGenerator implements GraphicsGenerator {
        @Override
        public List<Graphics> generateGraphics(Shape shape) {
            return drawDottedLine();
        }
    }
}
