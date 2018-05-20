'use strict';

import React, {Component} from 'react';
import {AppRegistry, StyleSheet, Text, View, TouchableHighlight, TouchableOpacity, Animated, PanResponder, Button,} from 'react-native';
import * as Constants from '../../constants'



const sideTopBorders = ((Constants.deviceWidth - 40)/2) + 2;

export default class DraggableView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            pan: new Animated.ValueXY(), // inits to zero
        };
        this.state.panResponder = PanResponder.create({
            onStartShouldSetPanResponder: () => true,
            onPanResponderMove: (e, gestureState) => {
                        let newdx = gestureState.dx,
                            newdy = -gestureState.dy;
                        if(newdy > Constants.toolRowColapsed) {
                            newdy = Constants.toolRowColapsed;
                        } else if (newdy < 0) { //when user trying to slide the view down to a position lower than its minimum height
                            newdy = 0;
                        }
                            Animated.event([null, {
                            dx: this.state.pan.x,
                            dy: this.state.pan.y,
                            }])(e, {dx: newdx, dy: -newdy});

                }// <<--- INVOKING HERE!
            ,
            onPanResponderRelease: () => true,
        });
    }

    //Slide down the view to its original location
    onActiveToolChanged() {
         Animated.spring(
             this.state.pan,         // Auto-multiplexed
             {toValue: {x: 0, y: 0}} // Back to zero
         ).start();
    }

    render() {
        return (
                <Animated.View
                    {...this.state.panResponder.panHandlers}
                    style={[dragViewStyles.dragview,{transform: [{translateY: this.state.pan.y}]}]}>
                    <TouchableOpacity disabled={true} style = {dragViewStyles.dragviewslider}>
                        <Text style={dragViewStyles.elipsisStyle}> .  .  . </Text>
                    </TouchableOpacity>
                    <View style={dragViewStyles.topBorder}>
                        <View style={dragViewStyles.topLeftSide}></View>
                        <View style={dragViewStyles.topMiddle}></View>
                        <View style={dragViewStyles.topRightSide}></View>
                    </View>
                    <View style={dragViewStyles.Toolbar}>
                    {this.props.children}
                    </View>
                </Animated.View>
        );
    }
}


var dragViewStyles = StyleSheet.create({

    dragviewslider: {
        alignSelf: 'center',
        alignItems: 'center',
        justifyContent: 'center',
        width: 60,
        height: 12,
        backgroundColor: '#f3f3f3',
        borderColor: '#b6b6b6',
        borderWidth:1.5,
        borderBottomWidth: 0,
        borderTopLeftRadius: 7,
        borderTopRightRadius: 7,
        margin: 0,
        padding: 0,
        marginBottom: -2,
        zIndex: 1,
        overflow: 'hidden',
    },
    elipsisStyle: {
        color: '#333333',
        backgroundColor: 'transparent',
        margin: 0,
        fontSize: 9,
        fontWeight: 'bold',

    },
    topBorder: {
        flex: 0,
        width: Constants.deviceWidth,
        flexDirection: "row",
        alignSelf: 'center',
        alignItems: 'center',
        justifyContent: 'center',
        height :10,
        padding: 0,
        margin:0,
        zIndex: 3,
    },
    topLeftSide:{
        height : 10,
        width: sideTopBorders,
        borderWidth: 1,
        borderBottomWidth: 0,
        borderRightWidth: 0,
        borderColor: '#b6b6b6',
        borderBottomColor: '#f3f3f3',
        borderTopLeftRadius: 10,
        marginRight: -2,
        zIndex: 0,
        backgroundColor: '#f3f3f3',
    },
    topMiddle:{
        height :10,
        width: 58,
        backgroundColor: '#f3f3f3',
        zIndex: 100,

    },
    topRightSide:{
        height : 10,
        width: sideTopBorders,
        borderWidth: 1.5,
        borderBottomWidth: 0,
        borderLeftWidth: 0,
        borderColor: '#b6b6b6',
        borderBottomColor: '#f3f3f3',
        borderTopRightRadius: 10,
        marginLeft: -2,
        zIndex: 0,
        backgroundColor: '#f3f3f3',

    },
    Toolbar: {
        flex:1,
        marginTop: -2,
        zIndex:100,
    },
    dragview: {
        flex:1,
        position: "absolute",
        backgroundColor:'transparent',
        height: Constants.toolRowHeight,
        bottom: Constants.toolRowOffset,
        left: 0,
        right: 0,
    }
});
AppRegistry.registerComponent('DraggableView', () => DraggableView);