package com.dodles.gdx.dodleengine.tools.chest;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler;
import com.dodles.gdx.dodleengine.input.SelectActorInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager.DisplayMode;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The "Chest" tool handles storing objects in the chest.
 */
@PerDodleEngine
public class ChestTool extends AbstractTool implements Tool {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "CHEST";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_MIDDLE_ACTIVATED_COLOR;
    
    private final DodleStageManager stageManager;
    private final ArrayList<InputHandler> inputHandlers = new ArrayList<InputHandler>();
    
    @Inject
    public ChestTool(
        AssetProvider assetProvider,
        ChestCharacterFullEditorRow chestCharacterRow,
        DodleStageManager stageManager,
        FullEditorViewState fullViewState,
        final ObjectManager objectManager,
        PanRotateZoomActorInputHandler przaih,
        SelectActorInputHandler saih,
        ToolRegistry toolRegistry
    ) {
        super(assetProvider);
        
        this.stageManager = stageManager;
        
        toolRegistry.registerTool(this);
        fullViewState.registerRow1View(TOOL_NAME, chestCharacterRow);
        
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
        inputHandlers.add(przaih);
        
        saih.setMultiSelectEnabled(true);
        inputHandlers.add(saih);
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
        return 2;
    }

    @Override
    public final int getOrder() {
        return 2;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 218, 145, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("chest_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "chest";
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return inputHandlers;
    }
    
    @Override
    public final void onActivation() {
        stageManager.setDisplayMode(DisplayMode.SHOW_OBJECT_MANIPULATION_OVERLAY);
    }

    @Override
    public final void onDeactivation() {
        stageManager.setDisplayMode();
    }
}
