import * as Constants from '../constants'

const initialState = {
    isOpacityButtonPressed: false,
    isGroupButtonPressed: false,
    isUngroupButtonPressed: false,
    isFlipHorizontalButtonPressed: false,
    isFlipVerticalButtonPressed: false,
    isSizeButtonPressed: false,
    isForwardButtonPressed: false,
    isFrontButtonPressed: false,
    isBackwardButtonPressed: false,
    isBackButtonPressed: false,
    isCopyButtonPressed: false,
    isDeleteButtonPressed: false,
}

export default function layerReducer (state = initialState, action) {
    switch (action.type) {
        case Constants.LAYER_OPACITY_BUTTON_PRESSED:
            return {
                ...state,
                isOpacityButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_GROUP_BUTTON_PRESSED:
            return {
                ...state,
                isGroupButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_UNGROUP_BUTTON_PRESSED:
            return {
                ...state,
                isUngroupButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_FLIP_H_BUTTON_PRESSED:
            return {
                ...state,
                isFlipHorizontalButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_FLIP_V_BUTTON_PRESSED:
            return {
                ...state,
                isFlipVerticalButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_SIZE_BUTTON_PRESSED:
            return {
                ...state,
                isSizeButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_FORWARD_BUTTON_PRESSED:
            return {
                ...state,
                isForwardButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_FRONT_BUTTON_PRESSED:
            return {
                ...state,
                isFrontButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_BACKWARD_BUTTON_PRESSED:
            return {
                ...state,
                isBackwardButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_BACK_BUTTON_PRESSED:
            return {
                ...state,
                isBackButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_COPY_BUTTON_PRESSED:
            return {
                ...state,
                isCopyButtonPressed: action.isPressed
            }
            break;
        case Constants.LAYER_DELETE_BUTTON_PRESSED:
            return {
                ...state,
                isDeleteButtonPressed: action.isPressed
            }
            break;
        default:
            return {
                ...state
            }
            break;
    }
}

