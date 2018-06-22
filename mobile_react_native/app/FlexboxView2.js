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
import {Image, Text, View} from "react-native";

import {flexboxView2Styles} from "../styles/Styles";

class FlexboxView2 extends Component {
  constructor(props) {
    super(props);
    this.state = {longText: "haven't made fetch call yet"};
  }
  
  componentWillMount() {
    console.log(`FlexboxView2 component will mount`);
    this.getLongText();
  }
  
  render() {
    let msg1: string = "Flexbox View";
    let msg2: string = "Details Text";
    let img = {uri: "http://i.imgur.com/RRUe0Mo.png"};
    
    return (
      <View style={flexboxView2Styles.container}>
        <Text style={flexboxView2Styles.text1}>{msg1}</Text>
        <Text style={flexboxView2Styles.text2}>{msg2}</Text>
        <Image style={flexboxView2Styles.image} source={img}/>
        <Text style={flexboxView2Styles.text3}>{this.state.longText}</Text>
        <Text style={flexboxView2Styles.text3}>{this.state.longText}</Text>
        <Text style={flexboxView2Styles.text3}>{this.state.longText}</Text>
        <Text style={flexboxView2Styles.text3}>{this.state.longText}</Text>
      </View>
    );
  }
  
  getLongText() {
    this.setState({longText: "about to load uri"});
    let numLines: number = 1;
    let uri: string = `https://baconipsum.com/api/?type=meat-and-filler&paras=${numLines}&format=text`;
    
    fetch(uri)
      .then((resp) => {
        resp.text()
            .then((data) => {
              this.setState({longText: data})
            })
      })
      .catch((err) => {
        console.log(`problem getting data from ${uri}, error: ${err}`)
      });
  }
  
}// end FlexBoxView2

export {FlexboxView2};