/**
 * @providesModule DodlesEngineView
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import ReactNative, {
    requireNativeComponent,
    StyleSheet,
    UIManager,
    View,
    ViewPropTypes
} from 'react-native';
import _ from 'underscore';
import { connect } from 'react-redux';
import { ActionCreators } from "../../actions/index";
import * as Constants from '../../constants'

const EDITOR_EVENT_TOPIC = "EDITOR";
const EVENT_CHANGE_TOOL = "CHANGE_TOOL";
const EVENT_CHANGE_BRUSH = "CHANGE_BRUSH";
const EVENT_CHANGE_SHAPE = "CHANGE_SHAPE";
const EVENT_ENTER_CUSTOM_SHAPE_MODE = "ENTER_CUSTOM_SHAPE_MODE";
const EVENT_CHANGE_SHAPE_COLOR = "CHANGE_SHAPE_COLOR";
const EVENT_CHANGE_SHAPE_SIZE = "CHANGE_SHAPE_SIZE";
const EVENT_CHANGE_SHAPE_OPACITY = "CHANGE_SHAPE_OPACITY";
const EVENT_CHANGE_SHAPE_CORNER = "CHANGE_SHAPE_CORNER";
const EVENT_CHANGE_SHAPE_ROUNDING = "CHANGE_SHAPE_ROUNDING";
const EVENT_CHANGE_BRUSH_OPACITY = "CHANGE_BRUSH_OPACITY";
const EVENT_CHANGE_BRUSH_SIZE = "CHANGE_BRUSH_SIZE"
const EVENT_CHANGE_BRUSH_COLOR = "CHANGE_BRUSH_COLOR";
const EVENT_CHANGE_BRUSH_RULER_MODE = "CHANGE_BRUSH_RULER_MODE";
const EVENT_OK_CANCEL_PRESSED_OK = "OK_CANCEL_STACK_POP_OK";
const EVENT_OK_CANCEL_PRESSED_CANCEL = "OK_CANCEL_STACK_POP_CANCEL";
const EVENT_CHANGE_SHAPE_STAR_DEPTH = "CHANGE_SHAPE_STAR_DEPTH";
const EVENT_UNDO = "UNDO"
const EVENT_DELETE_OBJECT = "DELETE_OBJECT"
const EVENT_REDO = "REDO"
const EVENT_DELETE_DODLE = "DELETE_DODLE"
// LAYER EVENTS
const EVENT_LAYER_OPACITY = "LAYER_OPACITY";
const EVENT_LAYER_GROUP = "LAYER_GROUP";
const EVENT_LAYER_UNGROUP = "LAYER_UNGROUP";
const EVENT_LAYER_FLIP_HORIZONTAL = "LAYER_FLIP_HORIZONTAL";
const EVENT_LAYER_FLIP_VERTICAL = "LAYER_FLIP_VERTICAL";
const EVENT_LAYER_SIZE = "LAYER_SIZE";
const EVENT_LAYER_FORWARD = "LAYER_FORWARD";
const EVENT_LAYER_FRONT = "LAYER_FRONT";
const EVENT_LAYER_BACKWARD = "LAYER_BACKWARD";
const EVENT_LAYER_BACK = "LAYER_BACK";
const EVENT_LAYER_COPY = "LAYER_COPY";
const EVENT_LAYER_DELETE = "LAYER_DELETE";
// ANIMATION EVENTS
const EVENT_ANIMATION_TOOL_SET_STATE = "SET_ANIMATION_TOOL_STATE"
const EVENT_ANIMATION_START = "ANIMATION_START";
const EVENT_ANIMATION_STOP = "ANIMATION_STOP";
const EVENT_MOVE_CONFIG_UPDATE = "MOVE_CONFIG_UPDATE";
const EVENT_ROTATE_CONFIG_UPDATE = "ROTATE_CONFIG_UPDATE";
const EVENT_ANIM_CHANGE_SELECTED_BLOCK = "ANIM_CHANGE_SELECTED_BLOCK"
const EVENT_ANIM_PLAYHEAD_POSITION_CHANGED = "ANIM_PLAYHEAD_POSITION_CHANGED"
const EVENT_ANIM_TOGGLE_PIVOT_POINT_MODE = "ANIM_TOGGLE_PIVOT_POINT_MODE"
// DATA EVENTS
const EVENT_SAVE_DODLE = "SAVE_DODLE"
const EVENT_LOAD_DODLE = "LOAD_DODLE"



var RCTDodlesEngineView = requireNativeComponent(`DodlesEngineView`, DodlesEngineView, {
    nativeOnly: {
        onChangeState: true,
        onReady: true,
        onEngineEvent: true
    }
});

class DodlesEngineView extends Component {

    static propTypes = {
        style: (ViewPropTypes && ViewPropTypes.style) || View.propTypes.style,
    };

    constructor(props) {
        super(props);
        this.state = {
            moduleMargin: StyleSheet.hairlineWidth * 2
        }
    }

    componentWillReceiveProps(nextProps) {
        if (!_.isEqual(this.props.brushData, nextProps.brushData)) {
            this.onBrushChange(this.props.brushData, nextProps.brushData);
        }
        if (!_.isEqual(this.props.shapeData, nextProps.shapeData)) {
            this.onShapeChange(this.props.shapeData, nextProps.shapeData);
        }
        if (!_.isEqual(this.props.toolData, nextProps.toolData)) {
            this.onToolChange(this.props.toolData, nextProps.toolData);
        }
        if (!_.isEqual(this.props.headerData, nextProps.headerData)) {
            this.onHeaderChange(this.props.headerData, nextProps.headerData);
        }
        if (!_.isEqual(this.props.layerData, nextProps.layerData)) {
            this.onLayerChange(this.props.layerData, nextProps.layerData);
        }
        if (!_.isEqual(this.props.animationData, nextProps.animationData)) {
            this.onAnimationChange(this.props.animationData, nextProps.animationData);
        }
        if (!_.isEqual(this.props.animationData.moveConfig, nextProps.animationData.moveConfig)) {
            this.onMoveConfigChange(this.props.animationData.moveConfig, nextProps.animationData.moveConfig);
        }
        //rotate
         if (!_.isEqual(this.props.animationData.rotateConfig, nextProps.animationData.rotateConfig)) {
             this.onRotateConfigChange(this.props.animationData.rotateConfig, nextProps.animationData.rotateConfig);
                }
        if (!_.isEqual(this.props.dataData, nextProps.dataData)) {
            this.onDataChange(this.props.dataData, nextProps.dataData);
        }
    }

    checkConfigChange(oldConfig, newConfig) {
        if((oldConfig.iterations.value !== newConfig.iterations.value)
            || (oldConfig.parallelToPath.value !== newConfig.parallelToPath.value)
            || (oldConfig.finishUpright.value !== newConfig.finishUpright.value)
            || (oldConfig.straightPath.value !== newConfig.straightPath.value)
            || (oldConfig.returnToStart.value !== newConfig.returnToStart.value)
            || (oldConfig.reverseMotion.value !== newConfig.reverseMotion.value))
        {
            return true;
        } else {
            return false;
        }
    }


    onToolChange(oldData, newData) {
        // UNDO
        if (!oldData.isUndoButtonPressed && newData.isUndoButtonPressed) {
            this.props.undoButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_UNDO, null);
        }
        // REDO
        else if (!oldData.isRedoButtonPressed && newData.isRedoButtonPressed) {
            this.props.redoButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_REDO, null);
        }
        // DELETE DODLE
        else if (!oldData.isDeleteDodleButtonPressed && newData.isDeleteDodleButtonPressed) {
            this.props.deleteDodleButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_DELETE_DODLE, null);
        }
        // TOOLS
        else if(oldData.activetool !== newData.activetool) {
            if(newData.activetool !== "TOOL.NULL") {
                this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_TOOL, newData.activetool);
            } else {
                this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_TOOL, oldData.activetool);
            }
        }
    }

    onLayerChange(oldData, newData) {

        if (!oldData.isOpacityButtonPressed && newData.isOpacityButtonPressed) {
            this.props.layerOpacityButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_OPACITY, null);
        } else if (!oldData.isGroupButtonPressed && newData.isGroupButtonPressed) {
            this.props.layerGroupButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_GROUP, null);
        } else if (!oldData.isUngroupButtonPressed && newData.isUngroupButtonPressed) {
            this.props.layerUngroupButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_UNGROUP, null);
        } else if (!oldData.isFlipHorizontalButtonPressed && newData.isFlipHorizontalButtonPressed) {
            this.props.layerFlipHorizontalButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_FLIP_HORIZONTAL, null);
        } else if (!oldData.isFlipVerticalButtonPressed && newData.isFlipVerticalButtonPressed) {
            this.props.layerFlipVerticalButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_FLIP_VERTICAL, null);
        } else if (!oldData.isSizeButtonPressed && newData.isSizeButtonPressed) {
            this.props.layerSizeButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_SIZE, null);
        } else if (!oldData.isForwardButtonPressed && newData.isForwardButtonPressed) {
            this.props.layerForwardButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_FORWARD, null);
        } else if (!oldData.isFrontButtonPressed && newData.isFrontButtonPressed) {
            this.props.layerFrontButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_FRONT, null);
        } else if (!oldData.isBackwardButtonPressed && newData.isBackwardButtonPressed) {
            this.props.layerBackwardButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_BACKWARD, null);
        } else if (!oldData.isBackButtonPressed && newData.isBackButtonPressed) {
            this.props.layerBackButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_BACK, null);
        } else if (!oldData.isCopyButtonPressed && newData.isCopyButtonPressed) {
            this.props.layerCopyButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_COPY, null);
        } else if (!oldData.isDeleteButtonPressed && newData.isDeleteButtonPressed) {
            this.props.layerDeleteButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LAYER_DELETE, null);
        }
    }

    onShapeChange(oldData, newData) {
        if(oldData.activeShape !== newData.activeShape) {
            if(oldData.activeShape == 'CustomShape') {
                this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_TOOL, "TOOL.GEOMETRY")
            }
            if(newData.activeShape !== 'CustomShape') {
                this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_SHAPE, newData.activeShape);
            } else {
                this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_ENTER_CUSTOM_SHAPE_MODE)
            }
        }
        if(oldData.starDepth !== newData.starDepth) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_SHAPE_STAR_DEPTH, newData.starDepth.toFixed(2))
        }
        if(oldData.shapeColor !== newData.shapeColor) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_SHAPE_COLOR, newData.shapeColor)
        }
        if(oldData.shapeSize !== newData.shapeSize) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_SHAPE_SIZE, newData.shapeSize.toFixed(2))
        }
        if(oldData.shapeOpacity !== newData.shapeOpacity) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_SHAPE_OPACITY, newData.shapeOpacity.toFixed(2))
        }
        if(oldData.shapeCorner !== newData.shapeCorner) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_SHAPE_CORNER, newData.shapeCorner.toFixed(2))
        }
        if(oldData.shapeRounding !== newData.shapeRounding) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_SHAPE_ROUNDING, newData.shapeRounding.toFixed(2))
        }
    }

    onBrushChange(oldData, newData) {
        if(oldData.activeBrush !== newData.activeBrush) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_BRUSH, newData.activeBrush);
        }
        if (oldData.activeSubmenu !== newData.activeSubmenu) {
            //console.log('submenu brush changed');
        }
        if (oldData.brushSize !== newData.brushSize) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_BRUSH_SIZE, newData.brushSize.toFixed(0));
        }
        if (oldData.brushOpacity !== newData.brushOpacity) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_BRUSH_OPACITY, newData.brushOpacity.toFixed(2));
        }
        if (oldData.brushColor !== newData.brushColor) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_BRUSH_COLOR, newData.brushColor);
        }
        if (oldData.rulerMode !== newData.rulerMode) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_CHANGE_BRUSH_RULER_MODE);
        }
    }

    onHeaderChange(oldData, newData) {
        if (!oldData.isCancelButtonPressed && newData.isCancelButtonPressed) {
            this.props.cancelButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_OK_CANCEL_PRESSED_CANCEL, null);
        } else if (!oldData.isOkayButtonPressed && newData.isOkayButtonPressed) {
            this.props.okButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_OK_CANCEL_PRESSED_OK, null);
        } else if (!oldData.isSaveDodleButtonPressed && newData.isSaveDodleButtonPressed) {
            this.props.saveDodleButtonPressed(false);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_SAVE_DODLE, null);
        }
    }

    onMoveConfigChange(oldData, newData) {
       //this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_MOVE_CONFIG_UPDATE, newData.iterations.value.toString() + '|' + newData.parallelToPath.value + '|' + newData.finishUpright.value + '|' + newData.straightPath.value + '|' + newData.returnToStart.value + '|' + newData.reverseMotion.value);
    }
     onRotateConfigChange(oldData, newData) {
           //this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_MOVE_CONFIG_UPDATE, newData.iterations.value.toString() + '|' + newData.parallelToPath.value + '|' + newData.finishUpright.value + '|' + newData.straightPath.value + '|' + newData.returnToStart.value + '|' + newData.reverseMotion.value);
        }

    onAnimationChange(oldData, newData) {
        if (newData.toolState != oldData.toolState) {
            console.log("***** DodlesEngineView::onAnimationChange - react is changing AnimationTool state: " + oldData.toolState + " -> " + newData.toolState);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_ANIMATION_TOOL_SET_STATE, newData.toolState);
        } else if (newData.animationRunning && !oldData.animationRunning) {
            console.log("send event " + EVENT_ANIMATION_START);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_ANIMATION_START, null);
        } else if (!newData.animationRunning && oldData.animationRunning) {
            console.log("send event " + EVENT_ANIMATION_STOP);
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_ANIMATION_STOP, null);
        } else if (!newData.moveConfig && oldData.moveConfig) {
            console.log("Iteration config option has changed to " + newData.moveConfig);
        } else if (!newData.rotateConfig && oldData.rotateConfig) {
             console.log("Iteration config option has changed to " + newData.rotateConfig);
        }
        else if (!oldData.updateMoveConfig && newData.updateMoveConfig){
            console.log("Move config Apply button clicked ");
            this.props.resetSendMoveConfig();
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_MOVE_CONFIG_UPDATE, newData.moveConfig.iterations.value.toString() + '|' + newData.moveConfig.parallelToPath.value + '|' + newData.moveConfig.finishUpright.value + '|' + newData.moveConfig.straightPath.value + '|' + newData.moveConfig.returnToStart.value + '|' + newData.moveConfig.reverseMotion.value);
        }  else if (!oldData.updateRotateConfig && newData.updateRotateConfig){
             this.props.resetSendRotateConfig();
             this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_ROTATE_CONFIG_UPDATE, newData.rotateConfig.iterations.value.toString() + '|' + newData.rotateConfig.finishUpright.value + '|' + newData.rotateConfig.autoRotation.value + '|' + newData.rotateConfig.reverseMotion.value);
        } else if (oldData.selectedTimelineEffect != newData.selectedTimelineEffect) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_ANIM_CHANGE_SELECTED_BLOCK, newData.selectedTimelineEffect);
        } else if (oldData.playheadPosition != newData.playheadPosition) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_ANIM_PLAYHEAD_POSITION_CHANGED, newData.playheadPosition.toString());
        } else if (!oldData.pivotPointButtonPressed && newData.pivotPointButtonPressed) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_ANIM_TOGGLE_PIVOT_POINT_MODE, null);
            this.props.pivotPointButtonPressed(false);
        }
    }

    onDataChange(oldData, newData) {
        if (newData.dodleToLoad != null && newData.dodleToLoad != oldData.dodleToLoad ) {
            this.sendEventToEngine(EDITOR_EVENT_TOPIC, EVENT_LOAD_DODLE, newData.dodleToLoad);
            this.props.clearLoadDodle();
        }
    }

    _onReady(event) {
        // The Android GLKView native module is pretty problematic when it comes to
        // mounting correctly and rendering inside React-Native's views hierarchy.
        // For now we must trigger some layout change to force a real render on it,
        // right after the onReady event, so it will smoothly appear after ready.
        // We also use the minimal margin to avoid `UNAUTHORIZED_OVERLAY` error from
        // the native module that is very sensitive to being covered or even touching
        // its containing view.
        this.setState({ moduleMargin: StyleSheet.hairlineWidth });
        if (this.props.onReady) this.props.onReady(event.nativeEvent);
    }

    _onEngineEvent(event) {
        var eventTopic = event.nativeEvent.topic;
        var eventType = event.nativeEvent.type;
        var eventData = event.nativeEvent.data;
        switch (event.nativeEvent.type) {
            case "UPDATE_PLAY_TIME":
                this.props.updatePlayTime(eventData);
                break;
            case "TOOL_CHANGED":
                this._processToolChangedEvent(eventData);
                break;
            case "OK_CANCEL_STACK_CHANGED":
                this._processOkayCancelStackChangeEvent(eventData);
                break;
            case "UNDO_REDO_CHANGED":
                this._processUndoRedoChangeEvent(eventData);
                break;
            case "SHAPE_ACTIVATED":
                if(this.props.toolData.activetool == Constants.TOOL_SHAPE) {
                    if(eventData != 'star') {
                        this.props.setActiveShapeSubmenu('ShapeCornerPicker')
                    } else {
                        this.props.setActiveShapeSubmenu('ShapeCornerDepthPicker')
                    }
                }
                break;
            case "SHAPE_DEACTIVATED":
                this.props.setActiveShapeSubmenu('Main');
                this.props.setActiveShape(null);
                break;
            case "TIMELINE_INFO_UPDATED":
                let cleanData = JSON.parse(eventData);
                this.props.updateTimelineData(cleanData.animations);
                break;
            case "ANIMATION_TOOL_STATE_CHANGED":
                if (this.props.animationData.toolState != eventData) {
                    this.props.animationData.toolState = eventData;
                    this.props.setAnimationToolState(eventData);
                }
                break;
            case "ANIMATION_COMPLETE":
                this.props.animationStop();
                break;
            case "ANIM_SELECTED_BLOCK_CHANGED":
                if (this.props.animationData.selectedTimelineEffect != eventData) {
                    this.props.animationData.selectedTimelineEffect = eventData;
                    this.props.setSelectedTimelineEffect(eventData);
                }
                break;
            case "DODLE_ENGINE_STATE_RESET":
                if (this.props.dataData.currentDodleID != eventData) {
                    this.props.dataData.currentDodleID = eventData;
                    this.props.setCurrentDodleID(eventData);
                }
                break;
            case "DODLE_LOAD_SUCCESS":
                // TODO: do we need to clear any react side state?
                break;
            default:
                console.log("Unhandled Dodle Engine Event Type: " + eventTopic + "::" + eventType);
                // if (eventData) {
                //     console.log(eventData);
                // }
        }
    }

    _processToolChangedEvent(data) {
        var newToolName = data;

        if (newToolName != this.props.toolData.activetool) {
            this.props.toolData.activetool = newToolName;
            this.props.setActiveTool(newToolName);
        }
    }

    _processOkayCancelStackChangeEvent(data) {
        var params = data.split("|");
        var okayCount = parseInt(params[0]);
        this.props.setOkayCancelState(params[2]=='true', params[1]=='true', okayCount, params[3]);
        if (okayCount == 0) {
            if (this.props.toolData.activetool == "TOOL.GEOMETRY") {
                this.props.setActiveShape(null);
                if(this.props.shapeData.isCustomizingShape) {
                    this.props.setCustomShapeStatus(false);
                    this.props.setActiveShapeSubmenu('Main');
                }
            }
        }
    }

    _processUndoRedoChangeEvent(data) {
        var params = data.split("|");
        this.props.setUndoRedoState(params[0]=='true', params[1]=='true');
    }

    sendEventToEngine(topic, type, data) {
        UIManager.dispatchViewManagerCommand(
            this.dodlesEngineView,
            UIManager.DodlesEngineView.Commands.sendEventToEngine,
            [topic, type, data]
        );
    }

    componentDidMount() {
        this.dodlesEngineView = ReactNative.findNodeHandle(this.dodlesEngineViewRef);
    }

    render() {
        return (
            <View style={[styles.container, this.props.style]}>
                <RCTDodlesEngineView
                    ref={(dodlesEngineView) =>  {this.dodlesEngineViewRef = dodlesEngineView;} }
                    {...this.props}
                    style={[styles.module, { margin: this.state.moduleMargin }]}
                    onReady={this._onReady.bind(this)}
                    onEngineEvent={this._onEngineEvent.bind(this)}
                />
            </View>
        );
    }
}

DodlesEngineView.propTypes = {
    ...View.propTypes
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'column',
    },
    module: {
        flex: 1,
    },
});

function mapStateToProps (state, ownProps) {
    return {
        brushData: state.brush,
        toolData: state.tool,
        shapeData: state.shape,
        headerData: state.header,
        layerData: state.layer,
        animationData: state.animation,
        dataData: state.data
    }
}

function mapDispatchToProps (dispatch) {
    return {
        setOkayCancelState: (showCancel, showOkay, okayCount, iconType) =>
            dispatch(ActionCreators.setOkCancelState(showCancel, showOkay, okayCount, iconType)),
        cancelButtonPressed: (isPressed) => dispatch(ActionCreators.cancelButtonPressed(isPressed)),
        okButtonPressed: (isPressed) => dispatch(ActionCreators.okButtonPressed(isPressed)),
        setUndoRedoState: (undoActive, redoActive) => dispatch(ActionCreators.setUndoRedoState(undoActive, redoActive)),
        undoButtonPressed: (isPressed) => dispatch(ActionCreators.undoButtonPressed(isPressed)),
        redoButtonPressed: (isPressed) => dispatch(ActionCreators.redoButtonPressed(isPressed)),
        setActiveBrush: (brushName) => dispatch(ActionCreators.setActiveBrush(brushName)),
        setActiveShape: (shapeName) => dispatch(ActionCreators.setActiveShape(shapeName)),
        setActiveShapeSubmenu: (submenu) => dispatch(ActionCreators.setActiveShapeSubmenu(submenu)),
        setCustomShapeStatus: (value) => dispatch(ActionCreators.setCustomShapeStatus(value)),
        deleteDodleButtonPressed: (isPressed) => dispatch(ActionCreators.deleteDodleButtonPressed(isPressed)),
        setActiveTool: (toolname) => dispatch(ActionCreators.setActiveTool(toolname)),
        // layer tool actions
        layerOpacityButtonPressed: (isPressed) => dispatch(ActionCreators.layerOpacityButtonPressed(isPressed)),
        layerGroupButtonPressed: (isPressed) => dispatch(ActionCreators.layerGroupButtonPressed(isPressed)),
        layerUngroupButtonPressed: (isPressed) => dispatch(ActionCreators.layerUngroupButtonPressed(isPressed)),
        layerFlipHorizontalButtonPressed: (isPressed) => dispatch(ActionCreators.layerFlipHorizontalButtonPressed(isPressed)),
        layerFlipVerticalButtonPressed: (isPressed) => dispatch(ActionCreators.layerFlipVerticalButtonPressed(isPressed)),
        layerSizeButtonPressed: (isPressed) => dispatch(ActionCreators.layerSizeButtonPressed(isPressed)),
        layerCopyButtonPressed: (isPressed) => dispatch(ActionCreators.layerCopyButtonPressed(isPressed)),
        layerDeleteButtonPressed: (isPressed) => dispatch(ActionCreators.layerDeleteButtonPressed(isPressed)),
        layerForwardButtonPressed: (isPressed) => dispatch(ActionCreators.layerForwardButtonPressed(isPressed)),
        layerFrontButtonPressed: (isPressed) => dispatch(ActionCreators.layerFrontButtonPressed(isPressed)),
        layerBackwardButtonPressed: (isPressed) => dispatch(ActionCreators.layerBackwardButtonPressed(isPressed)),
        layerBackButtonPressed: (isPressed) => dispatch(ActionCreators.layerBackButtonPressed(isPressed)),
        // animation tool actions
        setAnimationToolState: (toolState) => dispatch(ActionCreators.setAnimationToolState(toolState)),
        animationStop: () => dispatch(ActionCreators.animationStop()),
        resetSendMoveConfig: () => dispatch(ActionCreators.resetSendMoveConfig()),
        resetSendRotateConfig: () => dispatch(ActionCreators.resetSendRotateConfig()),
        updateTimelineData: (timelineData) => dispatch(ActionCreators.updateTimelineData(timelineData)),
        updatePlayTime: (playTime) => dispatch(ActionCreators.updatePlayTime(playTime)),
        setSelectedTimelineEffect: (effectID) => dispatch(ActionCreators.setSelectedTimelineEffect(effectID)),
        pivotPointButtonPressed: (isPressed) => dispatch(ActionCreators.pivotPointButtonPressed(isPressed)),
        // data actions
        setCurrentDodleID: (newDodleID) => dispatch(ActionCreators.setCurrentDodleID(newDodleID)),
        saveDodleButtonPressed: (isPressed) => dispatch(ActionCreators.saveDodleButtonPressed(isPressed)),
        clearLoadDodle: () => dispatch(ActionCreators.loadDodle(null))
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(DodlesEngineView)