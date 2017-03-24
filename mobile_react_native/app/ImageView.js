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
    return (
      <Image source={this.props.imageObject}
             style={{
               width: 193,
               height: 110
             }}/>
    );
  }
}

ImageView.propTypes = {
  imageObject: React.PropTypes.object.isRequired
};