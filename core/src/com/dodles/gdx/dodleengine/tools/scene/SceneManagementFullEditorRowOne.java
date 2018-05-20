package com.dodles.gdx.dodleengine.tools.scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.ObjectManager.SceneData;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * Row that allows management of the scenes in the dodle.
 */
public class SceneManagementFullEditorRowOne extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final EventBus eventBus;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    private final SceneUIListPanel sceneUIListPanel;
    
    private Table rootTable;
    private Skin skin;
    private EventSubscriber commandStackChanged;
    
    @Inject
    public SceneManagementFullEditorRowOne(AssetProvider assetProvider, CommandFactory commandFactory, CommandManager commandManager, EventBus eventBus, EngineEventManager eventManager, ObjectManager objectManager, SceneUIListPanel sceneUIListPanel) {
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.eventBus = eventBus;
        this.eventManager = eventManager;
        this.objectManager = objectManager;
        this.sceneUIListPanel = sceneUIListPanel;
    }
    
    @Override
    public final void activate(Skin newSkin, String newState) {
        this.skin = newSkin;
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .build();

            String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_SCENE_MANAGEMENT_ROW1);
            rootTable = (Table) parser.parseTemplate(template).get(0);
            
//            Button addSceneButton = rootTable.findActor("addSceneButton");
//            addSceneButton.addListener(new ChangeListener() {
//                @Override
//                public void changed(ChangeEvent event, Actor actor) {
//                    CreateSceneCommand command = (CreateSceneCommand) commandFactory.createCommand(CreateSceneCommand.COMMAND_NAME);
//                    command.init();
//                    command.execute();
//                    commandManager.add(command);
//
//                    refreshSceneData();
//                }
//            });

//            Button listButton = rootTable.findActor("listScenes");
//            listButton.addListener(new ClickListener() {
//
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SceneUIListPanel.STATE_NAME, "SceneList");
//                }
//            });
//
//            Button layersButton = rootTable.findActor("listLayers");
//            layersButton.addListener(new ClickListener() {
//
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                   eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SceneUIListPanel.STATE_NAME, "LayerList");
//                }
//            });

            Button addSceneButton2 = rootTable.findActor("addSceneButton2");
            addSceneButton2.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!sceneUIListPanel.isPushedOkStack()) {
                        eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, SceneUIListPanel.STATE_NAME, "AddScene");
                    }
                }
            });

            
            this.addActor(rootTable);
        }
        commandStackChanged = new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic topic, EventType eventType, EventData data) {
                if (EventType.COMMAND_STACK_CHANGED.equals(eventType)) {
                    refreshSceneData();
                }
            }
        };
        eventBus.addSubscriber(commandStackChanged);

        refreshSceneData();
    }

    @Override
    public final void deactivate() {
        eventBus.removeSubscriber(commandStackChanged);
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
    
    private void refreshSceneData() {
        Table sceneTable = rootTable.findActor("sceneTable");
        ButtonGroup group = new ButtonGroup();
        group.setMaxCheckCount(1);
        group.setMinCheckCount(1);
        sceneTable.clear();
        
        for (SceneData sceneData : objectManager.allSceneData()) {
            addSceneButton(sceneTable, group, sceneData);
        }
    }
    
    private void addSceneButton(Table sceneTable, ButtonGroup group, final SceneData sceneData) {
        final TextButton button = new TextButton(sceneData.getScene().getNumber() + "", skin, "toggle");
        button.setChecked(sceneData.isActive());
        
        button.addListener(new ChangeListener() { 
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (!sceneUIListPanel.isPushedOkStack()) {
                    if (button.isChecked()) {
                        objectManager.setActiveScene(sceneData.getScene().getName());
                    }
                }
            }
        });

        group.add(button);
        sceneTable.add(button).padLeft(5);
    }
}
