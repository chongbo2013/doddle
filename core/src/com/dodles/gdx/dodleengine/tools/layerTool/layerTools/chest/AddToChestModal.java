package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.chest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SkinAssets;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.CompoundCommand;
import com.dodles.gdx.dodleengine.commands.chest.CreateCharacterCommand;
import com.dodles.gdx.dodleengine.commands.chest.CreateInstanceCommand;
import com.dodles.gdx.dodleengine.commands.chest.CreateSceneInstanceCommand;
import com.dodles.gdx.dodleengine.editor.EditorInterfaceManager;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.util.DialogUtility;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.ui.reflected.ReflectedVisDialog;
import com.github.czyzby.lml.vis.util.VisLml;
import com.kotcrab.vis.ui.widget.VisTextField;
import de.hypergraphs.hyena.core.shared.data.UUID;
import javax.inject.Inject;

/**
 * Modal dialog for adding a new character in the chest.
 */
@PerDodleEngine
public class AddToChestModal {
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final DialogUtility dialogUtility;
    private final EditorInterfaceManager editorInterfaceManager;
    private final ObjectManager objectManager;
    
    private TextButton okButton;
    private TextButton cancelButton;
    private VisTextField objectNameField;
    private ReflectedVisDialog modal;
    private Skin skin;
    
    @Inject
    public AddToChestModal(
        AssetProvider assetProvider,
        CommandFactory commandFactory,
        CommandManager commandManager,
        DialogUtility dialogUtility,
        EditorInterfaceManager editorInterfaceManager,
        ObjectManager objectManager
    ) {
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.dialogUtility = dialogUtility;
        this.editorInterfaceManager = editorInterfaceManager;
        this.objectManager = objectManager;
    }
    
    /**
     * Opens the modal dialog.
     */
    public final void open() {
        if (skin == null) {
            skin = assetProvider.getSkin(SkinAssets.UI_SKIN);
        
            LmlParser parser = VisLml.parser()
                .skin(skin)
                .build();

            String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_LAYER_ADD_TO_CHEST_MODAL);
            modal = (ReflectedVisDialog) parser.parseTemplate(template).get(0);
            
            okButton = modal.findActor("okButton");
            okButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            save();
                        }
                    });
                }
            });
            
            cancelButton = modal.findActor("cancelButton");
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    modal.hide();
                }
            });
            
            objectNameField = modal.findActor("objectNameField");
        }
        
        objectNameField.setText("");
        
        modal.show(editorInterfaceManager.getStage());
    }
    
    private void save() {
        String objectName = objectNameField.getText();
        
        if (objectName.length() == 0) {
            dialogUtility.alert("Name Required", "Please enter a name for the object!");
            return;
        }
        
        DodlesActor newCharacter = objectManager.getSelectedActor();
        BaseDodlesViewGroup parentGroup = newCharacter.getParentDodlesViewGroup();
        CreateCharacterCommand ccc = (CreateCharacterCommand) commandFactory.createCommand(CreateCharacterCommand.COMMAND_NAME);
        ccc.init(newCharacter.getName(), objectName);
        
        if (ccc.executeLive()) {
            CreateInstanceCommand cic = (CreateInstanceCommand) commandFactory.createCommand(CreateInstanceCommand.COMMAND_NAME);
            String instanceID = UUID.uuid();
            cic.init(newCharacter.getName(), instanceID);
            cic.execute();
            
            CreateSceneInstanceCommand csic = (CreateSceneInstanceCommand) commandFactory.createCommand(CreateSceneInstanceCommand.COMMAND_NAME);
            String sceneInstanceID = UUID.uuid();
            csic.init(sceneInstanceID, parentGroup, instanceID);
            csic.execute();
            
            CompoundCommand compoundCommand = (CompoundCommand) commandFactory.createCommand(CompoundCommand.COMMAND_NAME);
            compoundCommand.init(ccc, cic, csic);
            
            commandManager.add(compoundCommand);
            objectManager.selectActor(objectManager.getActor(sceneInstanceID));
            modal.hide();
        } else {
            dialogUtility.alert("Object Exists in Chest", "An object already exists in the chest with this name, please give it another name!");
        }
    }
}
