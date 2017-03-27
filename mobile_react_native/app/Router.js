// @flow

import React, {Component} from "react";

import {MainView} from "./MainView";
import {FlexboxView1} from "./FlexboxView1";

class Router extends Component {
  componentWillMount() {
    console.log(`Router component will mount`);
  }
  
  render() {

    let route = 1;

    switch (route) {
      case 0:
        return (<MainView/>);
      case 1:
        return (<FlexboxView1/>);
    }
  
  }// end render()
  
}// end Router component

export {Router}