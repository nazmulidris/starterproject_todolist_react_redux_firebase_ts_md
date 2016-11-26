package com.r3bl.todo_app.container.redux.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * Created by nazmul on 11/8/16.
 */

public class State implements Serializable {

// make sure that anything you want to save to firebase is marked public
public User user;
public Data data;

public State() {
  // default constructor required for firebase DataSnapshot.getValue(State.class)
}

@Override
public String toString() {
  Gson gson = new GsonBuilder().setPrettyPrinting()
                               .serializeNulls()
                               .create();
  return gson.toJson(this);
}

public State deepCopy() {
  return SerializationUtils.clone(this);
}

}// end class ApplicationState