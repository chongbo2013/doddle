package com.dodles.gdx.dodleengine.brushes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.commands.CommandFactory;
import com.dodles.gdx.dodleengine.scenegraph.GraphicsGenerator;
import com.dodles.gdx.dodleengine.scenegraph.RenderState;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.graphics.Graphics;
import com.dodles.gdx.dodleengine.scenegraph.graphics.direct.DirectTextureGraphics;
import com.dodles.gdx.dodleengine.util.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Common particle brush functionality.
 */
public abstract class AbstractParticleBrush extends AbstractBrush {
    public static final float STAMP_SCALE = 1;
    private final float OPACITY_ADJUSTED_RATIO = 8.5f;
    private final float CACHED_PIXMAP_OPACITY = 0.6f;
    private HashMap<Float, Texture> particleCache = new HashMap<Float, Texture>();

    public AbstractParticleBrush(AssetProvider assetProvider, CommandFactory commandFactory) {
        super(assetProvider, commandFactory);
    }

    /**
     * Returns the size of particles in the brush.
     */
    protected abstract float getParticleSize();

    /**
     * Returns the density of particles in the brush.
     */
    protected abstract float getParticleDensity();

    /**
     * Returns the minimum transparency of particles in the brush.
     */
    protected abstract float getMinTransparency();

    /**
     * Returns the maximum transparency of particles in the brush.
     */
    protected abstract float getMaxTransparency();

    /**
     * Returns the streaking factor of particles in the brush.
     */
    protected abstract float getStreakFactor();

    @Override
    protected final void onMouseMove(Shape shape) {
        final ArrayList<Vector2> points = getNewSmoothedPoints();
        if (points.size() > 0) {
            shape.addGenerator(new GraphicsGenerator() {
                @Override
                public List<Graphics> generateGraphics(Shape shape) {
                    ArrayList<Graphics> result = new ArrayList<Graphics>();
                    Vector2 firstPoint = points.get(0);
                    ParticleBrushRenderState renderState = (ParticleBrushRenderState) shape.getRenderState();
                    float strokeSize = shape.getStrokeConfig().getSize();
                    StrokeConfig strokeConfig = shape.getStrokeConfig();
                    Color originalColor = strokeConfig.getColor();
                    Color adjustedOpacityColor = new Color(originalColor.r, originalColor.g, originalColor.b, originalColor.a/ OPACITY_ADJUSTED_RATIO);
                    float stampSide = strokeSize * STAMP_SCALE;

                    if (renderState == null) {
                        renderState = new ParticleBrushRenderState();
                        shape.setRenderState(renderState);
                    }

                    Random rng = renderState.getRng();

                    if (rng == null) {
                        rng = new Random(((long) firstPoint.x) ^ ((long) firstPoint.y));
                        renderState.setRng(rng);
                    }


                    for (Vector2 point : points) {
                        Texture sprites = generateSprites(strokeConfig);
                        int numSprites = (int) Math.ceil(sprites.getWidth() / stampSide);
                        if(numSprites <= 0) {
                            numSprites = 1;
                        }
                        int spriteIndex = rng.getRandomInt(numSprites);
                        float rotation = 0;

                        if (rng.getRandom(1) < getStreakFactor()) {
                            if (renderState.getStreakSpriteIndex() < 0) {
                                renderState.setStreakSpriteIndex(spriteIndex);
                            } else {
                                spriteIndex = renderState.getStreakSpriteIndex();
                            }

                            Vector2 prevPoint = renderState.getPrevPoint();

                            if (prevPoint == null) {
                                prevPoint = point;
                            }

                            rotation = point.angle(prevPoint);

                            renderState.setPrevPoint(point);
                        } else {
                            renderState.setStreakSpriteIndex(-1);
                            rotation = rng.getRandom(0, 360);
                        }

                        result.add(new DirectTextureGraphics(sprites, spriteIndex * stampSide, 0f, stampSide, stampSide, point.x - strokeSize / 2f, point.y - strokeSize / 2f, (float) strokeSize, strokeSize, rotation, adjustedOpacityColor));
                    }

                    return result;
                }
            });
        }
    }

    @Override
    protected final boolean keepDuplicatePoints() {
        return true;
    }

    /**
     * Generates the sprites for the particle brush.
     */
    protected final Texture generateSprites(StrokeConfig strokeConfig) {
        float strokeSize = strokeConfig.getSize();
        float particleSize = getParticleSize();
        float particleDensity = getParticleDensity();

        if (!particleCache.containsKey(strokeSize)) {
            Random rng = new Random(Math.round(strokeSize));
            
            /* The overlap ratio factors in how much overlap will be caused by the size of the brush.
                The ratio is (x = stroke size):

                Total area drawn in x stamps, the area of the entire stamp counted
                ------------------------------------------------------------------
                Actual area drawn on screen for x stamps

                Stroke size 1: 1 / 1 = 1
                Stroke size 5: 5 * 5 * 5 / 5 * 5 + 5 * 4 = 2.77
            */
            float overlapRatio = (float) Math.pow(strokeSize, 3) / (float) ((Math.pow(strokeSize, 2) * 2) - strokeSize);

            // Size ratio is simply the area of the particle excluding PI
            float sizeRatio = (float) Math.pow(particleSize / 2, 2);
            float finalParticleChance = particleDensity / (overlapRatio * sizeRatio);
            int numParticles = (int) Math.ceil(Math.pow(strokeSize, 2) * finalParticleChance);
            float outerParticleBound = Math.max((strokeSize / 2) - (particleSize / 2), 0);

            ArrayList<Float> distances = new ArrayList<Float>();

            for (float i = particleSize / 4f; i <= outerParticleBound; i += particleSize / 8f) {
                distances.add(i / outerParticleBound);
            }

            if (distances.isEmpty()) {
                distances.add(0.1f);
            }

            rng.shuffle(distances);

            int spriteNum = 0;
            int curSpriteParticleCount = 0;
            int numPartitions = (int) Math.ceil((float) distances.size() / (float) numParticles);
            Pixmap cache = new Pixmap((int) (numPartitions * strokeSize * STAMP_SCALE), (int) (strokeSize * STAMP_SCALE), Format.RGBA8888);

            for (float unadjDistance : distances) {
                if (curSpriteParticleCount == numParticles) {
                    spriteNum++;
                    curSpriteParticleCount = 0;
                }

                float angle = 2f * (float) Math.PI * rng.getRandom(1);
                float distance = Interpolation.circleOut.apply(unadjDistance) * outerParticleBound;
                int x = (int) (strokeSize / 2 + ((float) Math.cos(angle) * distance));
                int y = (int) (strokeSize / 2 + ((float) Math.sin(angle) * distance));


                Color color = new Color(1, 1, 1, CACHED_PIXMAP_OPACITY);
                cache.setColor(color);
                int radius = (int) Math.round(particleSize * STAMP_SCALE / 2f);
                int cx = (int) ((x + spriteNum * strokeSize) * STAMP_SCALE);
                int cy = (int) (y * STAMP_SCALE);
                    
                if (radius <= 0) {
                    plot(cx, cy, cache, color);
                } else {
                    cache.fillCircle(cx, cy, radius);
                    drawCircle(cx, cy, radius, cache, color);
                }

                curSpriteParticleCount++;
            }

            Texture tex = new Texture(cache);
            particleCache.put(strokeSize, tex);
            cache.dispose();
        }

        return particleCache.get(strokeSize);
    }

    /**
     * State to be saved across draw calls for particle brushes.
     */
    protected class ParticleBrushRenderState implements RenderState {
        private int streakSpriteIndex = -1;
        private Vector2 prevPoint;
        private Random rng;

        /**
         * Returns the index of the current streaking sprite.
         */
        public final int getStreakSpriteIndex() {
            return streakSpriteIndex;
        }

        /**
         * Sets the index of the current streaking sprite.
         */
        public final void setStreakSpriteIndex(int newStreakSpriteIndex) {
            streakSpriteIndex = newStreakSpriteIndex;
        }

        /**
         * Returns the previous point.
         */
        public final Vector2 getPrevPoint() {
            return prevPoint;
        }

        /**
         * Sets the previous point.
         */
        public final void setPrevPoint(Vector2 newPrevPoint) {
            prevPoint = newPrevPoint;
        }

        /**
         * Returns the RNG for the particle brush.
         */
        public final Random getRng() {
            return rng;
        }

        /**
         * Sets the RNG for the particle brush.
         */
        public final void setRng(Random newRng) {
            rng = newRng;
        }

        @Override
        public final void onRegenerate() {
            streakSpriteIndex = -1;
            prevPoint = null;
            rng = null;
        }
    }

    /**
     * Draws an antialiased circle.
     */
    public final void drawCircle(int x, int y, float r, Pixmap pixmap, Color color) {
        double rsq = r * r;
        int ffd = (int) Math.round(r / Math.sqrt(2));
        double xj, yj;
        double frc;
        int flr;
        for (int xi = 0; xi <= ffd; xi++) {
            yj = Math.sqrt(rsq - xi * xi);  // the "step 2" formula noted above
            flr = (int) yj;
            frc = yj - flr;
            //plot_4_points(x, y, xi,  flr, 1d -  frc, pixmap, color);
            plot4Points(x, y, xi, (flr + 1), frc, pixmap, color);
        }
        for (int yi = 0; yi <= ffd; yi++) {
            xj = Math.sqrt(rsq - yi * yi);
            flr = (int) xj;
            frc = xj - flr;
            //plot_4_points(x, y,  flr, yi, 1d - frc, pixmap, color);
            plot4Points(x, y, (flr + 1), yi, frc, pixmap, color);
        }
    }

    /**
     * Plots a point.
     */
    public final void plot(int x, int y, Pixmap pixmap, Color color) {
        pixmap.drawPixel((int) x, (int) y, Color.rgba8888(color));
    }

    /**
     * Plots 4 points around a center position.
     */
    public final void plot4Points(int x, int y, int dx, int dy, double f, Pixmap pixmap, Color color) {
        Color c = new Color(color);
        c.a = (float) (color.a * f);
        plot(x + dx, y + dy, pixmap, c);
        plot(x - dx, y + dy, pixmap, c);
        plot(x + dx, y - dy, pixmap, c);
        plot(x - dx, y - dy, pixmap, c);
    }
}
