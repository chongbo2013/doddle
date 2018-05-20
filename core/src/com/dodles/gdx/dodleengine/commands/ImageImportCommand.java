package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Scene;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.util.JsonUtility;
import com.dodles.gdx.dodleengine.util.PixmapFactory;

import java.util.ArrayList;

import javax.inject.Inject;

import de.hypergraphs.hyena.core.shared.data.UUID;


/**
 * A command that adds an image to a dodle.
 */
public class ImageImportCommand implements Command, Importable {
    public static final String COMMAND_NAME = "image";

    private final AssetProvider assetProvider;
    private final ObjectManager objectManager;
    private final GroupHelper groupHelper;
    private final PixmapFactory pixmapFactory;
    private String file;
    private String groupID;
    private String phaseID;
    private ArrayList<String> ids = new ArrayList<String>();
    private int counter;
    private String rootGroupID;
    private Rectangle textureRegion;

    @Inject
    public ImageImportCommand(AssetProvider assetProvider, ObjectManager objectManager, GroupHelper groupHelper, PixmapFactory pixmapFactory) {
        this.assetProvider = assetProvider;
        this.objectManager = objectManager;
        this.groupHelper = groupHelper;
        this.pixmapFactory = pixmapFactory;
    }

    @Override
    public final String getName() {
        return COMMAND_NAME;
    }

    /**
     * Initializes the command with a base64 PNG.
     */
    public final void init(String pGroupID, String pPhaseID, String pBase64File) {
        this.groupID = pGroupID;
        this.phaseID = pPhaseID;
        this.file = pBase64File;
    }

    /**
     * Initializes the command with a canned TextureAsset.
     */
    public final void init(String pGroupID, String pPhaseID, TextureAssets asset) {
        this.groupID = pGroupID;
        this.phaseID = pPhaseID;
        this.file = ImportedAssetConfig.TEXTURE_PREFIX + asset.name();
    }

    /**
     * Initializes the command with a base64 PNG.
     */
    public final void init(String pGroupID, String pPhaseID, String pBase64File, Rectangle pTextureRegion) {
        init(pGroupID, pPhaseID, pBase64File);
        this.textureRegion = pTextureRegion;
    }


    @Override
    public final void execute() {
        counter = 0;
        DodlesGroup group = new DodlesGroup(getId(), objectManager.getTrackingID());

        Scene hostGroup = (Scene) objectManager.getActor(groupID);
        hostGroup.addActor(group, phaseID);
        objectManager.addActor(group);
        rootGroupID = group.getName();

        Shape shape = new Shape(getId(), objectManager.getTrackingID());

        ImportedAssetConfig iac = new ImportedAssetConfig();

        iac.setFileData(file);
        if (textureRegion != null) {
            iac.setRegion(textureRegion);
        }

        shape.setCustomConfig(iac);

        ImportedAssetConfig.init(shape, assetProvider, pixmapFactory);

        objectManager.addActor(shape);

        groupHelper.addChildToGroup(rootGroupID, null, shape);
    }

    @Override
    public final void undo() {
        groupHelper.removeChildFromGroup(objectManager.getActor(ids.get(0)));
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("ids", ids);
        json.writeValue("groupID", groupID);
        json.writeValue("phaseID", phaseID);
        json.writeValue("file", file);
    }

    @Override
    public final void loadConfig(JsonValue json, CommandFactory factory) {
        ids = JsonUtility.readStringArray(json.get("ids"));
        groupID = json.getString("groupID");
        phaseID = json.getString("phaseID");
        file = json.getString("file");
    }

    /**
     * Get the dodle container that the svg is held in.
     */
    private String getId() {
        if (ids.size() <= counter) {
            ids.add(UUID.uuid());
        }

        return ids.get(counter++);
    }

    @Override
    public final String getObjectID() {
        return rootGroupID;
    }
}
