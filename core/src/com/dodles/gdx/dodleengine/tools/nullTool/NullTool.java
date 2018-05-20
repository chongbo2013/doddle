package com.dodles.gdx.dodleengine.tools.nullTool;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.input.GlobalCameraInputHandler;
import com.dodles.gdx.dodleengine.input.GlobalCameraZoomHandler;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler;
import com.dodles.gdx.dodleengine.input.SelectActorInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The "Null" Tool is active when no other tool is active.
 */
@PerDodleEngine
public class NullTool extends AbstractTool implements Tool {

    //region Properties & Variables

    //Constants
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "NULL";
    public static final String ACTIVATED_COLOR = "tray-background";

    // Subsystem References
    private ObjectManager objectManager;

    // Private Variables
    private final ArrayList<InputHandler> cameraInputHandlers = new ArrayList<InputHandler>();
    private final ArrayList<InputHandler> objectInputHandlers = new ArrayList<InputHandler>();
    private boolean objectManipulationMode = false;

    //endregion Properties & Variables

    //region Constructor
    @Inject
    public NullTool(
            AssetProvider assetProvider,
            final DodleStageManager dodleStageManager,
            EventBus eventBus,
            final ObjectManager objectManager,
            final ToolRegistry toolRegistry,
            GlobalCameraInputHandler gcih,
            GlobalCameraZoomHandler gczh,
            SelectActorInputHandler saih,
            PanRotateZoomActorInputHandler przaih
    ) {
        // Super
        super(assetProvider);

        // Sub-system References
        this.objectManager = objectManager;

        // Event Listeners
        eventBus.addSubscriber(new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case SELECTED_OBJECT_CHANGED:
                        objectManipulationMode = objectManager.hasActorsSelected();
                        if (objectManager.hasActorsSelected()) {
                            dodleStageManager.setDisplayMode(DodleStageManager.DisplayMode.SHOW_OBJECT_MANIPULATION_OVERLAY);
                        }
                        break;
                }
            }
        });

        // Input Handlers - Camera Manipulation Mode
        cameraInputHandlers.add(gcih);
        cameraInputHandlers.add(gczh);
        cameraInputHandlers.add(saih);

        // Input Handlers - Object Manipulation Mode
        przaih.initialize(new PanRotateZoomActorInputHandler.ActorProvider() {
            @Override
            public DodlesActor getActor() {
                List<DodlesActor> selectedActors = objectManager.getSelectedActors();
                if (selectedActors.size() == 1) {
                    return selectedActors.get(0);
                }
                return null;
            }
        });
        objectInputHandlers.add(przaih);
        objectInputHandlers.add(saih);

        // Register Tool
        toolRegistry.registerTool(this);
    }
    //endregion Constructor

    //region Tool UI Related Functions - TODO: refactor Tool class & remove! - CAD 2017.09.14

    @Override
    public final String getName() {
        return TOOL_NAME;
    }

    @Override
    public final String getActivatedColor() {
        return ACTIVATED_COLOR;
    }

    @Override
    public final int getOrder() {
        return -1;
    }

    @Override
    public final int getRow() {
        return -1;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 432, 144, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String icon) {
        return new TextureAtlas.AtlasRegion(getToolBarIconsTexture(), 432, 144, 69, 69);
    }

    @Override
    public final String getButtonStyleName() {
        return null;
    }

    //endregion Tool UI Related Functions

    //region AbstractTool Implementation

    @Override
    public final List<InputHandler> getInputHandlers() {
        if (objectManipulationMode) {
            return objectInputHandlers;
        }
        return cameraInputHandlers;
    }

    @Override
    public void onActivation() {
        objectManipulationMode = objectManager.hasActorsSelected();
    }

    @Override
    public void onDeactivation() {
    }

    //endregion AbstractTool Implementation
}
