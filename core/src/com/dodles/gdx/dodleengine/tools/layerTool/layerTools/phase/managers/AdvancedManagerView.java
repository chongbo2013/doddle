package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.DodleEngine;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.phase.UpdatePhaseValuesCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PHNode;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseStateManager;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseUIStates;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttribute;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseAttributeType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseHierarchy;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseSchema;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStep;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.objects.PhaseStepType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings.PhaseConfigView;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * panel specific to the Advanced Phases UI on the right side.
 * @author marknickel
 *
 */
@PerDodleEngine
public class AdvancedManagerView extends BasePanelView {
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final EngineEventManager eventManager;
    private final PhaseConfigView phaseConfigView;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStack;
    private final PhaseStateManager phaseStateManager;

    private Table advancedRootTable;
    private Table phasesUIManager;
    private EngineEventListener phaseModifiedListener;

    private PhasesUIRightPanel phasesUIRightPanel;

    private final PhaseHierarchy hierarchyStack;
    private EngineEventListener refreshFolderHierarchyListener;
    private EngineEventListener updatePhaseButtonTextListener;
    private UpdatePhaseValuesCommand upvCommand;
    private boolean inPhaseValuesEditing;

    @Inject
    public AdvancedManagerView(AssetProvider assetProvider, CommandFactory commandFactory, CommandManager commandManager, EngineEventManager eventManager, PhaseConfigView phaseConfigView, ObjectManager objectManager, OkCancelStackManager okCancelStack, PhaseStateManager phaseStateManager) {
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.phaseConfigView = phaseConfigView;
        this.objectManager = objectManager;
        this.okCancelStack = okCancelStack;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.phaseStateManager = phaseStateManager;

        this.hierarchyStack = phaseStateManager.getPhaseHierarchy();
    }

    /**
     * Get the full PhaseHierarchy object.
     */
    public final PhaseHierarchy getPhaseHierarchy() {
        return hierarchyStack;
    }

    /**
     * Initializes the component.
     */
    public final void initialize(Table rootTable, final Skin skin, final PhasesUIRightPanel uiRightPanel) {
        LmlParser parser = VisLml.parser()
                .skin(skin)
                .build();

        this.phasesUIManager = rootTable;
        this.phasesUIRightPanel = uiRightPanel;

        String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_PHASES_ADVANCED_VIEW);
        advancedRootTable = (Table) parser.parseTemplate(template).get(0);
        advancedRootTable.top();
        ScrollPane scrollPane = advancedRootTable.findActor("phasesUIManagerScroller");

        phasesUIManager.add(advancedRootTable).expandX().fillX();

        final VerticalGroup folderList = new VerticalGroup();
        folderList.grow();
        folderList.padTop(2f);

        if (hierarchyStack.getHierarchyStack().size() <= 0) {
            PHNode node = new PHNode();
            node.setDepth(-1);
            node.setStep(-1);
            node.setAttributeType(PhaseAttributeType.ROOT);
            node.setStepType(PhaseStepType.ROOT);
            hierarchyStack.getHierarchyStack().push(node);
        }

        uiRightPanel.initializePhaseButtonGroup();
        uiRightPanel.initializePhaseButtonList();

        buildFolderListAtDepth(skin, folderList, uiRightPanel.getPhaseButtonGroup());
        uiRightPanel.refreshPhaseButtons();

        Table layout = new Table();
        layout.add(folderList).row();
        layout.add(uiRightPanel.getPhaseButtonList());
        scrollPane.setWidget(layout);

        if (phaseModifiedListener == null) {
            phaseModifiedListener = new EngineEventListener(EngineEventType.PHASEUI_ACTIVE_PHASE_MODIFIED) {
                @Override
                public void listen(EngineEventData data) {
                    uiRightPanel.refreshPhaseButtons();
                }
            };

            eventManager.addListener(phaseModifiedListener);
        }

        TextButton phaseToggle = advancedRootTable.findActor("phaseToggler");
        advancedRootTable.removeActor(phaseToggle);
//        phaseToggle.addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y) {
//                if (!inPhaseValuesEditing) {
//                    eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_CONFIG_MANAGER.toString());
//                }
//            }
//        });

        refreshFolderHierarchyListener = new EngineEventListener(EngineEventType.PHASEUI_STATE_CHANGED) {
            @Override
            public void listen(EngineEventData data) {
                if (data.getFirstStringParam().equals(PhaseUIStates.RELOAD_ADVANCED_FOLDER_VIEW.toString())) {
                    buildFolderListAtDepth(skin, folderList, uiRightPanel.getPhaseButtonGroup());
                    uiRightPanel.refreshPhaseButtons();
                }
            }
        };
        eventManager.addListener(refreshFolderHierarchyListener);
    }
    
    /**
     * remove the Advanced UI from the Manager Panel.
     */
    public final void resetUI() {
        if (advancedRootTable != null) {
            //phasesUIManager.removeActor(advancedRootTable);
            phasesUIManager.clearChildren();
            advancedRootTable = null;
            phasesUIManager.invalidate();

            if (refreshFolderHierarchyListener != null) {
                eventManager.removeListener(refreshFolderHierarchyListener);
                refreshFolderHierarchyListener = null;
            }

            if (phaseModifiedListener != null) {
                eventManager.removeListener(phaseModifiedListener);
                phaseModifiedListener = null;
            }
        } else {
            DodleEngine.getLogger().log("AdvancedManagerView", "huh?  advancedRootTable is null?");
        }
    }

    /**
     * Build the folder list at a depth.
     */
    public final void buildFolderListAtDepth(final Skin skin, final VerticalGroup folderList, final ButtonGroup phaseButtonGroup) {
        folderList.clearChildren();

        // TODO: consider pulling the PhaseType from the DodleGroup.Phase.phaseSchema Object?
        // this sets the selectedPhaseSchema so the panel knows the folder structure based on the PhaseType
        final PhaseSchema selectedPhaseSchema = phaseStateManager.getSelectedPhaseSchema();


//        if (selectedPhaseSchema == null) {
//            return;
//        }

        TextureAtlas animationAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);

        Table items = new Table();
        folderList.addActor(items);

        // determine folders above the line
        for (PHNode node: hierarchyStack.getHierarchyStack()) {
            int depth = node.getDepth();
            int step = node.getStep();

            if (depth < 0) {
                TextButton rootFolder = new TextButton("*", skin, "medium");
                //rootFolder.addListener(buildFolderHierarchyClickListener(skin, folderList, phaseButtonGroup));

                rootFolder.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        // reset the Advanced view
                        hierarchyStack.getHierarchyStack().clear();
                        PHNode node = new PHNode();
                        node.setDepth(-1);
                        node.setStep(-1);
                        node.setAttributeType(PhaseAttributeType.ROOT);
                        node.setStepType(PhaseStepType.ROOT);
                        hierarchyStack.getHierarchyStack().push(node);

                        phaseStateManager.setInMove(false);
                        buildFolderListAtDepth(skin, folderList, phasesUIRightPanel.getPhaseButtonGroup());
                        phasesUIRightPanel.refreshPhaseButtons();

                        phasesUIRightPanel.initializePhaseConfigView();
                        phasesUIRightPanel.togglePanelExpand();
                    }
                });
                items.add(rootFolder);
            } else {
                if (phaseStateManager.isInMove()) {
                    if (depth == selectedPhaseSchema.getAttributeList().size()) {
                        depth -= 1;
                    }
                    PhaseStep level = selectedPhaseSchema.getAttributeList().get(depth).getPhaseSteps().get(step);
                    Table icon = LmlUtility.createImageTextButton(animationAtlas, skin,
                            "folder", "folder", level.getPhaseStepType().getDescription(), "medium", 1.75f);
                    icon.addListener(buildFolderHierarchyClickListener(skin, folderList, phaseButtonGroup));
                    items.row();
                    items.add(icon);
                }
            }
        }

        items.row();
        Table separator = new Table();
        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(Color.WHITE);
        pm1.fill();
        separator.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
        items.add(separator).height(5).expandX().fillX().padTop(2f).padBottom(2f);

        // unless you are in move mode, no need hierarchy
        if (phaseStateManager.isInMove()) {
            //-------
            // list folders below the line
            //-------
            PHNode hierarchyNode = hierarchyStack.getHierarchyStack().peek();
            int listDepth = 0;
            if (hierarchyNode.getDepth() >= 0) {
                listDepth = hierarchyNode.getDepth() + 1;
            }
            final int finalDepth = listDepth;

            if (selectedPhaseSchema != null && listDepth < selectedPhaseSchema.getAttributeList().size()) {
                for (int i = 0; i < selectedPhaseSchema.getAttributeList().get(listDepth).getPhaseSteps().size(); i++) {
                    final PhaseStep ps = selectedPhaseSchema.getAttributeList().get(listDepth).getPhaseSteps().get(i);
                    final PhaseAttribute pa = selectedPhaseSchema.getAttributeList().get(listDepth);
                    if (ps.isSelected()) {
                        //Button icon = LmlUtility.createButton(iconAtlas, step.getPhaseStepType().getIconAtlasKey(), 1.0f);

                        Table icon;

                        // if we are technically at a leafnode, show the hierarchy icon rather than the folder
                        if (listDepth == selectedPhaseSchema.getAttributeList().size() - 1) {
                            icon = LmlUtility.createImageTextButton(assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_PHASE_ICONS_ATLAS), skin,
                                    ps.getPhaseStepType().getIconAtlasKey(), ps.getPhaseStepType().getIconAtlasKey(), ps.getPhaseStepType().getDescription(), "medium", 1.25f);
                            //SnapshotArray<Actor> iconActors = icon.getChildren();
                            //Button button = (Button) iconActors.get(0);
                            //button.setDisabled(true);
                        } else {
                            icon = LmlUtility.createImageTextButton(animationAtlas, skin,
                                    "folder", "folder", ps.getPhaseStepType().getDescription(), "medium", 1.25f);
                        }

                        icon.setName(new Integer(i).toString());
                        icon.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                int folderStep = Integer.parseInt(event.getListenerActor().getName());

                                // so this is the level we'd be going to -- putting the phase inside this folder
                                // for a leafnode it would go "inside"
                                PHNode n = new PHNode();
                                n.setDepth(finalDepth);
                                n.setStep(folderStep);
                                n.setAttributeType(pa.getPhaseAttributeType());
                                n.setStepType(ps.getPhaseStepType());

                                // only go deeper if you are not at a leaf node
                                if (finalDepth < selectedPhaseSchema.getAttributeList().size() - 1) {
                                    hierarchyStack.getHierarchyStack().push(n);
                                    buildFolderListAtDepth(skin, folderList, phaseButtonGroup);
                                    eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_PROPERTIES_VIEW.toString());
                                    phaseStateManager.setAssignedPhaseStep(null);
                                } else {
                                    // updated the selected step
                                    phaseStateManager.setAssignedPhaseStep(ps);
                                }
                                eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.REFRESH_ASSIGNED_SLOT_NAME.toString());
                            }
                        });
                        items.row();
                        items.add(icon);
                    }
                }
            }

            // List any phases that have been assigned to this level and should
            // appear Above the line -- ie: not orphaned phases
            items.row();

            final DodlesGroup group = (DodlesGroup) objectManager.getActiveLayer();
            if (group != null) {
                for (int phaseIndex = 0; phaseIndex < objectManager.getActiveLayer().getViews().size(); phaseIndex++) {
                    final Phase phase = group.getPhases().get(phaseIndex);
                    boolean showPhaseButton = false;

//            DodleEngine.getLogger().log("AdvancedManagerPanel", "=");
//            DodleEngine.getLogger().log("AdvancedManagerPanel", "=");
//            DodleEngine.getLogger().log("AdvancedManagerView", "checking Phase: " + (phaseIndex + 1) + "   == " + phase.getName());
//            PhaseHierarchy.showTwoHierarchies( hierarchyStack, phase.getPhaseHierarchy());


                    // overly complex statement to determine if a phase button should appear above the line -- ie:  not orphaned
                    if ((hierarchyStack.getHierarchyStack() != null && phase.getPhaseHierarchy() != null)
                            && (hierarchyStack.getHierarchyStack().size() == phase.getPhaseHierarchy().getHierarchyStack().size() - 1)) {


                        for (int i = 0; i < hierarchyStack.getHierarchyStack().size(); i++) {
                            // compare two PHNodes
                            PHNode stackNode = hierarchyStack.getHierarchyStack().get(i);
                            PHNode phaseNode = phase.getPhaseHierarchy().getHierarchyStack().get(i);
//                    DodleEngine.getLogger().log("AdvancedManagerView","  - stackNode: " + stackNode.getAttributeType() + " == " + stackNode.getStepType());
//                    DodleEngine.getLogger().log("AdvancedManagerView","  - phaseNode: " + phaseNode.getAttributeType() + " == " + phaseNode.getStepType());
//                    DodleEngine.getLogger().log("AdvancedManagerView","");
                            if (stackNode.getAttributeType().name().equals(phaseNode.getAttributeType().name())
                                    && stackNode.getStepType().name().equals(phaseNode.getStepType().name())) {
                                showPhaseButton = true;
                            } else {
                                showPhaseButton = false;
                                break;
                            }
                        }
                    }
//            DodleEngine.getLogger().log("AdvancedManagerPanel", "showPhaseButton == " + showPhaseButton);
//            DodleEngine.getLogger().log("AdvancedManagerPanel", "=");
//            DodleEngine.getLogger().log("AdvancedManagerPanel", "=");

                    if (showPhaseButton) {
                        String displayName = phase.getDisplayName();
                        if (displayName == null) {
                            displayName = (phaseIndex + 1) + "";
                            phase.setDisplayName(displayName);
                        }
                        final TextButton phaseButton = new TextButton(displayName, skin, "toggle");

                        phaseButton.setName(phase.getName());

//                DodleEngine.getLogger().log("AdvancedManagerView", "Phase button: " + phaseButton.getText() + " == " + phaseButton.getName() + " == visibleID: " + group.getVisiblePhaseID());
//                DodleEngine.getLogger().log("AdvancedManagerView", "  -- setChecked: " + group.getVisiblePhaseID().equals(group.getPhases().get(phaseIndex).getName()));
                        phaseButton.setChecked(group.getVisiblePhaseID().equals(phase.getName()));

                        phaseButton.addListener(new ClickListener() {
                            // Initialize the ModifyPhaseSettingsView in the Left panel
                            public void clicked(InputEvent event, float x, float y) {
//                        DodleEngine.getLogger().log("AdvancedManagerView", "   -- phaseButton clicked: " + phaseButton.getName());
                                if (!inPhaseValuesEditing) {
                                    if (!group.getVisiblePhaseID().equals(phaseButton.getName())) {
                                        // First press - activate the phase...
                                        group.setVisiblePhase(phase.getName());
                                        objectManager.selectActor(phase);
                                    }

                                    upvCommand = (UpdatePhaseValuesCommand) commandFactory.createCommand(UpdatePhaseValuesCommand.COMMAND_NAME);
                                    upvCommand.init(objectManager.getActiveLayer().getName());

                                    okCancelStack.push(new Runnable() {
                                        @Override
                                        public void run() {
                                            commandManager.add(upvCommand);
                                            inPhaseValuesEditing = false;
                                            eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.EASEOUT_SETTINGS_PANEL.toString());
                                            eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_ADVANCED_FOLDER_VIEW.toString());
                                            eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_BASIC_PHASEBUTTONS.toString());
                                        }
                                    }, new Runnable() {
                                        @Override
                                        public void run() {
                                            upvCommand.undo();
                                            inPhaseValuesEditing = false;
                                            eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.EASEOUT_SETTINGS_PANEL.toString());
                                            eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_ADVANCED_FOLDER_VIEW.toString());
                                            eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_BASIC_PHASEBUTTONS.toString());
                                        }
                                    });
                                    eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_PROPERTIES_VIEW.toString());
                                    inPhaseValuesEditing = true;
                                }
                            }
                        });

                        items.add(phaseButton).row();
                        phaseButtonGroup.add(phaseButton);
                    } else {
//                DodleEngine.getLogger().log("AdvancedManagerView", "Phase button not shown");
//                DodleEngine.getLogger().log("AdvancedManagerView", "Phase button: " + phase.getDisplayName()+ " == " + phase.getName() + " == visibleID: " + group.getVisiblePhaseID());
                        int size = 0;
                        if (phase.getPhaseHierarchy() != null) {
                            size = phase.getPhaseHierarchy().getHierarchyStack().size();
                        }
//                DodleEngine.getLogger().log("AdvancedManagerView", "Phase hierarchy stack size: " + size);
                    }
                }
            }
        }

        items.row();
        separator = new Table();
        pm1 = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pm1.setColor(Color.WHITE);
        pm1.fill();
        separator.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));

        items.add(separator).height(5).expandX().fillX().padTop(2f).padBottom(2f);
    }

    private ClickListener buildFolderHierarchyClickListener(final Skin skin, final VerticalGroup folderList, final ButtonGroup phaseButtonGroup) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (hierarchyStack.getHierarchyStack().size() > 1) {
                    hierarchyStack.getHierarchyStack().pop();
                    buildFolderListAtDepth(skin, folderList, phaseButtonGroup);
                    eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_PROPERTIES_VIEW.toString());
                    phaseStateManager.setAssignedPhaseStep(null);
                    eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.REFRESH_ASSIGNED_SLOT_NAME.toString());
                }
            }
        };
    }
}
