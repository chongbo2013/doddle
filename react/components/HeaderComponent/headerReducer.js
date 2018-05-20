import * as Constants from '../../constants'
const initialState = {
    showCancel: false,
    showOkay: false,
    okayCount: 0,
    iconType: null,
    isCancelButtonPressed: false,
    isOkayButtonPressed: false,
    showMenu: false,
    isSaveDodleButtonPressed: false
}

export default function okCancelReducer (state = initialState, action) {
    switch (action.type) {
        case Constants.SET_OK_CANCEL_STATE:
            return {
                ...state,
                showCancel: action.showCancel,
                showOkay: action.showOkay,
                okayCount: action.okayCount,
                iconType: action.iconType
            }
            break;
        case Constants.CANCEL_BUTTON_PRESSED:
            return {
                ...state,
                isCancelButtonPressed: action.isPressed
            }
            break;
        case Constants.OK_BUTTON_PRESSED:
            return {
                ...state,
                isOkayButtonPressed: action.isPressed
            }
            break;
            case Constants.SAVE_DODLE_BUTTON_PRESSED:
             return {
                 ...state,
                 isSaveDodleButtonPressed: action.isPressed
             }
        case Constants.MENU_BUTTON_PRESSED:
            return {
                ...state,
                showMenu: !state.showMenu
            }
            break;
        default:
            return {
                ...state
            }
            break;
    }
}