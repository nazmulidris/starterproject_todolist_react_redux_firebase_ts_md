package com.r3bl.todo_app.container.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.Actions;
import com.r3bl.todo_app.container.redux.state.Data;
import com.r3bl.todo_app.container.redux.state.State;
import com.r3bl.todo_app.container.redux.state.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.r3bl.todo_app.container.firebase.MyDB.Locations.USER_ACCOUNT_ROOT;
import static com.r3bl.todo_app.container.firebase.MyDB.Locations.USER_DATA_ROOT;

/**
 * Created by nazmul on 11/8/16.
 */

public class MyDB {
private final App                _ctx;
private final FirebaseDatabase   _db;
private final ExecutorService    _exec;
private       ValueEventListener user_data_listener;
private       ValueEventListener user_info_listener;
private       DatabaseReference  user_data_ref;
private       DatabaseReference  user_info_ref;

public enum Locations {
  USER_ACCOUNT_ROOT,
  USER_DATA_ROOT,
  DATA_KEY,
}

public MyDB(App app) {
  _ctx = app;
  _db = com.google.firebase.database.FirebaseDatabase.getInstance();
  _exec = Executors.newSingleThreadExecutor();
}

public com.google.firebase.database.FirebaseDatabase getDatabase() {
  return _db;
}

public void saveUserAndLoadData(User user) {
  // save the user object to firebase
  saveUserInfoToFirebase(user);

  // start listening for updates from firebase (which will fire actions)
  attachListenerToUserInfo(user.uid);
  attachListenerToUserData(user.uid);
}

public void attachListenerToUserData(String uid) {
  removeUserDataValueListener();

  user_data_ref = _db.getReference().child(USER_DATA_ROOT.name())
                     .child(uid)
                     .child(Locations.DATA_KEY.name());

  user_data_listener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      _fromFirebaseUserData(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  user_data_ref.addValueEventListener(user_data_listener);
}

public void removeUserDataValueListener() {
  try {
    user_data_ref.removeEventListener(user_data_listener);
  } catch (Exception e) {
  }
}

public void attachListenerToUserInfo(String uid) {
  removeUserInfoValueListener();

  user_info_ref = _db.getReference().child(USER_ACCOUNT_ROOT.name())
                     .child(uid);

  user_info_listener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      _fromFirebaseUserInfo(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  user_info_ref.addValueEventListener(user_info_listener);

}

public void removeUserInfoValueListener() {
  try {
    user_info_ref.removeEventListener(user_info_listener);
  } catch (Exception e) {
  }
}

//
// UserInfo and Data load / save to / from Firebase
//

private void _fromFirebaseUserInfo(DataSnapshot dataSnapshot) {

  _exec.submit(() -> {
    try {
      App.log("Database", "_fromFirebaseUserInfo");
      User userinfo = dataSnapshot.getValue(User.class);

      if (userinfo == null) {
        App.log("Database._fromFirebaseUserInfo]",
                "no data object found in Firebase");
        return;
      }

      // Create a handler attached to the UI Looper
      Handler handler = new Handler(Looper.getMainLooper());
      // Post code to run on the main UI Thread (usually invoked from worker thread)
      handler.post(() -> {
        _ctx.getReduxStore().dispatch(new Actions.SetUser(userinfo));
        App.log("Database._fromFirebaseUserInfo",
                "dispatching SetData action");
      });

    } catch (Exception e) {
      App.logErr("Database", "_fromFirebaseUserInfo issue", e);
    }
  });

}// end _fromFirebaseUserInfo

public void saveUserInfoToFirebase(User userinfo) {
  if (userinfo != null) {
    DatabaseReference ref = _db.getReference()
                               .child(USER_ACCOUNT_ROOT.name())
                               .child(userinfo.uid);
    ref.setValue(userinfo);
  }
  saveStateToSharedPrefs(_ctx, _ctx.getReduxState());
}

/**
 * run this code in a background thread, since this is an expensive method call!
 * <p>
 * more info - https://guides.codepath.com/android/Managing-Threads-and-Custom-Services#using-an-asynctask
 */
private void _fromFirebaseUserData(DataSnapshot dataSnapshot) {

  _exec.submit(() -> {
    try {
      App.log("Database", "_fromFirebaseUserData");
      Data data = dataSnapshot.getValue(Data.class);

      if (data == null) {
        App.log("Database._fromFirebaseUserData]",
                "no data object found in Firebase");
        return;
      }

      // Create a handler attached to the UI Looper
      Handler handler = new Handler(Looper.getMainLooper());
      // Post code to run on the main UI Thread (usually invoked from worker thread)
      handler.post(() -> {
        _ctx.getReduxStore().dispatch(new Actions.SetData(data));
        App.log("Database._fromFirebaseUserData",
                "dispatching SetData action");
      });

    } catch (Exception e) {
      App.logErr("Database", "_fromFirebaseUserData issue", e);
    }
  });

}// end _fromFirebaseUserData

public void saveUserDataToFirebase(Data data) {
  if (data != null) {
    data.prepForSaveToFirebase(_ctx.getSessionId(), ServerValue.TIMESTAMP);

    DatabaseReference ref = _db.getReference()
                               .child(USER_DATA_ROOT.name())
                               .child(_ctx.getReduxState().user.uid)
                               .child(Locations.DATA_KEY.name());
    ref.setValue(data);
  }
  saveStateToSharedPrefs(_ctx, _ctx.getReduxState());
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
 * Y * 1) copy data from old -> new user (only if new user is NOT pre existing)
 * 2) delete old user from {@link Locations#USER_ACCOUNT_ROOT} & Locations#USER_DATA_ROOT
 * <p>
 * Note - this means that a user that converts from anon -> a signed in user that already
 * has data in the system will lose the anon data. Data is only migrated for signedin
 * users that don't already existing in the database.
 */
public void performDataMigration(@NonNull User old_user,
                                 @NonNull User new_user) {
  // delete the old_user
  _copyAndDelete(old_user, new_user);
}

/**
 * copy data from old_user -> new_user ONLY if new_user doesn't have any existing data
 */
private void _copyAndDelete(User old_user, User new_user) {
  DatabaseReference old_user_data_ref =
    getDatabase().getReference()
                 .child(USER_DATA_ROOT.name())
                 .child(old_user.uid)
                 .child(Locations.DATA_KEY.name());

  DatabaseReference new_user_data_ref =
    getDatabase().getReference()
                 .child(USER_DATA_ROOT.name())
                 .child(new_user.uid)
                 .child(Locations.DATA_KEY.name());

  new_user_data_ref.addListenerForSingleValueEvent(new MyValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot newUserDataSnapshot) {
      if (newUserDataSnapshot.getValue() == null) {
        // new_user doesnt have any day in the db
        old_user_data_ref.addListenerForSingleValueEvent(new MyValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot oldUserDataSnapshot) {
            _copy(oldUserDataSnapshot, new_user_data_ref);
            _delete(old_user);
          }
        });

      } else {
        _delete(old_user);
      }
    }
  });
}

private void _copy(DataSnapshot dataSnapshot, DatabaseReference new_user_data_ref) {
  Data oldUserData = dataSnapshot.getValue(Data.class);
  if (oldUserData != null) {
    // copy the data from old_user -> new_user
    new_user_data_ref.setValue(oldUserData);
  }
}

/**
 * delete user and data for the old_user
 */
private void _delete(User old_user) {
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

public abstract class MyValueEventListener implements ValueEventListener {
  @Override
  public void onCancelled(DatabaseError databaseError) {

  }
}

}// end class Database