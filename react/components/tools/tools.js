import React, {Component} from 'react'
import { TouchableHighlight, View,Modal, Text, StyleSheet, Dimensions, Platform, StatusBar, LayoutAnimation, TouchableWithoutFeedback } from 'react-native'
import DraggableView from "./DraggableView";
import ToolbarRowComponent from "./ToolbarRowComponent";
import BrushTools from  "./toolSets/brushTools"
import ShapeTools from "./toolSets/shapeTools"
import LayerTools from "./toolSets/layerTools"
import AnamationTools from "./toolSets/animationTools"
import { connect } from 'react-redux'
import {ActionCreators} from '../../actions/index'
import * as Constants from '../../constants'
import TrashConformation from '../GalleryImageComponent/TrashConformation'
import ShareConformation from '../GalleryImageComponent/ShareDodle'


const toolButtonSize = Constants.toolRowColapsed * .7;
const toolBarRow1 = [
    {iconName: 'Draw', buttonSize: toolButtonSize, buttonContainerWidth: Constants.toolMenuButtonBlockWidth, baseColor: '#129EC2', action: 'setActiveTool', passData: Constants.TOOL_DRAW},
    {iconName: 'Shapes', buttonSize: toolButtonSize, buttonContainerWidth: Constants.toolMenuButtonBlockWidth, baseColor: '#129EC2', action: 'setActiveTool', passData: Constants.TOOL_SHAPE},
    {iconName: 'Animate', buttonSize: toolButtonSize, buttonContainerWidth: Constants.toolMenuButtonBlockWidth, baseColor: '#129EC2', action: 'setActiveTool', passData: Constants.TOOL_ANIMATION},
    {iconName: 'Undo',  buttonSize: toolButtonSize, buttonContainerWidth: Constants.toolMenuButtonBlockWidth, baseColor: '#129EC2', action: 'undoButtonPressed', passData: true},
    {iconName: 'Redo', buttonSize: toolButtonSize, buttonContainerWidth: Constants.toolMenuButtonBlockWidth, baseColor: '#129EC2', action: 'redoButtonPressed', passData: true}
]
const toolBarRow2= [
    {iconName: 'Trash', buttonSize: toolButtonSize, buttonContainerWidth: Constants.toolMenuButtonBlockWidth2, baseColor: '#129EC2', action: 'deleteConformation', passData: true},
    {iconName: 'Send-V1', buttonSize: toolButtonSize, buttonContainerWidth: Constants.toolMenuButtonBlockWidth2, baseColor: '#129EC2', action: 'shareConformation', passData: true}
    ]


class Tools extends Component {

    constructor(props) {
        super(props);
    }

    componentWillReceiveProps(nextProps) {
        if(!this.props.animationData.showHelp == null) {
            LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
            if (nextProps.toolData.activetool) {
                this.draggableview.onActiveToolChanged();
            }
        }
    }

    checkActive(colorcode) {
        switch(this.props.toolData.activetool) {
            case Constants.TOOL_DRAW: {
                return (this.props.brushData.brushColor == colorcode);
            }
            case Constants.TOOL_SHAPE: {
                return (this.props.shapeData.shapeColor == colorcode);
            }
        }
    }

    renderSubTool(activetool) {
        switch (activetool){
            case 'TOOL.DRAW': {
                return(<BrushTools/>);
                break;
            }
            case 'TOOL.GEOMETRY': {
                return(<ShapeTools/>);
                break;
            }
            case 'TOOL.LAYER': {
                return(<LayerTools/>);
                break;
            }
            case 'TOOL.ANIMATION': {
                return(<AnamationTools/>);
                break;
            }
            default: {
                return(null);
            }

        }
    }
    renderDragableMenu (){
        if(this.props.animationData.showHelp == null){
            return (
                <DraggableView
                    ref={draggableview => {
                        this.draggableview = draggableview;
                    }}
                    maxHeight={Constants.toolRowHeight}
                    minHeight={Constants.toolRowColapsed + 12}
                    children= {
                        <View style={ToolStyles.baseToolArea}>
                            <View style={{height: Constants.toolRowColapsed}}>

                                <ToolbarRowComponent icons={toolBarRow1}/>
                            </View>
                            <View style={{height: Constants.toolRowColapsed}}>

                                <ToolbarRowComponent icons={toolBarRow2}/>
                            </View>
                        </View>
                    }
                />
            )
        }else {
            return null;
        }

    }

    renderDialog() {

        if(this.props.toolData.deleteConformationFlag) {
            return(
            <TrashConformation />
            );
        }
        else if(this.props.toolData.shareConformationFlag){
            return(
               <ShareConformation />
            );
        }
    }

    setFlag() {
        if(this.props.toolData.deleteConformationFlag) {
                        this.props.deleteConformation(false);
                }
                else if(this.props.toolData.shareConformationFlag){

                        this.props.shareConformation(false);

                }


    }
    render() {

        return(
            <View style={ToolStyles.toolContainer}>
                  <Modal transparent={true}
                                               animationType="none"
                                               visible={this.props.toolData.deleteConformationFlag ||
                                               this.props.toolData.shareConformationFlag}
                                               onRequestClose={() => {alert("Modal has been closed.")}}>
                                        <TouchableWithoutFeedback onPress={() => {this.setFlag()}}>
                                                <View style={ToolStyles.modalStyle}>
                                                   {this.renderDialog()}
                                                </View>
                                        </TouchableWithoutFeedback>
                                        </Modal>
                {this.renderSubTool(this.props.toolData.activetool)}
                {this.renderDragableMenu()}
            </View>
        );
    }

}

ToolStyles = StyleSheet.create({
    modalStyle:{
           flex: 1,
           backgroundColor: 'rgba(0,0,0,0.2)',
           alignItems: 'center',
           justifyContent: 'center'
       },
    toolContainer: {
        flex: 1,
        margin: 0,
        padding: 0,
        height: Constants.toolAreaHeight - Constants.toolRowColapsed,
    },
    baseToolArea: {
        flex: 1,
        backgroundColor: '#F3F3F3',
        marginTop: -2,
        zIndex:100,
    },

})

function mapStateToProps (state) {
    return {
        appData: state.data,
        toolData: state.tool,
        brushData: state.brush,
        layerData: state.layer,
        animationData: state.animation,

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
        deleteConformation: (isPressed) => dispatch(ActionCreators.deleteConformation(isPressed)),
                shareConformation: (isPressed) => dispatch(ActionCreators.shareConformation(isPressed))



    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Tools)