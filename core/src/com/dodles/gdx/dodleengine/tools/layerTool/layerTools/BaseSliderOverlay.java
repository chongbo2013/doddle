package com.dodles.gdx.dodleengine.tools.layerTool.layerTools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.UpdateStrokeConfigCommand;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import static com.dodles.gdx.dodleengine.editor.full.FullEditorInterface.getInterfaceRowSize;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import java.util.ArrayList;

/**
 * Base slider overlay functionality.
 */
@PerDodleEngine
public abstract class BaseSliderOverlay extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final ToolRegistry toolRegistry;
    private final OkCancelStackManager okCancelStack;
    private final CommandFactory commandFactory;
    private final ObjectManager objectManager;
    private final CommandManager commandManager;

    private Table rootTable;
    private ArrayList<String> selectedObjectIds = new ArrayList<String>();
    private Slider slider;
    private UpdateStrokeConfigCommand command;

    public BaseSliderOverlay(AssetProvider assetProvider, ToolRegistry toolRegistry, OkCancelStackManager okCancelStack, ObjectManager objectManager, CommandFactory commandFactory, CommandManager commandManager) {
        this.assetProvider = assetProvider;
        this.toolRegistry = toolRegistry;
        this.okCancelStack = okCancelStack;
        this.objectManager = objectManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
    }
    
    @Override
    public final void activate(Skin skin, String newState) {        
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                .skin(skin)
                .argument("oneRowSize", getInterfaceRowSize())
                .build();

            rootTable = (Table) parser.parseTemplate(assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_SLIDER_OVERLAY)).get(0);

            Table valueRow = rootTable.findActor("valueRow");
            Table sliderRow = rootTable.findActor("sliderRow");

            Drawable background = getBackground();
            if (background != null) {
                sliderRow.setBackground(background);
            }

            slider = getSlider(skin);

            final Label label = new Label(Integer.toString(Math.round(slider.getValue())), skin);
            label.setColor(Color.BLACK);

            slider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    float size = translateValue(slider.getValue(), false);

                    selectedObjectIds.clear();

                    for (DodlesActor selectedGroup : objectManager.getSelectedActors()) {
                        for (DodlesActor leaf : objectManager.getLeafActors(selectedGroup)) {
                            selectedObjectIds.add(leaf.getName());
                        }
                    }

                    if (command != null) {
                        command.undo();
                    }

                    command = (UpdateStrokeConfigCommand) commandFactory.createCommand(UpdateStrokeConfigCommand.COMMAND_NAME);
                    StrokeConfig strokeConfig = new StrokeConfig();
                    setConfigValue(strokeConfig, size);

                    command.init(selectedObjectIds, strokeConfig, getPropertyName());
                    command.execute();

                    label.setText(Float.toString(size));
                }
            });

            valueRow.add(label);

            sliderRow.add(slider).expandX().fillX();

            this.addActor(rootTable);
        }
        
        for (DodlesActor selectedGroup : objectManager.getSelectedActors()) {
            for (DodlesActor leaf : objectManager.getLeafActors(selectedGroup)) {
                if (leaf instanceof Shape) {
                    StrokeConfig strokeConfig = ((Shape) leaf).getStrokeConfig();
                    slider.setValue(translateValue(getConfigValue(strokeConfig), true));
                }
            }
        }
        
        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                if (command != null) {
                    commandManager.add(command);
                    command = null;
                }

                toolRegistry.setActiveTool(LayerTool.TOOL_NAME);
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (command != null) {
                    command.undo();
                    command = null;
                }
                
                toolRegistry.setActiveTool(LayerTool.TOOL_NAME);
            }
        });
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }

    /**
     * Returns the slider.
     */
    protected abstract Slider getSlider(Skin skin);

    /**
     * Translates the value between dodle and slider values.
     */
    protected abstract float translateValue(float value, boolean dodleToSlider);

    /**
     * Returns the property name.
     */
    protected abstract String getPropertyName();

    /**
     * Sets the configuration value changed by the slider change.
     */
    protected abstract void setConfigValue(StrokeConfig config, float value);
    
    /**
     * Returns the configuration value being changed by the slider.
     */
    protected abstract float getConfigValue(StrokeConfig config);

    /**
     * Returns the background image for the slider.
     */
    protected abstract Drawable getBackground();
}
