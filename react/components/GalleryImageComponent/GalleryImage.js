import React, { Component } from 'react';
import {
    View,
    Text,
    StyleSheet,
    Image,
    TouchableOpacity,
    TouchableWithoutFeedback,
    Modal
} from 'react-native';
import { connect } from 'react-redux';
import moment from 'moment';
import { ActionCreators } from '../../actions/index'
import * as Constants from '../../constants';
import PopMenu from './PopMenu';
import TrashConformation from './TrashConformation'

class GalleryImage extends Component {
    constructor(props) {
        super(props);
        this.titleAndTimeChange = this.titleAndTimeChange.bind(this);
    }

    titleAndTimeChange(title, time, isLarge) {
        let momenttime = moment(time, moment().format('llll')).fromNow();
        let filetime = new Date(time);
        let titleAndTimeObj = {};
        let tempDate = '';
        let tempTitle = '';
        if ((momenttime.search('month') > -1 || momenttime.search('day') > -1 || momenttime.search('hour') > -1 || momenttime.search('min') > -1)) {
            tempDate = `${filetime.getMonth()}/${filetime.getDate()}`;
        } else if (momenttime.search('year') > -1) {
            tempDate = `${filetime.getFullYear()}/${filetime.getMonth()}/${filetime.getDate()}`;
        }

        if (isLarge) {
            tempDate = (momenttime === 'a few seconds ago') ? 'a min ago' : tempDate;
        }

        titleAndTimeObj.timeString = tempDate;
        if ((tempDate.length + title.length) >= 19) {
            tempTitle = title.slice(0, 8).trim();
            tempTitle = tempTitle.concat('...');
        } else {
            tempTitle = title;
        }
        titleAndTimeObj.titleString = tempTitle;
        return titleAndTimeObj;
    }

    onShowPop = () => {
        this.props.showPop();
    }



    render() {
        const {
            dodle_id,
            source,
            title,
            time,
            isLarge,
            titleUnchanged
        } = this.props;
        let titleObj = this.titleAndTimeChange(title, time, isLarge);

        let dataImage = `data:image/png;base64,`;
        if (isLarge === true) {
            return (
                <View>
                    <Modal transparent={true}
                        animationType="none"
                        visible={this.props.showPopMenuFlag}
                        onRequestClose={() => { null }}>
                        <TouchableWithoutFeedback onPress={() => this.props.setShowPopMenu({ flag: false, data: '' })}>
                            <View style={imageStyles.modalStyle}>
                                <PopMenu />
                            </View>
                        </TouchableWithoutFeedback>
                    </Modal>
                    <TouchableOpacity onPress={() => { this.props.loadDodle(dodle_id) }}
                        onLongPress={() => { this.props.setShowPopMenu({ flag: true, data: titleUnchanged }) }}
                    >
                        <View style={imageStyles.largeContainer}>
                            <Image style={(Constants.deviceWidth >= 768) ? imageStyles.imageLargeIpad : imageStyles.imageLarge} source={{ uri: 'file://' + source }} />
                            <View style={imageStyles.box}>
                                <Text style={imageStyles.LargeText}>{titleObj.titleString}</Text>
                                <Text style={imageStyles.datetime}>{titleObj.timeString}</Text>
                            </View>
                        </View>
                    </TouchableOpacity>
                </View>
            );
        } else {
            return (
                <View>
                    <Modal transparent={true}
                        animationType="none"
                        visible={this.props.showPopMenuFlag}
                        onRequestClose={() => { null }}>
                        <TouchableWithoutFeedback onPress={() => this.props.setShowPopMenu({ flag: false, data: '' })}>
                            <View style={imageStyles.modalStyle}>
                                <PopMenu />
                            </View>
                        </TouchableWithoutFeedback>
                    </Modal>
                    <TouchableOpacity onPress={() => {
                        this.props.loadDodle(dodle_id);
                    }}
                        onLongPress={() => { this.props.setShowPopMenu({ flag: true, data: titleUnchanged }) }}
                    >
                        <View style={(Constants.deviceWidth >= 768) ? imageStyles.smallContainerIpad : imageStyles.smallContainer}>
                            <Image style={(Constants.deviceWidth >= 768) ? imageStyles.imageIpad : imageStyles.image} source={{ uri: 'file://' + source }} />
                            <View style={imageStyles.box}>
                                <Text style={imageStyles.text}>{titleObj.titleString}</Text>
                                <Text style={imageStyles.datetime}>{titleObj.timeString}</Text>
                            </View>
                        </View>
                    </TouchableOpacity>
                </View>
            );
        }
    }
}




let imageStyles = StyleSheet.create({
    modalStyle: {
        flex: 1,
        backgroundColor: 'rgba(0,0,0,0.1)',
        alignItems: 'center',
        justifyContent: 'center'
    },
    modalViewStyle: {
        flex: 1,
        backgroundColor: '#000000',
        alignItems: 'center',
        justifyContent: 'center',
        elevation: 6
    },
    largeContainer: {
        borderRadius: 2,
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        shadowRadius: 2,
        elevation: 1,
        marginLeft: 5,
        marginRight: 3,
    },
    smallContainer: {
        borderRadius: 2,
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 1,
        shadowRadius: 0.2,
        elevation: 1,
        marginLeft: 5,
        marginTop: 10,
        marginRight: 3,
        width: (Constants.deviceWidth / 3) - 10,
        height: (Constants.deviceHeight / 3) - 110,
    },
    smallContainerIpad: {
        borderRadius: 2,
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 1,
        shadowRadius: 0.2,
        elevation: 1,
        marginLeft: 5,
        marginTop: 10,
        marginRight: 3,
        width: (Constants.deviceWidth / 4) - 10,
        height: (Constants.deviceHeight / 3) - 110,
    },
    image: {
        width: 80,
        height: '80%'
    },
    imageLarge: {
        width: (Constants.deviceWidth / 2) + 10,
        height: (Constants.deviceHeight / 4) - 20
    },
    imageLargeIpad: {
        width: (Constants.deviceWidth / 3) + 5,
        height: (Constants.deviceHeight / 4) - 20
    },
    imageIpad: {
        width: 80,
        height: '80%'

    },
    box: {
        justifyContent: 'space-between',
        flexDirection: 'row'
    },
    text: {
        fontSize: 12,
        color: '#85929E',
        textAlign: 'left'
    },
    LargeText: {
        fontSize: 12,
        color: '#000000',
        textAlign: 'left',
        fontWeight: 'bold',
        fontStyle: 'italic'
    },
    datetime: {
        fontSize: 12,
        textAlign: 'right',
        color: '#85929E'
    }
});

function mapStateToProps(state) {
    return {
        showPopMenuFlag: state.data.showPopMenuFlag,
    }
}

function mapDispatchToProps(dispatch) {
    return {
        loadDodle: (dodleToLoad) => dispatch(ActionCreators.loadDodle(dodleToLoad)),
        setShowPopMenu: (showPop) => dispatch(ActionCreators.showPopUpMenu(showPop)),

    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(GalleryImage)