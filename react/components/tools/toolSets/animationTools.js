import React, {Component} from 'react'
import { TouchableHighlight, View, Text, Image, StyleSheet, Dimensions, Platform, StatusBar, LayoutAnimation, ScrollView } from 'react-native'
import Slider from 'react-native-slider';
import { Switch } from '../../../vendors/react-native-switch';
import { connect } from 'react-redux'
import {ActionCreators} from '../../../actions/index'
import ProgressCircle from 'react-native-progress-circle'
import CircleButton from '../CircleButton'
import HelpAndDemo from './animationHelpAndDemo'
import AnimationPlayPanel from './animationPlayPanel'
import AnimationConfig from './animationConfig'
import * as Constants from '../../../constants'

class AnimationTools extends Component {

    constructor(props) {
        super(props);
    }
    getSize(){
        return Constants.deviceWidth>=768? 
                Constants.deviceWidth*0.08:Constants.deviceWidth*0.10;
    }
    

    renderEffectsBar(){
        const{ effectsContainer, effectsContainerIpad } = animationToolStyles;        
        return(
            <View style={Constants.deviceWidth>=768? effectsContainerIpad:effectsContainer}>
              <View style={animationToolStyles.effectsButton}>
                    <CircleButton
                        buttonSize={ this.getSize() }
                        iconButton={{iconName:'flip-H'}}
                        baseColor="#565656"
                        disabled={true}
                        action={()=>{this.props.setSelectedEffect('Shake');}}/>
                    <Text style={animationToolStyles.effectTextAlpha}>Flip</Text>
                </View>
                <View style={animationToolStyles.effectsButton}>
                    <CircleButton
                        buttonSize={this.getSize() }
                        iconButton={{iconName:'Visibility'}}
                        baseColor="#565656"
                        disabled={true}
                        action={()=>{this.props.setSelectedEffect('Visibility');}}/>
                    <Text style={animationToolStyles.effectTextAlpha}>Visibility</Text>
                </View>
                <View style={animationToolStyles.effectsButton}>
                    <CircleButton
                        buttonSize={this.getSize() }
                        iconButton={{iconName:'Move-On-Timeline'}}
                        baseColor="#565656"
                        action={()=>{
                            this.props.setSelectedEffect('Move');
                            this.props.showHelp('Move');
                            if (this.props.animationData.showDemo.moveEffect) {
                                this.props.setAnimationToolState(Constants.ANIM_TOOL_STATE_MOVE_EFFECT_DEMO);
                            } else {
                                this.props.setAnimationToolState(Constants.ANIM_TOOL_STATE_MOVE_EFFECT_INPUT);
                            }
                        }}
                    />
                    <Text style={animationToolStyles.effectText}>move</Text>
                </View>
                <View style={animationToolStyles.effectsButton}>
                    <CircleButton
                    buttonSize={this.getSize() }
                    iconButton={{iconName:'Spin'}}
                        baseColor="#565656"
                        action={()=>{
                            this.props.setSelectedEffect('Spin');
                            this.props.showHelp('Spin');
                            if (this.props.animationData.showDemo.rotateEffect) {
                                this.props.setAnimationToolState(Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_DEMO);
                            } else {
                                this.props.setAnimationToolState(Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_INPUT);
                            }
                        }}
                    />
                    <Text style={animationToolStyles.effectText}>rotate</Text>
                </View>
                <View style={animationToolStyles.effectsButton}>
                    <CircleButton
                    buttonSize={this.getSize() }
                    iconButton={{iconName:'Size'}}
                        baseColor="#565656"
                        disabled={true}
                        action={()=>{
                            this.props.setSelectedEffect('Scale');
                            this.props.showHelp('Scale');
                            if (this.props.animationData.showDemo.scaleEffect) {
                                this.props.setAnimationToolState(Constants.ANIM_TOOL_STATE_SCALE_EFFECT_DEMO);
                            } else {
                                this.props.setAnimationToolState(Constants.ANIM_TOOL_STATE_SCALE_EFFECT_INPUT);
                            }
                        }}
                    />
                    <Text style={animationToolStyles.effectTextAlpha}>Scale</Text>
                </View>
                <View style={animationToolStyles.effectsButton}>
                    <CircleButton
                    buttonSize={this.getSize() }
                    iconButton={{iconName:'CopyEffect'}}
                        baseColor="#565656"
                        disabled={true}
                        action={()=>{this.props.setSelectedEffect('Copy');}}/>
                    <Text style={animationToolStyles.effectTextAlpha}>Copy</Text>
                </View>
            </View>
        )
    }


    renderActiveEffectDisplay(){
        let effectIcon = '';

        if(this.props.animationData.selectedEffect == null){
            return null;
        } else {
            let effectIcon = this.props.animationData.selectedEffect;
            let effectEditState = null;
            if(this.props.animationData.selectedEffect == null) {
                effectIcon = 'Master';
            }
            if(this.props.animationData.selectedEffect == 'Move'){
                effectIcon = "Move-On-Timeline"
                effectEditState = Constants.ANIM_TOOL_STATE_MOVE_EFFECT_CONFIGURE;
            }
            if(this.props.animationData.selectedEffect == 'Spin'){
                effectIcon = 'Spin';
                effectEditState = Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_CONFIGURE;
            }
            if(this.props.animationData.selectedEffect == 'Scale'){
                effectIcon = 'Size';
                effectEditState = Constants.ANIM_TOOL_STATE_SCALE_EFFECT_CONFIGURE;
            }

            return(
                <View style={animationToolStyles.effectDisplayContainer}>
                    <View style={animationToolStyles.effectDisplayButton}>
                        <CircleButton
                            buttonSize={Constants.toolRowColapsed * .7}
                            iconButton={{iconName:effectIcon}}
                            baseColor="#119EC2"
                            action={()=>{
                                if (this.props.animationData.selectedEffect == null) {
                                    // TODO: @Brian: should this switch to the EFFECT_SELECT state? or will this button
                                    // just not be visible when there is no effect selected? - Clint 2017.10.30
                                } else {
                                    this.props.SetAnimationConfig(this.props.animationData.selectedEffect)
                                    if (effectEditState != null) {
                                        this.props.setAnimationToolState(effectEditState)
                                    }
                                }
                            }}
                        />
                        <Text style={animationToolStyles.effectDisplayText}>{this.props.animationData.selectedEffect}</Text>
                    </View>
                </View>
            );
        }
    }


    renderAnimationTool(){
        let prevEffect = null;
        let nextEffect = null;
        let prevButtonColor = "#CCCCCC";
        let nextButtonColor = "#CCCCCC"
        let effectCount = this.props.animationData.nextPrevious.length;
        if(effectCount > 0){
            if(this.props.animationData.selectedTimelineEffect != null){
                let currentSelected = this.props.animationData.nextPrevious.indexOf(this.props.animationData.selectedTimelineEffect);
                if(currentSelected > 0){
                    prevEffect = this.props.animationData.nextPrevious[currentSelected - 1];
                    prevButtonColor = "black";
                }
                if(currentSelected + 1 < effectCount) {
                    nextEffect = this.props.animationData.nextPrevious[currentSelected + 1];
                    nextButtonColor = "black";
                }

            } else {
                nextEffect = this.props.animationData.nextPrevious[0];
                nextButtonColor = "black";
            }
        }

        return(
            <View>
                {this.props.animationData.toolState == 
                    Constants.ANIM_TOOL_STATE_EFFECT_SELECT ? this.renderEffectsBar() : null}
                <AnimationPlayPanel/>
                <View style={animationToolStyles.timelineBottom}>
                    <TouchableHighlight style={animationToolStyles.prevNextContainer}
                                        onPress={() => {this.props.setSelectedTimelineEffect(prevEffect)}}
                                        underlayColor={'transparent'}>
                        <View>
                            <Constants.DodlesIcon style={{marginBottom: 10}} name={'rewind'} size={20} color={prevButtonColor} />
                            <Text style={{color: prevButtonColor}}>Prev</Text>
                        </View>
                    </TouchableHighlight>
                    <TouchableHighlight style={animationToolStyles.prevNextContainer}
                                        onPress={() => {this.props.setSelectedTimelineEffect(nextEffect)}}
                                        underlayColor={'transparent'}>
                        <View>
                            <Constants.DodlesIcon style={{transform: [{ rotate: '180deg'}], marginBottom: 10}} name={'rewind'} size={20} color={nextButtonColor} />
                            <Text style={{color: nextButtonColor}}>Next</Text>
                        </View>
                     </TouchableHighlight>
                    <View style={animationToolStyles.rightSpacer}>
                        {this.renderActiveEffectDisplay()}
                    </View>
                </View>
            </View>
        )
    }

    render() {
        // console.log('#### In render state:');
        // console.log(this.props.animationData);
        if (this.props.animationData.activeConfig !== null) {
            return (
                <View>
                    <AnimationConfig/>
                </View>)
        } else {
            return(
                <View style={animationToolStyles.animationToolsBlock}>
                    <HelpAndDemo/>
                    {this.renderAnimationTool()}
                </View>
            )
        }
    }
}

animationToolStyles = StyleSheet.create({
    animationToolsBlock:{
        flexDirection: 'column',
        width: Constants.effectWidth,
        height: Constants.toolDisplayArea,
    },
    timelineTop:{
        flex: 0,
        flexDirection: 'row',
        backgroundColor: '#9c9c9c',
        width: Constants.deviceWidth,
        height: Constants.toolDisplayArea * .4,
    },

    prevNextContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    rightSpacer: {
        flex: 3
    },
    effectsContainer: {
        width: Constants.effectWidth,
        height: Constants.dodleEngineHeight,
        borderWidth: 1,
        borderColor: "#939393",
        backgroundColor: '#f3f3f3',
        flexDirection: 'column',
        position: 'absolute',
        top: Constants.dodleEngineHeight * -1,
        justifyContent: 'space-between',
        left: 0,
        
    },
 
    effectsContainerIpad:{
        width: Constants.effectWidth,
        height: Constants.dodleEngineHeight,
        borderWidth: 1,
        borderColor: "#939393",
        backgroundColor: '#f3f3f3',
        flexDirection: 'column',
        position: 'absolute',
        top: Constants.dodleEngineHeight * -1,
        left: 0,
        justifyContent: 'space-between',
        margin: 2
    },
    effectsButton: {
        alignSelf: 'center',
        justifyContent: 'space-between',
        margin: Constants.deviceWidth>=768? 5:3
        
    },
    effectText: {
        alignSelf: 'center',
        color: "#565656",
        fontSize: Constants.deviceWidth>=768? 16:10,
        fontFamily: 'OpenSans-Bold',
        margin:2
    },
    buttonSizePhone:{
       width: Constants.deviceWidth * .10
    },
    buttonSizeIpad:{
        width: Constants.deviceWidth * .08
    },
    effectTextAlpha: {
        alignSelf: 'center',
        color: "rgba(33.7,33.7,33.7,0.3)",
        fontSize: Constants.deviceWidth>=768? 16:10,        
        fontFamily: 'OpenSans-Bold',        
    },
    effectDisplayContainer: {
        alignSelf: 'flex-end',
        //height: Constants.toolDisplayArea * .5,
        position: 'absolute',
        top:-1,
        right: Constants.deviceWidth * .05,
        width: Constants.toolRowColapsed * .7 + 20,
        backgroundColor: 'white',
        borderWidth: 1,
        borderColor: '#119EC2',
        borderTopWidth: 0,
        borderLeftWidth: 1,
        borderRightWidth: 1,
        borderBottomWidth: 1,
        borderTopColor: 'transparent',
        borderLeftColor: '#119EC2',
        borderRightColor: '#119EC2',
        borderBottomColor: '#119EC2',
        borderBottomLeftRadius: Constants.deviceWidth * .16 / 2,
        borderBottomRightRadius: Constants.deviceWidth * .16 /2,
        justifyContent: 'center',
        alignItems: 'center',
    },
    effectDisplayButton: {
        alignSelf: 'center',
    },
    effectDisplayText: {
        color: '#119EC2',
        alignSelf: 'center',
    },
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

    leftSideIcon: {
        flex: 1,
    },
    labelContainer: {
        flex: 4,
        flexDirection: 'row',
        justifyContent: 'flex-start',
        alignItems: 'flex-start',
    },
    switchLabel: {
        fontSize: 18,
        color: '#565656',
        alignSelf: 'flex-start'
    },
    switchLabelDisabled: {
        fontSize: 18,
        color: '#B7B7B7',
    },
    Slider: {
        width: 200,
        height: 30,
    },
    rightSideSwitch: {
        flex:2,
    },
    configBottom: {
        flex: 1,
        flexDirection: 'row',
    },
    leftSpacer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    midButtonContainer: {
        flex: 4,
        justifyContent: 'center',
        alignItems: 'center',
    },
    bigMidButton: {
        flex: 0,
        width: Constants.deviceWidth * .8 > 400? 200: Constants.deviceWidth * .4,
        height:30,
        backgroundColor: '#119EC2',
        borderRadius: 15,
        justifyContent: 'center',
        alignItems: 'center',
    },
    bigMidButtonText: {
        fontSize: 15,
        fontWeight:'bold',
        color: 'white',
    },
    rightSpacer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    timelineBottom: {
        flex: 0,
        flexDirection: 'row',
        width: Constants.deviceWidth,
        height: Constants.toolDisplayArea * .5,
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
        setAnimationTotalLength: (animationLength) => dispatch(ActionCreators.setAnimationTotalLength(animationLength)),
        setAnimationToolState: (toolState) => dispatch(ActionCreators.setAnimationToolState(toolState)),
        setSelectedTimelineEffect: (effectID) => dispatch(ActionCreators.setSelectedTimelineEffect(effectID)),
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AnimationTools)