package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PHNode;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseConstants;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseStateManager;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseUIStates;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttribute;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseHierarchy;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseSchema;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStep;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseType;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * UI for the left panel to manage New PhaseSchema settings.
 */
@PerDodleEngine
public class PhaseConfigView {
    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    private final PhaseStateManager phaseStateManager;

    private Table phasesUIConfigSettings;
    private Table phaseConfigSettingsTable;
    private Table phaseObjects;
    private ScrollPane phasesConfigScroller;
    private TextureAtlas animationIconsAtlas;
    private float padSize;
    private HashMap<String, VerticalGroup> phaseTables;
    private HashMap<String, Table> attributeTables;
    private HashMap<String, VerticalGroup> phaseStepTables;
    private HashMap<String, Table> phaseStepContainers;

    private String selectedAttribute = "";
    private TextureAtlas phaseIcons;

    private String[] types = {"", PhaseType.MOUTH.getDescription(), PhaseType.GENERIC.getDescription(), PhaseType.CAR.getDescription()};


    private EngineEventListener phaseStepSelectedEvent;

    @Inject
    PhaseConfigView(
            AssetProvider assetProvider,
            EngineEventManager eventManager,
            ObjectManager objectManager,
            PhaseStateManager phaseStateManager
    ) {
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.objectManager = objectManager;
        this.phaseStateManager = phaseStateManager;
    }

    /**
     * Initialize this config view.
     */
    public final void initialize(Table rootTable, final Skin skin) {
        LmlParser parser = VisLml.parser()
                .skin(skin)
                .build();

        loadPhaseSchemaFromDodleGroup();
        this.phasesUIConfigSettings = rootTable;

        String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_PHASES_CONFIG_SETTINGS_VIEW);
        phaseConfigSettingsTable = (Table) parser.parseTemplate(template).get(0);


        SelectBox<String> phaseTypes = phaseConfigSettingsTable.findActor("phaseType");

        animationIconsAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);
        padSize = DensityManager.getScale() * 10;

        phaseObjects = phaseConfigSettingsTable.findActor("phaseObjects");
        phasesConfigScroller = phaseConfigSettingsTable.findActor("phasesConfigScroller");
        
        phaseIcons = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_PHASE_ICONS_ATLAS);

        phaseTypes.setItems(types);

        phaseTypes.setSelected(PhaseType.valueOf(phaseStateManager.getSelectedPhaseType()).getDescription());

        phaseTypes.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!event.isHandled()) {
                    phasesConfigScroller.clearChildren();
                    phasesConfigScroller.invalidate();

                    String phaseDescriptor = ((SelectBox) actor).getSelected().toString();

                    VerticalGroup table = phaseTables.get(phaseDescriptor);
                    table.grow();
                    phasesConfigScroller.setWidget(table);

                    phaseObjects.add(phasesConfigScroller).expandX().fillX().expandY().fillY();
                    phasesConfigScroller.invalidate();

                    String selectedPhase = "";
                    for (PhaseSchema p : phaseStateManager.getPhaseSchemas()) {
                        if (p.getPhaseType().getDescription().equals(phaseDescriptor)) {
                            selectedPhase = p.getPhaseType().name();
                        }
                    }

                    if (!selectedPhase.equals(phaseStateManager.getSelectedPhaseType())) {
                        clearPhaseHierarchyValue(null);
                    }

                    phaseStateManager.setSelectedPhaseType(selectedPhase);
                    updateDodleGroupPhaseSchema();
                }
            }
        });

        //phasesUIConfigSettings.top();
        phasesUIConfigSettings.add(phaseConfigSettingsTable).top();

        buildControls(skin);

        phaseTypes.fire(new ChangeListener.ChangeEvent());
        updateDodleGroupPhaseSchema();

        phaseStepSelectedEvent = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) { //PHASEUI_SWITCH_CONFIG_MANAGER
            @Override
            public void listen(EngineEventData data) {

                if (PhaseUIStates.SWITCH_PHASE_STEP_SELECTED.toString().equals(data.getFirstStringParam())) {
                    String[] keys = data.getParameters().get(1).split("\\|");
                    PhaseStep selectedPhaseStep = null;
                    PhaseStep selectedFallBackPhaseStep = null;
                    String fallBackPhaseType = data.getParameters().get(2);
                    String attributeKey = "";
                    PhaseSchema selectedPhaseSchema = null;

                    for (PhaseSchema phase : phaseStateManager.getPhaseSchemas()) {
                        if (phase.getPhaseType().name().equals(keys[0])) {
                            selectedPhaseSchema = phase;
                            for (PhaseAttribute attribute : phase.getAttributeList()) {
                                if (attribute.getPhaseAttributeType().name().equals(keys[1])) {
                                    attributeKey = phase.getPhaseType().getDescription() + "_" + attribute.getPhaseAttributeType().getDescription();
                                    for (PhaseStep step : attribute.getPhaseSteps()) {
                                        if (step.getPhaseStepType().name().equals(keys[2])) {
                                            selectedPhaseStep = step;
                                        } else if (step.getPhaseStepType().name().equals(fallBackPhaseType)) {
                                            selectedFallBackPhaseStep = step;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (selectedPhaseStep != null) {
                        selectedPhaseStep.setFallback(selectedFallBackPhaseStep);
                    }

                    buildControls(skin);

                    if (selectedPhaseSchema != null) {
                        VerticalGroup table = phaseTables.get(selectedPhaseSchema.getPhaseType().getDescription());
                        phasesConfigScroller.setWidget(table);
                    } else {
                        phasesConfigScroller.clear();
                    }
                    

                    Table phaseStepContainer = phaseStepContainers.get(attributeKey);
                    phaseStepContainer.add(phaseStepTables.get(attributeKey)).fillX();
                }
            }
        };

        eventManager.addListener(phaseStepSelectedEvent);
    }

    private void buildControls(Skin skin) {
        phaseTables = new HashMap<String, VerticalGroup>();
        attributeTables = new HashMap<String, Table>();
        phaseStepTables = new HashMap<String, VerticalGroup>();
        phaseStepContainers = new HashMap<String, Table>();

        for (PhaseSchema phaseSchema : phaseStateManager.getPhaseSchemas()) {
            buildPhaseRow(skin, phaseSchema);
        }
    }

    private void buildPhaseRow(Skin skin, PhaseSchema phaseSchema) {
        String phaseKey = phaseSchema.getPhaseType().getDescription();
        VerticalGroup phaseRow = new VerticalGroup();
        phaseTables.put(phaseKey, phaseRow);
        phaseRow.grow();
        phaseRow.setTouchable(Touchable.enabled);

        for (PhaseAttribute attribute : phaseSchema.getAttributeList()) {
            phaseRow.addActor(buildAttributeRow(skin, phaseSchema, attribute));
        }
    }

    private Table buildAttributeRow(Skin skin, PhaseSchema phaseSchema, PhaseAttribute attribute) {
        String phaseKey = phaseSchema.getPhaseType().getDescription();
        String attributeKey = attribute.getPhaseAttributeType().getDescription();
        attributeTables.put(attributeKey, new Table());
        Table attributeRow = attributeTables.get(attributeKey);

        attributeRow.add(new Image(new TextureRegionDrawable(animationIconsAtlas.findRegion("folder")), Scaling.fill)).size(FullEditorInterface.getInterfaceRowSize()).left().padTop(padSize);
        attributeRow.add(new Label(attributeKey, skin)).expand().padLeft(padSize).left().row();

        final String finalKey = phaseKey + "_" + attributeKey;
        attributeRow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.isHandled()) {
                    for (Table table : phaseStepContainers.values()) {
                        table.clearChildren();
                        table.invalidate();
                    }

                    if (!selectedAttribute.equals(finalKey)) {
                        Table phaseStepContainer = phaseStepContainers.get(finalKey);

                        phaseStepContainer.add(phaseStepTables.get(finalKey)).fillX();

                        selectedAttribute = finalKey;
                    } else {
                        selectedAttribute = "";
                    }
                }
            }
        });

        phaseStepContainers.put(finalKey, new Table());
        attributeRow.add(phaseStepContainers.get(finalKey)).colspan(2);

        phaseStepTables.put(finalKey, new VerticalGroup());
        VerticalGroup phaseStepGroup = phaseStepTables.get(finalKey);
        phaseStepGroup.grow();
        phaseStepGroup.align(Align.left);
        String partialKey = phaseSchema.getPhaseType().name() + "|" + attribute.getPhaseAttributeType().name();

        for (final PhaseStep step : attribute.getPhaseSteps()) {
            phaseStepGroup.addActor(buildPhaseStepRow(skin, partialKey, step));
        }

        return attributeRow;
    }

    private Table buildPhaseStepRow(Skin skin, String partialKey, final PhaseStep step) {
        float padding = FullEditorInterface.getInterfaceRowSize() / 8f;
        float iconSize = FullEditorInterface.getInterfaceRowSize() - padding * 2;

        Table phaseStepTable = new Table();
        phaseStepTable.left();
        final CheckBox checkBox = new CheckBox("", skin);
        checkBox.setChecked(step.isSelected());
        checkBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                step.setSelected(checkBox.isChecked());
                clearPhaseHierarchyValue(step);
                updateDodleGroupPhaseSchema();
            }
        });
        CheckBox.CheckBoxStyle style = checkBox.getStyle();
        float checkBoxScale = (FullEditorInterface.getInterfaceRowSize() * 0.60f);
        style.checkboxOn.setMinHeight(checkBoxScale);
        style.checkboxOn.setMinWidth(checkBoxScale);
        style.checkboxOff.setMinHeight(checkBoxScale);
        style.checkboxOff.setMinWidth(checkBoxScale);
        phaseStepTable.add(checkBox).pad(1.0f);

        Actor phaseButton;
        phaseButton = getButton(skin, phaseIcons, step, iconSize);
        phaseStepTable.add(phaseButton).pad(padding);
        if (!step.isInitial()) {
            phaseStepTable.add(LmlUtility.createButton(phaseIcons, "phase_arrow", 1.0f));
            final Actor button = getButton(skin, phaseIcons, step.getFallback(), iconSize);
            final String finalPhaseKey = partialKey + "|" + step.getPhaseStepType().name();
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_PHASE_STEP_FALL_BACK_MANAGER.toString(), finalPhaseKey);
                    event.handle();
                }
            });
            phaseStepTable.add(button).pad(padding);
        }
        phaseStepTable.row();
        return phaseStepTable;
    }

    private Actor getButton(Skin skin, TextureAtlas iconAtlas, PhaseStep step, float iconSize) {
        Actor phaseButton;
        phaseButton = new Table(skin);
        Button icon = LmlUtility.createButton(iconAtlas, step.getPhaseStepType().getIconAtlasKey(), 1.0f);
        ((Table) phaseButton).add(icon).size(iconSize, iconSize).expand().fill().top().center().row();
        ((Table) phaseButton).add(new Label(step.getPhaseStepType().getDescription(), skin, "small")).bottom().center();
        return phaseButton;
    }

    /**
     * remove the newPhase UI from the Settings Panel.
     */
    public final void resetUI() {
        if (phaseConfigSettingsTable != null) {
            phasesUIConfigSettings.clearChildren();
            phaseConfigSettingsTable = null;
            phasesUIConfigSettings.invalidate();
        }
        if (phaseStepSelectedEvent != null) {
            eventManager.removeListener(phaseStepSelectedEvent);
            phaseStepSelectedEvent = null;
        }
    }

    /**
     * Return the selected phase.
     */
    public final PhaseSchema getSelectedPhase() {
        for (PhaseSchema p : phaseStateManager.getPhaseSchemas()) {
            if (p.getPhaseType().name().equals(phaseStateManager.getSelectedPhaseType())) {
                return p;
            }
        }
        return null;
    }

    /**
     * loop through all phases for the current PhaseGroup and set phaseHierarchy to null.
     * Typically this will be done on any change to the Schema --- Type or Values(steps)
     */
    public final void clearPhaseHierarchyValue(PhaseStep step) {
        DodlesActor activePhaseGroup = objectManager.getActiveLayer();
        if (activePhaseGroup != null && activePhaseGroup instanceof DodlesGroup) {
            final DodlesGroup pg = ((DodlesGroup) activePhaseGroup);
            for (Phase p : pg.getPhases()) {
                if (step == null) {
                    p.setPhaseHierarchy(null);
                } else {
                    // since step is provided, loop through the Hierarchy for the Phase
                    // only null it if it's part of a change
                    PhaseHierarchy h = p.getPhaseHierarchy();
                    if (h != null) {
                        for (PHNode n : h.getHierarchyStack()) {
                            if (n.getStepType().name().equals(step.getPhaseStepType().name())) {
                                p.setPhaseHierarchy(null);
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * At any point in time, update the current selected objects DodleGroup with whatever schema is active.
     */
    public final void updateDodleGroupPhaseSchema() {
//        DodleEngine.getLogger().log("PhaseConfigView", "should be updating the DodlesGroup object phaseSchema");
        PhaseSchema currSchema = getSelectedPhase().dodleClone();
        DodlesActor activePhaseGroup = objectManager.getActiveLayer();
        if (activePhaseGroup != null && activePhaseGroup instanceof DodlesGroup) {
            final DodlesGroup pg = ((DodlesGroup) activePhaseGroup);
            pg.setPhaseSchema(currSchema);
        }
    }

    /**
     * load the saved PhaseSchema from the DodleGroup.
     */
    public final void loadPhaseSchemaFromDodleGroup() {
        DodlesActor activePhaseGroup = objectManager.getActiveLayer();
        if (activePhaseGroup != null && activePhaseGroup instanceof DodlesGroup) {
            final DodlesGroup pg = ((DodlesGroup) activePhaseGroup);
            PhaseSchema ps = pg.getPhaseSchema();
            if (ps != null) {
                phaseStateManager.setSelectedPhaseType(ps.getPhaseType().name());
                ArrayList<PhaseSchema> origSchemas = PhaseConstants.PHASES;
                phaseStateManager.setPhaseSchemas(new ArrayList<PhaseSchema>());

                // need to rebuilt this list an insert the appropriate PhaseSchema coming from the DodleGroup
                // may need to perform a deepClone here?
                for (PhaseSchema s : origSchemas) {
                    if (s.getPhaseType().name().equals(phaseStateManager.getSelectedPhaseType())) {
                        phaseStateManager.getPhaseSchemas().add(ps);
                    } else {
                        phaseStateManager.getPhaseSchemas().add(s);
                    }
                }

            } else {
                phaseStateManager.setSelectedPhaseType(PhaseType.EMPTY.name());
                phaseStateManager.setPhaseSchemas(new ArrayList<PhaseSchema>(PhaseConstants.PHASES));
            }
        }
    }
}
