package com.dodles.gdx.dodleengine.tools.file;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.LoadableAsset;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.ImageImportCommand;
import com.dodles.gdx.dodleengine.commands.ImportDodleCommand;
import com.dodles.gdx.dodleengine.commands.Importable;
import com.dodles.gdx.dodleengine.commands.SvgImportCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.ClickableTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.crop.CropTool;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * The "File" tool handles file operations.
 */
@PerDodleEngine
public class FileTool extends AbstractTool implements Tool, ClickableTool {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "FILE";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_BOTTOM_ACTIVATED_COLOR;
    private final EngineEventManager eventManager;
    private final EventBus eventBus;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final ToolRegistry toolRegistry;
    private final ObjectManager objectManager;
    private final DodleStageManager stageManager;
    private final AssetProvider assetProvider;
    private final OkCancelStackManager okCancelStack;
    private final CameraManager cameraManager;
    private EventSubscriber dodleListener;
    private EventSubscriber fileListener;

    @Inject
    public FileTool(AssetProvider assetProvider, ToolRegistry tolRegistry, EngineEventManager evntManager, EventBus evntBus, CommandFactory commandFactory, CommandManager commandManager, ObjectManager objectManager, DodleStageManager stageManager, OkCancelStackManager okCancelStack, CameraManager cameraManager) {
        super(assetProvider);

        this.eventManager = evntManager;
        this.eventBus = evntBus;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.toolRegistry = tolRegistry;
        this.objectManager = objectManager;
        this.stageManager = stageManager;
        this.assetProvider = assetProvider;
        this.okCancelStack = okCancelStack;
        this.cameraManager = cameraManager;
        
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
        return 3;
    }

    @Override
    public final int getOrder() {
        return 1;
    }

    @Override
    public final TextureRegion getIcon() {
        return new TextureRegion(getToolBarIconsTexture(), 148, 75, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("file_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "file";
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return Collections.emptyList();
    }
    
    @Override
    public final void onActivation() {
    }

    private void importDodle(String dodleID, String json) {
        final ImportDodleCommand command = (ImportDodleCommand) commandFactory.createCommand(ImportDodleCommand.COMMAND_NAME);
        command.init(dodleID, json);
        command.execute();

        finalizeGeneralImport(command);
    }

    private void importFile(LoadableAsset asset, String file) {
        final Command command;
        boolean imageImport;
        BaseDodlesViewGroup activeLayer = objectManager.getActiveLayer();
        if (file != null && file.indexOf("<svg") == -1) {
            imageImport = true;
            command = commandFactory.createCommand(ImageImportCommand.COMMAND_NAME);
            if (file == null) {
                ((ImageImportCommand) command).init(activeLayer.getName(), activeLayer.getActiveViewID(), (TextureAssets) asset);
            } else {
                ((ImageImportCommand) command).init(activeLayer.getName(), activeLayer.getActiveViewID(), file);
            }
        } else {
            imageImport = false;
            command = commandFactory.createCommand(SvgImportCommand.COMMAND_NAME);
            if (file == null) {
                file = assetProvider.getString((StringAssets) asset);
            } else {
                file = file.replaceAll("\\r|\\n", "").replaceAll("\\s+", " ").replaceAll("> <", "><");
            }
            ((SvgImportCommand) command).init(activeLayer.getName(), activeLayer.getActiveViewID(), file);
        }
        command.execute();

        if (imageImport) {
            finalizeImageImport(command);
        } else {
            finalizeGeneralImport(command);
        }
    }

    private void finalizeGeneralImport(final Command command) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                commandManager.add(command);

                toolRegistry.setActiveTool(LayerTool.TOOL_NAME);

                objectManager.selectActor(objectManager.getActor(((Importable) command).getObjectID()));

                stageManager.updateStateUi();

                okCancelStack.push(new Runnable() {
                    @Override
                    public void run() {
                        eventBus.removeSubscriber(dodleListener);
                        eventBus.removeSubscriber(fileListener);
                        toolRegistry.setActiveTool(null);
                    }
                });
            }
        });
    }

    private void finalizeImageImport(final Command command) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                commandManager.add(command);

                eventBus.removeSubscriber(dodleListener);
                eventBus.removeSubscriber(fileListener);

                DodlesActor actor = objectManager.getActor(((Importable) command).getObjectID());
                cameraManager.focus(actor);
                stageManager.setDisplayMode(DodleStageManager.DisplayMode.SHOW_OBJECT_OUTLINE);
                objectManager.selectActor(actor);

                okCancelStack.push(new Runnable() {
                    @Override
                    public void run() {
                        toolRegistry.setActiveTool(null);
                    }
                });

                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, CropTool.TOOL_NAME);
            }
        });
    }

    @Override
    public final void onDeactivation() {
    }

    @Override
    public final ClickListener onClick() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dodleListener = new EventSubscriber(EventTopic.DEFAULT) {
                    @Override
                    public void listen(EventTopic topic, EventType eventType, final EventData data) {
                        if (EventType.CALLBACK_IMPORT_DODLE.equals(eventType)) {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    String dodleID = data.getParameters().get(0);
                                    importDodle(dodleID, data.getParameters().get(1));
                                }
                            });
                        }
                    }
                };

                fileListener = new EventSubscriber(EventTopic.DEFAULT) {
                    @Override
                    public void listen(EventTopic topic, EventType eventType, final EventData data) {
                        if (EventType.CALLBACK_IMPORT_FILE.equals(eventType)) {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    importFile(null, data.getFirstStringParam());
                                }
                            });
                        }
                    }
                };
                eventBus.addSubscriber(dodleListener);
                eventBus.addSubscriber(fileListener);
                //if the device is not webGL, we want to keep the previous settings and still go to the new state
                if(Gdx.app.getType()!= Application.ApplicationType.WebGL) {
                    eventBus.publish(EventTopic.DEFAULT, EventType.STATE_GO, "dodles.import");
                }
                eventBus.publish(EventTopic.DEFAULT, EventType.HOST_ENGINE_SHOW_IMPORT_DODLE_MODAL);
            }
        };
    }
}
