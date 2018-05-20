package com.dodles.gdx.dodleengine.geometry.circle;

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
import com.dodles.gdx.dodleengine.tools.geometry.GeometryTool;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * Circle configuration row.
 */
@PerDodleEngine
public class CircleConfigurationRowEditorView extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final GeometryRegistry geometryRegistry;
    private final GeometryTool geometryTool;

    private Table rootTable;
    private Label sizeLabel;
    private Slider sizeSlider;

    @Inject
    public CircleConfigurationRowEditorView(AssetProvider assetProvider, GeometryRegistry geometryRegistry, GeometryTool geometryTool) {
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

            String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_ONE_SLIDER_ROW);
            rootTable = (Table) parser.parseTemplate(template).get(0);
            rootTable.setFillParent(true);

            sizeLabel = rootTable.findActor("label1");
            sizeLabel.getStyle().font.setFixedWidthGlyphs(" 0123456789");
            sizeSlider = rootTable.findActor("slider1");
            sizeSlider.setRange(10, 500);
            updateSize(sizeLabel, sizeSlider, geometryRegistry.getGeometryConfig().getSize());

            sizeSlider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    updateSize(sizeLabel, sizeSlider, sizeSlider.getValue());
                }
            });

            this.addActor(rootTable);
        }
        
        updateSize(sizeLabel, sizeSlider, geometryRegistry.getGeometryConfig().getSize());
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
}
