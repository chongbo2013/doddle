import * as Constants from '../constants';
import rnfs from 'react-native-fs';
import { Platform } from 'react-native';
import { isEmpty } from 'lodash';

export function setGalleryView(data) {
    return {
        type: Constants.LOAD_GALLERY,
        data
    }
}
export function setGalleryData() {
    let galleryDataCreate = new Promise(function (resolve, reject) {
        let completeData = [];
        rnfs.readDir(Constants.DODLE_SAVE_DIRECTORY).then((result) => {
            let arrayLength = result.length;
            for (let i = 0; i < arrayLength; i++) {
                if (!result[i].isFile()) {
                    rnfs.readDir(result[i].path).then((result1) => {
                        completeData.push((Platform.OS === 'ios') ? result1[1] : result1[0]);
                        completeData.sort(function (a, b) { return (b.mtime) - (a.mtime) });
                    })
                }
            }
            let wait = setTimeout(() => {
                clearTimeout(wait);
                resolve(completeData);
            }, 8)
        })
    });

    return (dispatch) => {
        galleryDataCreate.then((result) => {
            dispatch({ type: Constants.LOAD_GALLERY_DATA, gallery: result })
        });

    }
}

export function setCreateNewView(data) {
    return {
        type: Constants.LOAD_DRAWING_SCREEN,
        data
    }
}

export function deleteButtonPressed(isPressed) {
    return {
        type: Constants.DELETE_DODLE_BUTTON_PRESSED,
        isPressed
    }
}

export function loadDodle(dodleToLoad) {
    return {
        type: Constants.LOAD_DODLE,
        dodleToLoad
    }
}


export function showPopUpMenu(showPop) {
    return {
        type: Constants.SHOW_POPUP,
        payload: showPop
    }
};


export function showPopUpMenuItem(showPop) {
    return {
        type: Constants.SELECTED_DODLE,
        payload: showPop
    }
};


export function gifButtonPressed() {
    return {
        type: Constants.GIF_BUTTON
    }
};

export function vidButtonPressed() {
    return {
        type: Constants.VID_BUTTON
    }
};

export function setCurrentDodleID(newDodleID) {
    return {
        type: Constants.SET_CURRENT_DODLE_ID,
        newDodleID
    }
}








