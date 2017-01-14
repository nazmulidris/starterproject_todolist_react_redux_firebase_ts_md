package com.r3bl.todo_app.container.redux;

import com.brianegan.bansa.Action;
import com.brianegan.bansa.Middleware;
import com.brianegan.bansa.NextDispatcher;
import com.brianegan.bansa.Store;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.state.State;

/**
 * Created by nazmul on 1/14/17.
 */
public class StateMiddleware implements Middleware<State> {
  @Override
  public void dispatch(Store<State> store, Action action, NextDispatcher next) {
    App.log("Middleware [START]", "");
    App.log("Middleware [state]", store.getState().toString());
    App.log("Middleware [action]", action.toString());
    App.log("Middleware [END]", "");
    next.dispatch(action);
  }
}
