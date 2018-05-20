package com.dodles.gdx.dodleengine.editor.full.strokeconfigrow;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import javax.inject.Inject;

/**
 * Inline editor row for changing size/opacity.
 */
@PerDodleEngine
public class SizeOpacityInlineEditorRow extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final EditorState editorState;
    private final EngineEventManager eventManager;
    
    private Table rootTable;
    private Table opacityTable;
    private Table sizeTable;
    private Slider opacitySlider;
    private Slider sizeSlider;
    
    @Inject
    SizeOpacityInlineEditorRow(AssetProvider assetProvider, EditorState editorState, EngineEventManager eventManager) {
        this.assetProvider = assetProvider;
        this.editorState = editorState;
        this.eventManager = eventManager;
    }
    
    @Override
    public final void activate(Skin skin, String newState) {
        if (rootTable == null) {
            rootTable = new Table();
            rootTable.setFillParent(true);
            
            sizeTable = new Table();
            sizeSlider = SizeOpacitySelectorOverlay.createSizeSlider(null, skin, eventManager, editorState);
            sizeTable.add(sizeSlider).expandX().fillX();
            rootTable.add(sizeTable).width(Value.percentWidth(0.5f, rootTable));

            opacityTable = new Table();
            opacitySlider = SizeOpacitySelectorOverlay.createOpacitySlider(null, skin, eventManager, editorState);
            opacityTable.add(opacitySlider).expandX().fillX();
            rootTable.add(opacityTable).width(Value.percentWidth(0.5f, rootTable));

            this.addActor(rootTable);
        }
        
        SizeOpacitySelectorOverlay.updateSizeSlider(sizeTable, sizeSlider, null, assetProvider, editorState);
        SizeOpacitySelectorOverlay.updateOpacitySlider(opacityTable, opacitySlider, null, assetProvider, editorState);
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
    
}
