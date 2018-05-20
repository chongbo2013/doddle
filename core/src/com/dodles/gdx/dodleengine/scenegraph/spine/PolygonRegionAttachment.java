package com.dodles.gdx.dodleengine.scenegraph.spine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.FrameBufferRenderer;
import com.dodles.gdx.dodleengine.scenegraph.Spine;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.BoneData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.RegionAttachment;

import java.util.HashSet;

/**
 * An attachment that uses a PolygonSpriteBatch to draw regions using textured triangles.
 */
public class PolygonRegionAttachment extends RegionAttachment {
    private final Spine spine;
    private DodlesActor source;
    private Vector2 p1, p2, p3, p4;
    private float finalizedRotation;
    private PolygonRegion t1, t2;
    private TextureRegion blankRegion;
    private AtlasRegion atlasRegion;
    private TextureRegion rotatedRegion;

    /**
     * Get rotated pixmap.
     *
     * @return
     */
    public final TextureRegion getRotatedRegion() {
        return rotatedRegion;
    }

    /**
     * Get rotation of texture when it was finalized.
     *
     * @return
     */
    public final float getFinalizedRotation() {
        return finalizedRotation;
    }


    /**
     * Return original spine atlas region.
     *
     * @return
     */
    public final AtlasRegion getAtlasRegion() {
        return atlasRegion;
    }


//    private float slotRotation;


//    /**
//     * Return crop region under skeleton.
//     *
//     * @return
//     */
//    public final TextureRegion getCropRegion() {
////        if(skeleton!=null &&slot!=null &&source!=null){
////            finalize(skeleton,slot);
////        }
//        return cropRegion;
//    }

//    @Override
//    public final float getWidth() {
//        if (widthOverride > 0) {
//            return widthOverride;
//        }
//        return super.getWidth();
//    }
//
//    //
//    @Override
//    public final float getHeight() {
//        if (heightOverride > 0) {
//            return heightOverride;
//        }
//
//        return super.getHeight();
//    }
//
//    @Override
//    public final float getX() {
//        if (xOverride != 0) {
//            return xOverride;
//        }
//        return super.getX();
//    }
//
//    @Override
//    public final float getY() {
//        if (xOverride != 0) {
//            return 0;
//        }
//        return super.getY();
//    }
//
//    @Override
//    public final float getRotation() {
//        if (xOverride != 0) {
//            return 0;
//        }
//        return super.getRotation();
//    }

    //    @Override
//    public final float getScaleX() {
//        if (overRide) {
//            return 1;
//        }
//        return super.getScaleX();
//    }
//
//    ////
    @Override
    public final float getScaleY() {
//        if (overRide) {
//            return 1;
//        }
        return -super.getScaleY();
    }

    public PolygonRegionAttachment(String name, Spine spine, DodlesActor source, AtlasRegion region) {
        super(name);
        this.spine = spine;
        this.source = source;
        this.atlasRegion = region;
        setAtlasRegion();

    }

    /**
     * Draws the region to the batch at the given vertices.
     */
    public final void draw(PolygonSpriteBatch batch, float[] outputVertices) {
        Vector2 op1 = new Vector2(outputVertices[Batch.X1], outputVertices[Batch.Y1]);
        Vector2 op2 = new Vector2(outputVertices[Batch.X2], outputVertices[Batch.Y2]);
        Vector2 op3 = new Vector2(outputVertices[Batch.X3], outputVertices[Batch.Y3]);
        Vector2 op4 = new Vector2(outputVertices[Batch.X4], outputVertices[Batch.Y4]);

        Matrix4 originalTransform = batch.getTransformMatrix().cpy();
        Matrix4 pt = calculateTransformMatrix(p1, p2, p3, op1, op2, op3);
        if (pt != null) {
            batch.setTransformMatrix(originalTransform.cpy().mul(pt));
            batch.draw(t1, 0, 0);
        }
        pt = calculateTransformMatrix(p1, p3, p4, op1, op3, op4);
        if (pt != null) {
            batch.setTransformMatrix(originalTransform.cpy().mul(pt));
            batch.draw(t2, 0, 0);
        }

        batch.setTransformMatrix(originalTransform);
    }

//    /**
//     * Calculate bounding box of this attachment.
//     *
//     * @param slot
//     */
//    public final void calculateBoundsAdj(Slot slot) {
//        BoneData data = slot.getBone().getData();
//        float length = data.getLength();
//        float boneStartRad = spine.getJointRadius().get(data.getName());
//        float boneEndRad = spine.getJointRadius().get(data.getName() + Spine.CHILD_SUFFIX);
//        calculateBoundsAdj(length, boneStartRad, boneEndRad, data.getScaleX(), data.getScaleY());
//    }
//
//    /**
//     * Calculate bounding box of this attachment.
//     */
//    private void calculateBoundsAdj(float length, float boneStartRad, float boneEndRad, float scaleX, float scaleY) {
//        // Width is bone length, height is bone thickness
//        widthOverride = (boneStartRad + boneEndRad + length) / scaleX;
//        heightOverride = Math.max(boneStartRad, boneEndRad) * 2.f / scaleY;
//        xOverride = widthOverride / 2f - boneStartRad; // WHAT IS THE RIGHT VALUE FOR THIS????!?!?!
//    }


    /**
     * Debug blacken edges of rectangle.
     */
    public final void blackenEdges(Pixmap p, int levels) {
        p.setColor(new Color(0, 0, 0, .6f));
        for (int level = 0; level < levels; level++) {
            for (int i = 0; i < p.getWidth(); i++) {
                p.drawPixel(i, level);
                p.drawPixel(i, p.getHeight() - (1 + level));
            }
            for (int i = 0; i < p.getHeight(); i++) {
                p.drawPixel(level, i);
                p.drawPixel(p.getWidth() - (1 + level), i);
            }
        }
    }

    /**
     * Finalizes the source coordinates after the skeleton has been created.
     */
    public final void finalize(Skeleton skeleton, Slot slot) {
//        this.skeleton=skeleton;
//        this.slot=slot;


        if (source == null) {
            rotatedRegion = null;

            setAtlasRegion();
//            return new Vector2[]{p1, p2, p3, p4};

        } else {
            float slrot = 0;
            Bone tempBone = slot.getBone();
            while (tempBone != null) {
                slrot += tempBone.getRotation();
                tempBone = tempBone.getParent();
            }
            finalizedRotation = slrot;
            calculateBoundsAdj(slot, spine);
            updateOffset();
            float[] vertices = ((RegionAttachment) slot.getAttachment()).updateWorldVertices(slot, false);
            p1 = new Vector2(vertices[Batch.X1], vertices[Batch.Y1]);
            p2 = new Vector2(vertices[Batch.X2], vertices[Batch.Y2]);
            p3 = new Vector2(vertices[Batch.X3], vertices[Batch.Y3]);
            p4 = new Vector2(vertices[Batch.X4], vertices[Batch.Y4]);

            p1 = CommonActorOperations.localToLocalCoordinates(spine, source, p1);
            p2 = CommonActorOperations.localToLocalCoordinates(spine, source, p2);
            p3 = CommonActorOperations.localToLocalCoordinates(spine, source, p3);
            p4 = CommonActorOperations.localToLocalCoordinates(spine, source, p4);


            Rectangle drawBounds = source.getDrawBounds();
            p1.x -= drawBounds.x;
            p1.y -= drawBounds.y;

            p2.x -= drawBounds.x;
            p2.y -= drawBounds.y;

            p3.x -= drawBounds.x;
            p3.y -= drawBounds.y;

            p4.x -= drawBounds.x;
            p4.y -= drawBounds.y;
            int minX = (int) Math.min(p4.x, Math.min(p1.x, Math.min(p2.x, p3.x)));
            int maxX = (int) Math.max(p4.x, Math.max(p1.x, Math.max(p2.x, p3.x)));
            int minY = (int) Math.min(p4.y, Math.min(p1.y, Math.min(p2.y, p3.y)));
            int maxY = (int) Math.max(p4.y, Math.max(p1.y, Math.max(p2.y, p3.y)));

            int w = maxX - minX;
            int h = maxY - minY;

            float w1 = ((RegionAttachment) slot.getAttachment()).getWidth();
            float h1 = ((RegionAttachment) slot.getAttachment()).getHeight();

            System.out.println(slot.getBone().getData().getName() + "\t" + "w=" + w + "\t" + "h=" + h + "w1=" + w1 + "\t" + "h1=" + h1);
//            this.slotRotation = slot.getBone().getRotation();

            Pixmap selectedPixmap = null;


            Pixmap tempPixmap = FrameBufferRenderer.renderToPixmap(source, minX, minY, w, h);
//            System.out.println(w+","+h+"\t"+tempPixmap.getWidth()+","+tempPixmap.getHeight());
//            Pixmap tempPixmap = FrameBufferRenderer.renderToPixmap(source, minX, minY, w, h,((PolygonRegionAttachment) slot.getAttachment()).getSlotRotation());

            Pixmap npix = new Pixmap(w, h, Pixmap.Format.RGBA8888);
            Texture tex = new Texture(FrameBufferRenderer.getPowerOfTwo(w), FrameBufferRenderer.getPowerOfTwo(h), Pixmap.Format.RGBA8888);
//            TextureRegion cropRegion = new TextureRegion(tex, w, h);
//            cropRegion.flip(false, true);

            //erase here

            if (skeleton != null) {

                Array<Slot> slots = skeleton.getSlots();
                String slotName = slot.getBone().getData().getName();
                for (Slot s : slots) {
                    String name1 = s.getBone().getData().getName();
                    if (name1.equals(slotName)) {
                        HashSet<Vector2> erasePixels = new HashSet<Vector2>();
                        HashSet<Vector2> keepPixels = new HashSet<Vector2>();
                        roundEdges(s, erasePixels, keepPixels);
                        if (keepPixels.size() < erasePixels.size()) {
                            npix.setColor(Color.CLEAR);
//                        npix.setColor(0, 1, 0, 0.25f); //debug color
                            npix.fill();

                            for (Vector2 vv : keepPixels) {
                                npix.drawPixel((int) vv.x, (int) vv.y, tempPixmap.getPixel((int) vv.x, (int) vv.y));
                            }
                            //blackenEdges(npix, 2);
//                            cropRegion.getTexture().draw(npix, 0, 0);
                            selectedPixmap = npix;
                        } else {
                            tempPixmap.setColor(Color.CLEAR);
//                        tempPixmap.setColor(1, 0, 0, 0.25f); //debug color
                            for (Vector2 vv : erasePixels) {
                                tempPixmap.drawPixel((int) vv.x, (int) vv.y);
                            }
                            //blackenEdges(tempPixmap, 2);
//                            cropRegion.getTexture().draw(tempPixmap, 0, 0);
                            selectedPixmap = tempPixmap;
                        }
                    }
                }
                Pixmap tempPixmap1 = FrameBufferRenderer.renderToPixmap(selectedPixmap, slot);
//                blackenEdges(tempPixmap1,5);
                this.rotatedRegion = new TextureRegion(new Texture(tempPixmap1));


            }

//            p1.x -= minX;
//            p1.y -= minY;
//
//            p2.x -= minX;
//            p2.y -= minY;
//
//            p3.x -= minX;
//            p3.y -= minY;
//
//            p4.x -= minX;
//            p4.y -= minY;

            //right side up?
            p1 = new Vector2(getWidth(), 0);
            p2 = new Vector2(0, 0);
            p3 = new Vector2(0, getHeight());
            p4 = new Vector2(getWidth(), getHeight());

            float[] t1Vertices = new float[]{
                    p1.x, p1.y,
                    p2.x, p2.y,
                    p3.x, p3.y
            };
            t1 = new PolygonRegion(rotatedRegion, t1Vertices, new short[]
                    {
                            0, 1, 2
                    });

            float[] t2Vertices = new float[]{
                    p1.x, p1.y,
                    p3.x, p3.y,
                    p4.x, p4.y
            };
            t2 = new PolygonRegion(rotatedRegion, t2Vertices, new short[]
                    {
                            0, 1, 2
                    });
//            return new Vector2[]{p1, p2, p3, p4};
        }

    }

    private void setAtlasRegion() {
        //upside down
        p1 = new Vector2(atlasRegion.getRegionWidth(), atlasRegion.getRegionHeight());
        p2 = new Vector2(0, atlasRegion.getRegionHeight());
        p3 = new Vector2(0, 0);
        p4 = new Vector2(atlasRegion.getRegionWidth(), 0);

        //right side up?
//            p1 = new Vector2(getWidth(), 0);
//            p2 = new Vector2(0, 0);
//            p3 = new Vector2(0, getHeight());
//            p4 = new Vector2(getWidth(), getHeight());
//
        float[] t1Vertices = new float[]{
                p1.x, p1.y,
                p2.x, p2.y,
                p3.x, p3.y
        };
        t1 = new

                PolygonRegion(atlasRegion, t1Vertices, new short[]

                {
                        0, 1, 2
                });

        float[] t2Vertices = new float[]{
                p1.x, p1.y,
                p3.x, p3.y,
                p4.x, p4.y
        };
        t2 = new

                PolygonRegion(atlasRegion, t2Vertices, new short[]

                {
                        0, 1, 2
                });

    }


    /**
     * Magic function that creates a transform matrix that will move triangle S to triangle D.
     * http://tulrich.com/geekstuff/canvas/jsgl.js
     */
    private Matrix4 calculateTransformMatrix(Vector2 s0, Vector2 s1, Vector2 s2, Vector2 d0, Vector2 d1, Vector2 d2) {
        /*
            ctx.transform(m11, m12, m21, m22, dx, dy) sets the context transform matrix.

            The context matrix is:

            [ a:m11 b:m21 dx ]
            [ c:m12 d:m22 dy ]
            [  0   0   1 ]

            Coords are column vectors with a 1 in the z coord, so the transform is:
            x_out = m11 * x + m21 * y + dx;
            y_out = m12 * x + m22 * y + dy;

            From Maxima, these are the transform values that map the source
            coords to the dest coords:

            sy0 (x2 - x1) - sy1 x2 + sy2 x1 + (sy1 - sy2) x0
            [m11 = - -----------------------------------------------------,
            sx0 (sy2 - sy1) - sx1 sy2 + sx2 sy1 + (sx1 - sx2) sy0

            sy1 y2 + sy0 (y1 - y2) - sy2 y1 + (sy2 - sy1) y0
            m12 = -----------------------------------------------------,
            sx0 (sy2 - sy1) - sx1 sy2 + sx2 sy1 + (sx1 - sx2) sy0

            sx0 (x2 - x1) - sx1 x2 + sx2 x1 + (sx1 - sx2) x0
            m21 = -----------------------------------------------------,
            sx0 (sy2 - sy1) - sx1 sy2 + sx2 sy1 + (sx1 - sx2) sy0

            sx1 y2 + sx0 (y1 - y2) - sx2 y1 + (sx2 - sx1) y0
            m22 = - -----------------------------------------------------,
            sx0 (sy2 - sy1) - sx1 sy2 + sx2 sy1 + (sx1 - sx2) sy0

            sx0 (sy2 x1 - sy1 x2) + sy0 (sx1 x2 - sx2 x1) + (sx2 sy1 - sx1 sy2) x0
            dx = ----------------------------------------------------------------------,
            sx0 (sy2 - sy1) - sx1 sy2 + sx2 sy1 + (sx1 - sx2) sy0

            sx0 (sy2 y1 - sy1 y2) + sy0 (sx1 y2 - sx2 y1) + (sx2 sy1 - sx1 sy2) y0
            dy = ----------------------------------------------------------------------]
            sx0 (sy2 - sy1) - sx1 sy2 + sx2 sy1 + (sx1 - sx2) sy0
            */
        if (s0 == null || s1 == null || s2 == null || d0 == null || d1 == null || d2 == null) {
            return null;
        }
        float denom = s0.x * (s2.y - s1.y) - s1.x * s2.y + s2.x * s1.y + (s1.x - s2.x) * s0.y;
        if (denom != 0) {
            Matrix4 result = new Matrix4();
            float[] values = result.getValues();

            values[0] = -(s0.y * (d2.x - d1.x) - s1.y * d2.x + s2.y * d1.x + (s1.y - s2.y) * d0.x) / denom; // m11
            values[1] = (s1.y * d2.y + s0.y * (d1.y - d2.y) - s2.y * d1.y + (s2.y - s1.y) * d0.y) / denom; // m12
            values[4] = (s0.x * (d2.x - d1.x) - s1.x * d2.x + s2.x * d1.x + (s1.x - s2.x) * d0.x) / denom; // m21
            values[5] = -(s1.x * d2.y + s0.x * (d1.y - d2.y) - s2.x * d1.y + (s2.x - s1.x) * d0.y) / denom; // m22
            values[12] = (s0.x * (s2.y * d1.x - s1.y * d2.x) + s0.y * (s1.x * d2.x - s2.x * d1.x) + (s2.x * s1.y - s1.x * s2.y) * d0.x) / denom; // dx
            values[13] = (s0.x * (s2.y * d1.y - s1.y * d2.y) + s0.y * (s1.x * d2.y - s2.x * d1.y) + (s2.x * s1.y - s1.x * s2.y) * d0.y) / denom; // dy

            return result;
        }

        return null;
    }

    private void roundEdges(Slot slot, HashSet<Vector2> erasePixels, HashSet<Vector2> keepPixels) {
        Vector2[] vertices = new Vector2[4];
        float[] outputVertices = ((RegionAttachment) slot.getAttachment()).updateWorldVertices(slot, true);
        vertices[0] = new Vector2(outputVertices[Batch.X1], outputVertices[Batch.Y1]);
        vertices[1] = new Vector2(outputVertices[Batch.X2], outputVertices[Batch.Y2]);
        vertices[2] = new Vector2(outputVertices[Batch.X3], outputVertices[Batch.Y3]);
        vertices[3] = new Vector2(outputVertices[Batch.X4], outputVertices[Batch.Y4]);

        vertices[0] = CommonActorOperations.localToLocalCoordinates(spine, source, vertices[0]);
        vertices[1] = CommonActorOperations.localToLocalCoordinates(spine, source, vertices[1]);
        vertices[2] = CommonActorOperations.localToLocalCoordinates(spine, source, vertices[2]);
        vertices[3] = CommonActorOperations.localToLocalCoordinates(spine, source, vertices[3]);

        Rectangle drawBounds = source.getDrawBounds();
        vertices[0].x -= drawBounds.x;
        vertices[0].y -= drawBounds.y;

        vertices[1].x -= drawBounds.x;
        vertices[1].y -= drawBounds.y;

        vertices[2].x -= drawBounds.x;
        vertices[2].y -= drawBounds.y;

        vertices[3].x -= drawBounds.x;
        vertices[3].y -= drawBounds.y;

        int minX = (int) Math.min(vertices[0].x, Math.min(vertices[1].x, Math.min(vertices[2].x, vertices[3].x)));
        int maxX = (int) Math.max(vertices[0].x, Math.max(vertices[1].x, Math.max(vertices[2].x, vertices[3].x)));
        int minY = (int) Math.min(vertices[0].y, Math.min(vertices[1].y, Math.min(vertices[2].y, vertices[3].y)));
        int maxY = (int) Math.max(vertices[0].y, Math.max(vertices[1].y, Math.max(vertices[2].y, vertices[3].y)));

        Vector2 boneStart = new Vector2(slot.getBone().getWorldX(), slot.getBone().getWorldY());
        boneStart = CommonActorOperations.localToLocalCoordinates(spine, source, boneStart);

        Bone parent = slot.getBone();
        float xoff = parent.getData().getLength() * parent.getA() + parent.getWorldX();
        float yoff = parent.getData().getLength() * parent.getC() + parent.getWorldY();
        Vector2 boneEnd = new Vector2(xoff, yoff);
        boneEnd = CommonActorOperations.localToLocalCoordinates(spine, source, boneEnd);

        float boneStartRad = spine.getJointRadius().get(parent.getData().getName());
        float boneEndRad = spine.getJointRadius().get(parent.getData().getName() + Spine.CHILD_SUFFIX);

        boneStart.x -= drawBounds.x;
        boneStart.y -= drawBounds.y;
        boneEnd.x -= drawBounds.x;
        boneEnd.y -= drawBounds.y;

        Vector2[] tangents = SkeletonRendererDebug.calculateTangent(boneStart, boneStartRad, boneEnd, boneEndRad);
        Polygon tangentPolygon = new Polygon(new float[]{tangents[0].x, tangents[0].y, tangents[1].x, tangents[1].y, tangents[3].x, tangents[3].y, tangents[2].x, tangents[2].y});


        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Vector2 vv = new Vector2(x, y);
                Vector2 vadd = new Vector2(x - minX, y - minY);
                boolean eraseFlag = true;
                if ((vv.cpy().sub(boneStart).len() < boneStartRad || vv.cpy().sub(boneEnd).len() < boneEndRad || tangentPolygon.contains(vv))) {
                    eraseFlag = false;
                }

                if (eraseFlag) {
                    erasePixels.add(vadd);
                } else {
                    keepPixels.add(vadd);
                }
            }
        }
    }

    /**
     * Calculate bounding box of this attachment.
     */
    public final void calculateBoundsAdj(Slot slot, Spine spine1) {
        BoneData data = slot.getBone().getData();
        float length = data.getLength();
        float boneStartRad = spine1.getJointRadius().get(data.getName());
        float boneEndRad = spine1.getJointRadius().get(data.getName() + Spine.CHILD_SUFFIX);
        float widthOverride = (boneStartRad + boneEndRad + length) / data.getScaleX();
        float heightOverride = (Math.max(boneEndRad, boneStartRad) * 2f) / data.getScaleY();
        float xOverride = widthOverride / 2f - boneStartRad;
        if (widthOverride < 0) {
            widthOverride = -widthOverride;
        }
        if (heightOverride < 0) {
            heightOverride = -heightOverride;
        }
        //setX(xOverride);
        //setWidth(widthOverride);
        //setHeight(heightOverride);
        //setRotation(0);
    }


    @Override
    public final TextureRegion getRegion() {
        if (rotatedRegion != null) {
            return rotatedRegion;
        }
        if (atlasRegion != null) {
            return atlasRegion;
        }
        return getBlankRegion();
    }


    /**
     * Returns an "outline" texture to use in place of spine regions.
     */
    public static Texture getTangentTexture(int w, int h, int border, Color foreColor, Color backColor) {
        Pixmap pix = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pix.setColor(backColor);
        pix.fill();
        pix.setColor(foreColor);
        for (int i = 0; i < Math.min(Math.min(w, h), border); i++) {
            pix.drawRectangle(i, i, w - i * 2, h - i * 2);
        }
        return new Texture(pix);
    }

    /**
     * Generate and return a blank boundary texture with outline.
     *
     * @return
     */
    public final TextureRegion getBlankRegion() {
        if (blankRegion == null || blankRegion.getRegionWidth() != getWidth() || blankRegion.getRegionHeight() != getHeight()) {
            Texture blankTexture = getTangentTexture((int) getWidth(), (int) getHeight(), 2, new Color(0, 1, 1, 1), Color.CLEAR);
            blankRegion = new TextureRegion(blankTexture);
        }
        return blankRegion;
    }

    /**
     * Set source to grab textures from.
     *
     * @param source
     */
    public final void setSource(DodlesActor source) {
        this.source = source;
    }

}
