package com.dodles.gdx.dodleengine.tools.scene;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.CompoundCommand;
import com.dodles.gdx.dodleengine.commands.scene.CreateLayerCommand;
import com.dodles.gdx.dodleengine.commands.scene.CreateSceneCommand;
import com.dodles.gdx.dodleengine.commands.scene.DeleteLayerCommand;
import com.dodles.gdx.dodleengine.commands.scene.DeleteSceneCommand;
import com.dodles.gdx.dodleengine.commands.scene.EditLayerCommand;
import com.dodles.gdx.dodleengine.commands.scene.EditSceneCommand;
import com.dodles.gdx.dodleengine.commands.scene.ReorderLayersCommand;
import com.dodles.gdx.dodleengine.commands.scene.ReorderScenesCommand;
import com.dodles.gdx.dodleengine.editor.ActorPreviewWidget;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlay;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlayRegistry;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.scenegraph.Layer;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import com.dodles.gdx.dodleengine.tools.animation.BlockingDraggable;
import com.dodles.gdx.dodleengine.tools.animation.DodlesDragListener;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseConstants;
import com.dodles.gdx.dodleengine.util.DialogUtility;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.dodles.gdx.dodleengine.util.NumbersToLetters;
import com.dodles.gdx.dodleengine.util.ParamRunnable;
import com.kotcrab.vis.ui.layout.DragPane;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.Separator;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@PerDodleEngine
public class SceneUIListPanel implements FullEditorDodleOverlay {
    public static final String PANEL_NAME = "SceneUIListPanel";
    public static final String STATE_NAME = SceneTool.TOOL_NAME + "." + PANEL_NAME;

    public static final String ADD_MASTER_LAYER = "Add Master Layer";
    public static final String ADD_SCENE = "Add Scene";
    public static final String NEW = "New";
    public static final String LAYER_NAME = "Layer Name:";
    public static final String SCENE_NAME = "Scene Name:";
    public static final String EDIT = "Edit";
    public static final String SCENES = "Scenes";
    public static final String SEARCH = "Search";
    public static final String COPY_SCENE = "Copy Scene";
    public static final String DEFAULT_SCENE_NAME = "Scene";
    public static final String DEFAULT_LAYER_NAME = "Layer";
    public static final String COPY_SCENE_HEADER_TITLE = "Select Scene to Copy";
    public static final String LINK_SCENE_HEADER_TITLE = "Select Scene to Link";

    private AssetProvider assetProvider;
    private CommandFactory commandFactory;
    private CommandManager commandManager;
    private final DialogUtility dialogUtility;
    private EngineEventManager eventManager;
    private EventBus eventBus;
    private ObjectManager objectManager;
    private OkCancelStackManager okCancelStackManager;

    private Table sceneUI;
    private Table scrollContent;
    private Stack overlay;

    private Skin skin;

    private boolean toggle;
    private float originY;
    private float padBottom = 0f;
    private float padSize;
    private float threeRowSize;
    private boolean fullScreenList;

    private SnapshotArray<Actor> layerList;
    private List<ObjectManager.SceneData> sceneDataList;
    private String selectedScene;
    private DragPane dragPane;
    private CompoundCommand currentCompoundCommand;

    private boolean pushedOkStack;
    private boolean inEditMode; // interloc when an edit button is pressed
    private boolean inNewMode;  // interloc for when you are adding a new Layer or Scene
    private TextureAtlas animationIconsAtlas;
    private TextureAtlas toolbarAtlas;

    private Layer.DisplayMode[] eList = {Layer.DisplayMode.VISIBLE, Layer.DisplayMode.PARTIAL, Layer.DisplayMode.HIDDEN};


    @Inject
    public SceneUIListPanel(AssetProvider assetProvider, CommandFactory commandFactory, CommandManager commandManager, DialogUtility dialogUtility, EventBus eventBus, EngineEventManager eventManager, FullEditorDodleOverlayRegistry fedoRegistry, ObjectManager objectManager, OkCancelStackManager okCancelStackManager) {
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.dialogUtility = dialogUtility;
        this.eventBus = eventBus;
        this.eventManager = eventManager;
        this.objectManager = objectManager;
        this.okCancelStackManager = okCancelStackManager;

        fedoRegistry.registerOverlay(this);
    }

    @Override
    public final String getName() {
        return PANEL_NAME;
    }

    @Override
    public final void initialize(Stack dodleOverlayStack, Skin pSkin) {
        skin = pSkin;
        overlay = dodleOverlayStack;
        threeRowSize = FullEditorInterface.getInterfaceRowSize() * 3;
        padSize = DensityManager.getScale() * 20;

        if (sceneUI == null) {
            animationIconsAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_ANIMATIONICONS_ATLAS);
            toolbarAtlas = assetProvider.getTextureAtlas(TextureAtlasAssets.EDITOR_TOOL_TOOLBARICONS_ATLAS);

            sceneUI = FullEditorInterface.getScrollableOverlay(assetProvider, skin, true);
            sceneUI.setBackground(FullEditorViewState.TOOLBAR_MIDDLE_ACTIVATED_COLOR);
            sceneUI.setVisible(false);
            dodleOverlayStack.add(sceneUI);

            scrollContent = sceneUI.findActor("scrollContent");

            eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {

                @Override
                public void listen(EngineEventData data) {
                    String state = data.getFirstStringParam();
                    String action = data.getSecondStringParam();
                    if (state != null && state.indexOf(STATE_NAME) == 0) {


                        if (action.equals("LayerList")) {
                            easeIn();
                            displayLayerList();
                        } else if (action.equals("AddScene")) {
                            fullScreenList = true;
                            easeIn();
                            displayAddScene();
                        } else if (action.equals("SceneList")) {
                            fullScreenList = true;
                            easeIn();
                            displaySceneList();
                        }

                        currentCompoundCommand = (CompoundCommand) commandFactory.createCommand(CompoundCommand.COMMAND_NAME);

                        // okCancel for LayerList
                        okCancelStackManager.push(new Runnable() {
                            @Override
                            public void run() {
                                commandManager.add(currentCompoundCommand);
                                pushedOkStack = false;
                                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SceneTool.TOOL_NAME);
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                if (pushedOkStack) {
                                    currentCompoundCommand.undo();
                                    pushedOkStack = false;
                                    eventBus.publish(EventType.COMMAND_STACK_CHANGED);
                                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SceneTool.TOOL_NAME);
                                }
                            }
                        });

                        pushedOkStack = true;

                    } else {
                        easeOut();
                        //if (fullScreenList) {
                        //    fullScreenList = false;
                        //}
                        if (pushedOkStack) {
                            pushedOkStack = false;
                            okCancelStackManager.popCancel();
                        }
                    }
                }
            });
        }
    }

    /**
     * render the Layer List - primary entrypoint into the UI.
     */
    public final void displayLayerList() {
        scrollContent.clear();
        scrollContent.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();

        Table addLayerRow = new Table();

        TextButton addMasterLayerButton = new TextButton(ADD_MASTER_LAYER, skin);
        addMasterLayerButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!inEditMode) {
                    addMasterLayer();
                }
            }
        });
        addLayerRow.add(addMasterLayerButton).expand().padLeft(padSize).align(Align.left);
        scrollContent.add(addLayerRow).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).row();

        // Initialize drag pane and make sure that it fills all available horizontal space...
        dragPane = new DragPane(true);
        dragPane.fillX();
//        dragPane.getVerticalGroup().grow();

        final BlockingDraggable draggable = new BlockingDraggable();
        final DodlesDragListener dragListener = new DodlesDragListener(eventManager, DodlesDragListener.LAYER_DRAGTYPE);
        draggable.setListener(dragListener);
        dragPane.setDraggable(draggable);
        
        scrollContent.add(dragPane).expandX().fillX().row();
        
        // Display order is opposite of render order!
        final Scene activeScene = objectManager.getActiveScene();
        layerList = new SnapshotArray<Actor>(activeScene.getLayers());
        layerList.reverse();

        final ArrayList<CollapsibleWidget> allCollapsibles = new ArrayList<CollapsibleWidget>();
        
        dragListener.addDragEndRunnable(new Runnable() {
            @Override
            public void run() {
                SnapshotArray<Actor> rows = dragPane.getVerticalGroup().getChildren();
                ArrayList<Layer> newLayerOrder = new ArrayList<Layer>();
                
                for (int i = rows.size - 1; i >= 0; i--) {
                    Actor row = rows.get(i);
                    String[] keys = row.getName().split("_");
                    String layerID = keys[1];
                    
                    newLayerOrder.add(activeScene.getView(layerID));
                }
                
                ReorderLayersCommand reorderCommand = (ReorderLayersCommand) commandFactory.createCommand(ReorderLayersCommand.COMMAND_NAME);
                reorderCommand.init(activeScene, newLayerOrder);
                reorderCommand.execute();
                currentCompoundCommand.addCommand(reorderCommand);
            }
        });
        
        int i = -1;
        for (Actor l : layerList) {
            final Layer layer = (Layer) l;
            //DodleEngine.getLogger().log("SceneUIListPanel Show Layers", layer.getName() + " == " + layer.getDisplayName() + " == " + layer.getDisplayMode().name());
            final Integer indexPos = ++i;
            final Table layerDraggableRow = new Table();
            layerDraggableRow.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();
            layerDraggableRow.setName(objectManager.getActiveScene().getName() + "_" + layer.getName());

            // set up the collapsible region for the Scene list
            Table collapseContent = new Table();
            collapseContent.setFillParent(true);
            final CollapsibleWidget collapseWrapper = new CollapsibleWidget(collapseContent);
            collapseWrapper.setCollapsed(true);
            float padLeft = padSize * 2 + FullEditorInterface.getInterfaceRowSize();

            final Table editLayerLinkSceneShim = new Table();
            collapseContent.add(editLayerLinkSceneShim);

            Table row = new Table();
            row.setTouchable(Touchable.enabled);

            Image handleImage = new Image(new TextureRegionDrawable(animationIconsAtlas.findRegion("handle")), Scaling.fillY);
            handleImage.setTouchable(Touchable.enabled);
            dragListener.addDragHandle(handleImage);
            row.add(handleImage).size(FullEditorInterface.getInterfaceRowSize()).padLeft(padSize);
            
            ActorPreviewWidget actorPreview = new ActorPreviewWidget((Layer) l, true);
            row.add(actorPreview).size(FullEditorInterface.getInterfaceRowSize()).padLeft(padSize);

            final Table layerActionShim = new Table();
            controlVisibilityShowLabelUI(layerActionShim, layer.getDisplayName(), layer.getName(), "", true);
            row.add(layerActionShim);

            TextButton editButton = new TextButton(EDIT, skin);
            editButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!inEditMode) {
                        selectedScene = null;
                        inEditMode = true;
                        deleteCellInRowUI(layerActionShim, layer.getDisplayName(), layer.getName(), indexPos.toString(), "layer");
                        selectSceneWithCheckbox(editLayerLinkSceneShim, LINK_SCENE_HEADER_TITLE, layer.getPassthroughScene());

                        collapseWrapper.setCollapsed(false, true);
                        
                        final Runnable cancel = new Runnable() {
                            @Override
                            public void run() {
                                inEditMode = false;
                                collapseWrapper.setCollapsed(true, true);
                                controlVisibilityShowLabelUI(layerActionShim, layer.getDisplayName(), layer.getName(), indexPos.toString(), true);
                            }
                        };

                        okCancelStackManager.push(new Runnable() {
                            @Override
                            public void run() {
                                TextField layerNameField = (TextField) layerActionShim.findActor("textEditingField");                                
                                EditLayerCommand editCommand = (EditLayerCommand) commandFactory.createCommand(EditLayerCommand.COMMAND_NAME);
                                editCommand.init(layer, objectManager.getScene(selectedScene), layerNameField.getText());
                                editCommand.execute();
                                currentCompoundCommand.addCommand(editCommand);
                                
                                cancel.run();
                            }
                        }, cancel);
                    }
                }
            });


            row.add(editButton).expandX().align(Align.right).padRight(padSize);
            layerDraggableRow.add(row).expandX().fillX().align(Align.left).height(FullEditorInterface.getInterfaceRowSize()).row();

            layerDraggableRow.add(collapseWrapper).expandX().fillX().row();

            dragPane.addActor(layerDraggableRow);
        }
    }

    /**
     * build UI to add a Master layer.
     */
    public final void addMasterLayer() {
        scrollContent.clear();
        scrollContent.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();
        final Table newLayerUIShim = new Table();
        final Table linkSceneUIShim = new Table();

        Table masterLayerRow = new Table();
        masterLayerRow.add(new Label(ADD_MASTER_LAYER, skin, "default-black")).expand().padLeft(padSize).align(Align.left);
        scrollContent.add(masterLayerRow).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).row();

        // action buttons
        Table actionRow = new Table();

        TextButton newLayerButton = new TextButton(NEW, skin);
        newLayerButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!inNewMode) {
                    inNewMode = true;
                    newLayer(newLayerUIShim);

                    okCancelStackManager.push(new Runnable() {
                        @Override
                        public void run() {
                            TextField layerNameField = (TextField) newLayerUIShim.findActor("layerEditingField");

                            CreateLayerCommand command = (CreateLayerCommand) commandFactory.createCommand(CreateLayerCommand.COMMAND_NAME);
                            command.init(objectManager.getActiveScene().getName(), UUID.uuid(), null, layerNameField.getText());
                            command.execute();
                            currentCompoundCommand.addCommand(command);

                            inNewMode = false;
                            newLayerUIShim.clear();
                            displayLayerList();

                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            newLayerUIShim.clear();
                            inNewMode = false;
                        }
                    });
                }
            }
        });

        actionRow.add(newLayerButton).padLeft(padSize).align(Align.left);

        TextButton linkScenesButton = new TextButton(SCENES, skin);
        linkScenesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!inNewMode) {
                    selectedScene = null;
                    inNewMode = true;
                    newLayer(newLayerUIShim);
                    selectSceneWithCheckbox(linkSceneUIShim, LINK_SCENE_HEADER_TITLE, null);

                    okCancelStackManager.push(new Runnable() {
                        @Override
                        public void run() {
                            // create the new Layer
                            TextField layerNameField = (TextField) newLayerUIShim.findActor("layerEditingField");

                            String newLayerID = UUID.uuid();
                            CreateLayerCommand command = (CreateLayerCommand) commandFactory.createCommand(CreateLayerCommand.COMMAND_NAME);
                            command.init(objectManager.getActiveScene().getName(), UUID.uuid(), selectedScene, layerNameField.getText());
                            command.execute();
                            currentCompoundCommand.addCommand(command);

                            inNewMode = false;
                            newLayerUIShim.clear();
                            linkSceneUIShim.clear();
                            displayLayerList();
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            newLayerUIShim.clear();
                            linkSceneUIShim.clear();
                            inNewMode = false;
                        }
                    });
                }
            }
        });
        actionRow.add(linkScenesButton).padLeft(padSize);

        TextButton searchButton = new TextButton(SEARCH, skin);
        actionRow.add(searchButton).padLeft(padSize);

        scrollContent.add(actionRow).expandX().align(Align.left).height(FullEditorInterface.getInterfaceRowSize()).row();

        scrollContent.add(newLayerUIShim).row();
        scrollContent.add(linkSceneUIShim).row();
    }

    /**
     * build UI to add and copy Scenes.
     */
    public final void displayAddScene() {
        scrollContent.clear();
        scrollContent.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();
        final Table newSceneUIShim = new Table();

        Table masterLayerRow = new Table();
        masterLayerRow.add(new Label(ADD_SCENE, skin, "default-black")).expand().padLeft(padSize).align(Align.left);
        scrollContent.add(masterLayerRow).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).row();

        // action buttons
        Table actionRow = new Table();

        TextButton newSceneButton = new TextButton(NEW, skin);
        newSceneButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!inNewMode) {
                    inNewMode = true;
                    newScene(newSceneUIShim);

                    okCancelStackManager.push(new Runnable() {
                        @Override
                        public void run() {
                            TextField sceneNameField = (TextField) newSceneUIShim.findActor("sceneEditingField");
                            
                            CreateSceneCommand command = (CreateSceneCommand) commandFactory.createCommand(CreateSceneCommand.COMMAND_NAME);
                            command.init(sceneNameField.getText(), null);
                            command.execute();
                            currentCompoundCommand.addCommand(command);

                            inNewMode = false;
                            newSceneUIShim.clear();
                            displaySceneList();
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            newSceneUIShim.clear();
                            inNewMode = false;
                        }
                    });
                }
            }
        });

        actionRow.add(newSceneButton).padLeft(padSize).align(Align.left);

        final TextButton copySceneButton = new TextButton(COPY_SCENE, skin);
        copySceneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!inNewMode) {
                    inNewMode = true;
                    selectedScene = null;
                    selectSceneWithCheckbox(newSceneUIShim, COPY_SCENE_HEADER_TITLE, null);

                    okCancelStackManager.push(new Runnable() {
                        @Override
                        public void run() {
                            Scene scene = objectManager.getScene(selectedScene);
                            CreateSceneCommand command = (CreateSceneCommand) commandFactory.createCommand(CreateSceneCommand.COMMAND_NAME);
                            command.init("Copy of " + scene.getDisplayName(), selectedScene);
                            command.execute();
                            currentCompoundCommand.addCommand(command);
                            
                            selectedScene = null;
                            inNewMode = false;
                            displaySceneList();
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            newSceneUIShim.clear();
                            inNewMode = false;
                        }
                    });
                }
            }
        });
        actionRow.add(copySceneButton).padLeft(padSize);

        TextButton searchButton = new TextButton(SEARCH, skin);
        actionRow.add(searchButton).padLeft(padSize);

        scrollContent.add(actionRow).expandX().align(Align.left).height(FullEditorInterface.getInterfaceRowSize()).row();
        scrollContent.add(newSceneUIShim).row();
    }

    /**
     * render the Scene List.
     */
    public final void displaySceneList() {
        scrollContent.clear();
        scrollContent.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();

        Table addSceneRow = new Table();

        TextButton addSceneButton = new TextButton(ADD_SCENE, skin);
        addSceneButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!inEditMode) {
                    displayAddScene();
                }
            }
        });
        addSceneRow.add(addSceneButton).expand().padLeft(padSize).align(Align.left);
        scrollContent.add(addSceneRow).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).row();

        // Initialize drag pane and make sure that it fills all available horizontal space...
        dragPane = new DragPane(true);
        dragPane.fillX();
//        dragPane.getVerticalGroup().grow();

        final BlockingDraggable draggable = new BlockingDraggable();
        final DodlesDragListener dragListener = new DodlesDragListener(eventManager, DodlesDragListener.SCENE_DRAGTYPE);
        draggable.setListener(dragListener);
        dragPane.setDraggable(draggable);
        scrollContent.add(dragPane).expandX().fillX().row();
        
        
        dragListener.addDragEndRunnable(new Runnable() {
            @Override
            public void run() {
                SnapshotArray<Actor> rows = dragPane.getVerticalGroup().getChildren();
                ArrayList<Scene> holdSceneList = new ArrayList<Scene>();
                for (Actor a : rows) {
                    Scene s = objectManager.getScene(a.getName());
                    holdSceneList.add(s);
                }
                
                ReorderScenesCommand reorderCommand = (ReorderScenesCommand) commandFactory.createCommand(ReorderScenesCommand.COMMAND_NAME);
                reorderCommand.init(holdSceneList);
                reorderCommand.execute();
                currentCompoundCommand.addCommand(reorderCommand);
            }
        });

        int i = -1;
        sceneDataList = objectManager.allSceneData();
        for (final ObjectManager.SceneData sceneData : sceneDataList) {
            i++;
            //final String scene = sceneList.get(i);
            final Integer indexPos = i;
            final Table layerDraggableRow = new Table();
            final Table editSceneTable = new Table();
            layerDraggableRow.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();
            layerDraggableRow.setName(sceneData.getScene().getName());

            Table row = new Table();
            row.setTouchable(Touchable.enabled);

            Image handleImage = new Image(new TextureRegionDrawable(animationIconsAtlas.findRegion("handle")), Scaling.fillY);
            handleImage.setTouchable(Touchable.enabled);
            dragListener.addDragHandle(handleImage);
            row.add(handleImage).size(FullEditorInterface.getInterfaceRowSize()).padLeft(padSize);

            // scene id that does not change
            row.add(new Label(sceneData.getScene().getNumber().toString(), skin, "default-black")).padLeft(padSize);

            final Table sceneActionShim = new Table();
            controlVisibilityShowLabelUI(sceneActionShim, sceneData.getScene().getDisplayName(), sceneData.getScene().getName(),  "", false);
            row.add(sceneActionShim);

            TextButton editButton = new TextButton(EDIT, skin);
            editButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!inEditMode) {
                        inEditMode = true;
                        deleteCellInRowUI(sceneActionShim, sceneData.getScene().getDisplayName(), sceneData.getScene().getName(), indexPos.toString(), "scene");

                        okCancelStackManager.push(new Runnable() {
                            @Override
                            public void run() {
                                TextField sceneNameField = (TextField) sceneActionShim.findActor("textEditingField");
                                
                                EditSceneCommand editSceneCommand = (EditSceneCommand) commandFactory.createCommand(EditSceneCommand.COMMAND_NAME);
                                editSceneCommand.init(sceneData.getScene(), sceneNameField.getText());
                                editSceneCommand.execute();
                                currentCompoundCommand.addCommand(editSceneCommand);
                                
                                controlVisibilityShowLabelUI(sceneActionShim, sceneNameField.getText(), sceneData.getScene().getName(), indexPos.toString(), false);
                                inEditMode = false;
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                inEditMode = false;
                                controlVisibilityShowLabelUI(sceneActionShim, sceneData.getScene().getDisplayName(), sceneData.getScene().getName(), indexPos.toString(), false);
                            }
                        });
                    }
                }
            });


            row.add(editButton).expandX().align(Align.right).padRight(padSize);

            layerDraggableRow.add(row).expandX().fillX().align(Align.left).height(FullEditorInterface.getInterfaceRowSize()).row();
            dragPane.addActor(layerDraggableRow);
        }
    }

    /**
     * generic scene list with checkbox for picking a scene -- used when Linking a Scene to a Layer.
     * @param shim
     * @param sceneHeaderTitle
     */
    public final void selectSceneWithCheckbox(Table shim, final String sceneHeaderTitle, Scene passThroughScene) {
        shim.clear();
        shim.add(new Label(sceneHeaderTitle, skin, "default-black")).expandX().fillX().height(FullEditorInterface.getInterfaceRowSize()).padLeft(padSize).align(Align.left).row();
        shim.add(new Separator()).height(DensityManager.getScale()).expandX().fillX().row();

        Table sceneListTable = new Table();
        sceneListTable.padTop(10f);
        ButtonGroup sceneButtonGroup = new ButtonGroup();
        sceneButtonGroup.setMaxCheckCount(1);
        sceneButtonGroup.setMinCheckCount(0);

        int i = -1;
        sceneDataList = objectManager.allSceneData();
        for (final ObjectManager.SceneData sceneData : sceneDataList) {

            final Scene scene = sceneData.getScene();
            Table row = new Table();
            final Table editSceneTable = new Table();

            CheckBox pickScene = new CheckBox("", skin);
            sceneButtonGroup.add(pickScene);
            pickScene.setName(scene.getName());
            pickScene.setChecked(false);
            CheckBox.CheckBoxStyle style = pickScene.getStyle();
            float checkBoxScale = (FullEditorInterface.getInterfaceRowSize() * 0.60f);
            style.checkboxOn.setMinHeight(checkBoxScale);
            style.checkboxOn.setMinWidth(checkBoxScale);
            style.checkboxOff.setMinHeight(checkBoxScale);
            style.checkboxOff.setMinWidth(checkBoxScale);

            // see if any scenes match
            if (passThroughScene != null && scene.getName().equals(passThroughScene.getName())) {
                pickScene.setChecked(true);
                selectedScene = passThroughScene.getName();
            }

            boolean isLinked = isScenePreviouslyLinked(scene.getName(), objectManager.getActiveScene().getName());
            if (!isLinked) {
                pickScene.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        CheckBox actor = (CheckBox) event.getListenerActor();
                        if (((CheckBox) actor).isChecked()) {
                            selectedScene = actor.getName();
                        } else {
                            selectedScene = null;
                        }
                    }
                });
                row.add(pickScene);
                createLabelField(editSceneTable, scene.getDisplayName(), "", "default-black");
                row.add(editSceneTable);
            } else {
                Label spacer = new Label("", skin, "default-black");
                row.add(spacer).width(checkBoxScale);
                createLabelField(editSceneTable, scene.getDisplayName(), "", "default-italics-black");
                row.add(editSceneTable);
            }

            sceneListTable.add(row).expandX().fillX().align(Align.left).height(FullEditorInterface.getInterfaceRowSize()).row();

        }

        shim.add(sceneListTable).expandX().fillX().row();

    }

    private void controlVisibilityShowLabelUI(Table shim, String displayName, String layerID, String labelFieldName, boolean showImage) {
        final Table editLabelTable = new Table();
        final Table eyeballTable = new Table();
        shim.clear();

        // TODO: don't forget to add a layer identifier so that in the listener we know which layer to update visibility
        if (showImage) {
            // find the initial state
            Layer layer = objectManager.getActiveScene().getView(layerID);
            
            if (layer == null) {
                return;
            }
            
            Layer.DisplayMode layerDisplay = layer.getDisplayMode();

            int eyeBallListIndex = -1;
            for (int i = 0; i < eList.length; i++) {
                if (layerDisplay.getIconName().equals(eList[i].getIconName())) {
                    eyeBallListIndex = i;
                    break;
                }
            }

            ImageButton eyeballButton = toggleNextEyeballState((eyeBallListIndex - 1) + "~" + layerID);
            eyeballButton.addListener(toggleEyeButtonListener(eyeballTable, layerID));
            eyeballTable.add(eyeballButton);
            shim.add(eyeballTable);
        }

        createLabelField(editLabelTable, displayName, labelFieldName, "default-black");
        shim.add(editLabelTable).padLeft(padSize);
    }

    private void deleteCellInRowUI(Table shim, String layer, String oID, String labelFieldName, String type) {
        final Table editLabelTable = new Table();
        final Table eyeballTable = new Table();
        shim.clear();

        ImageButton trashButton = LmlUtility.createButton(toolbarAtlas, "trash_1", "trash_1", 1.0f);
        // construct a key so that we know what part of the objectManager we need to update
        // "scene" or "layer _ position of the row in the UI _ scene.getName() or layer.getName()
        trashButton.setName(type + "_" + labelFieldName + "_" + oID);

        trashButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                ImageButton thisButton = (ImageButton) event.getListenerActor();
                String[] name = thisButton.getName().split("_");
                final String id = name[2];
                final Integer pos = new Integer(name[1]);
                final String t = name[0];

                dialogUtility.confirm("Delete This Scene?", "Are you sure you want\nto delete this scene?", "Yes", "No", new ParamRunnable<Boolean>() {
                    @Override
                    public void run(Boolean parameter) {
                        if (parameter) {
                            if (t.equals("layer")) {                                
                                DeleteLayerCommand dlc = (DeleteLayerCommand) commandFactory.createCommand(DeleteLayerCommand.COMMAND_NAME);
                                dlc.init(objectManager.getActiveScene().getName(), id);
                                dlc.execute();
                                currentCompoundCommand.addCommand(dlc);

                                displayLayerList();
                            } else if (t.equals("scene")) {
                                //ObjectManager.SceneData scene = sceneDataList.get(pos);

                                DeleteSceneCommand dsc = (DeleteSceneCommand) commandFactory.createCommand(DeleteSceneCommand.COMMAND_NAME);
                                dsc.init(id);
                                dsc.execute();
                                currentCompoundCommand.addCommand(dsc);

                                displaySceneList();
                            }
                            
                            okCancelStackManager.popCancel();
                            inEditMode = false;
                            eventBus.publish(EventType.COMMAND_STACK_CHANGED);
                        }
                    }
                });

            }
        });

        eyeballTable.add(trashButton);
        shim.add(eyeballTable);

        createTextField(editLabelTable, layer, labelFieldName);
        shim.add(editLabelTable).padLeft(padSize);
    }

    /**
     * toggle to the next Eyeball state in the eyeballList.
     * @return
     */
    private ImageButton toggleNextEyeballState(String stateIndex) {
        String[] keys = stateIndex.split("~");
        int currEyeBall = Integer.parseInt(keys[0]);
        currEyeBall++;
        if (currEyeBall > 2) {
            currEyeBall = 0;
        }
        ImageButton eyeballButton = LmlUtility.createButton(toolbarAtlas, eList[currEyeBall].getIconName(), eList[currEyeBall].getIconName(), 1.0f);
        eyeballButton.setName(currEyeBall + "~" + keys[1]);
        return eyeballButton;
    }

    /**
     * create a listener for the eyeball button.  very inception-like as we
     * recreate the asset.
     * @param shim
     * @return
     */
    private ClickListener toggleEyeButtonListener(final Table shim, final String layerID) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // for now, toggle between the states
                ImageButton thisButton = (ImageButton) event.getListenerActor();
                shim.clear();

                ImageButton eyeballButton = toggleNextEyeballState(thisButton.getName());
                String[] keys = eyeballButton.getName().split("~");
                Layer layer = objectManager.getActiveScene().getView(keys[1]);
                layer.setDisplayMode(Layer.DisplayMode.valueOf(eList[new Integer(keys[0])].name()));

                eyeballButton.addListener(toggleEyeButtonListener(shim, layerID));
                shim.add(eyeballButton);
            }
        };
    }

    /**
     * mini UI for editing a new layer name.
     * @return
     */
    public final void newLayer(Table shim) {
        shim.clear();
        Table newLayerUIRow = new Table();
        newLayerUIRow.add(new Label(LAYER_NAME, skin, "default-black"));

        TextField layerName = new TextField(DEFAULT_LAYER_NAME + " " + NumbersToLetters.numberToLetters(objectManager.getMaxLayerId() - 1), skin);
        layerName.setName("layerEditingField");
        newLayerUIRow.add(layerName);
        shim.add(newLayerUIRow);
    }

    /**
     * Add New Scene widget.
     * @param shim
     */
    public final void newScene(Table shim) {
        shim.clear();
        Table newSceneUIRow = new Table();
        newSceneUIRow.add(new Label(SCENE_NAME, skin, "default-black"));

        TextField sceneName = new TextField(DEFAULT_SCENE_NAME + " " + objectManager.getMaxSceneId(), skin);
        sceneName.setName("sceneEditingField");
        newSceneUIRow.add(sceneName);
        shim.add(newSceneUIRow);
    }

    /**
     * Overlay Cell Editor for an In-Cell TextField.
     * @param shim
     * @param layerName
     * @param id
     */
    public final void createTextField(Table shim, String layerName, String id) {
        shim.clear();

        Table editLayerRow = new Table();
        TextField layerNameField = new TextField(layerName, skin);
        layerNameField.setName("textEditingField");
        editLayerRow.add(layerNameField);
        shim.add(editLayerRow);
    }

    /**
     * Cell generator for the Field name in the List widget.
     * @param shim
     * @param layerName
     * @param id
     */
    public final void createLabelField(Table shim, String layerName, String id, String styleName) {
        shim.clear();

        Table layerNameRow = new Table();
        layerNameRow.add(new Label(layerName, skin, styleName));
        shim.add(layerNameRow);
    }

    /**
     * check the tree under the sceneIDToLink to make sure that sceneIDSource is not already
     * linked.  Must prevent Inception.
     * @param sceneIDToLink
     * @param sceneIDSource
     * @return
     */
    public final boolean isScenePreviouslyLinked(String sceneIDToLink, String sceneIDSource) {
        boolean isLinked = false;

        // examine the tree under sceneIDToLink
        Scene sceneToLink = objectManager.getScene(sceneIDToLink);
        Scene sceneSource = objectManager.getScene(sceneIDSource);

        // no, you cannot link a scene to itself
        if (sceneToLink.getName().equals(sceneSource.getName())) {
            return true;
        }

        SnapshotArray<Actor> layers = sceneToLink.getLayers();
        for (Actor actor : layers) {
            Layer layer = (Layer) actor;
            Scene passThroughScene = layer.getPassthroughScene();

            if (passThroughScene != null) {
                if (passThroughScene.getName().equals(sceneIDSource)) {
                    return true;
                }
                isLinked = isScenePreviouslyLinked(passThroughScene.getName(), sceneIDSource);
            }
        }
        return isLinked;
    }

    /**
     * ease in the Layer Overlay.
     */
    public final void easeIn() {
        // unused calculation, but saving for posterity in case top-down panel slide-ins become necessary
        // float artifacts = sceneUI.getHeight() + (FullEditorInterface.getTrayTabOffset()/2) + (FullEditorInterface.getInterfaceRowSize() * 2);
        if (fullScreenList) {
            Cell cell = LmlUtility.getCell(overlay);
            cell.padBottom(padBottom);
        }
        sceneUI.setY(-sceneUI.getHeight());
        sceneUI.setVisible(true);
        Action animation = Actions.sequence(Actions.moveTo(sceneUI.getX(), 0, PhaseConstants.PANEL_EASE_DURATION), Actions.visible(true));
        sceneUI.addAction(animation);
        toggle = true;
    }

    /**
     * ease out the layer overlay.
     */
    public final void easeOut() {
        sceneUI.setY(0);
        Action animation = Actions.sequence(Actions.moveTo(sceneUI.getX(), -sceneUI.getHeight(), PhaseConstants.PANEL_EASE_DURATION), Actions.visible(false));
        sceneUI.addAction(animation);
        toggle = false;
        if (fullScreenList) {
            //DodleEngine.getLogger().log("SceneUIListPanel", "reseting padBottom to threeRowSize");
            Cell cell = LmlUtility.getCell(overlay);
            cell.padBottom(threeRowSize);
            fullScreenList = false;
        }
    }

    /**
     * expose to other parts of the SceneTool to know if a mode has been entered.
     * @return
     */
    public final boolean isPushedOkStack() {
        return pushedOkStack;
    }
}
