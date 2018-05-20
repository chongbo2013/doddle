package com.dodles.gdx.dodleengine.editor.full.strokeconfigrow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.UpdateStrokeConfigCommand;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.EditorState;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorViewState;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfigKey;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.dodles.gdx.dodleengine.editor.full.FullEditorInterface.getInterfaceRowSize;

/**
 * Overlay for the color selector.
 */
public class ColorSelectorOverlay extends AbstractEditorView {
    public static final ArrayList<Color> DEFAULT_COLORS = new ArrayList<Color>();

    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    private final OkCancelStackManager okCancelStack;
    private final EditorState editorState;
    private final ObjectManager objectManager;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;

    private Table rootTable;
    private Slider hueSlider;
    private Slider saturationSlider;
    private Slider valueSlider;
    private Table selectedColorButton;
    private Color currentSelectedColor;
    private Table saturationRow;
    private Table valueRow;
    private ColorSelectorMode mode = ColorSelectorMode.GLOBAL;
    private StrokeConfigKey property = StrokeConfigKey.COLOR.COLOR;
    private UpdateStrokeConfigCommand command;
    
    private float smallWidgetSize;
    private float smallRowSize;
    private float threeSmallRows;
    private Color previousColor;

    static {
        for (float r = 0; r <= 1; r += 0.5f) {
            for (float g = 0; g <= 1; g += 0.5f) {
                for (float b = 0; b <= 1; b += 0.5f) {
                    DEFAULT_COLORS.add(new Color(r, g, b, 1));
                }
            }
        }
    }

    private Table currentColorButton;

    @Inject
    public ColorSelectorOverlay(AssetProvider assetProvider, EngineEventManager eventManager, OkCancelStackManager okCancelStackManager, EditorState editorState, ObjectManager objectManager, CommandFactory commandFactory, CommandManager commandManager) {
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.okCancelStack = okCancelStackManager;
        this.editorState = editorState;
        this.objectManager = objectManager;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public final void activate(Skin skin, String newState) {
        currentSelectedColor = editorState.getStrokeConfig().getColor();
        previousColor = editorState.getStrokeConfig().getColor().cpy();
        if (property == StrokeConfigKey.FILL) {
            currentSelectedColor = editorState.getStrokeConfig().getFill();
            previousColor = editorState.getStrokeConfig().getFill().cpy();
        }
        
        smallWidgetSize = getInterfaceRowSize() * .65f;
        smallRowSize = getInterfaceRowSize() * .75f;
        threeSmallRows = smallRowSize * 3;

        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                .skin(skin)
                .argument("smallRowSize", smallRowSize)
                .argument("threeSmallRows", threeSmallRows)
                .argument("oneRowSize", getInterfaceRowSize())
                .argument("threeRowSize", getInterfaceRowSize() * 3)
                .build();

            rootTable = (Table) parser.parseTemplate(assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_COLOR_SELECTOR_OVERLAY)).get(0);
            rootTable.setFillParent(true);

            setupSliders(skin);

            setUpSelectedColorButtons();

            setUpColorButtons();

            this.addActor(rootTable);
        }

        Texture currentColorTexture = assetProvider.getTexture(TextureAssets.EDITOR_WHITE_COLOR_BUTTON);
        TextureRegionDrawable currentColorDrawable = new TextureRegionDrawable(new TextureRegion(currentColorTexture));
        currentColorButton.setBackground(currentColorDrawable.tint(previousColor));

        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                if (command != null) {
                    commandManager.add(command);
                    command = null;
                }
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, FullEditorViewState.PREVIOUS_STATE);
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (command != null) {
                    command.undo();
                    command = null;
                } else {
                    setSelectedColorButton(previousColor);
                }
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, FullEditorViewState.PREVIOUS_STATE);
            }
        });
        
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                // This layout is a pain in the butt, it seems to need a forceful invalidation.
                // Also, it can't be immediate, hence the postRunnable. :(
                rootTable.invalidate();
                rootTable.invalidateHierarchy();
                ((Table) rootTable.findActor("colors")).invalidate();
                ((Table) rootTable.findActor("colors")).invalidateHierarchy();
            } 
        });
        
        setSelectedColorButton(currentSelectedColor);
    }

    private void setUpSelectedColorButtons() {
        Table selectedColorRow = rootTable.findActor("selectedColorRow");

        Texture noColorTexture = assetProvider.getTexture(TextureAssets.EDITOR_NO_COLOR_BUTTON);
        TextureRegionDrawable noColorDrawable = new TextureRegionDrawable(new TextureRegion(noColorTexture));
        final Button noColorButton = new Button(noColorDrawable, noColorDrawable.tint(Color.TAN));
        noColorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setSelectedColorButton(Color.WHITE);
            }
        });

        Texture currentColorTexture = assetProvider.getTexture(TextureAssets.EDITOR_WHITE_COLOR_BUTTON);
        TextureRegionDrawable currentColorDrawable = new TextureRegionDrawable(new TextureRegion(currentColorTexture));
        currentColorButton = new Table();
        currentColorButton.setTouchable(Touchable.enabled);
        currentColorButton.setBackground(currentColorDrawable.tint(previousColor));
        currentColorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedColorButton(previousColor);
            }
        });

        selectedColorRow.add(noColorButton).size(smallWidgetSize, smallWidgetSize).expandX();
        selectedColorRow.add(currentColorButton).size(smallWidgetSize, smallWidgetSize).expandX();

        Texture selectedColorTexture = assetProvider.getTexture(TextureAssets.EDITOR_WHITE_COLOR_BUTTON);
        Drawable selectedColorDrawable = new TextureRegionDrawable(new TextureRegion(selectedColorTexture)).tint(currentSelectedColor);
        selectedColorButton = new Table();
        selectedColorButton.setBackground(selectedColorDrawable);

        setSelectedColorButton(currentSelectedColor);

        selectedColorRow.add(selectedColorButton).size(smallWidgetSize, smallWidgetSize).expandX();
    }

    private void setUpColorButtons() {
        Table colorRowTable = rootTable.findActor("colors");
        Table newTable = new Table();
        
        ArrayList<List<Color>> colorRows = new ArrayList<List<Color>>();
        
        if (DEFAULT_COLORS.size() != 27) {
            throw new GdxRuntimeException("Assuming 27 default colors!");
        }
        
        colorRows.add(DEFAULT_COLORS.subList(0, 10));
        colorRows.add(DEFAULT_COLORS.subList(10, 17));
        colorRows.add(DEFAULT_COLORS.subList(17, 27));
        
        for (List<Color> curColorRow : colorRows) {
            Table curColorRowTable = new Table();
            
            for (Color curColor : curColorRow) {
                createColorButton(curColor, curColorRowTable);
            }
            
            newTable.add(curColorRowTable).height(smallRowSize).fillX().expandX().row();
        }

        colorRowTable.add(newTable).width(Value.percentWidth(1f, rootTable)).height(threeSmallRows);
    }
    
    private void createColorButton(final Color color, Table colorRowTable) {
        Texture texture = assetProvider.getTexture(TextureAssets.EDITOR_WHITE_COLOR_ROUND_BUTTON);
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        Button button = new Button(drawable.tint(new Color(color)));
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setSelectedColorButton(color);
            }
        });
        
        colorRowTable.add(button).size(smallWidgetSize, smallWidgetSize).expandX();
    }

    private void setupSliders(Skin skin) {
        Table hueRow = rootTable.findActor("hueRow");
        saturationRow = rootTable.findActor("saturationRow");
        valueRow = rootTable.findActor("valueRow");

        Drawable hueBackground = getBackground(TextureAssets.EDITOR_HUE_SLIDER);
        hueRow.setBackground(hueBackground);

        setSliderBackgrounds(currentSelectedColor);

        int[] hsv = rgbToHsb(currentSelectedColor.r, currentSelectedColor.g, currentSelectedColor.b);

        hueSlider = new NoMinWidthSlider(0, 359, 1, false, skin);
        hueSlider.setValue(hsv[0]);

        saturationSlider = new NoMinWidthSlider(0, 100, 1, false, skin);
        saturationSlider.setValue(hsv[1]);

        valueSlider = new NoMinWidthSlider(0, 100, 1, false, skin);
        valueSlider.setValue(hsv[2]);

        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int[] rgb = hsvToRgb(Math.round(hueSlider.getValue()), saturationSlider.getValue(), valueSlider.getValue());
                Color tempColor = Color.valueOf(componentToHex(rgb[0]) + componentToHex(rgb[1]) + componentToHex(rgb[2]));
                if (!tempColor.equals(currentSelectedColor)) {
                    setSelectedColorButton(tempColor, true);
                    if (!actor.equals(saturationSlider)) {
                        setSliderBackgrounds(tempColor);
                    }
                    setSelectedColor(tempColor);
                }
            }
        };

        hueSlider.addListener(listener);
        saturationSlider.addListener(listener);
        valueSlider.addListener(listener);

        hueRow.add(hueSlider).expandX().fillX();
        saturationRow.add(saturationSlider).expandX().fillX();
        valueRow.add(valueSlider).expandX().fillX();
    }

    private void setSelectedColorButton(Color color) {
        setSelectedColorButton(color, false);
    }

    private void setSelectedColorButton(Color color, boolean skipSliders) {
        Texture selectedColorTexture = assetProvider.getTexture(TextureAssets.EDITOR_WHITE_COLOR_BUTTON);
        Drawable selectedColorDrawable = new TextureRegionDrawable(new TextureRegion(selectedColorTexture)).tint(color);
        selectedColorButton.setBackground(selectedColorDrawable);

        if (!skipSliders) {
            int[] hsv = rgbToHsb(color.r * 255, color.g * 255, color.b * 255);
            hueSlider.setValue(hsv[0]);
            saturationSlider.setValue(hsv[1]);
            valueSlider.setValue(hsv[2]);
        }

        currentSelectedColor = color;
    }

    private void setSliderBackgrounds(Color color) {
        Drawable saturationBackground = getBackground(TextureAssets.EDITOR_SATURATION_SLIDER, color);
        saturationRow.setBackground(saturationBackground);

        Drawable valueBackground = getBackground(TextureAssets.EDITOR_VALUE_SLIDER, color);
        valueRow.setBackground(valueBackground);
    }

    private void setSelectedColor(Color color) {
        if (objectManager.getSelectedActors().size() > 0 && this.mode == ColorSelectorMode.SELECTED) {
            ArrayList<String> selectedObjectIds = new ArrayList<String>();
            for (DodlesActor actor : objectManager.getSelectedActors()) {
                selectedObjectIds.add(actor.getName());
            }
            
            if (command != null) {
                command.undo();
            }

            command = (UpdateStrokeConfigCommand) commandFactory.createCommand(UpdateStrokeConfigCommand.COMMAND_NAME);
            StrokeConfig strokeConfig = new StrokeConfig();

            if (this.property == StrokeConfigKey.COLOR) {
                strokeConfig.setColor(color);
            } else {
                strokeConfig.setFill(color);
            }

            command.init(selectedObjectIds, strokeConfig, this.property.get());
            command.execute();
        } else if (this.mode == ColorSelectorMode.GLOBAL) {
            if (this.property == StrokeConfigKey.COLOR) {
                editorState.getStrokeConfig().setColor(color);
            } else {
                editorState.getStrokeConfig().setFill(color);
            }
            
            eventManager.fireEvent(EngineEventType.STROKE_CONFIG_CHANGED);
        }
    }

    @Override
    public final void deactivate() {

    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }

    private Drawable getBackground(TextureAssets textureAssets) {
        return getBackground(textureAssets, null);
    }

    private Drawable getBackground(TextureAssets textureAssets, Color tint) {
        Texture texture = assetProvider.getTexture(textureAssets);
        if (tint == null) {
            return new TextureRegionDrawable(new TextureRegion(texture));
        } else {
            return new TextureRegionDrawable(new TextureRegion(texture)).tint(tint);
        }
    }

    /**
     * Set the color selector mode.
     */
    public final void setMode(ColorSelectorMode newMode) {
        this.mode = newMode;
    }

    /**
     * Set the color property mode.
     */
    public final void setProperty(StrokeConfigKey newProperty) {
        this.property = newProperty;
    }

    /**
     * @param h
     *            0-360
     * @param s
     *            0-100
     * @param v
     *            0-100
     */
    public static int[] hsvToRgb(float h, float s, float v) {

        double r = 0;
        double g = 0;
        double b = 0;
        double i;
        double f;
        double p;
        double q;
        double t;

        // h = h / 360;
        if (v == 0) {
            return new int[] {0, 0, 0};
        }

        s = s / 100;
        v = v / 100;
        h = h / 60;

        i = Math.floor(h);
        f = h - i;
        p = v * (1 - s);
        q = v * (1 - (s * f));
        t = v * (1 - (s * (1 - f)));

        if (i == 0) {
            r = v;
            g = t;
            b = p;
        } else if (i == 1) {
            r = q;
            g = v;
            b = p;
        } else if (i == 2) {
            r = p;
            g = v;
            b = t;
        } else if (i == 3) {
            r = p;
            g = q;
            b = v;
        } else if (i == 4) {
            r = t;
            g = p;
            b = v;
        } else if (i == 5) {
            r = v;
            g = p;
            b = q;
        }

        r = Math.floor(r * 255);
        g = Math.floor(g * 255);
        b = Math.floor(b * 255);

        return new int[] {(int) r, (int) g, (int) b};
    }

    private int[] rgbToHsb(float r, float g, float b) {
        float max;
        float min;
        float h = 0;
        float s;
        float v;
        float d;

        r = r / 255;
        g = g / 255;
        b = b / 255;

        max = Math.max(r, g);
        max = Math.max(max, b);
        min = Math.min(r, g);
        min = Math.min(min, b);
        v = max;

        d = max - min;
        if (max == 0) {
            s = 0;
        } else {
            s = d / max;
        }

        if (max == min) {
            h = 0; // achromatic
        } else {
            if (max == r) {
                int value = 0;
                if (g < b) {
                    value = 6;
                }
                h = (g - b) / d + value;
            } else if (max == g) {
                h = (b - r) / d + 2;
            } else if (max == b) {
                h = (r - g) / d + 4;
            }
            h /= 6;
        }

        // map top 360,100,100
        h = Math.round(h * 360);
        s = Math.round(s * 100);
        v = Math.round(v * 100);

        return new int[] {(int) h, (int) s, (int) v};
    }

    private String componentToHex(int component) {
        String hex = Integer.toHexString(component);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }
    
    /**
     * Custom slider with no min width to avoid deforming the UI on smaller devices.
     */
    private class NoMinWidthSlider extends Slider {
        public NoMinWidthSlider(float min, float max, float stepSize, boolean vertical, Skin skin) {
            super(min, max, stepSize, vertical, skin);
        }
        
        public float getPrefWidth() {
            return 0;
        }
    }
}
