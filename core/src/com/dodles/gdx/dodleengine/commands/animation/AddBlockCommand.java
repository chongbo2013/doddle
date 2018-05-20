package com.dodles.gdx.dodleengine.commands.animation;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.Block;
import com.dodles.gdx.dodleengine.animation.SceneAnimation;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import de.hypergraphs.hyena.core.shared.data.UUID;
import javax.inject.Inject;

/**
 * Command to add a new block.
 */
public class AddBlockCommand implements Command {
    public static final String COMMAND_NAME = "addBlock";
    
    private final AnimationManager animationManager;
    private final ObjectManager objectManager;
    
    private String sceneID;
    private String parentBlockID;
    private String blockID;
    private String displayName;
    
    @Inject
    public AddBlockCommand(AnimationManager animationManager, ObjectManager objectManager) {
        this.animationManager = animationManager;
        this.objectManager = objectManager;
    }
    
    /**
     * Initializes the command.
     */
    public final void init(SceneAnimation sAnim, Block parentBlock, String pDisplayName) {
        sceneID = sAnim.getSceneID();
        parentBlockID = parentBlock.getBlockId();
        blockID = UUID.uuid();
        displayName = pDisplayName;
    }

    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    @Override
    public final void execute() {
        SceneAnimation sAnim = animationManager.getSceneAnimation(sceneID);
        Block parentBlock = sAnim.getRootBlock().findBlock(parentBlockID);
        Block newBlock = new Block(blockID, parentBlock, objectManager, sAnim);
        newBlock.setDisplayName(displayName);
        parentBlock.addBlock(newBlock);
    }

    @Override
    public final void undo() {
        SceneAnimation sAnim = animationManager.getSceneAnimation(sceneID);
        Block parentBlock = sAnim.getRootBlock().findBlock(parentBlockID);
        parentBlock.removeBlock(blockID);
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("sceneID", sceneID);
        json.writeValue("parentBlockID", parentBlockID);
        json.writeValue("blockID", blockID);
        json.writeValue("displayName", displayName);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        sceneID = json.getString("sceneID");
        parentBlockID = json.getString("parentBlockID");
        blockID = json.getString("blockID");
        displayName = json.getString("displayName");
    }
}
