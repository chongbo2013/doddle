package com.dodles.gdx.dodleengine.tools.animation.subtools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.commands.CommandManager;
import com.dodles.gdx.dodleengine.editor.OkCancelStackFrame;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.overlays.DrawStrokeOverlay;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.input.InteractionData;
import com.dodles.gdx.dodleengine.input.PanRotateZoomActorInputHandler;
import com.dodles.gdx.dodleengine.input.TouchInputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolRegistry;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.util.LineSmoothingUtility.PointExtractor;
import com.dodles.gdx.dodleengine.util.LineSmoothingUtility.Simplify;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PerDodleEngine
public class MoveEffectDrawSubtool extends AbstractAnimationSubtool implements TouchInputHandler {

    //region Properties & Variables

    // Constants
    private static final int MIN_PATH_POINTS = 2;
    private static final boolean APPLY_SMOOTHING = true;

    // Subsystem References
    private final PanRotateZoomActorInputHandler przaih;
    private final CommandManager commandManager;
    private final CommandFactory commandFactory;
    private final DodleStageManager dodleStageManager;
    private final EngineEventManager eventManager;
    private final DodleStageManager stageManager;
    private final DrawStrokeOverlay drawStrokeOverlay;

    // Internal Variables
    private final List<PositionAndTime> path = new ArrayList<PositionAndTime>();
    private final List<Vector2> positionPath = new ArrayList<Vector2>();
    private final List<Float> timingPercentages = new ArrayList<Float>();
    private Vector2 actorStart = null;
    private boolean creatingNewEffect = false;

    //endregion Properties & Variables

    //region Constructor
    @Inject
    public MoveEffectDrawSubtool(
            AnimationManager animationManager,
            AnimationSubtoolRegistry animationSubtoolRegistry,
            AnimationTool animationTool,
            CommandFactory commandFactory,
            CommandManager commandManager,
            DodleStageManager dodleStageManager,
            DrawStrokeOverlay drawStrokeOverlay,
            EventBus eventBus,
            EngineEventManager eventManager,
            final ObjectManager objectManager,
            OkCancelStackManager okCancelStackManager,
            PanRotateZoomActorInputHandler przaih,
            DodleStageManager stageManager
    ) {
        super(
                animationManager,
                animationSubtoolRegistry,
                animationTool,
                eventBus,
                objectManager,
                okCancelStackManager
        );
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
        this.dodleStageManager = dodleStageManager;
        this.drawStrokeOverlay = drawStrokeOverlay;
        this.eventManager = eventManager;
        this.przaih = przaih;
        this.stageManager = stageManager;

        przaih.initialize(new PanRotateZoomActorInputHandler.ActorProvider() {
            @Override
            public DodlesActor getActor() {
                return objectManager.getSelectedActor();
            }
        });

        inputHandlers.add(this);
        inputHandlers.add(przaih);
    }
    //endregion Constructor

    //region AbstractAnimationSubtool Implementation

    @Override
    public AnimationSubtoolState getSubtoolState() {
        return AnimationSubtoolState.MOVE_EFFECT_DRAW;
    }

    @Override
    public void onActivation(AnimationSubtoolState previousState) {
        creatingNewEffect = (previousState != AnimationSubtoolState.MOVE_EFFECT_CONFIGURE);
        dodleStageManager.setDisplayMode(DodleStageManager.DisplayMode.NORMAL);
    }

    @Override
    public void onDeactivation(AnimationSubtoolState nextState) {
        drawStrokeOverlay.clear();
        positionPath.clear();
        path.clear();
        actorStart = null;
        creatingNewEffect = false;
    }

    //endregion AbstractAnimationSubtool Implementation

    //region TouchInputHandler Implementation

    @Override
    public void handleTouchStart(InteractionData startData, int pointer) {
        DodlesActor actor = objectManager.getSelectedActor();
        actorStart = null;

        if (actor != null) {
            drawStrokeOverlay.clear();
            positionPath.clear();
            updatePathAndOverlay(startData);
            actorStart = new Vector2(actor.getX(), actor.getY());
        }
    }

    @Override
    public void handleTouchMove(InteractionData moveData, int pointer) {
        if (actorStart != null) {
            updatePathAndOverlay(moveData);
        }
    }

    @Override
    public void handleTouchEnd(InteractionData endData, int pointer) {
        // Reset input if path isn't long enough
        if (path.size() < MIN_PATH_POINTS) {
            path.clear();
            return;
        }

        // Apply Smoothing
        if (APPLY_SMOOTHING) {
            List<PositionAndTime> newPath2 = getSmoothedPath(path, getAnimationLength());
            path.clear();
            path.addAll(newPath2);
        }

        // Compute final timing and position lists
        positionPath.clear();
        List<Long> timingPath = new ArrayList<Long>();
        for (PositionAndTime positionAndTime : path) {
            positionPath.add(positionAndTime.position);
            timingPath.add(positionAndTime.time);
        }

        // Compute Timing Percentages
        List<Float> newTimingPercentages = computeTimingPercentages(timingPath);
        timingPercentages.clear();
        timingPercentages.addAll(newTimingPercentages);

        // Reset actor & UI
        DodlesActor actor = objectManager.getSelectedActor();
        if (actor != null && actorStart != null) {
            actor.setX(actorStart.x);
            actor.setY(actorStart.y);
        }
        stageManager.updateStateUi();

        // Create ok/cancel stack if new animation, but not when redrawing
        if (creatingNewEffect) {
            okCancelStackManager.push(new OkCancelStackFrame("Move-On-Timeline", true, false) {
                @Override
                public void execute() {
                    eventBus.publish(
                            EventTopic.EDITOR,
                            EventType.SET_ANIMATION_TOOL_STATE,
                            AnimationSubtoolState.TIMELINE.toString());
                    drawStrokeOverlay.clear();
                }
            });
        }

        // Transition to Configure Event
        eventBus.publish(
                EventTopic.EDITOR,
                EventType.SET_ANIMATION_TOOL_STATE,
                AnimationSubtoolState.MOVE_EFFECT_CONFIGURE.toString());
    }

    @Override
    public void handleTouchCancel() {
    }

    //endregion TouchInputHandler Implementation

    //region Public API

    public boolean isCreatingNewEffect() {
        return creatingNewEffect;
    }

    public final List<Vector2> getPositionPath() {
        return new ArrayList<Vector2>(positionPath);
    }


    public final List<Float> getTimingPercentages() {
        return new ArrayList<Float>(timingPercentages);
    }

    public final float getAnimationLength() {
        // NOTE: don't rely on the timingPath here, b/c it might not be calculated yet
        if (path.size() > 1) {
            return (path.get(path.size() - 1).time - path.get(0).time) / 1000f;
        } else {
            return  0f;
        }
    }

    //endregion Public API

    //region Private Helper Functions

    /**
     * Adds the supplied point to the path and overlay data
     * @param data
     */
    private void updatePathAndOverlay(InteractionData data) {
        long newTime = TimeUtils.millis();
        Vector2 newPosition = new Vector2(data.getDodlePoint().x, data.getDodlePoint().y);

        if ((path.size() > 0) && (newTime == path.get(path.size()-1).time)) {
            // we have a new point at the old time, so just update the point
            path.get(path.size()-1).position = newPosition;
        }
        else if (!((path.size() > 0) && newPosition.equals(path.get(path.size()-1).position))) {
            // Only create new point if the position has changed
            PositionAndTime positionAndTime = new PositionAndTime(newPosition, newTime);
            path.add(positionAndTime);
        }

        drawStrokeOverlay.update(newPosition);
    }

    private List<PositionAndTime> getSmoothedPath(List<PositionAndTime> points, float seconds) {
        float minFPS = 20;
        float pointExtract = 1f;
        while (points.size() > seconds * minFPS) {
            final float finalPointExtract = pointExtract;
            PointExtractor<PositionAndTime> pointExtractor = new PointExtractor<PositionAndTime>() {
                @Override
                public double getX(PositionAndTime point) {
                    return point.position.x * finalPointExtract;
                }

                @Override
                public double getY(PositionAndTime point) {
                    return point.position.y * finalPointExtract;
                }
            };

            points = getSmoothedPath(points, 2f, false, pointExtractor);
            pointExtract /= 2;
        }
        return points;
    }

    private List<PositionAndTime> getSmoothedPath(
            List<PositionAndTime> points,
            float tolerance,
            boolean highestQuality,
            PointExtractor<PositionAndTime> pointExtractor
    ) {
        PositionAndTime[] coords =  points.toArray(new PositionAndTime[points.size()]);
        Simplify<PositionAndTime> simplify = new Simplify<PositionAndTime>(new PositionAndTime[0], pointExtractor);
        PositionAndTime[] simplified = simplify.simplify(coords, tolerance, highestQuality);
        return Arrays.asList(simplified);
    }

    private List<Float> computeTimingPercentages(List<Long> pathTiming) {
        if (pathTiming.size() <= 1) {
            return new ArrayList<Float>();
        }

        float totalTime = pathTiming.get(pathTiming.size()-1) - pathTiming.get(0);
        List<Float> animationPercents = new ArrayList<Float>();

        animationPercents.add(0f);
        for (int i = 1; i < pathTiming.size(); i++) {
            float delta = pathTiming.get(i) - pathTiming.get(i - 1);
            animationPercents.add(delta / totalTime);
        }

        return animationPercents;
    }

    private class PositionAndTime {
        public Vector2 position;
        public long time;

        public PositionAndTime(Vector2 position, long time) {
            this.position = position;
            this.time = time;
        }

        @Override
        public String toString() {
            return "[" + position.toString() + ", " + time + "]";
        }
    }

    //endregion Private Helper Functions
}
