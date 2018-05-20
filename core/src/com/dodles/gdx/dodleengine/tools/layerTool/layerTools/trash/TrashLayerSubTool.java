package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.trash;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.DeleteObjectCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.ClickableTool;
import com.dodles.gdx.dodleengine.tools.layerTool.AbstractLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;
import com.dodles.gdx.dodleengine.tools.nullTool.NullTool;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * The "Trash" tool handles removing objects.
 */
@PerDodleEngine
public class TrashLayerSubTool extends AbstractLayerSubTool implements LayerSubTool, ClickableTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".Trash";
    public static final String ACTIVATED_COLOR = "tray-background";
    private final ObjectManager objectManager;
    private final CommandManager commandManager;
    private final CommandFactory commandFactory;
    private final OkCancelStackManager okCancelStack;
    private final EngineEventManager eventManager;

    @Inject
    public TrashLayerSubTool(AssetProvider assetProvider, LayerSubToolRegistry layerSubToolRegistry, ObjectManager objectManager, CommandFactory commandFactory, CommandManager commandManager, OkCancelStackManager okCancelStack, EngineEventManager eventManager) {
        super(assetProvider);
        this.objectManager = objectManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
        this.okCancelStack = okCancelStack;
        this.eventManager = eventManager;


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
        return 1;
    }

    @Override
    public final int getOrder() {
        return 2;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 192, 2, 44, 44);
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
                deleteSelectedObjects();
            }
        };
    }

    public void deleteSelectedObjects() {
        for (DodlesActor actor : objectManager.getSelectedActors()) {
            DeleteObjectCommand command = (DeleteObjectCommand) commandFactory.createCommand(DeleteObjectCommand.COMMAND_NAME);
            command.init(actor.getName());
            command.execute();

            commandManager.add(command);
        }

        if (okCancelStack.size() > 0) {
            okCancelStack.popOk();
        } else {
            objectManager.clearSelectedActors();
            eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, NullTool.TOOL_NAME);
        }
    }
}
