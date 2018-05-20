import React, {Component} from 'react'
import { TouchableHighlight, View, Text, StyleSheet, Dimensions, Platform, StatusBar, LayoutAnimation, PanResponder, ScrollView } from 'react-native'
import { connect } from 'react-redux'
import ProgressCircle from 'react-native-progress-circle'
import Slider from 'react-native-slider';
import {ActionCreators} from '../../../actions/index'
import CircleButton from '../CircleButton';
import * as Constants from '../../../constants'

class AnimationPlayPanel extends Component {

    constructor(props) {
        super(props);
        this.state = {sliding : false};
    }

    componentWillMount(){
        let timelineObjects = {};
        let nextPreviousSelector =[];
        let animationLength = 0;
        if (this.props.animationData.animations.length !== 0) {
            this.props.animationData.animations[0].rootBlock.effectTimings.map(function (effect, key) {
                timelineObjects[effect.effect.effectID] = {start: effect.delay, LENGTH:effect.effect.parameters.LENGTH };
                nextPreviousSelector.push(effect.effect.effectID)
                if (effect.delay + effect.effect.parameters.LENGTH > animationLength) {
                    animationLength = effect.delay + effect.effect.parameters.LENGTH;
                }
            });
        }
        this.props.initializeAnimationDisplay(animationLength,timelineObjects,nextPreviousSelector);
    }

    renderTimeBar() {
        let timeBar = [];
        let timeEnd = parseFloat(this.props.animationData.animationTotalLength) > 10 ? parseFloat(this.props.animationData.animationTotalLength): 10;
        for (var i = 0; i <= timeEnd; i++) {
            timeBar.push(
                <View style={playPanelStyles.timeBarElement} key={i}>
                    <Text style={playPanelStyles.timeBarElementNumber}> {i} </Text>
                </View>
            );
        }
        return timeBar;
    }

    getPercentLeft(animationTime){
        return ((parseFloat(animationTime)) / (parseFloat(this.props.animationData.animationTotalLength)) * 100);
    }
    renderPlayHead (){
        return (
            <View style={{
                flexDirection: 'row',
                height: Constants.toolDisplayArea * .4,
                width: 42,
                backgroundColor:'transparent',
                position: 'absolute',
                left: (parseFloat(this.props.animationData.animationPlayTime) * ((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10 ))-20,
                top:0,
                zIndex: 200,
            }}>
                <View style={{
                    height: Constants.toolDisplayArea * .2,
                    width:0,
                    borderRightWidth: 20,
                    borderRightColor: 'rgba(255,255,0,0.6)',
                    borderBottomWidth: Constants.toolDisplayArea * .1,
                    borderBottomColor: 'transparent',
                }}/>
                <View style={{
                    height: Constants.toolDisplayArea * .4,
                    width: 2,
                }}/>
                <View style={{
                    height: Constants.toolDisplayArea * .2,
                    width:0,
                    borderLeftWidth: 20,
                    borderLeftColor: 'rgba(255,255,0,0.6)',
                    borderBottomWidth: Constants.toolDisplayArea * .1,
                    borderBottomColor: 'transparent',
                }}/>
            </View>
        )
    }
    renderLeftSlide (effectStart) {
        let leftLocation = ((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10) * effectStart;
        return (
            <View style={{
                width: 20,
                height: Constants.toolDisplayArea,
                position:'absolute',
                left: leftLocation - 20,
                top: 0,
                borderRightWidth: 2,
                borderRightColor: '#3c3c3c',
                zIndex: 200,}}>
                <View style={playPanelStyles.effectLeftSlideAngle}>
                </View>
                <View style={playPanelStyles.effectLeftSlideHandel}>
                    <Text style={playPanelStyles.handelButtons}>...</Text>
                </View>
            </View>
        )
    }
    renderRightSlide (effectStart, effectLenght) {
        let rightLocation = (((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10) * effectLenght) + (((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10) * effectStart)
        return (
            <View style={{
                flexDirection: 'column',
                width: 20,
                height: Constants.toolDisplayArea,
                position:'absolute',
                left: rightLocation,
                top: 0,
                borderLeftWidth: 2,
                borderLeftColor: '#3c3c3c',
                zIndex: 200,}}>
                <View style={playPanelStyles.effectRightSlideAngle}>
                </View>
                <View style={playPanelStyles.effectRightSlideHandel}>
                    <View style={playPanelStyles.handleSpacer}></View>
                    <View><Text style={playPanelStyles.handelButtons}>...</Text></View>
                </View>
            </View>
        )
    }

    renderTimeLine() {
        let that = this;
        let timelineView = [];
        if (this.props.animationData.animations.length == 0) {
            return null;
        } else {
            return this.props.animationData.animations[0].rootBlock.effectTimings.map(function (effect, key) {
                return (
                    <View key={key} style={playPanelStyles.timeLineEffectRow}>
                        <TouchableHighlight
                            underlayColor={'transparent'}
                            style={{
                                position: 'absolute',
                                top: 5,
                                left: ((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10) * effect.delay,
                                width: ((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10) * effect.effect.parameters.LENGTH,
                                height: 7,
                                backgroundColor: 'transparent',
                                justifyContent: 'center',
                                alignItems: 'center',
                            }}
                            onPress={() => {that.props.setSelectedTimelineEffect(effect.effect.effectID)}}>
                            <View style={{
                                width: ((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10) * effect.effect.parameters.LENGTH,
                                height: 3,
                                backgroundColor: that.props.animationData.selectedTimelineEffect == effect.effect.effectID ? '#119EC2':'white',}}>

                            </View>
                        </TouchableHighlight>
                    </View>
                );
            });
        }
    }
    render() {
        let timeBlockHeight = 12;
        if (this.props.animationData.animations.length > 0) {
            timeBlockHeight = this.props.animationData.animations[0].rootBlock.effectTimings.length * 12;
        }
        return (
            <View style={playPanelStyles.timelineTop}>
                <View style={playPanelStyles.playButton}>
                    {this.props.animationData.animationRunning ?
                        <ProgressCircle
                            percent={this.getPercentLeft(this.props.animationData.animationPlayTime)}
                            radius={25}
                            borderWidth={1}
                            color="white"
                            shadowColor="#999"
                            bgColor="transparent"
                            children={<CircleButton
                                buttonSize={40}
                                iconButton={{iconName: 'pause'}}
                                iconSet={"MD"}
                                baseColor="white"
                                borderColor="#119EC2"
                                colorPicker="#119EC2"
                                action={() => {
                                    this.props.animationStop();
                                }}/>}/>:
                        <CircleButton
                            buttonSize={50}
                            iconButton={{iconName: 'Play'}}
                            baseColor="white"
                            borderColor="transparent"
                            action={() => {
                                this.props.animationStart();
                            }}/>
                    }
                </View>
                <View style={playPanelStyles.timeLineContainer}>
                    <ScrollView scrollEnabled={!this.state.sliding}
                                horizontal={true}
                                maximumZoomScale={1}
                                minimumZoomScale={-3}>
                        <View style={{
                            flexDirection: 'column',
                            width: ((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10) * (this.props.animationData.animationTotalLength > 10 ? this.props.animationData.animationTotalLength : 10)
                        }}>
                            <View style={{
                                flex: 0,
                                flexDirection: 'row',
                                height: 15,
                                width: ((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10) * this.props.animationData.animationTotalLength,
                            }}>
                                {this.renderTimeBar()}
                            </View>
                            <ScrollView scrollEnabled={!this.state.sliding}>
                                <View style={{
                                    flex: 0,
                                    flexDirection: 'column',
                                    width: ((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10) * this.props.animationData.animationTotalLength,
                                    height: timeBlockHeight
                                }}>

                                    {this.renderTimeLine()}
                                </View>

                            </ScrollView>
                            {this.renderPlayHead()}
                            {this.props.animationData.selectedTimelineEffect != null ?this.renderLeftSlide(this.props.animationData.timeLines[this.props.animationData.selectedTimelineEffect].start): null }
                            {this.props.animationData.selectedTimelineEffect != null ?this.renderRightSlide(this.props.animationData.timeLines[this.props.animationData.selectedTimelineEffect].start, this.props.animationData.timeLines[this.props.animationData.selectedTimelineEffect].LENGTH): null}
                            <View style={{
                                width:((Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10) * this.props.animationData.animationTotalLength ,
                                alignItems: 'stretch',
                                padding: 0,
                                position: 'absolute',
                                left: 0,
                                top: -15,
                                overflow: 'hidden',
                                backgroundColor:'transparent',
                                height: Constants.toolDisplayArea * .4 + 15,
                                zIndex: 400}}>
                                <Slider
                                    minimumValue={0}
                                    maximumValue={this.props.animationData.animationTotalLength}
                                    value={parseFloat(this.props.animationData.animationPlayTime)}
                                    onSlidingStart = {() => {this.setState({sliding: true});}}
                                    onValueChange={(value) => {
                                            this.setState({sliding: true});
                                            this.props.updatePlayTime(value);
                                        }
                                    }
                                    onSlidingComplete={(value) => {
                                        this.setState({sliding: false});
                                        this.props.updatePlayheadPosition(value);
                                    }}
                                    minimumTrackTintColor = 'transparent'
                                    maximumTrackTintColor = 'red'
                                    thumbTintColor = 'transparent'
                                    style ={{
                                        padding: 0,
                                        margin: 0,
                                        height: Constants.toolDisplayArea * .4,
                                    }}
                                    trackStyle = {{
                                        height: Constants.toolDisplayArea * .4,
                                        position: 'absolute',
                                        top: 0
                                    }}
                                    thumbStyle = {{
                                        height: Constants.toolDisplayArea * .4,
                                        width: 42,
                                        borderRadius: 0,
                                        backgroundColor:'transparent',
                                    }}
                                />
                            </View>
                        </View>
                    </ScrollView>
                </View>
            </View>
        )
    }
}
playPanelStyles = StyleSheet.create({
    animationToolsBlock: {
        flex: 0,
        flexDirection: 'column',
        width: Constants.deviceWidth,
        height: Constants.toolDisplayArea,
    },
    timelineTop: {
        flex: 0,
        flexDirection: 'row',
        backgroundColor: '#9c9c9c',
        width: Constants.deviceWidth,
        height: Constants.deviceWidth>=768? Constants.toolDisplayArea* 0.5: Constants.toolDisplayArea * .4,
    },
    playButton: {
        flex: 0,
        height: Constants.deviceWidth>=768? Constants.toolDisplayArea* 0.5: Constants.toolDisplayArea * .4,
        width: Constants.deviceWidth>=768? Constants.deviceWidth*0.20:Constants.effectWidth,
        backgroundColor: '#119EC2',
        justifyContent: 'center',
        alignItems: 'center',
    },
    timeLineContainer: {
        flex: 0,
        height: Constants.toolDisplayArea * .4,
        width: Constants.deviceWidth - Constants.toolDisplayArea * .4,
        flexDirection: 'column',
    },
    timeLineEffectRow: {
        flex: 0,
        flexDirection: 'row',
        width: Constants.deviceWidth - Constants.toolDisplayArea * .4,
        height: 12,
        justifyContent: 'center',
        position: 'relative',
    },
    timeBar: {
        flex: 0,
        flexDirection: 'row',
        height: 15,
    },
    timeContent: {
        flex: 0,
    },
    timeBarElement: {
        flex: 0,
        width: (Constants.deviceWidth - Constants.toolDisplayArea * .4) / 10,
        backgroundColor: "#666666",
        borderLeftWidth: 1,
        borderLeftColor: 'white',
    },
    timeBarElementNumber: {
        color: 'white'
    },

    effectLeftSlideAngle: {
        marginTop: Constants.toolDisplayArea * .2,
        height: 0,
        width:0,
        borderRightWidth: 20,
        borderRightColor: 'rgba(17,158,194,0.6)',
        borderTopWidth: Constants.toolDisplayArea * .1,
        borderTopColor: 'transparent',
    },
    effectLeftSlideHandel: {
        flex: 0,
        alignSelf: 'flex-end',
        backgroundColor: 'rgba(17,158,194,0.6)',
        height: Constants.toolDisplayArea * .1,
        width: 20,
        justifyContent: 'center',
        alignItems: 'center',
    },
    effectRightSlideAngle: {
        marginTop: Constants.toolDisplayArea * .2,
        height: 0,
        width:0,
        borderLeftWidth: 20,
        borderLeftColor: 'rgba(17,158,194,0.6)',
        borderTopWidth: Constants.toolDisplayArea * .1,
        borderTopColor: 'transparent',
    },
    effectRightSlideHandel: {
        flex: 0,
        flexDirection: 'row',
        alignSelf: 'flex-end',
        backgroundColor: 'rgba(17,158,194,0.6)',
        height: Constants.toolDisplayArea * .1,
        width: 20,
    },
    handleSpacer: {
        flex:0,
        height: Constants.toolDisplayArea * .1,
        width: 10,
    },
    handelButtons: {
        transform: [{ rotate: '90deg'}],
        color: 'rgba(255,255,255,0.6)',
        fontSize: 20,
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
        updatePlayTime: (playTime) => dispatch(ActionCreators.updatePlayTime(playTime)),
        updatePlayheadPosition: (value) => dispatch(ActionCreators.updatePlayheadPosition(value)),
        //rotate
        UpdateRotateConfig: (configObj) => dispatch(ActionCreators.UpdateRotateConfig(configObj)),
        sendRotateConfigUpdate: (configObj) => dispatch(ActionCreators.sendRotateConfigUpdate()),
        initializeAnimationDisplay: (animationLength,timeLines,nextPrevious) => dispatch(ActionCreators.initializeAnimationDisplay(animationLength,timeLines,nextPrevious))
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AnimationPlayPanel)