import React, {Component} from 'react';
import {AppRegistry, Text, View, Button, TouchableHighlight, TouchableOpacity, StyleSheet} from 'react-native';
import FontAwesome from 'react-native-vector-icons/FontAwesome';
import { connect } from 'react-redux'
import Slider from 'react-native-slider'
import {ActionCreators} from '../../actions/index'
import ColorPallete from "../ToolbarComponent/ColorPallete";
import ToolbarRowComponent from "../ToolbarComponent/ToolbarRowComponent";
import * as ShapeLayout from "../ShapeComponent/ShapeLayout";

class ShapeToolbar extends Component
{
    handleSubToolPressed(subtool) {
    }

    constructor(props) {
        super(props);
    }

    renderSizeAndCornerSliders() {
        sizeText = "Size: " + this.props.shapeData.shapeSize.toFixed(2);
        cornerText = "Corner: " + this.props.shapeData.shapeCorner.toFixed(2);
        return (
            <View>
                <Slider
                    style = {styles.Slider}
                    trackStyle = {styles.SliderTrack}
                    thumbTintColor = 'white'
                    thumbStyle = {styles.SliderHandle}
                    minimumTrackTintColor = '#119ec2'
                    maximumTrackTintColor = '#cccccc'
                    value={this.props.shapeData.shapeSize}
                    minimumValue={10}
                    maximumValue={500}
                    onValueChange={(value)=>this.props.setShapeSize(value)}/>
                <Text>
                    {sizeText}
                </Text>
                <Slider
                    style = {styles.Slider}
                    trackStyle = {styles.SliderTrack}
                    thumbTintColor = 'white'
                    thumbStyle = {styles.SliderHandle}
                    minimumTrackTintColor = '#119ec2'
                    maximumTrackTintColor = '#cccccc'
                    value={this.props.shapeData.shapeCorner}
                    minimumValue={1}
                    maximumValue={250}
                    onValueChange={(value)=>this.props.setShapeCorner(value)}/>
                <Text>
                    {cornerText}
                </Text>
            </View>
            );
    }

    renderOpacityPage() {
        return (
            <View>
                <Slider
                    style = {styles.Slider}
                    trackStyle = {styles.SliderTrack}
                    thumbTintColor = 'white'
                    thumbStyle = {styles.SliderHandle}
                    minimumTrackTintColor = '#119ec2'
                    maximumTrackTintColor = '#cccccc'
                    value={this.props.shapeData.shapeOpacity}
                    minimumValue={0}
                    maximumValue={1}
                    onValueChange={(value)=>this.props.setShapeOpacity(value)}/>
            </View>
        );
    }



    render() {
        var Activesubmenu;
        //console.log(this.props.shapeData.activeSubmenu);
        switch (this.props.shapeData.activeSubmenu) {
            case 'Main': {
                Activesubmenu =
                    <View>
                        <ToolbarRowComponent icons={ShapeLayout.subtoolbarrow1} onPressDone={(subtool)=>this.handleSubToolPressed(subtool)}/>
                        <ToolbarRowComponent icons={ShapeLayout.subtoolbarrow2} onPressDone={(subtool)=>this.handleSubToolPressed(subtool)}/>
                    </View>
                break;
            }
            case 'ShapeColorPicker': {
                Activesubmenu =
                    <ColorPallete onGoingBack={()=>{this.props.resetShapeSubmenu()}}/>
                break;
            }
            default: {
                var bottomSubMenu;
                if(this.props.shapeData.activeSubmenu == 'ShapeCornerPicker') {
                    bottomSubMenu = this.renderSizeAndCornerSliders();
                } else if (this.props.shapeData.activeSubmenu == 'ShapeOpacityPicker')  {
                    bottomSubMenu = this.renderOpacityPage();
                }
                Activesubmenu =
                    <View>
                        <ToolbarRowComponent icons={ShapeLayout.settingrow} onPressDone={(subtool)=>this.handleSubToolPressed(subtool)}/>
                        {bottomSubMenu}
                    </View>;
                break;
            }
        }

        return (
            <View>
                {Activesubmenu}
            </View>
        );
    }
}


const styles = StyleSheet.create({
    Slider: {
        width: 350,
        alignSelf: 'center',

    },
    SliderTrack: {
        height: 15,
        borderRadius: 80,
        color: '#119ec2',
    },
    SliderHandle : {
        width: 25,
        height: 25,
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
        setActiveShape: (shapename) => dispatch(ActionCreators.setActiveShape(shapename)),
        setShapeOpacity: (shapeopacity) => dispatch(ActionCreators.setShapeOpacity(shapeopacity)),
        setShapeSize: (shapesize) => dispatch(ActionCreators.setShapeSize(shapesize)),
        setShapeCorner: (shapecorner) => dispatch(ActionCreators.setShapeCorner(shapecorner)),
        setActiveShapeSubmenu: (submenu) => dispatch(ActionCreators.setActiveShapeSubmenu(submenu)),
        resetShapeSubmenu: () => dispatch(ActionCreators.resetShapeSubmenu()),
        setShapeColor: (shapecolor) => dispatch(ActionCreators.setShapeColor(shapecolor))
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(ShapeToolbar)