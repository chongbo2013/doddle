package com.dodles.gdx.dodleengine.editor.full;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.AbstractEditorViewState;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * Manages view state within the full editor.
 */
@PerDodleEngine
public class FullEditorViewState extends AbstractEditorViewState {
    public static final String PREVIOUS_STATE = "~previous~";

    public static final String TOOLBAR_TOP_ACTIVATED_COLOR = "toolbar-top-row";
    public static final String TOOLBAR_MIDDLE_ACTIVATED_COLOR = "toolbar-middle-row";
    public static final String TOOLBAR_BOTTOM_ACTIVATED_COLOR = "toolbar-bottom-row";

    private final HashMap<String, AbstractEditorView> row1States = new HashMap<String, AbstractEditorView>();
    private final HashMap<String, AbstractEditorView> row2States = new HashMap<String, AbstractEditorView>();
    private final HashMap<String, AbstractEditorView> row3States = new HashMap<String, AbstractEditorView>();
    private final HashMap<String, AbstractEditorView> overlayStates = new HashMap<String, AbstractEditorView>();
    private final EngineEventManager eventManager;
    private final OkCancelStackManager okCancelStack;
    private final ToolRegistry toolRegistry;

    private String curState = "";
    private String prevState;
    private WidgetGroup rootTable;
    private Skin skin;
    private ScrollPane toolBoxScrollPane;

    @Inject
    public FullEditorViewState(EngineEventManager eventManager, OkCancelStackManager okCancelStack, ToolRegistry toolRegistry) {
        this.eventManager = eventManager;
        this.okCancelStack = okCancelStack;
        this.toolRegistry = toolRegistry;
    }

    /**
     * Returns the current state of the editor.
     */
    public final String getState() {
        return curState;
    }

    /**
     * Needs to be called when the full editor is initialized.
     */
    public final void init(WidgetGroup pRootTable, Skin pSkin, ScrollPane pToolBoxScrollPane) {
        rootTable = pRootTable;
        skin = pSkin;
        toolBoxScrollPane = pToolBoxScrollPane;
        // Gobble up mouse events if they get to the container...
        for (String row : new String[]{"row1", "row2"}) {
            rootTable.findActor(row).addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
            });
        }
    }

    /**
     * Returns the view for row 1 for the given state (if any).
     */
    public final AbstractEditorView getRow1View(String state) {
        return getView(row1States, state);
    }

    /**
     * Returns the view for row 2 for the given state (if any).
     */
    public final AbstractEditorView getRow2View(String state) {
        return getView(row2States, state);
    }

    /**
     * Returns the view for row 2 for the given state (if any).
     */
    public final AbstractEditorView getRow3View(String state) {
        return getView(row3States, state);
    }

    /**
     * Returns the overlay view for the given state (if any).
     */
    public final AbstractEditorView getOverlayView(String state) {
        return getView(overlayStates, state);
    }

    /**
     * Registers a view for row 1 with the given state.
     */
    public final void registerRow1View(String state, AbstractEditorView view) {
        row1States.put(state, view);
    }

    /**
     * Registers a view for row 2 with the given state.
     */
    public final void registerRow2View(String state, AbstractEditorView view) {
        row2States.put(state, view);
    }

    /**
     * Registers a view for row 3 with the given state.
     */
    public final void registerRow3View(String state, AbstractEditorView view) {
        row3States.put(state, view);
    }

    /**
     * Registers a view for the overlay with the given state.
     */
    public final void registerOverlayView(String state, AbstractEditorView view) {
        overlayStates.put(state, view);
    }

    /**
     * Resets the full editor view state.
     */
    public final void reset() {
        curState = "";
        prevState = null;
    }

    /**
     * Changes to the new state.
     */
    public final void changeState(String newState) {
        if (newState == null) {
            newState = "";
        }
        if (newState.equals(PREVIOUS_STATE)) {
            newState = prevState;
        }
        prevState = curState;
        curState = newState;
        
        injectDynamicView(curState, getRow1View(curState), "row1", rootTable, skin);
        injectDynamicView(curState, getRow2View(curState), "row2", rootTable, skin);
        injectDynamicView(curState, getOverlayView(curState), "overlay", rootTable, skin);

        AbstractEditorView row3View = getRow3View(curState);
        injectDynamicView(curState, row3View, "row3", rootTable, skin);
        toolBoxScrollPane.setVisible(row3View == null);
    }

    /**
     * Adds the current state of the editor to the state stack, and adds an ok button to return.
     */
    public final void pushState(final ObjectManager objectManager) {
        final FullEditorStateFrame frame = new FullEditorStateFrame(curState, objectManager.getActiveLayer());

        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                objectManager.setActiveLayer(frame.getActiveLayer());
                objectManager.setNewObjectGroup(null);
                objectManager.clearSelectedActors();
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, frame.getEditorState());
            }
        });

        okCancelStack.nextLayer();
    }
}
