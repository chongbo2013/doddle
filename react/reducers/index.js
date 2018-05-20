import { combineReducers } from 'redux'
import tool from './toolReducer'
import brush from './brushReducer'
import shape from './shapeReducer'
import layer from './layerReducer'
import animation from './animationReducer'
import header from '../components/HeaderComponent/headerReducer'
import sideMenu from './sideMenuReducer'
import data from './dataReducer'

const rootReducer = combineReducers({ tool, brush, shape, header, layer, animation, sideMenu, data })

export default rootReducer
