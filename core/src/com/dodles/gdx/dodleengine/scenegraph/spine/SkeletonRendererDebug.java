package com.dodles.gdx.dodleengine.scenegraph.spine;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.scenegraph.Spine;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.MeshAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;

import static com.badlogic.gdx.graphics.g2d.Batch.X1;
import static com.badlogic.gdx.graphics.g2d.Batch.X2;
import static com.badlogic.gdx.graphics.g2d.Batch.X3;
import static com.badlogic.gdx.graphics.g2d.Batch.X4;
import static com.badlogic.gdx.graphics.g2d.Batch.Y1;
import static com.badlogic.gdx.graphics.g2d.Batch.Y2;
import static com.badlogic.gdx.graphics.g2d.Batch.Y3;
import static com.badlogic.gdx.graphics.g2d.Batch.Y4;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

/**
 * Renders Spine skeleton.
 */
public class SkeletonRendererDebug {
    public static final Color BONE_COLOR = new Color(0f, 0f, 0f, 1f);
    public static final Color BONE_ACTIVE_COLOR = new Color(0f, 1f, 0f, 1f);
    private static final Color OTHER_COLOR = Color.CYAN;

    private final ShapeRenderer shapes;
    private final Spine spine;
    private boolean drawBones = true, drawRegionAttachments = false, drawBoundingBoxes = true;
    private boolean drawMeshHull = false, drawMeshTriangles = false;
    private final SkeletonBounds bounds = new SkeletonBounds();
    private float scale = 1;
    private float boneWidth = 1;
    private boolean premultipliedAlpha = true;
    private boolean colorFlag;

    public SkeletonRendererDebug(Spine spine) {

        shapes = new ShapeRenderer();

        for (Bone b : spine.getEmptySkeleton().getBones()) {
            String name = b.getData().getName();
            spine.getJointRadius().put(name, 10f);
            spine.getJointRadius().put(name + Spine.CHILD_SUFFIX, 10.0001f);
            for (Slot sl : spine.getEmptySkeleton().getSlots()) {
                if (sl.getBone().getData().getName().equals(name)) {
                    if (sl.getAttachment() instanceof PolygonRegionAttachment) {
                        PolygonRegionAttachment ra = ((PolygonRegionAttachment) sl.getAttachment());
                        Vector2 vv = new Vector2(ra.getWidth(), ra.getHeight());
//                        vv.rotate(-ra.getFinalizedRotation());
                        float sameh = (vv.y) / 2f;
                        spine.getJointRadius().put(name, sameh);
                        spine.getJointRadius().put(name + Spine.CHILD_SUFFIX, (sameh + .0001f));
//                        spine.getOriginalDimensions().put(name, new Vector2(ra.getWidth(), ra.getHeight()));
//                        float scal = 2f / b.getScaleY();
//                        Vector2 vv = new Vector2(sameh, scal);
//                        spine.getOriginalScale().put(name, vv);
//                        if (ra instanceof PolygonRegionAttachment) {
//                            ((PolygonRegionAttachment) ra).calculateBoundsAdj(sl, spine);
//                        }
//                        ra.updateOffset();
                    }
                }
            }
        }
        this.spine = spine;

    }

    /**
     * draw skeleton using batch.
     *
     * @param batch
     * @param skeleton
     */
    public final void draw(Batch batch, Skeleton skeleton) {
        float skeletonX = skeleton.getX();
        float skeletonY = skeleton.getY();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        int srcFunc = GL20.GL_SRC_ALPHA;
        if (premultipliedAlpha) {
            srcFunc = GL20.GL_ONE;
        }
        Gdx.gl.glBlendFunc(srcFunc, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setProjectionMatrix(batch.getProjectionMatrix());
        shapes.setTransformMatrix(batch.getTransformMatrix());
        Array<Bone> bones = skeleton.getBones();
        if (drawBones) {
            shapes.setColor(BONE_COLOR);
            shapes.begin(ShapeType.Filled);
            for (int i = 0, n = bones.size; i < n; i++) {
                Bone bone = bones.get(i);
                if (bone.getParent() == null) {
                    continue;
                }
                float x = skeletonX + bone.getData().getLength() * bone.getA() + bone.getWorldX();
                float y = skeletonY + bone.getData().getLength() * bone.getC() + bone.getWorldY();
                shapes.rectLine(skeletonX + bone.getWorldX(), skeletonY + bone.getWorldY(), x, y, boneWidth * scale);
            }
            shapes.end();
            shapes.begin(ShapeType.Line);
        } else {
            shapes.begin(ShapeType.Line);
        }
        if (drawRegionAttachments) {
            shapes.setColor(OTHER_COLOR);
            Array<Slot> slots = skeleton.getSlots();
            for (int i = 0, n = slots.size; i < n; i++) {
                Slot slot = slots.get(i);
                Attachment attachment = slot.getAttachment();
                if (attachment instanceof RegionAttachment) {
                    RegionAttachment regionAttachment = (RegionAttachment) attachment;
                    float[] vertices = regionAttachment.updateWorldVertices(slot, false);
                    shapes.line(vertices[X1], vertices[Y1], vertices[X2], vertices[Y2]);
                    shapes.line(vertices[X2], vertices[Y2], vertices[X3], vertices[Y3]);
                    shapes.line(vertices[X3], vertices[Y3], vertices[X4], vertices[Y4]);
                    shapes.line(vertices[X4], vertices[Y4], vertices[X1], vertices[Y1]);
                }
            }
        }
        if (drawMeshHull || drawMeshTriangles) {
            Array<Slot> slots = skeleton.getSlots();
            for (int i = 0, n = slots.size; i < n; i++) {
                Slot slot = slots.get(i);
                Attachment attachment = slot.getAttachment();
                float[] vertices = null;
                short[] triangles = null;
                int hullLength = 0;
                if (attachment instanceof MeshAttachment) {
                    MeshAttachment mesh = (MeshAttachment) attachment;
                    mesh.updateWorldVertices(slot, false);
                    vertices = mesh.getWorldVertices();
                    triangles = mesh.getTriangles();
                    hullLength = mesh.getHullLength();
                }
                if (vertices == null || triangles == null) {
                    continue;
                }
                if (drawMeshTriangles) {
                    shapes.setColor(OTHER_COLOR);
                    for (int ii = 0, nn = triangles.length; ii < nn; ii += 3) {
                        int v1 = triangles[ii] * 5, v2 = triangles[ii + 1] * 5, v3 = triangles[ii + 2] * 5;
                        shapes.triangle(vertices[v1], vertices[v1 + 1], //
                                vertices[v2], vertices[v2 + 1], //
                                vertices[v3], vertices[v3 + 1] //
                        );
                    }
                }
                if (drawMeshHull && hullLength > 0) {
                    shapes.setColor(OTHER_COLOR);
                    hullLength = hullLength / 2 * 5;
                    float lastX = vertices[hullLength - 5], lastY = vertices[hullLength - 4];
                    for (int ii = 0, nn = hullLength; ii < nn; ii += 5) {
                        float x = vertices[ii], y = vertices[ii + 1];
                        shapes.line(x, y, lastX, lastY);
                        lastX = x;
                        lastY = y;
                    }
                }
            }
        }

        if (drawBoundingBoxes) {
            bounds.update(skeleton, true);
            shapes.setColor(OTHER_COLOR);
            shapes.rect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
            shapes.setColor(OTHER_COLOR);
            Array<FloatArray> polygons = bounds.getPolygons();
            for (int i = 0, n = polygons.size; i < n; i++) {
                FloatArray polygon = polygons.get(i);
                shapes.polygon(polygon.items, 0, polygon.size);
            }
        }
        shapes.end();
        colorFlag = !colorFlag;
        shapes.begin(ShapeType.Line);
        for (int i = 0; i < spine.getRenderPoints().length; i++) {
            Vector2 drawPoint = spine.getRenderPoints()[i];
            Color rc = Color.BLACK;
            if (colorFlag && Spine.RENDER_COL[i] != null) {
                rc = Spine.RENDER_COL[i];
            }
            shapes.setColor(rc);
            if (drawPoint != null) {
                shapes.circle(drawPoint.x, drawPoint.y, 9);
                shapes.circle(drawPoint.x, drawPoint.y, 5);
                shapes.circle(drawPoint.x, drawPoint.y, 3);
                shapes.line(drawPoint.cpy().sub(20f, 0f), drawPoint.cpy().add(20f, 0f));
                shapes.line(drawPoint.cpy().sub(0f, 20f), drawPoint.cpy().add(0f, 20f));
            }
        }
        shapes.end();
        if (drawBones) {
            shapes.end();
            shapes.begin(ShapeType.Line);
            for (int i = 0, n = bones.size; i < n; i++) {
                Bone bone = bones.get(i);
                if (bone.getData().getName().equals("root")) {
                    continue;
                }
                shapes.setColor(BONE_COLOR);
                if (bone == spine.getSelectedBone()) {
                    shapes.setColor(BONE_ACTIVE_COLOR);
                }
                shapes.circle(skeletonX + bone.getWorldX(), skeletonY + bone.getWorldY(), spine.getJointRadius().get(bone.getData().getName()));
                if (bone.getChildren().size == 0) {
                    float xoff = skeletonX + bone.getData().getLength() * bone.getA() + bone.getWorldX();
                    float yoff = skeletonY + bone.getData().getLength() * bone.getC() + bone.getWorldY();
                    shapes.circle(xoff, yoff, spine.getJointRadius().get(bone.getData().getName() + Spine.CHILD_SUFFIX));
                }
            }
//            shapes.end();
//            shapes.begin(ShapeType.Filled);
            for (int i = 0, n = bones.size; i < n; i++) {
                Bone parent = bones.get(i);
//                if (parent.getData().getName().equals("root")) {
//                    continue;
//                }
                shapes.setColor(BONE_COLOR);
                if (parent == spine.getSelectedBone()) {
                    shapes.setColor(BONE_ACTIVE_COLOR);
                }
                Vector2 parentPoint = new Vector2(skeletonX + parent.getWorldX(), skeletonY + parent.getWorldY());
//                if (parent.getChildren().size == 0) {
                float xoff = skeletonX + parent.getData().getLength() * parent.getA() + parent.getWorldX();
                float yoff = skeletonY + parent.getData().getLength() * parent.getC() + parent.getWorldY();
                Vector2 childPoint = new Vector2(xoff, yoff);
                Vector2[] tan = calculateTangent(parentPoint, spine.getJointRadius().get(parent.getData().getName()), childPoint, spine.getJointRadius().get(parent.getData().getName() + Spine.CHILD_SUFFIX));
                // shapes.polygon(new float[]{tan[0].x,tan[0].y,tan[2].x,tan[2].y,tan[3].x,tan[3].y,tan[1].x,tan[1].y,tan[0].x,tan[0].y});
                shapes.rectLine(tan[0], tan[1], boneWidth * scale);
                shapes.rectLine(tan[2], tan[3], boneWidth * scale);

//                } else {
//                    for (int j = 0; j < parent.getChildren().size; j++) {
//                        Bone child = parent.getChildren().get(j);
//                        Vector2 childPoint = new Vector2(skeletonX + child.getWorldX(), skeletonY + child.getWorldY());
//                        Vector2[] tan = calculateTangent(parentPoint, spine.getJointRadius().get(parent.getData().getName()), childPoint, spine.getJointRadius().get(child.getData().getName()));
//                        shapes.rectLine(tan[0], tan[1], boneWidth * scale);
//                        shapes.rectLine(tan[2], tan[3], boneWidth * scale);
//                    }
//                }
            }
        }
        shapes.end();

    }

    /**
     * Calculates tangent line between two circles.
     *
     * @param circ1
     * @param rad1
     * @param circ2
     * @param rad2
     * @return
     */
    public static final Vector2[] calculateTangent(Vector2 circ1, float rad1, Vector2 circ2, float rad2) {
        Vector2 cc1 = circ1;
        Vector2 cc2 = circ2;
        float rr1 = rad1;
        float rr2 = rad2;

        if (rad1 <= rad2) {
            cc1 = circ2;
            cc2 = circ1;
            rr1 = rad2;
            rr2 = rad1;
        }

        Vector2 delta = (cc1.cpy().sub(cc2));
        double dd = delta.len();
        if (dd == 0d) {
            dd = .00000001d;
        }
        double rad = (rr1 - rr2);
        double a = Math.asin(rad / dd);
        double b = Math.atan2(delta.y, delta.x);

        double t1 = b - a;
        Vector2 tan1 = new Vector2((float) (rad * Math.sin(t1)), (float) (rad * -Math.cos(t1)));
        Vector2 perp1 = tan1.cpy().nor().scl(rr2);


        double t2 = b + a;
        Vector2 tan2 = new Vector2((float) (rad * -Math.sin(t2)), (float) (rad * Math.cos(t2)));
        Vector2 perp2 = tan2.cpy().nor().scl(rr2);
        return new Vector2[]{tan1.cpy().add(cc1).add(perp1), cc2.cpy().add(perp1), tan2.cpy().add(cc1).add(perp2), cc2.cpy().add(perp2)};
    }

//    public ShapeRenderer getShapeRenderer() {
//        return shapes;
//    }

//    public void setBones(boolean bones) {
//        this.drawBones = bones;
//    }

//    public void setScale(float scale) {
//        this.scale = scale;
//    }

//    public void setRegionAttachments(boolean regionAttachments) {
//        this.drawRegionAttachments = regionAttachments;
//    }

//    public void setBoundingBoxes(boolean boundingBoxes) {
//        this.drawBoundingBoxes = boundingBoxes;
//    }

//    public void setMeshHull(boolean meshHull) {
//        this.drawMeshHull = meshHull;
//    }

//    public void setMeshTriangles(boolean meshTriangles) {
//        this.drawMeshTriangles = meshTriangles;
//    }

//    public void setPremultipliedAlpha(boolean premultipliedAlpha) {
//        this.premultipliedAlpha = premultipliedAlpha;
//    }
}