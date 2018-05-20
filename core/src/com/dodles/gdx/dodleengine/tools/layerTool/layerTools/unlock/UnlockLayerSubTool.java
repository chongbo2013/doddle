package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.unlock;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.LockCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
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
public class UnlockLayerSubTool extends AbstractLayerSubTool implements LayerSubTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".Unlock";
    public static final String ACTIVATED_COLOR = "tray-background";
    private final ObjectManager objectManager;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final DodleStageManager stageManager;
    private final OkCancelStackManager okCancelStack;
    private final EngineEventManager eventManager;

    @Inject
    public UnlockLayerSubTool(
            AssetProvider assetProvider,
            LayerSubToolRegistry layerSubToolRegistry,
            ObjectManager objectManager,
            CommandFactory commandFactory,
            CommandManager commandManager,
            DodleStageManager stageManager,
            OkCancelStackManager okCancelStack,
            FullEditorViewState fullViewState,
            UnlockLayerSubToolFullEditorOverlay overlay,
            EngineEventManager eventManager) {
        super(assetProvider);
        this.objectManager = objectManager;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.stageManager = stageManager;
        this.okCancelStack = okCancelStack;
        this.eventManager = eventManager;

        layerSubToolRegistry.registerTool(this);
        fullViewState.registerOverlayView(TOOL_NAME, overlay);
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
        return 6;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 146, 51, 44, 44);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        //return getToolBarIconsTextureAtlas().findRegion("save",i);
        return new TextureAtlas.AtlasRegion(getToolBarIconsTexture(), 146, 51, 44, 44);
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
    public final void onActivation() {
        objectManager.setSelectingLocked(true);

        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                LockCommand command = (LockCommand) commandFactory.createCommand(LockCommand.COMMAND_NAME);
                ArrayList<String> selectedObjectIDs = new ArrayList<String>();
                for (DodlesActor actor : objectManager.getSelectedActors()) {
                    selectedObjectIDs.add(actor.getName());
                }
                command.init(selectedObjectIDs, false);
                command.execute();

                commandManager.add(command);

                objectManager.setSelectingLocked(false);
                
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, LayerTool.TOOL_NAME);
            }
        }, new Runnable() {
            @Override
            public void run() {
                // Don't unlock
            }
        });
    }

    @Override
    public final void onDeactivation() {
        objectManager.setSelectingLocked(false);
    }
}
