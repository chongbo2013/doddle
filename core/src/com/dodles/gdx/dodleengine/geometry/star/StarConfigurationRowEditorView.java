package com.dodles.gdx.dodleengine.geometry.star;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.geometry.GeometryRenderState;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * Star configuration row.
 */
@PerDodleEngine
public class StarConfigurationRowEditorView extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final GeometryRegistry geometryRegistry;
    private final GeometryTool geometryTool;

    private Table rootTable;
    private Label sizeLabel;
    private Slider sizeSlider;
    private Label numPointsLabel;
    private Slider numPointsSlider;
    private Label depthLabel;
    private Slider depthSlider;
    private Label radiusLabel;
    private Slider radiusSlider;

    @Inject
    public StarConfigurationRowEditorView(AssetProvider assetProvider, GeometryRegistry geometryRegistry, GeometryTool geometryTool) {
        this.assetProvider = assetProvider;
        this.geometryRegistry = geometryRegistry;
        this.geometryTool = geometryTool;
    }

    @Override
    public final void activate(Skin skin, String newState) {
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .build();

            String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_FOUR_SLIDER_ROW);
            rootTable = (Table) parser.parseTemplate(template).get(0);
            rootTable.setFillParent(true);

            sizeLabel = rootTable.findActor("label1");
            sizeLabel.getStyle().font.setFixedWidthGlyphs(" 0123456789");
            sizeSlider = rootTable.findActor("slider1");
            sizeSlider.setRange(10, 500);

            sizeSlider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    updateSize(sizeLabel, sizeSlider, sizeSlider.getValue());
                }
            });

            numPointsLabel = rootTable.findActor("label2");
            numPointsLabel.getStyle().font.setFixedWidthGlyphs(" 0123456789");
            numPointsSlider = rootTable.findActor("slider2");
            numPointsSlider.setRange(3, 20);

            numPointsSlider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    updateNumPoints(numPointsLabel, numPointsSlider, numPointsSlider.getValue());
                }
            });

            depthLabel = rootTable.findActor("label3");
            depthLabel.getStyle().font.setFixedWidthGlyphs(" 0123456789");
            depthSlider = rootTable.findActor("slider3");
            depthSlider.setRange(1, 250);

            depthSlider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    updateDepth(depthLabel, depthSlider, depthSlider.getValue());
                }
            });

            radiusLabel = rootTable.findActor("label4");
            radiusLabel.getStyle().font.setFixedWidthGlyphs(" 0123456789");
            radiusSlider = rootTable.findActor("slider4");
            radiusSlider.setRange(1, 250);

            radiusSlider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    updateRadius(radiusLabel, radiusSlider, radiusSlider.getValue());
                }
            });


            this.addActor(rootTable);
        }
        
        updateSize(sizeLabel, sizeSlider, geometryRegistry.getGeometryConfig().getSize());
        updateNumPoints(numPointsLabel, numPointsSlider, (((StarGeometryConfig) geometryRegistry.getGeometryConfig()).getNumPoints() / 2));
        updateDepth(depthLabel, depthSlider, ((StarGeometryConfig) geometryRegistry.getGeometryConfig()).getDepth());
        updateRadius(radiusLabel, radiusSlider, ((StarGeometryConfig) geometryRegistry.getGeometryConfig()).getCornerRadius());
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }

    private void updateSize(Label label, Slider slider, float value) {
        int intValue = (int) value;
        String intString = intValue + "";
        while (intString.length() < 3) {
            intString = " " + intString;
        }
        label.setText(intString);
        slider.setValue(intValue);
        geometryRegistry.getGeometryConfig().setSize(value);
        geometryTool.regenerateShape();
    }

    private void updateRadius(Label label, Slider slider, float value) {
        int intValue = (int) value;
        String intString = intValue + "";
        while (intString.length() < 3) {
            intString = " " + intString;
        }
        label.setText(intString);
        slider.setValue(intValue);
        ((StarGeometryConfig) geometryRegistry.getGeometryConfig()).setCornerRadius(value);
        geometryTool.regenerateShape();
    }

    private void updateNumPoints(Label label, Slider slider, float value) {
        int intValue = (int) value;
        String intString = intValue + "";
        while (intString.length() < 3) {
            intString = " " + intString;
        }
        label.setText(intString);
        slider.setValue(intValue);

        ((StarGeometryConfig) geometryRegistry.getGeometryConfig()).setNumPoints(intValue * 2);

        StarGeometry starGeometry = (StarGeometry) geometryRegistry.getActiveGeometry();
        Shape shape = geometryTool.getActiveShape();
        if (shape != null) {
            // rebuild the handlehooks before requesting a rendering of any shapes -- PolygonGeometry and handleHooks
            GeometryRenderState grs = (GeometryRenderState) shape.getRenderState();
            starGeometry.generateHandleHooks(shape, grs.getHandleHooks());
        }
        geometryTool.regenerateShape();
    }

    private void updateDepth(Label label, Slider slider, float value) {
        int intValue = (int) value;
        String intString = intValue + "";
        while (intString.length() < 3) {
            intString = " " + intString;
        }
        label.setText(intString);
        slider.setValue(intValue);
        ((StarGeometryConfig) geometryRegistry.getGeometryConfig()).setDepth(intValue);
        geometryTool.regenerateShape();
    }
}
