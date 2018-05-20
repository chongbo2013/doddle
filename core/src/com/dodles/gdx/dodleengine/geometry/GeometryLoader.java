package com.dodles.gdx.dodleengine.geometry;

import com.dodles.gdx.dodleengine.geometry.circle.CircleGeometry;
import com.dodles.gdx.dodleengine.geometry.custom.CustomGeometry;
import com.dodles.gdx.dodleengine.geometry.heart.HeartGeometry;
import com.dodles.gdx.dodleengine.geometry.polygon.PolygonGeometry;
import com.dodles.gdx.dodleengine.geometry.rectangle.RectangleGeometry;
import com.dodles.gdx.dodleengine.geometry.star.StarGeometry;
import com.dodles.gdx.dodleengine.geometry.triangle.TriangleGeometry;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import javax.inject.Inject;

/**
 * Forces eager loading and registration of geometries.
 */
@PerDodleEngine
public class GeometryLoader {
    @Inject
    public GeometryLoader(
        RectangleGeometry rectangle,
        CircleGeometry circle,
        PolygonGeometry polygon,
        TriangleGeometry triangle,
        HeartGeometry heart,
        StarGeometry star,
        CustomGeometry CustomShape
    ) {
    }
}
