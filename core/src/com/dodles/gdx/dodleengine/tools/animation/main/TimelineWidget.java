package com.dodles.gdx.dodleengine.tools.animation.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.animation.AnimationManager;
import com.dodles.gdx.dodleengine.animation.BlockStatus;
import com.dodles.gdx.dodleengine.animation.EffectAnimator;
import com.dodles.gdx.dodleengine.animation.EffectTiming;
import com.dodles.gdx.dodleengine.animation.EffectType;
import com.dodles.gdx.dodleengine.animation.TimelineEffectGroup;
import com.dodles.gdx.dodleengine.animation.TimelineInfo;
import com.dodles.gdx.dodleengine.editor.DensityManager;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.animation.AnimationTool;
import com.dodles.gdx.dodleengine.tools.animation.EffectIconResolver;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Widget that renders the timeline interface.
 */
@PerDodleEngine
public class TimelineWidget extends Widget {
    private final AnimationManager animationManager;
    private final AnimationTool animationTool;
    private final DodleStageManager stageManager;
    private final EffectIconResolver effectIconResolver;
    private final EngineEventManager eventManager;
    private final ObjectManager objectManager;
    
    private final Color[] timelineColors = {
        Color.BLUE.cpy(),
        Color.FOREST.cpy(),
        Color.RED.cpy(),
        Color.PURPLE.cpy(),
        Color.ORANGE.cpy()
    };
    
    private ShapeRenderer shapeRenderer;
    private Float spliceTime;
    private boolean spliceAdd;
    private EffectTiming activeEffect;
    private TimelineEffectGroup activeEffectGroup;
    private float lastTouchX;
    private ArrayList<EffectIconData> iconsToDraw = new ArrayList<EffectIconData>();
    
    @Inject
    public TimelineWidget(
            AnimationManager animationManager,
            AnimationTool animationTool,
            DodleStageManager stageManager,
            EffectIconResolver effectIconResolver,
            EngineEventManager eventManager,
            ObjectManager objectManager
    ) {
        this.animationManager = animationManager;
        this.animationTool = animationTool;
        this.effectIconResolver = effectIconResolver;
        this.eventManager = eventManager;
        this.stageManager = stageManager;
        this.objectManager = objectManager;
        
        this.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                lastTouchX = x;
                return handleTouch(x);
            }
            
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                handleTouch(x);
                lastTouchX = x;
            }
        });
    }
    
    /**
     * Clears the splice overlay.
     */
    public final void clearSpliceOverlay() {
        spliceTime = null;
    }
    
    /**
     * Sets the amount of time to splice.
     */
    public final void setSpliceTime(float time) {
        spliceTime = time;
    }
    
    /**
     * Sets the splice mode (add or remove time).
     */
    public final void setSpliceMode(boolean add) {
        spliceAdd = add;
    }
    
    /**
     * Sets the active effect.
     */
    public final void setActiveEffect(EffectTiming effect) {
        activeEffect = effect;
        activeEffectGroup = null;
    }
    
    /**
     * Sets the active effect group.
     */
    public final void setActiveEffectGroup(TimelineEffectGroup group) {
        activeEffectGroup = group;
        activeEffect = null;
    }
    
    /**
     * Returns the active effect.
     */
    public final EffectTiming getActiveEffect() {
        return activeEffect;
    }
    
    @Override
    public final void draw(Batch batch, float parentAlpha) {        
        iconsToDraw.clear();
        
        validate();
        batch.end();
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        
        shapeRenderer.setColor(0, 0, 0, 1);
        float width = 1;
        float halfWidth = width / 2;
        shapeRenderer.rectLine(getX() + halfWidth, getY(), getX() + halfWidth, getY() + getHeight(), width);
        shapeRenderer.rectLine(getX() + halfWidth, getY() + getHeight() - halfWidth, getX() + getWidth() - halfWidth, getY() + getHeight() - halfWidth, width);
        shapeRenderer.rectLine(getX() + getWidth() - halfWidth, getY() + getHeight(), getX() + getWidth() - halfWidth, getY(), width);
        shapeRenderer.rectLine(getX() + getWidth() - halfWidth, getY() + halfWidth, getX() + halfWidth, getY() + halfWidth, width);
        
        TimelineInfo info = getTimelineInfo();
        int line = 1;
        
        if (info.getSceneLength() > 0) {            
            for (TimelineEffectGroup eg : info.getAllEffectGroups()) {
                line = drawEffectGroup(eg, line, info);
            }
            
            drawMarker(getX() + getWidth() - getHPadding(), Color.FIREBRICK);
        }
        
        drawMarker(getX() + getHPadding(), Color.FOREST);
        
        float timeMarkerPos = getX() + getHPadding();
        BlockStatus status = animationManager.getBlockStatus(info.getBlock().getBlockId());
        
        if (status != null) {
            timeMarkerPos += getRenderWidth() * (status.getCurTime() / info.getSceneLength());
        }
        
        if (spliceTime != null && spliceAdd) {
            Color color = Color.GREEN.cpy();
            color.a = 0.5f;
            shapeRenderer.setColor(color);
            
            shapeRenderer.rect(timeMarkerPos, getY(), getRenderWidth() * (spliceTime / info.getSceneLength()), getHeight());
        }
        
        drawMarker(timeMarkerPos, Color.GRAY);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        
        for (EffectIconData data : iconsToDraw) {
            batch.draw(data.texture, data.x, data.y, data.width, data.height);
        }
    }
    
    private int drawEffectGroup(TimelineEffectGroup effectGroup, int line, TimelineInfo info) {
        List<List<EffectTiming>> groupTimings = effectGroup.getGroupTimings();
        
        for (List<EffectTiming> lineTimings : groupTimings) {            
            for (EffectTiming timing : lineTimings) {
                float curTime = timing.calculateStartTime();
                
                EffectAnimator effect = timing.getEffect();
                float lineThickness = getLineThickness(info.getThreadCount());
                float effectLength = effect.getEffectLength();
                float startX = getHPadding() + getRenderWidth() * (curTime / info.getSceneLength());
                float endX = startX + getRenderWidth() * (effectLength / info.getSceneLength());
                float lineVPos = getY() + getHeight() - (line * lineThickness * 2);

                Color lineColor = timelineColors[line % timelineColors.length];
                
                if (startX == endX) {
                    startX -= lineThickness / 2;
                    endX += lineThickness / 2;
                }

                if (timing == activeEffect || (activeEffectGroup != null && activeEffectGroup.contains(timing))) {
                    lineColor = Color.WHITE;
                    
                    EffectType type = timing.getEffect().getEffectType();
                    String effectName = timing.getEffect().getEffectName();
                    
                    AtlasRegion region = effectIconResolver.getIcon(type, effectName);
                    
                    if (region != null) {
                        float iconSize = getHeight() / 2;
                        iconsToDraw.add(new EffectIconData(region, getX() + startX, getY() + getHeight() * 0.25f, iconSize, iconSize));
                    }
                }

                shapeRenderer.setColor(lineColor);
                shapeRenderer.rectLine(getX() + startX, lineVPos, getX() + endX, lineVPos, lineThickness);
            }
            
            line++;
        }
        
        return line;
    }
    
    /**
     * Sets the position of the timeline to the given time.
     */
    public final void setTimelinePosition(float time) {        
        animationManager.endAnimation();
        animationTool.getActiveBlock().resetToBaseTransform();
        animationManager.startAnimation(
                animationTool.getScene().getSceneID(),
                animationTool.getActiveBlock().getBlockId(),
                time);
        animationManager.stopAnimation();
        stageManager.updateStateUi();
    }
    
    private boolean handleTouch(float x) {
        if (x <= 0 || x >= getWidth()) {
            return false;
        }
        
        if (activeEffect == null) {
            setTimelineCoordinate(x);
        } else {
            moveActiveEffect(x);
        }
        
        eventManager.fireEvent(EngineEventType.ANIMATION_TIMELINE_POSITION_MOVED_BY_USER);
        
        return true;
    }
    
    private void moveActiveEffect(float x) {
        TimelineInfo info = getTimelineInfo();
        float distance = x - lastTouchX;
        float pct = distance / getRenderWidth();
        float time = info.getSceneLength() * pct;
        float finalDelay = activeEffect.getDelay() + time;
        
        if (finalDelay < 0 && activeEffect.getAfterEffectID() != null) {
            float globalStartTime = activeEffect.calculateStartTime();
            
            // Detach from dependent effect and put on it's own timeline
            activeEffect.setAfterEffect(null);
            activeEffect.setDelay(Math.max(globalStartTime + finalDelay, 0));
        } else {
            activeEffect.setDelay(Math.max(activeEffect.getDelay() + time, 0));
        }
    }
    
    private TimelineInfo getTimelineInfo() {
        return new TimelineInfo(animationTool.getScene(), animationTool.getActiveBlock());
    }
    
    private void setTimelineCoordinate(float x) {
        TimelineInfo info = getTimelineInfo();
        
        float pct = Math.max(0, Math.min(1, (x - getHPadding()) / getRenderWidth()));
        float time = info.getSceneLength() * pct;
        
        setTimelinePosition(time);
    }
    
    private float getHPadding() {
        return getWidth() / 50f;
    }
    
    private float getRenderWidth() {
        return getWidth() - (getHPadding() * 2);
    }
    
    private float getLineThickness(int lanes) {
        float maxThickness = DensityManager.getScale() * 5;
        float thickness = getHeight() / ((float) (lanes + 1) * 2f);
        
        if (thickness > maxThickness) {
            return maxThickness;
        }
        
        return thickness;
    }
    
    private void drawMarker(float xPos, Color color) {
        float baseThickness = getHeight() / 20f;
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(xPos, getY(), xPos, getY() + getHeight(), baseThickness);
        
        float x1 = xPos - baseThickness * 2.5f;
        float x2 = xPos + baseThickness * 2.5f;
        float yTop = getY() + getHeight();
        shapeRenderer.triangle(xPos, getY() + baseThickness * 5, x1, getY(), x2, getY());
        shapeRenderer.triangle(xPos, yTop - baseThickness * 5, x1, yTop, x2, yTop);
    }
    
    /**
     * Stores information needed to render effect icons on the timeline.
     */
    private class EffectIconData {
        private TextureRegion texture;
        private float x;
        private float y;
        private float width;
        private float height;
        
        public EffectIconData(TextureRegion texture, float x, float y, float width, float height) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
