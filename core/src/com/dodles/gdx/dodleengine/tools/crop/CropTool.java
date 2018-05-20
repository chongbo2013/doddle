package com.dodles.gdx.dodleengine.tools.crop;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.DeleteObjectCommand;
import com.dodles.gdx.dodleengine.commands.ImageImportCommand;
import com.dodles.gdx.dodleengine.commands.ImportedAssetConfig;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.input.CropActorInputHandler;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;
import com.dodles.gdx.dodleengine.util.PixmapFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The secret crop tool that you should only be able to access after an image import.
 */

public class CropTool extends AbstractTool implements Tool {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "CROP";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_BOTTOM_ACTIVATED_COLOR;

    private final ArrayList<InputHandler> inputHandlers = new ArrayList<InputHandler>();
    private final OkCancelStackManager okCancelStack;
    private final ObjectManager objectManager;
    private final CommandFactory commandFactory;
    private final CropActorInputHandler caih;
    private final PixmapFactory pixmapFactory;
    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    private final CommandManager commandManager;
    private DodlesGroup group;
    private Shape child;

    @Inject
    public CropTool(
            AssetProvider assetProvider,
            FullEditorViewState fullViewState,
            CropToolFullEditorOverlay overlay,
            ToolRegistry registry,
            CropActorInputHandler caih,
            OkCancelStackManager okCancelStack,
            ObjectManager pobjectManager,
            CommandFactory commandFactory,
            PixmapFactory pixmapFactory,
            EngineEventManager eventManager,
            CommandManager commandManager) {
        super(assetProvider);

        this.assetProvider = assetProvider;
        this.okCancelStack = okCancelStack;
        this.objectManager = pobjectManager;
        this.commandFactory = commandFactory;
        this.caih = caih;
        this.pixmapFactory = pixmapFactory;
        this.eventManager = eventManager;
        this.commandManager = commandManager;

        registry.registerTool(this);
        fullViewState.registerOverlayView(TOOL_NAME, overlay);

        caih.setSelectionCallback(new Runnable() {
            @Override
            public void run() {
                cropImage();
            }
        });

        caih.setMultiSelectEnabled(false);
        inputHandlers.add(caih);
    }

    @Override
    public final String getName() {
        return TOOL_NAME;
    }

    @Override
    public final String getActivatedColor() {
        return ACTIVATED_COLOR;
    }

    @Override
    public final int getRow() {
        return 0;
    }

    @Override
    public final int getOrder() {
        return 0;
    }

    @Override
    public final TextureRegion getIcon() {
        return null;
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String icon) {
        return null;
    }

    @Override
    public final String getButtonStyleName() {
        return null;
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return inputHandlers;
    }

    @Override
    public final void onActivation() {
        group = (DodlesGroup) objectManager.getSelectedActor();
        for (Actor actor : group.getChildren()) {
            if (actor instanceof Shape) {
                child = (Shape) actor;
            }
        }

        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                ImportedAssetConfig config = (ImportedAssetConfig) child.getCustomConfig().cpy();
                config.setRegion(caih.getSelection());

                DeleteObjectCommand deleteCommand = (DeleteObjectCommand) commandFactory.createCommand(DeleteObjectCommand.COMMAND_NAME);
                deleteCommand.init(group.getName());
                deleteCommand.execute();
                commandManager.add(deleteCommand);

                BaseDodlesViewGroup activeLayer = objectManager.getActiveLayer();
                ImageImportCommand command = (ImageImportCommand) commandFactory.createCommand(ImageImportCommand.COMMAND_NAME);
                command.init(activeLayer.getName(), activeLayer.getActiveViewID(), config.getFileData(), caih.getSelection());
                command.execute();
                commandManager.add(command);

                objectManager.selectActor(objectManager.getActor(command.getObjectID()));

                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, LayerTool.TOOL_NAME);
            }
        });
    }

    private Shape cropImage() {
        if (child.getCustomConfig() instanceof ImportedAssetConfig) {
            ImportedAssetConfig config = (ImportedAssetConfig) child.getCustomConfig();
            config.setRegion(caih.getSelection());
            ImportedAssetConfig.updateShapeGenerator(child, assetProvider, pixmapFactory);
            child.regenerate();

            return child;
        }

        return null;
    }

    @Override
    public final void onDeactivation() {

    }
}
