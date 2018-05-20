package com.dodles.gdx.dodleengine.editor.inline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SkinAssets;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.brushes.BrushRegistry;
import com.dodles.gdx.dodleengine.brushes.PencilBrush;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.editor.EditorInterface;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.draw.DrawTool;
import com.dodles.gdx.dodleengine.tools.font.FontTool;
import com.dodles.gdx.dodleengine.tools.save.SaveTool;
import com.dodles.gdx.dodleengine.tools.trash.TrashTool;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.github.czyzby.kiwi.util.gdx.scene2d.Alignment;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * Inline editor interface.
 */
@PerDodleEngine
public class InlineEditorInterface implements EditorInterface {
    private final AssetProvider assetProvider;
    private final BrushRegistry brushRegistry;
    private final CommandManager commandManager;
    private final EngineEventManager eventManager;
    private final EventBus eventBus;
    private final InlineEditorViewState viewState;
    private final ToolRegistry toolRegistry;
    
    private EngineEventListener editorStateChangedListener;
    private EventSubscriber toolChangedListener;
    private Table rootTable;
    private Stage stage;
    private Skin skin;
    
    private Button redoButton;
    
    @Inject
    public InlineEditorInterface(
        AssetProvider assetProvider,
        BrushRegistry brushRegistry,
        CommandManager commandManager,
        EngineEventManager eventManager,
        EventBus eventBus,
        InlineEditorViewState viewState,
        ToolRegistry toolRegistry
    ) {
        this.assetProvider = assetProvider;
        this.brushRegistry = brushRegistry;
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.eventBus = eventBus;
        this.toolRegistry = toolRegistry;
        this.viewState = viewState;
    }
    
    /**
     * Returns the height of a "row" in the full editor based on the resolution of the device.
     */
    public static int getInterfaceRowSize() {
        if (Gdx.graphics.getWidth() > 500) {
            return 100;
        }
        
        return 50;
    }
    
    @Override
    public final void activate(Stage stageParam) {
        stage = stageParam;
        
        if (skin == null) {
            skin = assetProvider.getSkin(SkinAssets.UI_SKIN);

            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .argument("oneRowSize", getInterfaceRowSize())
                    .argument("sideColumnSize", getInterfaceRowSize() * 1.25)
                    .argument("twoRowSize", getInterfaceRowSize() * 2)
                    .build();

            String template = assetProvider.getString(StringAssets.TEMPLATE_INLINE_EDITOR);
            rootTable = (Table) parser.parseTemplate(template).get(0);
            rootTable.setFillParent(true);
            
            LmlUtility.setPctHeightRelativeToParent(rootTable, "rightColumnSpace", 0.5f);
            LmlUtility.setPctHeightRelativeToParent(rootTable, "invisibleColumnSpace", 0.75f);
            
            configureCircleButton("drawButton", DrawTool.TOOL_NAME);
            configureCircleButton("fontButton", FontTool.TOOL_NAME);
            configureCircleButton("contentButton", null);
            configureUndoButton();
            redoButton = configureRedoButton();
            configureTrashButton();
            configureSendButton();
        }
        
        editorStateChangedListener = new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
            @Override
            public void listen(EngineEventData data) {
                viewState.changeState(data.getFirstStringParam(), rootTable, skin);
            }
        };

        toolChangedListener = new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic topic, EventType eventType, EventData data) {
                if (EventType.TOOL_CHANGED.equals(eventType)) {
                    // If a tool gets changed, we want to change state to the base state for that tool.
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, data.getFirstStringParam());
                }
            }
        };
        
        eventManager.addListener(editorStateChangedListener);
        eventBus.addSubscriber(toolChangedListener);
        
        stage.addActor(rootTable);
        brushRegistry.setActiveBrush(PencilBrush.BRUSH_NAME);
        toolRegistry.setActiveTool(DrawTool.TOOL_NAME);
    }

    @Override
    public final void deactivate() {
        eventManager.removeListener(editorStateChangedListener);
        eventBus.removeSubscriber(toolChangedListener);
        rootTable.remove();
    }

    @Override
    public final EditorInterface.Padding getInterfacePadding() {
        return new EditorInterface.Padding(getInterfaceRowSize() * 1.25f, getInterfaceRowSize() * 1.25f, 0, getInterfaceRowSize() * 2);
    }
    
    private Button configureUndoButton() {
        Button undoButton = configureCircleButton("undoButton", null);
        
        undoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (commandManager.size() > 0) {
                    commandManager.undo();
                    redoButton.setVisible(true);
                    setSendButtonText("Done");
                }
            }
        });
        
        return undoButton;
    }
    
    private Button configureRedoButton() {
        final Button newRedoButton = configureCircleButton("redoButton", null);
        
        newRedoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commandManager.redo();
                newRedoButton.setVisible(commandManager.canRedo());
                
                if (!commandManager.canRedo()) {
                    setSendButtonText("Send");
                }
            }
        });
        
        newRedoButton.setVisible(false);
        
        return newRedoButton; 
    }
    
    private Button configureTrashButton() {
        final Button newTrashButton = configureCircleButton("trashButton", null);
        
        newTrashButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((TrashTool) toolRegistry.getTool(TrashTool.TOOL_NAME)).trashDodle();
            }
        });
        
        return newTrashButton;
    }
    
    private Button configureSendButton() {
        final Button newSendButton = configureCircleButton("sendButton", null);
        
        newSendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (redoButton.isVisible()) {
                    redoButton.setVisible(false);
                    setSendButtonText("Send");
                } else {
                    ((SaveTool) toolRegistry.getTool(SaveTool.TOOL_NAME)).saveDodle();
                }
            }
        });
        
        return newSendButton;
    }
    
    private void setSendButtonText(String newText) {
        Label label = rootTable.findActor("sendButton_label");
        label.setText(newText);
    }
    
    private Button configureCircleButton(String id, final String toolName) {
        Button button = rootTable.findActor(id);
        LmlUtility.setPctHeightRelativeToParent(button, 0.25f);
        
        if (toolName != null) {
            button.addListener(new ClickListener() {
                 @Override
                public void clicked(InputEvent event, float x, float y) {
                    toolRegistry.setActiveTool(toolName);
                }
            });
        }
        
        Image image = rootTable.findActor(id + "_image");        
        TextureRegion tr = new TextureRegion(assetProvider.getTexture(TextureAssets.EDITOR_WHITE_COLOR_ROUND_BUTTON));
        image.setDrawable(new TextureRegionDrawable(tr));
        image.setColor(Color.RED);
        
        Table leftColumn = rootTable.findActor("leftColumn");
        final Table imageTable = (Table) image.getParent();
        imageTable.getCell(image).size(new Value() {
            @Override
            public float get(Actor context) {
                float minDim = Math.min(imageTable.getWidth(), imageTable.getHeight());
                return minDim * .95f;
            }
            
        });
        
        Label label = rootTable.findActor(id + "_label");
        label.setAlignment(Alignment.CENTER.get());
        label.setFontScale(0.5f);
        return button;
    }

    /**
     * wire into the resize event in case you need to change some
     * UI elements.
     */
    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void reset() {
    }
}
