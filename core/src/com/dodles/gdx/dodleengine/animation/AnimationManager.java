package com.dodles.gdx.dodleengine.animation;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dodles.gdx.dodleengine.CameraManager;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.EffectTiming.EffectTimingStatus;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SpeechAssets;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.input.InputHandler;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.DodlesGroup;
import com.dodles.gdx.dodleengine.scenegraph.Layer;
import com.dodles.gdx.dodleengine.scenegraph.Scene;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages animation.
 */
@PerDodleEngine
public class AnimationManager {
    private final HashMap<String, SceneAnimation> sceneAnimations = new HashMap<String, SceneAnimation>();
    private final ArrayList<EffectAnimator> effectsPlayed = new ArrayList<EffectAnimator>();
    private final ArrayList<DeltaTimeRunnable> actRunnables = new ArrayList<DeltaTimeRunnable>();
    private final AssetProvider assetProvider;
    private final CameraManager cameraManager;
    private final ObjectManager objectManager;
    private final EngineEventManager eventManager;
    private final EventBus eventBus;
    private final PlaybackSettings playbackSettings;
    
    private HashMap<String, BlockStatus> animationBlockStatus = null;
    private HashMap<EffectTiming, EffectTimingStatus> timingStatus = null;
    private boolean playSingleBlock = false;
    private boolean isSeeking = false;

    private float maxDuration = 0;
    private float _lastSentPlayTime = 0;
    private float _currentPlayTime = 0;
    private void setCurrentPlayTime(float newValue) {
        if (_currentPlayTime != newValue) {
            _currentPlayTime = newValue;

            if (_currentPlayTime > (_lastSentPlayTime + 0.25) || _currentPlayTime < _lastSentPlayTime) {
                sendCurrentPlayTimeEvent();
            }
        }
    }
    private void incrementCurrentPlayTime(float deltaTime) {
        setCurrentPlayTime(_currentPlayTime + deltaTime);
    }
    private void sendCurrentPlayTimeEvent() {
        _lastSentPlayTime = _currentPlayTime;
        eventBus.publish(EventTopic.DEFAULT, EventType.UPDATE_PLAY_TIME, String.valueOf(_currentPlayTime));
    }
    
    @Inject
    public AnimationManager(
            AssetProvider assetProvider,
            CameraManager cameraManager,
            ObjectManager objectManager,
            PlaybackSettings playbackSettings,
            EngineEventManager eventManager,
            final EventBus eventBus
    ) {
        this.assetProvider = assetProvider;
        this.cameraManager = cameraManager;
        this.objectManager = objectManager;
        this.playbackSettings = playbackSettings;
        this.eventManager = eventManager;
        this.eventBus = eventBus;

        this.eventBus.addSubscriber(new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                switch (eventType) {
                    case SCENE_ANIMATIONS_CHANGED: {
                        eventBus.publish(
                                EventTopic.DEFAULT,
                                EventType.TIMELINE_INFO_UPDATED,
                                exportCurrentSceneAnimationWithoutKeyframes());
                        break;
                    }
                }
            }
        });
    }
    
    /**
     * Resets the animation manager.
     */
    public final void reset(String rootSceneID, String rootBlockID) {
        sceneAnimations.clear();
        addSceneAnimation(rootSceneID, rootBlockID);
        eventBus.publish(EventTopic.DEFAULT, EventType.SCENE_ANIMATIONS_CHANGED);
        setCurrentPlayTime(0);
    }
    
    /**
     * Returns any active input handlers for the animation.
     */
    public final List<InputHandler> getInputHandlers() {
        ArrayList<InputHandler> result = new ArrayList<InputHandler>();
        
        if (animationBlockStatus != null) {
            for (BlockStatus status : animationBlockStatus.values()) {
                if (status.isPlaying()) {
                    for (Block childBlock : status.getBlock().getChildBlocks()) {
                        BlockTrigger trigger = childBlock.getTrigger();

                        if (trigger instanceof InputHandler) {
                            result.add((InputHandler) trigger);
                        }                        
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Adds animation information for a new scene.
     */
    public final void addSceneAnimation(String sceneID, String rootBlockID) {
        sceneAnimations.put(sceneID, new SceneAnimation(sceneID, rootBlockID, objectManager));
    }

    /**
     * convenience method for the undo/redo when you have already-constructed objects.
     * @param scene
     * @param sceneAnimation
     */
    public final void addSceneAnimation(Scene scene, SceneAnimation sceneAnimation) {
        sceneAnimations.put(scene.getName(), sceneAnimation);
    }
    
    /**
     * Removes animation information for the given scene.
     */
    public final void removeSceneAnimation(String sceneID) {
        sceneAnimations.remove(sceneID);
    }
    
    /**
     * Returns the animation information for the given scene.
     */
    public final SceneAnimation getSceneAnimation(String sceneID) {
        return sceneAnimations.get(sceneID);
    }
    
    /**
     * Returns a value indicating whether we're currently playing a single block instead of a full dodle.
     */
    public final boolean isPlayingSingleBlock() {
        return playSingleBlock;
    }
    
    /**
     * Returns a value indicating whether the dodle has animations.
     */
    public final boolean hasAnimations() {
        boolean result = getAllAnimations().size() > 0;
        
        // Temporary until we really work spines into the animation system...
        Collection<DodlesActor> allActors = objectManager.allActors();
        for (DodlesActor da : allActors) {
            if (da instanceof DodlesGroup) {
                if (((DodlesGroup) da).getVisiblePhase().getSpine() != null) {
                    result = true;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Starts animating the entire dodle.
     */
    @Deprecated
    public final void startAnimation() {
        // Temporary until we really work spines into the animation system...
        Collection<DodlesActor> allActors = objectManager.allActors();
        for (DodlesActor da : allActors) {

            if (da instanceof DodlesGroup) {
                if (((DodlesGroup) da).getVisiblePhase().getSpine() != null) {
                    ((DodlesGroup) da).getVisiblePhase().getSpine().animate();
                }
            }
        }
        startAnimation(null, null, 0);
    }

    public final void startAnimation(String sceneID, String blockID) {
        startAnimation(sceneID, blockID, _currentPlayTime);
    }

    /**
     * Starts animating the given scene/block combination.
     */
    public final void startAnimation(String sceneID, String blockID, float time) {
        // Reset any previous animation state
        endAnimation();
        animationBlockStatus = new HashMap<String, BlockStatus>();
        timingStatus = new HashMap<EffectTiming, EffectTimingStatus>();
        effectsPlayed.clear();

        // Determine scene and block to animate
        playSingleBlock = true;
        toggleViewTransparency(true);
        if (sceneID == null) {
            playSingleBlock = false;
            sceneID = objectManager.getScene(1).getName();
        }
        SceneAnimation animation = getSceneAnimation(sceneID);
        Block block = animation.getRootBlock();
        if (blockID != null) {
            block = block.findBlock(blockID);
        }

        // Reset and activate animation block
        block.resetToBaseTransform();
        maxDuration = block.calculateDuration();
        if (time > maxDuration) {
            time = 0;
        }
        activateBlock(sceneID, blockID);

        // "Fast forward" animations to desired play time
        setCurrentPlayTime(time);
        float prevPlaySpeed = playbackSettings.getPlaySpeed();
        playbackSettings.setPlaySpeed(1);
        if (time > 0) {
            HashSet<Float> timesToAnimateTo = new HashSet<Float>();
            timesToAnimateTo.add(time);

            for (EffectChain chain : block.getEffectChains()) {
                for (EffectTiming timing : chain.getAllTimings()) {
                    Float startTime = timing.calculateStartTime();

                    if (startTime < time) {
                        addTimes(timesToAnimateTo, startTime);

                        for (float tweenTime : timing.getEffect().getTweenTimes()) {
                            tweenTime += startTime;

                            if (tweenTime < time) {
                                addTimes(timesToAnimateTo, tweenTime);
                            }
                        }
                    }

                    Float endTime = timing.calculateEndTime();

                    if (endTime < time) {
                        addTimes(timesToAnimateTo, endTime);
                    }
                }
            }

            ArrayList<Float> sortedTimesToAnimateTo = new ArrayList<Float>(timesToAnimateTo);

            Collections.sort(sortedTimesToAnimateTo);
            float prevTime = 0;
            isSeeking = true;

            for (Float curTime : sortedTimesToAnimateTo) {
                for (int i = 0; i < 2; i++) {
                    // Sorta hacky, but run each time twice to make sure any delayed runnables get a chance to run.
                    float deltaTime = curTime - prevTime;

                    act(deltaTime);
                    objectManager.getScene(sceneID).act(deltaTime);
                    cameraManager.getSceneCamera(objectManager.getScene(sceneID)).act(deltaTime);
                    prevTime = curTime;
                }
            }
            isSeeking = false;
        }
        playbackSettings.setPlaySpeed(prevPlaySpeed);
    }
    
    /**
     * Returns a value indicating whether we're in the middle of seeking to a position in the animation.
     */
    public final boolean isSeeking() {
        return isSeeking;
    }
    
    private void addTimes(HashSet<Float> times, float baseTime) {
        float adj = 1f / 60f;
        
        if (baseTime - adj > 0) {
            times.add(baseTime - adj);
        }
        
        times.add(baseTime);
        times.add(baseTime + adj);
    }

    /**
     * Activates a single block in the dodle.
     */
    public final void activateBlock(String sceneID, String blockID) {
        objectManager.setActiveScene(sceneID);
        
        if (animationBlockStatus != null) {
            if (!animationBlockStatus.containsKey(blockID)) {
                Block block = getSceneAnimation(sceneID).getRootBlock();
                
                if (blockID != null) {
                    block = block.findBlock(blockID);
                }
                
                animationBlockStatus.put(blockID, new BlockStatus(block));
            }
            
            final BlockStatus blockStatus = animationBlockStatus.get(blockID);
            blockStatus.setPlaying(true);
            
            playBlock(sceneID, blockID, new Runnable() {
                @Override
                public void run() {
                    blockStatus.setPlaying(false);
                    blockStatus.incrementExecutionCount();
                    
                    if (!playSingleBlock) {
                        checkForEndOfAnimation();
                    }
                }
            });
        }
    }
    
    /**
     * Returns all animations in the dodle.
     */
    public final List<EffectAnimator> getAllAnimations() {
        ArrayList<EffectAnimator> result = new ArrayList<EffectAnimator>();
        
        for (SceneAnimation anim : sceneAnimations.values()) {
            for (Block block : anim.getRootBlock().getAllBlocks()) {
                for (EffectTiming timing : block.allEffects()) {
                    result.add(timing.getEffect());
                }
            }
        }
        
        return result;
    }
    
    /**
     * Allows the animation manager to note the passage of time and act accordingly.
     */
    public final void act(float deltaTime) {
        deltaTime *= playbackSettings.getPlaySpeed();

        ArrayList<DeltaTimeRunnable> cloneRunnables = new ArrayList<DeltaTimeRunnable>(actRunnables);
        actRunnables.clear();

        for (DeltaTimeRunnable runnable : cloneRunnables) {
            runnable.run(deltaTime);
        }        

        boolean didUpdateBlockTime = false;
        if (animationBlockStatus != null) {
            for (BlockStatus status : animationBlockStatus.values()) {
                if (status.isPlaying()) {
                    didUpdateBlockTime = true;
                    for (Block childBlock : status.getBlock().getChildBlocks()) {
                        BlockStatus childStatus = animationBlockStatus.get(childBlock.getBlockId());
                        
                        if (childStatus == null || !childStatus.isPlaying()) {
                            BlockTrigger trigger = childBlock.getTrigger();
                            
                            if (trigger != null && trigger.isTriggered()) {
                                activateBlock(objectManager.getActiveScene().getName(), childBlock.getBlockId());
                            }
                        }
                    }
                    
                    status.incrementCurTime(deltaTime);
                }
            }
        }

        if (didUpdateBlockTime && !isSeeking) {
            incrementCurrentPlayTime(deltaTime);
            if (_currentPlayTime > maxDuration) {
                stopAnimation();
                sendCurrentPlayTimeEvent();
                eventBus.publish(EventTopic.DEFAULT, EventType.ANIMATION_COMPLETE);
                toggleViewTransparency(false);
            }
        }
    }
    
    /**
     * Returns the status for the requested block.
     */
    public final BlockStatus getBlockStatus(String blockID) {
        if (animationBlockStatus != null) {
            return animationBlockStatus.get(blockID);
        }
        
        return null;
    }
    
    /**
     * Returns the time position in the given block.
     */
    public final float getBlockTime(String blockID) {
        BlockStatus status = getBlockStatus(blockID);
        
        if (status != null) {
            return status.getCurTime();
        }
        
        return 0;
    }
    
    private void checkForEndOfAnimation() {
        if (isAnimationComplete()) {
            eventBus.publish(EventTopic.DEFAULT, EventType.ANIMATION_COMPLETE);
            toggleViewTransparency(false);
        }
    }

    /**
     * search through the animationBlockStatus.  return false if a block playing or executionCount < 1.
     * This may or may not be the final state for a completely non-deterministic animation engine.
     * @return
     */
    private boolean isAnimationComplete() {
        boolean isComplete = true;
        
        if (animationBlockStatus != null) {
            for (String key : animationBlockStatus.keySet()) {
                BlockStatus status = animationBlockStatus.get(key);
                if (status.isPlaying() || status.getExecutionCount() < 1) {
                    return false;
                }
            }
        }
        
        return isComplete;
    }
    
    private void playBlock(String sceneID, String blockID, final Runnable blockComplete) {
        final AtomicInteger count = new AtomicInteger(0);
        Block block = getSceneAnimation(sceneID).getRootBlock();
        
        if (blockID != null) {
            block = block.findBlock(blockID);
        }
        
        for (EffectChain effectChain : block.getEffectChains()) {
            count.incrementAndGet();
            playEffectChain(effectChain, new Runnable() {
                @Override
                public void run() {
                    if (count.decrementAndGet() == 0 && blockComplete != null) {
                        blockComplete.run();
                    }
                }
            });
        }
    }
    
    private void playEffectChain(final EffectChain chain, final Runnable chainComplete) {        
        final EffectTiming timing = chain.getCurrentTiming();
        final List<EffectChain> nextLinks = chain.getDependentEffects();
        
        if (!timingStatus.containsKey(timing)) {
            timingStatus.put(timing, timing.createTimingStatus());
        }
        
        final EffectTimingStatus curStatus = timingStatus.get(timing);
        
        if (curStatus.getDelayRemaining() > 0) {
            actRunnables.add(new DeltaTimeRunnable() {
                @Override
                public void run(float deltaTime) {
                    curStatus.decrementDelayRemaining(deltaTime);
                    playEffectChain(chain, chainComplete);
                }
            });
        } else {
            EffectAnimator effect = timing.getEffect();
            effectsPlayed.add(effect);
            effect.startAnimation(new Runnable() {
                @Override
                public void run() {
                    if (nextLinks.isEmpty()) {
                        if (chainComplete != null) {
                            chainComplete.run();
                        }
                    } else {
                        final AtomicInteger count = new AtomicInteger(0);
                        
                        for (EffectChain link : nextLinks) {
                            count.incrementAndGet();
                            
                            playEffectChain(link, new Runnable() {
                                @Override
                                public void run() {
                                    if (count.decrementAndGet() == 0 && chainComplete != null) {
                                        chainComplete.run();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }
    
    /**
     * Looks up the sound with the given key.
     */
    public final Sound getSound(String key) {
        // TODO: When we have dynamic sounds, there should be a command that handles
        // loading the sound into memory and registering it here so it's available for
        // effects.
        return assetProvider.getSound(SpeechAssets.valueOf(key));
    }
    
    /**
     * Stops the animation (but doesn't reset it).
     */
    public final void stopAnimation() {
        actRunnables.clear();
        
        for (EffectAnimator anim : getAllAnimations()) {
            anim.stopAnimation();
        }
        
        if (animationBlockStatus != null) {
            for (BlockStatus status : animationBlockStatus.values()) {
                status.setPlaying(false);
            }
        }
    }
    
    /**
     * Plays a single effect.
     */
    public final void playSingleEffect(EffectAnimator effect, Runnable callback, float delay) {
        effectsPlayed.add(effect);
        effect.startAnimation(callback, delay);
    }

    /**
     * Stop the animation.
     */
    public final void endAnimation() {
        animationBlockStatus = null;
        
        // Reset animations in reverse order...
        Collections.reverse(effectsPlayed);
        
        for (EffectAnimator anim : effectsPlayed) {
            anim.resetAnimation();
        }
        
        effectsPlayed.clear();
        
        // Temporary until we really work spines into the animation system...
        Collection<DodlesActor> allActors = objectManager.allActors();
        for (DodlesActor da : allActors) {
            if (da instanceof DodlesGroup) {
                if (((DodlesGroup) da).getVisiblePhase().getSpine() != null) {
                    ((DodlesGroup) da).getVisiblePhase().getSpine().stop();
                }
            }
        }
    }
    
    /**
     * Exports the animations in this dodle to JSON.
     */
    public final void exportAnimations(Json json) {
        json.writeArrayStart("animations");
        
        for (SceneAnimation sa : sceneAnimations.values()) {
            sa.writeConfig(json);
        }
        
        json.writeArrayEnd();
    }

    public final String exportCurrentSceneAnimationWithoutKeyframes() {
        StringWriter writer = new StringWriter();
        Json json = new Json(JsonWriter.OutputType.json);
        json.setWriter(writer);
        json.setQuoteLongValues(true);
        json.writeObjectStart();
        exportAnimationsWithoutKeyFrames(json);
        json.writeObjectEnd();

        // because we supplied out own JsonWriter / StringWriter, we need to reach deep into the
        // object model of libgdx.Json
        String retVal = json.getWriter().getWriter().toString();

        //have to close the Writer ourselves;
        try {
            json.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retVal;
    }

    /**
     * Exports the animations in this dodle to JSON, without including key frame information
     */
    public final void exportAnimationsWithoutKeyFrames(Json json) {
        json.writeArrayStart("animations");

        for (SceneAnimation sa : sceneAnimations.values()) {
            sa.writeAnimationWithoutKeyFrames(json);
        }

        json.writeArrayEnd();
    }
    
    /**
     * Imports the animations from the JSON into the dodle.
     */
    public final void importAnimations(JsonValue json) {
        JsonValue animations = json.get("animations");
        
        for (int i = 0; i < animations.size; i++) {
            SceneAnimation sa = new SceneAnimation(animations.get(i), this, objectManager);
            sceneAnimations.put(sa.getSceneID(), sa);
        }
    }

    /**
     * set the flag in the Layer/TView that will be used in the draw process to
     * use or block the DisplayMode (layer visibility).
     * @param isBlocked
     */
    private void toggleViewTransparency(boolean isBlocked) {
        List<ObjectManager.SceneData> sceneData = objectManager.allSceneData();

        for (ObjectManager.SceneData s : sceneData) {
            Scene scene = s.getScene();

            SnapshotArray<Actor> layers = scene.getLayers();
            for (Actor a : layers) {
                Layer layer = (Layer) a;
                layer.setBlockDisplayMode(isBlocked);
            }
        }
    }
    
    /**
     * A Runnable that passes through the animation delta time as a parameter.
     */
    private interface DeltaTimeRunnable {
        /**
         * Runs the runnable.
         */
        void run(float deltaTime);
    }
}
