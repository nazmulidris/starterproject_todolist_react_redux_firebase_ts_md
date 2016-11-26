package com.r3bl.todo_app.container.redux.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nazmul on 11/24/16.
 */

public class Data implements Serializable {

// make sure that anything you want to save to firebase is marked public
public String sessionId;
public Object timestamp;
public ArrayList<TodoItem> todoArray = new ArrayList<>();

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

}// end class Data