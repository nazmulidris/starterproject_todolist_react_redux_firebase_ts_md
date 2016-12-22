package com.r3bl.todo_app.container.redux;

import com.brianegan.bansa.Action;
import com.r3bl.todo_app.container.redux.state.Data;
import com.r3bl.todo_app.container.redux.state.State;
import com.r3bl.todo_app.container.redux.state.TodoItem;
import com.r3bl.todo_app.container.redux.state.User;

/**
 * Created by nazmul on 11/10/16.
 */

public class Actions {

// This extends the Redux action interface marker
public interface MyAction<T> extends Action {
  public T getParam();
}

// AddTodoItem action class
public static class AddTodoItem implements MyAction<TodoItem> {
  private final TodoItem todoItem;

  public AddTodoItem(String item, boolean done) {
    this.todoItem = new TodoItem(item, done);
  }

  @Override
  public TodoItem getParam() {
    return this.todoItem;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("AddTodoItem{");
    sb.append("todoItem=").append(todoItem);
    sb.append('}');
    return sb.toString();
  }
}

// SetUser action class
public static class SetUser implements MyAction<User> {
  private final User userObject;

  public SetUser(User userObject) {
    this.userObject = userObject;
  }

  @Override
  public User getParam() {
    return this.userObject;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("SetUser{");
    sb.append("userObject=").append(userObject);
    sb.append('}');
    return sb.toString();
  }
}

// SetData action class
public static class SetData implements MyAction<Data> {
  private final Data dataObject;

  public SetData(Data dataObject) {
    this.dataObject = dataObject;
  }

  @Override
  public Data getParam() {
    return this.dataObject;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("SetData{");
    sb.append("dataObject=").append(dataObject);
    sb.append('}');
    return sb.toString();
  }
}

// ResetState action class
public static class ResetState implements MyAction<Data> {
  public ResetState() {
  }

  @Override
  public Data getParam() {
    return null;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("ResetState{");
    sb.append('}');
    return sb.toString();
  }
}

// RestoreState action class
public static class RestoreState implements MyAction<State> {
  private final State stateObject;

  public RestoreState(State stateObject) {
    this.stateObject = stateObject;
  }

  @Override
  public State getParam() {
    return this.stateObject;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("RestoreState{");
    sb.append("stateObject=").append(stateObject);
    sb.append('}');
    return sb.toString();
  }
}

}// end class MyApplicationActions