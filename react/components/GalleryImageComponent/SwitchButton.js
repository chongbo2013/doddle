import React, { Component } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import * as Constants from '../../constants';
import { connect } from 'react-redux';
import { ActionCreators } from '../../actions/index'

class SwitchButton extends Component {

    render() {
        return (
            <View style={styles.outerButtonStyle}>
                <TouchableOpacity style={this.props.gifButton ? styles.buttonStyle : styles.dummyStyle}
                    onPress={() => this.props.gifButtonPressed()}
                >
                    <Text style={styles.textStyle}>gif</Text>
                </TouchableOpacity>
                <TouchableOpacity style={this.props.vidButton ? styles.buttonStyle : styles.dummyStyle}
                    onPress={() => this.props.vidButtonPressed()}
                >
                    <Text style={styles.textStyle}>video</Text>
                </TouchableOpacity>
            </View>
        );
    }
}

const styles = {
    outerButtonStyle: {
        height: Constants.deviceHeight * 0.05,
        width: Constants.deviceWidth * 0.5,
        backgroundColor: '#d3d3d3',
        borderRadius: 100,
        flexDirection: 'row'
    },
    buttonStyle: {
        flex: 0.5,
        backgroundColor: '#119ec2',
        margin: 3,
        borderRadius: 100,
        alignItems: 'center',
        justifyContent: 'center'
    },
    dummyStyle: {
        flex: 0.5,
        backgroundColor: '#d3d3d3',
        margin: 3,
        borderRadius: 100,
        alignItems: 'center',
        justifyContent: 'center'
    },
    textStyle: {
        fontSize: Constants.deviceWidth > 768 ? 18 : 14,
        color: '#000'
    }

};

function mapStateToProps(state) {
    return {
        gifButton: state.data.gifButton,
        vidButton: state.data.vidButton
    }
}

function mapDispatchToProps(dispatch) {
    return {
        gifButtonPressed: () => dispatch(ActionCreators.gifButtonPressed()),
        vidButtonPressed: () => dispatch(ActionCreators.vidButtonPressed()),

    }
}
export default connect(
    mapStateToProps,
    mapDispatchToProps
)(SwitchButton)