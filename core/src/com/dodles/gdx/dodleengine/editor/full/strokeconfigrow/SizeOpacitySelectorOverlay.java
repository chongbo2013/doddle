package com.dodles.gdx.dodleengine.editor.full.strokeconfigrow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.brushes.BrushRegistry;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;

import javax.inject.Inject;

/**
 * Overlay for the size/opacity selector.
 */
public class SizeOpacitySelectorOverlay extends AbstractEditorView {

    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    private final OkCancelStackManager okCancelStack;
    private final ToolRegistry toolRegistry;
    private final EditorState editorState;
    private final FrameBufferAtlasManager atlasManager;
    private final BrushRegistry brushRegistry;
    private final FullEditorViewState fullEditorViewState;

    private Table rootTable;
    private Table opacityRow;
    private Table sizeRow;
    private Label opacityLabel;
    private Label sizeLabel;
    private SampleStrokeWidget sampleStrokeWidget;
    private Slider opacitySlider;
    private Slider sizeSlider;


    @Inject
    public SizeOpacitySelectorOverlay(AssetProvider assetProvider, EngineEventManager eventManager, OkCancelStackManager okCancelStackManager, ToolRegistry toolRegistry, EditorState editorState, FrameBufferAtlasManager atlasManager, BrushRegistry brushRegistry, FullEditorViewState fullEditorViewState) {
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.okCancelStack = okCancelStackManager;
        this.toolRegistry = toolRegistry;
        this.editorState = editorState;
        this.atlasManager = atlasManager;
        this.brushRegistry = brushRegistry;
        this.fullEditorViewState = fullEditorViewState;
    }

    @Override
    public final void activate(Skin skin, String newState) {
        if (rootTable == null) {
            rootTable = FullEditorInterface.getThreeRowOverlay(assetProvider, skin);
            rootTable.setFillParent(true);

            Table valueRow = rootTable.findActor("row1");
            sizeRow = rootTable.findActor("row2");
            opacityRow = rootTable.findActor("row3");

            sizeLabel = new Label("", skin);
            sizeLabel.setColor(Color.BLACK);

            opacityLabel = new Label("", skin);
            opacityLabel.setColor(Color.BLACK);

            sizeSlider = createSizeSlider(sizeLabel, skin, eventManager, editorState);
            sizeRow.add(sizeSlider).expandX().fillX();

            opacitySlider = createOpacitySlider(opacityLabel, skin, eventManager, editorState);
            opacityRow.add(opacitySlider).expandX().fillX();

            sampleStrokeWidget = new SampleStrokeWidget(atlasManager, brushRegistry, eventManager, toolRegistry);

            valueRow.add(sizeLabel).expandX();
            valueRow.add(sampleStrokeWidget).expandX();
            valueRow.add(opacityLabel).expandX();

            this.addActor(rootTable);
        }

        final StrokeConfig initialStrokeConfig = editorState.getStrokeConfig().cpy();
        updateSizeSlider(sizeRow, sizeSlider, sizeLabel, assetProvider, editorState);
        updateOpacitySlider(opacityRow, opacitySlider, opacityLabel, assetProvider, editorState);

        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, FullEditorViewState.PREVIOUS_STATE);
            }
        }, new Runnable() {
            @Override
            public void run() {
                editorState.getStrokeConfig().setSize(initialStrokeConfig.getSize());
                editorState.getStrokeConfig().setOpacity(initialStrokeConfig.getOpacity());
                eventManager.fireEvent(EngineEventType.STROKE_CONFIG_CHANGED);
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, FullEditorViewState.PREVIOUS_STATE);
            }
        });
    }

    /**
     * Creates a size slider UI element.
     */
    public static Slider createSizeSlider(final Label sizeLabel, Skin skin, final EngineEventManager eventManager, final EditorState editorState) {
        final Slider sizeSlider = new Slider(1, 50, 1, false, skin);
        sizeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float size = translateSize(sizeSlider.getValue(), false);

                editorState.getStrokeConfig().setSize(Math.round(size));

                if (sizeLabel != null) {
                    sizeLabel.setText(Float.toString(size));
                }

                eventManager.fireEvent(EngineEventType.STROKE_CONFIG_CHANGED);
            }
        });

        return sizeSlider;
    }

    /**
     * Updates the size slider with the current stroke config values.
     */
    public static void updateSizeSlider(Table sizeRow, Slider sizeSlider, Label sizeLabel, AssetProvider assetProvider, EditorState editorState) {
        Drawable sizeBackground = getBackground(assetProvider, TextureAssets.EDITOR_LAYER_TOOL_SIZE_SLIDER, editorState.getStrokeConfig().getColor());
        sizeRow.setBackground(sizeBackground);

        float sliderValue = translateSize(editorState.getStrokeConfig().getSize(), true);
        sizeSlider.setValue(sliderValue);
    }

    /**
     * Creates an opacity slider UI element.
     */
    public static Slider createOpacitySlider(final Label opacityLabel, Skin skin, final EngineEventManager eventManager, final EditorState editorState) {
        final Slider opacitySlider = new Slider(1, 100, 1, false, skin);
        opacitySlider.setValue(100);

        opacitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float opacity = translateOpacity(opacitySlider.getValue(), false);

                editorState.getStrokeConfig().setOpacity(opacity);

                if (opacityLabel != null) {
                    opacityLabel.setText(Float.toString(opacitySlider.getValue()));
                }

                eventManager.fireEvent(EngineEventType.STROKE_CONFIG_CHANGED);
            }
        });

        return opacitySlider;
    }

    /**
     * Updates the opacity slider with the current stroke config values.
     */
    public static void updateOpacitySlider(Table opacityRow, Slider opacitySlider, Label opacityLabel, AssetProvider assetProvider, EditorState editorState) {
        Drawable opacityBackground = getBackground(assetProvider, TextureAssets.EDITOR_SATURATION_SLIDER, editorState.getStrokeConfig().getColor());
        opacityRow.setBackground(opacityBackground);

        opacitySlider.setValue(editorState.getStrokeConfig().getOpacity() * 100);

        if (opacityLabel != null) {
            opacityLabel.setText(Float.toString(translateOpacity(opacitySlider.getValue(), false)));
        }
    }

    private static Drawable getBackground(AssetProvider assetProvider, TextureAssets textureAssets, Color tint) {
        Texture texture = assetProvider.getTexture(textureAssets);
        if (tint == null) {
            return new TextureRegionDrawable(new TextureRegion(texture));
        } else {
            return new TextureRegionDrawable(new TextureRegion(texture)).tint(tint);
        }
    }

    private static float translateSize(float value, boolean dodleToWidget) {
        if (value <= 25) {
            return value;
        } else if (dodleToWidget) {
            return 25 + (value - 25) / 3;
        } else {
            return 25 + (value - 25) * 3;
        }
    }

    private static float translateOpacity(float value, boolean dodleToWidget) {
        if (dodleToWidget) {
            return value * 100f;
        } else {
            return value / 100f;
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
