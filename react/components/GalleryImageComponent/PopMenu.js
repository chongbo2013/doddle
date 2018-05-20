import React, { Component } from 'react';
import { Modal, Text, View, StyleSheet, TouchableOpacity, TouchableWithoutFeedback } from 'react-native';
import * as Constants from '../../constants';
import { connect } from 'react-redux';
import { ActionCreators } from '../../actions/index';
import TrashConformation from './TrashConformation';
import ShareConformation from './ShareDodle';

class PopMenu extends Component {
    constructor() {
        super();

    }
    render() {

        if (this.props.shareConformationFlag) {
            return (
                <ShareConformation />
            )
        }
        else if (this.props.deleteConformationFlag) {
            return (
                <TrashConformation />
            );
        }
        else {
            const newMenuStyle = Constants.deviceWidth > 768 ? styles.menuIpad : styles.menuPhone;
            return (

                <View style={styles.container}>

                    <TouchableOpacity style={newMenuStyle}
                        onPress={() => {
                            this.props.loadDodle(this.props.dodle_id),
                            this.props.setShowPopMenu({ flag: false, data: '' })
                        }}>
                        <View style={styles.icon}><Constants.DodlesIcon name="Dodles" size={20} style={styles.iconStyle} /></View>
                        <View style={styles.textView}>
                            <Text style={styles.menuText}>Edit</Text>
                            <Text style={styles.smallText}>Edit this Dodle’s Drawing & Animation</Text>
                        </View>
                        <View style={styles.iconLines}>
                            <Constants.DodlesIcon name="right-line" size={20} style={styles.iconLine} />
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity style={styles.menuPhone}>
                        <View style={styles.icon}><Constants.DodlesIcon name="update" size={20} style={styles.iconStyle} /></View>
                        <View style={styles.textView}>
                            <Text style={styles.menuText}>Name</Text>
                            <Text style={styles.smallText}>Tap to Update</Text>
                        </View>
                        <View style={styles.iconLines}>
                            <Constants.DodlesIcon name="right-line" size={20} style={styles.iconLine} />
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity style={styles.menuPhone}
                        onPress={() => {
                            this.props.shareConformation(true)
                        }}
                    >
                        <View style={styles.icon}><Constants.DodlesIcon name="Send-V1" size={20} style={styles.iconStyle} /></View>
                        <View style={styles.textView}>
                            <Text style={styles.menuText}>Share</Text>
                            <Text style={styles.smallText}>Share with Friends</Text>
                        </View>
                        <View style={styles.iconLines}>
                            <Constants.DodlesIcon name="right-line" size={20} style={styles.iconLine} />
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity style={styles.menuPhone}
                        onPress={() => {
                            this.props.deleteConformation(true)

                        }}>

                        <View style={styles.icon}><Constants.DodlesIcon name="Trash" size={20} style={styles.iconStyle} /></View>
                        <View style={styles.textView}>
                            <Text style={styles.menuText}>Trash</Text>
                            <Text style={styles.smallText}>Delete this Dodle</Text>
                        </View>
                        <View style={styles.iconLines}>
                            <Constants.DodlesIcon name="right-line" size={20} style={styles.iconLine} />
                        </View>
                    </TouchableOpacity>
                    <TouchableOpacity style={styles.close}
                        onPress={() => {
                            this.props.setShowPopMenu({ flag: false, data: '' })
                        }}>
                        <Text style={styles.text}>Close</Text>
                    </TouchableOpacity>

                </View>
            )
        }

    }
}

const styles = StyleSheet.create({
    modalStyle: {
        flex: 1,
        backgroundColor: 'rgba(0,0,0,0.2)',
        alignItems: 'center',
        justifyContent: 'center'
    },
    container: {
        flexDirection: 'column',
        justifyContent: 'center',
        flex: 1,
        width: Constants.deviceWidth * 0.92,
        height: Constants.deviceHeight * 0.5,
        zIndex: 5,
        position: 'absolute',
        borderRadius: 10

    },
    containerIpad: {
        flexDirection: 'column',
        justifyContent: 'center',
        flex: 1,
        width: Constants.deviceWidth * 0.70,
        height: Constants.deviceHeight * 0.55,
        zIndex: 5,
        position: 'absolute',
        borderRadius: 10

    },
    menuPhone: {
        backgroundColor: '#FFFFFF',
        flexDirection: 'row',
        flex: 0.2,
        justifyContent: 'center',
        alignItems: 'center',
        borderBottomWidth: 1,
        borderBottomColor: '#d3d3d3'
    },
    menuIpad: {
        backgroundColor: '#FFFFFF',
        flexDirection: 'row',
        flex: 0.18,
        justifyContent: 'center',
        alignItems: 'center',
        borderBottomWidth: 1,
        borderBottomColor: '#d3d3d3'
    },
    icon: {
        flex: 0.1,
        alignSelf: 'center',
        alignItems: 'center',
        justifyContent: 'center'


    },
    iconStyle: {
        color: '#14B0D8',
        alignSelf: 'center'
    },
    textView: {
        flex: 0.6,
        justifyContent: 'center',


    },


    menuText: {

        fontSize: Constants.deviceHeight * 0.03,
        color: '#14B0D8',
        fontWeight: 'bold',

    },
    smallText: {
        fontSize: Constants.deviceHeight * 0.01,
        paddingBottom: 2
    },

    close: {
        backgroundColor: '#119ec2',
        flex: 0.15,

    },
    text: {
        color: '#FFFFFF',
        fontSize: 20,
        textAlign: 'center',
        fontWeight: 'bold',
        marginTop: 10,

    },
    iconLines: {
        justifyContent: 'space-around',
        flex: 0.1,
        alignSelf: 'center'
    },
    iconLine: {
        color: '#14B0D8',

    },

})

function mapStateToProps(state) {
    return {
        showPopMenuFlag: state.data.showPopMenuFlag,
        popMenuItemPressed: state.data.popMenuItemPressed,
        clickedImage: state.data.clickedImage,
        deleteConformationFlag: state.tool.deleteConformationFlag,
        shareConformationFlag: state.tool.shareConformationFlag

    }
}

function mapDispatchToProps(dispatch) {
    return {
        loadDodle: (dodleToLoad) => dispatch(ActionCreators.loadDodle(dodleToLoad)),
        setShowPopMenu: (showPop) => dispatch(ActionCreators.showPopUpMenu(showPop)),
        showMenuItem: (showPop) => dispatch(ActionCreators.showPopUpMenuItem(showPop)),
        deleteConformation: (isPressed) => dispatch(ActionCreators.deleteConformation(isPressed)),
        shareConformation: (isPressed) => dispatch(ActionCreators.shareConformation(isPressed))

    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(PopMenu)