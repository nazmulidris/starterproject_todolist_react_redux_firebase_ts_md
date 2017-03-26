// @flow

import React, {Component} from 'react';
import {
  StyleSheet,
  Text,
  View,
  Image
} from 'react-native';

import {styles} from '../styles/Styles';

import {MainView} from './MainView';
import {FlexboxView} from './FlexboxView';

class Router extends Component {
  render() {
    
    let route = 1;
    
    switch (route) {
      case 0:
        return new MainView().render();
        break;
      case 1:
        return new FlexboxView().render();
        break;
    }
    
  }
}

export {Router}