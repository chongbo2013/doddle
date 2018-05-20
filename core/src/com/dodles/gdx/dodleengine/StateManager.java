package com.dodles.gdx.dodleengine;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.editor.EditorInterfaceManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActorFactory;
import com.dodles.gdx.dodleengine.scenegraph.ProcessAfterLoad;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Manages dodle state, specifically managing loading, saving and resetting the dodle data.
 */
@PerDodleEngine
public class StateManager {
    private final CameraManager cameraManager;
    private final CommandManager commandManager;
    private final DodlesActorFactory actorFactory;
    private final ObjectManager objectManager;
    private final AnimationManager animationManager;
    private final DodleStageManager stageManager;
    private final EditorInterfaceManager editorInterfaceManager;
    private final EventBus eventBus;
    private final FrameBufferAtlasManager fbaManager;
    private final OkCancelStackManager okCancelStack;
    private final ToolRegistry toolRegistry;

    private String rootSceneID;
    private String rootBlockID;

    public static final String AUTO_SAVE_PREFERENCE = "dodles.autosave";
    public static final String AUTO_SAVE_PROPERTY = "commandstack";

    @Inject
    public StateManager(
        CameraManager cameraManager,
        CommandManager commandManager,
        DodlesActorFactory actorFactory,
        ObjectManager objectManager,
        AnimationManager animationManager,
        DodleStageManager stageManager,
        EditorInterfaceManager editorInterfaceManager,
        EventBus eventBus,
        FrameBufferAtlasManager fbaManager,
        OkCancelStackManager okCancelStack,
        ToolRegistry toolRegistry
    ) {
        this.cameraManager = cameraManager;
        this.commandManager = commandManager;
        this.actorFactory = actorFactory;
        this.objectManager = objectManager;
        this.animationManager = animationManager;
        this.stageManager = stageManager;
        this.editorInterfaceManager = editorInterfaceManager;
        this.eventBus = eventBus;
        this.fbaManager = fbaManager;
        this.okCancelStack = okCancelStack;
        this.toolRegistry = toolRegistry;

        // Handle events from the editor UI
        this.eventBus.addSubscriber(new EventSubscriber(EventTopic.EDITOR) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case DELETE_DODLE:
                        resetState();
                        break;
                }
            }
        });
    }

    /**
     * Imports the given JSON into the DodleEngine.
     */
    public final void importJson(String json) {
        try {
            JsonValue data = new JsonReader().parse(json);
            String format = data.getString("format", "default");
            resetState(data.getString("trackingID"), data.getString("rootSceneID"), data.getString("rootBlockID"), !format.equals("sg1"));
            objectManager.importReferences(data);
            
            if (format.equals("sg1")) {
                actorFactory.importCharacters(null, data);
                actorFactory.importScenes(null, data);
                animationManager.importAnimations(data);
            } else {
                commandManager.importCommands(data);
            }
            
            for (DodlesActor actor : objectManager.allActors()) {
                if (actor instanceof ProcessAfterLoad) {
                    ((ProcessAfterLoad) actor).afterLoad(objectManager);
                }
            }
        } catch (Exception e) {
            resetState();
            DodleEngine.getLogger().error("StateManager", "Error loading dodle...", e);
            eventBus.publish(EventTopic.DEFAULT, EventType.MAKE_TOAST, "Dodle failed to load. :(");
        }
    }
    
    /**
     * Returns JSON representing the current state of the scene graph.
     */
    public final String exportGraphJson() {
        return exportJson(false);
    }

    /**
     * Returns JSON representing the commands used to build the current state of the DodleEngine.
     */
    public final String exportCommandJson() {
        return exportJson(true);
    }
    
    /**
     * Returns a JSON string containing the debug commands for the current dodle.
     */
    public final String exportDebugCommands() {
        StringWriter writer = new StringWriter();
        Json json = new Json(OutputType.json);
        json.setWriter(writer);
        json.setQuoteLongValues(true);

        json.writeObjectStart();
        commandManager.exportDebugStack(json);
        json.writeObjectEnd();

        // because we supplied out own JsonWriter / StringWriter, we need to reach deep into the
        // object model of libgdx.Json
        String retVal = json.getWriter().getWriter().toString();

        //have to close the Writer ourselves;
        try {
            json.getWriter().close();
        } catch (IOException e) {
            return retVal;
        }

        return retVal;
    }
    
    private String exportJson(boolean useCommands) {
        StringWriter writer = new StringWriter();
        Json json = new Json(OutputType.json);
        json.setWriter(writer);
        json.setQuoteLongValues(true);

        json.writeObjectStart();
        
        if (useCommands) {
            commandManager.exportCommands(json);            
        } else {
            json.writeValue("format", "sg1");
            actorFactory.exportCharacters(json);
            actorFactory.exportScenes(json);
            animationManager.exportAnimations(json);
        }

        json.writeValue("trackingID", objectManager.getTrackingID());
        json.writeValue("rootSceneID", rootSceneID);
        json.writeValue("rootBlockID", rootBlockID);
        objectManager.exportReferences(json);
        json.writeObjectEnd();

        // because we supplied out own JsonWriter / StringWriter, we need to reach deep into the
        // object model of libgdx.Json
        String retVal = json.getWriter().getWriter().toString();

        //have to close the Writer ourselves;
        try {
            json.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retVal;
    }
    
    /**
     * Resets to a blank dodle state.
     */
    public final void resetState() {
        resetState(UUID.uuid(), UUID.uuid(), UUID.uuid(), true);
        eventBus.publish(EventTopic.DEFAULT, EventType.HOST_ENGINE_TRASH_STATE);
    }

    private void resetState(String newTrackingID, String newRootSceneID, String newRootBlockID, boolean addRootScene) {
        this.rootSceneID = newRootSceneID;
        this.rootBlockID = newRootBlockID;

        stageManager.resetOverlays();
        editorInterfaceManager.reset();
        okCancelStack.reset();
        objectManager.reset(newTrackingID, newRootSceneID, addRootScene);
        commandManager.reset();
        animationManager.reset(rootSceneID, rootBlockID);
        stageManager.setDisplayMode(DodleStageManager.DisplayMode.NORMAL);
        fbaManager.reset();
        toolRegistry.setActiveTool(null);
        cameraManager.reset();

        eventBus.publish(EventTopic.DEFAULT, EventType.DODLE_ENGINE_STATE_RESET, newTrackingID);
    }

    /**
     * simple helper for now to fire a share event.  It's just a thin wrapper as we don't make the eventManager public.
     */
    public final void fireShareEvent() {
        String json = this.exportGraphJson();
        eventBus.publish(EventTopic.DEFAULT, EventType.HOST_ENGINE_SHARE, json);
    }
}
