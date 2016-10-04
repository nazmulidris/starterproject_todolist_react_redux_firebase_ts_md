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

function add_todo(state: ReduxStateIF,
                  action: ReduxActionIF) {
  
  const todo_text: string = action.payload;
  
  if (!lodash.isNil(todo_text)) {
    
    let data_copy: DataIF = lodash.cloneDeep(state.data);
    
    let todoObject: TodoIF = {
      item: todo_text,
      done: false,
    };
    
    if (lodash.isNil(data_copy)) {
      data_copy = {todoArray: [todoObject]};
    } else {
      data_copy.todoArray.push(todoObject);
    }
    
    const retval = {
      user: state.user,
      data: data_copy,
    }
    return retval;
    
  } else {
    return state;
  }
  
}

function toggle_todo(state: ReduxStateIF,
                     action: ReduxActionIF) {
  
  try {
    
    const index: number = action.payload;
    let data_copy: DataIF = lodash.cloneDeep(applicationContext.getData());
    let todoObject: TodoIF = data_copy.todoArray[index];
    todoObject.done = !todoObject.done;
    
    const retval =  {
      user: state.user,
      data: data_copy,
    }
    return retval;
    
  } catch (e) {
    console.log("_modifyTodoItem had a problem ...");
    console.dir(e);
  }
  
  return state;
  
}

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
    case actions.TYPES.ADD_TODO:
      return add_todo(state, action);
    case actions.TYPES.TOGGLE_TODO:
      return toggle_todo(state, action);
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