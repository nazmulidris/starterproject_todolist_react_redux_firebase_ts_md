/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, {Component} from "react";
import {AppRegistry, StyleSheet, Text, View} from "react-native";

export default class mobile_react_native extends Component {
  render() {
    return (
      <View style={myStyles.container}>
        <Text style={myStyles.welcome}>
          Welcome to React Native!
        </Text>
        <Text style={myStyles.instructions}>
          To get started, edit index.ios.js
        </Text>
        <Text style={myStyles.instructions}>
          Press Cmd+R to reload,{'\n'}
          Cmd+D or shake for dev menu
        </Text>
      </View>
    );
  }
}

const myStyles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('mobile_react_native', () => mobile_react_native);
