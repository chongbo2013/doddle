import * as Constants from '../constants';

const initialState = {
    isDeleteDodleButtonPressed: false,
}

export default function sideMenuReducer (state = initialState, action) {
    switch (action.type) {
        case Constants.DELETE_DODLE_BUTTON_PRESSED:
            return {
                ...state,
                isDeleteDodleButtonPressed: action.isPressed
            }
        default:
            return {
                ...state
        }
            break;
    }
}
