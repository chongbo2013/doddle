package com.dodles.gdx.dodleengine.tools.font;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventData;
import com.dodles.gdx.dodleengine.events.EngineEventListener;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import javax.inject.Inject;

/**
 * Full editor row that shows the currently selected font.
 */
@PerDodleEngine
public class SelectFontRowEditorView extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final EditorState editorState;
    private final EngineEventManager eventManager;
    private final OkCancelStackManager okCancelStack;
    
    private FontDemoTable fontDemoTable;
    private Stack rootStack;
    private EngineEventListener strokeConfigListener;
    
    @Inject
    public SelectFontRowEditorView(AssetProvider assetProvider, EditorState editorState, EngineEventManager eventManager, OkCancelStackManager okCancelStack) {
        this.assetProvider = assetProvider;
        this.editorState = editorState;
        this.eventManager = eventManager;
        this.okCancelStack = okCancelStack;
    }

    @Override
    public final void activate(Skin skin, String newState) {
        if (rootStack == null) {
            rootStack = new Stack();
            rootStack.setFillParent(true);
            
            fontDemoTable = new FontDemoTable(assetProvider, editorState.getStrokeConfig().getFont(), editorState.getStrokeConfig().getColor().cpy());
            fontDemoTable.setFillParent(true);
            rootStack.addActor(fontDemoTable);
        
            Button clicker = new Button(skin, "no-tint");
            clicker.setFillParent(true);
            rootStack.addActor(clicker);

            clicker.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, FontTool.SELECT_FONT_STATE);
                }
            });

            this.addActor(rootStack);
        }
        
        strokeConfigListener = new EngineEventListener(EngineEventType.STROKE_CONFIG_CHANGED) {
            @Override
            public void listen(EngineEventData data) {
                fontDemoTable.setFontStyle(editorState.getStrokeConfig());
            }
        };
        
        eventManager.addListener(strokeConfigListener);
        strokeConfigListener.listen(null);
    }

    @Override
    public final void deactivate() {
        eventManager.removeListener(strokeConfigListener);
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootStack;
    }
}
