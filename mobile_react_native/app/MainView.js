// @flow

import React, {Component} from "react";
import {Text, View} from "react-native";

import type {ImageType} from "../flow/TypeAliases";
import {ImageView} from "../app/ImageView";
import {mainViewStyles} from "../styles/Styles";

const imageUrl: ImageType = {
  uri: 'https://upload.wikimedia.org/wikipedia/commons/d/de/Bananavarieties.jpg',
  alt_text: 'banana'
};

class MainView extends Component {
  render() {
    
    return (
      <View style={mainViewStyles.container}>
        <Text style={mainViewStyles.welcome}>
          Welcome to R3BL RN!
        </Text>
        <Text style={mainViewStyles.instructions}>
          To get started, edit index.android.js
        </Text>
        <Text style={mainViewStyles.instructions}>
          Double tap R on your keyboard to reload,{'\n'}
          Shake or press menu button for dev menu
        </Text>
        <ImageView uri={imageUrl.uri} height={110} width={193}/>
      </View>
    );
  }
}

export {MainView};