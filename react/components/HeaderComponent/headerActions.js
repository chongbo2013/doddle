import * as Constants from '../../constants'
import rnfs from 'react-native-fs'

let saveDirectory = new Promise(function(resolve, reject){
    rnfs.exists(Constants.DODLE_SAVE_DIRECTORY).then((result) => {
        resolve(result)
    })
})


export function setOkCancelState(showCancel, showOkay, okayCount, iconType) {
    return {
        type: Constants.SET_OK_CANCEL_STATE,
        showCancel,
        showOkay,
        okayCount,
        iconType
    }
}
export function cancelButtonPressed (isPressed) {
    return {
        type: Constants.CANCEL_BUTTON_PRESSED,
        isPressed
    }
}
export function okButtonPressed (isPressed) {
    return {
        type: Constants.OK_BUTTON_PRESSED,
        isPressed
    }
}

export function saveDodleButtonPressed (isPressed) {
    return (dispatch) => {
        saveDirectory.then((result) => {
            dispatch({type: Constants.SAVE_DODLE_BUTTON_PRESSED, isPressed})
        }).catch(function(err){

        })
    }
}


export function showHideMenu () {
    return {
        type: Constants.MENU_BUTTON_PRESSED,
    }
}