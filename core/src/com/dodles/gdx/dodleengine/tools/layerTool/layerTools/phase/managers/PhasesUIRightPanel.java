package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.VisibleAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.phase.UpdatePhaseValuesCommand;
import com.dodles.gdx.dodleengine.editor.ActorPreviewWidget;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlay;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlayRegistry;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.ManagerButton;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseConstants;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseLayerSubTool;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseStateManager;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseUIStates;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseSchema;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings.NewPhaseSettingsView;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings.PhaseConfigView;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings.PropertiesSettingsView;
import com.dodles.gdx.dodleengine.util.LmlUtility;

import javax.inject.Inject;

/**
 * Overall controller for the right panel in the phases UI.
 * @author marknickel
 *
 */
@PerDodleEngine
public class PhasesUIRightPanel implements FullEditorDodleOverlay {
    private static final String PANEL_NAME = "phasesUIManagerPanel";
    private static final float PANEL_WIDTH_PERCENT = 0.16f;
    private static final float EXTENDED_PANEL_WIDTH_PERCENT = 0.32f;

    private final PhaseStepFallbackView phaseStepFallbackView;
    private final AdvancedManagerView advancedManagerView;
    private final ConfigManagerView configManagerView;
    private final BasicManagerView basicManagerView;
    private final PhaseConfigView phaseConfigView;
    private final PropertiesSettingsView propertiesSettingsView;
    private final NewPhaseSettingsView newPhaseSettingsView;
    private final PhaseStateManager phaseStateManager;

    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    private final CommandFactory commandFactory;
    private final OkCancelStackManager okCancelStackManager;
    private final CommandManager commandManager;



    private Skin skin;
    private Table phasesUI;
    private Table leftUI;
    private Table rightUI;
    private boolean toggle = false;

    private ButtonGroup phaseButtonGroup;
    private VerticalGroup phaseButtonList;
    private UpdatePhaseValuesCommand upvCommand;

    private Stack dodleOverlayStack;

    //Listeners so as to not register multiple times
    private EngineEventListener phaseUIListener;
    private EngineEventListener phaseUISwitchBasicListener;
    private EngineEventListener phaseUISwitchAdvancedListener;
    private EngineEventListener phaseUISwitchPhaseStepSelectedListener;
    private EngineEventListener phaseUISwitchConfigListener;
    private EngineEventListener phaseUISwitchPhaseStepFallbackListener;
    private EngineEventListener updatePhaseButtonTextListener;
    private EngineEventListener showHierarchyInModeModeListener;

    @Inject
    public PhasesUIRightPanel(
        PhaseStepFallbackView phaseStepFallbackView,
        AdvancedManagerView advancedManagerView,
        ConfigManagerView configManagerView,
        BasicManagerView basicManagerView,
        PhaseConfigView phaseConfigView,
        PropertiesSettingsView propertiesSettingsView,
        NewPhaseSettingsView newPhaseSettingsView,

        PhaseStateManager phaseStateManager,
        EngineEventManager eventManager,
        FullEditorDodleOverlayRegistry overlayRegistry,
        ObjectManager objectManager,
        CommandFactory commandFactory,
        OkCancelStackManager okCancelStackManager,
        CommandManager commandManager
    ) {
        this.phaseStepFallbackView = phaseStepFallbackView;
        this.advancedManagerView = advancedManagerView;
        this.configManagerView = configManagerView;
        this.basicManagerView = basicManagerView;
        this.phaseConfigView = phaseConfigView;
        this.propertiesSettingsView = propertiesSettingsView;
        this.newPhaseSettingsView = newPhaseSettingsView;

        this.eventManager = eventManager;
        this.objectManager = objectManager;
        this.commandFactory = commandFactory;
        this.okCancelStackManager = okCancelStackManager;
        this.commandManager = commandManager;
        this.phaseStateManager = phaseStateManager;
        
        overlayRegistry.registerOverlay(this);
    }
    
    @Override
    public final void initialize(Stack stack, Skin pSkin) {
        this.dodleOverlayStack = stack;
        if (phasesUI == null) {
            Table hostTable = new Table(pSkin);
            hostTable.top();
            hostTable.setFillParent(true);
            stack.add(hostTable);
            skin = pSkin;

            phasesUI = new Table(pSkin);
            phasesUI.top().padTop(2);
            leftUI = new Table(pSkin);
            rightUI = new Table(pSkin);
            phasesUI.setBackground("dodles-red");
            phasesUI.setVisible(false);
            hostTable.add(phasesUI).expand().fillY().width(Value.percentWidth(PANEL_WIDTH_PERCENT, stack)).align(Align.right);

            //phasesUI.left();

            phasesUI.add(leftUI).padLeft(5).padRight(5);
            phasesUI.add(rightUI).padLeft(5).padRight(5).top();
            initializeAdvancedView();

            // Listener for Corner click event
            if (phaseUIListener == null) {
                phaseUIListener = new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
                    @Override
                    public void listen(EngineEventData data) {
                        String state = data.getFirstStringParam();

                        if (state != null && state.equals(PhaseLayerSubTool.TOOL_NAME)) {
                            slidePanel(true);
                            phaseConfigView.loadPhaseSchemaFromDodleGroup();
                        } else {
                            slidePanel(false);
                            eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.EASEOUT_SETTINGS_PANEL.toString());
                        }
                    }
                };
                eventManager.addListener(phaseUIListener);
            }

            if (phaseUISwitchBasicListener == null) {
                phaseUISwitchBasicListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) { //PHASEUI_SWITCH_ADVANCED_MANAGER
                    @Override
                    public void listen(EngineEventData data) {
                        if (PhaseUIStates.SWITCH_ADVANCED_MANAGER.toString().equals(data.getFirstStringParam())) {

                            if (getSelectedPhaseSchema() != null) {
                                initializeAdvancedView();
                            } else {
                                // go right to the Configuration if your Object doesn't have a schema
                                basicManagerView.resetUI();
                                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_CONFIG_MANAGER.toString());
                            }
                        }
                    }
                };
                eventManager.addListener(phaseUISwitchBasicListener);
            }

            if (phaseUISwitchAdvancedListener == null) {
                phaseUISwitchAdvancedListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) { //PHASEUI_SWITCH_CONFIG_MANAGER
                    @Override
                    public void listen(EngineEventData data) {
                        if (PhaseUIStates.SWITCH_CONFIG_MANAGER.toString().equals(data.getFirstStringParam())) {
                            initializeConfigView();
                        }
                    }
                };
                eventManager.addListener(phaseUISwitchAdvancedListener);
            }

            if (phaseUISwitchPhaseStepSelectedListener == null) {
                phaseUISwitchPhaseStepSelectedListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) { //PHASEUI_SWITCH_CONFIG_MANAGER
                    @Override
                    public void listen(EngineEventData data) {
                        if (PhaseUIStates.SWITCH_PHASE_STEP_SELECTED.toString().equals(data.getFirstStringParam())) {
                            initializeAdvancedView();
                        }
                    }
                };
                eventManager.addListener(phaseUISwitchPhaseStepSelectedListener);
            }

            if (phaseUISwitchConfigListener == null) {
                phaseUISwitchConfigListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) { //PHASEUI_SWITCH_BASIC_MANAGER
                    @Override
                    public void listen(EngineEventData data) {
                        if (PhaseUIStates.SWITCH_BASIC_MANAGER.toString().equals(data.getFirstStringParam())) {
                            initializeBasicView();
                            phaseConfigView.loadPhaseSchemaFromDodleGroup();
                        }
                    }
                };
                eventManager.addListener(phaseUISwitchConfigListener);
            }

            if (phaseUISwitchPhaseStepFallbackListener == null) {
                phaseUISwitchPhaseStepFallbackListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) { //PHASEUI_SWITCH_BASIC_MANAGER
                    @Override
                    public void listen(EngineEventData data) {
                        if (PhaseUIStates.SWITCH_PHASE_STEP_FALL_BACK_MANAGER.toString().equals(data.getFirstStringParam())) {
                            initializePhaseStepFallbackView(data.getParameters().get(1));
                        }
                    }
                };
                eventManager.addListener(phaseUISwitchPhaseStepFallbackListener);
            }

            if (updatePhaseButtonTextListener == null) {
                updatePhaseButtonTextListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) {
                    @Override
                    public void listen(EngineEventData data) {
                        if (data.getFirstStringParam().equals(PhaseUIStates.UPDATE_PHASEBUTTON_TEXT.toString())) {
                            updatePhaseButtonText();
                        }

                    }
                };
                eventManager.addListener(updatePhaseButtonTextListener);
            }

            if (showHierarchyInModeModeListener == null) {
                showHierarchyInModeModeListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) {
                    @Override
                    public void listen(EngineEventData data) {
                        //String firstParam = data.getFirstStringParam();
                        //if (firstParam.equals(PhaseUIStates.MOVE_PHASE_OFF) || firstParam.equals(PhaseUIStates.MOVE_PHASE_ON)) {
                            //TODO - why is this missing logic?
                        //}
                    }
                };
                eventManager.addListener(showHierarchyInModeModeListener);
            }
        }
    }

    @Override
    public final String getName() {
        return PANEL_NAME;
    }
    
    /**
     * ease the side panel out.
     */
    private void easeOut() {
        float originX = phasesUI.getX();
        MoveToAction out = new MoveToAction();
        out.setPosition(Gdx.graphics.getWidth(), phasesUI.getY());
        out.setDuration(PhaseConstants.PANEL_EASE_DURATION);

        VisibleAction hide = new VisibleAction();
        hide.setVisible(false);

        MoveToAction in = new MoveToAction();
        in.setPosition(originX, phasesUI.getY());
        in.setDuration(PhaseConstants.PANEL_INSTANCE_DURATION);

        SequenceAction seq = new SequenceAction(out, hide, in);
        phasesUI.addAction(seq);
        toggle = false;
    }
    
    /**
     * ease the side panel "in".
     */
    private void easeIn() {
        //Gdx.app."PhasesUIManagerPanel", "called easeIn()");
        float originX = phasesUI.getX();

        MoveToAction out = new MoveToAction();
        out.setPosition(Gdx.graphics.getWidth(), phasesUI.getY());
        out.setDuration(PhaseConstants.PANEL_EASE_DURATION / 3);

        VisibleAction show = new VisibleAction();
        show.setVisible(true);

        MoveToAction in = new MoveToAction();
        in.setPosition(originX, phasesUI.getY());
        in.setDuration(PhaseConstants.PANEL_EASE_DURATION);

        SequenceAction seq = new SequenceAction(out, show, in);
        phasesUI.addAction(seq);

        toggle = true;
    }
    
    /**
     * Slides the panel in or out of view.
     */
    private void slidePanel(boolean visible) {
        if (visible && !toggle) {
            easeIn();
            toggle = true;
        } else if (!visible && toggle) {
            easeOut();
            toggle = false;
        }
    }

    /**
     * expand the panel to account for an additional UI Panel
     * TODO: the UI transition is probably a little rough, come back and clean up to make smooth.
     */
    public final void togglePanelExpand() {
        float originX = phasesUI.getX();
        float originY = phasesUI.getY();

        float folderBrowserWidth = dodleOverlayStack.getWidth() * PANEL_WIDTH_PERCENT;
        float settingPanelWidth = dodleOverlayStack.getWidth() * EXTENDED_PANEL_WIDTH_PERCENT;

        float finalX;
        float cellWidth;

        if (!phaseStateManager.isToggleExpand()) {
            cellWidth = folderBrowserWidth + settingPanelWidth;
            finalX = originX - settingPanelWidth;
            phaseStateManager.setToggleExpand(true);
        } else {
            cellWidth = folderBrowserWidth;
            finalX = originX + settingPanelWidth;
            phaseStateManager.setToggleExpand(false);
            resetViews();
        }

        Cell cell = LmlUtility.getCell(phasesUI);
        cell.width(cellWidth).expand();
        ((Table) phasesUI.getParent()).invalidateHierarchy();

        // slide out a little further
        MoveToAction out = new MoveToAction();
        out.setPosition(finalX, originY);
        out.setDuration(PhaseConstants.PANEL_EASE_DURATION);
        phasesUI.addAction(out);
    }
    
    /**
     * initialize the Basic View.
     */
    private void initializeBasicView() {
        configManagerView.resetUI();
        phaseStateManager.setCurrView(basicManagerView);
        basicManagerView.initialize(phasesUI, skin, this);
    }
    
    /**
     * initialize the Advanced View.
     */
    private void initializeAdvancedView() {
        phaseStepFallbackView.resetUI();
        phaseStateManager.setCurrView(advancedManagerView);
        advancedManagerView.initialize(leftUI, skin, this);
    }

    /**
     * Initialize the Config View.
     */
    private void initializeConfigView() {
        //advancedManagerView.resetUI();
        //phaseStepFallbackView.resetUI();
        phaseStateManager.setCurrView(configManagerView);
        configManagerView.initialize(rightUI, skin);
    }

    /**
     * Initialize the fall back view.
     */
    private void initializePhaseStepFallbackView(String phaseStepName) {
        //configManagerView.resetUI();
        advancedManagerView.resetUI();
        phaseStepFallbackView.resetUI();
        phaseStepFallbackView.initialize(leftUI, skin, phaseStepName);
    }

    /**
     * Initialize the PhaseSchema Config view.
     */
    public final void initializePhaseConfigView() {
        resetViews();
        phaseConfigView.initialize(rightUI, skin);
    }

    /**
     * initialize the new Phases View.
     */
    public final void initializeNewPhasesView() {
        resetViews();
        newPhaseSettingsView.initialize(rightUI, skin);
    }

    /**
     * initialize the new Property Settings View.
     */
    public final void initializePropertiesSettingsView() {
        resetViews();
        propertiesSettingsView.initialize(rightUI, skin);
    }

    /**
     * reset views.
     */
    private void resetViews() {
//        modifyPhaseSettingsView.resetUI();
        newPhaseSettingsView.resetUI();
        phaseConfigView.resetUI();
        propertiesSettingsView.resetUI();
    }

    /**
     * helper method to refresh the PhaseSchema buttons in the right panel as phase CRUD operation happens.
     */
    public final void refreshPhaseButtons() {
//        DodleEngine.getLogger().log("PhaseseUIRightPanel", "calling refreshPhaseButtons()");
        phaseButtonList.clearChildren();

        DodlesActor activePhaseGroup = objectManager.getActiveLayer();

        // must be an instance of a DodlesGroup in order to do phase magic.
        if (activePhaseGroup != null && activePhaseGroup instanceof DodlesGroup) {
            final DodlesGroup pg = ((DodlesGroup) activePhaseGroup);

            // generate a button for each Phase
            for (int i = 0; i < pg.getPhases().size(); i++) {

                // don't show orphaned phases in move mode
                if (!phaseStateManager.isInMove()) {
                    createPhaseButton(pg, i, phaseButtonList, phaseButtonGroup);
                }
            }

            objectManager.selectActor(pg.getVisiblePhase());

            // create the "+" button to swing out basic phase functions like add, copy, etc
            Table mgrContainer = new Table();
            ManagerButton mgrButton = new ManagerButton("Add", "+", skin);
            mgrButton.getLabel().setAlignment(Align.center);
            mgrButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    // TODO: when in move mode, need same functionality that used to be in PropertiesSettingsView to have current phase updated with current Hierarchy

//                    if (!phaseStateManager.isInEditing()) {
                       // createUndoRedoOkStack();

                        initializeNewPhasesView();
                        if (!phaseStateManager.isToggleExpand()) {
                            togglePanelExpand();
                        }
                        //eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_NEW_PHASE.toString());
                        phaseStateManager.setInEditing(true);
//                    }
                }

            });
            mgrContainer.add(mgrButton).size(FullEditorInterface.getInterfaceRowSize() * 1.15f).expand().fill();
            phaseButtonList.addActor(mgrContainer);
        }
    }

    /**
     * create a button for each orphaned Phase.
     * @param group
     * @param phaseIndex
     * @param buttonList
     * @param buttonGroup
     */
    private void createPhaseButton(final DodlesGroup group, final int phaseIndex, VerticalGroup buttonList, ButtonGroup buttonGroup) {
        final Phase phase = group.getPhases().get(phaseIndex);
        if (phase.getPhaseHierarchy() == null || phaseStateManager.getCurrView() instanceof BasicManagerView) {
            String displayName = phase.getDisplayName();
            if (displayName == null) {
                displayName = phase.getNumber().toString();
                phase.setDisplayName(displayName);
            }
            
            final Button phaseButton = new Button(new Button.ButtonStyle());
            
            Table phaseButtonStack = new Table();

            phaseButton.add(phaseButtonStack).expand().fill();

            ActorPreviewWidget apw = new ActorPreviewWidget(phase, false, Color.WHITE);
            //apw.setFillParent(true);
            phaseButtonStack.add(apw).size(FullEditorInterface.getInterfaceRowSize() * 1.5f).expand().fill().row();
            
            Label label = new Label(displayName, skin, "default-black");
            //label.setFillParent(true);
            label.setAlignment(Align.center);
            phaseButtonStack.add(label).expand().fill();
            phaseButton.setName(phase.getName());

//            DodleEngine.getLogger().log("PhaseUrIRightPanel", "Phase button: " + phaseButton.getText() + " == " + phaseButton.getName() + " == visibleID: " + group.getVisiblePhaseID());
//            DodleEngine.getLogger().log("PhaseUIRightPanel", "  -- setChecked: " + group.getVisiblePhaseID().equals(group.getPhases().get(phaseIndex).getName()));
            phaseButton.setChecked(group.getVisiblePhaseID().equals(group.getPhases().get(phaseIndex).getName()));

            phaseButton.addListener(new ClickListener() {
                // Initialize the ModifyPhaseSettingsView in the Left panel
                public void clicked(InputEvent event, float x, float y) {
//                    DodleEngine.getLogger().log("PhasesUIRightPanel", "   -- phaseButton clicked: " + phaseButton.getName());
//                    if (inEditing && !group.getVisiblePhaseID().equals(phaseButton.getName())) {
//                        okCancelStackManager.pop(true);
//                        inEditing = false;
//                    }

//                    if (!phaseStateManager.isInEditing()) {
                        if (!group.getVisiblePhaseID().equals(phaseButton.getName())) {
                            // First press - activate the phase...
                            group.setVisiblePhase(phase.getName());
                            objectManager.selectActor(phase);
                        }
                        //createUndoRedoOkStack();

                        initializePropertiesSettingsView();
                        if (!phaseStateManager.isToggleExpand()) {
                            togglePanelExpand();
                        }


                        //eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_PROPERTIES_VIEW.toString());
                        phaseStateManager.setInEditing(true);
//                    }
                }
            });

            buttonList.addActor(phaseButton);
            buttonGroup.add(phaseButton);
            //DodleEngine.getLogger().log("PhasesUIRightPanel", "Phase: " + phase.getName() + " is an orphan");
        }
//        else {
//            //not an orphaned phase
//            DodleEngine.getLogger().log("PhasesUIRightPanel", "Phase: " + phase.getName() + " is NOT an orphan");
//        }
    }

    /**
     * get the phase button group.
     * @return
     */
    public final ButtonGroup getPhaseButtonGroup() {
        return phaseButtonGroup;
    }

    /**
     * set the phase button group.
     * @param phaseButtonGroup
     */
    public final void setPhaseButtonGroup(ButtonGroup phaseButtonGroup) {
        this.phaseButtonGroup = phaseButtonGroup;
    }

    /**
     * get the phase button list VerticalGroup.
     * @return
     */
    public final VerticalGroup getPhaseButtonList() {
        return phaseButtonList;
    }

    /**
     * set the phase button list VerticalGroup.
     * @param phaseButtonList
     */
    public final void setPhaseButtonList(VerticalGroup phaseButtonList) {
        this.phaseButtonList = phaseButtonList;
    }

    /**
     * initialize the phase button group.
     */
    public final void initializePhaseButtonGroup() {
//        DodleEngine.getLogger().log("PhaseseUIRightPanel", "calling initializePhaseButtonGroup()");
        phaseButtonGroup = new ButtonGroup();
        phaseButtonGroup.setMinCheckCount(0);
        phaseButtonGroup.setMaxCheckCount(1);
    }

    /**
     * initialize the Phase button list vertical group.
     */
    public final void initializePhaseButtonList() {
        phaseButtonList = new VerticalGroup();
        phaseButtonList.grow();
        phaseButtonList.padTop(2f);
    }

    /**
     * set the text on the phaseButton to the displayName from the active Phase.
     */
    public final void updatePhaseButtonText() {
//        DodleEngine.getLogger().log("PhasesUIRightPanel", "in updatePhaseButtonText: " + phaseButtonList.hasChildren());
        if (phaseButtonGroup.getButtons().size > 0) {
            Phase phase = (Phase) objectManager.getSelectedActor();
            for (int i = 0; i < phaseButtonGroup.getButtons().size; i++) {
                Actor textButton = (Actor) phaseButtonGroup.getButtons().get(i);
                if (textButton instanceof  Button && textButton.getName().equals(phase.getName())) {
                    Button b = (Button) textButton;
                    SnapshotArray<Actor> bItems = b.getChildren();
                    Actor stack = bItems.get(0);
                    SnapshotArray<Actor> sItems = ((Table) stack).getChildren();

                    Label label = (Label) sItems.get(1);
                    label.setText(phase.getDisplayName());
                }
                if (textButton instanceof TextButton && textButton.getName().equals(phase.getName())) {
                    if (textButton instanceof TextButton) {
                        ((TextButton) textButton).setText(phase.getDisplayName());
                    }
                }
            }
        }
    }


    /**
     * gets the PhaseSchema from the currently selected object.
     * @return
     */
    public final PhaseSchema getSelectedPhaseSchema() {
        DodlesActor activePhaseGroup = objectManager.getActiveLayer();
        if (activePhaseGroup != null && activePhaseGroup instanceof DodlesGroup) {
            final DodlesGroup pg = ((DodlesGroup) activePhaseGroup);
            PhaseSchema selectedPhaseSchema = pg.getPhaseSchema();
            return selectedPhaseSchema;
        }
        return null;
    }

    private void createUndoRedoOkStack() {
        upvCommand = (UpdatePhaseValuesCommand) commandFactory.createCommand(UpdatePhaseValuesCommand.COMMAND_NAME);
        upvCommand.init(objectManager.getActiveLayer().getName());

        okCancelStackManager.push(new Runnable() {
            @Override
            public void run() {
                commandManager.add(upvCommand);
                phaseStateManager.setInEditing(false);
                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.EASEOUT_SETTINGS_PANEL.toString());
                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_ADVANCED_FOLDER_VIEW.toString());
                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_BASIC_PHASEBUTTONS.toString());
            }
        }, new Runnable() {
            @Override
            public void run() {
                upvCommand.undo();
                phaseStateManager.setInEditing(false);
                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.EASEOUT_SETTINGS_PANEL.toString());
                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_ADVANCED_FOLDER_VIEW.toString());
                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_BASIC_PHASEBUTTONS.toString());
            }
        });
    }

}
