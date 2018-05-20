package com.dodles.gdx.dodleengine.editor.full.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.SkinAssets;
import com.dodles.gdx.dodleengine.editor.OkCancelStackManager;
import com.dodles.gdx.dodleengine.editor.full.FullEditorInterface;
import com.dodles.gdx.dodleengine.editor.utils.Dimensions;
import com.dodles.gdx.dodleengine.editor.utils.DodlesUIUtil;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;


/**
 * Encapsulates the functionality of the Dodles App Header Bar.
 *
 * On Desktop, this renders placeholder header bar to contain the Ok Stack and Cancel Buttons.
 * On deployed builds (iOS, Android, Web), this just renders a blank background, needed to take the space required by
 * the real header bar to properly reduce the available editor drawing space.
 */
public class HeaderBar extends Table {

    // region Variables

    // Constants
    private static final float HEADER_SCREEN_HEIGHT_PERCENT = 0.09f;
    private static final float HEADER_SIDE_SCREEN_WIDTH_PERCENT = 0.25f;
    private static final float LOGO_SCREEN_WIDTH_PERCENT = 0.5f;
    private static final String BACKGROUND_STYLE = "grey_lt";
    private static final String BORDER_BACKGROUND_STYLE = "grey_md";
    private static final int BORDER_SIZE = 1;

    // Internal UI references
    private OkCancelStackManager okCancelStack;
    private EventBus eventBus;
    private AssetProvider assetProvider;
    private boolean renderFullHeader;
    private Skin skin;

    // endregion Variables


    // region Constructors
    /**
     * Constructor for the HeaderBar UI.
     *
     * @param skin primary UI Skin used for rendering the header bar
     * @param renderFullHeader boolean indicating whether or not to render all the elements of the header.
     * @param assetProvider reference to the AssetProvider, required to render the full header
     */
    public HeaderBar(
            OkCancelStackManager okCancelStack,
            EventBus eventBus,
            Skin skin,
            boolean renderFullHeader,
            AssetProvider assetProvider
    ) {
        super(skin);
        this.okCancelStack = okCancelStack;
        this.eventBus = eventBus;
        this.skin = skin;
        this.renderFullHeader = renderFullHeader;
        this.assetProvider = assetProvider;
        initialize();
    }
    // endregion Constructors


    // region Initialization

    private void initialize() {

        this.background(skin.getDrawable(BACKGROUND_STYLE));

        Table headerContent = new Table(skin);
        this.add(headerContent).expand().fill();

        if (renderFullHeader) {

            // NOTE: we use a separate atlas because this is only rendered in the desktop version, and its assets would
            // needlessly bloat the core editor atlas
            final Skin headerContentSkin = assetProvider.getSkin(SkinAssets.EDITOR_UI_HEADER_SKIN);

            // Hamburger Menu Button
            float maxMenuWidth = Gdx.graphics.getWidth() * HEADER_SIDE_SCREEN_WIDTH_PERCENT;
            ImageButton menuButton = createHeaderButton(headerContentSkin, "menu", maxMenuWidth, "dodles.myProfile");
            headerContent.add(menuButton).width(menuButton.getWidth()).height(menuButton.getHeight());

            // Logo Home Button
            float logoWidth = Gdx.graphics.getWidth() * LOGO_SCREEN_WIDTH_PERCENT;
            ImageButton logoButton = createHeaderButton(headerContentSkin, "logo", logoWidth, "dodles.home");
            headerContent.add(logoButton).width(logoButton.getWidth()).height(logoButton.getHeight()).expandX();

            // Cancel Button
            float maxCancelWidth = Gdx.graphics.getWidth() * HEADER_SIDE_SCREEN_WIDTH_PERCENT / 2;
            final ImageButton cancelButton = new ImageButton(headerContentSkin, "cancel");
            rescaleKeepingAspectRatio(cancelButton, maxCancelWidth, getMaxHeaderHeight());
            headerContent.add(cancelButton).width(cancelButton.getWidth()).height(cancelButton.getHeight());
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    okCancelStack.popCancel();
                }
            });

            // Okay Button
            float maxOkayWidth = Gdx.graphics.getWidth() * HEADER_SIDE_SCREEN_WIDTH_PERCENT / 2;
            final ImageButton okayButton = new ImageButton(headerContentSkin, "okay_0");
            rescaleKeepingAspectRatio(okayButton, maxOkayWidth, getMaxHeaderHeight());
            headerContent.add(okayButton).width(okayButton.getWidth()).height(okayButton.getHeight());
            okayButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    FullEditorInterface.setToolFinished(true);
                    okCancelStack.popOk();
                }
            });

            // Event listener for when the OK / Cancel Stack Button States Change
            eventBus.addSubscriber(new EventSubscriber(EventTopic.DEFAULT) {
                @Override
                public void listen(EventTopic eventTopic, EventType eventType, EventData data) {
                    if (eventType.equals(EventType.OK_CANCEL_STACK_CHANGED)) {
                        boolean cancelVisible = false;
                        boolean okayVisible = false;
                        int stackSize = okCancelStack.size();

                        if (stackSize > 0) {
                            if (okCancelStack.getCurFrame().hasCancel()) {
                                cancelVisible = true;
                            }
                            okayVisible = true;
                        }

                        String okayStyle = "okay_" + (stackSize % 6);
                        okayButton.setStyle(headerContentSkin.get(okayStyle, ImageButton.ImageButtonStyle.class));

                        // Set button visibility
                        cancelButton.setVisible(cancelVisible);
                        okayButton.setVisible(okayVisible);
                    }
                }

                // NOTE: I did not add the feed and editor buttons, as they dont seem to do anything in the desktop
                // editor. If those needed to be added back in, refer to the original code in the FullEditorInterface's
                // initHeader function, in the commits before this refactoring. - CAD 2017.04.20
            });
        }

        // Add bottom border
        this.row();
        Image bottomBorder = new Image(skin.getDrawable(BORDER_BACKGROUND_STYLE));
        this.add(bottomBorder).height(BORDER_SIZE).expandX().fillX();

        // Add event listener so that stray clicks don't pass through to the canvas
        // TODO: ensure header bar works on device. If not, may need to be moved within the renderFullHeader section
        this.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }

    // endregionInitialization


    // region Helper Functions

    private ImageButton createHeaderButton(Skin buttonSkin, String styleName, float maxWidth, final String eventState) {
        ImageButton button = new ImageButton(buttonSkin, styleName);
        rescaleKeepingAspectRatio(button, maxWidth, getMaxHeaderHeight());

        if (eventState != null && !eventState.isEmpty()) {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    eventBus.publish(EventTopic.DEFAULT, EventType.STATE_GO, "dodles.myProfile");
                }
            });
        }
        return button;
    }

    /**
     * Rescales the input actor to within the sizes specified by the max width and max height, but ensures the original
     * aspect ratio of the actor is maintained.
     * @param actor the actor to be scaled
     * @param maxWidth the desired width after scaling
     * @param maxHeight the desired height after scaling
     */
    private void rescaleKeepingAspectRatio(Actor actor, float maxWidth, float maxHeight) {
        Dimensions newDimensions = DodlesUIUtil.computeDimensionsWithAspectRatio(
                actor.getWidth(), actor.getHeight(), maxWidth, maxHeight);

        actor.setWidth(newDimensions.getWidth());
        actor.setHeight(newDimensions.getHeight());

    }

    /**
     * Returns the maximum height that the header can occupy.
     * @return maximum height of the header bar
     */
    public static float getMaxHeaderHeight() {
        return Gdx.graphics.getHeight() * HEADER_SCREEN_HEIGHT_PERCENT;
    }

    /**
     * Returns a boolean indicating whether or not the header should be rendered.
     * @return
     */
    public final boolean getRenderFullHeader() {
        return this.renderFullHeader;
    }

    // endregion Helper Functions
}
