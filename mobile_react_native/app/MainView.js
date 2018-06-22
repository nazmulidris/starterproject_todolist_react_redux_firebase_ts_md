/*
 * Copyright 2018 Nazmul Idris All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
  componentWillMount() {
    console.log(`MainView component will mount`);
  }
  
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