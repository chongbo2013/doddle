package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.spine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.badlogic.gdx.utils.Array;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.StateManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SpineAssets;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.GroupHelper;
import com.dodles.gdx.dodleengine.commands.spine.AddSpineCommand;
import com.dodles.gdx.dodleengine.commands.spine.DeleteSpineCommand;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Phase;
import com.dodles.gdx.dodleengine.scenegraph.Spine;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.layerTool.LayerTool;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;

/**
 * Editor overlay for controlling spine actors.
 */
@PerDodleEngine
public class SpineToolFullEditorOverlay extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final EngineEventManager eventManager;
    private final GroupHelper groupHelper;
    private final ToolRegistry toolRegistry;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStack;
    private final StateManager stateManager;

    private Table rootTable;
    private Skin skin;
    private static int selectedSkeleton;

    @Inject
    public SpineToolFullEditorOverlay(
            AssetProvider assetProvider,
            CommandFactory commandFactory,
            CommandManager commandManager,
            EngineEventManager eventManager,
            GroupHelper groupHelper,
            ToolRegistry toolRegistry,
            ObjectManager objectManager,
            OkCancelStackManager okCancelStack,
            StateManager stateManager
    ) {
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.groupHelper = groupHelper;
        this.toolRegistry = toolRegistry;
        this.objectManager = objectManager;
        this.okCancelStack = okCancelStack;
        this.stateManager = stateManager;
    }

    @Override
    public final void activate(Skin pSkin, String newState) {
        skin = pSkin;
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .build();

            rootTable = (Table) parser.parseTemplate(assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_EMPTY_OVERLAY)).get(0);
            this.addActor(rootTable);
        }

        updateButtons();

        okCancelStack.push(new Runnable() {
            @Override
            public void run() {
                eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, LayerTool.TOOL_NAME);
            }
        });
    }

    private void updateButtons() {
        rootTable.clear();
        Array items = new Array();

//                SPINE_SPEEDY("spine/speedy.json", "spine/speedy.atlas"),
//                SPINE_SPINEBOY("spine/spineboy.json", "spine/spineboy.atlas"),
//                SPINE_SNAKE("spine/alien.json", "spine/alien.atlas"),
//                SPINE_SIMPLEBOY("spine/simpleBoy.json", "spine/simpleBoy.atlas"),
//                SPINE_HERO("spine/hero.json", "spine/hero.atlas"),
//                SPINE_GOBLINS("spine/goblins.json", "spine/goblins.atlas"),
//                SPINE_DRAGON("spine/dragon.json", "spine/dragon.atlas"),
//                SPINE_SPINEBOYOLD("spine/soineboy-old.json", "spine/soineboy-old.atlas"),
//                SPINE_STRETCHYMAN("spine/stretchyman.json", "spine/stretchyman.atlas"),
//                SPINE_TANK("spine/tank.json", "spine/tank.atlas"),
//                SPINE_TEST("spine/test.json", "spine/test.atlas"),
//                SPINE_VINE("spine/vine.json", "spine/vine.atlas"),
//                SPINE_POWERUP("spine/powerup.json", "spine/powerup.atlas");

        items.add("Simple");
        items.add("Speedy");
        items.add("Spineboy");
        items.add("Alien");
        items.add("Hero");
        items.add("Dragon");
        items.add("Powerup");
        items.add("Snake");

        items.add(" ");
        items.add(" ");
        items.add(" ");
        items.add(" ");
        items.add(" ");

        BitmapFont bf = new BitmapFont();
        bf.getData().setScale(1.5f * DensityManager.getScale());
        List.ListStyle style = new List.ListStyle(bf, Color.RED, Color.BLACK, new Drawable() {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {

            }

            @Override
            public float getLeftWidth() {
                return 0;
            }

            @Override
            public void setLeftWidth(float leftWidth) {

            }

            @Override
            public float getRightWidth() {
                return 0;
            }

            @Override
            public void setRightWidth(float rightWidth) {

            }

            @Override
            public float getTopHeight() {
                return 0;
            }

            @Override
            public void setTopHeight(float topHeight) {

            }

            @Override
            public float getBottomHeight() {
                return 0;
            }

            @Override
            public void setBottomHeight(float bottomHeight) {

            }

            @Override
            public float getMinWidth() {
                return 0;
            }

            @Override
            public void setMinWidth(float minWidth) {

            }

            @Override
            public float getMinHeight() {
                return 0;
            }

            @Override
            public void setMinHeight(float minHeight) {

            }
        });
        final List spineList = new List(style);
        spineList.setItems(items);
        //spineList.setScale(DensityManager.getScale());
        spineList.setSelected(0);
        spineList.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                Phase activePhase = getActivePhase();
//                Spine spine = getActiveSpine();
                setSelectedSkeleton(spineList.getSelectedIndex());
            }
        });
        rootTable.add(spineList).expandX().expandY();

        final String addSpineText = "Add";
        final String removeSpineText = "Rem";
        final TextButton addSpine = new TextButton(addSpineText, skin);

        if (getActiveSpine() != null) {
            addSpine.setText(removeSpineText);
        }

        addSpine.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Phase activePhase = getActivePhase();
                Spine spine = getActiveSpine();

                if (activePhase != null) {
                    if (spine == null) {
                        String newID = UUID.uuid();
                        AddSpineCommand asc = (AddSpineCommand) commandFactory.createCommand(AddSpineCommand.COMMAND_NAME);
//                        asc.init(newID, activePhase.getName(), assetProvider.getString(SpineAssets.SELECTED));
                        asc.init(newID, activePhase.getName(), assetProvider.getString(getSelectedSkeleton()));
                        asc.execute();

                        commandManager.add(asc);

                        activePhase.setDisplayMode(Phase.DisplayMode.SPINE_OUTLINE);
                        addSpine.setText(removeSpineText);
                    } else {
                        DeleteSpineCommand dsc = (DeleteSpineCommand) commandFactory.createCommand(DeleteSpineCommand.COMMAND_NAME);
                        dsc.init(spine.getName());
                        dsc.execute();
                        commandManager.add(dsc);

                        activePhase.setDisplayMode(Phase.DisplayMode.SOURCE);
                        addSpine.setText(addSpineText);
                    }
                }
            }
        });

        rootTable.add(addSpine).expandX();

        final String playText = "Play";
        final String stopText = "Stop";
        final TextButton playButton = new TextButton(playText, skin);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Phase activePhase = getActivePhase();
                Spine spine = getActiveSpine();

                if (spine != null) {
                    if (playButton.getText().toString().equals(playText)) {
                        activePhase.setDisplayMode(Phase.DisplayMode.SPINE_FINAL);
                        spine.animate();
                        playButton.setText(stopText);
                    } else {
                        activePhase.setDisplayMode(Phase.DisplayMode.SPINE_OUTLINE);
                        spine.stop();
                        playButton.setText(playText);
                    }
                }
            }
        });
        rootTable.add(playButton).expandX();

        final String toggleGrabTextureText = "Orig";
        final String toggleSpineBoyText = "Text";
        final TextButton textureButton = new TextButton(toggleSpineBoyText, skin);

        textureButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Spine spine = getActiveSpine();
                Phase activePhase = getActivePhase();
                if (textureButton.getText().toString().equals(toggleGrabTextureText)) {
                    spine.setUseSpineBoy(true);
                    //activePhase.setDisplayMode(Phase.DisplayMode.SPINE_FINAL);
//                    spine.atlas
                    textureButton.setText(toggleSpineBoyText);
                } else {
                    if (spine != null) {
                        spine.setUseSpineBoy(false);
                    }
                    //activePhase.setDisplayMode(Phase.DisplayMode.SPINE_OUTLINE);
                    textureButton.setText(toggleGrabTextureText);
                }
            }
        });
        rootTable.add(textureButton).expandX();

        //Goofy, does the button show current mode, or the next mode activated on click?
        final TextButton poseButton = new TextButton("Off", skin);
        poseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Phase activePhase = getActivePhase();
                Spine spine = getActiveSpine();
                if (spine != null) {
                    Spine.Modes m = spine.getMode();
                    if (m == Spine.Modes.Off) {
                        //but do rotate now
                        spine.setMode(Spine.Modes.Rotate);
                        poseButton.setText("Rotate");

                    } else if (m == Spine.Modes.Rotate) {
                        spine.setMode(Spine.Modes.Translate);
                        poseButton.setText("Translate");

                    } else if (m == Spine.Modes.Translate) {
                        spine.setMode(Spine.Modes.Scale);

                        poseButton.setText("Scale");

                    } else if (m == Spine.Modes.Scale) {
                        spine.setMode(Spine.Modes.Length);
                        poseButton.setText("Length");

                    } else if (m == Spine.Modes.Length) {
                        spine.setMode(Spine.Modes.Off);
                        poseButton.setText("Off");

                    }
                }
            }
        });
        rootTable.add(poseButton).expandX();


    }

    /**
     * Returns the phase being edited.
     */
    public final Phase getActivePhase() {
        DodlesActor selected = objectManager.getSelectedActor();

        if (selected instanceof DodlesGroup) {
            return ((DodlesGroup) selected).getVisiblePhase();
        }

        return null;
    }

    /**
     * Returns the spine being edited.
     */
    public final Spine getActiveSpine() {
        Phase phase = getActivePhase();

        if (phase != null) {
            return phase.getSpine();
        }

        return null;
    }

    @Override
    public final void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }

    /**
     * Set the pre defined skeleton to use.
     */
    public final void setSelectedSkeleton(int selectedSkeleton) {
        this.selectedSkeleton = selectedSkeleton;
    }

    /**
     * return the predefined skeleton from the list.
     */
    public static SpineAssets getSelectedSkeleton() {

        switch (selectedSkeleton) {
            case 0:
                return SpineAssets.SPINE_SIMPLEBOY;
            case 1:
                return SpineAssets.SPINE_SPEEDY;
            case 2:
                return SpineAssets.SPINE_SPINEBOY;
            case 3:
                return SpineAssets.SPINE_ALIEN;
            case 4:
                return SpineAssets.SPINE_HERO;
            case 5:
                return SpineAssets.SPINE_DRAGON;
            case 6:
                return SpineAssets.SPINE_POWERUP;
            case 7:
                return SpineAssets.SPINE_SNAKE;
            default:
                return SpineAssets.SPINE_SIMPLEBOY;
        }
    }
}