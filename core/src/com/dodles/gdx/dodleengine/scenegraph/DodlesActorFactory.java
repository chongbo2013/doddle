package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.brushes.BrushRegistry;
import com.dodles.gdx.dodleengine.commands.ImportedAssetConfig;
import com.dodles.gdx.dodleengine.commands.PathConfig;
import com.dodles.gdx.dodleengine.geometry.GeometryRegistry;
import com.dodles.gdx.dodleengine.scenegraph.chest.ChestCharacter;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import com.dodles.gdx.dodleengine.util.PixmapFactory;

import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Factory for creating dodles actors from JSON.
 */
public class DodlesActorFactory {
    private final AssetProvider assetProvider;
    private final BrushRegistry brushRegistry;
    private final DodleStageManager stageManager;
    private final FrameBufferAtlasManager atlasManager;
    private final GeometryRegistry geometryRegistry;
    private final ObjectManager objectManager;
    private final PixmapFactory pixmapFactory;

    private boolean addToObjectManager = false;

    @Inject
    public DodlesActorFactory(
            AssetProvider assetProvider,
            BrushRegistry brushRegistry,
            DodleStageManager stageManager,
            GeometryRegistry geometryRegistry,
            FrameBufferAtlasManager atlasManager,
            ObjectManager objectManager,
            PixmapFactory pixmapFactory
    ) {
        this.assetProvider = assetProvider;
        this.atlasManager = atlasManager;
        this.brushRegistry = brushRegistry;
        this.stageManager = stageManager;
        this.geometryRegistry = geometryRegistry;
        this.objectManager = objectManager;
        this.pixmapFactory = pixmapFactory;
    }

    /**
     * Imports the entire scene graph from JSON.
     */
    public final void importScenes(IdDatabase idDB, JsonValue json) {
        addToObjectManager = true;
        objectManager.clearScenes();
        ArrayList<Scene> scenes = loadScenes(idDB, json);

        for (int i = 0; i < scenes.size(); i++) {
            Scene s = scenes.get(i);
            if (s.getNumber() == null) {
                s.setNumber(i);
            }
            if (s.getDisplayName() == null) {
                s.setDisplayName(i + "");
            }
            objectManager.addScene(s);
        }

        addToObjectManager = false;
    }

    /**
     * Loads scenes into a list without adding to the object manager.
     */
    public final ArrayList<Scene> loadScenes(IdDatabase idDB, JsonValue json) {
        JsonValue jsonScenes = json.get("scenes");
        ArrayList<Scene> result = new ArrayList<Scene>();

        for (int i = 0; i < jsonScenes.size; i++) {
            result.add(new Scene(this, idDB, jsonScenes.get(i)));
        }

        return result;
    }

    /**
     * Exports the scene graph to JSON.
     */
    public final void exportScenes(Json json) {
        json.writeArrayStart("scenes");

        for (ObjectManager.SceneData sceneData : objectManager.allSceneData()) {
            sceneData.getScene().writeConfig(json);
        }

        json.writeArrayEnd();
    }

    /**
     * Imports characters from JSON into the dodle.
     */
    public final void importCharacters(IdDatabase idDB, JsonValue json) {
        JsonValue jsonCharacters = json.get("characters");

        for (int i = 0; i < jsonCharacters.size; i++) {
            ChestCharacter character = new ChestCharacter(this, idDB, jsonCharacters.get(i), objectManager, stageManager);
            objectManager.addToChest(character);
        }
    }

    /**
     * Exports characters from the dodle to JSON.
     */
    public final void exportCharacters(Json json) {
        json.writeArrayStart("characters");

        for (ChestCharacter character : objectManager.allChestCharacters()) {
            character.writeConfig(json);
        }

        json.writeArrayEnd();
    }

    /**
     * Imports a single actor from JSON.
     */
    public final DodlesActor createFromJson(IdDatabase idDB, JsonValue json) {
        String type = json.getString("type");
        DodlesActor result;

        if (type.equals(DodlesGroup.ACTOR_TYPE)) {
            result = new DodlesGroup(this, idDB, json);
        } else if (type.equals(Shape.ACTOR_TYPE)) {
            result = new Shape(json, idDB, this, atlasManager);
            geometryRegistry.init((Shape) result);
            brushRegistry.init((Shape) result);
            ImportedAssetConfig.init((Shape) result, assetProvider, pixmapFactory);
            PathConfig.init((Shape) result);
        } else if (type.equals(Spine.ACTOR_TYPE)) {
            result = new Spine(assetProvider, idDB, json);
        } else if (type.equals(TextShape.ACTOR_TYPE)) {
            result = new TextShape(json, idDB, this, atlasManager, assetProvider);
        } else if (type.equals(LinkActor.ACTOR_TYPE)) {
            result = new LinkActor(json, idDB, objectManager);
        } else if (type.equals(Phase.ACTOR_TYPE)) {
            result = new Phase(this, idDB, json);
        } else if (type.equals(Layer.ACTOR_TYPE)) {
            result = new Layer(this, idDB, json);
        } else {
            throw new UnsupportedOperationException("Unrecognized actor type:" + type);
        }

        if (addToObjectManager) {
            objectManager.addActor(result);
        }

        return result;
    }
}
