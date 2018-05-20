import * as Constants from '../constants';

const initialState = {
    currentDodleID: null,
    galleryView: false,
    galleryData: [],
    isDeleteDodleButtonPressed: false,
    dodleToLoad: null,
    showPopMenuFlag: false,
    popMenuItemPressed: false,
    gifButton: true,
    vidButton: false,
    clickedImage: '',
}

export default function dataReducer(state = initialState, action) {
    switch (action.type) {
        case Constants.SET_CURRENT_DODLE_ID:
            console.log("[DEV-548] setting the current dodle ID to " + action.newDodleID);
            return {
                ...state,
                currentDodleID: action.newDodleID
            }
        case Constants.LOAD_GALLERY:
            return {
                ...state,
                galleryView: action.data,
            }
        case Constants.LOAD_GALLERY_DATA:
            return {
                ...state,
                galleryData: action.gallery
            }
        case Constants.LOAD_DRAWING_SCREEN:
            return {
                ...state,
                galleryView: action.data
            }
        case Constants.DELETE_DODLE_BUTTON_PRESSED:
            return {
                ...state,
                isDeleteDodleButtonPressed: action.isPressed
            }
        case Constants.LOAD_DODLE:
            return {
                ...state,
                dodleToLoad: action.dodleToLoad,
                galleryView: false
            }
        case Constants.SHOW_POPUP:
            return {
                ...state,
                showPopMenuFlag: action.payload.flag,
                clickedImage: action.payload.data=='' ? state.clickedImage:action.payload.data

            }
        case Constants.SELECTED_DODLE:
            return {
                ...state,
                popMenuItemPressed: action.payload
            }
        case Constants.GIF_BUTTON:
            return {
                ...state,
                gifButton: true,
                vidButton: false


            }
        case Constants.VID_BUTTON:
            return {
                ...state,
                gifButton: false,
                vidButton: true
            }

        default:
            return state;
    }
}

