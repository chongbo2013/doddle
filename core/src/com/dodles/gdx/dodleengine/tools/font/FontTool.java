package com.dodles.gdx.dodleengine.tools.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SkinAssets;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.CompoundCommand;
import com.dodles.gdx.dodleengine.commands.DeleteObjectCommand;
import com.dodles.gdx.dodleengine.commands.DrawFontCommand;
import com.dodles.gdx.dodleengine.commands.MergeCommand;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.editor.full.strokeconfigrow.FullEditorSelectStrokeConfigRow;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.input.GlobalCameraZoomHandler;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.input.InteractionData;
import com.dodles.gdx.dodleengine.input.TouchInputHandler;
import com.dodles.gdx.dodleengine.input.TwoFingerGlobalCameraInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesContainer;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.TextShape;
import com.dodles.gdx.dodleengine.tools.AbstractTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolHelper;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The "Font" tool handles text characters on the canvas.
 */
@PerDodleEngine
public class FontTool extends AbstractTool implements Tool, TouchInputHandler {
    public static final String TOOL_NAME = ToolRegistry.TOOL_NAMESPACE + "FONT";
    public static final String SELECT_FONT_STATE = TOOL_NAME + ".selectFont";
    public static final String ACTIVATED_COLOR = FullEditorViewState.TOOLBAR_TOP_ACTIVATED_COLOR;

    private final ArrayList<InputHandler> inputHandlers = new ArrayList<InputHandler>();
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final DodleStageManager stageManager;
    private final EditorState editorState;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStack;
    private final ToolHelper toolHelper;
    private final EngineEventManager eventManager;

    private MergeCommand newGroupMergeCommand;
    private EditTextData editTextData;
    private TextShape editActor;
    private StrokeConfig defaultStrokeConfig;
    private String initialLayer;

    @Inject
    public FontTool(
            AssetProvider assetProvider,
            CommandFactory commandFactory,
            CommandManager commandManager,
            DodleStageManager stageManager,
            EditorState editorState,
            FontSelectionFullEditorOverlay overlay,
            SelectFontRowEditorView selectFontRow,
            FullEditorSelectStrokeConfigRow strokeConfigRow,
            FullEditorViewState fullViewState,
            ObjectManager objectManager,
            OkCancelStackManager okCancelStack,
            ToolHelper toolHelper,
            ToolRegistry toolRegistry,
            EngineEventManager eventManager,
            TwoFingerGlobalCameraInputHandler tfgcih,
            GlobalCameraZoomHandler gczh
    ) {
        super(assetProvider);

        toolRegistry.registerTool(this);

        fullViewState.registerRow1View(TOOL_NAME, strokeConfigRow);
        fullViewState.registerRow2View(TOOL_NAME, selectFontRow);
        fullViewState.registerOverlayView(SELECT_FONT_STATE, overlay);

        inputHandlers.add(this);
        inputHandlers.add(tfgcih);
        inputHandlers.add(gczh);
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.editorState = editorState;
        this.objectManager = objectManager;
        this.okCancelStack = okCancelStack;
        this.stageManager = stageManager;
        this.toolHelper = toolHelper;
        this.eventManager = eventManager;
        
        defaultStrokeConfig = new StrokeConfig();
        defaultStrokeConfig.setSize(50);
        defaultStrokeConfig.setColor(Color.BLACK);
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
        return new TextureRegion(getToolBarIconsTexture(), 72, 3, 69, 69);
    }

    @Override
    public final TextureAtlas.AtlasRegion getIcon(String i) {
        return getToolBarIconsTextureAtlas().findRegion("font_" + i);
    }

    @Override
    public final String getButtonStyleName() {
        return "font";
    }

    @Override
    public final List<InputHandler> getInputHandlers() {
        return inputHandlers;
    }

    @Override
    public final void onActivation() {
        initialLayer = okCancelStack.getLayerID();
        
        editorState.setStrokeConfig(defaultStrokeConfig.cpy());
    }

    @Override
    public final void onDeactivation() {
        if (okCancelStack.getLayerID().equals(initialLayer)) {
            // Pop OK stack layer when exiting font tool...
            okCancelStack.popLayer(false);
        }
        
        defaultStrokeConfig = editorState.getStrokeConfig().cpy();
    }

    @Override
    public final void handleTouchStart(InteractionData startData, int pointer) {
        if (editTextData == null) {
            DodlesActor selectedChild = objectManager.selectLeaf(startData.getDodlePoint());

            if (selectedChild instanceof TextShape) {
                editActor = (TextShape) selectedChild;
                editTextData = new EditTextData();
                editTextData.previousText = editActor.getText();
                editTextData.textID = editActor.getName();
                editTextData.point = new Vector2(editActor.getX(), editActor.getY());
                editTextData.rotation = CommonActorOperations.getDodleRotation(editActor);
                editTextData.strokeConfig = editActor.getStrokeConfig();
            } else {
                newGroupMergeCommand = toolHelper.possiblyPushCloseNewObjectGroup(TOOL_NAME);
                editTextData = new EditTextData();
                editTextData.textID = UUID.uuid();
                editTextData.groupID = objectManager.getNewObjectGroup().getName();
                editTextData.point = startData.getDodlePoint();
                editTextData.rotation = 0;
                editTextData.strokeConfig = editorState.getStrokeConfig();
            }

            setupTextBox();

            okCancelStack.push(new Runnable() {
                @Override
                public void run() {
                    commitChanges();
                }
            }, new Runnable() {
                @Override
                public void run() {
                    cancelChanges();
                }
            });
        }
    }

    @Override
    public void handleTouchMove(InteractionData moveData, int pointer) {
    }

    @Override
    public void handleTouchEnd(InteractionData endData, int pointer) {
    }

    @Override
    public void handleTouchCancel() {

    }

    private void setupTextBox() {
        if (editActor != null) {
            editActor.setVisible(false);
        }

        String text = "";

        if (editTextData.previousText != null) {
            text = editTextData.previousText;
        }
        
        final DodlesTextField editTextField = new DodlesTextField(text, assetProvider.getSkin(SkinAssets.UI_SKIN), assetProvider.getFont(editTextData.strokeConfig.getFont()), editTextData.strokeConfig);
        DodlesContainer container = new DodlesContainer();
        container.setTransform(true);
        container.setScale(1, -1);
        container.setOriginX(editTextField.getPrefWidth() / -2);
        container.setPosition(editTextData.point.x + editTextField.getPrefWidth() / 2, editTextData.point.y);
        container.setActor(editTextField);
        this.eventManager.fireEvent(EngineEventType.SHOW_KEYBOARD_REQUEST, Boolean.TRUE.toString());
        this.eventManager.addListener(new EngineEventListener(EngineEventType.KEY_PRESS) {
            @Override
            public void listen(EngineEventData data) {
                // TODO handle font sizes better
                editTextField.setFontSize(80);
                String stringAdded = data.getFirstStringParam();
                
                if (stringAdded == null || stringAdded.length() == 0) {
                    stringAdded = "";
                }
   
                if (stringAdded.length() > 0) {
                    Gdx.app.getInput().getInputProcessor().keyTyped(stringAdded.charAt(0));
                }
                Gdx.app.debug("FontTool", "Got key press: " + stringAdded);
            }
        });

        stageManager.getDrawGroup().addActor(container);
        stageManager.getStage().setKeyboardFocus(editTextField);
        editTextField.getOnscreenKeyboard().show(true);
        editTextData.textField = editTextField;
    }

    private void cancelChanges() {
        if (editActor != null) {
            editActor.setVisible(true);
        }

        editActor = null;
        editTextData.textField.getOnscreenKeyboard().show(false);
        editTextData.textField.getParent().remove();
        editTextData = null;
    }

    private void commitChanges() {
        Command command = null;
        String text = editTextData.textField.getText();

        if (text.length() > 0) {
            command = commandFactory.createCommand(DrawFontCommand.COMMAND_NAME);

            if (editActor != null) {
                ((DrawFontCommand) command).initEdit(editTextData.textID, text);
            } else {
                ((DrawFontCommand) command).initCreate(editTextData.textID, editTextData.groupID, editTextData.phaseID, editTextData.strokeConfig, text, editTextData.point);
            }
        } else if (editActor != null) {
            Actor deleteTarget = editActor;

            while (!(deleteTarget.getParent() instanceof Scene) && ((Group) deleteTarget.getParent()).getChildren().size == 1) {
                deleteTarget = deleteTarget.getParent();
            }

            command = commandFactory.createCommand(DeleteObjectCommand.COMMAND_NAME);
            ((DeleteObjectCommand) command).init(deleteTarget.getName());
        }

        if (command != null) {
            Command commandToAdd = command;

            if (newGroupMergeCommand != null) {
                CompoundCommand cc = (CompoundCommand) commandFactory.createCommand(CompoundCommand.COMMAND_NAME);
                ArrayList commands = new ArrayList();
                commands.add(newGroupMergeCommand);
                commands.add(command);
                cc.init(commands);
                commandToAdd = cc;
            }

            commandManager.add(commandToAdd);

            command.execute();
        } else if (newGroupMergeCommand != null) {
            newGroupMergeCommand.undo();
            okCancelStack.pop(true);
            objectManager.setNewObjectGroup(null);
        }

        cancelChanges();
    }

    /**
     * Data structure for storing data related to in-progress text edits.
     */
    private class EditTextData {
        private String textID;
        private String groupID;
        private String phaseID;
        private Vector2 point;
        private float rotation;
        private StrokeConfig strokeConfig;
        private DodlesTextField textField;
        private String previousText;
    }
}
