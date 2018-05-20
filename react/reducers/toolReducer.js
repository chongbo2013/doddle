import * as Constants from '../constants'
const initialState = {
    activetool: null,
    undoActive: false,
    redoActive: false,
    isUndoButtonPressed: false,
    isRedoButtonPressed: false,
    isDeleteDodleButtonPressed: false,
    isSharedDodleButtonPressed: false,
    deleteConformationFlag: false,
    shareConformationFlag: false
}

export default function toolReducer(state = initialState, action) {
    switch (action.type) {
        case Constants.SET_ACTIVE_TOOL:
            return {
                ...state,
                activetool: action.toolname
            }
        case Constants.SET_UNDO_REDO_STATE:
            return {
                ...state,
                undoActive: action.undoActive,
                redoActive: action.redoActive
            }
        case Constants.UNDO_BUTTON_PRESSED:
            return {
                ...state,
                isUndoButtonPressed: action.isPressed
            }
            break;
        case Constants.REDO_BUTTON_PRESSED:
            return {
                ...state,
                isRedoButtonPressed: action.isPressed
            }
            break;
        case Constants.DELETE_DODLE_BUTTON_PRESSED:
            return {
                ...state,
                isDeleteDodleButtonPressed: action.isPressed
            }
            break;
        case Constants.DELETE_CONFORMATION:
            return {
                ...state,
                deleteConformationFlag: action.isPressed
            }
        case Constants.SHARE_CONFORMATION:
            return {
                ...state,
                shareConformationFlag: action.isPressed
            }
        default:
            return state
    }
}
