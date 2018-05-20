package com.dodles.gdx.dodleengine.geometry.polygon;

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
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.geometry.GeometryRenderState;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * Polygon configuration row.
 */
@PerDodleEngine
public class PolygonConfigurationRowEditorView extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final GeometryRegistry geometryRegistry;
    private final GeometryTool geometryTool;
    private final EventBus eventBus;

    private Table rootTable;
    private Label sizeLabel;
    private Slider sizeSlider;
    private Label numPointsLabel;
    private Slider numPointsSlider;
    private Label radiusLabel;
    private Slider radiusSlider;

    @Inject
    public PolygonConfigurationRowEditorView(AssetProvider assetProvider, EventBus eventBus, GeometryRegistry geometryRegistry, GeometryTool geometryTool) {
        this.assetProvider = assetProvider;
        this.geometryRegistry = geometryRegistry;
        this.geometryTool = geometryTool;
        this.eventBus = eventBus;

        this.eventBus.addSubscriber(new EventSubscriber(EventTopic.EDITOR) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case CHANGE_SHAPE_CORNER:
                        setNumPoints(Float.parseFloat(data.getFirstStringParam()));
                        break;
                }
            }
        });

    }

    @Override
    public final void activate(Skin skin, String newState) {
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .build();

            String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_THREE_SLIDER_ROW);
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
            numPointsSlider.setRange(5, 20);

            numPointsSlider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    updateNumPoints(numPointsLabel, numPointsSlider, numPointsSlider.getValue());
                }
            });

            radiusLabel = rootTable.findActor("label3");
            radiusLabel.getStyle().font.setFixedWidthGlyphs(" 0123456789");
            radiusSlider = rootTable.findActor("slider3");
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
        updateNumPoints(numPointsLabel, numPointsSlider, ((PolygonGeometryConfig) geometryRegistry.getGeometryConfig()).getNumPoints());
        updateRadius(radiusLabel, radiusSlider, ((PolygonGeometryConfig) geometryRegistry.getGeometryConfig()).getCornerRadius());
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
        ((PolygonGeometryConfig) geometryRegistry.getGeometryConfig()).setCornerRadius(value);
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

        setNumPoints(value);
    }

    private void setNumPoints (float value) {
        int intValue = (int) value;
        PolygonGeometryConfig polygonGeometryConfig = (PolygonGeometryConfig) geometryRegistry.getGeometryConfig();
        polygonGeometryConfig.setNumPoints(intValue);
        System.out.println("Set num point to " + intValue);
        PolygonGeometry polygonGeometry = (PolygonGeometry) geometryRegistry.getActiveGeometry();
        Shape shape = geometryTool.getActiveShape();
        if (shape != null) {
            // rebuild the handlehooks before requesting a rendering of any shapes -- PolygonGeometry and handleHooks
            GeometryRenderState grs = (GeometryRenderState) shape.getRenderState();
            polygonGeometry.generateHandleHooks(shape, grs.getHandleHooks());
        }

        geometryTool.regenerateShape();
    }
}
