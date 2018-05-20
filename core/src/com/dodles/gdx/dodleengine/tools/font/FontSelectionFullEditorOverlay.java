package com.dodles.gdx.dodleengine.tools.font;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.BitmapFontAssets;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;
import com.kotcrab.vis.ui.widget.ListView;

import javax.inject.Inject;

/**
 * Overlay for the font tool in the full editor.
 */
@PerDodleEngine
public class FontSelectionFullEditorOverlay extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final EditorState editorState;
    private final EngineEventManager eventManager;
    private final ToolRegistry toolRegistry;
    private final OkCancelStackManager okCancelStack;
    
    private Table rootTable;
    private FontListAdapter fontListAdapter;
    
    @Inject
    public FontSelectionFullEditorOverlay(AssetProvider assetProvider, EditorState editorState, EngineEventManager eventManager, ToolRegistry toolRegistry, OkCancelStackManager okCancelStack) {
        this.assetProvider = assetProvider;
        this.editorState = editorState;
        this.eventManager = eventManager;
        this.toolRegistry = toolRegistry;
        this.okCancelStack = okCancelStack;
    }
    
    @Override
    public final void activate(Skin skin, String newState) {
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                .skin(skin)
                .build();

            rootTable = (Table) parser.parseTemplate(assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_EMPTY_OVERLAY)).get(0);
            rootTable.setBackground(skin.getDrawable(toolRegistry.getActiveTool().getActivatedColor()));
            fontListAdapter = new FontListAdapter(assetProvider, editorState);
            ListView fontList = new ListView(fontListAdapter);
            rootTable.add(fontList.getMainTable()).expandX().fillX();
            
            this.addActor(rootTable);
        }
        
        fontListAdapter.getSelectionManager().deselectAll();
        fontListAdapter.getSelectionManager().select(editorState.getStrokeConfig().getFont());
        
        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                Array<BitmapFontAssets> selections = fontListAdapter.getSelection();
                
                if (selections.size > 0) {
                    editorState.getStrokeConfig().setFont(selections.get(0));
                }
                
                eventManager.fireEvent(EngineEventType.STROKE_CONFIG_CHANGED);
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, FontTool.TOOL_NAME);
            }
        }, new Runnable() {
            @Override
            public void run() {
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, FontTool.TOOL_NAME);
            }
        });
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
}
