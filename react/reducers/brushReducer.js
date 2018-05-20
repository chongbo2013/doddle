import * as Constants from '../constants'
const initialState = {
    activeBrush: null,
    toolPickerActive: false,
    activeSubmenu: 'BrushToolPicker',
    brushOpacity: 1,
    brushSize: 10,
    brushColor: '#848484',
    brushOldColor: '#848484',
    rulerMode: false
}

export default function brushReducer (state = initialState, action) {
    switch (action.type) {
        case Constants.SET_ACTIVE_BRUSH:
            return {
                ...state,
                toolPickerActive: false,
                activeBrush: action.brushname
            }
        case Constants.SET_ACTIVE_BRUSH_SUBMENU:
            return {
                ...state,
                toolPickerActive: false,
                activeSubmenu: action.submenu
            }
        case Constants.SET_BRUSH_OPACITY:
            return {
                ...state,
                brushOpacity: action.brushopacity
            }
        case Constants.SET_BRUSH_SIZE:
            return {
                ...state,
                brushSize: action.brushsize
            }
        case Constants.SET_BRUSH_COLOR:
            return {
                ...state,
                toolPickerActive: false,
                brushColor: action.brushcolor
            }
        case Constants.SET_BRUSH_OLD_COLOR:
            return {
                ...state,
                toolPickerActive: false,
                brushOldColor: action.brushOldColor
            }
        case Constants.RESET_BRUSH_SUBMENU:
            return {
                ...state,
                toolPickerActive: false,
                activeSubmenu: 'Main'
            }
        case Constants.BRUSH_TOOL_SELECTOR_PRESSED:
            return {
                ...state,
                toolPickerActive: !state.toolPickerActive
            }
        case Constants.SET_RULER_MODE: {
            return {
                ...state,
                rulerMode: !state.rulerMode
            }
        }
        default:
            return state
    }
}