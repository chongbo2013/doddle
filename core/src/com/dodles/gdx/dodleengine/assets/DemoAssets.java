package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.scenegraph.DodleStageManager;
import com.dodles.gdx.dodleengine.tools.animation.AnimationSubtoolState;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;

import javax.inject.Inject;

@PerDodleEngine
public class DemoAssets{

    // Subtool-specific Subsystem References
    private final AssetProvider assetProvider;
    private final DodleStageManager dodleStageManager;

    // Internal Variables
    private boolean initializedAssets = false;

    private TextureRegion TR_backGround;
    private TextureRegion TR_character;
    private TextureRegion TR_touchpoint;
    private TextureRegion TR_movepath;
//    private TextureRegion TR_rotatepath;
    private Pair<TextureRegion, TextureRegion> TR_rotatepath;
    private TextureRegion TR_scalepath;
    private TextureRegion TR_arrow;

    private Image background;
    private Image character;
    private Pair<Image, Image> touchpoint;
    private Image movepath;
    private Pair<Image, Image> rotatepath;
    private Pair<Image, Image> scalepath;
    private Image arrow;

    private Vector2 backgroundSize, characterSize, touchpointSize, movepathSize, rotatepathSize, scalepathSize, arrowSize;

    @Inject
    DemoAssets(AssetProvider assetProvider,
               DodleStageManager dodleStageManager
    ) {
        this.assetProvider = assetProvider;
        this.dodleStageManager = dodleStageManager;
    }

    public void initializeAssets(){
        if(!initializedAssets) {
            System.out.println("_______DemoAssets: Initializing the first time!!!");

            // Get TextureRegion
            Skin skin = assetProvider.getSkin(SkinAssets.DEMO);
            TR_backGround = skin.getRegion("DemoBackground");
            TR_character = skin.getRegion("DemoCharacter");
            TR_touchpoint = skin.getRegion("DemoTouchPoint");
            TR_movepath = skin.getRegion("DemoMovePath");
            TR_rotatepath = new Pair<TextureRegion, TextureRegion>(skin.getRegion("DemoRotatePathFlip"), skin.getRegion("DemoRotatePath"));
            TR_scalepath = skin.getRegion("DemoScalePath");
            TR_arrow = skin.getRegion("DemoArrow");

            TR_backGround.flip(false, true);
            TR_character.flip(false, true);
            TR_touchpoint.flip(false, true);
            TR_movepath.flip(false, true);
            TR_scalepath.flip(false, true);
            TR_arrow.flip(false, true);

            background = new Image(TR_backGround);
            movepath = new Image(TR_movepath);
            character = new Image(TR_character);
            touchpoint = new Pair<Image, Image>(new Image(TR_touchpoint), new Image(TR_touchpoint));
            rotatepath = new Pair<Image, Image>(new Image(TR_rotatepath.getFirst()), new Image(TR_rotatepath.getSecond()));
            scalepath = new Pair<Image, Image>(new Image(TR_scalepath), new Image(TR_scalepath));
            arrow = new Image(TR_arrow);

            // Set up size and position
            Viewport viewport = dodleStageManager.getStage().getViewport();
            float screenWidth = viewport.getScreenWidth();
            float screenHeight = viewport.getWorldHeight();

            // With this scale, the height of character would be 0.7 screenHeight
            float SCALE = screenHeight * 0.65f / character.getHeight();

            backgroundSize = new Vector2(screenWidth, screenHeight);
            characterSize = new Vector2(character.getWidth() * SCALE, character.getHeight() * SCALE);
            touchpointSize = new Vector2(touchpoint.getFirst().getWidth(), touchpoint.getFirst().getHeight());
            movepathSize = new Vector2(movepath.getWidth() * SCALE, movepath.getHeight() * SCALE);
            rotatepathSize = new Vector2(rotatepath.getFirst().getWidth() * SCALE, rotatepath.getFirst().getHeight() * SCALE);
            scalepathSize = new Vector2(scalepath.getFirst().getWidth() * SCALE, scalepath.getFirst().getHeight() * SCALE);
            arrowSize = new Vector2(arrow.getWidth() * SCALE, arrow.getHeight() * SCALE);

            background.setSize(backgroundSize.x, backgroundSize.y);
            character.setSize(characterSize.x, characterSize.y);
            movepath.setSize(movepathSize.x, movepathSize.y);
            rotatepath.getFirst().setSize(rotatepathSize.x, rotatepathSize.y);
            rotatepath.getSecond().setSize(rotatepathSize.x, rotatepathSize.y);
            scalepath.getFirst().setSize(scalepathSize.x, scalepathSize.y);
            scalepath.getSecond().setSize(scalepathSize.x, scalepathSize.y);
            arrow.setSize(arrowSize.x, arrowSize.y);

            initializedAssets = true;
        }
    }

    public void show(AnimationSubtoolState animationSubtoolState){
        dodleStageManager.getStage().addActor(background);

        if(animationSubtoolState == AnimationSubtoolState.MOVE_EFFECT_DEMO){
            dodleStageManager.getStage().addActor(movepath);
        }
        else if(animationSubtoolState == AnimationSubtoolState.ROTATE_EFFECT_DEMO) {
            dodleStageManager.getStage().addActor(rotatepath.getFirst());
            dodleStageManager.getStage().addActor(rotatepath.getSecond());
        }
        else if(animationSubtoolState == AnimationSubtoolState.SCALE_EFFECT_DEMO){
            dodleStageManager.getStage().addActor(scalepath.getFirst());
            dodleStageManager.getStage().addActor(scalepath.getSecond());
            dodleStageManager.getStage().addActor(touchpoint.getSecond());
        }

        dodleStageManager.getStage().addActor(character);

        if(animationSubtoolState == AnimationSubtoolState.SCALE_EFFECT_DEMO)
            touchpoint.getSecond().toFront();
        dodleStageManager.getStage().addActor(touchpoint.getFirst());

        touchpoint.getFirst().setVisible(true);
    }

    public void reset(AnimationSubtoolState animationSubtoolState) {
        background.remove();

        character.remove();
        character.clearActions();

        touchpoint.getFirst().remove();
        touchpoint.getFirst().clearActions();

        if(animationSubtoolState == AnimationSubtoolState.MOVE_EFFECT_DEMO){
            movepath.remove();
        }
        else if(animationSubtoolState == AnimationSubtoolState.ROTATE_EFFECT_DEMO) {
            character = new Image(TR_character);
            character.setSize(characterSize.x, characterSize.y);

            rotatepath.getFirst().remove();
            rotatepath.getFirst().clearActions();

            rotatepath.getSecond().remove();
            rotatepath.getSecond().clearActions();
        }
        else if(animationSubtoolState == AnimationSubtoolState.SCALE_EFFECT_DEMO){
            character = new Image(TR_character);
            character.setSize(characterSize.x, characterSize.y);
            scalepath.getFirst().remove();
            scalepath.getFirst().clearActions();
            scalepath.getSecond().remove();
            scalepath.getSecond().clearActions();

            touchpoint.getSecond().remove();
            touchpoint.getSecond().clearActions();
        }
    }

    public Image getBackground() {
        return background;
    }

    public Image getCharacter() {
        return character;
    }

    public Image getTouchpoint() {
        return touchpoint.getFirst();
    }

    public Image getSecondTouchPoint(){
        return touchpoint.getSecond();
    }

    public Image getMovepath() {
        return movepath;
    }

    public Pair<Image, Image> getRotatepath() {
        return rotatepath;
    }

    public Pair<Image, Image> getScalepath() {
        return scalepath;
    }

    public Image getArrow() {
        return arrow;
    }

    public Vector2 getBackgroundSize() {
        return backgroundSize;
    }

    public Vector2 getCharacterSize() {
        return characterSize;
    }

    public Vector2 getTouchpointSize() {
        return touchpointSize;
    }

    public Vector2 getMovepathSize() {
        return movepathSize;
    }

    public Vector2 getRotatepathSize() {
        return rotatepathSize;
    }

    public Vector2 getScalepathSize() {
        return scalepathSize;
    }

    public Vector2 getArrowSize() {
        return arrowSize;
    }
}