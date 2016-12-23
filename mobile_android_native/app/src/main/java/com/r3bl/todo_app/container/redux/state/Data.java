package com.r3bl.todo_app.container.redux.state;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class and all its contained classes must implement equals(). This is used by
 * {@link com.r3bl.todo_app.container.firebase.MyDB#_processUpdateFromFirebase(DataSnapshot)}
 * in order to determine whether {@link com.r3bl.todo_app.container.redux.Actions.SetData}
 * is dispatched to the redux store.
 * <ol>
 * <li> Data.equals() cares about the todoArray field only (not sessionId or timestamp)</li>
 * <li> TodoItem.equals() cares about all its public fields</li>
 * </ol>
 * Created by nazmul on 11/24/16.
 */

public class Data implements Serializable {

// make sure that anything you want to save to firebase is marked public
public String sessionId;
public Object timestamp;
public ArrayList<TodoItem> todoArray = new ArrayList<>();

@Override
public boolean equals(Object o) {
  if (this == o) return true;
  if (!(o instanceof Data)) return false;

  Data data = (Data) o;

  return todoArray != null ? todoArray.equals(data.todoArray) : data.todoArray == null;
}

@Override
public int hashCode() {
  return todoArray != null ? todoArray.hashCode() : 0;
}

public Data() {
  // default constructor required for firebase DataSnapshot.getValue(Data.class)
}

public void prepForSaveToFirebase(String sessionId, Object timestamp) {
  this.sessionId = sessionId;
  this.timestamp = timestamp;
}

@Override
public String toString() {
  Gson gson = new GsonBuilder().setPrettyPrinting()
                               .serializeNulls()
                               .create();
  return gson.toJson(this);
}

/**
 * This class implements an equals() method which is required by {@link Data}.
 * equals() cares about all the fields in this class
 * <p>
 * Created by nazmul on 11/10/16.
 */
public static class TodoItem implements Serializable {
  // make sure that anything you want to save to firebase is marked public
  public boolean done;
  public String  item;

  public TodoItem() {
    // default constructor required for firebase DataSnapshot.getValue(TodoItem.class)
  }

  public TodoItem(String item, boolean done) {
    this.done = done;
    this.item = item;
  }

  @Override
  public String toString() {
    Gson gson = new GsonBuilder().setPrettyPrinting()
                                 .serializeNulls()
                                 .create();
    return gson.toJson(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TodoItem)) return false;

    TodoItem todoItem = (TodoItem) o;

    if (done != todoItem.done) return false;
    return item != null ? item.equals(todoItem.item) : todoItem.item == null;
  }

  @Override
  public int hashCode() {
    int result = (done ? 1 : 0);
    result = 31 * result + (item != null ? item.hashCode() : 0);
    return result;
  }
}// end class TodoItem

}// end class Data