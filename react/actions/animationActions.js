import * as Constants from '../constants'


export function setAnimationToolState(toolState) {
    return {
        type: Constants.SET_ANIMATION_TOOL_STATE,
        toolState
    }
}
export function initializeAnimationDisplay(animationLength,timeLines,nextPrevious) {
    return {
        type: Constants.INITIALIZE_ANIMATION_DISPLAY,
        animationLength,timeLines,nextPrevious
    }
}
export function setSelectedEffect(selectedEffect) {
    return {
        type: Constants.SET_SELECTED_EFFECT,
        selectedEffect
    }
}
export function animationStart(){
    return{
        type: Constants.ANIMATION_START
    }
}
export function animationStop(){
    return{
        type: Constants.ANIMATION_STOP
    }
}
export function SetAnimationConfig(activeConfig) {
    return {
        type: Constants.SET_ANIMATION_CONFIG,
        activeConfig
    }
}
export function showHelp(helpContext){
    return {
        type: Constants.SHOW_HELP,
        helpContext
    }
}
export function hideHelp(){
    return {
        type: Constants.HIDE_HELP
    }
}
export function setAnimationTotalLength(animationLength){
    return {
        type: Constants.SET_ANIMATION_TOTAL_LENGTH,
        animationLength
    }
}
export function updateTimelineData(timelineData) {
    return {
        type: Constants.SET_TIMELINE_DATA,
        timelineData
    }
}
export function setSelectedTimelineEffect(effectID) {
    return {
        type: Constants.SET_SELECTED_TIMELINE_EFFECT,
        effectID
    }
}
export function updatePlayTime(playTime){
    return{
        type: Constants.UPDATE_PLAY_TIME,
        playTime
    }
}
export function updatePlayheadPosition(playheadPosition) {
    return {
        type: Constants.UPDATE_PLAYHEAD_POSITION,
        playheadPosition
    }
}
export function pivotPointButtonPressed(isPressed) {
    return {
        type: Constants.ANIM_PIVOT_POINT_BUTTON_PRESSED,
        isPressed
    }
}

// move effect actions
export function UpdateMoveConfig(configObj){
    return {
        type: Constants.UPDATE_MOVE_CONFIG,
        configObj
    }
}
export function sendMoveConfigUpdate (){
    return {
        type: Constants.SEND_MOVE_CONFIG_UPDATE
    }
}
export function resetSendMoveConfig(){
    return {
        type: Constants.RESET_SEND_MOVE_CONFIG
    }
}

// rotate effect actions
export function UpdateRotateConfig(configObj){
    return {
        type: Constants.UPDATE_ROTATE_CONFIG,
        configObj
    }
}
export function sendRotateConfigUpdate (){
    return {
        type: Constants.SEND_ROTATE_CONFIG_UPDATE
    }
}
export function resetSendRotateConfig(){
    return {
        type: Constants.RESET_SEND_ROTATE_CONFIG
    }
}



