package com.r3bl.todo_app.container.redux.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * Created by nazmul on 11/10/16.
 */
public class TodoItem implements Serializable {

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

}// end class TodoItem