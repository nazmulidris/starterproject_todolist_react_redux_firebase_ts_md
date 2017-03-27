// @flow

import React, {Component} from "react";
import {Text, View} from "react-native";

import {flexboxViewStyles} from "../styles/Styles";

class FlexboxView1 extends Component {
  constructor(props) {
    super(props);
    this.state = {longText: "haven't made fetch call yet"};
  }
  
  componentWillMount() {
    console.log(`FlexbovView1 component will mount`);
    this.getLongText();
  }
  
  render() {
    let msg1: string = "Flexbox View";
    let msg2: string = "Details Text";
    
    return (
      <View style={flexboxViewStyles.container}>
        <Text style={flexboxViewStyles.text1}>{msg1}</Text>
        <Text style={flexboxViewStyles.text2}>{msg2}</Text>
        <Text style={flexboxViewStyles.text3}>{this.state.longText}</Text>
      </View>
    );
  }
  
  getLongText() {
    this.setState({longText: "about to load uri"});
    let uri: string = "https://baconipsum.com/api/?type=meat-and-filler&paras=2&format=text";
    let msg: string = "";
    
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
  
}// end FlexBoxView1

export {FlexboxView1};