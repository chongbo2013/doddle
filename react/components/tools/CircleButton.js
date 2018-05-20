import React, { Component } from 'react';
import {
    AppRegistry,
    Image,
    TouchableHighlight
} from 'react-native'

import MDIcon from 'react-native-vector-icons/MaterialCommunityIcons';
import FAIcon from 'react-native-vector-icons/FontAwesome';
import * as Constants from '../../constants'

/* Circle Button Component originaly built July 2017 Brian Fuller

    to use the circle button component import CircleButton
    <CircleButton/> will generate a size 40 TouchableHighlight with a transparent background and a grey border
*/
const defaultButtonSize = 40;

const buttonStyle = (color = 'transparent', 
size = defaultButtonSize, baseColor = '#939393', borderColor, active = false, 
enabled = true ,disabled=false, buttonSpacing = 10 ) => {
    
    if(typeof borderColor == 'undefined'){
        borderColor = baseColor;
    }
    let setButtonStyle = {
        backgroundColor: color,
        borderRadius: size / 2,
        borderWidth: 1,
        borderColor: borderColor,
        height:size,
        width:size,
        overflow: 'hidden',
        marginRight: buttonSpacing,
        marginLeft: buttonSpacing,
        flex: 0,
        justifyContent: 'center',
        alignItems: 'center'
    }
    if(active) {
        setButtonStyle.borderWidth = 5;
        setButtonStyle.borderColor = 'white';
        setButtonStyle.height =size + 4;
        setButtonStyle.width = size + 4;
    }
    if(!enabled){
        setButtonStyle.borderWidth = 1;
        setButtonStyle.borderColor = '#cccccc';
        setButtonStyle.backgroundColor = 'transparent';
    };
    if(disabled){
        setButtonStyle.borderColor='rgba(204,204,204,0.3)';
        setButtonStyle.backgroundColor = 'rgba(204,204,204,0.3)';        
    }
    return setButtonStyle;
}
const buttonImageStyle = (size = defaultButtonSize, 
    imageSizeOverride = .9, isColorPickerButton = false, 
    colorPickerButtonColor = 'white', disabled=false) => {
    let imageSize = size - 2;
    let leftSide = 0;
    let bottom = 0
    if(imageSizeOverride == 0){
        imageSize = 0;
    }

    if(disabled){
        return({
            tintColor:'rgb(204,204,204)',
            height:imageSize,
            width:imageSize,
            position: 'absolute',
            bottom: bottom,
            left:leftSide,
            overflow: 'hidden',
            borderRadius: imageSize / 2,
        })
    }

    if(isColorPickerButton) {
        return({
            height:imageSize,
            width:imageSize,
            position: 'absolute',
            bottom: bottom,
            left:leftSide,
            overflow: 'hidden',
            borderRadius: imageSize / 2,
        })
    }


    return({
        height:imageSize,
        width:imageSize,
        position: 'absolute',
        bottom: bottom,
        left:leftSide,
        overflow: 'hidden',
        borderRadius: imageSize / 2,
    })
}
const generateIcon = (name, size = defaultButtonSize, 
    color = "#129EC2", enabled = true, iconSet = 'Dodles', disabled=false) => {
    let iconSize = size * .5;
    if(!enabled){
        color = "#cccccc"
    }
    switch (iconSet)
    {
        case 'FA':
            return(<FAIcon name={name} size={iconSize} color={color} />)
        break;
        case 'MD':
            return(<MDIcon name={name} size={iconSize} color={color}  />)
            break;
        case 'Dodles':
            return(<Constants.DodlesIcon
                name={name}
            size={iconSize} color={color} />)
            break;
        default :
            return(<Constants.DodlesIcon name={name} size={iconSize} color={color}  />);
    }
}
export default class CircleButton extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isPressed: false,
            disabled: props.disabled||false,
        }
    }

    togglePressed() {
        this.state.isPressed = !this.state.isPressed;
    }
    render() {

        return (
            <TouchableHighlight
                disabled={this.props.disabled}
                style={buttonStyle(
                    this.props.colorPicker, 
                    this.props.buttonSize, 
                    this.props.baseColor, 
                    this.props.borderColor, 
                    this.props.active, 
                    this.props.enabled,
                    this.props.disabled)}
                onPress={() => {this.togglePressed(); this.props.action(this.state.isPressed)}}
                underlayColor={'transparent'}
            >
                { this.props.iconButton ? generateIcon(
                    this.props.iconButton.iconName, this.props.buttonSize, 
                    this.props.baseColor, this.props.enabled, 
                    this.props.iconSet,
                    this.props.disabled) : 
                    <Image style={buttonImageStyle(this.props.buttonSize, 
                    this.props.imageSizeOverride, this.props.isColorPickerButton, 
                    this.props.colorPickerButtonColor
                    ,this.props.disabled)} source={this.props.buttonImage}/>}

            </TouchableHighlight>
        );
    }

}

