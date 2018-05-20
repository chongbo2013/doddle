package com.dodles.gdx.dodleengine.editor.full.strokeconfigrow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfigKey;
import com.dodles.gdx.dodleengine.tools.Tool;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.draw.DrawTool;

import javax.inject.Inject;

/**
 * Full editor UI Row that displays all the available brushes for selection.
 */
public class FullEditorSelectStrokeConfigRow extends AbstractEditorView {
    private static final String SIZE_OPACITY_STATE = "STROKE_CONFIG.SIZE_OPACITY";
    private static final String COLOR_STATE = "STROKE_CONFIG.COLOR";
    private static final String FILL_STATE = "STROKE_CONFIG.FILL";

    private Table rootTable;
    private AssetProvider assetProvider;
    private EngineEventManager eventManager;
    private ToolRegistry toolRegistry;
    private Button colorButton;
    private Button sizeOpacityButton;
    private Button fillButton;
    private Button lineButton;
    private boolean showFill = false;
    private boolean showLine = false;

    @Inject
    public FullEditorSelectStrokeConfigRow(AssetProvider assetProvider, FullEditorViewState fullViewState, SizeOpacitySelectorOverlay sizeOpacityOverlay, ColorSelectorOverlay colorSelectorOverlay, ColorSelectorOverlay fillColorSelectorOverlay, EngineEventManager eventManager, ToolRegistry toolRegistry) {
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.toolRegistry = toolRegistry;

        fullViewState.registerOverlayView(SIZE_OPACITY_STATE, sizeOpacityOverlay);

        colorSelectorOverlay.setMode(ColorSelectorMode.GLOBAL);
        colorSelectorOverlay.setProperty(StrokeConfigKey.COLOR);
        fullViewState.registerOverlayView(COLOR_STATE, colorSelectorOverlay);

        fillColorSelectorOverlay.setMode(ColorSelectorMode.GLOBAL);
        fillColorSelectorOverlay.setProperty(StrokeConfigKey.FILL);
        fullViewState.registerOverlayView(FILL_STATE, fillColorSelectorOverlay);
    }

    @Override
    public final void activate(final Skin skin, String newState) {
        if (rootTable == null) {
            rootTable = new Table();
            rootTable.setFillParent(true);

            colorButton = new TextButton("Color", skin);
            colorButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, COLOR_STATE);
                }
            });

            sizeOpacityButton = new TextButton("Size / Opacity", skin);
            sizeOpacityButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SIZE_OPACITY_STATE);
                }
            });

            fillButton = new TextButton("Fill", skin);
            fillButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, FILL_STATE);
                }
            });
            boolean lineFlag = false;
            final Tool temp = toolRegistry.getTool(DrawTool.TOOL_NAME);
            if (temp instanceof DrawTool) {
                lineFlag = ((DrawTool) temp).isLineFlag();
            }
            lineButton = new TextButton("Line", skin);
            Color tempColor = Color.RED;
            if (lineFlag) {
                tempColor = Color.GREEN;
            }
            lineButton.setColor(tempColor);

            lineButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, LINE_STATE);
                    boolean lineFlag = false;
                    if (temp instanceof DrawTool) {
                        ((DrawTool) temp).toggleLine();
                        lineFlag = ((DrawTool) temp).isLineFlag();
                    }
                    Color tempColor = Color.RED;
                    if (lineFlag) {
                        tempColor = Color.GREEN;
                    }
                    lineButton.setColor(tempColor);
                    lineButton.invalidate();
                }
            });

            this.addActor(rootTable);
        }

        rootTable.clear();
        rootTable.add(colorButton).expandX();
        rootTable.add(sizeOpacityButton).expandX();
        if (showLine) {
            rootTable.add(lineButton).expandX();
        }

        if (showFill) {
            rootTable.add(fillButton).expandX();
        }
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }

    /**
     * Sets a value indicating whether to show the fill button.
     */
    public final void setShowFill(boolean show) {
        showFill = show;
    }

    /**
     * Sets a value indicating whether to show the line button.
     */
    public final void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }
}
