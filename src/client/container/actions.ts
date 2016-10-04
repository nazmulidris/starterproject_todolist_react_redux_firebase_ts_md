import {UserIF, DataIF, ReduxActionIF} from "./interfaces";

/**
 * these are all the named redux actions
 */
const TYPES = {
  SET_STATE_USER: "SET_STATE_USER",
  SET_STATE_DATA: "SET_STATE_DATA",
  ADD_TODO: "ADD_TODO",
  TOGGLE_TODO: "TOGGLE_TODO",
  INIT_REDUX_STORE: "INIT_REDUX_STORE",
};

function action_init() {
  return {
    type: TYPES.INIT_REDUX_STORE,
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