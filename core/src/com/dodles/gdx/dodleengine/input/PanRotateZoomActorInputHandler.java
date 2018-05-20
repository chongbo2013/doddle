package com.dodles.gdx.dodleengine.input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.commands.TransformActorCommand;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Spine;
import com.esotericsoftware.spine.Bone;

import javax.inject.Inject;

/**
 * Common input handler that allows panning, rotating and zooming actors.
 */
public class PanRotateZoomActorInputHandler implements PanInputHandler, TouchInputHandler, RotationInputHandler, ZoomInputHandler {
    private final CommandFactory commandFactory;
    private final CommandManager commandManager;
    private final DodleStageManager stageManager;
    private final EngineEventManager eventManager;

    private ActorProvider actorProvider;
    private TransformActorCommand command;
    private boolean ignoreTransformCommands;

    private boolean isPanning = false;
    private Vector2 prevPanPoint;
    private Vector2 touchStartPoint;
    private PanEventHandler panHandler;

    private boolean isRotating = false;
    private float prevRotation;
    private RotateEventHandler rotateHandler;

    private boolean isZooming = false;
    private float prevScale;
    private ZoomEventHandler zoomHandler;

    private boolean isEnabled = true;
    private Spine spactor;

    @Inject
    public PanRotateZoomActorInputHandler(CommandFactory commandFactory, CommandManager commandManager, DodleStageManager stageManager, EngineEventManager eventManager) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.stageManager = stageManager;
        this.eventManager = eventManager;
    }

    /**
     * Initializes the handler.
     */
    public final void initialize(ActorProvider ap) {
        actorProvider = ap;
    }

    /**
     * Initializes the handler with override actions for panning, rotating and zooming.
     */
    public final void initialize(ActorProvider ap, PanEventHandler newPanHandler, RotateEventHandler newRotateHandler, ZoomEventHandler newZoomHandler) {
        initialize(ap);

        ignoreTransformCommands = true;
        panHandler = newPanHandler;
        rotateHandler = newRotateHandler;
        zoomHandler = newZoomHandler;
    }

    /**
     * Enables or disables the handler.
     */
    public final void setEnabled(boolean newEnabled) {
        isEnabled = newEnabled;
    }

    /**
     * Searches for closest bone touched.
     *
     * @param spactor2
     * @param point
     */
    private Bone getClosestBone(Spine spactor2, Vector2 point) {
        spactor2.getEmptySkeleton().updateWorldTransform();
        Array<Bone> bones = spactor2.getEmptySkeleton().getBones();
        float dist = Float.MAX_VALUE;
        Bone best = null;
        Vector2 bestc1 = null;
        Vector2 bestc2 = null;
        Vector2 bestc3 = null;
        Vector2 d = new Vector2(spactor2.getEmptySkeleton().getX(), spactor2.getEmptySkeleton().getY());
        for (Bone b : bones) {
            float xoff1 = (float) (b.getWorldX());
            float yoff1 = (float) (b.getWorldY());

            float xoff2 = (float) (b.getWorldX() + (b.getData().getLength() / 2f) * b.getA());
            float yoff2 = (float) (b.getWorldY() + (b.getData().getLength() / 2f) * b.getC());

            float xoff3 = (float) (b.getWorldX() + (b.getData().getLength()) * b.getA());
            float yoff3 = (float) (b.getWorldY() + (b.getData().getLength()) * b.getC());

            Vector2 c1 = new Vector2(xoff1, yoff1);
            Vector2 c2 = new Vector2(xoff2, yoff2);
            Vector2 c3 = new Vector2(xoff3, yoff3);

            float bdist = point.sub(d).dst(c1);
            if (bdist < dist) {
                dist = bdist;
                best = b;
                spactor.setSelectedBonePickSpot(0);
                bestc1 = c1.cpy();
                bestc2 = c2.cpy();
                bestc3 = c3.cpy();
            }
            bdist = point.sub(d).dst(c2);
            if (bdist < dist) {
                dist = bdist;
                best = b;
                spactor.setSelectedBonePickSpot(1);
                bestc1 = c1.cpy();
                bestc2 = c2.cpy();
                bestc3 = c3.cpy();
            }
            bdist = point.sub(d).dst(c3);
            if (bdist < dist) {
                dist = bdist;
                spactor.setSelectedBonePickSpot(2);
                best = b;
                bestc1 = c1.cpy();
                bestc2 = c2.cpy();
                bestc3 = c3.cpy();
            }

        }
        spactor.getRenderPoints()[3] = null;
        spactor.getRenderPoints()[4] = null;
        spactor.getRenderPoints()[5] = null;
        if (spactor.getSelectedBonePickSpot() == 0 && spactor.getRenderPoints() != null) {
            spactor.getRenderPoints()[3] = bestc1.cpy();
        }
        if (spactor.getSelectedBonePickSpot() == 1 && spactor.getRenderPoints() != null) {
            spactor.getRenderPoints()[4] = bestc2.cpy();
        }
        if (spactor.getSelectedBonePickSpot() == 2 && spactor.getRenderPoints() != null) {
            spactor.getRenderPoints()[5] = bestc3.cpy();
        }
        return best;
    }


    /**
     * Handles skeleton touch event.
     */
    public final void handleTouch() {
        if (spactor != null) {
            spactor.finalizeSelectedBone(spactor.getEmptySkeleton(), !spactor.isUseSpineBoy());
        }
    }

    @Override
    public final void handleTouchStart(InteractionData startData, int pointer) {

        DodlesActor actor = actorProvider.getActor();
        touchStartPoint = CommonActorOperations.dodleToLocalCoordinates((DodlesActor) ((Actor) actor), startData.getDodlePoint());

        if (actor instanceof Spine) {


            spactor = (Spine) actor;
            if (spactor != null) {
                spactor.setDistance(1.3f);
            }
            if (touchStartPoint != null && spactor.getRenderPoints() != null) {
                spactor.getRenderPoints()[0] = touchStartPoint.cpy();
            }
            if (isEnabled && actor != null) {

                //what is distance to bone? that is an offset for draggin
                Bone tempBone = getClosestBone(spactor, touchStartPoint);
                spactor.setSelectedBone(tempBone);
                spactor.setSelectedBoneRotate(new Vector2(tempBone.getX(), tempBone.getY()));
                spactor.setSelectedBoneRotate1(new Vector2(tempBone.getWorldX(), tempBone.getWorldY()));
            }
            handleTouch();
        }
    }

    @Override
    public final void handleTouchMove(InteractionData moveData, int pointer) {
        DodlesActor actor = actorProvider.getActor();
        Vector2 movePoint = CommonActorOperations.dodleToLocalCoordinates((DodlesActor) ((Actor) actor), moveData.getDodlePoint());
        if (spactor != null) {
            if (spactor.getDistance() < 1.5) {
                spactor.setDistance(spactor.getDistance() + .005f);
            }
        }
        if (spactor != null && spactor.getSelectedBone() != null) {
            if (movePoint != null && spactor.getRenderPoints() != null) {
                spactor.getRenderPoints()[8] = movePoint.cpy();
            }
            spactor = (Spine) actor;
            //find rotation of the bone connected to root bone

//            float rootRotation = 0;
//            Bone rootBone = spactor.getSelectedBone();
//            do {
//                rootRotation = rootBone.getRotation();
//                rootBone=rootBone.getParent();
//            }
//            while (rootBone!=null);
//            System.out.println("root rotation="+rootRotation);

            float rotation = 0;
            if (spactor != null && spactor.getSelectedBone() != null) {
                Bone tempBone = spactor.getSelectedBone().getParent();
                while (tempBone != null) {
                    rotation += tempBone.getRotation();
                    tempBone = tempBone.getParent();
                }
            }

//            spactor.finalizeBone();
            if (spactor.getMode().equals(Spine.Modes.Translate)) {
                Vector2 delta = (movePoint.cpy().sub(touchStartPoint));
                Vector2 deltarot = delta.cpy().rotate(rotation).scl(1, -1);
                spactor.getSelectedBone().setX(spactor.getSelectedBoneRotate().x + deltarot.x);
                spactor.getSelectedBone().setY(spactor.getSelectedBoneRotate().y + deltarot.y);
            } else if (spactor.getMode().equals(Spine.Modes.Rotate)) {
                Vector2 delta = movePoint.cpy().sub(spactor.getSelectedBoneRotate1());
                spactor.getSelectedBone().setRotation(-(rotation + delta.angle()));
            } else if (spactor.getMode().equals(Spine.Modes.Scale)) {
                String isChild = "";
                if (spactor.getSelectedBonePickSpot() == 2) {
                    isChild = Spine.CHILD_SUFFIX;
                }
                Vector2 delta = (movePoint.cpy().sub(touchStartPoint));
                spactor.getJointRadius().put(spactor.getSelectedBone().getData().getName() + isChild, delta.len() / 6f);
            } else if (spactor.getMode().equals(Spine.Modes.Length)) {
                Vector2 delta = movePoint.cpy().sub(spactor.getSelectedBoneRotate1());
                float len = delta.len();
                spactor.getSelectedBone().getData().setLength(len);
                spactor.getSelectedBone().setRotation(-(rotation + delta.angle()));

            }
            handleTouch();
        }
    }

    @Override
    public final void handleTouchEnd(InteractionData endData, int pointer) {
        handleTouch();
    }

    @Override
    public void handleTouchCancel() {

    }

    @Override
    public final void handlePanStart(InteractionData startData) {
        DodlesActor actor = actorProvider.getActor();
        if (startData.getNumPointers() == 1) {
            boolean flag = true;
            if (actor instanceof Spine) {
                spactor = (Spine) actor;
                flag = spactor.getMode().equals(Spine.Modes.Off);
            }
            if (flag && isEnabled && actor != null) {
                isPanning = true;
                prevPanPoint = CommonActorOperations.dodleToLocalCoordinates((DodlesActor) ((Actor) actor).getParent(), startData.getDodlePoint());

                initTransformCommand();
            }
        }
    }

    @Override
    public final void handlePanMove(InteractionData moveData) {
        DodlesActor actor = actorProvider.getActor();
        if (moveData.getNumPointers() == 1) {
            if (actor != null && isPanning) {
                Vector2 curPanPoint = CommonActorOperations.dodleToLocalCoordinates((DodlesActor) ((Actor) actor).getParent(), moveData.getDodlePoint());
                Vector2 delta = new Vector2(curPanPoint.x - prevPanPoint.x, curPanPoint.y - prevPanPoint.y);
                prevPanPoint = curPanPoint;
                if (panHandler != null) {
                    panHandler.onPan(delta);
                } else {
                    actor.setX(actor.getX() + delta.x);
                    actor.setY(actor.getY() + delta.y);

                    stageManager.updateStateUi();
                }
            }
        }
    }

    @Override
    public final void handlePanEnd(InteractionData endData) {
        isPanning = false;
        possiblyCloseCommand();
    }

    @Override
    public final void handleRotationStart(RotationInteractionData startData) {
        DodlesActor actor = actorProvider.getActor();

        if (isEnabled && actor != null) {
            prevRotation = startData.getRotation();
            initTransformCommand();
        }
    }

    @Override
    public final void handleRotationMove(RotationInteractionData moveData) {
        DodlesActor actor = actorProvider.getActor();
        float delta = moveData.getRotation() - prevRotation;
        if (!isRotating) {
            isRotating = Math.abs(delta) > 10 && !isZooming;
        }

        if (actor != null && isRotating) {
            eventManager.fireEvent(EngineEventType.DISPLAY_TRANSFORM_OVERLAY, "rotate");

            prevRotation = moveData.getRotation();

            if (rotateHandler != null) {
                rotateHandler.onRotate(delta);
            } else {
                actor.setRotation(actor.getRotation() + delta);
                stageManager.updateStateUi();
            }
        }
    }

    @Override
    public final void handleRotationEnd(RotationInteractionData endData) {
        if (isRotating) {
            eventManager.fireEvent(EngineEventType.DISPLAY_TRANSFORM_OVERLAY);
            isRotating = false;
            possiblyCloseCommand();
        }
    }

    @Override
    public final void handleZoomStart(ZoomInteractionData startData) {
        DodlesActor actor = actorProvider.getActor();

        if (isEnabled && actor != null) {
            prevScale = startData.getScale();
            initTransformCommand();
        }
    }

    @Override
    public final void handleZoomMove(ZoomInteractionData moveData) {
        DodlesActor actor = actorProvider.getActor();
        float ratio = moveData.getScale() / prevScale;

        if (!isZooming) {
            isZooming = (Math.abs(ratio) > 1.5 || Math.abs(ratio) < .75) && !isRotating;
        }

        if (actor != null && isZooming) {
            eventManager.fireEvent(EngineEventType.DISPLAY_TRANSFORM_OVERLAY, "zoom");
            prevScale = moveData.getScale();

            if (zoomHandler != null) {
                zoomHandler.onZoom(ratio);
            } else {
                actor.setScaleX(actor.getScaleX() * ratio);
                actor.setScaleY(actor.getScaleY() * ratio);
                stageManager.updateStateUi();
            }
        }
    }

    @Override
    public final void handleZoomEnd(ZoomInteractionData endData) {
        if (isZooming) {
            eventManager.fireEvent(EngineEventType.DISPLAY_TRANSFORM_OVERLAY);
            isZooming = false;
            possiblyCloseCommand();
        }
    }

    /**
     * An interface to allow consumers of this handler to provide the actor to be transformed.
     */
    public interface ActorProvider {
        /**
         * Returns the actor to transform.
         */
        DodlesActor getActor();
    }

    private void initTransformCommand() {
        if (!ignoreTransformCommands && command == null) {
            command = (TransformActorCommand) commandFactory.createCommand(TransformActorCommand.COMMAND_NAME);
            command.startTransformLive(actorProvider.getActor());
        }
    }

    private void possiblyCloseCommand() {
        if (!isPanning && !isRotating && !isZooming && command != null) {
            command.finishTransformLive();
            commandManager.add(command);
            command = null;
        }
    }

    /**
     * Pan event handler.
     */
    public interface PanEventHandler {
        /**
         * Called on pan.
         */
        void onPan(Vector2 delta);
    }

    /**
     * Zoom event handler.
     */
    public interface ZoomEventHandler {
        /**
         * Called on zoom.
         */
        void onZoom(float delta);
    }

    /**
     * Rotate event handler.
     */
    public interface RotateEventHandler {
        /**
         * Called on rotate.
         */
        void onRotate(float delta);
    }
}
