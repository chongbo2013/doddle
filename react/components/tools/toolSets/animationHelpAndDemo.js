import React, {Component} from 'react'
import { TouchableHighlight, View, Text, StyleSheet, Dimensions, Platform, StatusBar, LayoutAnimation, ScrollView } from 'react-native'
import { connect } from 'react-redux'
import {ActionCreators} from '../../../actions/index'
import * as Constants from '../../../constants'

class HelpAndDemo extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        switch (this.props.animationData.selectedEffect) {
            case 'Move': {
                if (this.props.animationData.toolState == Constants.ANIM_TOOL_STATE_MOVE_EFFECT_DEMO || this.props.animationData.toolState == Constants.ANIM_TOOL_STATE_MOVE_EFFECT_INPUT) {
                    return (
                        <View style={helpDemoStyles.helpPanel}>
                            <View style={helpDemoStyles.helpTextBlock}>
                                <Text style={helpDemoStyles.helpText}>Drag object to</Text>
                                <Text style={helpDemoStyles.helpText}>perform move.</Text>
                            </View>
                            {this.props.animationData.toolState == Constants.ANIM_TOOL_STATE_MOVE_EFFECT_DEMO ?
                                <TouchableHighlight
                                        style={helpDemoStyles.helpPanelActionButton}
                                        onPress={() => {
                                            this.props.setAnimationToolState(Constants.ANIM_TOOL_STATE_MOVE_EFFECT_INPUT)
                                        }}>
                                    <Text style={helpDemoStyles.helpPanelActionButtonText}>Continue</Text>
                                </TouchableHighlight> : null}

                        </View>
                    )
                } else {
                    return null;
                }
                break;
            }
            case 'Spin': {
                if (this.props.animationData.toolState == Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_INPUT || this.props.animationData.toolState == Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_DEMO) {
                    return (
                        <View style={helpDemoStyles.helpPanel}>
                            <View style={helpDemoStyles.helpTextBlock}>
                                <Text style={helpDemoStyles.helpText}>Use Your Finger</Text>
                                <Text style={helpDemoStyles.helpText}>to rotate the object.</Text>
                            </View>
                            {this.props.animationData.toolState == Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_DEMO ?
                                <TouchableHighlight
                                        onPress={() => {
                                            this.props.setAnimationToolState(Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_INPUT);
                                        }}
                                        style={helpDemoStyles.helpPanelActionButton}>
                                    <Text style={helpDemoStyles.helpPanelActionButtonText}>Continue</Text>
                                </TouchableHighlight> : null
                            }
                        </View>
                    )
                } else if (this.props.animationData.toolState == Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_PIVOT) {
                    return (
                        <View style={helpDemoStyles.helpPanel}>
                            <View style={helpDemoStyles.helpTextBlock}>
                                <Text style={helpDemoStyles.helpText}>Use Your Finger </Text>
                                <Text style={helpDemoStyles.helpText}>to move the pivot point</Text>
                            </View>
                        </View>
                    );
                } else {
                    return null;
                }
                break;
            }
            case 'Scale': {
                if (this.props.animationData.toolState == Constants.ANIM_TOOL_STATE_SCALE_EFFECT_DEMO || this.props.animationData.toolState == Constants.ANIM_TOOL_STATE_SCALE_EFFECT_INPUT) {
                    return (
                        <View style={helpDemoStyles.helpPanel}>
                            <View style={helpDemoStyles.helpTextBlock}>
                                <Text style={helpDemoStyles.helpText}>Use Your Finger</Text>
                                <Text style={helpDemoStyles.helpText}>to scale the object.</Text>
                            </View>
                            {this.props.animationData.toolState == Constants.ANIM_TOOL_STATE_SCALE_EFFECT_DEMO ?
                            <TouchableHighlight
                                    style={helpDemoStyles.helpPanelActionButton}
                                    onPress={() => {
                                        this.props.setAnimationToolState(Constants.ANIM_TOOL_STATE_SCALE_EFFECT_INPUT)
                                    }}>
                                <Text style={helpDemoStyles.helpPanelActionButtonText}>Continue</Text>
                            </TouchableHighlight> : null}
                        </View>
                    );
                } else {
                    return null;
                }
                break;
            }
            default: {
                return null;
            }
        }
    }
}

helpDemoStyles = StyleSheet.create({

    helpPanel: {
        height: Constants.toolAreaHeight,
        width: Constants.deviceWidth,
        zIndex: 120,
        backgroundColor: '#EDECED',
        flexDirection: 'column',
        overflow: 'hidden',
        justifyContent: 'center',
        alignItems: 'center',
    },
    helpTextBlock: {
        flex: 3,
        justifyContent: 'center',
        alignItems: 'center',
    },
    helpText: {
        fontSize: 18,
        fontWeight:'bold',
        color: '#565656',
    },
    helpPanelActionButton: {
        flex: 1,
        width: Constants.deviceWidth,
        backgroundColor: '#119EC2',
        paddingVertical: 15,
        justifyContent: 'center',
        alignItems: 'center',
    },
    helpPanelActionButtonText: {
        fontSize: 20,
        fontWeight:'bold',
        color: 'white',
    },
});

function mapStateToProps (state, ownProps) {
    return {
        animationData: state.animation
    }
}

function mapDispatchToProps (dispatch) {
    return {
        setSelectedEffect: (effectName) => dispatch(ActionCreators.setSelectedEffect(effectName)),
        animationStart: () => dispatch(ActionCreators.animationStart()),
        animationStop: () => dispatch(ActionCreators.animationStop()),
        SetAnimationConfig: (activeConfig) => dispatch(ActionCreators.SetAnimationConfig(activeConfig)),
        UpdateMoveConfig: (configObj) => dispatch(ActionCreators.UpdateMoveConfig(configObj)),
        sendMoveConfigUpdate: (configObj) => dispatch(ActionCreators.sendMoveConfigUpdate()),
        showHelp: (helpContext) => dispatch(ActionCreators.showHelp(helpContext)),
        hideHelp: () => dispatch(ActionCreators.hideHelp()),
        UpdateRotateConfig: (configObj) => dispatch(ActionCreators.UpdateRotateConfig(configObj)),
        sendRotateConfigUpdate: (configObj) => dispatch(ActionCreators.sendRotateConfigUpdate()),
        setAnimationTotalLength: (animationLength) => dispatch(ActionCreators.setAnimationTotalLength(animationLength)),
        setAnimationToolState: (toolState) => dispatch(ActionCreators.setAnimationToolState(toolState)),


    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(HelpAndDemo)