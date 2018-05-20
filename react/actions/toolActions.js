import * as Constants from '../constants'

export function setActiveTool(toolname) {
    return {
        type: Constants.SET_ACTIVE_TOOL,
        toolname,
    }
}

export function setUndoRedoState(undoActive, redoActive) {
    return {
        type: Constants.SET_UNDO_REDO_STATE,
        undoActive,
        redoActive
    }
}

export function undoButtonPressed(isPressed) {
    return {
        type: Constants.UNDO_BUTTON_PRESSED,
        isPressed
    }
}

export function redoButtonPressed(isPressed) {
    return {
        type: Constants.REDO_BUTTON_PRESSED,
        isPressed
    }
}

export function deleteDodleButtonPressed(isPressed) {
    return {
        type: Constants.DELETE_DODLE_BUTTON_PRESSED,
        isPressed
    }
}


export function deleteConformation(isPressed) {
    return {
        type: Constants.DELETE_CONFORMATION,
        isPressed
    }
}


export function shareConformation(isPressed) {
    return {
        type: Constants.SHARE_CONFORMATION,
        isPressed
    }
}