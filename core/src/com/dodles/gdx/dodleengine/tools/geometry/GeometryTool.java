package com.dodles.gdx.dodleengine.tools.geometry;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.geometry.Geometry;
import com.dodles.gdx.dodleengine.geometry.GeometryConfig;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;
import java.util.List;

/**
 * The "Shape" tool handles adding shapes to the canvas.
 */
@PerDodleEngine
public class GeometryTool extends AbstractTool implements Tool {

    //region Properties & Variables

    // Constants
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "GEOMETRY";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_TOP_ACTIVATED_COLOR;

    // Sub-system References
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStackManager;
    private final ToolRegistry toolRegistry;

    // Subtool Management
    private boolean isActive = false;
    private AbstractGeometrySubtool activeSubtool;
    private final StandardGeometrySubtool standardGeometrySubtool;
    private final CustomGeometrySubtool customGeometrySubtool;

    // Misc. Local Variables
    private boolean isDeactivating = false;
    private String initialLayer;

    //region Properties & Variables

    //region Constructors

    @Inject
    public GeometryTool(
            AssetProvider assetProvider,
            ObjectManager objectManager,
            OkCancelStackManager okCancelStackManager,
            ToolRegistry toolRegistry,
            final StandardGeometrySubtool standardGeometrySubtool,
            final CustomGeometrySubtool customGeometrySubtool
    ) {
        // construct super
        super(assetProvider);

        // Sub-system references
        this.objectManager = objectManager;
        this.okCancelStackManager = okCancelStackManager;
        this.toolRegistry = toolRegistry;

        // Subtool references
        this.activeSubtool = standardGeometrySubtool;
        this.standardGeometrySubtool = standardGeometrySubtool;
        this.customGeometrySubtool = customGeometrySubtool;

        // Register tool
        toolRegistry.registerTool(this);
    }

    //endregion Constructors

    //region Tool UI Related Functions - TODO: refactor Tool class & remove! - CAD 2017.09.14

    @Override
    public final String getName() {
        return TOOL_NAME;
    }

    @Override
    public final String getActivatedColor() {
        return ACTIVATED_COLOR;
    };

    @Override
    public final int getRow() {
        return 1;
    }

    @Override
    public final int getOrder() {
        return 3;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 145, 3, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("shapes_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "shapes";
    }

    //endregion Tool UI Related Functions

    //region Public API

    @Override
    public final void onActivation() {
        isActive = true;
        objectManager.clearSelectedActors();

        // Manage Ok / Cancel Stack
        initialLayer = okCancelStackManager.getLayerID();
        okCancelStackManager.push(new OkCancelStackFrame("Shapes", true, false) {
            @Override
            public void execute() {
                // Deactivate the tool if its not already deactivating (ie, the user pressed the OK button)
                if (!isDeactivating) {
                    toolRegistry.deactivateTool(TOOL_NAME);
                }
            }
        });
        okCancelStackManager.nextLayer();

        if (isCustomShapeModeActive()) {
            System.out.println("GeometryTool::onActivation - custom shape mode is already active. This is a bug");
            customGeometrySubtool.onDeactivation();
            activeSubtool = standardGeometrySubtool;
        }
        activeSubtool.onActivation();
    }

    @Override
    public final void onDeactivation() {
        isDeactivating = true;
        if (isCustomShapeModeActive()) {
            customGeometrySubtool.onDeactivation();
            activeSubtool = standardGeometrySubtool;
        } else {
            standardGeometrySubtool.onDeactivation();
        }
        okCancelStackManager.popThroughLayer(initialLayer, false);
        isDeactivating = false;
        isActive = false;
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return activeSubtool.getInputHandlers();
    }

    /**
     * Returns the shape that's being actively modified.
     */
    public final Shape getActiveShape() {
        return activeSubtool.getActiveShape();
    }

    /**
     * Regenerates the shape and its handles.
     */
    public final void regenerateShape() {
        activeSubtool.regenerateShape();
    }

    /**
     * Method to add the shape to the canvas.
     */
    public final void addShapeToCanvas(Geometry geometry, GeometryConfig config) {
        activeSubtool.addShapeToCanvas(geometry, config);
    }

    public final boolean isCustomShapeModeActive() {
        return activeSubtool == customGeometrySubtool;
    }

    public final void setCustomShapeModeActive(boolean activate) {
        if (isCustomShapeModeActive() != activate && isActive) {
            activeSubtool.onDeactivation();
            activeSubtool = (isCustomShapeModeActive() ? standardGeometrySubtool : customGeometrySubtool);
            activeSubtool.onActivation();
        }
    }

    //endregion Public API

}