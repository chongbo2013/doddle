package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.unmerge;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.MergeCommand;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
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
 * The "Unmerge" layer tool handles unmerging grouped objects.
 */
@PerDodleEngine
public class UnmergeLayerSubTool extends AbstractLayerSubTool implements LayerSubTool, ClickableTool {
    public static final String TOOL_NAME = LayerTool.TOOL_NAME + ".Unmerge";
    public static final String ACTIVATED_COLOR = "tray-backgound";

    private final ObjectManager objectManager;
    private final CommandFactory commandFactory;
    private final DodleStageManager stageManager;
    private final CommandManager commandManager;

    @Inject
    public UnmergeLayerSubTool(AssetProvider assetProvider, LayerSubToolRegistry layerSubToolRegistry, ObjectManager objectManager, CommandFactory commandFactory, CommandManager commandManager, DodleStageManager stageManager) {
        super(assetProvider);
        this.objectManager = objectManager;
        this.commandFactory = commandFactory;
        this.stageManager = stageManager;
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
        return 1;
    }

    @Override
    public final int getOrder() {
        return 6;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 335, 3, 44, 44);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        //return getToolBarIconsTextureAtlas().findRegion("save",i);
        return new TextureAtlas.AtlasRegion(getToolBarIconsTexture(), 335, 3, 44, 44);
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
                executeUnmerge();
            }
        };
    }

    public void executeUnmerge() {
        List<DodlesActor> selectedChildren = new ArrayList<DodlesActor>(objectManager.getSelectedActors());
        for (DodlesActor actor : selectedChildren) {
            MergeCommand command = (MergeCommand) commandFactory.createCommand(MergeCommand.COMMAND_NAME);

            List<String> childIDs = new ArrayList<String>();
            if (actor instanceof DodlesGroup) {
                DodlesGroup dg = (DodlesGroup) actor;
                for (Actor child : dg.getChildren()) {
                    childIDs.add(child.getName());
                }

                command.init(dg.getName(), dg.getVisiblePhaseID(), null, null, false);
                command.execute();

                commandManager.add(command);

                objectManager.clearSelectedActors();

                for (String actorID : childIDs) {
                    objectManager.addToMultiSelect(objectManager.getActor(actorID));
                }

                stageManager.updateStateUi();
            }
        }
    }
}
