package com.r3bl.todo_app.container.redux;

import com.brianegan.bansa.Action;
import com.brianegan.bansa.Middleware;
import com.brianegan.bansa.NextDispatcher;
import com.brianegan.bansa.Store;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.state.Data;
import com.r3bl.todo_app.container.redux.state.State;

/**
 * Created by nazmul on 1/14/17.
 */
public class StateMiddleware implements Middleware<State> {
private final App _ctx;

public StateMiddleware(App app) {
  this._ctx = app;
}

@Override
public void dispatch(Store<State> store, Action actionParam, NextDispatcher next) {

  try {

    State newState = store.getState().deepCopy();

    if (actionParam instanceof Actions.AddTodoItem) {

      if (newState.data == null) newState.data = new Data();
      Actions.AddTodoItem action = (Actions.AddTodoItem) actionParam;
      newState.data.todoArray.add(action.getParam());
      _ctx.getDatabase().saveUserDataToFirebase(newState.data);

    } else if (actionParam instanceof Actions.ToggleTodoItem) {

      Actions.ToggleTodoItem action = (Actions.ToggleTodoItem) actionParam;
      int index = action.getParam();
      newState.data.todoArray.get(index).done = !newState.data.todoArray.get(index).done;
      _ctx.getDatabase().saveUserDataToFirebase(newState.data);

    }

  } catch (Exception e) {
    App.logErr("StateMiddleware", "problem with dispatch()", e);
  }

  next.dispatch(actionParam);

}

}// end StateMiddleware