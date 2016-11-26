package com.r3bl.todo_app.container.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.Actions;
import com.r3bl.todo_app.container.redux.state.Data;
import com.r3bl.todo_app.container.redux.state.State;
import com.r3bl.todo_app.container.redux.state.User;

/**
 * Created by nazmul on 11/8/16.
 */

public class Database {
private final App                _ctx;
private final FirebaseDatabase   _db;
private       ValueEventListener valueListener;

public enum Locations {
  USER_ACCOUNT_ROOT,
  USER_DATA_ROOT,
  DATA_KEY,
  sessionId,
  timestamp,
}

public Database(App app) {
  _ctx = app;
  _db = FirebaseDatabase.getInstance();
  app.getReduxStore().subscribe(state -> {
    _saveStateToFirebase(state);
  });
}

public FirebaseDatabase getDatabase() {
  return _db;
}

public void saveUserAndLoadData(User userObject) {

  // save the userObject to firebase
  DatabaseReference rootRef = _db.getReference()
                                 .child(Locations.USER_ACCOUNT_ROOT.name())
                                 .child(userObject.uid);
  rootRef.setValue(userObject);
  App.log("Database.saveUserAndLoadData",
          "saving userObject to Firebase, uid:" + userObject.uid);

  // dispatch a redux action to set the user object
  _ctx.getReduxStore().dispatch(new Actions.SetUser(userObject));

  // load the data for the user
  _loadDataForUserAndAttachListenerToFirebase(userObject.uid);

}

private void _loadDataForUserAndAttachListenerToFirebase(String uid) {

  DatabaseReference ref = _db.getReference().child(Locations.USER_DATA_ROOT.name())
                             .child(uid)
                             .child(Locations.DATA_KEY.name());

  if (valueListener != null) {
    ref.removeEventListener(valueListener);
  }

  valueListener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      _processUpdateFromFirebase(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  ref.addValueEventListener(valueListener);

}

private void _processUpdateFromFirebase(DataSnapshot dataSnapshot) {

  Data dataObject = dataSnapshot.getValue(Data.class);

  if (dataObject != null) {

    String sessionIdFromFirebase = dataObject.sessionId;
    String localSessionId = _ctx.getSessionId();

    App.log("Database._processUpdateFromFirebase",
            "comparing session ids:", sessionIdFromFirebase, localSessionId);

    if (!sessionIdFromFirebase.equals(localSessionId)) {

      // dispatch a redux action to set the data object
      _ctx.getReduxStore().dispatch(new Actions.SetData(dataObject));
      App.log("Database._processUpdateFromFirebase",
              "load dataObject from Firebase", dataObject.toString());

    } else {

      // don't dispatch
      App.log("Database._processUpdateFromFirebase",
              "ignoring dataObject from Firebase, since I made this change",
              dataObject.toString());

    }

  } else {
    // nothing to dispatch
    App.log("Database._processUpdateFromFirebase]",
            "no dataObject found in Firebase");
  }

}

private void _saveStateToFirebase(State state) {

  Data data = state.data;
  if (data != null) {
    data.prepForSaveToFirebase(_ctx.getSessionId(), ServerValue.TIMESTAMP);

    DatabaseReference ref = _db.getReference()
                               .child(Locations.USER_DATA_ROOT.name())
                               .child(_ctx.getReduxState().user.uid)
                               .child(Locations.DATA_KEY.name());
    ref.setValue(data);
  }
  saveStateToSharedPrefs(_ctx, state);

}

//
// Shared Preferences
//

public static State loadStateFromSharedPrefs(App context) {
  SharedPreferences pref = context.getSharedPreferences(Database.class.getSimpleName(),
                                                        Context.MODE_PRIVATE);
  String serform = pref.getString(Locations.DATA_KEY.name(), null);
  if (serform == null) return null;
  Gson gson = new GsonBuilder().create();
  State retval = gson.fromJson(serform, State.class);

  App.log("Database.loadStateFromSharedPrefs",
          "loading state from SharedPreferences");

  return retval;
}

public static void saveStateToSharedPrefs(App context, State state) {
  SharedPreferences.Editor pref = context.getSharedPreferences(Database.class.getSimpleName(),
                                                               Context.MODE_PRIVATE)
                                         .edit();
  pref.putString(Locations.DATA_KEY.name(),
                 state.toString());
  pref.apply();

  App.log("Database.saveStateToSharedPrefs",
          "saving state to SharedPreferences");
}

}// end class Database