package com.dodles.gdx.dodleengine.tools.crop;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * An empty overlay for the crop tool.
 */

public class CropToolFullEditorOverlay extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final ToolRegistry toolRegistry;

    private Table rootTable;

    @Inject
    public CropToolFullEditorOverlay(AssetProvider assetProvider, ToolRegistry toolRegistry) {
        this.assetProvider = assetProvider;
        this.toolRegistry = toolRegistry;
    }

    @Override
    public final void activate(Skin skin, String newState) {
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .build();

            rootTable = (Table) parser.parseTemplate(assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_EMPTY_OVERLAY)).get(0);
            rootTable.setBackground(skin.getDrawable(toolRegistry.getActiveTool().getActivatedColor()));

            this.addActor(rootTable);
        }
    }

    @Override
    public final void deactivate() {

    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
}
