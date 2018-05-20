package com.dodles.gdx.dodleengine.editor.overlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * The corner images are rectangular but the visible portion is a triangle.  Use
 * the alpha channel from the pixmap to determine whether the click is happening
 * on the image or over the alpha channel. If the click is happening over the alpha
 * channel we do not absorb the event so that it will bubble up.
 */
public class CornerImage extends Button {
    private Rectangle selectedBounds, unselectedBounds;
    private Pixmap selectedPixmap, unselectedPixmap;

    public CornerImage(TextureRegion unselectedRegion, TextureRegion selectedRegion, TextureRegion checkedRegion) {
        super(new TextureRegionDrawable(unselectedRegion), new TextureRegionDrawable(selectedRegion), new TextureRegionDrawable(checkedRegion));
        setTextureData(unselectedRegion, selectedRegion);
    }

    public CornerImage(TextureRegion unselectedRegion, TextureRegion selectedRegion) {
        super(new TextureRegionDrawable(unselectedRegion), new TextureRegionDrawable(selectedRegion));
        setTextureData(unselectedRegion, selectedRegion);
    }

    private void setTextureData(TextureRegion unselectedRegion, TextureRegion selectedRegion) {
        TextureData unselectedTextureData = unselectedRegion.getTexture().getTextureData();
        if (!unselectedTextureData.isPrepared()) {
            unselectedTextureData.prepare();
        }
        unselectedRegion.flip(false, true);
        unselectedBounds = new Rectangle(new Rectangle(unselectedRegion.getRegionX(), unselectedRegion.getRegionY() - unselectedRegion.getRegionHeight(), 0, 0));
        unselectedPixmap = unselectedTextureData.consumePixmap();

        TextureData selectedTextureData = selectedRegion.getTexture().getTextureData();
        if (!selectedTextureData.isPrepared()) {
            selectedTextureData.prepare();
        }
        selectedRegion.flip(false, true);
        selectedBounds = new Rectangle(new Rectangle(unselectedRegion.getRegionX(), unselectedRegion.getRegionY() - unselectedRegion.getRegionHeight(), 0, 0));
        selectedPixmap = selectedTextureData.consumePixmap();
    }

    private boolean hitCorner(float x, float y) {
        Rectangle bounds = isChecked() ? selectedBounds : unselectedBounds;
        x += bounds.x;
        y += bounds.y;

        Pixmap pixmap = isChecked() ? selectedPixmap : unselectedPixmap;
        Color color = new Color(pixmap.getPixel((int) x, (int) y));
        return (color.a > 0);
    }

    @Override
    public final Actor hit(float x, float y, boolean touchable) {
        // Do not register a hit on events that happen over the image alpha channel on the corner images
        if (!hitCorner(x, y)) {
            return null;
        } else {
            return super.hit(x, y, touchable);
        }
    }
}