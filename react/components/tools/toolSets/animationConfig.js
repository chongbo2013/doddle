import React, {Component} from 'react'
import { TouchableHighlight, View, Text, StyleSheet, Dimensions, Platform, StatusBar, LayoutAnimation, ScrollView, Image, TouchableOpacity } from 'react-native'
import { connect } from 'react-redux'
import {ActionCreators} from '../../../actions/index'
import Slider from 'react-native-slider';
import { Switch } from '../../../vendors/react-native-switch';
import CircleButton from '../CircleButton'
import AnimationPlayPanel from './animationPlayPanel'
import * as Constants from '../../../constants'

class AnimationConfig extends Component {

    constructor(props) {
        super(props);
        this.state = { rotateFlag: false}
    }

    // Move Configuration

    updateMoveIteration(value) {
        cleanValue = Math.floor(value);
        if(cleanValue !== this.props.animationData.moveConfig.iterations.value) {
            let moveConfiguration = this.props.animationData.moveConfig;
            moveConfiguration.iterations.value = cleanValue;
            this.props.UpdateMoveConfig(moveConfiguration);
        }

    }
    updateParallelToPath(value){
        let moveConfiguration = this.props.animationData.moveConfig;
        moveConfiguration.parallelToPath.value = value;
        if(value) {
            moveConfiguration.finishUpright.disabled = true;
            moveConfiguration.finishUpright.value = false;

        }else {
            moveConfiguration.finishUpright.disabled = false;
            moveConfiguration.finishUpright.value = false;
        }
        this.props.UpdateMoveConfig(moveConfiguration);
    }
    updateFinishUpright(value){
        let moveConfiguration = this.props.animationData.moveConfig;
        moveConfiguration.finishUpright.value = value;
        if(value) {
            moveConfiguration.parallelToPath = {
                disabled: true,
                value:false
            }
        } else {
            moveConfiguration.parallelToPath = {
                disabled: false,
                value:false
            }
        }
        this.props.UpdateMoveConfig(moveConfiguration);
    }
    updateStraightPath(value) {
        let moveConfiguration = this.props.animationData.moveConfig;
        moveConfiguration.straightPath.value = value;
        this.props.UpdateMoveConfig(moveConfiguration);
    }
    updateReturnToStart(value) {
        let moveConfiguration = this.props.animationData.moveConfig;
        moveConfiguration.returnToStart.value = value;
        this.props.UpdateMoveConfig(moveConfiguration);
    }
    updateReverseMotion(value) {
        let moveConfiguration = this.props.animationData.moveConfig;
        moveConfiguration.reverseMotion.value = value;
        this.props.UpdateMoveConfig(moveConfiguration);
    }
    updateMoveConfigExpand() {
        let moveConfiguration = this.props.animationData.moveConfig;
        moveConfiguration.expanded = true;
        this.props.UpdateMoveConfig(moveConfiguration);
    }
    updateMoveConfigContract() {
        let moveConfiguration = this.props.animationData.moveConfig;
        moveConfiguration.expanded = false;
        this.props.UpdateMoveConfig(moveConfiguration);
    }
    renderMoveConfig() {
        return(<View style={configStyles.configBlock}>
            <AnimationPlayPanel/>
            <View style={configStyles.configContent}>
                <View style={configStyles.configTop}>
                    <View style={configStyles.leftSpacer}>
                    </View>
                    <View style={configStyles.midButtonContainer}>
                        <View style={configStyles.leftSpacer}>
                        </View>
                        <TouchableHighlight
                            style={configStyles.bigMidButton}
                            onPress={() => {this.props.setAnimationToolState('MOVE_EFFECT_DRAW');this.props.SetAnimationConfig(null);}}>
                            <Text style={configStyles.bigMidButtonText}>REDRAW PATH</Text>
                        </TouchableHighlight>
                        <View style={configStyles.rightSpacer}>
                        </View>
                    </View>
                    <View style={configStyles.rightSpacer}>
                        {this.props.animationData.moveConfig.expanded ?
                            <View style={{transform: [{rotate: '180deg'}]}}>
                                <CircleButton
                                    buttonSize={20}
                                    iconButton={{iconName: 'up-line'}}
                                    baseColor="#565656"
                                    borderColor="transparent"
                                    action={() => {
                                        this.updateMoveConfigContract()
                                    }}/>
                            </View>:
                            <CircleButton
                                buttonSize={20}
                                iconButton={{iconName: 'up-line'}}
                                baseColor="#565656"
                                borderColor="transparent"
                                action={() => {
                                    this.updateMoveConfigExpand()
                                }}/>
                        }
                    </View>
                </View>
                <View style={configStyles.configMid}>
                    <View style={configStyles.configLine}>
                    <View style={configStyles.leftSideIcon}>
                            <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                        </View>
                        <View style={configStyles.labelContainer} >
                            <Text style={configStyles.switchLabel}>Loops: {this.props.animationData.moveConfig.iterations.value}</Text>
                        </View>
                        <View style={configStyles.rightSideSwitch}>
                        </View>
                    </View>
                    <View style={configStyles.configLine}>
                        <Slider
                            style = {configStyles.SliderBlock}
                            trackStyle = {configStyles.SliderTrack}
                            thumbTintColor = 'white'
                            thumbStyle = {configStyles.SliderHandle}
                            minimumTrackTintColor = '#119EC2'
                            maximumTrackTintColor = '#119EC2'
                            value={this.props.animationData.moveConfig.iterations.value}
                            minimumValue={1}
                            maximumValue={50}
                            borderRadius={50}
                            onValueChange={(value)=> this.updateMoveIteration(value)}/>
                    </View>
                    <View style={configStyles.configLine}>
                        <View style={configStyles.leftSideIcon}>
                            <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                        </View>
                        <View style={configStyles.labelContainer} >
                            <Text style={this.props.animationData.moveConfig.parallelToPath.disabled? configStyles.switchLabelDisabled : configStyles.switchLabel}>Tilt Along Path</Text>
                        </View>
                        <View style={configStyles.rightSideSwitch}>
                            <Switch
                                value={this.props.animationData.moveConfig.parallelToPath.value}
                                onValueChange={(val) => this.updateParallelToPath(val)}
                                disabled={this.props.animationData.moveConfig.parallelToPath.disabled}
                                activeText={''}
                                inActiveText={''}
                                backgroundActive={'#119EC2'}
                                backgroundInactive={this.props.animationData.moveConfig.parallelToPath.disabled? '#B7B7B7':'gray'}
                                circleActiveColor={'white'}
                                circleInActiveColor={this.props.animationData.moveConfig.parallelToPath.disabled? '#EDECED': 'white'}
                            />
                        </View>
                    </View>
                    <View style={configStyles.configLine}>
                        <View style={configStyles.leftSideIcon}>
                            <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                        </View>
                        <View style={configStyles.labelContainer} >
                            <Text style={this.props.animationData.moveConfig.finishUpright.disabled? configStyles.switchLabelDisabled : configStyles.switchLabel}>Finish Upright</Text>
                        </View>
                        <View style={configStyles.rightSideSwitch}>
                            <Switch
                                value={this.props.animationData.moveConfig.finishUpright.value}
                                onValueChange={(val) => this.updateFinishUpright(val)}
                                disabled={this.props.animationData.moveConfig.finishUpright.disabled}
                                activeText={''}
                                inActiveText={''}
                                backgroundActive={'#119EC2'}
                                backgroundInactive={this.props.animationData.moveConfig.finishUpright.disabled? '#B7B7B7':'gray'}
                                circleActiveColor={'white'}
                                circleInActiveColor={this.props.animationData.moveConfig.finishUpright.disabled? '#EDECED': 'white'}
                            />
                        </View>
                    </View>
                    <View style={configStyles.configLine}>
                        <View style={configStyles.leftSideIcon}>
                            <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                        </View>
                        <View style={configStyles.labelContainer} >
                            <Text style={configStyles.switchLabel}>Straight path</Text>
                        </View>
                        <View style={configStyles.rightSideSwitch}>
                            <Switch
                                value={this.props.animationData.moveConfig.straightPath.value}
                                onValueChange={(val) => this.updateStraightPath(val)}
                                disabled={this.props.animationData.moveConfig.straightPath.disabled}
                                activeText={''}
                                inActiveText={''}
                                backgroundActive={'#119EC2'}
                                backgroundInactive={this.props.animationData.moveConfig.straightPath.disabled? '#B7B7B7':'gray'}
                                circleActiveColor={'white'}
                                circleInActiveColor={this.props.animationData.moveConfig.straightPath.disabled? '#EDECED': 'white'}
                            />
                        </View>
                    </View>
                    <View style={configStyles.configLine}>
                        <View style={configStyles.leftSideIcon}>
                            <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                        </View>
                        <View style={configStyles.labelContainer} >
                            <Text style={configStyles.switchLabel}>Return To Start</Text>
                        </View>
                        <View style={configStyles.rightSideSwitch}>
                            <Switch
                                value={this.props.animationData.moveConfig.returnToStart.value}
                                onValueChange={(val) => this.updateReturnToStart(val)}
                                disabled={this.props.animationData.moveConfig.returnToStart.disabled}
                                activeText={''}
                                inActiveText={''}
                                backgroundActive={'#119EC2'}
                                backgroundInactive={this.props.animationData.moveConfig.returnToStart.disabled? '#B7B7B7':'gray'}
                                circleActiveColor={'white'}
                                circleInActiveColor={this.props.animationData.moveConfig.returnToStart.disabled? '#EDECED': 'white'}
                            />
                        </View>
                    </View>
                    <View style={configStyles.configLine}>
                        <View style={configStyles.leftSideIcon}>
                            <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                        </View>
                        <View style={configStyles.labelContainer} >
                            <Text style={configStyles.switchLabel}>Retrace</Text>
                        </View>
                        <View style={configStyles.rightSideSwitch}>
                            <Switch
                                value={this.props.animationData.moveConfig.reverseMotion.value}
                                onValueChange={(val) => this.updateReverseMotion(val)}
                                disabled={this.props.animationData.moveConfig.reverseMotion.disabled}
                                activeText={''}
                                inActiveText={''}
                                backgroundActive={'#119EC2'}
                                backgroundInactive={this.props.animationData.moveConfig.reverseMotion.disabled? '#B7B7B7' : 'gray'}
                                circleActiveColor={'white'}
                                circleInActiveColor={this.props.animationData.moveConfig.reverseMotion.disabled? '#EDECED': 'white'}
                            />
                        </View>
                    </View>
                </View>
                <View style={configStyles.configBottom}>
                    <View style={configStyles.leftSpacer}>
                    </View>
                    <View style={configStyles.midButtonContainer}>
                        <TouchableHighlight style={configStyles.bigMidButton}
                                            onPress={() => {this.props.SetAnimationConfig(null); this.props.showMoveHelp()}}>
                            <Text style={configStyles.bigMidButtonText}>SHOW HELP</Text>
                        </TouchableHighlight>
                        <View style={configStyles.rightSpacer}>
                        </View>
                    </View>
                    <View style={configStyles.rightSpacer}>
                        <CircleButton
                            buttonSize={50}
                            iconButton={{iconName:'Trash'}}
                            baseColor="#565656"
                            borderColor="transparent"
                            action={()=>{this.props.SetAnimationConfig(null)}}/>
                        <Text style={{color:'#565656', marginTop: -10}}>Trash</Text>
                    </View>
                </View>
            </View>
        </View>);
    }

    // Rotate Configuration

    updateRotateIteration(value) {
        cleanValue = Math.floor(value);
       if(cleanValue !== this.props.animationData.rotateConfig.iterations.value) {
            let rotateConfiguration = this.props.animationData.rotateConfig;
            rotateConfiguration.iterations.value = cleanValue;
            this.props.UpdateRotateConfig(rotateConfiguration);
        }
    }
    updateAutoRotateRotation(value) {
        cleanValue = Math.floor(value);
        if(cleanValue !== this.props.animationData.rotateConfig.rotations.value) {
            let rotateConfiguration = this.props.animationData.rotateConfig;
            rotateConfiguration.rotations.value = cleanValue;
            this.props.UpdateRotateConfig(rotateConfiguration);
        }
    }
    updateFinishUprightRotate(value) {
        let rotateConfiguration = this.props.animationData.rotateConfig;
        rotateConfiguration.finishUpright.value = value;
        this.props.UpdateRotateConfig(rotateConfiguration);
    }
    updateRotateConfigExpand() {
        let rotateConfiguration = this.props.animationData.rotateConfig;
        rotateConfiguration.expanded = true;
        this.props.UpdateRotateConfig(rotateConfiguration);
    }
    updateRotateConfigContract() {
        let rotateConfiguration = this.props.animationData.rotateConfig;
        rotateConfiguration.expanded = false;
        this.props.UpdateRotateConfig(rotateConfiguration);
    }
    updateAutoRotation(value) {
        // temporary: we are not currently using the rotations slider. waiting on design to be finalized
        // this.setState({rotateFlag: value})
        let rotateConfiguration = this.props.animationData.rotateConfig;
        rotateConfiguration.autoRotation.value = value;
        this.props.UpdateRotateConfig(rotateConfiguration);
    }
    updateRotateReverseMotion(value) {
        let rotateConfiguration = this.props.animationData.rotateConfig;
        rotateConfiguration.reverseMotion.value = value;
        this.props.UpdateRotateConfig(rotateConfiguration);
    }

    renderRotations() {
        if (this.state.rotateFlag) {
            return(
                <View style={configStyles.configLine}>
                    <View style={configStyles.leftSideIcon}>
                        <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                    </View>
                    <View style={configStyles.labelContainer} >
                        <Text style={configStyles.switchLabel}>Rotations: {this.props.animationData.rotateConfig.rotations.value}</Text>
                    </View>
                    <View style={configStyles.rightSideSwitch}>
                    </View>
                </View>
            );
        }
    }

    renderRotationswitch() {
        if (this.state.rotateFlag) {
            return(
                <View style={[configStyles.configLine]}>
                    <Slider
                        style = {[configStyles.SliderBlock]}
                        trackStyle = {configStyles.SliderTrack}
                        thumbTintColor = 'white'

                        thumbStyle = {configStyles.SliderHandle}
                        minimumTrackTintColor = '#119EC2'
                        maximumTrackTintColor = '#119EC2'
                        value={this.props.animationData.rotateConfig.rotations.value}
                        minimumValue={1}
                        maximumValue={50}
                        onValueChange={(value)=> this.updateAutoRotateRotation(value)}
                    />
                </View>
            );
        }
    }


    renderRotateConfig() {
        return(
            <View style={configStyles.configBlock}>
                <AnimationPlayPanel/>
                    <View style={configStyles.configContent}>
                        <View style={configStyles.configTop}>
                            <View style={configStyles.leftSpacer}>
                            </View>
                            <View style = {[configStyles.pivotView, {marginLeft: -80}]}>
                            <TouchableOpacity
                                    onPress={() => {
                                        this.props.pivotPointButtonPressed(true);
                                    }}>
                                <Image style = {configStyles.pivot}
                                        source={require('./images/PivotPointImage.png')}></Image>
                            </TouchableOpacity>
                        </View>
                        <View style={configStyles.midButtonContainer}>
                            <View style={configStyles.leftSpacer}>
                            </View>
                            <TouchableHighlight
                                style={configStyles.bigMidButton}
                                onPress={() => {
                                    this.props.setAnimationToolState(Constants.ANIM_TOOL_STATE_ROTATE_EFFECT_INPUT);
                                    this.props.SetAnimationConfig(null);
                                }}>
                                <Text style={configStyles.bigMidButtonText}>RECREATE ROTATE</Text>
                            </TouchableHighlight>
                            <View style={configStyles.rightSpacer} />
                        </View>
                        
                        <View style={configStyles.rightSpacer}>
                             {this.props.animationData.rotateConfig.expanded ?
                             <View style={{transform: [{rotate: '180deg'}]}}>
                                     <CircleButton
                                         buttonSize={20}
                                         iconButton={{iconName: 'up-line'}}
                                         baseColor="#565656"
                                         borderColor="transparent"
                                         action={() => {
                                             this.updateRotateConfigContract()
                                         }}
                                     />
                                 </View> :
                                 <CircleButton
                                     buttonSize={20}
                                     iconButton={{iconName: 'up-line'}}
                                     baseColor="#565656"
                                     borderColor="transparent"
                                     action={() => {
                                         this.updateRotateConfigExpand()
                                     }}
                                 />
                              }
                         </View>
                     </View>
                    <View style={configStyles.configMid}>
                                <View style={configStyles.configLine}>
                                 <View style={configStyles.leftSideIcon}>
                                        <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                            </View>
                            <View style={configStyles.labelContainer} >
                                    <Text style={configStyles.switchLabel}>Loops: {this.props.animationData.rotateConfig.iterations.value}</Text>
                                        </View>
                                        <View style={configStyles.rightSideSwitch}>
                                        </View>
                                </View>
                                <View style={[configStyles.configLine, {marginTop: -30}]}>
                                    <Slider
                                        style = {configStyles.SliderBlock}
                                        trackStyle = {configStyles.SliderTrack}
                                        thumbTintColor = 'white'
                                        //thumbStyle = {configStyles.SliderHandle}
                                        minimumTrackTintColor = '#119EC2'
                                        maximumTrackTintColor = '#119EC2'
                                        value={this.props.animationData.rotateConfig.iterations.value}
                                        minimumValue={1}
                                        maximumValue={50}
                                        onValueChange={(value)=> this.updateRotateIteration(value)}/>
                                </View>

                             <View style={configStyles.configLine}>
                                 <View style={configStyles.leftSideIcon}>
                                     <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                                 </View>
                                 <View style={configStyles.labelContainer} >
                                     <Text style={this.props.animationData.rotateConfig.finishUpright.disabled? configStyles.switchLabelDisabled : configStyles.switchLabel}>Finish Upright</Text>
                                 </View>
                                 <View style={configStyles.rightSideSwitch}>
                                     <Switch
                                        style={{
                                            margin:0, 
                                            overflow: 'hidden',
                                            height: 30,
                                            padding:0}}
                                         value={this.props.animationData.rotateConfig.finishUpright.value}
                                         onValueChange={(value) => this.updateFinishUprightRotate(value)}
                                         disabled={this.props.animationData.rotateConfig.finishUpright.disabled}
                                         activeText={''}
                                         inActiveText={''}
                                         backgroundActive={'#119EC2'}
                                         backgroundInactive={this.props.animationData.rotateConfig.finishUpright.disabled? '#B7B7B7':'#8e8e8e'}
                                         circleActiveColor={'white'}
                                         circleInActiveColor={this.props.animationData.rotateConfig.finishUpright.disabled? '#EDECED': 'white'}
                                     />
                                 </View>


                             </View>

                             <View style={configStyles.configLine}>
                                 <View style={configStyles.leftSideIcon}>
                                     <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                                 </View>
                                 <View style={configStyles.labelContainer} >
                                     <Text style={configStyles.switchLabel}>Auto-Rotate</Text>
                                 </View>
                                 <View style={configStyles.rightSideSwitch}>
                                     <Switch
                                         value={this.props.animationData.rotateConfig.autoRotation.value}
                                         onValueChange={(val) => this.updateAutoRotation(val)}
                                         disabled={this.props.animationData.rotateConfig.autoRotation.disabled}
                                         activeText={''}
                                         inActiveText={''}
                                         backgroundActive={'#119EC2'}
                                         backgroundInactive={this.props.animationData.rotateConfig.autoRotation.disabled? '#8e8e8e':'gray'}
                                         circleActiveColor={'white'}
                                         circleInActiveColor={this.props.animationData.rotateConfig.autoRotation.disabled? '#EDECED': 'white'}

                                     />
                              </View>
                             </View>
                               {this.renderRotations()}
                                {this.renderRotationswitch()}
                           <View style={configStyles.configLine}>
                                 <View style={configStyles.leftSideIcon}>
                                     <Constants.DodlesIcon name={'Move-Up'} size={30} color={'#B7B7B7'} />
                                 </View>
                                 <View style={configStyles.labelContainer} >
                                     <Text style={configStyles.switchLabel}>Retrace</Text>
                                 </View>
                                 <View style={configStyles.rightSideSwitch}>
                                     <Switch
                                         value={this.props.animationData.rotateConfig.reverseMotion.value}
                                         onValueChange={(val) => this.updateRotateReverseMotion(val)}
                                         disabled={this.props.animationData.rotateConfig.reverseMotion.disabled}
                                         activeText={''}
                                         inActiveText={''}
                                         backgroundActive={'#119EC2'}
                                         backgroundInactive={this.props.animationData.rotateConfig.reverseMotion.disabled? '#8e8e8e' : 'gray'}
                                         circleActiveColor={'white'}
                                         circleInActiveColor={this.props.animationData.rotateConfig.reverseMotion.disabled? '#EDECED': 'white'}
                                     />
                                 </View>
                             </View>
                         </View>
                         <View style={configStyles.configBottom}>
                             <View style={configStyles.leftSpacer}>
                             </View>
                             <View style={configStyles.midButtonContainer}>
                                 <TouchableHighlight style={configStyles.bigMidButton}
                                                     onPress={() => {this.props.SetAnimationConfig(null);}}>
                                     <Text style={configStyles.bigMidButtonText}>SHOW HELP</Text>
                                 </TouchableHighlight>
                                 <View style={configStyles.rightSpacer}>
                                 </View>
                             </View>
                             <View style={[configStyles.rightSpacer, {marginTop: -40}]}>
                                 <CircleButton
                                     buttonSize={70}
                                     iconButton={{iconName:'Trash'}}
                                     baseColor="#8e8e8e"
                                     borderColor="transparent"
                                     action={()=>{this.props.SetAnimationConfig(null)}}/>
                                 <Text style={{color:'#565656', marginTop: -10}}>Trash</Text>
                             </View>
                         </View>
                     </View>

        </View>);
    }


    render() {
        switch (this.props.animationData.selectedEffect) {
            case 'Move': {
                return(
                    <View style={{position: 'relative'}}>
                        <ScrollView style={this.props.animationData.moveConfig.expanded? configStyles.configPanelExpanded : configStyles.configPanelContracted}>
                            {this.renderMoveConfig()}
                        </ScrollView>
                    </View>
                );
                break;
            }
            case 'Spin': {
                return(
                    <View style={{position: 'relative'}}>
                        <ScrollView style={this.props.animationData.rotateConfig.expanded? configStyles.configPanelExpanded : configStyles.configPanelContracted}>
                            {this.renderRotateConfig()}
                        </ScrollView>
                    </View>
                );
                break;
            }
            case 'Scale': {
                return(
                    <View style={{position: 'relative'}}>
                        <ScrollView style={this.props.animationData.rotateConfig.expanded? configStyles.configPanelExpanded : configStyles.configPanelContracted}>
                            <Text>Placeholder for Scale Effect configuration panel.</Text>
                        </ScrollView>
                    </View>
                );
                break;
            }
            default: {
                return(
                    <View style={{position: 'relative'}}>
                        <ScrollView  style={this.props.animationData.rotateConfig.expanded? configStyles.configPanelExpanded : configStyles.configPanelContracted}>
                            <Text>No configuration panel found for {this.props.animationData.selectedEffect}.</Text>
                        </ScrollView>
                    </View>
                );
                break;
            }
        }
    }

}

configStyles = StyleSheet.create({
    configPanelContracted: {
        height: Constants.toolAreaHeight,
        width: Constants.deviceWidth,
        position: 'absolute',
        top: 0,
        left: 0,
        zIndex: 100,
        backgroundColor: '#edeced',
        flexDirection: 'column',
        overflow: 'hidden'
    },
    configPanelExpanded: {
        height: Constants.deviceHeight - Constants.headerHeight - 10,
        width: Constants.deviceWidth,
        position: 'absolute',
        top: (Constants.dodleEngineHeight - 10) * -1,
        left: 0,
        zIndex: 100,
        backgroundColor: '#edeced',
        flexDirection: 'column',
    },
    configBlock: {
        height: Constants.deviceHeight - Constants.headerHeight - 10,
        width: Constants.deviceWidth,
        flexDirection:'column',
        justifyContent: 'center',
        alignItems: 'center',
        zIndex: 101,
    },
    configContent: {
        flex: 1,
        width: Constants.deviceWidth * .8 > 400? 400: Constants.deviceWidth * .8,
    },
    configTop: {
        flex: 1,
        flexDirection: 'row',

    },
    pivot: {
         width:40,
         height:40
    },
    pivotView: {
         marginLeft: 15,
         marginTop: 8

    },
    configMid: {
        flex: 8,
        flexDirection: 'column',
        justifyContent: 'space-around',
        alignItems: 'center',

    },
    SliderBlock: {
        flex: 1,

    },
    SliderTrack: {
        height: 23,
        borderRadius: 50,
        overflow: 'hidden'
    },
    configLine:{
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'flex-start',
        alignItems: 'flex-start',
        overflow: 'hidden'

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
        fontFamily: 'OpenSans-Bold',
        color: '#474747',
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
    SliderHandle:{
        borderRadius: 50,

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
        width: Constants.deviceWidth *.5,
        height:30,
        backgroundColor: '#119EC2',
        borderRadius: 15,
        justifyContent: 'center',
        alignItems: 'center',
    },
    bigMidButtonText: {
        fontSize: 15,
        fontFamily: 'OpenSans-Bold',
        color: 'white',
    },
    rightSpacer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',

    }
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
        pivotPointButtonPressed: (isPressed) => dispatch(ActionCreators.pivotPointButtonPressed(isPressed)),
        //rotate

    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(AnimationConfig)