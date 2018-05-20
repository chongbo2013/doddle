package com.dodles.gdx.dodleengine.editor.full;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.dodles.gdx.dodleengine.DodleEngineConfig;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SkinAssets;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.editor.EditorInterface;
import com.dodles.gdx.dodleengine.editor.EditorStateManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.dodleoverlay.FullEditorDodleOverlayRegistry;
import com.dodles.gdx.dodleengine.editor.full.widgets.HeaderBar;
import com.dodles.gdx.dodleengine.editor.utils.Dimensions;
import com.dodles.gdx.dodleengine.editor.utils.DodlesUIUtil;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.tools.ClickableTool;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.redo.RedoTool;
import com.dodles.gdx.dodleengine.tools.undo.UndoTool;
import com.dodles.gdx.dodleengine.util.LmlUtility;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Implements the full featured, full screen dodle editor interface.
 */
@PerDodleEngine
public class FullEditorInterface implements EditorInterface {

    // region Properties & Variables

    // Constants
    public static final int MAX_COLUMNS = 5;

    // Sub-system references
    private final AssetProvider assetProvider;
    private final DodleEngineConfig engineConfig;
    private final EventBus eventBus;
    private final EngineEventManager eventManager;
    private final FullEditorDodleOverlayRegistry dodleOverlayRegistry;
    private final CommandManager commandManager;
    private final FullEditorViewState viewState;
    private final OkCancelStackManager okCancelStack;
    private final ToolRegistry toolRegistry;
    private final EditorStateManager editorStateManager;

    // Internal UI references
    private Skin skin;
    private Table rootTable;
    private EngineEventListener editorStateChangedListener;
    private EventSubscriber toolChangedListener;
    private EventSubscriber updateToolsListener;
    private EngineEventListener rasterizerOverloadedListener;
    private InputListener scrollPaneListener;
    private Table toolRows;
    private Table busyOverlay;
    private Stage stage;
    private ScrollPane scrollPane;
    private Button[] redoButton = new Button[3];
    private Button[] undoButton = new Button[3];
    private Cell cellPosA = null;
    private Cell cellPosB = null;
    private Array<Integer> toolswaps = new Array<Integer>();
    private Vector2 gridOrig;
    private Vector2 gridDist;
    private int activeRowNum;
    private static boolean isToolFinished = false;

    // endregion

    // region Constructor
    @Inject
    public FullEditorInterface(
            AssetProvider assetProvider,
            DodleEngineConfig engineConfig,
            FullEditorDodleOverlayRegistry dodleOverlayRegistry,
            EventBus eventBus,
            EngineEventManager eventManager,
            FullEditorViewState viewState,
            OkCancelStackManager okCancelStack,
            ToolRegistry toolRegistry,
            CommandManager commandManager,
            EditorStateManager editorStateManager
    ) {
        this.assetProvider = assetProvider;
        this.dodleOverlayRegistry = dodleOverlayRegistry;
        this.engineConfig = engineConfig;
        this.eventBus = eventBus;
        this.eventManager = eventManager;
        this.commandManager = commandManager;
        this.viewState = viewState;
        this.okCancelStack = okCancelStack;
        this.toolRegistry = toolRegistry;
        this.editorStateManager = editorStateManager;
    }
    // endregion

    // region Property Accessors

    /**
     * Returns the current value of the isToolFinished variable.
     */
    public static boolean getToolFinished() {
        return isToolFinished;
    }

    /**
     * Setter for the isToolFinished variable.
     */
    public static void setToolFinished(boolean toolFinished) {
        isToolFinished = toolFinished;
    }

    // endregion

    /**
     * Returns the height of a "row" in the full editor based on the resolution of the device.
     */
    public static int getInterfaceRowSize() {
        return (int) (0.07f * Gdx.graphics.getHeight());
    }

    /**
     * Returns the height of the header row.
     */
    public final int getHeaderRowSize() {
        return (int) HeaderBar.getMaxHeaderHeight();
    }

    /**
     * Returns the offset for the tray "tab".
     */
    public static int getTrayTabOffset() {
        return (int) (0.06f * Gdx.graphics.getHeight());
    }

    /**
     * Returns a preconfigured instance of the empty three row overlay.
     */
    public static Table getThreeRowOverlay(AssetProvider assetProvider, Skin skin) {
        LmlParser parser = VisLml.parser()
                .skin(skin)
                .argument("oneRowSize", getInterfaceRowSize())
                .build();

        return (Table) parser.parseTemplate(
                assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_THREE_ROW_OVERLAY)).get(0);
    }

    /**
     * Returns a preconfigured instance of the empty scrollable overlay.
     */
    public static Table getScrollableOverlay(AssetProvider assetProvider, Skin skin, boolean showScrollBar) {
        String scrollPaneStyle = "no-scroll-bars";

        if (showScrollBar) {
            scrollPaneStyle = "default";
        }

        LmlParser parser = VisLml.parser()
                .skin(skin)
                .argument("scrollPaneStyle", scrollPaneStyle)
                .argument("fadeScrollBars", scrollPaneStyle == "no-scroll-bars")
                .build();

        Table result = (Table) parser.parseTemplate(assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_SCROLLPANE_OVERLAY)).get(0);

        ((ScrollPane) result.findActor("scrollPane")).setScrollingDisabled(true, false);

        return result;
    }

    @Override
    public final void activate(Stage stageParam) {
        toolswaps.clear();
        toolswaps.add(0);
        toolswaps.add(1);
        toolswaps.add(2);
        activeRowNum = 0;

        stage = stageParam;

        if (skin == null) {
            skin = assetProvider.getSkin(SkinAssets.EDITOR_UI_SKIN);

            // Initialize LML Builder for Editor UI
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .argument("oneRowSize", getInterfaceRowSize())
                    .argument("twoRowSize", getInterfaceRowSize() * 2)
                    .argument("threeRowSize", getInterfaceRowSize() * 3)
                    .argument("trayTabOffset", getTrayTabOffset())
                    .argument("headerRowSize", getHeaderRowSize())
                    .tag(new LmlTagProvider() {
                        @Override
                        public LmlTag create(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
                            return new AbstractActorLmlTag(parser, parentTag, rawTagData) {
                                @Override
                                protected Actor getNewInstanceOfActor(LmlActorBuilder builder) {
                                    return new PassthroughScrollPane(null);
                                }

                                @Override
                                protected void handlePlainTextLine(String plainTextLine) {
                                }

                                @Override
                                protected void handleValidChild(LmlTag childTag) {
                                    ((ScrollPane) getActor()).setWidget(childTag.getActor());
                                }
                            };
                        }
                    }, "passthroughScrollPane")
                    .build();

            // Build Editor UI, grab references to key objects
            String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR);
            rootTable = (Table) parser.parseTemplate(template).get(0);
            rootTable.setFillParent(true);
            toolRows = rootTable.findActor("toolRow");

            // Header
            initHeader();

            // Allow buttons to be clicked that are under the scrollpane!
            scrollPane = rootTable.findActor("scrollPane");
            scrollPane.setOverscroll(false, false);

            // Style tool tray tab
            Table trayTable = rootTable.findActor("trayIconRow");
            Image trayBottomBorderImage = new Image(skin, "tray_bottom_border");
            trayTable.add(trayBottomBorderImage).expand().fillX().bottom().left();
            ImageButton trayTabButton = new ImageButton(skin, "tray_tab_button");
            Dimensions trayTabButtonDimensions = DodlesUIUtil.computeDimensionsWithAspectRatio(
                    trayTabButton.getWidth(), trayTabButton.getHeight(),
                    Gdx.graphics.getWidth() * 0.2f, getTrayTabOffset() / 2);
            trayTable.add(trayTabButton)
                    .width(trayTabButtonDimensions.getWidth())
                    .height(trayTabButtonDimensions.getHeight())
                    .align(Align.bottom);
            Image trayBottomBorderImage2 = new Image(skin, "tray_bottom_border");
            trayTable.add(trayBottomBorderImage2).expand().fillX().bottom().right();

            viewState.init(rootTable, skin, scrollPane);
            dodleOverlayRegistry.initializeOverlays((Stack) rootTable.findActor("canvasOverlayStack"), skin);
            busyOverlay = rootTable.findActor("busyOverlay");
        }

        scrollPaneListener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (stage.getScrollFocus() == scrollPane) {
                    // Remove mouse scroll from the scrollpane because it hijacks the mouse wheel...
                    stage.setScrollFocus(null);
                }
                return false;
            }
        };

        editorStateChangedListener = new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
            @Override
            public void listen(EngineEventData data) {
                viewState.changeState(data.getFirstStringParam());
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

        updateToolsListener = new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic topic, EventType eventType, EventData data) {
                if (EventType.COMMAND_STACK_CHANGED.equals(eventType)) {
                    updateToolbar();
                }
            }
        };

        rasterizerOverloadedListener = new EngineEventListener(EngineEventType.RASTERIZER_OVERLOADED) {
            @Override
            public void listen(EngineEventData data) {
                busyOverlay.setVisible(data.getFirstStringParam() != null);
            }
        };

        eventManager.addListener(new EngineEventListener(EngineEventType.ENGINE_POST_DRAW) {
            @Override
            public void listen(EngineEventData data) {
                updateToolbar();
                eventManager.removeListener(this);
            }
        });

        stage.addListener(scrollPaneListener);
        eventManager.addListener(editorStateChangedListener);
        eventBus.addSubscriber(toolChangedListener);
        eventBus.addSubscriber(updateToolsListener);
        eventManager.addListener(rasterizerOverloadedListener);

        updateRows();

        stage.addActor(rootTable);
    }

    @Override
    public final void deactivate() {
        stage.removeListener(scrollPaneListener);
        eventManager.removeListener(editorStateChangedListener);
        eventBus.removeSubscriber(toolChangedListener);
        eventBus.removeSubscriber(updateToolsListener);
        eventManager.removeListener(rasterizerOverloadedListener);
        rootTable.remove();
    }

    @Override
    public final EditorInterface.Padding getInterfacePadding() {
        int top = 0;

        if (engineConfig.hasOption(DodleEngineConfig.Options.FULL_EDITOR_DODLES_HEADER)
                || engineConfig.hasOption(DodleEngineConfig.Options.IOS_EMPTY_HEADER)) {
            top = getHeaderRowSize();
        }
        return new EditorInterface.Padding(0, 0, top, getInterfaceRowSize() * 3);
    }

    private void initHeader() {
        Container<Table> headerRow = rootTable.findActor("headerRow");
        HeaderBar headerBar = new HeaderBar(
                okCancelStack,
                eventBus,
                skin,
                engineConfig.hasOption(DodleEngineConfig.Options.FULL_EDITOR_DODLES_HEADER),
                assetProvider);
        headerRow.setActor(headerBar);

        if (!headerBar.getRenderFullHeader() && !engineConfig.hasOption(DodleEngineConfig.Options.IOS_EMPTY_HEADER)) {
            headerRow.setVisible(false);
            Cell cell = LmlUtility.getCell(headerRow);
            cell.height(0f);
        }
    }

    private void updateRows() {
        toolRows.clear();
        gridDist = null;
        gridOrig = null;
        updateRow(1, true);
        updateRow(2, true);
        updateRow(3, false);
    }

    private void updateRow(int rowNum, boolean includeUndo) {
        List<Tool> tools = toolRegistry.getTools(rowNum, includeUndo);


        toolRows.padLeft(0);
        toolRows.padRight(0);

        for (int i = 0; i < tools.size(); i++) {
            Tool tool = tools.get(i);
            if (i == 0 && rowNum == 1) {
                cellPosA = configureTool(rowNum - 1, tool, includeUndo);
            } else if (i == 1 && rowNum == 2) {
                cellPosB = configureTool(rowNum - 1, tool, includeUndo);
            } else {
                configureTool(rowNum - 1, tool, includeUndo);
            }
        }
        toolRows.row();

    }

    private Cell configureTool(final int rowNum, final Tool tool, boolean includeUndo) {

        final Button button = new Button(skin, tool.getButtonStyleName());

        if (tool instanceof UndoTool) {
            undoButton[rowNum] = button;
        } else if (tool instanceof RedoTool) {
            redoButton[rowNum] = button;
        }

        if (tool instanceof ClickableTool) {
            button.addListener(((ClickableTool) tool).onClick());

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Clickable tools should never be "checked"
                    button.setChecked(false);
                }
            });
        } else {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    editorStateManager.onToolChange(tool.getName());
                    activeRowNum = rowNum;
                    updateToolbar();
                }
            });
        }

        // set the checked state.
        Tool activeTool = toolRegistry.getActiveTool();
        if (tool == activeTool) {
            button.setChecked(true);
        } else {
            button.setChecked(false);
        }

        // reset the checked state of a button when state changes (buttons are always recreated
        // this listener handles that scenario)
        eventManager.addListener(new EngineEventListener(EngineEventType.CHANGE_EDITOR_STATE) {
            @Override
            public void listen(EngineEventData data) {
                String state = data.getFirstStringParam();
                String toolName = toolRegistry.getToolNameFromState(state);

                if (tool.getName().equals(toolName)) {
                    button.setChecked(true);
                } else {
                    button.setChecked(false);
                }
            }
        });

        // catch when the user trashes their dodle...
        eventBus.addSubscriber(new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic topic, EventType eventType, EventData data) {
                if (EventType.HOST_ENGINE_TRASH_STATE.equals(eventType)) {
                    button.setChecked(false);
                }
            }
        });

        float padding = getInterfaceRowSize() / MAX_COLUMNS;
        float iconSize = getInterfaceRowSize() - padding * 2;

        Cell c = toolRows.add(button);
        c.size(iconSize, iconSize).pad(padding).expandX();
        return c;
    }

    private void updateToolbar() {

        if (gridDist == null && cellPosA != null && cellPosB != null) {
            gridDist = new Vector2(cellPosB.getActorX() - cellPosA.getActorX(), cellPosB.getActorY() - cellPosA.getActorY());
            gridOrig = new Vector2(cellPosA.getActorX(), cellPosA.getActorY());
        }

        Array<Cell> cells = toolRows.getCells();
        toolswaps.swap(0, toolswaps.indexOf(activeRowNum, false));
        if (toolswaps.get(1) > toolswaps.get(2)) {
            toolswaps.swap(1, 2);
        }
        if (toolswaps.get(1) == 0 && toolswaps.get(2) == 1) {
            toolswaps.swap(1, 2);
        }


        for (int g : toolswaps) {
            for (int i = 0; i < MAX_COLUMNS; i++) {
                int pos1 = i + g * toolRows.getColumns();
                Cell cell1 = cells.get(pos1);
                float tx2 = gridOrig.x + gridDist.x * i;
                if (toolswaps.get(1) == g && g < 2) {
                    tx2 += gridDist.x / 2;
                }
                float ty2 = gridOrig.y + gridDist.y * toolswaps.indexOf(g, false);
                final Actor a1 = cell1.getActor();
                if (g == activeRowNum) {
                    a1.setZIndex(1);
                } else {
                    a1.setZIndex(0);
                }
                a1.addAction(sequence(moveTo(tx2, ty2, .3f), run(new Runnable() {
                    public void run() {

                    }
                })));
            }
        }
        if (activeRowNum == 2) {
            undoButton[0].addAction(sequence(moveTo(gridOrig.x + (MAX_COLUMNS - 2) * gridDist.x, gridOrig.y + gridDist.y * 2, .3f, Interpolation.bounceOut), run(new Runnable() {
                public void run() {
                    undoButton[0].setVisible(commandManager.canUndo());
                }
            })));
            redoButton[0].addAction(sequence(moveTo(gridOrig.x + (MAX_COLUMNS - 1) * gridDist.x, gridOrig.y + gridDist.y * 2, .3f, Interpolation.bounceOut), run(new Runnable() {
                public void run() {
                    redoButton[0].setVisible(commandManager.canRedo());
                }
            })));
            undoButton[1].addAction(sequence(moveTo(gridOrig.x + (MAX_COLUMNS - 2) * gridDist.x, gridOrig.y + gridDist.y * 1, .3f, Interpolation.bounceOut), run(new Runnable() {
                public void run() {
                    undoButton[1].setVisible(false);
                }
            })));
            redoButton[1].addAction(sequence(moveTo(gridOrig.x + (MAX_COLUMNS - 1) * gridDist.x, gridOrig.y + gridDist.y * 1, .3f, Interpolation.bounceOut), run(new Runnable() {
                public void run() {
                    redoButton[1].setVisible(false);
                }
            })));

        } else {
            boolean canRedo = commandManager.canRedo();
            boolean canUndo = commandManager.canUndo();
            boolean setRedoVisFuture = true;
            boolean setUndoVisFuture = true;
            int redoSpot = -1;
            int undoSpot = -1;
            if (canUndo && !canRedo) {
                undoSpot = MAX_COLUMNS - 2;
                redoSpot = MAX_COLUMNS - 2;
                setRedoVisFuture = false;
                setUndoVisFuture = true;
            } else if (canUndo && canRedo) {
                undoSpot = MAX_COLUMNS - 2;
                redoSpot = MAX_COLUMNS - 1;
                setRedoVisFuture = true;
                setUndoVisFuture = true;
            } else if (!canUndo && canRedo) {
                undoSpot = MAX_COLUMNS - 1;
                redoSpot = MAX_COLUMNS - 1;
                setRedoVisFuture = true;
                setUndoVisFuture = false;
            } else if (!canUndo && !canRedo) {
                undoSpot = MAX_COLUMNS;
                redoSpot = MAX_COLUMNS;
                setRedoVisFuture = false;
                setUndoVisFuture = false;
            }
            final boolean finalSetUndoVisFuture = setUndoVisFuture;
            undoButton[activeRowNum].addAction(sequence(moveTo(gridOrig.x + (undoSpot) * gridDist.x, gridOrig.y, .3f, Interpolation.bounceOut), run(new Runnable() {
                public void run() {
                    undoButton[activeRowNum].setVisible(finalSetUndoVisFuture);
                }
            })));
            undoButton[1 - activeRowNum].addAction(sequence(moveTo(gridOrig.x + (MAX_COLUMNS) * gridDist.x, gridOrig.y, .3f, Interpolation.bounceOut), run(new Runnable() {
                public void run() {
                    undoButton[activeRowNum].setVisible(finalSetUndoVisFuture);
                }
            })));

            final boolean finalSetRedoVisFuture = setRedoVisFuture;
            redoButton[activeRowNum].addAction(sequence(moveTo(gridOrig.x + (redoSpot) * gridDist.x, gridOrig.y, .3f, Interpolation.bounceOut), run(new Runnable() {
                public void run() {
                    redoButton[activeRowNum].setVisible(finalSetRedoVisFuture);
                }
            })));
            redoButton[1 - activeRowNum].addAction(sequence(moveTo(gridOrig.x + (MAX_COLUMNS) * gridDist.x, gridOrig.y, .3f, Interpolation.bounceOut), run(new Runnable() {
                public void run() {
                    redoButton[activeRowNum].setVisible(finalSetRedoVisFuture);
                }
            })));
            undoButton[activeRowNum].setVisible(canUndo);
            redoButton[activeRowNum].setVisible(canRedo);
            undoButton[1 - activeRowNum].setVisible(false);
            redoButton[1 - activeRowNum].setVisible(false);
        }
        scrollPane.scrollTo(0, (getInterfaceRowSize() * 5) + (getTrayTabOffset() * 1.5f), 0, 0);

    }

    /**
     * handle resize events as necessary.
     */
    @Override
    public final void resize(int width, int height) {
        //updateRows();
    }

    @Override
    public final void reset() {
        viewState.reset();
    }
}
