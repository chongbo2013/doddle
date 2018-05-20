import React, {Component} from 'react';
import {StyleSheet, View} from 'react-native';
import { connect } from 'react-redux';
import ToolbarRowComponent from "../ToolbarRowComponent";
import {ActionCreators} from '../../../actions/index';
import * as Constants from '../../../constants';

const pickerHeight = Constants.toolAreaHeight - (Constants.toolRowColapsed + 12);

const layerToolsRow1 = [
    {iconName: 'Opacity', baseColor: '#56565644', buttonSize: 30,  action: 'layerOpacityButtonPressed', passData: true},
    {iconName: 'Group', baseColor: '#565656', buttonSize: 30,  action: 'layerGroupButtonPressed', passData: true},
    {iconName: 'Ungroup', baseColor: '#565656', buttonSize: 30,  action: 'layerUngroupButtonPressed', passData: true},
    {iconName: 'flip-H', baseColor: '#565656', buttonSize: 30,  action: 'layerFlipHorizontalButtonPressed', passData: true},
    {iconName: 'flip-V', baseColor: '#565656', buttonSize: 30,  action: 'layerFlipVerticalButtonPressed', passData: true},
    {iconName: 'Size', baseColor: '#56565644', buttonSize: 30,  action: 'layerSizeButtonPressed', passData: true},
    ];

const layerToolsRow2 = [
    {iconName: 'forward', baseColor: '#565656', buttonSize: 30,  action: 'layerForwardButtonPressed', passData: true},
    {iconName: 'front', baseColor: '#56565644', buttonSize: 30,  action: 'layerFrontButtonPressed', passData: true},
    {iconName: 'backward', baseColor: '#565656', buttonSize: 30,  action: 'layerBackwardButtonPressed', passData: true},
    {iconName: 'back', baseColor: '#56565644', buttonSize: 30,  action: 'layerBackButtonPressed', passData: true},
    {iconName: 'copy', baseColor: '#565656', buttonSize: 30, action: 'layerCopyButtonPressed', passData: true},
    {iconName: 'Trash', baseColor: '#565656', buttonSize: 30,  action: 'layerDeleteButtonPressed', passData: true}
    ];

class LayerTools extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <View  style={layerToolStyles.toolArea}>
                <ToolbarRowComponent style={layerToolStyles.rowArea} icons={layerToolsRow1} />
                <ToolbarRowComponent style={layerToolStyles.rowArea} icons={layerToolsRow2} />
            </View>
        )
    }

}

layerToolStyles = StyleSheet.create({
    toolArea: {
        flex: 1,
        flexDirection: 'column',
        margin: 0,
        padding: 0,
        height: pickerHeight,
    },
    rowArea: {
        height: pickerHeight/2
    }
})

function mapStateToProps (state, ownProps) {
    return {
        layerData: state.layer,
    }
}

function mapDispatchToProps (dispatch) {
    return {
        layerOpacityButtonPressed: (isPressed) => dispatch(ActionCreators.layerOpacityButtonPressed(isPressed)),
        layerGroupButtonPressed: (isPressed) => dispatch(ActionCreators.layerGroupButtonPressed(isPressed)),
        layerUngroupButtonPressed: (isPressed) => dispatch(ActionCreators.layerUngroupButtonPressed(isPressed)),
        layerFlipHorizontalButtonPressed: (isPressed) => dispatch(ActionCreators.layerFlipHorizontalButtonPressed(isPressed)),
        layerFlipVerticalButtonPressed: (isPressed) => dispatch(ActionCreators.layerFlipVerticalButtonPressed(isPressed)),
        layerSizeButtonPressed: (isPressed) => dispatch(ActionCreators.layerSizeButtonPressed(isPressed)),
        layerCopyButtonPressed: (isPressed) => dispatch(ActionCreators.layerCopyButtonPressed(isPressed)),
        layerDeleteButtonPressed: (isPressed) => dispatch(ActionCreators.layerDeleteButtonPressed(isPressed)),
        layerForwardButtonPressed: (isPressed) => dispatch(ActionCreators.layerForwardButtonPressed(isPressed)),
        layerFrontButtonPressed: (isPressed) => dispatch(ActionCreators.layerFrontButtonPressed(isPressed)),
        layerBackwardButtonPressed: (isPressed) => dispatch(ActionCreators.layerBackwardButtonPressed(isPressed)),
        layerBackButtonPressed: (isPressed) => dispatch(ActionCreators.layerBackButtonPressed(isPressed))
        }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(LayerTools)