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

import {MainView} from "./MainView";
import {FlexboxView1} from "./FlexboxView1";
import {FlexboxView2} from "./FlexboxView2";

class Router extends Component {
  componentWillMount() {
    console.log(`Router component will mount`);
  }
  
  render() {
  
    let route = 2;

    switch (route) {
      case 0:
        return (<MainView/>);
      case 1:
        return (<FlexboxView1/>);
      case 2:
        return (<FlexboxView2/>);
    }
  
  }// end render()
  
}// end Router component

export {Router}