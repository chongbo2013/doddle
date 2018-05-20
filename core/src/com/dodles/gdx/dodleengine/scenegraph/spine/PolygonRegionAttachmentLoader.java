package com.dodles.gdx.dodleengine.scenegraph.spine;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Spine;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.attachments.AttachmentLoader;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.esotericsoftware.spine.attachments.MeshAttachment;
import com.esotericsoftware.spine.attachments.PathAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;

import java.util.ArrayList;

/**
 * Uses a texture generated from rendering an actor as the backing store for generating Spine attachments.
 */
public class PolygonRegionAttachmentLoader implements AttachmentLoader {
    private final Spine spine;
    private TextureAtlas atlas;
    private DodlesActor source;
    private final ArrayList<PolygonRegionAttachment> attachments = new ArrayList<PolygonRegionAttachment>();

    public PolygonRegionAttachmentLoader(Spine spine, DodlesActor source, TextureAtlas textureAtlas) {
        this.spine = spine;
        this.source = source;
        this.atlas = textureAtlas;
    }

    public PolygonRegionAttachmentLoader(Spine spine) {
        this.spine = spine;
    }

    @Override
    public final RegionAttachment newRegionAttachment(Skin skin, String name, String path) {
        TextureAtlas.AtlasRegion region = atlas.findRegion(path);
        PolygonRegionAttachment newAttachment = new PolygonRegionAttachment(name, spine, source, region);
        attachments.add(newAttachment);
        return newAttachment;
    }

    @Override
    public final MeshAttachment newMeshAttachment(Skin skin, String name, String path) {
        ///throw new UnsupportedOperationException("Not supported.");
        return null;
    }

    @Override
    public final BoundingBoxAttachment newBoundingBoxAttachment(Skin skin, String name) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public final PathAttachment newPathAttachment(Skin skin, String name) {
        throw new UnsupportedOperationException("Not supported.");
    }

//    public void setSource(DodlesActor source) {
//        this.source = source;
//        for (PolygonRegionAttachment pra:attachments){
//            pra.setSource(source);
//        }
//    }
}
