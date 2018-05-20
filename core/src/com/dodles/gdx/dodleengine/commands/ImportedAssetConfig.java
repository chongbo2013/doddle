package com.dodles.gdx.dodleengine.commands;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.TextureAssets;
import com.dodles.gdx.dodleengine.scenegraph.CustomToolConfig;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.direct.DirectTextureGraphics;
import com.dodles.gdx.dodleengine.util.PixmapFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Tool config that saves imported image data to the shape.
 */
public class ImportedAssetConfig implements CustomToolConfig {
    public static final String CONFIG_TYPE = "ImportedAssetConfig";
    public static final String TEXTURE_PREFIX = "texture://";

    private String fileData;
    private Rectangle region;

    /**
     * Creates a new brush configuration from the given JSON.
     */
    public static ImportedAssetConfig create(JsonValue json) {
        if (json.getString("type").equals(CONFIG_TYPE)) {
            return new ImportedAssetConfig(json);
        }

        return null;
    }

    /**
     * Initializes the shape with the asset data.
     */
    public static void init(Shape shape, AssetProvider assetProvider, PixmapFactory pixmapFactory) {
        addShapeGenerator(shape, assetProvider, pixmapFactory);
    }

    /**
     * Update the given shapes generator.
     */
    public static void updateShapeGenerator(Shape shape, AssetProvider assetProvider, PixmapFactory pixmapFactory) {
        shape.clearGenerators();
        addShapeGenerator(shape, assetProvider, pixmapFactory);
        shape.regenerate();
    }

    private static void addShapeGenerator(Shape shape, AssetProvider assetProvider, PixmapFactory pixmapFactory) {
        if (shape.getCustomConfig() instanceof ImportedAssetConfig) {
            ImportedAssetConfig config = (ImportedAssetConfig) shape.getCustomConfig();
            final TextureRegion txt = getTexture(config, assetProvider, pixmapFactory);

            shape.addGenerator(new GraphicsGenerator() {
                @Override
                public List<Graphics> generateGraphics(Shape newShape) {
                    ArrayList<Graphics> result = new ArrayList<Graphics>();
                    result.add(new DirectTextureGraphics(txt.getTexture(), txt.getRegionX(), txt.getRegionY(), txt.getRegionWidth(), txt.getRegionHeight(), txt.getRegionX(), txt.getRegionY(), txt.getRegionWidth(), txt.getRegionHeight(), 0, new Color(0xffffffff)));

                    return result;
                }
            });
        }
    }

    private static TextureRegion getTexture(ImportedAssetConfig config, AssetProvider assetProvider, PixmapFactory pixmapFactory) {
        TextureRegion txt = new TextureRegion();

        Texture texture;
        if (config.getFileData().startsWith(TEXTURE_PREFIX)) {
            texture = assetProvider.getTexture(TextureAssets.valueOf(config.getFileData().replace(TEXTURE_PREFIX, "")));
            setTextureRegion(config, txt, texture);
        } else {
            Pixmap tmpPixmap = pixmapFactory.createPixmapFromBase64String(config.getFileData());
            texture = new Texture(tmpPixmap);
            setTextureRegion(config, txt, texture);
            tmpPixmap.dispose();
        }

        return txt;
    }

    private static void setTextureRegion(ImportedAssetConfig config, TextureRegion txt, Texture texture) {
        txt.setTexture(texture);
        Rectangle region = config.getRegion();
        if (region == null) {
            region = new Rectangle(0, 0, texture.getWidth(), texture.getHeight());
        } else {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        txt.setRegion((int) region.x, (int) region.y, (int) region.width, (int) region.height);
    }

    public ImportedAssetConfig() {
    }

    private ImportedAssetConfig(JsonValue json) {
        fileData = json.getString("fileData");
        if (json.has("x")) {
            region = new Rectangle(
                    json.getFloat("x"),
                    json.getFloat("y"),
                    json.getFloat("width"),
                    json.getFloat("height")
            );
        }
    }

    /**
     * Returns the stringified file data.
     */
    public final String getFileData() {
        return fileData;
    }

    /**
     * Sets the stringified file data.
     */
    public final void setFileData(String newFileData) {
        fileData = newFileData;
    }

    @Override
    public final CustomToolConfig cpy() {
        ImportedAssetConfig result = new ImportedAssetConfig();
        result.fileData = fileData;
        result.region = region;
        return result;
    }

    @Override
    public final String getType() {
        return CONFIG_TYPE;
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeValue("type", CONFIG_TYPE);
        json.writeValue("fileData", fileData);
        if (region != null) {
            json.writeValue("x", region.x);
            json.writeValue("y", region.y);
            json.writeValue("width", region.width);
            json.writeValue("height", region.height);
        }
    }

    /**
     * Set the region of the texture to display.
     */
    public final void setRegion(Rectangle region) {
        this.region = region;
    }

    /**
     * Get the region of the texture to display.
     */
    public final Rectangle getRegion() {
        return this.region;
    }
}
