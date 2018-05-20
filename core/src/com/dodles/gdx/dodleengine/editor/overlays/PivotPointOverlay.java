package com.dodles.gdx.dodleengine.editor.overlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.dodles.gdx.dodleengine.ObjectManager;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SkinAssets;
import com.dodles.gdx.dodleengine.assets.TextureAtlasAssets;
import com.dodles.gdx.dodleengine.brushes.Brush;
import com.dodles.gdx.dodleengine.brushes.BrushConfig;
import com.dodles.gdx.dodleengine.brushes.BrushRegistry;
import com.dodles.gdx.dodleengine.brushes.PencilBrush;
import com.dodles.gdx.dodleengine.brushes.RulerMode;
import com.dodles.gdx.dodleengine.scenegraph.CommonActorOperations;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.scenegraph.DodlesActor;
import com.dodles.gdx.dodleengine.scenegraph.Shape;
import com.dodles.gdx.dodleengine.scenegraph.StrokeConfig;
import com.dodles.gdx.dodleengine.scenegraph.graphics.FrameBufferAtlasManager;
import de.hypergraphs.hyena.core.shared.data.UUID;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PivotPointOverlay {
    protected final BrushRegistry brushRegistry;
    protected final ObjectManager objectManager;
    protected final FrameBufferAtlasManager atlasManager;
    protected final DodleStageManager stageManager;
    protected final AssetProvider assetProvider;

    protected ArrayList<Vector2> points = new ArrayList<Vector2>();
    protected ArrayList<Shape> shapes = new ArrayList<Shape>();
    protected RulerMode rulerMode = RulerMode.NONE;
    protected static final int pivotPointWidth = 50;
    protected static final int pivotPointHeight = 50;
    protected boolean hasInitialized = false;
    protected Image pivotPointImage;
    private Vector2 lastLocation;
    @Inject
    public PivotPointOverlay(
            BrushRegistry brushRegistry,
            AssetProvider assetProvider,
            ObjectManager objectManager,
            FrameBufferAtlasManager atlasManager,
            DodleStageManager stageManager
    ) {
        this.brushRegistry = brushRegistry;
        this.objectManager = objectManager;
        this.atlasManager = atlasManager;
        this.stageManager = stageManager;
        this.assetProvider = assetProvider;
    }


    public void initializePivotPoint() {
        if(!hasInitialized) {
            Skin skin = assetProvider.getSkin(SkinAssets.UI);
            TextureRegion pivotPointTR = skin.getRegion("PivotPointImage");
            pivotPointImage = new Image(pivotPointTR);
            pivotPointImage.setSize(pivotPointWidth, pivotPointHeight);
            stageManager.getStage().addActor(pivotPointImage);
            hasInitialized = true;
        }
    }

    public void update(Vector2 point) {
        pivotPointImage.setPosition(point.x - pivotPointWidth/2, point.y - pivotPointHeight/2);
        lastLocation = point;
    }

    public Vector2 getLastLocation() {
        return lastLocation;
    }

    public void displayOverlay(boolean value) {
        pivotPointImage.setVisible(value);
    }

}
