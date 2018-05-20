package com.dodles.gdx.dodleengine.tools.layerTool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.tools.ClickableTool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import java.util.List;

import javax.inject.Inject;

/**
 * Overlay for the play tool in the full editor.
 */
@PerDodleEngine
public class LayerToolFullEditorOverlay extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final LayerSubToolRegistry layerSubToolRegistry;
    private final ToolRegistry toolRegistry;
    private final OkCancelStackManager okCancelStack;
    
    private Table rootTable;
    private Skin skin;
    private String[] labels = new String[6];

    @Inject
    public LayerToolFullEditorOverlay(AssetProvider assetProvider, LayerSubToolRegistry layerSubToolRegistry, ToolRegistry toolRegistry, OkCancelStackManager okCancelStack) {
        this.assetProvider = assetProvider;
        this.layerSubToolRegistry = layerSubToolRegistry;
        this.toolRegistry = toolRegistry;
        this.okCancelStack = okCancelStack;

        labels[0] = "";
        labels[1] = "OBJECT";
        labels[2] = "LAYER";
        labels[3] = "MODIFY";
        labels[4] = "";
        labels[5] = "DODLES";
    }
    
    @Override
    public final void activate(Skin pSkin, String newState) {
        this.skin = pSkin;
        if (rootTable == null) {
            rootTable = FullEditorInterface.getScrollableOverlay(assetProvider, skin, false);

            buildLayerTools();
            
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

    private void buildLayerTools() {
        int row = 1;
        List<LayerSubTool> subTools;
        Table scrollContent = (Table) rootTable.findActor("scrollContent");
        
        // CHECKSTYLE.OFF: InnerAssignment - much clearer logic inline
        while ((subTools = layerSubToolRegistry.getTools(row)).size() > 0) {
            Table rowTable = new Table();
            rowTable.add(new Label(labels[row], skin, "small")).top().left().colspan(0);

            scrollContent.add(rowTable).expandX().fillX().row();
            
            if (subTools.size() < 5) {
                rowTable.padLeft(Value.percentWidth(0.1f, rowTable));
            } else {
                rowTable.padLeft(0);
            }
            
            for (LayerSubTool layerSubTool : subTools) {
                configureTool(rowTable, layerSubTool);
            }
            
            if (subTools.size() < 5) {
                rowTable.padRight(Value.percentWidth(0.1f + (4 - subTools.size()) * 0.2f, rowTable));
            } else {
                rowTable.padRight(0);
            }
            
            row++;
        }
        // CHECKSTYLE.ON: InnerAssignment
    }

    private void configureTool(Table row, final LayerSubTool layerSubTool) {
        TextureRegion icon = layerSubTool.getIcon();
        if (icon != null) {
            Button button = new Button(new TextureRegionDrawable(icon), new TextureRegionDrawable(icon).tint(Color.TAN));
            String name = layerSubTool.getName().replace("_", " ");

            ClickListener click = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    layerSubToolRegistry.setActiveTool(layerSubTool.getName());
                }
            };

            if (layerSubTool instanceof ClickableTool) {
                click = ((ClickableTool) layerSubTool).onClick();
            }

            float padding = FullEditorInterface.getInterfaceRowSize() / 8f;
            float iconSize = FullEditorInterface.getInterfaceRowSize() - padding * 2;

            Table cell = new Table(skin);
            cell.addListener(click);
            cell.add(button).size(iconSize, iconSize).expand().fill().top().center().row();
            cell.add(new Label(name.substring(name.lastIndexOf('.') + 1), skin, "small")).bottom().center();

            row.add(cell).padTop(padding).expand().uniform().center();
        }
    }
}
