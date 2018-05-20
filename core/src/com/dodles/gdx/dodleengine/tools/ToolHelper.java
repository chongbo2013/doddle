package com.dodles.gdx.dodleengine.tools;

import com.dodles.gdx.dodleengine.DodleEngineConfig;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.EffectAnimator;
import com.dodles.gdx.dodleengine.animation.EffectDefinition;
import com.dodles.gdx.dodleengine.animation.EffectParameterType;
import com.dodles.gdx.dodleengine.animation.EffectType;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.MergeCommand;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.scenegraph.BaseDodlesViewGroup;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * Helpers for tools.
 */
@PerDodleEngine
public class ToolHelper {
    private final AnimationManager animationManager;
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final DodleEngineConfig engineConfig;
    private final DodleStageManager stageManager;
    private final ObjectManager objectManager;
    private final OkCancelStackManager okCancelStack;
    private final ToolRegistry toolRegistry;

    @Inject
    public ToolHelper(
        AnimationManager animationManager,
        AssetProvider assetProvider,
        CommandFactory commandFactory,
        DodleEngineConfig engineConfig,
        DodleStageManager stageManager,
        ObjectManager objectManager,
        OkCancelStackManager okCancelStack,
        ToolRegistry toolRegistry
    ) {
        this.animationManager = animationManager;
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.engineConfig = engineConfig;
        this.stageManager = stageManager;
        this.objectManager = objectManager;
        this.okCancelStack = okCancelStack;
        this.toolRegistry = toolRegistry;
    }
    
    /**
     * Creates a new object group and pushes a frame onto the ok stack to close it
     * if there isn't currently a new object group.
     */
    public final MergeCommand possiblyPushCloseNewObjectGroup(final String toolName) {
        return possiblyPushCloseNewObjectGroup(toolName, null);
    }

    public final MergeCommand possiblyPushCloseNewObjectGroup(final String toolName, String okCancelFrameType) {
        if (objectManager.getNewObjectGroup() == null) {
            Runnable newOkStackFrame = new Runnable() {
                @Override
                public void run() {
                    DodlesActor newObjectGroup = objectManager.getNewObjectGroup();
                    
                    if (newObjectGroup != null) {
                        objectManager.setNewObjectGroup(null);
                        
                        Tool activeTool = toolRegistry.getActiveTool();
                        
                        if (engineConfig.hasOption(DodleEngineConfig.Options.FULL_EDITOR) && activeTool != null && activeTool.getName().equals(toolName)) {
                        }
                        
                        stageManager.updateStateUi();

                        EffectDefinition puffEffect = EffectDefinition.getDefaultDefinitions(assetProvider).get(EffectType.MOVE).get("puff");
                        HashMap<EffectParameterType, Object> parameters = new HashMap<EffectParameterType, Object>();
                        parameters.put(EffectParameterType.LENGTH, 0.25f);
                        final EffectAnimator effectAnimator = new EffectAnimator("puffID", animationManager, objectManager, EffectType.MOVE, "puff", puffEffect, parameters, newObjectGroup);
                        
                        effectAnimator.startAnimation(new Runnable() {
                            @Override
                            public void run() {
                                effectAnimator.resetAnimation();
                                stageManager.updateStateUi();
                            }
                        });
                    }
                }
            };

            okCancelStack.push(newOkStackFrame, null, true, okCancelFrameType);

            String newGroupID = UUID.uuid();

            MergeCommand mc = (MergeCommand) commandFactory.createCommand(MergeCommand.COMMAND_NAME);
            BaseDodlesViewGroup layer = (BaseDodlesViewGroup) objectManager.getActiveLayer();
            mc.init(newGroupID, null, layer.getName(), layer.getActiveViewID(), true);
            mc.execute();

            objectManager.setNewObjectGroup((DodlesGroup) objectManager.getActor(newGroupID));

            return mc;
        }

        return null;
    }
}
