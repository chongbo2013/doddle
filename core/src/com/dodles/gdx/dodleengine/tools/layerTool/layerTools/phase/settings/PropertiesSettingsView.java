package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.dodles.gdx.dodleengine.DodleEngine;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.phase.DeletePhaseCommand;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseStateManager;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseUIStates;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseSchema;
import com.dodles.gdx.dodleengine.util.DialogUtility;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.dodles.gdx.dodleengine.util.ParamRunnable;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Displays the phase settings for the selected phase.
 */
@PerDodleEngine
public class PropertiesSettingsView {
    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    private final DialogUtility dialogUtility;
    private final PhaseConfigView phaseConfigView;
    private final ObjectManager objectManager;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final PhaseStateManager phaseStateManager;

    private Table phasesUIManager;
    private Table propertiesRootTable;
    private ScrollPane stepsScrollablePanel;
    private float padSize;
    private TextureAtlas animationIconsAtlas;
    private TextureAtlas phaseIconsAtlas;
    private String selectedAttribute = "";
    private Table breadCrumbTrail;
    private int currentDepth = 0;
    private EngineEventListener refreshAssignedSlotListener;
    private PhaseSchema selectedPhaseSchema;
    private TextField phaseNameText;
    private Label assignedSlot;

    @Inject
    public PropertiesSettingsView(AssetProvider assetProvider, EngineEventManager eventManager, DialogUtility dialogUtility, PhaseConfigView phaseConfigView, ObjectManager objectManager, CommandFactory commandFactory, CommandManager commandManager, PhaseStateManager phaseStateManager) {
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.dialogUtility = dialogUtility;
        this.phaseConfigView = phaseConfigView;
        this.objectManager = objectManager;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.phaseStateManager = phaseStateManager;
    }

    /**
     * Initializes the component.
     */
    public final void initialize(Table rootTable, final Skin skin) {
        phasesUIManager = rootTable;

        Table panelHost = new Table();
        phasesUIManager.top();
        phasesUIManager.add(panelHost);

        padSize = DensityManager.getScale() * 10;
        animationIconsAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);
        phaseIconsAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_PHASE_ICONS_ATLAS);

        propertiesRootTable = new Table(skin);

        panelHost.add(propertiesRootTable).colspan(3);
        TextureAtlas toolbarAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_TOOLBARICONS_ATLAS);

        // set phase name
        Table t = new Table(skin);
        Label phaseNameLabel = new Label("Name:", skin);
        Phase selectedPhase = (Phase) objectManager.getSelectedActor();
        phaseNameText = new TextField(selectedPhase.getDisplayName(), skin);
        phaseNameText.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // update the phase button name and persist it into the scenegraph
//                DodleEngine.getLogger().log("PropertiesSettingsView", "phaseName: " + ((TextField)actor).getText());
                Phase selectedPhase = (Phase) objectManager.getSelectedActor();
                selectedPhase.setDisplayName(((TextField) actor).getText());
                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.UPDATE_PHASEBUTTON_TEXT.toString());
            }
        });

        t.add(phaseNameLabel);
        t.add(phaseNameText);
        propertiesRootTable.add(t).colspan(3);
        propertiesRootTable.row();

        final TextButton moveButton = new TextButton("Move", skin, "toggle");
        moveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DodleEngine.getLogger().log("PropertiesSettingView", "move button state: " + moveButton.isChecked());
                phaseStateManager.setInMove(moveButton.isChecked());
                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_ADVANCED_FOLDER_VIEW.toString());
            }
        });

        propertiesRootTable.add(moveButton).padTop(3).colspan(3);
        propertiesRootTable.row();


        Label assignedSlotLabel = new Label("Moved To:", skin);
        assignedSlot = new Label("", skin);
        Table l = new Table(skin);
        l.add(assignedSlotLabel);
        l.add(assignedSlot);
        propertiesRootTable.add(l).padTop(3).colspan(3);


        // trash phase action
        propertiesRootTable.row();
        final ImageButton trashButton = LmlUtility.createButton(toolbarAtlas, "trash_1", "trash_1", 1.0f);

        trashButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    dialogUtility.confirm("Delete PhaseSchema?", "Are you sure you want\nto delete this phase?", "Yes", "No", new ParamRunnable<Boolean>() {
                        @Override
                        public void run(Boolean parameter) {
                            if (parameter) {
                                DodlesActor activeLayer = objectManager.getActiveLayer();
                                DodlesGroup phaseGroup = (DodlesGroup) activeLayer;
                                ArrayList<Command> commands = new ArrayList<Command>();

                                String phaseID = phaseGroup.getVisiblePhaseID();
                                DeletePhaseCommand dpc = (DeletePhaseCommand) commandFactory.createCommand(DeletePhaseCommand.COMMAND_NAME);
                                dpc.init(phaseID, phaseGroup.getName());
                                dpc.execute();
                                commandManager.add(dpc);

                                // advance to the next visible phase
                                phaseGroup.nextVisiblePhase();
                                Phase p = phaseGroup.getVisiblePhase();
                                if (p != null) {
                                    objectManager.selectActor(p);
                                    phaseNameText.setText(p.getDisplayName());
                                    phaseNameText.setName(p.getName());
                                } else {
                                    phaseNameText.setText("");
                                    phaseNameText.setName("");
                                }

                                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_BASIC_PHASEBUTTONS.toString());
                                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_ADVANCED_FOLDER_VIEW.toString());

                            }
                        }
                    });
                }
        });
        Table table = new Table(skin);
        Label trashLabel = new Label("Trash", skin);
        table.add(trashButton);
        table.add(trashLabel).align(Align.left);
        propertiesRootTable.add(table).colspan(3);

        refreshAssignedSlotListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) {
            @Override
            public void listen(EngineEventData data) {
                String firstParm = data.getFirstStringParam();
                if (firstParm.equals(PhaseUIStates.REFRESH_ASSIGNED_SLOT_NAME.toString())) {
                    if (phaseStateManager.getAssignedPhaseStep() != null) {
                        assignedSlot.setText(phaseStateManager.getAssignedPhaseStep().getPhaseStepType().getDescription());
                    } else {
                        assignedSlot.setText("");
                    }
                }
            }
        };
        eventManager.addListener(refreshAssignedSlotListener);
    }

    /**
     * remove the UI from the Properties Panel.
     */
    public final void resetUI() {
        if (propertiesRootTable != null) {
            phasesUIManager.clearChildren();
            propertiesRootTable = null;
            phasesUIManager.invalidate();
        }
        if (refreshAssignedSlotListener != null) {
            eventManager.removeListener(refreshAssignedSlotListener);
        }
    }

    /**
     * get the phaseNameText field.
     * @return
     */
    public final TextField getPhaseNameText() {
        return phaseNameText;
    }

    /**
     * set the phaseNameText field.
     * @param phaseNameText
     */
    public final void setPhaseNameText(TextField phaseNameText) {
        this.phaseNameText = phaseNameText;
    }
}
