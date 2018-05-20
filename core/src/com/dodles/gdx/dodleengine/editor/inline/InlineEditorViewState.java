package com.dodles.gdx.dodleengine.editor.inline;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.brushes.SelectBrushRowEditorView;
import com.dodles.gdx.dodleengine.editor.full.strokeconfigrow.ColorPickerInlineEditorRow;
import com.dodles.gdx.dodleengine.editor.full.strokeconfigrow.SizeOpacityInlineEditorRow;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.AbstractEditorViewState;
import com.dodles.gdx.dodleengine.tools.draw.DrawTool;
import com.dodles.gdx.dodleengine.tools.font.FontSelectionFullEditorOverlay;
import com.dodles.gdx.dodleengine.tools.font.FontTool;
import com.dodles.gdx.dodleengine.tools.font.SelectFontRowEditorView;
import java.util.HashMap;
import javax.inject.Inject;

/**
 * Manages view state within the inline editor.
 */
@PerDodleEngine
public class InlineEditorViewState extends AbstractEditorViewState {
    private HashMap<String, AbstractEditorView> row1States = new HashMap<String, AbstractEditorView>();
    private HashMap<String, AbstractEditorView> row2States = new HashMap<String, AbstractEditorView>();
    private HashMap<String, AbstractEditorView> overlayStates = new HashMap<String, AbstractEditorView>();
    
    @Inject
    public InlineEditorViewState(
        SelectBrushRowEditorView brushRow,
        SelectFontRowEditorView fontRow,
        ColorPickerInlineEditorRow colorPicker,
        FontSelectionFullEditorOverlay fontSelectionOverlay,
        InlineToolConfigRow toolConfigRow,
        SizeOpacityInlineEditorRow sizeOpacityPicker
    ) {
        row1States.put("", toolConfigRow);
        
        row2States.put(DrawTool.TOOL_NAME, brushRow);
        row2States.put(DrawTool.TOOL_NAME + ".color", colorPicker);
        row2States.put(DrawTool.TOOL_NAME + ".stroke", sizeOpacityPicker);
        
        row2States.put(FontTool.TOOL_NAME, fontRow);
        row2States.put(FontTool.TOOL_NAME + ".color", colorPicker);
        row2States.put(FontTool.TOOL_NAME + ".stroke", sizeOpacityPicker);
        overlayStates.put(FontTool.SELECT_FONT_STATE, fontSelectionOverlay);
    }

    /**
     * Changes to the new state.
     */
    public final void changeState(String newState, WidgetGroup rootTable, Skin skin) {
        injectDynamicView(newState, getView(row1States, newState), "row1", rootTable, skin);
        injectDynamicView(newState, getView(row2States, newState), "row2", rootTable, skin);
        injectDynamicView(newState, getView(overlayStates, newState), "overlay", rootTable, skin);
    }
}
