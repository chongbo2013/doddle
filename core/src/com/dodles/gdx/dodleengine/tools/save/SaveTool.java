package com.dodles.gdx.dodleengine.tools.save;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.StateManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The "Save" tool handles saving dodles.
 */
@PerDodleEngine
public class SaveTool extends AbstractTool implements Tool {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "SAVE";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_BOTTOM_ACTIVATED_COLOR;
    
    private final ToolRegistry toolRegistry;
    private final StateManager stateManager;
    
    @Inject
    public SaveTool(AssetProvider assetProvider, FullEditorViewState fullViewState, ToolRegistry toolRegistry, SaveToolFullEditorOverlay saveOverlay, StateManager stateManager) {
        super(assetProvider);
        
        this.toolRegistry = toolRegistry;
        this.stateManager = stateManager;
        
        toolRegistry.registerTool(this);
        fullViewState.registerOverlayView(TOOL_NAME, saveOverlay);
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
        return 3;
    }

    @Override
    public final int getOrder() {
        return 3;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 9, 150, 50, 50);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("bookmark_" + i);
        //return new TextureAtlas.AtlasRegion(getToolBarIconsTexture(), 9, 150, 50, 50);
    }

    @Override
    public final String getButtonStyleName() {
        return "save";
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return new ArrayList<InputHandler>();
    }
    
    @Override
    public void onActivation() {
    }

    @Override
    public void onDeactivation() {
    }
    
    /**
     * Saves the current dodle.
     */
    public final void saveDodle() {
        System.out.println("SaveTool::saveDodle - deprecated. Send `SAVE_DODLE` event instead.");
        //toolRegistry.setActiveTool(null);
        //stateManager.fireSaveEvent();
    }
}
