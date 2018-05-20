package com.dodles.gdx.dodleengine.tools.trash;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.StateManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.EditorInterfaceManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.ClickableTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.util.DialogUtility;
import com.dodles.gdx.dodleengine.util.ParamRunnable;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * The "Trash" tool handles removing objects.
 */
@PerDodleEngine
public class TrashTool extends AbstractTool implements Tool, ClickableTool {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "TRASH";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_BOTTOM_ACTIVATED_COLOR;
    private final AssetProvider assetProvider;
    private final DialogUtility dialogUtility;
    private final DodleStageManager stageManager;
    private final EditorInterfaceManager editorInterfaceManager;
    private final StateManager stateManager;
    
    @Inject
    public TrashTool(AssetProvider assetProvider, DialogUtility dialogUtility, DodleStageManager stageManager, ToolRegistry toolRegistry, EditorInterfaceManager editorInterfaceManager, StateManager stateManager) {
        super(assetProvider);
        this.assetProvider = assetProvider;
        this.dialogUtility = dialogUtility;
        this.editorInterfaceManager = editorInterfaceManager;
        this.stageManager = stageManager;
        this.stateManager = stateManager;

        toolRegistry.registerTool(this);
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
        return new TextureRegion(getToolBarIconsTexture(), 290, 73, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("trash_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "trash";
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return Collections.emptyList();
    }
    
    @Override
    public void onActivation() {
    }

    @Override
    public void onDeactivation() {
    }

    @Override
    public final ClickListener onClick() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialogUtility.confirm("Delete Dodle?", "Are you sure you want\nto delete this dodle?", "Yes", "No", new ParamRunnable<Boolean>() {
                    @Override
                    public void run(Boolean parameter) {
                        if (parameter) {
                            trashDodle();
                        }
                    } 
                });
            }
        };
    }
    
    /**
     * Trashes the current dodle.
     */
    public final void trashDodle() {
        stateManager.resetState();
    }
}
