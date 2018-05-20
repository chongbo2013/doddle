import React, {Component} from 'react'
import {StyleSheet, Text, TouchableHighlight, View} from 'react-native'
import Slider from 'react-native-slider';
import ToolbarRowComponent from "../ToolbarRowComponent";
import {connect} from 'react-redux'
import {ActionCreators} from '../../../actions/index'
import CircleButton from '../CircleButton';
import HueSaturationBlock from '../HueSaturationBlock';
import * as Constants from '../../../constants'

const pickerHeight = Constants.toolAreaHeight - (Constants.toolRowColapsed + 12);


const shapePickerrow1 = [
    {iconName: 'Circle', buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveShape', passData: 'circle'},
    {iconName: 'Square', buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveShape', passData: 'rectangle' },
    {iconName: 'Triangle', buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveShape', passData: 'triangle' },
    {iconName: 'Polygon', buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveShape', passData: 'polygon' },
    {iconName: 'Star', buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveShape', passData: 'star' },
];

const shapePickerrow2 = [
    {iconName: 'Heart', buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveShape', passData: 'heart' },
    {iconName: 'Freeform', buttonSize: Constants.subtoolButtonSize2Row, action: 'setActiveShape', passData: 'CustomShape'  }

];
const colorpickerRow1 = [
    {colorPicker: '#F8EC1F', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#F8EC1F'},
    {colorPicker: '#F1991F', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#F1991F'},
    {colorPicker: '#F76FC9', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#F76FC9'},
    {colorPicker: '#129EC2', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#129EC2'},
    {colorPicker: '#61F203', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#61F203'}];

const colorpickerRow2 = [
    {colorPicker: '#EB2C2A', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#EB2C2A'},
    {colorPicker: '#670AC9', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#670AC9'},
    {colorPicker: '#004BB7', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#004BB7'},
    {colorPicker: '#1D9105', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#1D9105'}];

const colorpickerRow3 = [
    {colorPicker: '#F7BD77', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#F7BD77'},
    {colorPicker: '#A8680C', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#A8680C'},
    {colorPicker: '#FFFFFF', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#FFFFFF'},
    {colorPicker: '#939393', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#939393'},
    {colorPicker: '#000000', buttonSize: Constants.subtoolButtonSize3Row,  action: 'setShapeColor', passData: '#000000'}];

class ShapeTools extends Component
{
    constructor(props) {
        super(props);
    }

    iconPicker(activeShape) {
        switch(activeShape){
            case 'circle': {
                return 'Circle';
                break;
            }
            case 'rectangle': {
                return 'Square';
                break;
            }
            case 'triangle': {
                return 'Triangle';
                break;
            }
            case 'polygon': {
                return 'Polygon';
                break;
            }
            case 'star': {
                return 'Star';
                break;
            }
            case 'heart': {
                return 'Heart';
                break;
            }
            case 'CustomShape': {
                return 'Freeform';
                break;
            }
        }
    }

    renderColorSizeCircle(shapeSize,shapeColor,shapeOpacity) {
        let circleSize = shapeSize / 2;
        let radius = circleSize / 2
        if(circleSize < 2){
            circleSize = 2
        };
        let positionBottom = Constants.subtoolButtonSize2Row - 10 - radius;
        return(
            <View style={{
                height: circleSize,
                width: circleSize,
                backgroundColor: shapeColor,
                opacity: shapeOpacity,
                borderRadius: circleSize / 2,
                position:'absolute',
                bottom: positionBottom,
                left: positionLeft,
                zIndex: 11,
            }}/>
        )
    }

    rendershapeSubmenu() {
        switch (this.props.shapeData.activeSubmenu) {
            case 'shapeColorPicker' : {
                return(
                    <View style={shapeToolStyles.colorSwatch}>
                        <ToolbarRowComponent icons={colorpickerRow1}/>
                        <ToolbarRowComponent icons={colorpickerRow2}/>
                        <ToolbarRowComponent icons={colorpickerRow3}/>
                    </View>
                )
                break;
            }
            case 'shapeSizeOpacity' : {
                sizeText = 'Size: ' + this.props.shapeData.shapeSize.toFixed(0);
                opacityText = 'Opacity: ' + this.props.shapeData.shapeOpacity.toFixed(2)*100 + '%';
                return(
                    <View style={shapeToolStyles.sizeOpacityBlock}>
                        <Slider
                            style = {shapeToolStyles.Slider}
                            trackStyle = {shapeToolStyles.SliderTrack}
                            thumbTintColor = 'white'
                            thumbStyle = {shapeToolStyles.SliderHandle}
                            minimumTrackTintColor = '#129EC2'
                            maximumTrackTintColor = 'white'
                            value={this.props.shapeData.shapeOpacity}
                            minimumValue={0}
                            maximumValue={1}
                            onValueChange={(value)=>this.props.setShapeOpacity(value)}/>
                        <Text>
                            {opacityText}
                        </Text>
                        <Slider
                            style = {shapeToolStyles.Slider}
                            trackStyle = {shapeToolStyles.SliderTrack}
                            thumbTintColor = 'white'
                            thumbStyle = {shapeToolStyles.SliderHandle}
                            minimumTrackTintColor = '#129EC2'
                            maximumTrackTintColor = 'white'
                            value={this.props.shapeData.shapeSize}
                            minimumValue={1}
                            maximumValue={150}
                            onValueChange={(value)=>this.props.setShapeSize(value)}/>
                        <Text>
                            {sizeText}
                        </Text>

                    </View>
                );
                break;
            }
            case 'shapeHueSaturationPicker' : {
                return(
                    <HueSaturationBlock/>
                )
                break;
            }
            case 'ShapeCornerPicker' : {
                roundingText = this.props.shapeData.shapeRounding+ 'pt';
                cornersText = this.props.shapeData.shapeCorner;
                starDepthText = this.props.shapeData.starDepth + '%';
                let round = <View>
                                <Slider
                                    style = {shapeToolStyles.Slider}
                                    trackStyle = {shapeToolStyles.SliderTrack}
                                    thumbTintColor = 'white'
                                    thumbStyle = {shapeToolStyles.SliderHandle}
                                    minimumTrackTintColor = '#129EC2'
                                    maximumTrackTintColor = 'white'
                                    value={this.props.shapeData.shapeRounding}
                                    minimumValue={0}
                                    maximumValue={30}
                                    step={1}
                                    onValueChange={(value)=>this.props.setShapeRounding(value)}/>
                                <Text>
                                    Rounding: {roundingText}
                                </Text>
                            </View>;

                if(this.props.shapeData.activeShape === 'circle' || this.props.shapeData.activeShape === 'heart')
                {
                    this.props.setActiveShapeSubmenu('shapeColorPicker')
                }
                else if(this.props.shapeData.activeShape === 'rectangle' || this.props.shapeData.activeShape === 'triangle')
                {
                    return(
                        <View style={shapeToolStyles.sizeConfigBlock}>
                            {round}
                        </View>
                    )
                }
                else if(this.props.shapeData.activeShape === 'polygon')
                {
                    return(
                        <View style={shapeToolStyles.sizeConfigBlock}>
                            {round}
                            <Slider
                                style = {shapeToolStyles.Slider}
                                trackStyle = {shapeToolStyles.SliderTrack}
                                thumbTintColor = 'white'
                                thumbStyle = {shapeToolStyles.SliderHandle}
                                minimumTrackTintColor = '#129EC2'
                                maximumTrackTintColor = 'white'
                                value={this.props.shapeData.shapeCorner}
                                minimumValue={5}
                                maximumValue={20}
                                step={1}
                                onValueChange={(value)=>this.props.setShapeCorner(value)}/>
                            <Text>
                                Corners: {cornersText}
                            </Text>

                        </View>
                    )
                }
                else if(this.props.shapeData.activeShape === 'star')
                {
                    return(
                        <View style={shapeToolStyles.sizeConfigBlock}>
                            <Slider
                                style = {shapeToolStyles.Slider}
                                trackStyle = {shapeToolStyles.SliderTrack}
                                thumbTintColor = 'white'
                                thumbStyle = {shapeToolStyles.SliderHandle}
                                minimumTrackTintColor = '#129EC2'
                                maximumTrackTintColor = 'white'
                                value={this.props.shapeData.shapeRounding}
                                minimumValue={0}
                                maximumValue={30}
                                step={1}
                                onValueChange={(value)=>this.props.setShapeRounding(value)}/>
                            <Text style={{fontSize: 10}}>
                                Rounding: {roundingText}
                            </Text>
                            <Slider
                                style = {shapeToolStyles.Slider}
                                trackStyle = {shapeToolStyles.SliderTrack}
                                thumbTintColor = 'white'
                                thumbStyle = {shapeToolStyles.SliderHandle}
                                minimumTrackTintColor = '#129EC2'
                                maximumTrackTintColor = 'white'
                                value={this.props.shapeData.shapeCorner}
                                minimumValue={3}
                                maximumValue={20}
                                step={1}
                                onValueChange={(value)=>this.props.setShapeCorner(value)}/>
                            <Text style={{fontSize: 10}}>
                                Corners: {cornersText}
                            </Text>
                            <Slider
                                style = {shapeToolStyles.Slider}
                                trackStyle = {shapeToolStyles.SliderTrack}
                                thumbTintColor = 'white'
                                thumbStyle = {shapeToolStyles.SliderHandle}
                                minimumTrackTintColor = '#129EC2'
                                maximumTrackTintColor = 'white'
                                value={this.props.shapeData.starDepth}
                                minimumValue={0}
                                maximumValue={100}
                                step={1}
                                onValueChange={(value)=>this.props.setShapeStarDepth(value)}/>
                            <Text style={{fontSize: 10}}>
                                Depth: {starDepthText}
                            </Text>
                        </View>
                    )
                }
                break;
            }

        }
    }



    render(){

        if(this.props.shapeData.activeShape === null)
        {
            return(
                <View style={shapeToolStyles.toolArea}>
                    <View style={shapeToolStyles.shapePickerTools}>
                        <View style={shapeToolStyles.mainTools}>
                            <ToolbarRowComponent icons={shapePickerrow1}/>
                            <ToolbarRowComponent icons={shapePickerrow2}/>
                        </View>
                    </View>
                </View>
            )
        }
        else{
            return(
                <View style={shapeToolStyles.toolArea}>
                    <View style={shapeToolStyles.leftSideToolOptions}>
                        <View style={shapeToolStyles.toolOptionsPicker}>
                            <View style={shapeToolStyles.androidBorderFix}/>

                            <TouchableHighlight style={shapeToolStyles.toolPickerButton} onPress={()=>{this.props.shapeToolSelectorPressed()}}>
                                <Constants.DodlesIcon name={this.iconPicker(this.props.shapeData.activeShape)} size={20} color={this.props.shapeData.shapeColor} />
                            </TouchableHighlight>
                        </View>
                        {this.props.shapeData.toolPickerActive? <View style={shapeToolStyles.toolOptions}>
                            <View  style={shapeToolStyles.toolPickerButtons}>
                                <CircleButton
                                    buttonSize={Constants.subtoolButtonSize2Row - 10}
                                    buttonImage={Constants.buttonImages.opacity}
                                    baseColor="white"
                                    action={()=>{this.props.setActiveShapeSubmenu('shapeSizeOpacity')}} />
                            </View>
                            <View  style={shapeToolStyles.toolPickerButtons}>
                                <CircleButton
                                    buttonSize={Constants.subtoolButtonSize2Row - 10}
                                    buttonImage={Constants.buttonImages.hueSaturation}
                                    baseColor="white"
                                    action={()=>{this.props.setActiveShapeSubmenu('shapeHueSaturationPicker');
                                        this.props.setShapeOldColor(this.props.shapeData.shapeColor);}} />
                            </View>
                            <View  style={shapeToolStyles.toolPickerButtons}>
                                <CircleButton
                                    buttonSize={Constants.subtoolButtonSize2Row - 10}
                                    buttonImage={Constants.buttonImages.dotSwatch}
                                    baseColor="white"
                                    action={()=>{this.props.setActiveShapeSubmenu('shapeColorPicker')}} />
                            </View>
                            <View  style={shapeToolStyles.toolPickerButtons}>
                                <CircleButton
                                    buttonSize={Constants.subtoolButtonSize2Row - 10}
                                    iconButton={{iconName:'Config-Options'}}
                                    baseColor="#939393"
                                    borderColor="transparent"
                                    action={()=>{this.props.setActiveShapeSubmenu('ShapeCornerPicker')}}/>
                            </View>
                        </View>:null}
                    </View>

                    <View style={shapeToolStyles.rightSideToolOptions}>
                        {this.rendershapeSubmenu()}
                    </View>

                </View>
            )
        }


    }
}

shapeToolStyles = StyleSheet.create({
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
    shapePickerTools: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
    },
    mainTools: {
        flex: 3,
        paddingTop: 20,
        paddingLeft: 40,
        justifyContent: 'center',
        alignItems: 'flex-start',
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
        zIndex:1,
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
    sizeConfigBlock:{
        width: Constants.deviceWidth * .75 -20,
        margin: 5,
        marginBottom: 10,
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
        shapename: ownProps.shapename,
        shapeData: state.shape,
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
        setActiveShape: (shapename) => dispatch(ActionCreators.setActiveShape(shapename)),
        setShapeOpacity: (shapeopacity) => dispatch(ActionCreators.setShapeOpacity(shapeopacity)),
        setShapeSize: (shapesize) => dispatch(ActionCreators.setShapeSize(shapesize)),
        setShapeRounding: (shaperounding) => dispatch(ActionCreators.setShapeRounding(shaperounding)),
        setShapeCorner: (shapecorner) => dispatch(ActionCreators.setShapeCorner(shapecorner)),
        setActiveShapeSubmenu: (submenu) => dispatch(ActionCreators.setActiveShapeSubmenu(submenu)),
        resetShapeSubmenu: () => dispatch(ActionCreators.resetShapeSubmenu()),
        setShapeColor: (shapecolor) => dispatch(ActionCreators.setShapeColor(shapecolor)),
        setShapeOldColor: (shapeOldColor) => dispatch(ActionCreators.setShapeOldColor(shapeOldColor)),
        setShapeStarDepth: (starDepth) => dispatch(ActionCreators.setShapeStarDepth(starDepth)),
        shapeToolSelectorPressed: () => dispatch(ActionCreators.shapeToolSelectorPressed())
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShapeTools)