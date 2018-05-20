package com.dodles.gdx.dodleengine.commands;

// CHECKSTYLE.OFF: AvoidStarImport - All commands will be used here...
import com.dodles.gdx.dodleengine.commands.animation.*;
import com.dodles.gdx.dodleengine.commands.chest.*;
import com.dodles.gdx.dodleengine.commands.phase.*;
import com.dodles.gdx.dodleengine.commands.scene.*;
import com.dodles.gdx.dodleengine.commands.spine.AddSpineCommand;
import com.dodles.gdx.dodleengine.commands.spine.DeleteSpineCommand;
// CHECKSTYLE.ON: AvoidStarImport

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Creates instances of Commands.
 */
public class CommandFactory {
    //CHECKSTYLE.OFF: VisibilityModifier - No way around this with Dagger...
    @Inject Provider<AddBlockCommand> addBlockCommandProvider;
    @Inject Provider<AddPhaseCommand> addPhaseCommandProvider;
    @Inject Provider<AddSpineCommand> addSpineCommandProvider;
    @Inject Provider<CompoundCommand> compoundCommandProvider;
    @Inject Provider<CreateCharacterCommand> createCharacterCommandProvider;
    @Inject Provider<CreateInstanceCommand> createInstanceCommandProvider;
    @Inject Provider<CreateLayerCommand> createLayerCommandProvider;
    @Inject Provider<CreateSceneInstanceCommand> createSceneInstanceCommandProvider;
    @Inject Provider<CreateSceneCommand> createSceneCommandProvider;
    @Inject Provider<DeleteLayerCommand> deleteLayerCommandProvider;
    @Inject Provider<DeletePhaseCommand> deletePhaseCommandProvider;
    @Inject Provider<DeleteSceneCommand> deleteSceneCommandProvider;
    @Inject Provider<DeleteSpineCommand> deleteSpineCommandProvider;
    @Inject Provider<DrawFontCommand> drawFontCommandProvider;
    @Inject Provider<DrawGeometryCommand> drawShapeCommandProvider;
    @Inject Provider<DrawCustomGeometryCommand> drawCustomGeometryCommandProvider;
    @Inject Provider<DrawEraserCommand> drawEraserCommandProvider;
    @Inject Provider<DrawStrokeCommand> drawStrokeCommandProvider;
    @Inject Provider<ImportDodleCommand> importDodleCommandProvider;
    @Inject Provider<MergeCommand> mergeCommandProvider;
    @Inject Provider<ModifyEffectCommand> modifyEffectCommandProvider;
    @Inject Provider<TransformActorCommand> transformActorCommandProvider;
    @Inject Provider<DeleteObjectCommand> deleteObjectCommandProvider;
    @Inject Provider<FlipCommand> flipCommandProvider;
    @Inject Provider<UpdateStrokeConfigCommand> updateStrokeConfigCommandProvider;
    @Inject Provider<LockCommand> lockCommandProvider;
    @Inject Provider<CopyCommand> copyCommandProvider;
    @Inject Provider<SvgImportCommand> svgImportCommandProvider;
    @Inject Provider<UpdateBlockEffectsCommand> updateBlockEffectsCommand;
    @Inject Provider<UpdatePhaseSchemaCommand> updatePhaseSchemaCommand;
    @Inject Provider<UpdatePhaseValuesCommand> updatePhaseValuesCommand;
    @Inject Provider<ZIndexCommand> zIndexCommandProvider;
    @Inject Provider<ImageImportCommand> imageImportCommandProvider;
    @Inject Provider<ReorderLayersCommand> reorderLayersCommandProvider;
    @Inject Provider<ReorderScenesCommand> reorderScenesCommandProvider;
    @Inject Provider<EditLayerCommand> editLayerCommand;
    @Inject Provider<EditSceneCommand> editSceneCommand;
    //CHECKSTYLE.ON: VisibilityModifier
    
    @Inject
    public CommandFactory(CommandManager cm) {
        cm.registerCommandFactory(this);
    }
    
    /**
     * Creates the command with the given name.
     */
    public final Command createCommand(String commandName) {
        // There's gotta be a better way to do this...
        if (commandName.equals(AddBlockCommand.COMMAND_NAME)) {
            return addBlockCommandProvider.get();
        } else if (commandName.equals(AddPhaseCommand.COMMAND_NAME)) {
            return addPhaseCommandProvider.get();
        } else if (commandName.equals(AddSpineCommand.COMMAND_NAME)) {
            return addSpineCommandProvider.get();
        } else if (commandName.equals(CompoundCommand.COMMAND_NAME)) {
            return compoundCommandProvider.get();
        } else if (commandName.equals(CreateCharacterCommand.COMMAND_NAME)) {
            return createCharacterCommandProvider.get();
        } else if (commandName.equals(CreateInstanceCommand.COMMAND_NAME)) {
            return createInstanceCommandProvider.get();
        } else if (commandName.equals(CreateLayerCommand.COMMAND_NAME)) {
            return createLayerCommandProvider.get();
        } else if (commandName.equals(CreateSceneInstanceCommand.COMMAND_NAME)) {
            return createSceneInstanceCommandProvider.get();
        } else if (commandName.equals(CreateSceneCommand.COMMAND_NAME)) {
            return createSceneCommandProvider.get();
        } else if (commandName.equals(DeleteLayerCommand.COMMAND_NAME)) {
            return deleteLayerCommandProvider.get();
        } else if (commandName.equals(DeletePhaseCommand.COMMAND_NAME)) {
            return deletePhaseCommandProvider.get();
        } else if (commandName.equals(DeleteSceneCommand.COMMAND_NAME)) {
            return deleteSceneCommandProvider.get();
        } else if (commandName.equals(DeleteSpineCommand.COMMAND_NAME)) {
            return deleteSpineCommandProvider.get();
        } else if (commandName.equals(DrawEraserCommand.COMMAND_NAME)) {
            return drawEraserCommandProvider.get();
        } else if (commandName.equals(DrawFontCommand.COMMAND_NAME)) {
            return drawFontCommandProvider.get();
        } else if (commandName.equals(DrawGeometryCommand.COMMAND_NAME)) {
            return drawShapeCommandProvider.get();
        } else if (commandName.equals(DrawCustomGeometryCommand.COMMAND_NAME)) {
            return drawCustomGeometryCommandProvider.get();
        } else if (commandName.equals(DrawStrokeCommand.COMMAND_NAME)) {
            return drawStrokeCommandProvider.get();
        } else if (commandName.equals(MergeCommand.COMMAND_NAME)) {
            return mergeCommandProvider.get();
        } else if (commandName.equals(ModifyEffectCommand.COMMAND_NAME)) {
            return modifyEffectCommandProvider.get();
        } else if (commandName.equals(TransformActorCommand.COMMAND_NAME)) {
            return transformActorCommandProvider.get();
        } else if (commandName.equals(DeleteObjectCommand.COMMAND_NAME)) {
            return deleteObjectCommandProvider.get();
        } else if (commandName.equals(FlipCommand.COMMAND_NAME)) {
            return flipCommandProvider.get();
        } else if (commandName.equals(UpdateStrokeConfigCommand.COMMAND_NAME)) {
            return updateStrokeConfigCommandProvider.get();
        } else if (commandName.equals(CopyCommand.COMMAND_NAME)) {
            return copyCommandProvider.get();
        } else if (commandName.equals(LockCommand.COMMAND_NAME)) {
            return lockCommandProvider.get();
        } else if (commandName.equals(SvgImportCommand.COMMAND_NAME)) {
            return svgImportCommandProvider.get();
        } else if (commandName.equals(UpdateBlockEffectsCommand.COMMAND_NAME)) {
            return updateBlockEffectsCommand.get();
        } else if (commandName.equals(UpdatePhaseSchemaCommand.COMMAND_NAME)) {
            return updatePhaseSchemaCommand.get();
        } else if (commandName.equals(UpdatePhaseValuesCommand.COMMAND_NAME)) {
            return updatePhaseValuesCommand.get();
        } else if (commandName.equals(ZIndexCommand.COMMAND_NAME)) {
            return zIndexCommandProvider.get();
        } else if (commandName.equals(ImageImportCommand.COMMAND_NAME)) {
            return imageImportCommandProvider.get();
        } else if (commandName.equals(ImportDodleCommand.COMMAND_NAME)) {
            return importDodleCommandProvider.get();
        } else if (commandName.equals(ReorderLayersCommand.COMMAND_NAME)) {
            return reorderLayersCommandProvider.get();
        } else if (commandName.equals(ReorderScenesCommand.COMMAND_NAME)) {
            return reorderScenesCommandProvider.get();
        } else if (commandName.equals(EditLayerCommand.COMMAND_NAME)) {
            return editLayerCommand.get();
        } else if (commandName.equals(EditSceneCommand.COMMAND_NAME)) {
            return editSceneCommand.get();
        }

        return null;
    }
}
