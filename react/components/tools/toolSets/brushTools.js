import React, {Component} from 'react'
import {Image, StyleSheet, Text, TouchableHighlight, View} from 'react-native'
import Slider from 'react-native-slider';
import ToolbarRowComponent from "../ToolbarRowComponent";
import {connect} from 'react-redux'
import {ActionCreators} from '../../../actions/index'
import CircleButton from '../CircleButton';
import HueSaturationBlock from '../HueSaturationBlock';
import * as Constants from '../../../constants'

const pickerHeight = Constants.toolAreaHeight - (Constants.toolRowColapsed + 12);

const brushPickerrow1 = [
    {imageButton: Constants.buttonImages.pencil, buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveBrush', passData: 'pencil'},
    {imageButton: Constants.buttonImages.drybrush, buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveBrush', passData: 'drybrush'},
    {imageButton: Constants.buttonImages.eraser, buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveBrush', passData: 'erasebrush'}
];
const brushPickerrow2 = [
    {imageButton: Constants.buttonImages.fountainpen, buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveBrush', passData: 'fountainpen'},
    {imageButton: Constants.buttonImages.marker, buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveBrush', passData: 'marker'},
    {imageButton: Constants.buttonImages.chalk, buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveBrush', passData: 'chalk'}
];
const colorpickerRow1 = [
    {colorPicker: '#F8EC1F', buttonSize: Constants.subtoolButtonSize3Row, action: 'setBrushColor', passData: '#F8EC1F'},
    {colorPicker: '#F1991F', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#F1991F'},
    {colorPicker: '#F76FC9', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#F76FC9'},
    {colorPicker: '#129EC2', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#129EC2'},
    {colorPicker: '#61F203', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#61F203'}];

const colorpickerRow2 = [
    {colorPicker: '#EB2C2A', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#EB2C2A'},
    {colorPicker: '#670AC9', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#670AC9'},
    {colorPicker: '#004BB7', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#004BB7'},
    {colorPicker: '#1D9105', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#1D9105'}];

const colorpickerRow3 = [
    {colorPicker: '#F7BD77', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#F7BD77'},
    {colorPicker: '#A8680C', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#A8680C'},
    {colorPicker: '#FFFFFF', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#FFFFFF'},
    {colorPicker: '#939393', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#848484'},
    {colorPicker: '#000000', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setBrushColor', passData: '#000000'}];

class BrushTools extends Component
{
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        this.props.setActiveBrush('pencil');
        this.props.setBrushColor('#848484');
        this.props.setBrushSize(2);
        this.props.setBrushOpacity(1);
    }

    renderColorSizeCircle(brushSize,brushColor,brushOpacity) {
        let circleSize = ((brushSize / 100) * (Constants.subtoolButtonSize2Row - 10));
        circleSize = circleSize > 5? circleSize : 5;
        let radius = circleSize / 2;
        let positionBottom = Constants.subtoolButtonSize2Row - 10 - radius;
        return(
            <View style={{
                height: circleSize,
                width: circleSize,
                backgroundColor: brushColor,
                opacity: brushOpacity,
                borderRadius: circleSize / 2,
                position:'absolute',
                bottom: positionBottom,
                alignSelf: 'center',
                zIndex: 11,
            }}/>
        )
    }

    renderBrushSubmenu() {
        switch (this.props.brushData.activeSubmenu) {
            case 'BrushToolPicker': {
                return(
                    <View style={brushToolStyles.brushPickerTools}>
                        <View style={brushToolStyles.rulerArea}>
                            <CircleButton
                                buttonImage={Constants.buttonImages.ruler}
                                buttonSize={Constants.subtoolButtonSize2Row}
                                colorPicker = {this.props.brushData.rulerMode? 'white': 'transparent'}
                                action = {()=>{this.props.switchRulerMode()}}/>
                        </View>
                        <View style={brushToolStyles.rightSideTools}>
                            <View style={{height:((Constants.toolAreaHeight - 90)/3)+90}}>
                                <ToolbarRowComponent icons={brushPickerrow1}/>
                                <ToolbarRowComponent icons={brushPickerrow2}/>
                            </View>
                        </View>
                    </View>
                );
                break;
            }
            case 'BrushColorPicker' : {
                return(
                   <View style={brushToolStyles.colorSwatch}>
                       <ToolbarRowComponent icons={colorpickerRow1}/>
                       <ToolbarRowComponent icons={colorpickerRow2}/>
                       <ToolbarRowComponent icons={colorpickerRow3}/>
                   </View>
                )
                break;
            }
            case 'BrushSizeOpacity' : {
                sizeText = 'Size: ' + this.props.brushData.brushSize.toFixed(0);
                opacityText = 'Opacity: ' + this.props.brushData.brushOpacity.toFixed(2) *100 +'%';
                return(
                    <View style={brushToolStyles.sizeOpacityBlock}>
                        <Slider
                            style = {brushToolStyles.Slider}
                            trackStyle = {brushToolStyles.SliderTrack}
                            thumbTintColor = 'white'
                            thumbStyle = {brushToolStyles.SliderHandle}
                            minimumTrackTintColor = '#129EC2'
                            maximumTrackTintColor = 'white'
                            value={this.props.brushData.brushOpacity}
                            minimumValue={0}
                            maximumValue={1}
                            onValueChange={(value)=>this.props.setBrushOpacity(value)}/>
                        <Text>
                            {opacityText}
                        </Text>
                        <Slider
                            style = {brushToolStyles.Slider}
                            trackStyle = {brushToolStyles.SliderTrack}
                            thumbTintColor = 'white'
                            thumbStyle = {brushToolStyles.SliderHandle}
                            minimumTrackTintColor = '#129EC2'
                            maximumTrackTintColor = 'white'
                            value={this.props.brushData.brushSize}
                            minimumValue={1}
                            maximumValue={50}
                            onValueChange={(value)=>this.props.setBrushSize(value)}/>
                        <Text>
                            {sizeText}
                        </Text>

                    </View>
                );
                break;
            }
            case 'BrushHueSaturationPicker' : {
                return(
                    <HueSaturationBlock/>
                )
                break;
            }
        }
    }
    //
    render(){
        return (
            <View style={brushToolStyles.toolArea}>
                <View style={brushToolStyles.leftSideToolOptions}>
                    <View style={brushToolStyles.toolOptionsPicker}>
                        <View style={brushToolStyles.androidBorderFix}/>
                        <Image style={brushToolStyles.toolPickerLayer1} source={Constants.buttonImages.checker25Alpha} resizeMode="cover"/>
                        {this.renderColorSizeCircle(this.props.brushData.brushSize, this.props.brushData.brushColor, this.props.brushData.brushOpacity)}

                        <Image style={brushToolStyles.toolPickerLayer3} source={Constants.buttonImages[this.props.brushData.activeBrush]}/>
                        <TouchableHighlight
                            style={brushToolStyles.toolPickerButton}
                            underlayColor={'transparent'}
                            onPress={()=>{this.props.brushToolSelectorPressed()}}>
                            <View style={brushToolStyles.toolPickerButton}></View>
                        </TouchableHighlight>
                    </View>
                    {this.props.brushData.toolPickerActive? <View style={brushToolStyles.toolOptions}>
                        <View  style={brushToolStyles.toolPickerButtons}>
                            <CircleButton
                                buttonSize={Constants.subtoolButtonSize2Row - 10}
                                buttonImage={Constants.buttonImages.opacity}
                                baseColor="white"
                                action={()=>{this.props.setActiveBrushSubmenu('BrushSizeOpacity')}} />
                        </View>
                        <View  style={brushToolStyles.toolPickerButtons}>
                            <CircleButton
                                buttonSize={Constants.subtoolButtonSize2Row - 10}
                                buttonImage={Constants.buttonImages.hueSaturation}
                                baseColor="white"
                                action={()=>{this.props.setActiveBrushSubmenu('BrushHueSaturationPicker');
                                    this.props.setBrushOldColor(this.props.brushData.brushColor);}} />
                        </View>
                        <View  style={brushToolStyles.toolPickerButtons}>
                            <CircleButton
                                buttonSize={Constants.subtoolButtonSize2Row - 10}
                                buttonImage={Constants.buttonImages.dotSwatch}
                                baseColor="white"
                                action={()=>{this.props.setActiveBrushSubmenu('BrushColorPicker')}} />
                        </View>
                        <View  style={brushToolStyles.toolPickerButtons}>
                            <CircleButton
                                buttonSize={Constants.subtoolButtonSize2Row - 10}
                                iconButton={{iconName:'Brush'}}
                                baseColor="#939393"
                                borderColor="transparent"
                                action={()=>{this.props.setActiveBrushSubmenu('BrushToolPicker')}}/>
                        </View>
                    </View>:null}
                </View>
                <View style={brushToolStyles.rightSideToolOptions}>
                    {this.renderBrushSubmenu()}
                </View>

            </View>
        )
    }
}

brushToolStyles = StyleSheet.create({
    toolArea: {
        flex: 1,
        flexDirection: 'row',
        margin: 0,
        padding: 0,
        height: pickerHeight,
    },
    leftSideToolOptions: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',

    },
    rightSideToolOptions: {
        flex: 3,
        height: pickerHeight,
        justifyContent: 'center',
        alignItems: 'center',
    },
    brushPickerTools: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
    },
    rulerArea: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'flex-end',
    },
    rightSideTools: {
        flex: 3,
        justifyContent: 'center',
        alignItems: 'flex-start',
    },
    toolOptionsPicker: {
        width: Constants.subtoolButtonSize2Row,
        height: (pickerHeight / 2) + 10,
        borderBottomLeftRadius: Constants.subtoolButtonSize2Row / 2,
        borderBottomRightRadius: Constants.subtoolButtonSize2Row /2,
        backgroundColor: 'white',
        borderColor: '#848484',
        borderWidth: 1,
        borderTopWidth: 0,
        position:'absolute',
        top: 0,
        zIndex:200,
        justifyContent: 'flex-end',
    },
    androidBorderFix: {
        width: Constants.subtoolButtonSize2Row - 2,
        height: 7,
        backgroundColor: 'white',
        position:'absolute',
        top: -5,
        zIndex: 100,
    },
    toolPickerButton:{
        height: Constants.subtoolButtonSize2Row * 1.2,
        width: Constants.subtoolButtonSize2Row - 10,
        backgroundColor: 'transparent',
        position: 'absolute',
        bottom:0,
        left:5,
        borderRadius:(Constants.subtoolButtonSize2Row - 10) / 2,
        zIndex: 100,
    },
    colorSwatch:{
        flex: 1,
        padding: 0,
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
    },
    sizeOpacityBlock:{
        width: Constants.deviceWidth * .75 -20,
         margin: 5,
        flexDirection:'column',
    },
    sliders:{
    },
    toolPickerLayer1:{
        height: Constants.subtoolButtonSize2Row - 10,
        width:Constants.subtoolButtonSize2Row - 10,
        borderRadius: (Constants.subtoolButtonSize2Row - 10) / 2,
        padding: 0,
        position: 'absolute',
        bottom: (Constants.subtoolButtonSize2Row - 10) / 2,
        alignSelf:'center',
        zIndex: 10,
    },
    toolPickerLayer3:{
        height: Constants.subtoolButtonSize2Row - 10,
        width:Constants.subtoolButtonSize2Row - 10,
        position: 'absolute',
        bottom: 0,
        alignSelf:'center',
        borderRadius: (Constants.subtoolButtonSize2Row - 10) / 2,
        zIndex: 12,
    },
    toolOptions: {
        width: Constants.subtoolButtonSize2Row,
        backgroundColor: 'white',
        borderColor: '#848484',
        borderBottomColor: 'white',
        borderWidth: 1,
        borderBottomWidth: 0,
        borderTopLeftRadius: Constants.subtoolButtonSize2Row / 2,
        borderTopRightRadius: Constants.subtoolButtonSize2Row / 2,
        position:'absolute',
        paddingBottom: 20,
        top: ((Constants.subtoolButtonSize2Row -10) * 4 + 80) * -1,
    },
    toolPickerButtons: {
        marginTop: 20,
        alignSelf:'center',
    }

});

function mapStateToProps (state, ownProps) {
    return {
        subtool: ownProps.subtool,
        brushname: ownProps.brushname,
        brushData: state.brush,
        toolData: state.tool,
        name: ownProps.name,
        toolname: ownProps.toolname,
        ButtonColor: ownProps.ButtonColor,
        ButtonBorderColor: ownProps.ButtonBorderColor,
        onPressedBackgroundColor: ownProps.onPressedBackgroundColor,
        colorcode: ownProps.colorcode,
    }
}

function mapDispatchToProps (dispatch) {
    return {
        setActiveTool: (toolname) => dispatch(ActionCreators.setActiveTool(toolname)),
        setActiveBrush: (brushname) => dispatch(ActionCreators.setActiveBrush(brushname)),
        setActiveBrushSubmenu: (submenu) => dispatch(ActionCreators.setActiveBrushSubmenu(submenu)),
        setBrushColor: (brushcolor) => dispatch(ActionCreators.setBrushColor(brushcolor)),
        setBrushOldColor: (brushOldColor) => dispatch(ActionCreators.setBrushOldColor(brushOldColor)),
        setBrushSize: (brushsize) => dispatch(ActionCreators.setBrushSize(brushsize)),
        switchRulerMode: () => dispatch(ActionCreators.switchRulerMode()),
        setBrushOpacity: (brushopacity) => dispatch(ActionCreators.setBrushOpacity(brushopacity)),
        resetBrushSubmenu: () => dispatch(ActionCreators.resetBrushSubmenu()),
        undoButtonPressed: (isPressed) => dispatch(ActionCreators.undoButtonPressed(isPressed)),
        redoButtonPressed: (isPressed) => dispatch(ActionCreators.redoButtonPressed(isPressed)),
        brushToolSelectorPressed: () => dispatch(ActionCreators.brushToolSelectorPressed())
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(BrushTools)
