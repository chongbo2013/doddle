import * as brushActions from './brushActions';
import * as toolActions from './toolActions';
import * as shapeActions from './shapeActions';
import * as layerActions from './layerActions';
import * as animationActions from './animationActions'
import * as headerActions from '../components/HeaderComponent/headerActions';
import * as sideMenuActions from './sideMenuActions';
import * as dataActions from './dataActions';

export const ActionCreators = Object.assign({},
    brushActions, toolActions, shapeActions, headerActions, layerActions, animationActions, sideMenuActions, dataActions
);
