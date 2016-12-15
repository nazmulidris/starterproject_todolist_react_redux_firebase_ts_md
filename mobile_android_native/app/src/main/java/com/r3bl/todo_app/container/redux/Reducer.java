package com.r3bl.todo_app.container.redux;

import com.brianegan.bansa.Action;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.state.Data;
import com.r3bl.todo_app.container.redux.state.State;

/**
 * Created by nazmul on 11/8/16.
 */
public class Reducer implements com.brianegan.bansa.Reducer<State> {

private final App ctx;

public Reducer(App myApplication) {
  this.ctx = myApplication;
}

@Override
public State reduce(State state, Action actionParam) {

  try {

    State newState = state.deepCopy();

    if (actionParam instanceof Actions.AddTodoItem) {
      if (newState.data == null) newState.data = new Data();
      Actions.AddTodoItem action = (Actions.AddTodoItem) actionParam;
      newState.data.todoArray.add(action.getParam());
    } else if (actionParam instanceof Actions.SetUser) {
      Actions.SetUser action = (Actions.SetUser) actionParam;
      newState.user = action.getParam();
    } else if (actionParam instanceof Actions.SetData) {
      Actions.SetData action = (Actions.SetData) actionParam;
      newState.data = action.getParam();
    } else if (actionParam instanceof Actions.RestoreState) {
      Actions.RestoreState action = (Actions.RestoreState) actionParam;
      newState = action.getParam();
    }

    ctx.getReduxLog().push(ctx.getTime(), state, actionParam, newState);

    App.log("Reducer", "applying action: " + actionParam.getClass().getSimpleName());
    try {
      App.log("Reducer", "state diff: ", App.diff(state.toString(), newState.toString()));
    } catch (Exception e) {
      App.log("Reducer", "reduce: old state: " + state);
      App.log("Reducer", "reduce: new state: " + newState);
    }

    return newState;

  } catch (Exception e) {
    App.logErr("Reducer", "reduce: problem running reducer", e);
  }
  return state;
}

}// end class ApplicationReducer