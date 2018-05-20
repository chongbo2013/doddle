import * as Constants from '../constants'

export function setActiveShape(shapename) {
    return  {
        type: Constants.SET_ACTIVE_SHAPE,
        shapename,
    }
}

export function setShapeOpacity(shapeopacity) {
    return  {
        type: Constants.SET_SHAPE_OPACITY,
        shapeopacity,
    }
}

export function setShapeSize(shapesize) {
    return  {
        type: Constants.SET_SHAPE_SIZE,
        shapesize,
    }
}

export function setActiveShapeSubmenu(submenu) {
    return  {
        type: Constants.SET_ACTIVE_SHAPE_SUBMENU,
        submenu,
    }
}

export function resetShapeSubmenu() {
    return  {
        type: Constants.RESET_SHAPE_SUBMENU
    }
}

export function setShapeColor(shapecolor) {
    return {
        type: Constants.SET_SHAPE_COLOR,
        shapecolor,
    }
}
export function setShapeOldColor(shapeOldColor) {
    return {
        type: Constants.SET_SHAPE_OLD_COLOR,
        shapeOldColor,
    }
}
export function setShapeStarDepth (depth) {
    return {
        type: Constants.SET_SHAPE_STAR_DEPTH,
        depth,
    }
}

export function setShapeCorner(shapecorner) {
    return {
        type: Constants.SET_SHAPE_CORNER,
        shapecorner,
    }
}

export function setShapeRounding(shaperounding) {
    return {
        type: Constants.SET_SHAPE_ROUNDING,
        shaperounding,
    }
}

export function setCustomShapeStatus (value) {
    return {
        type: Constants.SET_CUSTOMIZING_SHAPE_STATUS,
        value,
    }
}
export function shapeToolSelectorPressed() {
    return {
        type: Constants.SHAPE_TOOL_SELECTOR_PRESSED
    }
}