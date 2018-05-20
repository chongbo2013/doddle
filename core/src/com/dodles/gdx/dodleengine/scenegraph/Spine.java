package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dodles.gdx.dodleengine.IdDatabase;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SpineAssets;
import com.dodles.gdx.dodleengine.scenegraph.spine.PolygonBatchSkeletonRenderer;
import com.dodles.gdx.dodleengine.scenegraph.spine.PolygonRegionAttachment;
import com.dodles.gdx.dodleengine.scenegraph.spine.PolygonRegionAttachmentLoader;
import com.dodles.gdx.dodleengine.scenegraph.spine.SkeletonRendererDebug;
import com.dodles.gdx.dodleengine.tools.layerTool.layerTools.spine.SpineToolFullEditorOverlay;

import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.AttachmentLoader;
import com.esotericsoftware.spine.attachments.RegionAttachment;

import java.util.HashMap;


/**
 * A Shape is an actor that can render Graphics commands to draw arbitrary shapes
 * on the stage.
 */
public class Spine extends Actor implements DodlesActor {
    public static final String ACTOR_TYPE = "Spine";

    private static final PolygonSpriteBatch POLYGON_BATCH = new PolygonSpriteBatch();

    /**
     * get Distance of ring to render.
     *
     * @return
     */
    public final float getDistance() {
        return distance;
    }

    private float distance;
    private Bone selectedBone;
    private static final int RENDER_SIZE = 10;
    private static final Vector2[] RENDER_POINTS = new Vector2[RENDER_SIZE];
    public static final Color[] RENDER_COL = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.CYAN, Color.BLUE, Color.NAVY, Color.CORAL, Color.MAGENTA, Color.GREEN, Color.FOREST, Color.LIME};
    private int selectedBonePickSpot;
    private Vector2 selectedBoneRotate;
    private Vector2 selectedBoneRotate1;

    private final AssetProvider assetProvider;

    private PolygonBatchSkeletonRenderer renderer;
    private com.dodles.gdx.dodleengine.scenegraph.spine.SkeletonRendererDebug debug;

    private String skeletonJson;
    private Transform baseTransform = new Transform();
    private Skeleton finalSkeleton;
    private Skeleton emptySkeleton;

    private AnimationState stateFinal;
    private String trackingID;
    private String originalID;
    private boolean useSpineBoy = true;
    //    private HashMap<String, Vector2> originalDimensions = new HashMap<String, Vector2>();
//    private HashMap<String, Vector2> originalJointRadius = new HashMap<String, Vector2>();
    private HashMap<String, Float> jointRadius = new HashMap<String, Float>();
    private Modes mode = Modes.Off;
    public static final String CHILD_SUFFIX = "CHILD";
    private String animationName;
    private int chosAngle;
    private int selectedAngle;
    private Phase phase;
//    private AnimationState stateEmpty;
//    private PolygonRegionAttachmentLoader attachmentLoader;

//    /**
//     * Hashmap to keep track of original skeleton scaling.
//     *
//     * @return
//     */
//    public final HashMap<String, Vector2> getOriginalScale() {
//        return originalJointRadius;
//    }


    /**
     * crosshair debug points to draw.
     *
     * @return
     */
    public final Vector2[] getRenderPoints() {
        return RENDER_POINTS;
    }

    /**
     * closest bone to touch event.
     *
     * @param selectedBone
     */
    public final void setSelectedBone(Bone selectedBone) {
        this.selectedBone = selectedBone;
    }

    /**
     * touch event mode.
     *
     * @param mode
     */
    public final void setMode(Modes mode) {
        this.mode = mode;
    }

    /**
     * closest spot of bone found.
     * 0 = bone origin
     * 1 = bone midpoint
     * 2 = bone end point
     * @return
     */

    /**
     * pick spot of bone.
     *
     * @return
     */
    public final int getSelectedBonePickSpot() {
        return selectedBonePickSpot;
    }

    /**
     * pick spot of bone.
     *
     * @param selectedBonePickSpot
     */
    public final void setSelectedBonePickSpot(int selectedBonePickSpot) {
        this.selectedBonePickSpot = selectedBonePickSpot;
    }

    /**
     * hold value.
     *
     * @param selectedBoneRotate
     */
    public final void setSelectedBoneRotate(Vector2 selectedBoneRotate) {
        this.selectedBoneRotate = selectedBoneRotate;
    }

    /**
     * hold value.
     *
     * @param selectedBoneRotate1
     */
    public final void setSelectedBoneRotate1(Vector2 selectedBoneRotate1) {
        this.selectedBoneRotate1 = selectedBoneRotate1;
    }

    /**
     * hold value.
     *
     * @return
     */
    public final Vector2 getSelectedBoneRotate() {
        return selectedBoneRotate;
    }

    /**
     * hold value.
     *
     * @return
     */
    public final Vector2 getSelectedBoneRotate1() {
        return selectedBoneRotate1;
    }

    /**
     * selected bone.
     *
     * @return
     */
    public final Bone getSelectedBone() {
        return selectedBone;
    }

    /**
     * get empty skeleton.
     *
     * @return
     */
    public final Skeleton getEmptySkeleton() {
        return emptySkeleton;
    }

    /**
     * get radius map.
     *
     * @return
     */
    public final HashMap<String, Float> getJointRadius() {
        return jointRadius;
    }

//    /**
//     * Get original size of atachments from skeleton.
//     *
//     * @return
//     */
//    public final HashMap<String, Vector2> getOriginalDimensions() {
//        return originalDimensions;
//    }

//    public void finalizeBone() {
//        Bone bone = getSelectedBone();
//        String boneName = bone.getData().getName();
//        Array<Slot> array = bone.getSkeleton().getSlots();
//        for (Slot slot : array) {
//            {
//                if (slot.getBone().getData().getName().equals(boneName)) {
//                    System.out.println("finalizing:" + slot);
//                    ((PolygonRegionAttachment) slot.getAttachment()).setSource(this);
//                    ((PolygonRegionAttachment) slot.getAttachment()).finalize(finalSkeleton, slot);
//                }
//            }
//        }
//    }

    /**
     * Finalizes the attachment textures after the skeleton has been loaded.
     */
    public final void finalize(Skeleton skeleton) {
        skeleton.updateWorldTransform();
        Array<Slot> drawOrder = skeleton.getDrawOrder();
        for (int i = 0, n = drawOrder.size; i < n; i++) {
            Slot slot = drawOrder.get(i);
            if (slot.getAttachment() != null && slot.getAttachment() instanceof PolygonRegionAttachment) {
                PolygonRegionAttachment attachment = (PolygonRegionAttachment) slot.getAttachment();
                if (attachment != null) {
                    attachment.finalize(skeleton, slot);
                }
            }
        }
    }

    /**
     * Finalize selected bone.
     */
    public final void finalizeSelectedBone(Skeleton skel, boolean erase) {
        finalizeBone(getSelectedBone(), skel, erase);
    }

    private void finalizeBone(Bone bone, Skeleton skel, boolean erase) {
        String boneName = bone.getData().getName();
        Array<Slot> array = bone.getSkeleton().getSlots();
        for (int i = 0; i < array.size; i++) {

            Slot slot = array.get(i);
            if (slot.getBone().getData().getName().equals(boneName)) {
                System.out.println("finalizing:" + slot);
                PolygonRegionAttachment attach = ((PolygonRegionAttachment) slot.getAttachment());
                if (attach != null) {
                    if (erase) {
                        attach.setSource(null);
                    } else {
                        attach.setSource(phase);
                    }
                    attach.finalize(skel, slot);
                }
            }

        }
    }

    /**
     * Finalize all bones in the skeleton.
     */
    public final void finalizeAllBones(Skeleton skel, boolean erase) {
        Array<Bone> bones = skel.getBones();
        for (int i = 0; i < bones.size; i++) {
            finalizeBone(bones.get(i), skel, erase);
        }
    }

    /**
     * Sets distance of center ring.
     *
     * @param distance
     */
    public final void setDistance(float distance) {
        this.distance = distance;
    }

    /**
     * touch event modes.
     */
    public enum Modes {
        Off,
        Rotate,
        Translate,
        Scale,
        Length;
    }

    /**
     * Set bone pose mode.
     */

    public final Modes getMode() {
        return mode;
    }

    /**
     * Use spineboy texture flag.
     */
    public final boolean isUseSpineBoy() {
        return useSpineBoy;
    }


    /**
     * Set use spineboy texture flag.
     */
    public final void setUseSpineBoy(boolean useSpineBoy) {
        this.useSpineBoy = useSpineBoy;
    }

    public Spine(AssetProvider assetProvider, String id, String trackingID, String skeletonJson) {
        super();
        setName(id);
        setTrackingID(trackingID);
        this.skeletonJson = skeletonJson;
        this.assetProvider = assetProvider;
        commonInit();
    }

    public Spine(AssetProvider assetProvider, IdDatabase idDB, JsonValue json) {
        ActorMixins.importFromJson(this, idDB, json);
        skeletonJson = json.getString("skeletonJson");
        this.assetProvider = assetProvider;
        commonInit();
    }

    @Override
    public final String getType() {
        return ACTOR_TYPE;
    }

    @Override
    public final String getTrackingID() {
        return trackingID;
    }

    @Override
    public final void setTrackingID(String pTrackingID) {
        trackingID = pTrackingID;
    }

    @Override
    public final String getOriginalID() {
        return originalID;
    }

    @Override
    public final void setOriginalID(String pOriginalID) {
        originalID = pOriginalID;
    }

    private void commonInit() {
        renderer = new PolygonBatchSkeletonRenderer();
        renderer.setPremultipliedAlpha(true); // PMA results in correct blending without outlines.
//        attachmentLoader = new PolygonRegionAttachmentLoader(this);
        PolygonRegionAttachmentLoader attachmentLoader = new PolygonRegionAttachmentLoader(this, phase, assetProvider.getTextureAtlas(SpineToolFullEditorOverlay.getSelectedSkeleton()));
        emptySkeleton = createSkeleton(attachmentLoader);
        finalize(emptySkeleton);
        debug = new SkeletonRendererDebug(this);
        finalizeAllBones(emptySkeleton, !isUseSpineBoy());
        //  finalizeAllBones();
    }

    private Skeleton createSkeleton(AttachmentLoader loader) {
        SkeletonJson json = new SkeletonJson(loader);
        json.setScale(SpineAssets.SCALE);
        SkeletonData skeletonData = json.readSkeletonData(skeletonJson, "dodlesSkeleton");

        Skeleton result = new Skeleton(skeletonData); // Skeleton holds skeleton stateFinal (bone positions, slot attachments, etc).
        if (emptySkeleton != null) {
            for (Bone b : result.getBones()) {
                Bone f = emptySkeleton.findBone(b.getData().getName());
                b.setRotation(f.getRotation());
                b.setX(f.getX());
                b.setY(f.getY());
//                b.setScaleX(f.getScaleX());
//                b.setScaleY(f.getScaleY());
                b.getData().setLength(f.getData().getLength());
            }

            for (Slot s : result.getSlots()) {
                Slot f = emptySkeleton.findSlot(s.getData().getName());
                Attachment atts = s.getAttachment();
                Attachment attf = f.getAttachment();

                if (atts instanceof RegionAttachment && attf instanceof RegionAttachment) {
                    RegionAttachment tans = (RegionAttachment) atts;
                    RegionAttachment tanf = (RegionAttachment) attf;
                    tans.setWidth(tanf.getWidth());
                    tans.setHeight(tanf.getHeight());
                    tans.setX(tanf.getX());
                    tans.setY(tanf.getY());
                    tans.setScaleX(tanf.getScaleX());
                    tans.setScaleY(tanf.getScaleY());
                    tans.setRotation(tanf.getRotation());
                    //tans.getRegion().flip(tanf.getRegion().isFlipX(),tanf.getRegion().isFlipY());
                    tans.updateOffset();

//                    Vector2 vec = new Vector2(tanf.getWidth(), tanf.getHeight());
//                    if (originalDimensions.get(f.getBone().getData().getName()) == null) {
//                        originalDimensions.put(f.getBone().getData().getName(), vec);
//                    }
                }
            }
        }
        result.setFlipY(true);
        result.updateWorldTransform();

        Vector2 offset = new Vector2();
        Vector2 size = new Vector2();
        result.getBounds(offset, size);

        // Make sure skeleton draw starts at 0,0 for ease of mental visualization...

        //offset messes up all the calculations. :(
//        result.setX(-offset.x);
//        result.setY(-offset.y);

//        setOriginX(size.x / 2);
//        setOriginY(size.y / 2);

//        result.updateWorldTransform();

        return result;
    }

    /**
     * Captures the rendered output of the given actor and uses it as the background texture for the spine animation.
     */
    public final void setTexture(Phase phase1) {
        this.phase = phase1;
        PolygonRegionAttachmentLoader attachmentLoader = new PolygonRegionAttachmentLoader(this, phase, assetProvider.getTextureAtlas(SpineToolFullEditorOverlay.getSelectedSkeleton()));
        finalSkeleton = createSkeleton(attachmentLoader);
        finalize(finalSkeleton);

        //set bones back in place
        for (Bone b : finalSkeleton.getBones()) {
            b.setRotation(b.getSkeleton().getData().findBone(b.getData().getName()).getRotation());
            b.setX(b.getSkeleton().getData().findBone(b.getData().getName()).getX());
            b.setY(b.getSkeleton().getData().findBone(b.getData().getName()).getY());
        }

        AnimationStateData stateDataFinal = new AnimationStateData(finalSkeleton.getData()); // Defines mixing (crossfading) between animations.
        stateFinal = new AnimationState(stateDataFinal); // Holds the animation stateFinal for a skeleton (current animation, time, etc).
        stateFinal.setTimeScale(.5f);
    }


    /**
     * Clears any previous captured texture.
     */
    public final void clearTexture() {
        finalSkeleton = null;


//        for (Slot slot:emptySkeleton.getSlots()){
//            PolygonRegionAttachment attach=((PolygonRegionAttachment)slot.getAttachment()).se
//        }
//        emptySkeleton = null;
//        PolygonRegionAttachmentLoader attachmentLoader = new PolygonRegionAttachmentLoader(this, phase, assetProvider.getTextureAtlas(SpineToolFullEditorOverlay.getSelectedSkeleton()));
//        emptySkeleton = createSkeleton(attachmentLoader);
//        finalize(emptySkeleton);
    }

    /**
     * Animates the spine.
     */
    public final void animate() {
        // Queue animations on track 0.
        Array<Animation> animations = finalSkeleton.getData().getAnimations();
        int pick = (int) (Math.random() * animations.size);
        animationName = animations.get(pick).getName();
        stateFinal.setAnimation(0, animationName, true);
    }

    /**
     * Stops the animation.
     */
    public final void stop() {
        if (animationName != null) {
            stateFinal.setAnimation(0, animationName, false);

        }
    }

    @Override
    public final Rectangle getDrawBounds() {
        Vector2 offset = new Vector2();
        Vector2 size = new Vector2();
        emptySkeleton.getBounds(offset, size);
        return new Rectangle(offset.x, offset.y, size.x, size.y);
    }

    @Override
    public final void updateOrigin() {
        // Nothing to do, origin gets set in createSkeleton.
    }

    @Override
    public final Vector2 getOrigin() {
        return new Vector2(getOriginX(), getOriginY());
    }

    @Override
    public final void updateBaseTransform(Transform transform) {
        baseTransform = transform;
        ActorMixins.updateTransformProperties(this, transform);
    }

    @Override
    public final void resetToBaseTransform() {
        updateBaseTransform(baseTransform);
    }

    @Override
    public final Transform getBaseTransform() {
        return baseTransform;
    }

    /**
     * Draws all of the graphics commands to the scene relative to the current
     * position of the Shape.
     */
    @Override
    public final void draw(Batch batch, float parentAlpha) {

        Matrix4 originalTransform = ActorMixins.setBatchTransformMatrix(batch, this);
        batch.end();
        POLYGON_BATCH.begin();
        POLYGON_BATCH.setProjectionMatrix(batch.getProjectionMatrix());
        POLYGON_BATCH.setTransformMatrix(batch.getTransformMatrix());

        if (useSpineBoy && finalSkeleton != null) {
            stateFinal.update(Gdx.graphics.getDeltaTime());
            stateFinal.apply(finalSkeleton);

            finalSkeleton.updateWorldTransform();
            renderer.draw(POLYGON_BATCH, finalSkeleton, parentAlpha); // Draw the skeleton images.
            POLYGON_BATCH.setProjectionMatrix(batch.getProjectionMatrix());
            POLYGON_BATCH.setTransformMatrix(batch.getTransformMatrix());
            //draw debug tangent circles on running animation
            debug.draw(POLYGON_BATCH, finalSkeleton);
        } else {
            stateFinal.update(Gdx.graphics.getDeltaTime());
            stateFinal.apply(emptySkeleton);

            emptySkeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.
            for (Slot sl : emptySkeleton.getSlots()) {
                if (sl.getAttachment() instanceof RegionAttachment) {
                    RegionAttachment r = ((RegionAttachment) sl.getAttachment());

                    //calculate region attachment bounds here
                    if (r instanceof PolygonRegionAttachment) {
                        ((PolygonRegionAttachment) r).calculateBoundsAdj(sl, this);
                        r.updateOffset();
                    }

                    //---------------------------------------------------------------------
                }
            }
            Skeleton temp = emptySkeleton;
            if ((useSpineBoy || temp == null) && finalSkeleton != null) {
                temp = finalSkeleton;
            }
            renderer.draw(POLYGON_BATCH, temp, 1);
            POLYGON_BATCH.setProjectionMatrix(batch.getProjectionMatrix());
            POLYGON_BATCH.setTransformMatrix(batch.getTransformMatrix());
//            if (isUseSpineBoy())
            debug.draw(POLYGON_BATCH, temp);
        }

        POLYGON_BATCH.end();
        batch.begin();
        Array<Slot> slots = null;
        float height = 0;
        float width = 0;

        if (finalSkeleton != null) {
            slots = finalSkeleton.getSlots();
            width = finalSkeleton.getData().getWidth();
            height = finalSkeleton.getData().getHeight();
        } else if (emptySkeleton != null) {
            slots = emptySkeleton.getSlots();
            width = emptySkeleton.getData().getWidth();
            height = emptySkeleton.getData().getHeight();
        }

        int anglePart = 360 / slots.size;
        int pos = 0;
        selectedAngle = -1;

        for (Slot sl : slots) {
            if (sl.getAttachment() instanceof PolygonRegionAttachment) {
                PolygonRegionAttachment attachment = ((PolygonRegionAttachment) sl.getAttachment());
                if (attachment.getRegion() != null) {
                    int rotation = anglePart * pos + chosAngle;
                    rotation = (rotation + 360) % 360;
                    float off = 1f - Math.abs((180f - rotation) / 180f);
                    Vector2 dd1 = new Vector2(0, Math.max(width, height) * distance);
                    if (distance > 0.0f) {
                        distance -= .01f;
                    }
//                    System.out.println("distance:"+distance);

//                    Vector2 dd2 = new Vector2(0, Math.max(width, height) / 1.0f);
                    if (getSelectedBone() != null && sl.getBone().getData().getName().equals(getSelectedBone().getData().getName())) {
                        batch.setColor(Color.WHITE);
                        selectedAngle = pos;
                    } else {
                        batch.setColor(new Color(off, off, off, 0));
                    }

                    dd1.rotate(rotation);

//                    Vector2 dd2=dd1.cpy();
                    dd1.add(-width / 2, height / 3);
//                    dd2.add(-width / 2, -height);
                    pos++;
                    TextureRegion cropRegion = attachment.getRotatedRegion();
                    boolean atlas = false;
                    if (cropRegion == null || !isUseSpineBoy()) {
                        cropRegion = attachment.getAtlasRegion();
                        atlas = true;
                    }
                    TextureRegion blankRegion = attachment.getBlankRegion();

                    if (cropRegion != null && blankRegion != null) {
                        Vector2 diff = new Vector2(cropRegion.getRegionWidth() - blankRegion.getRegionWidth(), cropRegion.getRegionHeight() - blankRegion.getRegionHeight());
                        diff.scl(off);
                        diff.scl(.5f);
                        off *= 2f;
                        if (!atlas) {
//                            float rot = attachment.getFinalizedRotation();
//                            batch.draw(cropRegion, dd1.x - diff.x, dd1.y - diff.y, cropRegion.getRegionWidth() * .5f, cropRegion.getRegionHeight() * .5f, cropRegion.getRegionWidth(), cropRegion.getRegionHeight(), off, off * flip, rot);
                            TextureRegion tr = attachment.getRotatedRegion();
                            if (tr != null) {
                                batch.draw(tr, dd1.x, dd1.y, blankRegion.getRegionWidth() * .5f, blankRegion.getRegionHeight() * .5f, blankRegion.getRegionWidth(), blankRegion.getRegionHeight(), off, off, 0);
                            }

                        } else {
                            TextureRegion tr = attachment.getAtlasRegion();
                            if (tr != null) {
                                batch.draw(tr, dd1.x, dd1.y, blankRegion.getRegionWidth() * .5f, blankRegion.getRegionHeight() * .5f, blankRegion.getRegionWidth(), blankRegion.getRegionHeight(), off, -off, 0);
                            }
                        }

                        batch.draw(blankRegion, dd1.x, dd1.y, blankRegion.getRegionWidth() * .5f, blankRegion.getRegionHeight() * .5f, blankRegion.getRegionWidth(), blankRegion.getRegionHeight(), off, off, 0);


                    }
                }
            }
        }
        if (selectedAngle != -1) {
            int dff = anglePart * selectedAngle + chosAngle;
            if (dff > 185) {
                chosAngle -= 5;
            }
            if (dff < 175) {
                chosAngle += 5;
            }
//            if (dff < 0) {
//                chosAngle += 180;
//            }
        }
//        chosAngle=(chosAngle+360)%360;
        batch.setTransformMatrix(originalTransform);
    }

    @Override
    public final BaseDodlesViewGroup getParentDodlesViewGroup() {
        return CommonActorOperations.getParentDodlesViewGroup(this);
    }

    @Override
    public final String getParentViewID() {
        return CommonActorOperations.getParentView(this).getName();
    }

    @Override
    public final DodlesActor dodleClone(IdDatabase iddb, ObjectManager objectManager) {
        Spine cloneSpine = new Spine(assetProvider, iddb.getNewID(getName()), trackingID, skeletonJson);
        ActorMixins.commonClone(this, objectManager, cloneSpine);
        return cloneSpine;
    }

    @Override
    public final void writeConfig(Json json) {
        json.writeObjectStart();

        ActorMixins.exportToJson(this, json);
        json.writeValue("skeletonJson", skeletonJson);

        json.writeObjectEnd();
    }

}
