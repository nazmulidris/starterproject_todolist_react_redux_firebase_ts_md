// @flow

import React from "react";
import {StyleSheet} from "react-native";

// fonts available - https://github.com/dabit3/react-native-fonts
const flexboxViewStyles = StyleSheet.create(
  {
    container: {
      flex: 1,
      backgroundColor: '#8ed1fc'
    },
    text1: {
      fontSize: 20,
      fontWeight: '500',
      fontFamily: 'monospace',
      flex: 1,
      backgroundColor: '#00d084',
      margin: 16,
      padding: 16,
    },
    text2: {
      fontSize: 12,
      fontWeight: '200',
      fontFamily: 'monospace',
      flex: 4,
      color: '#c4def6',
      backgroundColor: '#1273de',
      margin: 16,
      padding: 16,
    }
  }
);

const mainViewStyles = StyleSheet.create(
  {
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

function imageViewStyles(width: number, height: number) {
  return StyleSheet.create(
    {
      imageSize: {
        width: width,
        height: height,
      },
      padding: {
        marginTop: 16,
      },
    });
}

export {mainViewStyles, imageViewStyles, flexboxViewStyles};