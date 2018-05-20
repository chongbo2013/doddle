package com.dodles.gdx.dodleengine.tools.redo;

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
 * The "Redo" tool handles redoing commands or actions.
 */
@PerDodleEngine
public class RedoTool extends AbstractTool implements Tool, ClickableTool {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "REDO";
    public static final String ACTIVATED_COLOR = "tray-background";
    private final CommandManager commandManager;

    @Inject
    public RedoTool(AssetProvider assetProvider, ToolRegistry toolRegistry, CommandManager commandManager) {
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
        return 10;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 357, 3, -69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("redo_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "redo";
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
                commandManager.redo();
            }
        };
    }
}
