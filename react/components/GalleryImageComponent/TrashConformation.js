import React, { Component } from 'react';
import {
    View,
    Text,
    StyleSheet,
    TouchableOpacity
} from 'react-native';
import * as Constants from '../../constants';
import { connect } from 'react-redux';
import { ActionCreators } from '../../actions/index'
import RNFS from 'react-native-fs';

class TrashConformation extends Component {

    deleteFiles = (dodleID) => {
        let dodleDirectory = Constants.DODLE_SAVE_DIRECTORY +'/' + dodleID;
        if (RNFS.exists(dodleDirectory)) {
            RNFS.unlink(dodleDirectory).then(() => {
                console.log("Deleted files for " + dodleID);
            }).catch((err) => {
                console.log(err.message);
            });
        }
    }

    onDeleteConfirmed = () => {
        if(this.props.showPopMenuFlag){
            // coming from gallery
            let dodleToDelete = this.props.clickedImage.replace(".png", "");
            this.deleteFiles(dodleToDelete);
            this.props.setGalleryData();
        } else {
            // coming from toolbar
            this.deleteFiles(this.props.currentDodleID);
            this.props.deleteDodleButtonPressed(true);
        }
        this.props.deleteConformation(false);
        this.props.setShowPopMenu({flag: false, data: ''});
    }

    render() {
        return (
            <View style={styles.container}>
                <View style={styles.textView}>
                    <Text style={styles.text}> Are you sure you want</Text>
                    <Text style={styles.text}>  to trash this Dodle? </Text>
                    <Text style={styles.smallText}>  You will not be able to retrieve it. </Text>
                </View>
                <View style={styles.buttonView}>
                    <TouchableOpacity onPress={() => { this.props.deleteConformation(false) }}>
                        <View style={styles.cancelButton}>
                            <Text style={styles.buttonText}>Cancel</Text>
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this.onDeleteConfirmed}>
                        <View style={styles.deleteButton}>
                            <Text style={styles.buttonText}>Delete</Text>
                        </View>
                    </TouchableOpacity>
                </View>
            </View>
        )
    }
}

let styles = StyleSheet.create({
    container: {

        justifyContent: 'center',
        height: Constants.deviceHeight * 0.3,
        backgroundColor: '#F3F3F3',
        width: Constants.deviceWidth * 0.9,
    },
    textView: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center'
    },
    text: {
        fontSize: 22,
        color: '#14B0D8',
        fontWeight: 'bold',
    },
    smallText: {
        fontSize: 10,
        color: '#7C7C7C'

    },
    buttonView: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'space-around',
        height: Constants.deviceHeight,
        alignItems: 'flex-end'
    },
    cancelButton: {
        alignItems: 'center',
        backgroundColor: '#14B0D8',
        width: Constants.deviceWidth * 0.45,
        height: Constants.deviceHeight * 0.065

    },
    deleteButton: {
        alignItems: 'center',
        backgroundColor: '#ff0000',
        width: Constants.deviceWidth * 0.45,
        height: Constants.deviceHeight * 0.065

    },
    buttonText: {
        fontSize: 20,
        color: '#F3F3F3',
        marginTop: '5%',
        fontWeight: 'bold',
    }
})
function mapStateToProps(state) {
    return {
        showPopMenuFlag: state.data.showPopMenuFlag,
        clickedImage: state.data.clickedImage,
        currentDodleID: state.data.currentDodleID
    }
}
function mapDispatchToProps(dispatch) {
    return {
        deleteConformation: (isPressed) => dispatch(ActionCreators.deleteConformation(isPressed)),
        deleteDodleButtonPressed: (isPressed) => dispatch(ActionCreators.deleteDodleButtonPressed(isPressed)),
        setGalleryData: () => dispatch(ActionCreators.setGalleryData()),
        setShowPopMenu: (showPop) => dispatch(ActionCreators.showPopUpMenu(showPop)),
 }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(TrashConformation)

