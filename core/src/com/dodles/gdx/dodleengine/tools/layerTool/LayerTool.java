package com.dodles.gdx.dodleengine.tools.layerTool;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler.ActorProvider;
import com.dodles.gdx.dodleengine.input.SelectActorInputHandler;
import com.dodles.gdx.dodleengine.input.TwoFingerGlobalCameraInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The "Layer" tool handles selecting objects.
 */
@PerDodleEngine
public class LayerTool extends AbstractTool implements Tool {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "LAYER";
    public static final String ACTIVATED_COLOR = "tray-background";
    
    private final DodleStageManager stageManager;
    private final LayerSubToolRegistry subToolRegistry;
    private final ArrayList<InputHandler> inputHandlers = new ArrayList<InputHandler>();

    @Inject
    public LayerTool(
        AssetProvider assetProvider,
        final DodleStageManager stageManager,
        FullEditorViewState fullViewState,
        LayerToolFullEditorOverlay overlay,
        LayerSubToolRegistry subToolRegistry,
        final ObjectManager objectManager,
        PanRotateZoomActorInputHandler przaih,
        SelectActorInputHandler saih,
        TwoFingerGlobalCameraInputHandler tfgcaih,
        ToolRegistry toolRegistry
    ) {
        super(assetProvider);
        
        toolRegistry.registerTool(this);
        this.stageManager = stageManager;
        this.subToolRegistry = subToolRegistry;
        fullViewState.registerOverlayView(LayerTool.TOOL_NAME, overlay);
        
        przaih.initialize(new ActorProvider() {
            @Override
            public DodlesActor getActor() {
                List<DodlesActor> selectedActors = objectManager.getSelectedActors();
                
                if (selectedActors.size() == 1) {
                    return selectedActors.get(0);
                }
                
                return null;
            }
        });
        inputHandlers.add(przaih);
        
        saih.setMultiSelectEnabled(true);
        inputHandlers.add(saih);
        inputHandlers.add(tfgcaih);
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
    public final int getRow() {
        return -1;
    }

    @Override
    public final int getOrder() {
        return -1;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 9, 150, 50, 50);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        //return getToolBarIconsTextureAtlas().findRegion("save",i);
        // for now adapt by using the old texture
        return new TextureAtlas.AtlasRegion(getToolBarIconsTexture(), 9, 150, 50, 50);
    }

    @Override
    public final String getButtonStyleName() {
        return "layer";
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        LayerSubTool activeSubTool = subToolRegistry.getActiveTool();
        
        if (activeSubTool != null && activeSubTool.getInputHandlers().size() > 0) {
            return activeSubTool.getInputHandlers();
        }
            
        return inputHandlers;
    }

    @Override
    public final void onActivation() {
    }

    @Override
    public final void onDeactivation() {
    }
}
