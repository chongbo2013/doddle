package com.dodles.gdx.dodleengine.editor.overlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.brushes.Brush;
import com.dodles.gdx.dodleengine.brushes.BrushConfig;
import com.dodles.gdx.dodleengine.brushes.BrushRegistry;
import com.dodles.gdx.dodleengine.brushes.PencilBrush;
import com.dodles.gdx.dodleengine.brushes.RulerMode;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DrawStrokeOverlay {
    protected final BrushRegistry brushRegistry;
    protected final ObjectManager objectManager;
    protected final FrameBufferAtlasManager atlasManager;
    protected final DodleStageManager stageManager;

    protected ArrayList<Vector2> points = new ArrayList<Vector2>();
    protected ArrayList<Shape> shapes = new ArrayList<Shape>();
    protected RulerMode rulerMode = RulerMode.NONE;
    protected static final int DASH_DISTANCE = 30;
    protected static final int SPACE_DISTANCE = 15;
    protected boolean draw = true;

    @Inject
    public DrawStrokeOverlay(
            BrushRegistry brushRegistry,
            ObjectManager objectManager,
            FrameBufferAtlasManager atlasManager,
            DodleStageManager stageManager
    ) {
        this.brushRegistry = brushRegistry;
        this.objectManager = objectManager;
        this.atlasManager = atlasManager;
        this.stageManager = stageManager;
    }

    public void setPoints(List<Vector2> points) {
        clear();
        for (Vector2 point : points ) {
            update(point);
        }
    }

    /**
     * Adds the new point to the list of points
     * Once there's enough distance to create a dash (or space between dashes),
     * the dash/space will be created and points is cleared
     * If points are created too far apart, it will interpolate where the end points should be
     * and loop back to create more dashes as needed
     */
    public void update(Vector2 point) {
        points.add(point);

        int distance = draw ? DASH_DISTANCE : SPACE_DISTANCE;
        float currentDistance = point.dst(points.get(0));

        if (points.size() > 2 && currentDistance >= distance) {

            float dashes = currentDistance / DASH_DISTANCE;

            do {
                ArrayList<Vector2> pointsToReAdd = new ArrayList<Vector2>();

                if (dashes > 1.3f) {

                    // If points are too far for the dash, they're taken out re-added next loop
                    for (Iterator<Vector2> iterator = points.iterator(); iterator.hasNext(); ) {
                        Vector2 p = iterator.next();
                        if (p.dst(points.get(0)) > distance) {
                            pointsToReAdd.add(p);
                            iterator.remove();
                        }
                    }

                    // And then create new mid and end points
                    points.add(new Vector2(points.get(0)).interpolate(pointsToReAdd.get(pointsToReAdd.size() - 1), 1 / dashes / 2, Interpolation.linear));
                    points.add(new Vector2(points.get(0)).interpolate(pointsToReAdd.get(pointsToReAdd.size() - 1), 1 / dashes, Interpolation.linear));

                    dashes -= 1;
                } else {
                    dashes = 0;
                }

                if (draw) {
                    Vector2 first = points.get(0);
                    Vector2 middle = points.get((int) Math.ceil((double) points.size() / 2.0));
                    Vector2 last = points.get(points.size() - 1);

                    // Adding a point for padding on each side of the dash seems to help it be drawn most accurately
                    float verticalPadding = 0;
                    float horizontalPadding = 0;
                    if (Math.abs(first.x - last.x) > Math.abs(first.y - last.y)) {
                        horizontalPadding = .00001f * (first.x - last.x);
                    } else {
                        verticalPadding = .00001f * (first.y - last.y);
                    }

                    // Simply need the end points and mid point to create the dash
                    ArrayList<Vector2> dash = new ArrayList<Vector2>();
                    dash.add(first);
                    dash.add(new Vector2(first.x - horizontalPadding, first.y - verticalPadding)); // Front point padding
                    dash.add(middle);
                    dash.add(last);
                    dash.add(new Vector2(last.x - horizontalPadding, last.y - verticalPadding)); // End point padding

                    createStroke(new ArrayList<Vector2>(dash));
                }

                draw = !draw;
                distance = (distance == DASH_DISTANCE) ? SPACE_DISTANCE : DASH_DISTANCE;

                Vector2 lastPoint = points.get(points.size() - 1);
                points.clear();
                points.add(lastPoint);
                points.addAll(pointsToReAdd);
            }
            while (dashes > 1);
        }
    }

    private void createStroke(ArrayList<Vector2> path) {
        String id = UUID.uuid();

        Brush brush = brushRegistry.getBrush(PencilBrush.BRUSH_NAME);
        StrokeConfig strokeConfig = createStrokeConfig(brush);

        Shape shape = new Shape(id, objectManager.getTrackingID(), atlasManager);
        shapes.add(shape);
        shape.setStrokeConfig(strokeConfig);

        brush.init(shape, rulerMode);

        stageManager.getDrawGroup().addActor(shape);

        for (Vector2 point : path) {
            brush.mouseMove(point);
        }

        finalizeStroke(shape, brush, rulerMode);
    }

    public void clear() {
        for (Shape shape : shapes) {
            stageManager.getDrawGroup().removeActor(shape);
            shape.dispose();
        }
        shapes.clear();
        points.clear();
    }

    private StrokeConfig createStrokeConfig(Brush brush) {
        StrokeConfig strokeConfig = brush.getBrushStrokeConfig();
        strokeConfig.setColor(Color.RED);
        strokeConfig.setSize(10);
        return strokeConfig;
    }

    private void finalizeStroke(Shape shape, Brush brush, RulerMode rulerMode) {
        BrushConfig newConfig = new BrushConfig();
        newConfig.setPoints(points);
        newConfig.setBrush(brush.getName());
        newConfig.setRulerMode(rulerMode);
        shape.setCustomConfig(newConfig);
    }
}
