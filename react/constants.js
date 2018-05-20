import { Dimensions, Platform } from 'react-native'
import { createIconSetFromIcoMoon } from 'react-native-vector-icons';
import icoMoonConfig from './assets/fonts/selection.json';
var RNFS = require('react-native-fs');

export const DodlesIcon = createIconSetFromIcoMoon(icoMoonConfig);

export const SET_ACTIVE_TOOL = 'SET_ACTIVE_TOOL'
export const SET_ACTIVE_BRUSH = 'SET_ACTIVE_BRUSH'
export const SET_BRUSH_OPACITY = 'SET_BRUSH_OPACITY'
export const SET_BRUSH_SIZE = 'SET_BRUSH_SIZE'
export const SET_BRUSH_COLOR = 'SET_BRUSH_COLOR'
export const SET_BRUSH_OLD_COLOR = 'SET_BRUSH_OLD_COLOR'
export const SET_ACTIVE_BRUSH_SUBMENU = 'SET_ACTIVE_BRUSH_SUBMENU'
export const RESET_BRUSH_SUBMENU = 'RESET_BRUSH_SUBMENU'
export const SET_ACTIVE_SHAPE = 'SET_ACTIVE_SHAPE'
export const SET_SHAPE_OPACITY = 'SET_SHAPE_OPACITY'
export const SET_SHAPE_SIZE = 'SET_SHAPE_SIZE'
export const SET_SHAPE_CORNER = 'SET_SHAPE_CORNER'
export const SET_SHAPE_ROUNDING = 'SET_SHAPE_ROUNDING'
export const SET_RULER_MODE = 'SET_RULER_MODE'
export const SET_SHAPE_COLOR = 'SET_SHAPE_COLOR'
export const SET_SHAPE_OLD_COLOR = 'SET_SHAPE_OLD_COLOR'
export const SET_SHAPE_STAR_DEPTH = 'SET_SHAPE_STAR_DEPTH'
export const SET_CUSTOMIZING_SHAPE_STATUS = 'SET_CUSTOMIZING_SHAPE_STATUS'
export const SET_ACTIVE_SHAPE_SUBMENU = 'SET_ACTIVE_SHAPE_SUBMENU'
export const RESET_SHAPE_SUBMENU = 'RESET_SHAPE_SUBMENU'
export const SET_OK_CANCEL_STATE = 'SET_OK_CANCEL_STATE'
export const CANCEL_BUTTON_PRESSED = 'CANCEL_BUTTON_PRESSED'
export const OK_BUTTON_PRESSED = 'OK_BUTTON_PRESSED'
export const MENU_BUTTON_PRESSED = 'MENU_BUTTON_PRESSED'
export const BRUSH_TOOL_SELECTOR_PRESSED = 'BRUSH_TOOL_SELECTOR_PRESSED'
export const SHAPE_TOOL_SELECTOR_PRESSED = 'SHAPE_TOOL_SELECTOR_PRESSED'
// Undo / Redo
export const SET_UNDO_REDO_STATE = 'SET_UNDO_REDO_STATE'
export const UNDO_BUTTON_PRESSED = 'UNDO_BUTTON_PRESSED'
export const REDO_BUTTON_PRESSED = 'REDO_BUTTON_PRESSED'
// Save, Load, Delete Dodle
export const DODLE_SAVE_DIRECTORY = (
    Platform.OS === 'ios' ? RNFS.LibraryDirectoryPath + "/local" : RNFS.DocumentDirectoryPath
) + "/saves"
export const DELETE_DODLE_BUTTON_PRESSED = 'DELETE_DODLE_BUTTON_PRESSED'
export const SAVE_DODLE_BUTTON_PRESSED = 'SAVE_DODLE_BUTTON_PRESSED'
export const LOAD_DODLE = 'LOAD_DODLE'
export const SHARED_DODLE_BUTTON_PRESSED = 'SHARED_DODLE_BUTTON_PRESSED'
export const SET_CURRENT_DODLE_ID = 'SET_CURRENT_DODLE_ID'
// Layer Tools
export const LAYER_OPACITY_BUTTON_PRESSED = 'LAYER_OPACITY_BUTTON_PRESSED'
export const LAYER_GROUP_BUTTON_PRESSED = 'LAYER_GROUP_BUTTON_PRESSED'
export const LAYER_UNGROUP_BUTTON_PRESSED = 'LAYER_UNGROUP_BUTTON_PRESSED'
export const LAYER_FLIP_H_BUTTON_PRESSED = 'LAYER_FLIP_H_BUTTON_PRESSED'
export const LAYER_FLIP_V_BUTTON_PRESSED = 'LAYER_FLIP_V_BUTTON_PRESSED'
export const LAYER_SIZE_BUTTON_PRESSED = 'LAYER_STROKE_SIZE_BUTTON_PRESSED'
export const LAYER_FORWARD_BUTTON_PRESSED = 'LAYER_FORWARD_BUTTON_PRESSED'
export const LAYER_FRONT_BUTTON_PRESSED = 'LAYER_FRONT_BUTTON_PRESSED'
export const LAYER_BACKWARD_BUTTON_PRESSED = 'LAYER_BACKWARD_BUTTON_PRESSED'
export const LAYER_BACK_BUTTON_PRESSED = 'LAYER_BACK_BUTTON_PRESSED'
export const LAYER_COPY_BUTTON_PRESSED = 'LAYER_COPY_BUTTON_PRESSED'
export const LAYER_DELETE_BUTTON_PRESSED = 'LAYER_DELETE_BUTTON_PRESSED'

// Animation Tools
export const SET_SELECTED_EFFECT = 'SET_SELECTED_EFFECT'
export const INITIALIZE_ANIMATION_DISPLAY = 'INITIALIZE_ANIMATION_DISPLAY'
export const ANIMATION_START = 'ANIMATION_START'
export const ANIMATION_STOP = 'ANIMATION_STOP'
export const SET_ANIMATION_CONFIG = 'SET_ANIMATION_CONFIG'

export const CONTRACT_MOVE_CONFIG = 'CONTRACT_MOVE_CONFIG'
export const EXPAND_MOVE_CONFIG = 'EXPAND_MOVE_CONFIG'
export const UPDATE_MOVE_CONFIG = 'UPDATE_MOVE_CONFIG'
export const SEND_MOVE_CONFIG_UPDATE = 'SEND_MOVE_CONFIG_UPDATE'

export const RESET_SEND_ROTATE_CONFIG = 'RESET_SEND_ROTATE_CONFIG'
export const EXPAND_ROTATE_CONFIG = 'EXPAND_ROTATE_CONFIG'
export const UPDATE_ROTATE_CONFIG = 'UPDATE_ROTATE_CONFIG'
export const SEND_ROTATE_CONFIG_UPDATE = 'SEND_ROTATE_CONFIG_UPDATE'

export const RESET_SEND_MOVE_CONFIG = 'RESET_SEND_MOVE_CONFIG'
export const SHOW_HELP = 'SHOW_HELP'
export const HIDE_HELP = 'HIDE_HELP'
export const SET_ANIMATION_TOTAL_LENGTH = 'SET_ANIMATION_TOTAL_LENGTH'
export const SET_TIMELINE_DATA = 'SET_TIMELINE_DATA'
export const SET_ANIMATION_TOOL_STATE = 'SET_ANIMATION_TOOL_STATE'
export const UPDATE_PLAY_TIME = 'UPDATE_PLAY_TIME'
export const UPDATE_PLAYHEAD_POSITION = 'UPDATE_PLAYHEAD_POSITION'
export const SET_SELECTED_TIMELINE_EFFECT = 'SET_SELECTED_TIMELINE_EFFECT'
export const ANIM_PIVOT_POINT_BUTTON_PRESSED = 'ANIM_PIVOT_POINT_BUTTON_PRESSED'
// Animation Subtool States
export const ANIM_TOOL_STATE_UNKNOWN = "UNKNOWN"
export const ANIM_TOOL_STATE_DISABLED = "DISABLED"
export const ANIM_TOOL_STATE_TIMELINE = "TIMELINE"
export const ANIM_TOOL_STATE_EFFECT_SELECT = "EFFECT_SELECT"
export const ANIM_TOOL_STATE_MOVE_EFFECT_DEMO = "MOVE_EFFECT_DEMO"
export const ANIM_TOOL_STATE_MOVE_EFFECT_INPUT = "MOVE_EFFECT_DRAW"
export const ANIM_TOOL_STATE_MOVE_EFFECT_CONFIGURE = "MOVE_EFFECT_CONFIGURE"
export const ANIM_TOOL_STATE_ROTATE_EFFECT_DEMO = "ROTATE_EFFECT_DEMO"
export const ANIM_TOOL_STATE_ROTATE_EFFECT_INPUT = "ROTATE_EFFECT_INPUT"
export const ANIM_TOOL_STATE_ROTATE_EFFECT_CONFIGURE = "ROTATE_EFFECT_CONFIGURE"
export const ANIM_TOOL_STATE_SCALE_EFFECT_DEMO = "SCALE_EFFECT_DEMO"
export const ANIM_TOOL_STATE_SCALE_EFFECT_INPUT = "SCALE_EFFECT_INPUT"
export const ANIM_TOOL_STATE_SCALE_EFFECT_CONFIGURE = "SCALE_EFFECT_CONFIGURE"
export const ANIM_TOOL_NO_HELP_STATES = [
    ANIM_TOOL_STATE_UNKNOWN,
    ANIM_TOOL_STATE_DISABLED,
    ANIM_TOOL_STATE_TIMELINE,
    ANIM_TOOL_STATE_EFFECT_SELECT,

]
// Tool names
export const TOOL_DRAW = 'TOOL.DRAW'
export const TOOL_SHAPE = 'TOOL.GEOMETRY'
export const TOOL_ANIMATION = 'TOOL.ANIMATION'
// Data Names
export const LOAD_GALLERY_DATA = 'LOAD_GALLERY_DATA'
export const LOAD_DRAWING_SCREEN = 'LOAD_DRAWING_SCREEN'
export const LOAD_GALLERY = 'LOAD_GALLERY'
export const SHOW_POPUP = 'SHOW_POPUP';
export const SELECTED_DODLE = 'SELECTED_DODLE'
export const DELETE_CONFORMATION = "DELETE_CONFORMATION"
export const SHARE_CONFORMATION = "SHARE_CONFORMATION"
export const GIF_BUTTON = "GIF_BUTTON"
export const VID_BUTTON = "VID_BUTTON"
//rotate effect


//export const SET_ANIMATION_CONFIG = 'SET_ANIMATION_CONFIG'
//export const CONTRACT_ROTATE_CONFIG = 'CONTRACT_ROTATE_CONFIG'

//export const SEND_MOVE_CONFIG_UPDATE = 'SEND_MOVE_CONFIG_UPDATE'



export const buttonImages = {
    drybrush: require('./assets/images/Brush.png'),
    chalk: require('./assets/images/Chalk.png'),
    eraser: require('./assets/images/Eraser.png'),
    marker: require('./assets/images/Marker.png'),
    fountainpen: require('./assets/images/Pen.png'),
    pencil: require('./assets/images/Pencil.png'),
    colorPicker: require('./assets/images/ColorPicker.png'),
    opacity: require('./assets/images/Opacity.png'),
    ruler: require('./assets/images/Ruler.png'),
    hueSaturation: require('./assets/images/HueSaturation.png'),
    dotSwatch: require('./assets/images/dotswatch.png'),
    checker: require('./assets/images/Checkerboard_Long.png'),
    smallCheck: require('./assets/images/Checkerboard_TinySq.png'),
    checker25Alpha: require('./assets/images/Checkerboard_25alpha.png'),
}

// set platform specific styling constants
export const deviceHeight = Platform.OS === 'ios' ? Dimensions.get('window').height + 2 : Dimensions.get('window').height + 50;
export const deviceWidth = Dimensions.get('window').width;
export const MENU_WIDTH = deviceWidth * .75;
export const headerHeight = deviceHeight * 0.09 > 60 ? 60 : deviceHeight * 0.09;
export const toolAreaHeight = deviceWidth / deviceHeight > .65 ? deviceHeight * .35 : deviceHeight - deviceWidth - headerHeight;
export const dodleEngineHeight = deviceHeight - headerHeight - toolAreaHeight;
export const toolRowHeight = toolAreaHeight / 1.8;
export const effectWidth = deviceWidth>=768? deviceWidth*0.15:deviceWidth*0.18;
export const toolRowColapsed = (toolRowHeight - 25) / 2;
export const toolRowOffset = (toolRowColapsed * -2) + 2;
export const toolMenuButtonBlockWidth = deviceWidth / 5;
export const toolMenuButtonBlockWidth2 = deviceWidth / 1.6;
export const toolDisplayArea = toolAreaHeight - toolRowColapsed
export const subtoolButtonSize2Row = ((toolAreaHeight - toolRowColapsed) / 4) * .9;
export const subtoolButtonSize3Row = ((toolAreaHeight - toolRowColapsed) / 5) * .9;

