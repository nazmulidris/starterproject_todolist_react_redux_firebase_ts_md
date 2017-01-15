/// <reference path="../../../typings/globals/node/index.d.ts" />

/**
 * These are all the redux reducer functions and initial state
 */

import {combineReducers} from 'redux';
import * as actions from "./actions";
import {applicationContext} from "./context";
import {ReduxStateIF, ReduxActionIF, DataIF, TodoIF} from "./interfaces";

const lodash = require('lodash');

/**
 * these are initialized with null to match Firebase, since it doens't store any
 * empty values and set empty values to null
 * @type {{user: any; data: any}}
 */
const initialState: ReduxStateIF = {
  user: null, // store user object in here once auth is complete
  data: null, // store data object in here for user
};

function set_data(state: ReduxStateIF, action: ReduxActionIF) {
  const retval = {
    data: action.payload,
    user: state.user,
  };
  return retval;
}

function set_user(state: ReduxStateIF, action: ReduxActionIF) {
  const retval = {
    data: state.data,
    user: action.payload,
  };
  return retval;
}

function reducer_main(state: ReduxStateIF,
                      action: ReduxActionIF): ReduxStateIF {
  
  switch (action.type) {
    case actions.TYPES.REDUX_INIT:
      return initialState;
    case actions.TYPES.SET_STATE_DATA:
      return set_data(state, action);
    case actions.TYPES.SET_STATE_USER:
      return set_user(state, action);
  }
  
}

export {
  reducer_main,
  initialState,
}