package com.dodles.gdx.dodleengine.tools.chest;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.chest.CreateSceneInstanceCommand;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.LinkActor;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import com.dodles.gdx.dodleengine.scenegraph.chest.CharacterInstance;
import com.dodles.gdx.dodleengine.scenegraph.chest.ChestCharacter;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;
import de.hypergraphs.hyena.core.shared.data.UUID;
import javax.inject.Inject;

/**
 * Interface for adding characters to a scene.
 */
public class ChestCharacterFullEditorRow extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final ObjectManager objectManager;
    
    private Table rootTable;
    private Skin skin;
    
    @Inject
    public ChestCharacterFullEditorRow(AssetProvider assetProvider, CommandFactory commandFactory, CommandManager commandManager, ObjectManager objectManager) {
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.objectManager = objectManager;
    }
    
    @Override
    public final void activate(Skin newSkin, String newState) {
        skin = newSkin;
        
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .build();

            String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_CHEST_CHARACTER_ROW);
            rootTable = (Table) parser.parseTemplate(template).get(0);
            
            this.addActor(rootTable);
        }
        
        updateUI();
    }

    @Override
    public void deactivate() {
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
    
    private void updateUI() {
        Table characterTable = rootTable.findActor("characterTable");
        characterTable.clear();
        
        for (ChestCharacter character : objectManager.allChestCharacters()) {
            characterTable.add(createCharacterButton(character));
        }
    }
    
    private TextButton createCharacterButton(ChestCharacter character) {
        // TODO: support multiple instances
        final CharacterInstance instance = character.getInstances().get(0);
        final Scene activeScene = objectManager.getScene();
        boolean characterInScene = false;
        
        for (LinkActor link : instance.getSceneInstances()) {
            Scene linkScene = CommonActorOperations.getScene(link);
            characterInScene |= linkScene == activeScene;
        }
        
        final TextButton button = new TextButton(character.getCharacterName(), skin, "toggle");
        button.setChecked(characterInScene);
        
        button.addListener(new ChangeListener() {
            private boolean wasChecked = button.isChecked();
            
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (!wasChecked) {
                    wasChecked = true;
                    
                    CreateSceneInstanceCommand csic = (CreateSceneInstanceCommand) commandFactory.createCommand(CreateSceneInstanceCommand.COMMAND_NAME);
                    csic.init(UUID.uuid(), activeScene, instance.getName());
                    csic.execute();
                    commandManager.add(csic);
                }
                
                button.setChecked(true);
            }
        });
        
        return button;
    }
}
