import * as Constants from '../constants'

export function setActiveBrush(brushname) {
    return  {
        type: Constants.SET_ACTIVE_BRUSH,
        brushname,
    }
}

export function setBrushOpacity(brushopacity) {
    return  {
        type: Constants.SET_BRUSH_OPACITY,
        brushopacity,
    }
}

export function setBrushSize(brushsize) {
    return  {
        type: Constants.SET_BRUSH_SIZE,
        brushsize,
    }
}

export function setActiveBrushSubmenu(submenu) {
    return  {
        type: Constants.SET_ACTIVE_BRUSH_SUBMENU,
        submenu,
    }
}

export function resetBrushSubmenu() {
    return  {
        type: Constants.RESET_BRUSH_SUBMENU
    }
}

export function setBrushColor(brushcolor) {
    return {
        type: Constants.SET_BRUSH_COLOR,
        brushcolor,
    }
}
export function setBrushOldColor(brushOldColor) {
    return {
        type: Constants.SET_BRUSH_OLD_COLOR,
        brushOldColor,
    }
}

export function switchRulerMode() {
    return {
        type: Constants.SET_RULER_MODE
    }
}

export function brushToolSelectorPressed() {
    return {
        type: Constants.BRUSH_TOOL_SELECTOR_PRESSED
    }
}