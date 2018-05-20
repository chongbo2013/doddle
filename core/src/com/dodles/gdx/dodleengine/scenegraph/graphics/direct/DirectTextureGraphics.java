package com.dodles.gdx.dodleengine.scenegraph.graphics.direct;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.dodles.gdx.dodleengine.scenegraph.graphics.GraphicsRendererType;

/**
 * Draws a texture direct to the batch.
 */
public class DirectTextureGraphics implements DirectGraphics {
    private final Texture textureToDraw;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final float dstX;
    private final float dstY;
    private final float dstWidth;
    private final float dstHeight;
    private final float rotation;
    private final Color color;
    
    private Rectangle bounds;

    public DirectTextureGraphics(Texture textureToDraw, float dstX, float dstY, float dstWidth, float dstHeight, float rotation, Color color) {
        this(textureToDraw, 0, 0, textureToDraw.getWidth(), textureToDraw.getHeight(), dstX, dstY, dstWidth, dstHeight, rotation, color);
    }

    public DirectTextureGraphics(Texture textureToDraw, float x, float y, float width, float height, float dstX, float dstY, float dstWidth, float dstHeight, float rotation, Color color) {
        this.textureToDraw = textureToDraw;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dstX = dstX;
        this.dstY = dstY;
        this.dstWidth = dstWidth;
        this.dstHeight = dstHeight;
        this.rotation = rotation;
        this.color = color;
    }

    @Override
    public final void draw(Batch batch, float parentAlpha) {
        batch.setColor(color.r, color.g, color.b, parentAlpha * color.a);

        batch.draw(
                textureToDraw,
                dstX,
                dstY,
                (dstWidth / 2), // origin
                (dstHeight / 2),
                dstWidth,
                dstHeight,
                1, // scale
                1,
                rotation,
                (int) x,
                (int) y,
                (int) width,
                (int) height,
                false,
                true
        );
        
        batch.setColor(Color.WHITE);
    }

    @Override
    public final GraphicsRendererType getRendererType() {
        return GraphicsRendererType.Direct;
    }

    @Override
    public final Rectangle getBounds() {
        if (this.bounds == null) {
            //calculate bounds of rotated rectangle around center
            float cx = dstX + dstWidth / 2f;
            float cy = dstY + dstHeight / 2f;
            float sin = (float) Math.sin(MathUtils.degRad * rotation);
            float cos = (float) Math.cos(MathUtils.degRad * rotation);

            if (sin < 0) {
                sin = -sin;
            }

            if (cos < 0) {
                cos = -cos;
            }

            float finalW = dstHeight * sin + dstWidth * cos;
            float finalH = dstHeight * cos + dstWidth * sin;

            this.bounds = new Rectangle(cx - finalW / 2, cy - finalH / 2, finalW, finalH);
        }
        
        return this.bounds;
    }
}
