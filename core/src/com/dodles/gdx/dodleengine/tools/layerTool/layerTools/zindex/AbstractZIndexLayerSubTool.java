package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.zindex;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.ZIndexCommand;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.ClickableTool;
import com.dodles.gdx.dodleengine.tools.layerTool.AbstractLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerSubToolRegistry;
import java.util.Collections;
import java.util.List;

/**
 * Core tool functionality for creating z-index commands.
 */
public abstract class AbstractZIndexLayerSubTool extends AbstractLayerSubTool implements ClickableTool {
    public static final String ACTIVATED_COLOR = "tray-background";
    
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final ObjectManager objectManager;
    private final boolean increment;
    
    public AbstractZIndexLayerSubTool(AssetProvider assetProvider, CommandFactory commandFactory, CommandManager commandManager, LayerSubToolRegistry layerSubToolRegistry, ObjectManager objectManager, boolean increment) {
        super(assetProvider);
        
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.objectManager = objectManager;
        this.increment = increment;
        
        layerSubToolRegistry.registerTool((LayerSubTool) this);
    }

    /**
     * Not implemented.
     */
    public final TextureAtlas.AtlasRegion getIcon(String icon) {
        return null;
    }


    /**
     * Not implemented.
     */
    public final List<InputHandler> getInputHandlers() {
        return Collections.emptyList();
    }

    /**
     * Not implemented.
     */
    public final void onActivation() {
    }

    /**
     * Not implemented.
     */
    public final void onDeactivation() {
    }
    
    /**
     * Returns the activated color.
     */
    public final String getActivatedColor() {
        return ACTIVATED_COLOR;
    };

    /**
     * Click handler for the tool.
     */
    @Override
    public final ClickListener onClick() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            execute();
            }
        };
    }

    public void execute() {
        DodlesActor actor = objectManager.getSelectedActor();

        if (actor != null) {
            ZIndexCommand command = (ZIndexCommand) commandFactory.createCommand(ZIndexCommand.COMMAND_NAME);
            command.init(actor.getName(), increment);
            command.execute();

            commandManager.add(command);
        }
    }
}
