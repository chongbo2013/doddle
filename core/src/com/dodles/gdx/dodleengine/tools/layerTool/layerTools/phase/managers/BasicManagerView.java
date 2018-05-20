package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.managers;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseStateManager;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseUIStates;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * Phases UI component to handle basic phases tasks.
 * @author marknickel
 *
 */
@PerDodleEngine
public class BasicManagerView extends BasePanelView {
    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    private final PhaseStateManager phaseStateManager;
    
    private Table basicRootTable;
    private Table phasesUIManager;
    private Skin skin;
    private EngineEventListener phaseModifiedListener;
    private EngineEventListener updatePhaseButtonTextListener;
    private EngineEventListener refreshPhaseButtons;
    
    @Inject
    public BasicManagerView(AssetProvider assetProvider, EngineEventManager eventManager, PhaseStateManager phaseStateManager) {
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.phaseStateManager = phaseStateManager;
    }

    /**
     * Initializes the view.
     */
    public final void initialize(Table rootTable, Skin skinin, final PhasesUIRightPanel phasesUIRightPanel) {
        LmlParser parser = VisLml.parser()
                .skin(skin)
                .build();

        phasesUIManager = rootTable;
        this.skin = skinin;

        String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_PHASES_BASIC_VIEW);
        basicRootTable = (Table) parser.parseTemplate(template).get(0);
        
        phasesUIManager.top();
        phasesUIManager.add(basicRootTable).expandX().fillX();


        phasesUIRightPanel.initializePhaseButtonGroup();
        //phasesUIRightPanel.initializePhaseButtonList();

        VerticalGroup buttonList = basicRootTable.findActor("phasesUIList");
        phasesUIRightPanel.setPhaseButtonList(buttonList);
        phasesUIRightPanel.refreshPhaseButtons();

        if (phaseModifiedListener == null) {
            phaseModifiedListener = new EngineEventListener(EngineEventType.PHASEUI_ACTIVE_PHASE_MODIFIED) {
                @Override
                public void listen(EngineEventData data) {
                    phasesUIRightPanel.refreshPhaseButtons();
                }
            };

            eventManager.addListener(phaseModifiedListener);
        }

        
        // wire up the button to toggle between basic and advanced phases
        TextButton phaseToggleButton = basicRootTable.findActor("phaseToggler");
        phaseToggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                if (!phaseStateManager.isInEditing()) {
                    eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_ADVANCED_MANAGER.toString());
//                }
            }
        });

        refreshPhaseButtons = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) {
            @Override
            public void listen(EngineEventData data) {
                if (data.getFirstStringParam().equals(PhaseUIStates.RELOAD_BASIC_PHASEBUTTONS.toString())) {
                    phasesUIRightPanel.refreshPhaseButtons();
                }
            }
        };
        eventManager.addListener(refreshPhaseButtons);

    }
    
    /**
     * remove the Basic UI from the Manager Panel.
     */
    public final void resetUI() {
        if (basicRootTable != null) {
            //phasesUIManager.removeActor(basicRootTable);
            phasesUIManager.clearChildren();
            basicRootTable = null;
            phasesUIManager.invalidate();
        }
        if (refreshPhaseButtons != null) {
            eventManager.removeListener(refreshPhaseButtons);
            refreshPhaseButtons = null;
        }
        if (phaseModifiedListener != null) {
            eventManager.removeListener(phaseModifiedListener);
            phaseModifiedListener = null;
        }
    }
}
