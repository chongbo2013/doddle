import React, { Component } from 'react';
import {
    View,
    Text,
    StyleSheet,
    Image,
    TouchableOpacity,
    Platform
} from 'react-native';
import { connect } from 'react-redux';
import { ActionCreators } from '../../actions/index'
import SwitchButton from './SwitchButton';
import RNFS from 'react-native-fs';
import Share, { ShareSheet, Button } from 'react-native-share';
import * as Constants from '../../constants';

class ShareConformation extends Component {

    render() {

        const renderShare = () => {
            //console.log(this.props.clickedImage.replace(".png", "") + '/' + this.props.clickedImage);
            RNFS.readFile(
                Constants.DODLE_SAVE_DIRECTORY + '/' + this.props.clickedImage.replace(".png", "") + '/' + this.props.clickedImage, 'base64')
                .then(data => {
                //console.log(`data:image/png;base64,${data}`);
                Share.open(data1(`data:image/png;base64,${data}`));

            }
            );
        }
        const data1 = (url) => {
            return {
                title: "React Native",
                message: "sharing a dodle",
                url: url,
                subject: "Share Link" //  for email
            }
        }

        return (
            <View style={styles.container}>
                <View style={styles.textView}>
                    <Text style={styles.text}> Share Your Dodle</Text>
                    <Text style={styles.smallText}>  Share With Friends </Text>

                </View>
                <View style={styles.switchButton}>
                    <SwitchButton />
                </View>
                <View style={styles.buttonView}>
                    <TouchableOpacity onPress={() => { this.props.shareConformation(false) }}
                    >

                        <View style={styles.cancelButton}>
                            <Text style={styles.buttonText}>Cancel</Text>
                        </View>
                    </TouchableOpacity>

                    <TouchableOpacity onPress={() => {
                        this.props.shareConformation(false);
                        this.props.setShowPopMenu({ flag: false, data: '' });
                        renderShare();
                    }}>
                        <View style={styles.deleteButton}>
                            <Text style={styles.buttonText}>Share</Text>
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
    switchButton: {
        alignItems: 'center',
        justifyContent: 'center',

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
        backgroundColor: '#14B0D8',
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

        clickedImage: state.data.clickedImage
    }
}

function mapDispatchToProps(dispatch) {
    return {
        setShowPopMenu: (showPop) => dispatch(ActionCreators.showPopUpMenu(showPop)),
        shareConformation: (isPressed) => dispatch(ActionCreators.shareConformation(isPressed)),
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShareConformation)
