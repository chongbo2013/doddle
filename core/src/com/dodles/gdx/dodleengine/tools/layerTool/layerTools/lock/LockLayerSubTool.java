package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.lock;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.LockCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.ClickableTool;
import com.dodles.gdx.dodleengine.tools.layerTool.AbstractLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The "Lock" layer tool handles locking the selected objects.
 */
@PerDodleEngine
public class LockLayerSubTool extends AbstractLayerSubTool implements LayerSubTool, ClickableTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".Lock";
    public static final String ACTIVATED_COLOR = "tray-background";
    private final ObjectManager objectManager;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final DodleStageManager stageManager;
    private final OkCancelStackManager okCancelStack;

    @Inject
    public LockLayerSubTool(AssetProvider assetProvider, LayerSubToolRegistry layerSubToolRegistry, ObjectManager objectManager, CommandFactory commandFactory, CommandManager commandManager, DodleStageManager stageManager, OkCancelStackManager okCancelStack) {
        super(assetProvider);
        this.objectManager = objectManager;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.stageManager = stageManager;
        this.okCancelStack = okCancelStack;

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
        return 2;
    }

    @Override
    public final int getOrder() {
        return 5;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 99, 51, 44, 44);
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
                LockCommand command = (LockCommand) commandFactory.createCommand(LockCommand.COMMAND_NAME);
                ArrayList<String> selectedObjectIDs = new ArrayList<String>();
                for (DodlesActor actor : objectManager.getSelectedActors()) {
                    selectedObjectIDs.add(actor.getName());
                }
                command.init(selectedObjectIDs, true);
                command.execute();

                commandManager.add(command);

                objectManager.clearSelectedActors();

                stageManager.updateStateUi();

                okCancelStack.popOk();
            }
        };
    }
}
