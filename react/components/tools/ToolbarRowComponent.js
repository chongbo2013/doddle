import React, {Component} from 'react';
import {AppRegistry, Text, View, ListView, StyleSheet, Animated, ScrollView} from 'react-native';
import CircleButton from './CircleButton';
import { connect } from 'react-redux';
import {ActionCreators} from '../../actions/index'
import * as Constants from '../../constants'


class ToolbarRowComponent extends Component
{
    componentWillReceiveProps(newProps) {
        const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.setState({
            userDataSource: ds.cloneWithRows(newProps.icons)
        });
    }

    constructor(props) {
        super(props);
        const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.state = {
            userDataSource: ds.cloneWithRows(this.props.icons),
        };
    }
    checkToolActive(tool){
        if(tool == this.props.brushData.activetool) {
            return "white";
        }
        else return "transparent";
    }
    checkColorActive(colorcode) {
        switch(this.props.toolData.activetool) {
            case Constants.TOOL_DRAW: {
                return (this.props.brushData.brushColor == colorcode);
            }
            case Constants.TOOL_SHAPE: {
                return (this.props.shapeData.shapeColor == colorcode);
            }
        }
    }

    checkEnabled(buttonToCheck){
        switch(buttonToCheck){
            case 'undoButtonPressed': {
                return(this.props.toolData.undoActive);
                break
            }
            case 'redoButtonPressed': {
                return(this.props.toolData.redoActive);
                break
            }
            case 'deleteButtonPressed': {
                return(this.props.layerData.active)
            }
            default : {
                return true;
            }
        }
    }

    renderRow(icon, sectionID, rowID, highlightRow){
        var renderbutton;
        if(icon.iconName) {
            if (this.props.toolData.activetool == icon.passData){
                renderbutton = <CircleButton
                    iconButton={{iconName:icon.iconName}}
                    iconSet={icon.iconSet}
                    subtool={icon.subtool}
                    buttonSize = {icon.buttonSize}
                    baseColor = {'white'}
                    brushname = {icon.brushname}
                    colorPicker={icon.baseColor}
                    borderColor={icon.baseColor}
                    enabled = {this.checkEnabled(icon.action)}
                    action = {()=>{this.props[icon.action](icon.passData)}}/>
            } else {
                renderbutton = <CircleButton
                    iconButton={{iconName:icon.iconName}}
                    iconSet={icon.iconSet}
                    subtool={icon.subtool}
                    buttonSize = {icon.buttonSize}
                    baseColor = {icon.baseColor}
                    brushname = {icon.brushname}
                    ButtonColor={this.props.ButtonColor}
                    ButtonBorderColor={this.props.ButtonBorderColor}
                    onPressedBackgroundColor={this.props.onPressedBackgroundColor}
                    enabled = {this.checkEnabled(icon.action)}
                    action = {()=>{this.props[icon.action](icon.passData)}}/>
            }

        } else if(icon.imageButton) {
            renderbutton = <CircleButton
                buttonImage={icon.imageButton}
                toolname={icon.toolname}
                subtool={icon.subtool}
                buttonSize = {icon.buttonSize}
                colorPicker = {icon.passData == this.props.brushData.activeBrush? "white": "transparent"}
                borderColor={this.props.ButtonBorderColor}
                imageSizeOverride={icon.imageSizeOverride}
                onPressedBackgroundColor={this.props.onPressedBackgroundColor}
                isColorPickerButton = {icon.isColorPickerButton || icon.subtool == "setColorBrush" || icon.subtool == "setShapeColor"}
                enabled = {this.checkEnabled(icon.toolname)}
                action = {()=>{this.props[icon.action](icon.passData)}}/>
        } else {
            renderbutton = <CircleButton
                colorPicker={icon.colorPicker}
                toolname={icon.toolname}
                subtool={icon.subtool}
                colorcode = {icon.colorcode}
                brushname = {icon.brushname}
                buttonSize = {icon.buttonSize}
                ButtonColor={this.props.ButtonColor}
                ButtonBorderColor={this.props.ButtonBorderColor}
                onPressedBackgroundColor={this.props.onPressedBackgroundColor}
                active = {this.checkColorActive(icon.colorPicker)}
                enabled = {this.checkEnabled(icon.toolname)}
                action = {()=>{this.props[icon.action](icon.passData)}}/>
        }

        return(
            <View style = {[rowComponentStyles.row, {width: icon.buttonContainerWidth}]}>
                {renderbutton}
            </View>
        )
    }

    render() {
        return (
                <ListView
                    scrollEnabled = {false}
                    contentContainerStyle={rowComponentStyles.grid}
                    dataSource={this.state.userDataSource}
                    renderRow={this.renderRow.bind(this)}
                />
        );
    }
}


ToolbarRowComponent.defaultProps = {
    onPressDone: ()=> {onPress();}
};

const rowComponentStyles = StyleSheet.create({
    grid: {
        flex:1,
        justifyContent: 'center',
        alignItems: 'center',
        flexDirection: 'row',
        margin:0,
        padding:0,
        backgroundColor:"transparent",

    },
    row: {
        alignItems: 'center',
        marginTop: 0,

    }
});

function mapStateToProps (state, ownProps) {
    return {

        brushData: state.brush,
        toolData: state.tool,
        shapeData: state.shape,
        name: ownProps.name,
        toolname: ownProps.toolname,
        ButtonColor: ownProps.ButtonColor,
        ButtonBorderColor: ownProps.ButtonBorderColor,
        onPressedBackgroundColor: ownProps.onPressedBackgroundColor,
        colorcode: ownProps.colorcode,
        appData: state.data,
        layerData: state.layer,
    }
}

function mapDispatchToProps (dispatch) {
    return {
        setActiveTool: (toolname) => dispatch(ActionCreators.setActiveTool(toolname)),
        setActiveBrush: (brushname) => dispatch(ActionCreators.setActiveBrush(brushname)),
        setActiveShape: (shapename) => dispatch(ActionCreators.setActiveShape(shapename)),
        setActiveBrushSubmenu: (submenu) => dispatch(ActionCreators.setActiveBrushSubmenu(submenu)),
        setActiveShapeSubmenu: (submenu) => dispatch(ActionCreators.setActiveShapeSubmenu(submenu)),
        setBrushColor: (brushcolor) => dispatch(ActionCreators.setBrushColor(brushcolor)),
        setShapeColor: (shapecolor) => dispatch(ActionCreators.setShapeColor(shapecolor)),
        resetBrushSubmenu: () => dispatch(ActionCreators.resetBrushSubmenu()),
        resetShapeSubmenu: () => dispatch(ActionCreators.resetShapeSubmenu()),
        setCustomShapeStatus: (value) => dispatch(ActionCreators.setCustomShapeStatus(value)),
        undoButtonPressed: (isPressed) => dispatch(ActionCreators.undoButtonPressed(isPressed)),
        redoButtonPressed: (isPressed) => dispatch(ActionCreators.redoButtonPressed(isPressed)),
        deleteDodleButtonPressed: (isPressed) => dispatch(ActionCreators.deleteDodleButtonPressed(isPressed)),
        deleteButtonPressed: (isPressed) => dispatch(ActionCreators.deleteButtonPressed(isPressed)),
        sharedDodleButtonPressed: (isPressed) => dispatch(ActionCreators.sharedDodleButtonPressed(isPressed)),
        // layer tool actions
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
        layerBackButtonPressed: (isPressed) => dispatch(ActionCreators.layerBackButtonPressed(isPressed)),
        deleteConformation: (isPressed) => dispatch(ActionCreators.deleteConformation(isPressed)),
        shareConformation: (isPressed) => dispatch(ActionCreators.shareConformation(isPressed))
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ToolbarRowComponent)

// AppRegistry.registerComponent('ToolbarRowComponent', () => ToolbarRowComponent);