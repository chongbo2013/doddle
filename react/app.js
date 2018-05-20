import React, {Component} from 'react'
import { TouchableHighlight,Modal, TouchableWithoutFeedback, TouchableOpacity,
 View, Text, StyleSheet, Dimensions, Platform, StatusBar, LayoutAnimation } from 'react-native'
import DodlesEngineView from './components/DodlesEngineView/DodlesEngineView';
import { connect } from 'react-redux'
import {ActionCreators} from './actions/index'
import Header from './components/HeaderComponent/header';
import Gallery  from './components/GalleryComponent/Gallery';
import Menu from './components/Menu/Menu';
import Tools from './components/tools/tools';
import * as Constants from './constants';
import rnfs from 'react-native-fs';



class App extends Component {

    constructor(props) {
        super(props);
    }

    componentWillMount() {
        // ensure the <SAVE_DIR>/files directory exists
        rnfs.mkdir(Constants.DODLE_SAVE_DIRECTORY);
    }

    render() {
        return (
               <View style={this.props.header.showMenu ? styles.engineMenuContainerShowMenu : styles.engineMenuContainerHideMenu }>
                         <StatusBar hidden={true} animated={false}></StatusBar>
                         <View style={styles.menuSlideIn}>
                            <Modal
                               animationType="none"
                               transparent={true}
                               visible={this.props.header.showMenu}
                               >
                               <TouchableWithoutFeedback onPress={() => {
                                    this.props.showHideMenu();
                               }}>
                               <View style={styles.modalStyle} >
                                <TouchableOpacity style={styles.insideModalView} onPress={() => null}>
                                 <Menu {...this.props}/>
                                </TouchableOpacity>
                               </View>
                               </TouchableWithoutFeedback>

                         </Modal>
                         </View>
                         <View style={styles.appContainer}>
                             <View style={styles.headerView}>
                                 <Header/>
                             </View>
                             {(()=>{
                                 if(this.props.appData.galleryView){
                                     return(
                                         <View>
                                             <Gallery {...this.props}/>
                                         </View>
                                     );
                                 }
                             })()}
                             <View style={styles.dodlesEngineHolder}>
                                    <DodlesEngineView style={styles.dodlesEngineView}></DodlesEngineView>
                             </View>
                             <View style={styles.toolArea}>
                                     <Tools/>
                             </View>
                       </View>
                     </View>
                 )
             }
            }

styles = StyleSheet.create({
    modalStyle: {
        flex: 1,
        elevation: 4,


    },
    insideModalView: {
        width: Constants.MENU_WIDTH,
        backgroundColor: '#f3f3f3',
        flex: 1,
    },
    engineMenuContainerHideMenu: {
        flexDirection: 'row',
        alignSelf: 'stretch',
        margin: 0,
        padding: 0,
        width: Constants.deviceWidth + Constants.MENU_WIDTH,
        marginLeft: Constants.MENU_WIDTH * -1,
        backgroundColor: '#F3F3F3',
    },
    engineMenuContainerShowMenu: {
        flexDirection: 'row',
        alignSelf: 'stretch',
        margin: 0,
        padding: 0,
        width: Constants.deviceWidth + Constants.MENU_WIDTH,
        marginLeft: 0,
        backgroundColor: '#F3F3F3',
    },
    menuSlideIn: {
        flex: 1,
        width: Constants.MENU_WIDTH,
        backgroundColor: '#F3F3F3'
    },
    appContainer: {
        flex : 0,
        paddingTop: 0,
        margin: 0,
        flexDirection: 'column',
        backgroundColor: '#ecebec',
        height: Constants.deviceHeight,
        width: Constants.deviceWidth,
    },
    headerView: {
        flex: 0 ,
        padding: 0,
        height: Constants.headerHeight,
        width: Constants.deviceWidth,
        zIndex:1
    },
    dodlesEngineHolder: {
        flex: 0,
        margin: 0,
        padding: 0,
        height: Constants.dodleEngineHeight,
        width: Constants.deviceWidth,
        zIndex:0
    },
    dodlesEngineView: {
        flex: 0,
        height: Constants.dodleEngineHeight,
        width: Constants.deviceWidth,
        margin: 0,
        padding: 0,
    },
    toolArea: {
        flex:1,
        flexDirection: 'row',
        margin: 0,
        padding:0,
        height: Constants.toolAreaHeight,
    },
})

function mapStateToProps (state) {
    return {
        appData: state.data,
        toolData: state.tool,
        brushData: state.brush,
        header: state.header,
        menu: state.tempMenu,
        gallery: state.gallery
    }
}

function mapDispatchToProps (dispatch) {
    return {
        fetchData: () => dispatch(ActionCreators.fetchData()),
        setBrushSize: (brushsize) => dispatch(ActionCreators.setBrushSize(brushsize)),
        setBrushOpacity: (brushopacity) => dispatch(ActionCreators.setBrushOpacity(brushopacity)),
        setBrushColor: (brushcolor) => dispatch(ActionCreators.setBrushColor(brushcolor)),
        resetBrushSubmenu: () => dispatch(ActionCreators.resetBrushSubmenu()),
        resetShapeSubmenu: () => dispatch(ActionCreators.resetShapeSubmenu()),
        setGalleryView: () => dispatch(ActionCreators.setGalleryView(true)),
        setGalleryData: () => dispatch(ActionCreators.setGalleryData()),
        setCreateNewView: () => dispatch(ActionCreators.setCreateNewView(false)),
        deleteButtonPressed: () => dispatch(ActionCreators.deleteButtonPressed(true)),
        showHideMenu: () => dispatch(ActionCreators.showHideMenu()),
        saveDodleButtonPressed: () => dispatch(ActionCreators.saveDodleButtonPressed(isPressed)),
    }
}

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(App)