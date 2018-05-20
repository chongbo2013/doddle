import React, {Component} from 'react';
import {AppRegistry, Text, View, Button, TouchableHighlight, TouchableOpacity, StyleSheet, Image} from 'react-native';
import { connect } from 'react-redux'
import {ActionCreators} from '../../actions/index'
import CircleButton from './CircleButton'
import ToolbarRowComponent from "./ToolbarRowComponent";
import * as Constants from '../../constants'

const HueSaturation = Constants.buttonImages.hueSaturation;

const colorpickerRow1 = [
    {colorPicker: '#F8EC1F', buttonSize: 30, action: 'setBrushColor', passData: '#F8EC1F'},
    {colorPicker: '#F1991F', buttonSize: 30,  action: 'setBrushColor', passData: '#F1991F'},
    {colorPicker: '#F76FC9', buttonSize: 30,  action: 'setBrushColor', passData: '#F76FC9'},
    {colorPicker: '#129EC2', buttonSize: 30,  action: 'setBrushColor', passData: '#129EC2'},
    {colorPicker: '#61F203', buttonSize: 30,  action: 'setBrushColor', passData: '#61F203'}];

const colorpickerRow2 = [
    {colorPicker: '#EB2C2A', buttonSize: 30,  action: 'setBrushColor', passData: '#EB2C2A'},
    {colorPicker: '#670AC9', buttonSize: 30,  action: 'setBrushColor', passData: '#670AC9'},
    {colorPicker: '#004BB7', buttonSize: 30,  action: 'setBrushColor', passData: '#004BB7'},
    {colorPicker: '#1D9105', buttonSize: 30,  action: 'setBrushColor', passData: '#1D9105'}];

const colorpickerRow3 = [
    {colorPicker: '#F7BD77', buttonSize: 30,  action: 'setBrushColor', passData: '#F7BD77'},
    {colorPicker: '#A8680C', buttonSize: 30,  action: 'setBrushColor', passData: '#A8680C'},
    {colorPicker: '#FFFFFF', buttonSize: 30,  action: 'setBrushColor', passData: '#FFFFFF'},
    {colorPicker: '#939393', buttonSize: 30,  action: 'setBrushColor', passData: '#939393'},
    {colorPicker: '#000000', buttonSize: 30,  action: 'setBrushColor', passData: '#000000'}];

class ColorPallete extends Component
{
    constructor(props) {
        super(props);
    }

    goBack() {
        this.props.onGoingBack();
    }

    onPress() {
    }

    checkColor() {
        switch(this.props.toolData.activetool) {
            case Constants.TOOL_DRAW: {
                return this.props.brushData.brushColor;
            }
            case Constants.TOOL_SHAPE: {
                return this.props.shapeData.shapeColor;
            }
        }
    }
    checkTool() {
        if(this.props.toolData.activetool == Constants.TOOL_DRAW) {
            return (ToolbarLayout.buttonImages[this.props.brushData.activeBrush]);
        }
    }


    render() {
        return (
            <View style = {styles.ColorPalleteContainer}>
                <View style = {styles.SaturationBlock}>
                    <CircleButton
                        colorPicker = {this.checkColor()}
                        buttonImage ={this.checkTool()}
                        buttonSize={45}
                        imageSizeOverride = {1}
                        isColorPickerButton = {true}
                        colorPickerButtonColor = {this.checkColor()}
                        action ={this.props.onGoingBack}/>
                    <CircleButton
                        buttonImage={Constants.buttonImages.hueSaturation}
                        buttonSize ={45}
                        imageSizeOverride={1.1}
                        action ={this.onPress.bind(this)}/>
                </View>
                <View style = {styles.ColorBlock}>
                    <ToolbarRowComponent icons={colorpickerRow1}/>
                    <ToolbarRowComponent icons={colorpickerRow2}/>
                    <ToolbarRowComponent icons={colorpickerRow3}/>
                </View>
            </View>
        );
    }
}


const styles = StyleSheet.create({
    ColorPalleteContainer: {
        flexDirection: 'row',
        height: 130,
    },
    SaturationBlock: {
        flexDirection: 'column',
        flex: 1,
        paddingBottom: 8,
        paddingTop: 8,
        borderRightWidth: 1.5,
        borderColor: 'grey',
        justifyContent: 'space-between',
        alignItems: 'center'
    },
    ColorBlock: {
        flex: 4,
    }
});



function mapStateToProps (state, ownProps) {
    return {
        brushData: state.brush,
        toolData: state.tool,
        shapeData: state.shape
    }
}

function mapDispatchToProps (dispatch) {
    return {
        setActiveTool: (toolname) => dispatch(ActionCreators.setActiveTool(toolname)),
        setActiveBrush: (brushname) => dispatch(ActionCreators.setActiveBrush(brushname)),
        setActiveBrushSubmenu: (submenu) => dispatch(ActionCreators.setActiveBrushSubmenu(submenu)),
        resetBrushSubmenu: () => dispatch(ActionCreators.resetBrushSubmenu()),
        resetShapeSubmenu: () => dispatch(ActionCreators.resetShapeSubmenu())
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ColorPallete)