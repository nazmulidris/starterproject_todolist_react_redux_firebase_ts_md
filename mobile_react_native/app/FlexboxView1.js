// @flow

import React, {Component} from "react";
import {Text, View} from "react-native";

import {flexboxViewStyles} from "../styles/Styles";

class FlexboxView1 extends Component {
  render() {
    let msg1: string = "Flexbox View";
    let msg2: string = "Details Text";
    
    return (
      <View style={flexboxViewStyles.container}>
        <Text style={flexboxViewStyles.text1}>{msg1}</Text>
        <Text style={flexboxViewStyles.text2}>{msg2}</Text>
      </View>
    );
  }
}

export {FlexboxView1};