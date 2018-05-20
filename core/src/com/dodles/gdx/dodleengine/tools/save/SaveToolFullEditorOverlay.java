package com.dodles.gdx.dodleengine.tools.save;

import javax.inject.Inject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.StateManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

/**
 * Overlay for the save tool in the full editor.
 */
@PerDodleEngine
public class SaveToolFullEditorOverlay extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final ToolRegistry toolRegistry;
    private final OkCancelStackManager okCancelStack;
    private final StateManager stateManager;
    
    private Table rootTable;
    
    private static final String SAVE_ACTION = "save_test";
    private static final String LOAD_ACTION = "load_test";
    private static final String TAKE_SCREENSHOT_ACTION = "take_screenshot";
    private static final String LOAD_JSON = "load_json_asset";
    private static final String PREFERENCE_NAME = "dodles.save_tool";
    private static final String DODLES_OBJ_PREF = "dodles_object";

    @Inject
    public SaveToolFullEditorOverlay(AssetProvider assetProvider, ToolRegistry toolRegistry, OkCancelStackManager okCancelStack, StateManager stateManager) {
        this.assetProvider = assetProvider;
        this.toolRegistry = toolRegistry;
        this.okCancelStack = okCancelStack;
        this.stateManager = stateManager;
    }
    
    @Override
    public final void activate(Skin skin, String newState) {
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                .skin(skin)
                .build();

            rootTable = (Table) parser.parseTemplate(assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_EMPTY_OVERLAY)).get(0);
            rootTable.setBackground(skin.getDrawable(toolRegistry.getActiveTool().getActivatedColor()));

            createButton(LOAD_JSON, StringAssets.HELLO_WORLD_JSON, 80, 150, 69, 69);
            createButton(LOAD_JSON, StringAssets.DRIVING_JSON, 150, 150, 69, 69);
            createButton(TAKE_SCREENSHOT_ACTION, null, 290, 150, 69, 69);
            
            this.addActor(rootTable);
        }
        
        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                ((SaveTool) toolRegistry.getTool(SaveTool.TOOL_NAME)).saveDodle();
            }
        }, new Runnable() {
            @Override
            public void run() {
                toolRegistry.setActiveTool(null);
            }
        });
    }

    private void createButton(final String action, final StringAssets json, int xOffset, int yOffset, int width, int height) {
        TextureRegion icon = new TextureRegion(this.assetProvider.getTexture(TextureAssets.EDITOR_TOOL_TOOLBARICONS), xOffset, yOffset, width, height);
        Button button = new Button(new TextureRegionDrawable(icon), new TextureRegionDrawable(icon).tint(Color.TAN));

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (action.equals(LOAD_JSON)) {
                    stateManager.importJson(assetProvider.getString(json));
                    toolRegistry.setActiveTool(null);
                }
                /* else if (action.equals(TAKE_SCREENSHOT_ACTION)) {
                    DefaultScreenGrabber.takeScreenshot();
                }*/
            }
        });

        rootTable.add(button).size(FullEditorInterface.getInterfaceRowSize(), FullEditorInterface.getInterfaceRowSize());
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
}
