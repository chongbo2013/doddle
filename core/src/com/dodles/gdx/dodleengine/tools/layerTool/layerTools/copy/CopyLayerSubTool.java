package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.copy;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.CompoundCommand;
import com.dodles.gdx.dodleengine.commands.CopyCommand;
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
 * The "Copy" layer tool copies the selected object.
 */
@PerDodleEngine
public class CopyLayerSubTool extends AbstractLayerSubTool implements LayerSubTool, ClickableTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".Copy";
    public static final String ACTIVATED_COLOR = "tray-background";
    private final ObjectManager objectManager;
    private final CommandFactory commandFactory;
    private final OkCancelStackManager okCancelStack;
    private final CommandManager commandManager;
    private final DodleStageManager stageManager;

    @Inject
    public CopyLayerSubTool(AssetProvider assetProvider, LayerSubToolRegistry layerSubToolRegistry, ObjectManager objectManager, DodleStageManager stageManager, CommandFactory commandFactory, OkCancelStackManager okCancelStackManager, CommandManager commandManager) {
        super(assetProvider);
        this.objectManager = objectManager;
        this.commandFactory = commandFactory;
        this.okCancelStack = okCancelStackManager;
        this.commandManager = commandManager;
        this.stageManager = stageManager;

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
        return 1;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 147, 3, 44, 44);
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
                copySelectedObjects();
            }
        };
    }

    public void copySelectedObjects() {
        List<DodlesActor> actors = objectManager.getSelectedActors();
        if (actors.size() > 0) {
            CompoundCommand cc = (CompoundCommand) commandFactory.createCommand(CompoundCommand.COMMAND_NAME);
            ArrayList<Command> commands = new ArrayList<Command>();
            ArrayList<String> selectedObjectIDs = new ArrayList<String>();
            for (DodlesActor selectedItem : actors) {
                CopyCommand command = (CopyCommand) commandFactory.createCommand(CopyCommand.COMMAND_NAME);
                command.init(selectedItem.getName(), new IdDatabase());

                commands.add((command));
                command.execute();

                objectManager.setNewObjectGroup(null);
                selectedObjectIDs.add(command.getCloneID());
            }

            cc.init(commands);
            commandManager.add(cc);

            objectManager.clearSelectedActors();
            for (String id : selectedObjectIDs) {
                objectManager.addToMultiSelect(objectManager.getActor(id));
            }

            stageManager.updateStateUi();
        }
    }
}
