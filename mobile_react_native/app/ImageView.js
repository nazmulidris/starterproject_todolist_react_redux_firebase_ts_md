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
import {imageStyles} from '../styles/Styles';

class ImageView extends Component {
  render() {
    
    let {uri, width, height} = this.props;
    
    const styles = imageStyles(width, height);
    
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

export {ImageView};