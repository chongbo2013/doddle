import * as Constants from '../constants'

const initialState = {
    activeShape: null,
    toolPickerActive: false,
    activeSubmenu: 'ShapeCornerPicker',
    shapeOpacity: 1,
    shapeSize: 150,
    shapeColor: '#000000',
    shapeOldColor: '#000000',
    isCustomizingShape: false,
    shapeRounding: 0,
    shapeCorner: 5,
    //to support the number of polygon corners (if the number of corners is less than 5 when we try to play with the polygon, the app will crash)
    starDepth: 50,
}

export default function shapeReducer (state = initialState, action) {
    switch (action.type) {
        case Constants.SET_ACTIVE_SHAPE:
            return {
                ...state,
                activeShape: action.shapename
            }
        case Constants.SET_SHAPE_STAR_DEPTH:
            return {
                ...state,
                starDepth: action.depth
            }
        case Constants.SET_ACTIVE_SHAPE_SUBMENU:
            return {
                ...state,
                toolPickerActive: false,
                activeSubmenu: action.submenu
            }
        case Constants.SET_SHAPE_OPACITY:
            return {
                ...state,
                shapeOpacity: action.shapeopacity
            }
        case Constants.SET_SHAPE_SIZE:
            return {
                ...state,
                shapeSize: action.shapesize
            }
        case Constants.SET_SHAPE_COLOR:
            return {
                ...state,
                toolPickerActive: false,
                shapeColor: action.shapecolor
            }
        case Constants.SET_SHAPE_CORNER:
            return {
                ...state,
                shapeCorner: action.shapecorner
            }
        case Constants.SET_SHAPE_ROUNDING:
            return {
                ...state,
                shapeRounding: action.shaperounding
            }
        case Constants.RESET_SHAPE_SUBMENU:
            return {
                ...state,
                toolPickerActive: false,
                activeSubmenu: 'ShapeCornerPicker'
            }
        case Constants.SET_SHAPE_OLD_COLOR:
            return {
                ...state,
                toolPickerActive: false,
                shapeOldColor: action.shapeOldColor
            }
        case Constants.SET_CUSTOMIZING_SHAPE_STATUS:
            return {
                ...state,
                isCustomizingShape: action.value
            }
        case Constants.SHAPE_TOOL_SELECTOR_PRESSED:
            return {
                ...state,
                toolPickerActive: !state.toolPickerActive
            }
        default:
            return state
    }
}