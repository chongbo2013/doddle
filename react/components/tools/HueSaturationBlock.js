import React, {Component} from 'react';
import {StyleSheet, TouchableHighlight, View} from 'react-native';
import {connect} from 'react-redux'
import {ActionCreators} from '../../actions/index'
import * as Constants from '../../constants'
import {fromHsv, toHsv, TriangleColorPicker} from '../../vendors/react-native-color-picker'
import CircleButton from './CircleButton';

class HueSaturationBlock extends Component
{
    constructor(props)
    {
        super(props);
        this.state = {
            color: toHsv(this.checkColor()),
        };
        this.onColorChange = this.onColorChange.bind(this);
    }

    onColorChange(color)
    {
        this.setState({ color : color });
        if(this.props.toolData.activetool === Constants.TOOL_DRAW)
        {
            this.props.setBrushColor(fromHsv(color));
        }
        else if(this.props.toolData.activetool === Constants.TOOL_SHAPE)
        {
            this.props.setShapeColor(fromHsv(color));
        }

    }

    checkColor() {
        switch(this.props.toolData.activetool) {
            case Constants.TOOL_DRAW: {
                return this.props.brushData.brushColor
            }
            case Constants.TOOL_SHAPE: {
                return this.props.shapeData.shapeColor;
            }
        }
    }

    checkOldColor() {
        switch(this.props.toolData.activetool) {
            case Constants.TOOL_DRAW: {
                return this.props.brushData.brushOldColor
            }
            case Constants.TOOL_SHAPE: {
                return this.props.shapeData.shapeOldColor;
            }
        }
    }

    setHueSaturationInPicker(color){
        this.onColorChange(this.checkOldColor());
        this.setState({
            color : color
        })
    }

    render()
    {
        return(
            <View style={styles.container}>
                <View style={styles.colorContainer}>
                    <CircleButton
                        colorPicker={this.checkColor()}
                        active={true}
                        action={()=>{this.setHueSaturationInPicker(this.checkColor())}}/>
                    <TouchableHighlight
                        style={styles.oldColorTouch}
                        onPress={() => {this.setHueSaturationInPicker(this.checkOldColor())}}>
                       <View style={[styles.oldcolor, {backgroundColor: this.checkOldColor()}]}/>
                    </TouchableHighlight>
                </View>
                <TriangleColorPicker
                    style={styles.trianglePickerContainer}
                    onColorChange={this.onColorChange}
                    color={this.state.color}
                />
            </View>
        );
    }
}

const CIRCLE_SIZE = 40;

const styles = StyleSheet.create({
   container: {
       flex: 1,
       flexDirection: 'row',
       alignItems: 'center',
   },
   colorContainer: {
       marginTop: 10
   },
   oldColorTouch: {
       width: CIRCLE_SIZE,
       height: CIRCLE_SIZE,
       borderRadius: CIRCLE_SIZE/2,
       marginLeft: 30,
       marginTop: 10
   },
   oldcolor: {
       width: CIRCLE_SIZE,
       height: CIRCLE_SIZE,
       borderRadius: CIRCLE_SIZE/2,
   },

   trianglePickerContainer: {
       flex: 1,
       height: 175,
       width: 175,
       marginBottom: 15,
   }
});

function mapStateToProps (state, ownProps) {
    return {
        brushData: state.brush,
        toolData: state.tool,
        shapeData: state.shape,
    }
}

function mapDispatchToProps (dispatch) {
    return {
        setBrushColor: (brushcolor) => dispatch(ActionCreators.setBrushColor(brushcolor)),
        setShapeColor: (shapeColor) => dispatch(ActionCreators.setShapeColor(shapeColor))
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(HueSaturationBlock)



