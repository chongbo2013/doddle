package com.dodles.gdx.dodleengine.tools.scene;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.PerDodleEngine;
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
 * The "Scene" tool handles removing objects.
 */
@PerDodleEngine
public class SceneTool extends AbstractTool implements Tool {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "SCENE";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_MIDDLE_ACTIVATED_COLOR;
    
    @Inject
    public SceneTool(AssetProvider assetProvider, FullEditorViewState fullViewState, ToolRegistry toolRegistry, SceneManagementFullEditorRowOne managementRow, SceneManagementFullEditorRowTwo buttonRow) {
        super(assetProvider);
        
        toolRegistry.registerTool(this);
        fullViewState.registerRow1View(TOOL_NAME, managementRow);
        fullViewState.registerRow2View(TOOL_NAME, buttonRow);
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
        return 1;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 75, 75, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("scenes_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "scenes";
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
}
