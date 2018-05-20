package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.phase.UpdatePhaseSchemaCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseUIStates;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * Allows for the creation of the object phase schema.
 */
@PerDodleEngine
public class ConfigManagerView extends BasePanelView {

    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStack;

    private Table phasesUIManager;
    private Table configRootTable;
    private UpdatePhaseSchemaCommand upsCommand;

    private boolean inPhaseSchemaEditing; // only allow 1 push on to okStack when click Configure

    @Inject
    public ConfigManagerView(AssetProvider assetProvider, CommandFactory commandFactory, CommandManager commandManager, EngineEventManager eventManager, ObjectManager objectManager, OkCancelStackManager okCancelStack) {
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.objectManager = objectManager;
        this.okCancelStack = okCancelStack;
    }

    /**
     * Initializes the component.
     */
    public final void initialize(Table rootTable, Skin skin) {
        LmlParser parser = VisLml.parser()
                .skin(skin)
                .build();

        this.phasesUIManager = rootTable;

        String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_PHASES_CONFIG_VIEW);
        configRootTable = (Table) parser.parseTemplate(template).get(0);

        phasesUIManager.top();
        phasesUIManager.add(configRootTable).expandX().fillX();


        TextButton phaseToggle = configRootTable.findActor("phaseToggler");
        phaseToggle.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!inPhaseSchemaEditing) {
                    eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_BASIC_MANAGER.toString());
                }
            }
        });

        VerticalGroup buttonList = configRootTable.findActor("phasesUIList");

        Table playButton = getButton(TextureAssets.PLAY_PNG, "Play", skin);
        buttonList.addActor(playButton);

        Table upgradeButton = getButton(TextureAssets.UPGRADE_PNG, "Upgrade", skin);
        buttonList.addActor(upgradeButton);

        Table settingsButton = getButton(TextureAssets.SETTINGS_PNG, "Settings", skin);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!inPhaseSchemaEditing) {
                    upsCommand = (UpdatePhaseSchemaCommand) commandFactory.createCommand(UpdatePhaseSchemaCommand.COMMAND_NAME);
                    upsCommand.init(objectManager.getActiveLayer().getName());

                    okCancelStack.push(new Runnable() {
                        @Override
                        public void run() {
                            commandManager.add(upsCommand);
                            inPhaseSchemaEditing = false;
                            eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.EASEOUT_SETTINGS_PANEL.toString());
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            upsCommand.undo();
                            inPhaseSchemaEditing = false;
                            eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.EASEOUT_SETTINGS_PANEL.toString());
                        }
                    });
                    eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.SWITCH_CONFIG_PHASE.toString());
                    inPhaseSchemaEditing = true;
                }
            }
        });
        buttonList.addActor(settingsButton);
    }

    private Table getButton(TextureAssets asset, String label, Skin skin) {
        Texture texture = assetProvider.getTexture(asset);
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        Button button = new Button(drawable);

        float padding = FullEditorInterface.getInterfaceRowSize() / 8f;
        float iconSize = FullEditorInterface.getInterfaceRowSize() - padding * 2;

        Table cell = new Table(skin);
        cell.add(button).size(iconSize, iconSize).expand().fill().top().center().row();
        cell.add(new Label(label, skin, "small")).bottom().center();

        return cell;
    }

    /**
     * remove the Basic UI from the Manager Panel.
     */
    public final void resetUI() {
        if (configRootTable != null) {
            //phasesUIManager.removeActor(basicRootTable);
            phasesUIManager.clearChildren();
            configRootTable = null;
            phasesUIManager.invalidate();
        }
    }
}
