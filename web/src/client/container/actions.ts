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

import {UserIF, DataIF, ReduxActionIF} from "./interfaces";

/**
 * these are all the named redux actions
 */
const TYPES = {
  SET_STATE_USER: "SET_STATE_USER",
  SET_STATE_DATA: "SET_STATE_DATA",
  ADD_TODO: "ADD_TODO",
  TOGGLE_TODO: "TOGGLE_TODO",
  REDUX_INIT: "REDUX_INIT",
};

function action_init():ReduxActionIF{
  return {
    type: TYPES.REDUX_INIT,
    payload: null,
  }
}

function action_set_state_user(user: UserIF): ReduxActionIF {
  return {
    type: TYPES.SET_STATE_USER,
    payload: user,
  }
}

function action_set_state_data(data: DataIF): ReduxActionIF {
  return {
    type: TYPES.SET_STATE_DATA,
    payload: data,
  }
}

function action_add_todo_text(text: string): ReduxActionIF {
  return {
    type: TYPES.ADD_TODO,
    payload: text,
  }
}

function action_toggle_todo_index(index: number): ReduxActionIF {
  return {
    type: TYPES.TOGGLE_TODO,
    payload: index,
  }
}

export {
  TYPES,
  action_set_state_data,
  action_set_state_user,
  action_toggle_todo_index,
  action_add_todo_text,
  action_init,
}