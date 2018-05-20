package com.dodles.gdx.dodleengine.tools.undo;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.ClickableTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * The "Undo" tool handles undoing commands/actions.
 */
@PerDodleEngine
public class UndoTool extends AbstractTool implements Tool, ClickableTool {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "UNDO";
    public static final String ACTIVATED_COLOR = "tray-background";
    private final CommandManager commandManager;

    
    @Inject
    public UndoTool(AssetProvider assetProvider, ToolRegistry toolRegistry, CommandManager commandManager) {
        super(assetProvider);

        this.commandManager = commandManager;
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
        return -1;
    }

    @Override
    public final int getOrder() {
        return 5;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 288, 3, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("undo_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "undo";
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
                commandManager.undo();
            }
        };
    }
}
