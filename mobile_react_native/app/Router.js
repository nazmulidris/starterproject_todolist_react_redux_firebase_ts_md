// @flow

import React, {Component} from "react";

import {MainView} from "./MainView";
import {FlexboxView1} from "./FlexboxView1";

class Router extends Component {
  render() {
    
    let route = 1;
    
    switch (route) {
      case 0:
        return new MainView().render();
        break;
      case 1:
        return new FlexboxView1().render();
        break;
    }
    
  }
}

export {Router}