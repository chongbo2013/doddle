package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.full.EmptyEditorOverlay;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.tools.layerTool.AbstractLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings.PhaseConfigView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Tool that manages phase creation/editing.
 */
@PerDodleEngine
public class PhaseLayerSubTool extends AbstractLayerSubTool implements LayerSubTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".PHASE";
    public static final String ACTIVATED_COLOR = "tray-background";
    
    private final ArrayList<InputHandler> inputHandlers = new ArrayList<InputHandler>();
    private final DodleStageManager stageManager;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    private final PhaseConfigView phaseConfigView;
    
    @Inject
    public PhaseLayerSubTool(
        AssetProvider assetProvider,
        DodleStageManager stageManager,
        EmptyEditorOverlay emptyOverlay,
        EngineEventManager eventManager,
        FullEditorViewState fullViewState,
        final ObjectManager objectManager,
        PanRotateZoomActorInputHandler przaih,
        LayerSubToolRegistry layerSubToolRegistry,
        PhaseConfigView phaseConfigView
    ) {
        super(assetProvider);
        
        this.eventManager = eventManager;
        this.objectManager = objectManager;
        this.stageManager = stageManager;
        this.phaseConfigView = phaseConfigView;

        przaih.initialize(new PanRotateZoomActorInputHandler.ActorProvider() {
            @Override
            public DodlesActor getActor() {
                return objectManager.getSelectedActor();
            }
        });
        inputHandlers.add(przaih);
        
        layerSubToolRegistry.registerTool(this);
        fullViewState.registerOverlayView(TOOL_NAME, emptyOverlay);
    }
    
    @Override
    public final int getRow() {
        return -1;
    }

    @Override
    public final int getOrder() {
        return -1;
    }

    @Override
    public final String getName() {
        return TOOL_NAME;
    }

    @Override
    public final String getActivatedColor() {
        return ACTIVATED_COLOR;
    };

    @Override
    public final TextureRegion getIcon() {
        return null;
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        //return getToolBarIconsTextureAtlas().findRegion("save",i);
        return null;
    }

    @Override
    public final String getButtonStyleName() {
        return null;
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return inputHandlers;
    }

    @Override
    public final void onActivation() {
        stageManager.setDisplayMode(DodleStageManager.DisplayMode.SHOW_OBJECT_MANIPULATION_OVERLAY);
        
        DodlesActor activeLayer = objectManager.getActiveLayer();
        
        if (activeLayer instanceof DodlesGroup) {
            // Possibly disable stencil if all phases have children
            DodlesGroup pg = (DodlesGroup) activeLayer;
            boolean disableStencil = true;


            
            for (Phase child : pg.getPhases()) {
                if (child.getChildren().size == 0) {
                    disableStencil = false;
                    break;
                }
            }
            
            if (disableStencil) {
                pg.setStencilPhaseID(null);
            }
        }
        
        eventManager.fireEvent(EngineEventType.PHASEUI_ACTIVE_PHASE_MODIFIED);
    }

    @Override
    public final void onDeactivation() {
        stageManager.setDisplayMode();
        eventManager.fireEvent(EngineEventType.PHASEUI_ACTIVE_PHASE_MODIFIED);
    }
}
