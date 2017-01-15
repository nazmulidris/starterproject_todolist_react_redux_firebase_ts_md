/// <reference path="../../../typings/globals/node/index.d.ts" />

import * as actions from "./actions";
import {applicationContext} from "./context";
import {ReduxStateIF, ReduxActionIF, DataIF, TodoIF} from "./interfaces";
import {writeStateToFirebase} from "./firebase";

const lodash = require('lodash');

const add_todo_item = store => next => action => {
  if (action.type !== actions.TYPES.ADD_TODO) return next(action);
  add_todo(action);
};

const toggle_todo_item = store => next => action => {
  if (action.type !== actions.TYPES.TOGGLE_TODO) return next(action);
  toggle_todo(action);
};

function add_todo(action: ReduxActionIF) {
  
  const todo_text: string = action.payload;
  
  if (!lodash.isNil(todo_text)) {
    
    let data_copy: DataIF = lodash.cloneDeep(applicationContext.getData());
    
    let todoObject: TodoIF = {
      item: todo_text,
      done: false,
    };
    
    if (lodash.isNil(data_copy)) {
      data_copy = {todoArray: [todoObject]};
    } else {
      data_copy.todoArray.push(todoObject);
    }
    
    writeStateToFirebase(applicationContext, data_copy);
    
  }
  
}

function toggle_todo(action: ReduxActionIF) {
  
  try {
    
    const index: number = action.payload;
    let data_copy: DataIF = lodash.cloneDeep(applicationContext.getData());
    let todoObject: TodoIF = data_copy.todoArray[index];
    todoObject.done = !todoObject.done;
    
    writeStateToFirebase(applicationContext, data_copy);
    
  } catch (e) {
    console.log("_modifyTodoItem had a problem ...");
    console.dir(e);
  }
  
}

export {add_todo_item, toggle_todo_item}