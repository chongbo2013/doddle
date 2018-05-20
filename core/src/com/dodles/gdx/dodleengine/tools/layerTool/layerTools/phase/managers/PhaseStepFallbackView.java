package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.managers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseConstants;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseUIStates;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttribute;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseSchema;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStep;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStepType;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * Manages the right side PhaseStep fallback view.
 */
@PerDodleEngine
public class PhaseStepFallbackView {
    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;

    private Table basicRootTable;
    private Table phasesUIManager;
    private Skin skin;
    private TextureAtlas phaseIconAtlas;

    @Inject
    public PhaseStepFallbackView(AssetProvider assetProviderin, EngineEventManager engineEventManagerin, ObjectManager objectManagerin) {
        this.assetProvider = assetProviderin;
        this.eventManager = engineEventManagerin;
        this.objectManager = objectManagerin;
    }

    /**
     * Initialize the view.
     */
    public final void initialize(Table rootTable, Skin skinin, String keysin) {
        phaseIconAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_PHASE_ICONS_ATLAS);
        
        LmlParser parser = VisLml.parser()
                .skin(skin)
                .build();

        phasesUIManager = rootTable;
        this.skin = skinin;

        String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_PHASES_FALL_BACK_VIEW);
        basicRootTable = (Table) parser.parseTemplate(template).get(0);

        phasesUIManager.top();
        phasesUIManager.add(basicRootTable).expandX().fillX();

        // wire up the button to toggle between basic and advanced phases
        TextButton phaseToggleButton = basicRootTable.findActor("phaseToggler");
        basicRootTable.removeActor(phaseToggleButton);
        phaseToggleButton.setText("<- Back");
//        phaseToggleButton.addListener(new InputListener() {
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_CONFIG_MANAGER.toString());
//                return true;
//            }
//        });

        String[] keys = keysin.split("\\|");
        String phaseKey = keys[0];
        String attributeKey = keys[1];
        String phaseStepkey = keys[2];

        VerticalGroup buttonList = basicRootTable.findActor("phasesUIList");
        buttonList.clear();

        for (PhaseSchema phaseSchema : PhaseConstants.PHASES) {
            if (phaseSchema.getPhaseType().name().equals(phaseKey)) {
                for (PhaseAttribute phaseAttribute : phaseSchema.getAttributeList()) {
                    if (phaseAttribute.getPhaseAttributeType().name().equals(attributeKey)) {
                        for (PhaseStep step : phaseAttribute.getPhaseSteps()) {
                            if (!step.getPhaseStepType().name().equals(phaseStepkey)) {
                                createPhaseStepButton(phaseIconAtlas, step.getPhaseStepType(), buttonList, keysin);
                            }
                        }
                    }
                }
            }
        }
    }

    private void createPhaseStepButton(TextureAtlas iconAtlas, PhaseStepType phaseStepType, VerticalGroup buttonList, final String keysin) {
        float padding = FullEditorInterface.getInterfaceRowSize() / 8f;
        float iconSize = FullEditorInterface.getInterfaceRowSize() - padding * 2;

        Button iconButton = LmlUtility.createButton(iconAtlas, phaseStepType.getIconAtlasKey(), 1.0f);
        final Table phaseButton = new Table(skin);
        phaseButton.add(iconButton).size(iconSize, iconSize).expand().fill().top().center().row();
        phaseButton.add(new Label(phaseStepType.getDescription(), skin, "small")).bottom().center();

        final String phaseStepTypeName = phaseStepType.name();
        phaseButton.addListener(new ClickListener() {
            // Initialize the ModifyPhaseSettingsView in the Left panel
            public void clicked(InputEvent event, float x, float y) {
                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_PHASE_STEP_SELECTED.toString(), keysin, phaseStepTypeName);
            }
        });

        buttonList.addActor(phaseButton);
    }

    /**
     * Reset the UI.
     */
    public final void resetUI() {
        if (basicRootTable != null) {
            phasesUIManager.clearChildren();
            basicRootTable = null;
            phasesUIManager.invalidate();
        }
    }
}
