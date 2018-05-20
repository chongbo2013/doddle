package com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.settings;

import javax.inject.Inject;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.commands.Command;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.CompoundCommand;
import com.dodles.gdx.dodleengine.commands.CopyCommand;
import com.dodles.gdx.dodleengine.commands.MergeCommand;
import com.dodles.gdx.dodleengine.commands.phase.AddPhaseCommand;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.phase.PhaseUIStates;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;
import de.hypergraphs.hyena.core.shared.data.UUID;
import java.util.ArrayList;

/**
 * UI for the left panel to manage New PhaseSchema settings.
 */
@PerDodleEngine
public class NewPhaseSettingsView  {
    private final AssetProvider assetProvider;
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    
    private Table phasesUISettings;
    private Table newPhaseRootTable;


    @Inject
    public NewPhaseSettingsView(
        AssetProvider assetProvider,
        CommandFactory commandFactory,
        CommandManager commandManager,
        EngineEventManager eventManager,
        ObjectManager objectManager
    ) {
        this.assetProvider = assetProvider;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.eventManager = eventManager;
        this.objectManager = objectManager;
    }

    /**
     * Initializes the view.
     */
    public final void initialize(Table rootTable, Skin skin) {
        LmlParser parser = VisLml.parser()
                .skin(skin)
                .build();

        this.phasesUISettings = rootTable;

        String template = assetProvider.getString(StringAssets.TEMPLATE_FULL_EDITOR_PHASES_SETTING_ADDPHASE_VIEW);
        newPhaseRootTable = (Table) parser.parseTemplate(template).get(0);

        phasesUISettings.top();
        phasesUISettings.add(newPhaseRootTable).expandX().fillX();
        
        TextButton blankPhaseButton = newPhaseRootTable.findActor("blankPhase");
        blankPhaseButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                addBlankPhase();
            }
        });
        
        TextButton copyPhaseButton = newPhaseRootTable.findActor("copyPhase");
        copyPhaseButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                addCopyPhase();
            }
        });
    }
    
    /**
     * remove the newPhase UI from the Settings Panel.
     */
    public final void resetUI() {
        if (newPhaseRootTable != null) {
            phasesUISettings.clearChildren();
            newPhaseRootTable = null;
            phasesUISettings.invalidate();
        }
    }
    
    /**
     * Adds a new, blank phase.
     */
    private void addBlankPhase() {
        addPhase(null);
    }
    
    /**
     * Adds a new phase that's a copy of an old phase.
     */
    private void addCopyPhase() {
        DodlesActor selectedActor = objectManager.getSelectedActor();
        String idToCopy = selectedActor.getName();
        
        if (selectedActor instanceof DodlesGroup) {
            DodlesGroup pg = (DodlesGroup) selectedActor;
            idToCopy = pg.getVisiblePhaseID();
        }
        
        addPhase(idToCopy);
    }
    
    private void addPhase(String oldPhaseID) {
        DodlesActor activeLayer = objectManager.getActiveLayer();
        String newPhaseID = UUID.uuid();
        ArrayList<Command> commands = new ArrayList<Command>();

        if (oldPhaseID != null) {
            CopyCommand cc = (CopyCommand) commandFactory.createCommand(CopyCommand.COMMAND_NAME);
            IdDatabase idDB = new IdDatabase();
            cc.init(oldPhaseID, idDB, 0, 0);
            cc.execute();
            commands.add(cc);
            newPhaseID = idDB.getNewID(oldPhaseID);
        }
        
        if (!(activeLayer instanceof DodlesGroup)) {
            // We need to make sure the phase root is a group...
            String newGroupID = UUID.uuid();
            MergeCommand mc = (MergeCommand) commandFactory.createCommand(MergeCommand.COMMAND_NAME);
            ArrayList<String> childIDs = new ArrayList<String>();
            childIDs.add(activeLayer.getName());
            mc.init(newGroupID, null, childIDs, activeLayer.getParentDodlesViewGroup().getName(), activeLayer.getParentViewID(), true);
            mc.execute();
            commands.add(mc);
            
            activeLayer = objectManager.getActor(newGroupID);
        }
        
        DodlesGroup phaseGroup = (DodlesGroup) activeLayer;
        
        if (oldPhaseID == null) {
            phaseGroup.setStencilPhaseID(phaseGroup.getVisiblePhaseID());
        }

        AddPhaseCommand apc = (AddPhaseCommand) commandFactory.createCommand(AddPhaseCommand.COMMAND_NAME);
        apc.init(newPhaseID, phaseGroup.getName());
        apc.execute();
        commands.add(apc);
        
        objectManager.selectActor(newPhaseID);
        objectManager.setActiveLayer(phaseGroup);
        
        if (commands.size() == 1) {
            commandManager.add(apc);
        } else {
            CompoundCommand compound = (CompoundCommand) commandFactory.createCommand(CompoundCommand.COMMAND_NAME);
            compound.init(commands);
            commandManager.add(compound);
        }
        
        phaseGroup.setVisiblePhase(newPhaseID);
        
        eventManager.fireEvent(EngineEventType.PHASEUI_ACTIVE_PHASE_MODIFIED);
        eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_ADVANCED_FOLDER_VIEW.toString());
        eventManager.fireEvent(EngineEventType.PHASEUI_STATE_CHANGED, PhaseUIStates.RELOAD_BASIC_PHASEBUTTONS.toString());
    }
}
