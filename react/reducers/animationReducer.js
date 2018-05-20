import * as Constants from '../constants'

const initialState = {
    toolState: null,
    selectedEffect: null,
    animationTotalLength: 10,
    animationRunning: false,
    selectedTimelineEffect: null,
    activeConfig: null,
    showHelp: null,
    showDemo: {
        moveEffect: true,
        rotateEffect: true,
        scaleEffect: true
    },
    pivotPointButtonPressed: false,
    updateMoveConfig: false,
    updateRotateConfig: false,
    moveConfig: {
        expanded: false,
        iterations: {
            disabled: false,
            value: 1
        },
        parallelToPath: {
            disabled: false,
            value: false
        },
        finishUpright: {
            disabled: false,
            value: false
        },
        straightPath: {
            disabled: false,
            value: false
        },
        returnToStart : {
            disabled: false,
            value: false
        },
        reverseMotion: {
            disabled: false,
            value:false
        }
    },
    rotateConfig: {
        expanded: false,
        iterations: {
            disabled: false,
            value: 1
        },
        finishUpright: {
            disabled: false,
            value: false
        },
        autoRotation: {
            disabled: false,
            value: false
        },
        rotations:{
            disabled: false,
            value: 1
        },
        reverseMotion: {
            disabled: false,
            value: false
        }
    },
    animationPlayTime: 0,
    playheadPosition: 0,
    animations:[],
    timeLines:{},
    nextPrevious:[]

}

export default function animationReducer (state = initialState, action) {
    switch (action.type) {
        case Constants.SET_ANIMATION_TOOL_STATE:
            let newState = {
                ...state,
                toolState: action.toolState,
            };
            // Determine if canvas' help area is needed
            if (Constants.ANIM_TOOL_NO_HELP_STATES.indexOf(action.toolState) > -1) {
                newState.showHelp = null;
            }
            // State Specific State Overrides
            switch (action.toolState) {
                case Constants.ANIM_TOOL_STATE_MOVE_EFFECT_DEMO:
                    newState.showDemo.moveEffect = false;
                    break;
                case Constants.ANIM_TOOL_STATE_MOVE_EFFECT_CONFIGURE:
                    newState.activeConfig = 'Move';
                    break;
                case Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_DEMO:
                    newState.showDemo.rotateEffect = false;
                    break;
                case Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_CONFIGURE:
                    newState.activeConfig = "Spin";
                    break;
                case Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_PIVOT:
                    newState.activeConfig = null;
                    newState.showHelp = "Spin";
                    break;
                case Constants.ANIM_TOOL_STATE_SCALE_EFFECT_DEMO:
                    newState.showDemo.scaleEffect = false;
                    break;
                case Constants.ANIM_TOOL_STATE_SCALE_EFFECT_CONFIGURE:
                    newState.activeConfig = "Scale";
                    break;
                case Constants.ANIM_TOOL_STATE_TIMELINE:
                    newState.activeConfig = null;
                    break;
            }
            return newState;
        case Constants.INITIALIZE_ANIMATION_DISPLAY:
            return {
                ...state,
                animationTotalLength: action.animationLength,
                timeLines: action.timeLines,
                nextPrevious: action.nextPrevious

            }
        case Constants.SET_SELECTED_EFFECT:
            return {
                ...state,
                selectedEffect: action.selectedEffect
            }
        case Constants.ANIMATION_START:
            return {
                ...state,
                animationRunning: true
            }
        case Constants.ANIMATION_STOP:
            return {
                ...state,
                animationRunning: false,
            }
        case Constants.SET_ANIMATION_CONFIG:
            return{
                ...state,
                activeConfig: action.activeConfig
            }
        case Constants.UPDATE_MOVE_CONFIG:
            return{
                ...state,
                moveConfig: action.configObj,
                updateMoveConfig: true,
            }
        case Constants.SEND_MOVE_CONFIG_UPDATE:
            return{
                ...state,
                updateMoveConfig: true,
            }
        case Constants.RESET_SEND_MOVE_CONFIG:
            return{
                ...state,
                updateMoveConfig: false,
            }
        case Constants.SHOW_HELP:
            return{
                ...state,
                showHelp: action.helpContext,
            }
        case Constants.HIDE_HELP:
            return{
                ...state,
                showHelp: null,
            }
        case Constants.SET_ANIMATION_TOTAL_LENGTH:
            return{
                ...state,
                animationTotalLength: action.animationLength,
            }
        case Constants.SET_TIMELINE_DATA:
            return{
                ...state,
                animations: action.timelineData,
            }
        case Constants.UPDATE_PLAY_TIME:
            return {
                ...state,
                animationPlayTime: action.playTime,
            }
        case Constants.UPDATE_PLAYHEAD_POSITION:
            return state.animationRunning? {...state} : {
                ...state,
                playheadPosition: action.playheadPosition,
                animationPlayTime: action.playheadPosition,
            }
        case Constants.SET_SELECTED_TIMELINE_EFFECT:
            return {
                ...state,
                selectedTimelineEffect: action.effectID
            }
        case Constants.ANIM_PIVOT_POINT_BUTTON_PRESSED:
            return {
                ...state,
                pivotPointButtonPressed: action.isPressed
            }
        case Constants.UPDATE_ROTATE_CONFIG:
            return{
                ...state,
                RotateConfig: action.configObj,
                updateRotateConfig: true,
            }
        case Constants.SEND_ROTATE_CONFIG_UPDATE:
            return{
                ...state,
                updateRotateConfig: true,
            }
        case Constants.RESET_SEND_ROTATE_CONFIG:
            return{
                ...state,
                updateRotateConfig: false,
            }
        default:
            return state
    }
}