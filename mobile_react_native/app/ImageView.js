// @flow

import React, {Component} from 'react';

import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Image
} from 'react-native';

import type {ImageType} from '../flow/TypeAliases';
import {MyClass} from '../flow/TestFile';

export default class ImageView extends Component {
  render() {
    
    let {uri, width, height} = this.props;
    
    const styles = StyleSheet.create({
                                       imageSize: {
                                         width: width,
                                         height: height,
                                       },
                                       padding: {
                                         marginTop: 16,
                                       },
                                     });
  
    return (
      <Image source={{uri}} style={[styles.imageSize, styles.padding]}/>
    );
    
  }
}

ImageView.propTypes = {
  uri: React.PropTypes.string.isRequired,
  width: React.PropTypes.number.isRequired,
  height: React.PropTypes.number.isRequired,
};