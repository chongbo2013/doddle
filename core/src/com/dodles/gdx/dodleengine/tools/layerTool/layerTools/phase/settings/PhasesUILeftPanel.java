package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings;

import javax.inject.Inject;

import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.VisibleAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlay;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlayRegistry;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseConstants;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseUIStates;

/**
 * For the Phases Dimension, this is the left panel in the UI corresponding to those CRUD for managing basic phases.
 * @author marknickel
 *
 */
@PerDodleEngine
public class PhasesUILeftPanel implements FullEditorDodleOverlay {
    public static final String PANEL_NAME = "phasesUISettingsPanel";
    public static final float PANEL_WIDTH_PERCENT = 0.65f;

    private final EngineEventManager eventManager;
    private final ModifyPhaseSettingsView modifyPhaseSettingsView;
    private final NewPhaseSettingsView newPhaseSettingsView;
    private final PhaseConfigView phaseConfigView;
    private final PropertiesSettingsView propertiesSettingsView;
    
    private Table phasesUI;
    private Skin skin;

    private boolean toggle = false;
    
    private EngineEventListener phaseUISwitchNewPhaseListener;
    private EngineEventListener phaseUISwitchModifyPhaseListener;
    private EngineEventListener phaseUIToggleModifyPhasesListener;
    private EngineEventListener phaseUIEaseOutSettingsListener;
    private EngineEventListener phaseUISwitchConfigPhaseListener;
    private EngineEventListener phaseUISwitchPhasePropertiesListener; // the "database" screen when clicking on a specific phase

    @Inject
    public PhasesUILeftPanel(EngineEventManager eventManager, FullEditorDodleOverlayRegistry fedoRegistry, ModifyPhaseSettingsView modifyPhaseSettingsView, NewPhaseSettingsView newPhaseSettingsView, PhaseConfigView phaseConfigView, PropertiesSettingsView propertiesSettingsView) {
        this.eventManager = eventManager;
        this.modifyPhaseSettingsView = modifyPhaseSettingsView;
        this.newPhaseSettingsView = newPhaseSettingsView;
        this.phaseConfigView = phaseConfigView;
        this.propertiesSettingsView = propertiesSettingsView;

        fedoRegistry.registerOverlay(this);
    }
    
    @Override
    public final void initialize(Stack dodleOverlayStack, Skin skinIn) {
        if (phasesUI == null) {
            Table hostTable = new Table();
            hostTable.setFillParent(true);
            dodleOverlayStack.add(hostTable);
            
            this.skin = skinIn;
        
            phasesUI = new Table(skin);
            phasesUI.setBackground("dodles-red");
            phasesUI.setVisible(false);
            hostTable.add(phasesUI).expand().fillY().width(Value.percentWidth(PANEL_WIDTH_PERCENT, dodleOverlayStack)).align(Align.left);

            initializeNewPhasesView();
            //phasesUI.invalidate();
            //updatePanelWidth();
            moveInitialPanelOffsceen();

            if (phaseUISwitchNewPhaseListener == null) {
                phaseUISwitchNewPhaseListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) { //PHASEUI_SWITCH_NEW_PHASE
                    @Override
                    public void listen(EngineEventData data) {
                        if (data.getFirstStringParam().equals(PhaseUIStates.SWITCH_NEW_PHASE.toString())) {
                            //Gdx.app.log("PhasesUISettings","heard the switch to add phase view");
                            initializeNewPhasesView();
                            if (!toggle) {
                                easeIn();
                            }
                        }
                    }
                };
                eventManager.addListener(phaseUISwitchNewPhaseListener);
            }

            if (phaseUISwitchModifyPhaseListener == null) {
                phaseUISwitchModifyPhaseListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) { //PHASEUI_SWITCH_MODIFY_PHASE
                    @Override
                    public void listen(EngineEventData data) {
                        if (data.getFirstStringParam().equals(PhaseUIStates.SWITCH_MODIFY_PHASE.toString())) {
                            //Gdx.app.log("PhasesUISettings","heard the switch to modify phase view");
                            initializeModifyPhasesView();
                            if (!toggle) {
                                easeIn();
                            }
                        }
                    }
                };
                eventManager.addListener(phaseUISwitchModifyPhaseListener);
            }

            // There can be only one view active at any one time -- for now we can toggle between them
            if (phaseUIToggleModifyPhasesListener == null) {
                phaseUIToggleModifyPhasesListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) {
                    @Override
                    public void listen(EngineEventData data) {
                        if (data.getFirstStringParam().equals(PhaseUIStates.SWITCH_SETTINGS_VIEW.toString())) {
                            // go ahead and toggle the view between modify phases and new phases
                            if (newPhaseSettingsView == null) {
                                initializeNewPhasesView();
                            } else {
                                initializeModifyPhasesView();
                            }

                            if (!toggle) {
                                easeIn();
                            }
                        }
                    }
                };
                eventManager.addListener(phaseUIToggleModifyPhasesListener);
            }

            if (phaseUISwitchConfigPhaseListener == null) {
                phaseUISwitchConfigPhaseListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) { //PHASEUI_SWITCH_MODIFY_PHASE
                    @Override
                    public void listen(EngineEventData data) {
                        if (data.getFirstStringParam().equals(PhaseUIStates.SWITCH_CONFIG_PHASE.toString())) {
                            initializePhaseConfigView();
                            slidePanel();
                        }
                    }
                };
                eventManager.addListener(phaseUISwitchConfigPhaseListener);
            }

            if (phaseUISwitchPhasePropertiesListener == null) {
                phaseUISwitchPhasePropertiesListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) {
                    @Override
                    public void listen(EngineEventData data) {

                        if (data.getFirstStringParam().equals(PhaseUIStates.SWITCH_PROPERTIES_VIEW.toString())) {
                            initializePhasePropertiesView();
                            if (data.getSecondStringParam() != null) {
                                propertiesSettingsView.getPhaseNameText().setText(data.getSecondStringParam());
                            }
                            if (data.getThirdStringParam() != null) {
                                propertiesSettingsView.getPhaseNameText().setName(data.getThirdStringParam());
                            }
                            //slidePanel();
                            if (!toggle) {
                                easeIn();
                            }
                        }
                    }
                };
                eventManager.addListener(phaseUISwitchPhasePropertiesListener);
            }

            if (phaseUIEaseOutSettingsListener == null) {
                phaseUIEaseOutSettingsListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) {
                    @Override
                    public void listen(EngineEventData data) {
                        if (data.getFirstStringParam().equals(PhaseUIStates.EASEOUT_SETTINGS_PANEL.toString())) {
                            if (toggle) {
                                easeOut();
                            }
                        }
                    }
                };
                eventManager.addListener(phaseUIEaseOutSettingsListener);
            }
        }
    }

    @Override
    public final String getName() {
        return PANEL_NAME;
    }
    
    /**
     * slide the panel off screen.
     */
    public final void moveInitialPanelOffsceen() {
        if (phasesUI != null) {
            phasesUI.setVisible(false);
            toggle = false;
        }
    }
    
    
    /**
     * ease the side panel out.
     */
    public final void easeOut() {
        float originX = phasesUI.getX();
        MoveToAction out = new MoveToAction();
        out.setPosition(phasesUI.getWidth() * -1, phasesUI.getY());
        out.setDuration(PhaseConstants.PANEL_EASE_DURATION);

        VisibleAction hide = new VisibleAction();
        hide.setVisible(false);

        MoveToAction in = new MoveToAction();
        in.setPosition(originX, phasesUI.getY());
        in.setDuration(PhaseConstants.PANEL_INSTANCE_DURATION);

        SequenceAction seq = new SequenceAction(out, hide, in);
        phasesUI.addAction(seq);
        //phasesUI.setVisible(false);
        toggle = false;
    }
    
    /**
     * ease the side panel "in".
     */
    public final void easeIn() {
        float originX = phasesUI.getX();
        MoveToAction out = new MoveToAction();
        out.setPosition(phasesUI.getWidth() * -1, phasesUI.getY());
        out.setDuration(PhaseConstants.PANEL_EASE_DURATION / 3);

        VisibleAction show = new VisibleAction();
        show.setVisible(true);

        MoveToAction in = new MoveToAction();
        in.setPosition(originX, phasesUI.getY());
        in.setDuration(PhaseConstants.PANEL_EASE_DURATION);

        SequenceAction seq = new SequenceAction(out, show, in);
        phasesUI.addAction(seq);

        //phasesUI.setVisible(true);
        toggle = true;
    }
    
    /**
     * toggle slide the panel... if out, slide in, etc.
     */
    public final void slidePanel() {
        //Gdx.app.log("PhasesUISettings", "slidePanel executed");
        if (!toggle) {
            easeIn();
            toggle = true;
        } else {
            easeOut();
            toggle = false;
        }
    }
    
    /**
     * initialize the new Phases View.
     */
    public final void initializeNewPhasesView() {
        resetViews();
        newPhaseSettingsView.initialize(phasesUI, skin);
    }

    /**
     * initialize the phase properties view -- 'database' setup.
     */
    public final void initializePhasePropertiesView() {
        resetViews();
        propertiesSettingsView.initialize(phasesUI, skin);
    }
    
    /**
     * initialize the modify Phases View.
     */
    public final void initializeModifyPhasesView() {
        resetViews();
        modifyPhaseSettingsView.initialize(phasesUI, skin);
    }

    /**
     * Initialize the PhaseSchema Config view.
     */
    private void initializePhaseConfigView() {
        resetViews();
        phaseConfigView.initialize(phasesUI, skin);
    }

    private void resetViews() {
        modifyPhaseSettingsView.resetUI();
        newPhaseSettingsView.resetUI();
        phaseConfigView.resetUI();
        propertiesSettingsView.resetUI();
    }
}
