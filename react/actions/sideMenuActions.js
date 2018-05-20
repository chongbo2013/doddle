import * as Constants from '../constants'

export function deleteButtonPressed (isPressed) {
    return {
        type: Constants.DELETE_DODLE_BUTTON_PRESSED,
        isPressed
    }
}
