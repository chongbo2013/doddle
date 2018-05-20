import React, { Component, } from 'react';
import {
    View,
    FlatList,
    StyleSheet,
    TouchableOpacity
} from 'react-native';
import { connect } from 'react-redux';
import GalleryImage from '../GalleryImageComponent/GalleryImage';
import * as Constants from '../../constants';
import { ActionCreators } from '../../actions/index'




class Gallery extends Component {
    constructor(props) {
        super(props);
        this.noOfDodles = this.noOfDodles.bind(this);
    }

    noOfDodles() {
        return (Constants.deviceWidth >= 768) ? 2 : 1;
    }

    render() {
        return (
            <View style={galleryStyles.container}>
                <View style={galleryStyles.recentImages}>
                    <View style={(Constants.deviceWidth >= 768) ? galleryStyles.iconLayoutIpad : galleryStyles.iconLayout}>
                        <TouchableOpacity onPress={() => {
                            this.props.setCreateNewView();
                            this.props.deleteButtonPressed();
                        }}>
                            <Constants.DodlesIcon style={(Constants.deviceWidth >= 768) ? galleryStyles.iconIpad : galleryStyles.icon} name="plus-rounded" />
                        </TouchableOpacity>
                    </View>
                    <View style={galleryStyles.largeDoodle}>
                        {(() => {
                            let noOfRecentDodles = this.noOfDodles();
                            let listOfDodles = this.props.appData.galleryData.slice(0, noOfRecentDodles).map((prop, key) => {
                                return (<GalleryImage
                                    dodle_id={prop.name.replace(".png", "")}
                                    source={prop.path}
                                    title={'Autosaved'}
                                    time={prop.mtime}
                                    isLarge={true}
                                    titleUnchanged={prop.name}
                                />
                                )
                            });
                            return listOfDodles;
                        })()}
                    </View>
                </View>
                <View>
                    <FlatList
                        numColumns={Constants.deviceWidth >= 768 ? 4 : 3}
                        data={this.props.appData.galleryData.slice(this.noOfDodles(), this.props.appData.galleryData.length)}
                        keyExtractor={item => item.name}
                        renderItem={({ item }) => <GalleryImage
                            dodle_id={item.name.replace(".png", "")}
                            source={item.path}
                            title={item.name}
                            time={item.mtime}
                            titleUnchanged={item.name}
                            isLarge={false} />
                        } />
                </View>
            </View>



        );
    }

}

let galleryStyles = StyleSheet.create({
    container: {
        width: Constants.deviceWidth,
        height: Constants.deviceHeight,
        backgroundColor: '#FFFFFF',
        zIndex: -1
    },
    recentImages: {
        flexDirection: 'row',
    },
    iconLayout: {
        backgroundColor: '#f2f2f2',
        width: (Constants.deviceWidth / 2) - 30,
        height: Constants.deviceHeight / 4 + 10,
        borderRadius: 2,
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        marginTop: 5,
        marginLeft: 5,
    },
    iconLayoutIpad: {
        marginTop: 5,
        marginLeft: 5,
        width: (Constants.deviceWidth / 4) + 30,
        height: (Constants.deviceHeight / 4) + 3,
        backgroundColor: '#f2f2f2',
        borderRadius: 2,
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        flexDirection: 'row'
    },
    largeDoodle: {
        flexDirection: 'row',
        marginTop: 5,
        marginLeft: 2
    },
    icon: {
        fontSize: (Constants.deviceWidth / 4) / 2,
        padding: (Constants.deviceHeight / 4) / 2 - 25,
        color: '#14B0D8',
    },
    iconIpad: {
        fontSize: (Constants.deviceWidth / 4) / 3 + 20,
        padding: (Constants.deviceHeight / 4) / 3 - 12,
        color: '#14B0D8',
    },
});

function mapStateToProps(state) {
    return {

    }
}

function mapDispatchToProps(dispatch) {
    return {
        deleteDodleButtonPressed: (isPressed) => dispatch(ActionCreators.deleteDodleButtonPressed(isPressed)),
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Gallery)