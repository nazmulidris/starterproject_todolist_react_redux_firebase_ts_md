package com.r3bl.todo_app.container.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.brianegan.bansa.Subscription;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.Actions;
import com.r3bl.todo_app.container.redux.state.Data;
import com.r3bl.todo_app.container.redux.state.State;
import com.r3bl.todo_app.container.redux.state.User;

import static com.r3bl.todo_app.container.firebase.MyDB.Locations.USER_DATA_ROOT;
import static com.r3bl.todo_app.container.utils.DiffMatchPatch.diff;

/**
 * Created by nazmul on 11/8/16.
 */

public class MyDB {
private final App                _ctx;
private final FirebaseDatabase   _db;
private       Subscription       _subscription;
private       ValueEventListener valueListener;
private       DatabaseReference  refWithValueListener;

public enum Locations {
  USER_ACCOUNT_ROOT,
  USER_DATA_ROOT,
  DATA_KEY,
  sessionId,
  timestamp,
}

public MyDB(App app) {
  _ctx = app;
  _db = com.google.firebase.database.FirebaseDatabase.getInstance();
  startSavingStateToFirebase();
}

public com.google.firebase.database.FirebaseDatabase getDatabase() {
  return _db;
}

public void saveUserAndLoadData(User user) {
  removeValueListener();

  // check to see whether SetUser should be fired or not
  _dispatchSetUserAction(user);

  // load the data for the user
  _loadDataForUserAndAttachListenerToFirebase(user.uid);
}

private void _saveUserObjectToFirebase(User user) {
  // save the userObject to firebase
  DatabaseReference rootRef = _db.getReference()
                                 .child(Locations.USER_ACCOUNT_ROOT.name())
                                 .child(user.uid);
  rootRef.setValue(user);
  App.log("Database._saveUserObjectToFirebase",
          "saving User object to Firebase, uid:" + user.uid);
}

private void _dispatchSetUserAction(User firebaseUser) {
  // check to see whether SetUser action should be dispatched
  User localUser = _ctx.getReduxState().user;

  if (!firebaseUser.equals(localUser)) {
    App.log("Database._dispatchSetUserAction",
            "Local user and Firebase user are not the same",
            "1) dispatching SetUser action, ",
            "2) Saving the user object to Firebase",
            diff(firebaseUser, localUser));

    // dispatch a redux action to set the user object
    _ctx.getReduxStore().dispatch(new Actions.SetUser(firebaseUser));

    // check to see if the firebase userobject should be saved
    _saveUserObjectToFirebase(firebaseUser);

  } else {
    App.log("Database._dispatchSetUserAction",
            "Local user and Firebase user are the same - will not dispatch SetUser action");
  }
}

private void _loadDataForUserAndAttachListenerToFirebase(String uid) {

  removeValueListener();

  refWithValueListener = _db.getReference().child(USER_DATA_ROOT.name())
                            .child(uid)
                            .child(Locations.DATA_KEY.name());

  valueListener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      _processUpdateFromFirebase(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  refWithValueListener.addValueEventListener(valueListener);

}

public void removeValueListener() {
  if (refWithValueListener != null) {
    if (valueListener != null) {
      refWithValueListener.removeEventListener(valueListener);
    }
  }
}

private void _processUpdateFromFirebase(DataSnapshot dataSnapshot) {

  Data dataObject = dataSnapshot.getValue(Data.class);

  if (dataObject != null) {

    String sessionIdFromFirebase = dataObject.sessionId;
    String localSessionId = _ctx.getSessionId();

    //App.log("Database._processUpdateFromFirebase",
    //        "comparing session ids:", sessionIdFromFirebase, localSessionId);

    if (!sessionIdFromFirebase.equals(localSessionId)) {

      // dispatch a redux action to set the data object
      _ctx.getReduxStore().dispatch(new Actions.SetData(dataObject));
      App.log("Database._processUpdateFromFirebase",
              "local and firebase sessionIds do NOT match",
              "dispatching SetData action");
      //, dataObject.toString());

    } else {

      // don't dispatch
      App.log("Database._processUpdateFromFirebase",
              "local and firebase sessionIds are the SAME",
              "will NOT dispatch SetData action, since I already applied it locally");
      //,dataObject.toString());

    }

  } else {
    // nothing to dispatch
    App.log("Database._processUpdateFromFirebase]",
            "no data object found in Firebase");
  }

}

public void startSavingStateToFirebase() {
  stopSavingStateToFirebase();
  _subscription = _ctx.getReduxStore().subscribe(state -> {
    _saveStateToFirebase(state);
  });
}

public void stopSavingStateToFirebase() {
  if (_subscription != null) _subscription.unsubscribe();
}

private void _saveStateToFirebase(State state) {

  Data data = state.data;
  if (data != null) {
    data.prepForSaveToFirebase(_ctx.getSessionId(), ServerValue.TIMESTAMP);

    DatabaseReference ref = _db.getReference()
                               .child(USER_DATA_ROOT.name())
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
  SharedPreferences pref = context.getSharedPreferences(MyDB.class.getSimpleName(),
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
  SharedPreferences.Editor pref = context.getSharedPreferences(MyDB.class.getSimpleName(),
                                                               Context.MODE_PRIVATE)
                                         .edit();
  pref.putString(Locations.DATA_KEY.name(),
                 state.toString());
  pref.apply();

  App.log("Database.saveStateToSharedPrefs",
          "saving state to SharedPreferences");
}

//
// Data migration from one user to another
//

/**
 * 1) copy data from old -> new user (only if new user is NOT pre existing)
 * 2) delete old user from {@link Locations#USER_ACCOUNT_ROOT} & Locations#USER_DATA_ROOT
 * <p>
 * Note - this means that a user that converts from anon -> a signed in user that already
 * has data in the system will lose the anon data. Data is only migrated for signedin
 * users that don't already existing in the database.
 */
public void performDataMigration(@NonNull User old_user,
                                 @NonNull User new_user) {
  // delete the old_user
  removeValueListener();
  _deleteDataAndUser(old_user);

}

private void _deleteDataAndUser(User old_user) {
  DatabaseReference old_user_data_root =
    getDatabase().getReference()
                 .child(USER_DATA_ROOT.name());

  DatabaseReference old_user_account_root =
    getDatabase().getReference()
                 .child(Locations.USER_ACCOUNT_ROOT.name());

  old_user_data_root.child(old_user.uid).removeValue();
  old_user_account_root.child(old_user.uid).removeValue();

  App.log("Database", "_deleteDataAndUser: deleting USER_DATA_ROOT/old_user & USER_ACCOUNT_ROOT/old_user");
}

}// end class Database