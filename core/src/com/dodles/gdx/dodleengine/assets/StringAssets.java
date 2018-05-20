package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.assets.AssetManager;

/**
 * Defines all string assets in the dodle engine.
 */
public enum StringAssets implements LoadableAsset {
    ANIMATION_DEFAULT_EFFECT_DEFINITIONS("editor/tool/animation/defaulteffectdefinitions.json"),
    
    TEMPLATE_FULL_EDITOR("templates/editor/full/fulleditor.lml"),
    TEMPLATE_FULL_EDITOR_ACTOR_MANAGEMENT_ROW("templates/editor/full/animation/actormanagementrow.lml"),
    TEMPLATE_FULL_EDITOR_BLOCK_MANAGEMENT_ROW("templates/editor/full/animation/blockmanagementrow.lml"),
    TEMPLATE_FULL_EDITOR_CHEST_CHARACTER_ROW("templates/editor/full/chest/chestmanagementrow.lml"),
    TEMPLATE_FULL_EDITOR_LAYER_ADD_TO_CHEST_MODAL("templates/editor/full/layer/addtochestmodal.lml"),
    TEMPLATE_FULL_EDITOR_OK_CANCEL("templates/editor/full/okcancel.lml"),
    TEMPLATE_FULL_EDITOR_THREE_ROW_OVERLAY("templates/editor/full/threerowoverlay.lml"),
    TEMPLATE_FULL_EDITOR_COLOR_SELECTOR_OVERLAY("templates/editor/full/colorselectoroverlay.lml"),
    TEMPLATE_FULL_EDITOR_EMPTY_OVERLAY("templates/editor/full/emptyoverlay.lml"),
    TEMPLATE_FULL_EDITOR_PLAY_OVERLAY("templates/editor/full/play/playoverlay.lml"),
    TEMPLATE_FULL_EDITOR_PLAY_BLOCK_OVERLAY("templates/editor/full/animation/playblockoverlay.lml"),
    TEMPLATE_FULL_EDITOR_SCENE_MANAGEMENT_ROW1("templates/editor/full/scene/scenemanagementrow1.lml"),
    TEMPLATE_FULL_EDITOR_SCENE_MANAGEMENT_ROW2("templates/editor/full/scene/scenemanagementrow2.lml"),
    TEMPLATE_FULL_EDITOR_SLIDER_OVERLAY("templates/editor/full/slideroverlay.lml"),
    TEMPLATE_FULL_EDITOR_ONE_SLIDER_ROW("templates/editor/full/onesliderrow.lml"),
    TEMPLATE_FULL_EDITOR_TWO_SLIDER_ROW("templates/editor/full/twosliderrow.lml"),
    TEMPLATE_FULL_EDITOR_THREE_SLIDER_ROW("templates/editor/full/threesliderow.lml"),
    TEMPLATE_FULL_EDITOR_FOUR_SLIDER_ROW("templates/editor/full/foursliderow.lml"),
    TEMPLATE_FULL_EDITOR_SCROLLPANE_OVERLAY("templates/editor/full/scrollPaneOverlay.lml"),
    
    TEMPLATE_FULL_EDITOR_PHASES_BASIC_VIEW("templates/editor/full/phases/basicmanagerview.lml"),
    TEMPLATE_FULL_EDITOR_PHASES_ADVANCED_VIEW("templates/editor/full/phases/advancedmanagerview.lml"),
    TEMPLATE_FULL_EDITOR_PHASES_CONFIG_VIEW("templates/editor/full/phases/configmanagerview.lml"),
    TEMPLATE_FULL_EDITOR_PHASES_FALL_BACK_VIEW("templates/editor/full/phases/fallbackmanagerview.lml"),
    TEMPLATE_FULL_EDITOR_PHASES_CONFIG_SETTINGS_VIEW("templates/editor/full/phases/configsettingsview.lml"),
    TEMPLATE_FULL_EDITOR_PHASES_SETTING_ADDPHASE_VIEW("templates/editor/full/phases/newphasesettingsview.lml"),
    TEMPLATE_FULL_EDITOR_PHASES_SETTING_MODIFYPHASE_VIEW("templates/editor/full/phases/modifyphasesettingsview.lml"),
    
    TEMPLATE_INLINE_EDITOR("templates/editor/inline/inlineeditor.lml"),
    TEMPLATE_INLINE_TOOL_CONFIG_ROW("templates/editor/inline/inlinetoolconfigrow.lml"),
    
    DRIVING_JSON("editor/tool/driving.json"),
    HELLO_WORLD_JSON("editor/tool/helloworld.json"),
    ACID_SVG("acid.svg"),
    TIGER_SVG("tiger.svg"),
    RACCOON_SVG("raccon_layers.svg"),
    RABBIT_SVG("rabbit.svg"),
    CIRCLES_SVG("circles.svg"),
    BUILDINGS_SVG("buildings.svg"),
    MONKEY_SVG("monkey.svg");
    
    private final String path;
    
    StringAssets(String path) {
        this.path = path;
    }
    
    /**
     * Returns the asset materialized as a string.
     */
    public String getString(AssetManager manager) {
        return manager.get(path, String.class);
    }

    @Override
    public void load(AssetManager manager) {
        manager.load(path, String.class);
    }
}
