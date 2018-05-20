import React, { Component } from 'react';
import {
    AppRegistry,
    View,
    Image,
    StyleSheet,
    Platform,
    TouchableOpacity
} from 'react-native';
import { connect } from 'react-redux'
import {ActionCreators} from '../../actions/index'
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import * as Constants from '../../constants'

//finds what platform is being used and uses certain padding
const PADDING_TOP = Platform.OS === 'ios' ? 20 : 15;
const hamburgerSize = Constants.headerHeight - PADDING_TOP;
const okImageHeight = Constants.headerHeight * 1.1;
const okImageWidth = 30/50 * okImageHeight;


export const initialState = {
    slideOpen : false
};

class Header extends Component
{
    constructor(props) {
        super(props);
       this.state = initialState;
    }

    headerContainerStyle(){
        return({
            flex: 0,
            backgroundColor: '#F3F3F3',
            flexDirection: 'row',
            margin: 0,
            padding: 0,
            height: Constants.headerHeight,
        })
    }

    logoStyle(){
        let logoHeight = Constants.headerHeight - 20;
        let logoWidth = (358/133) * logoHeight;
        return({
            height: logoHeight,
            width: logoWidth,
            alignSelf: 'center',
        })
    }

    renderOkayCancelButtons() {
        if (!this.props.appData.galleryView) {
            return (
                <View>
                    <View style={headerStyles.cancelButtonView}>
                        <TouchableOpacity
                            onPress={() => {this.props.cancelButtonPressed(true)}}>
                            {this.renderCancelButtonDisplay()}
                        </TouchableOpacity>
                    </View>
                    <View style={headerStyles.okayButtonView}>
                        <TouchableOpacity
                            onPress={() => {this.props.okButtonPressed(true)}}>
                            {this.renderOkayButtonDisplay()}
                        </TouchableOpacity>
                    </View>
                </View>
            )
        }
    }

    renderCancelButtonDisplay() {
        if (this.props.header.showCancel) {
            return (
                <Image
                    style={headerStyles.cancelButtonImage}
                    source={require('./images/cancel.png')}/>
            );
        }
    }

    renderOkayButtonDisplay(){
        if (this.props.header.showOkay) {

            var okayButtonIcon = this.props.header.iconType;

            switch(this.props.header.okayCount){
                case 0 :
                    return (
                        <Image
                            style={headerStyles.okayButtonImage}
                            source={require('./images/ok0.png')} >
                            <Constants.DodlesIcon name={okayButtonIcon} style={headerStyles.okayButtonIcon} />
                        </Image>
                    );
                    break;
                case 1 :
                    return (
                            <Image
                                style={headerStyles.okayButtonImage}
                                source={require('./images/ok1.png')} >
                                <Constants.DodlesIcon name={okayButtonIcon} style={headerStyles.okayButtonIcon} />
                            </Image>
                    );
                    break;
                case 2 :
                    return (
                        <Image
                            style={headerStyles.okayButtonImage}
                            source={require('./images/ok2.png')} >
                            <Constants.DodlesIcon name={okayButtonIcon} style={headerStyles.okayButtonIcon} />
                        </Image>
                    );
                    break;
                case 3 :
                    return (
                        <Image
                            style={headerStyles.okayButtonImage}
                            source={require('./images/ok3.png')} >
                            <Constants.DodlesIcon name={okayButtonIcon} style={headerStyles.okayButtonIcon} />
                        </Image>
                    );
                    break;
                case 4 :
                    return (
                        <Image
                            style={headerStyles.okayButtonImage}
                            source={require('./images/ok4.png')} >
                            <Constants.DodlesIcon name={okayButtonIcon} style={headerStyles.okayButtonIcon} />
                        </Image>
                    );
                    break;
                default :
                    // assumes okayCount >= 5
                    return (
                        <Image
                            style={headerStyles.okayButtonImage}
                            source={require('./images/ok5.png')} >
                            <Constants.DodlesIcon name={okayButtonIcon} style={headerStyles.okayButtonIcon} />
                        </Image>
                    );
                    break;
            }
        }
    }

    render()
    {
        return (
            <View style={this.headerContainerStyle()}>
                <View style={headerStyles.menuView}>
                    <TouchableOpacity
                        style={headerStyles.menu}
                       onPress={() => {
                            if(!this.state.slideOpen){
                                this.setState({slideOpen: true});

                            } else {
                                this.setState({slideOpen: false});
                            }
                                this.props.saveDodleButtonPressed();
                                this.props.showHideMenu();

                            }}>
                        <Icon name="menu"
                            size={hamburgerSize}
                            color="#7C7C7C"/>
                    </TouchableOpacity>
                </View>
                <View style={headerStyles.logoView}>
                    <Image
                        source={require('./images/logo.png')}
                        style={this.logoStyle()}
                    />
                </View>
                <View style={headerStyles.rightSpacer}>
                </View>
                {this.renderOkayCancelButtons()}
            </View>
        );
    }
}

var headerStyles = StyleSheet.create({
    container:{
        flex: 1,
        margin: 0,
        height: 60,
    },
    header:{
        backgroundColor: '#F3F3F3',
        flexDirection: 'row',
        padding: 0,
    },
    menuView: {
        flex: 2,
        paddingLeft: PADDING_TOP,
        justifyContent: 'center',
        alignItems: 'center',
    },
    menu: {
        alignSelf: 'flex-start',
    },
    logoView: {
        flex:5,
        alignSelf: 'center',
        justifyContent: 'center',
        alignItems: 'center',
    },
    rightSpacer: {
        flex: 2,
    },
    cancelButtonView: {
        position: 'absolute',
        top: 0,
        right: okImageWidth + 40,
        height: okImageHeight,
        width: okImageWidth,
        padding: 0,
        zIndex:105,
    },
    cancelButtonImage: {
        height: okImageHeight,
        width: okImageWidth,
        zIndex: 110,
    },
    okayButtonView: {
        position: 'absolute',
        top: 0,
        right: 20,
        height: okImageHeight,
        width: okImageWidth,
        padding: 0,
        zIndex:105,
    },
    okayButtonImage: {
        height: okImageHeight,
        width: okImageWidth,
        zIndex: 110,
    },
    okayButtonIcon: {
        backgroundColor: 'transparent',
        color: "white",
        textShadowColor: "green",
        textShadowOffset: { height: 2, width: 2 },
        textShadowRadius: 2,
        fontSize: 16,
        position:'absolute',
        left: "35%",
        top: "25%",
        zIndex: 115,
    }
});

function mapStateToProps (state) {
    return {
        appData: state.data,
        header: state.header,
    }
}

function mapDispatchToProps (dispatch) {
    return {
        cancelButtonPressed: (isPressed) => dispatch(ActionCreators.cancelButtonPressed(isPressed)),
        okButtonPressed: (isPressed) => dispatch(ActionCreators.okButtonPressed(isPressed)),
        saveDodleButtonPressed: () => dispatch(ActionCreators.saveDodleButtonPressed(true)),
        showHideMenu: () => dispatch(ActionCreators.showHideMenu()),
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Header)