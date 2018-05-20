import React, { Component } from 'react';
import { Button, Text, View, StyleSheet, TouchableOpacity } from 'react-native';
import { connect } from 'react-redux';
import {ActionCreators} from '../../actions/index';
import * as Constants from '../../constants'


class Menu extends Component {
    constructor(props) {
        super(props);
    }
    render() {
        return (
            <View style = {styles.container}>
                <TouchableOpacity style = {(Constants.deviceWidth >= 768)?styles.menuIpad:styles.menuPhone}
                                  onPress={() => {
                                      this.props.setGalleryData();
                                      this.props.setGalleryView();
                                      this.props.showHideMenu();
                                  }}>
                    <Constants.DodlesIcon name="Dodles" size= {20} style = {styles.icon}/>
                    <Text style={styles.menuText}>Gallery</Text>
                </TouchableOpacity>
                <TouchableOpacity  style = {(Constants.deviceWidth >= 768)?styles.menuIpad:styles.menuPhone}
                                   onPress={() => {
                                       this.props.deleteButtonPressed();
                                       this.props.setCreateNewView();
                                       this.props.showHideMenu();
                                   }}>
                    <Constants.DodlesIcon name="plus-rounded" size= {20} style = {styles.icon}/>
                    <Text style={styles.menuText}>Create New</Text>
                </TouchableOpacity>
                <TouchableOpacity style = {(Constants.deviceWidth >= 768)?styles.menuIpad:styles.menuPhone}>
                    <Constants.DodlesIcon name="Advanced" size= {20} style = {styles.icon}/>
                    <Text style={styles.menuText}>Settings</Text>
                </TouchableOpacity>
                <TouchableOpacity style = {(Constants.deviceWidth >= 768)?styles.menuIpad:styles.menuPhone}>
                    <Constants.DodlesIcon name="Help" size= {20} style = {styles.icon}/>
                    <Text style={styles.menuText}>Help</Text>
                </TouchableOpacity>
            </View>
        );
    }}

const styles = StyleSheet.create({
    container: {
        height: '50%'
    },
    menuPhone: {
        flexDirection: 'row',
        alignItems: 'center',
        borderBottomColor: '#F3F3F3',
        borderBottomWidth: 2,
        backgroundColor:'#FFFFFF',
        height: '14%'
    },
    menuIpad: {
        flexDirection: 'row',
        alignItems: 'center',
        borderBottomColor: '#F3F3F3',
        borderBottomWidth: 2,
        backgroundColor:'#FFFFFF',
        height: '10%',
    },
    icon:{
        marginBottom: 10,
        marginLeft: 10,
        marginTop: 10,
        color: '#14B0D8',
    },
    menuText: {
        marginLeft: 30,
        fontSize: 18,
        color: '#14B0D8',
        fontWeight: 'bold'
    }
})




function mapStateToProps (state) {
    return {
        menu: state.menu,
    }
}
function mapDispatchToProps (dispatch) {
    return {
        deleteDodleButtonPressed: (isPressed) => dispatch(ActionCreators.deleteDodleButtonPressed(isPressed)),
        showHideMenu: () => dispatch(ActionCreators.showHideMenu())
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Menu)
