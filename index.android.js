import React from 'react'
import {
    AppRegistry
} from 'react-native'

import { Provider } from 'react-redux'
import configureStore from './react/configureStore'
import App from './react/app'

const store = configureStore()

const ReduxApp = () => (
    <Provider store={store}>
        <App />
    </Provider>
)

AppRegistry.registerComponent('mainactivity', () => ReduxApp)