package com.dodles.gdx.dodleengine.scenegraph;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferStack;
import com.dodles.gdx.dodleengine.scenegraph.spine.PolygonRegionAttachment;
import com.esotericsoftware.spine.Slot;

/**
 * Renders an actor to a framebuffer.
 */
public final class FrameBufferRenderer {
    private FrameBufferRenderer() {
    }

    /**
     * copy a texture.
     *//*
    public static TextureRegion copyTextureRegion(TextureRegion texture) {
        TextureRegion tr = new TextureRegion(new Texture(cropTextureRegionToPixmap(texture, 0, 0, texture.getRegionWidth(), texture.getRegionHeight())));
        tr.flip(false, true);
        return tr;
    }

    *//**
     * copy a texture.
     *//*
    public static TextureRegion cropTextureRegion(TextureRegion texture, float x, float y, float w, float h) {
        TextureRegion tr = new TextureRegion(new Texture(cropTextureRegionToPixmap(texture, x, y, w, h)));
        tr.flip(false, true);
        return tr;
    }
*/

   /* *//**
     * crop  a texture.
     *//*
    public static Pixmap cropTextureRegionToPixmap(TextureRegion tr, float x, float y, float w, float h) {
        FrameBuffer drawBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) w, (int) h, false);
        FrameBufferStack.instance().begin(drawBuffer);
        Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

        DodlesSpriteBatch batch = new DodlesSpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);

        batch.enableBlending();
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
        batch.begin();
        batch.draw(tr, -x, -y);

        batch.end();
        int of=0;
        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap((int) of, (int) of, (int) w-2*of, (int) h-2*of);
        Pixmap pixmap=new Pixmap((int)w,(int)h, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.CLEAR);
        pixmap.fill();
        pixmap.drawPixmap(pixmap1,of,of);
        FrameBufferStack.instance().end();
        drawBuffer.dispose();
        return pixmap;
    }*/


    /**
     * Renders the actor to the framebuffer.
     */
    public static TextureRegion renderToTextureRegion(DodlesActor actor) {
        Rectangle bounds = actor.getDrawBounds();
        return renderToTextureRegion(actor, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /**
     * Renders the actor to the framebuffer.
     */
    public static TextureRegion renderToTextureRegion(DodlesActor actor, float x, float y, float w, float h) {
        Pixmap pixmap1 = renderToPixmap(actor, x, y, w, h);
        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        pixmap.drawPixmap(pixmap1, 0, 0);
        Texture texture = new Texture(pixmap);
//        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        TextureRegion cachedTexture = new TextureRegion(texture);
        cachedTexture.flip(false, true);
        return cachedTexture;
    }

    /**
     * Renders the actor to the framebuffer.
     */
    public static Pixmap renderToPixmap(DodlesActor actor) {
        Rectangle bounds = actor.getDrawBounds();
        Pixmap pixmap1 = renderToPixmap(actor, bounds.x, bounds.y, bounds.width, bounds.height);
        Pixmap pixmap = new Pixmap((int) bounds.width, (int) bounds.height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.CLEAR);
        pixmap.fill();
        pixmap.drawPixmap(pixmap1, 0, 0);
        return pixmap;
    }


//    public static Pixmap renderToPixmap(DodlesActor actor, float x, float y, float w, float h, float rotation) {
//        Pixmap render = renderToPixmap(actor, x, y, w, h);
//        FrameBuffer drawBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) w, (int) h, false);
//        FrameBufferStack.instance().begin(drawBuffer);
//        Gdx.gl.glClearColor(1f, .3f, 1f, 0f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
//        DodlesSpriteBatch batch = new DodlesSpriteBatch();
//        batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
//        batch.enableBlending();
//        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
//        batch.begin();
//        batch.draw(new TextureRegion(new Texture(render)), 0, 0, w/2f,h/2f, w, h, 1f, 1f, rotation);
//        batch.end();
//        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap(0, 0, (int) w, (int) h);
//        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);
//        pixmap.setColor(Color.CLEAR);
//        pixmap.fill();
//        pixmap.drawPixmap(pixmap1, 0, 0);
//        FrameBufferStack.instance().end();
//        drawBuffer.dispose();
//        return pixmap;
//    }


    /**
     * Renders the actor to the framebuffer.
     */
    public static Pixmap renderToPixmap(DodlesActor actor, float x, float y, float w, float h) {
        Rectangle bounds = actor.getDrawBounds();
        FrameBuffer drawBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) bounds.width, (int) bounds.height, false);
        FrameBufferStack.instance().begin(drawBuffer);
        Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

        DodlesSpriteBatch batch = new DodlesSpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());

        batch.enableBlending();
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
        batch.begin();

        if (actor instanceof BaseGroup) {
            BaseGroup group = (BaseGroup) actor;
            group.setWorldTransformOverride(new Affine2());
            actor.draw(batch, 1);
            group.setWorldTransformOverride(null);
        } else {
            actor.draw(batch, 1);
        }

        batch.end();
        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap((int) x, (int) y, (int) w, (int) h);
        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);

        pixmap.setColor(Color.CLEAR);
        pixmap.fill();
        pixmap.drawPixmap(pixmap1, 0, 0);
        FrameBufferStack.instance().end();
        drawBuffer.dispose();
        return pixmap;
    }

    /**
     * Calculate power of two bigger than size.
     *
     * @param size
     * @return
     */
    public static int getPowerOfTwo(float size) {
        int result = 2;

        while (size > result) {
            result *= 2;
        }

        return result;
    }

//    public static Pixmap renderToPixmap(DodlesActor source, int minX, int minY, int w, int h, Slot slot) {
//        Pixmap cropped = renderToPixmap(source, minX, minY, w, h);
//
//        int nw = (int) ((PolygonRegionAttachment) slot.getAttachment()).getWidth();
//        int nh = (int) ((PolygonRegionAttachment) slot.getAttachment()).getHeight();
////        Pixmap squeezeInto = new Pixmap(nw, nh, Pixmap.Format.RGBA8888);
//        float rotation = ((PolygonRegionAttachment) slot.getAttachment()).getFinalizedRotation();
//        System.out.println(":::" + w + "," + h + "\t" + nw + "," + nh + "\t" + rotation);
//        //TODO rotate pixmap here?
//        //===================================================================================================
//        FrameBuffer drawBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, nw, nh, false);
//        FrameBufferStack.instance().begin(drawBuffer);
//        Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
//
//        DodlesSpriteBatch batch = new DodlesSpriteBatch();
//        batch.getProjectionMatrix().setToOrtho2D(0, 0, nw, nh);
//
//        batch.enableBlending();
//        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
//        batch.begin();
//        Vector2 diff = new Vector2(w - nw, h - nh);
//        diff.scl(.5f);
//
//        batch.draw(new TextureRegion(new Texture(cropped)), -diff.x, -diff.y, cropped.getWidth() * .5f, cropped.getHeight() * .5f, cropped.getWidth(), cropped.getHeight(), 1, 1, -rotation);
//
//        batch.end();
//        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap((int) 0, (int) 0, (int) nw, (int) nh);
////        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);
////
////        pixmap.setColor(Color.CLEAR);
////        pixmap.fill();
////        pixmap.drawPixmap(pixmap1, 0, 0);
//        FrameBufferStack.instance().end();
//        drawBuffer.dispose();
//        //=======================================================================================================
//        return pixmap1;
//    }

    /**
     * Renders rotated pixmap to another pixmap.
     * @param selectedPixmap
     * @param slot
     * @return
     */
    public static Pixmap renderToPixmap(Pixmap selectedPixmap, Slot slot) {
//        Pixmap cropped = renderToPixmap(source, minX, minY, w, h);

        int nw = (int) ((PolygonRegionAttachment) slot.getAttachment()).getWidth();
        int nh = (int) ((PolygonRegionAttachment) slot.getAttachment()).getHeight();
//        Pixmap squeezeInto = new Pixmap(nw, nh, Pixmap.Format.RGBA8888);
        float rotation = ((PolygonRegionAttachment) slot.getAttachment()).getFinalizedRotation();
        //TODO rotate pixmap here?
        //===================================================================================================
        FrameBuffer drawBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, nw, nh, false);
        FrameBufferStack.instance().begin(drawBuffer);
        Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

        DodlesSpriteBatch batch = new DodlesSpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, nw, nh);

        batch.enableBlending();
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
        batch.begin();
        Vector2 diff = new Vector2(selectedPixmap.getWidth() - nw, selectedPixmap.getHeight() - nh);
        diff.scl(.5f);

        batch.draw(new TextureRegion(new Texture(selectedPixmap)), -diff.x, -diff.y, selectedPixmap.getWidth() * .5f, selectedPixmap.getHeight() * .5f, selectedPixmap.getWidth(), selectedPixmap.getHeight(), 1, 1, -rotation);

        batch.end();
        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap((int) 0, (int) 0, (int) nw, (int) nh);
//        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);
//
//        pixmap.setColor(Color.CLEAR);
//        pixmap.fill();
//        pixmap.drawPixmap(pixmap1, 0, 0);
        FrameBufferStack.instance().end();
        drawBuffer.dispose();
        //=======================================================================================================
        return pixmap1;
    }

//    public static Pixmap renderToPixmap(DodlesActor actor, int x, int y, int w, int h, float rotation) {
//
//        Pixmap capture = renderToPixmap(actor, x, y, w, h);
//        TextureRegion tr = new TextureRegion(new Texture(capture));
//
//        Rectangle bounds = actor.getDrawBounds();
//        FrameBuffer drawBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) bounds.width, (int) bounds.height, false);
//        FrameBufferStack.instance().begin(drawBuffer);
//        Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
//        DodlesSpriteBatch batch = new DodlesSpriteBatch();
//
//        batch.begin();
//        batch.draw(tr,0,0,tr.getRegionWidth()/2f,tr.getRegionHeight()/2f,tr.getRegionWidth(),tr.getRegionHeight(),1f,1f,rotation);
//        batch.end();
//        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap((int) x, (int) y, (int) w, (int) h);
//        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);
//        pixmap.setColor(Color.CLEAR);
//        pixmap.fill();
//        pixmap.drawPixmap(pixmap1, 0, 0);
//        FrameBufferStack.instance().end();
//        drawBuffer.dispose();
//        return pixmap;
//    }


//        Rectangle bounds = actor.getDrawBounds();
//        FrameBuffer drawBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) bounds.width, (int) bounds.height, false);
//        FrameBufferStack.instance().begin(drawBuffer);
//        Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
//
//        DodlesSpriteBatch batch = new DodlesSpriteBatch();
//        batch.getProjectionMatrix().setToOrtho2D(bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
//        batch.enableBlending();
//        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
//        batch.begin();
//
//        if (actor instanceof BaseGroup) {
//            BaseGroup group = (BaseGroup) actor;
//            group.setWorldTransformOverride(new Affine2());
//            actor.draw(batch, 1);
//            group.setWorldTransformOverride(null);
//        } else {
//            actor.draw(batch, 1);
//        }
//
//        batch.end();
//        Pixmap pixmap1 = ScreenUtils.getFrameBufferPixmap((int) x, (int) y, (int) w, (int) h);
//        Pixmap pixmap = new Pixmap((int) w, (int) h, Pixmap.Format.RGBA8888);
//
//        pixmap.setColor(Color.CLEAR);
//        pixmap.fill();
//        pixmap.drawPixmap(pixmap1, 0, 0);
//        FrameBufferStack.instance().end();
//        drawBuffer.dispose();
//        return pixmap;
//    }
}