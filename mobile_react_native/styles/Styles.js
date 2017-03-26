// @flow

import React, {Component} from 'react';
import {
  StyleSheet,
  Text,
  View,
  Image
} from 'react-native';

const styles = StyleSheet.create(
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

function imageStyles(width: number, height: number) {
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

export {styles, imageStyles};