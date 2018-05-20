package com.dodles.gdx.dodleengine.scenegraph.spine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.BlendMode;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.RegionAttachment;

/**
 * A skeleton renderer that uses polygon batches to allow us to morph textured triangles into
 * the correct positions.
 */
public class PolygonBatchSkeletonRenderer {
    private boolean premultipliedAlpha;

    /**
     * Draws the skeleton to the batch.
     */
    public final void draw(PolygonSpriteBatch batch, Skeleton skeleton, float alpha) {
        BlendMode blendMode = null;
        batch.setColor(1, 1, 1, alpha);

        Array<Slot> drawOrder = skeleton.getDrawOrder();
        for (int i = 0, n = drawOrder.size; i < n; i++) {
            Slot slot = drawOrder.get(i);
            RegionAttachment attachment = (RegionAttachment) slot.getAttachment();
            if (attachment == null) {
                continue;
            }
            float[] vertices = attachment.updateWorldVertices(slot, premultipliedAlpha);
            // TODO: when this code is uncommented, and adding a new blank phase after a
            // phase with a spine, it renders the stencil transparent.  Not sure why. :(
            /*BlendMode slotBlendMode = slot.getData().getBlendMode();
            if (slotBlendMode != blendMode) {
                blendMode = slotBlendMode;
                batch.setBlendFunction(blendMode.getSource(premultipliedAlpha), blendMode.getDest());
            }*/

            if (attachment instanceof PolygonRegionAttachment) {
                ((PolygonRegionAttachment) attachment).draw(batch, vertices);
            } else {
                batch.draw(attachment.getRegion().getTexture(), vertices, 0, 20);
            }
        }

        batch.setColor(Color.WHITE);
    }

    /**
     * Gets or sets whether to use premultiplied alpha.
     */
    public final void setPremultipliedAlpha(boolean newValue) {
        premultipliedAlpha = newValue;
    }
}
