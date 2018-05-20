package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.flipvertically;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.FlipCommand;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.ClickableTool;
import com.dodles.gdx.dodleengine.tools.layerTool.AbstractLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * The "Flip Vertically" layer tool handles flipping the selected object vertically.
 */
@PerDodleEngine
public class FlipVerticallyLayerSubTool extends AbstractLayerSubTool implements LayerSubTool, ClickableTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".Flip_V";
    public static final String ACTIVATED_COLOR = "tray-background";
    private final ObjectManager objectManager;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;

    @Inject
    public FlipVerticallyLayerSubTool(AssetProvider assetProvider, LayerSubToolRegistry layerSubToolRegistry, ObjectManager objectManager, CommandFactory commandFactory, CommandManager commandManager) {
        super(assetProvider);
        this.objectManager = objectManager;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;

        layerSubToolRegistry.registerTool(this);
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
        return 4;
    }

    @Override
    public final int getOrder() {
        return 2;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 1, 51, 44, 44);
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
            executeFlip();
            }
        };
    }

    public void executeFlip() {
        for (DodlesActor actor : objectManager.getSelectedActors()) {
            FlipCommand command = (FlipCommand) commandFactory.createCommand(FlipCommand.COMMAND_NAME);
            command.init(actor.getName(), true);
            command.execute();

            commandManager.add(command);
        }
    }
}
