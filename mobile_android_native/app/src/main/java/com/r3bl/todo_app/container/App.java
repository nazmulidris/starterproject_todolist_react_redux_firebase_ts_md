package com.r3bl.todo_app.container;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import com.brianegan.bansa.BaseStore;
import com.r3bl.todo_app.container.firebase.MyAuth;
import com.r3bl.todo_app.container.firebase.MyDB;
import com.r3bl.todo_app.container.redux.Actions;
import com.r3bl.todo_app.container.redux.Reducer;
import com.r3bl.todo_app.container.redux.ReduxDebugLog;
import com.r3bl.todo_app.container.redux.state.State;
import com.r3bl.todo_app.container.redux.state.User;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by nazmul on 10/26/16.
 */

public class App extends android.app.Application {

private static final String TAG = App.class.getSimpleName();
private BaseStore<State> _store;
private MyAuth           _auth;
private MyDB             _database;
private String sessionId = UUID.randomUUID().toString();
private ReduxDebugLog _log;

public void resetSessionId() {
  sessionId = UUID.randomUUID().toString();
}

public enum LoggedInState {
  NotLoggedIn,
  AnonLoggedIn,
  GoogleLoggedIn,
}

//
// Constructor
//

@Override
public void onCreate() {
  super.onCreate();
  log("App.onCreate", "[START]");

  _initReduxStore();

  _initFromSharedPrefs();

//  log("App.onCreate", "created a new redux store object");
//  String msg = getReduxStore().getState().toString();
//  log("App.onCreate", String.format("{%s}", msg));

  _initDatabase();

  _initFirebaseAuth();

  log("App.onCreate", "[END]");
}

//
// Shared Preferences
//
private void _initFromSharedPrefs() {
  State oldState = MyDB.loadStateFromSharedPrefs(this);
  if (oldState != null) {
    getReduxStore().dispatch(new Actions.RestoreState(oldState));
    log("App._initFromSharedPrefs", "loaded saved state from SharedPreferences");
  }
}

//
// Database
//

public MyDB getDatabase() {
  return _database;
}

private void _initDatabase() {
  _database = new MyDB(this);
}

//
// Utility
//
public static App getContext(Activity activity) {
  return (App) activity.getApplicationContext();
}

public String getTime() {
  return DateFormat.getTimeInstance().format(new Date());
}

public String getSessionId() {
  return sessionId;
}

public static final void log(String line1_p1, String line1_p2, String... params) {
  String logmsg = _formatLogMessage(line1_p1, line1_p2, params);
  Log.d(TAG, logmsg);
}

public static final void logErr(String line1_p1, String line1_p2, String... params) {
  String logmsg = _formatLogMessage(line1_p1, line1_p2, params);
  Log.e(TAG, logmsg);
}

public static final void logErr(String line1_p1, String line1_p2, Throwable e) {
  String logmsg = _formatLogMessage(line1_p1, line1_p2, new String[]{e.toString()});
  Log.e(TAG, logmsg);
}

@NonNull
private static String _formatLogMessage(String line1_p1, String line2_p2, String[] params) {
  StringBuilder sb = new StringBuilder();
  sb.append(line1_p1).append(":").append(line2_p2);
  for (String s : params) {
    sb.append("\n\t").append(s);
  }
  return sb.toString();
}

//
// Auth
//

private void _initFirebaseAuth() {
  _auth = new MyAuth(this);
}

public MyAuth getAuth() {
  return _auth;
}

public LoggedInState getUserLoginState() {
  User user = getReduxState().user;
  if (user != null) {
    if (user.isAnonymous) return LoggedInState.AnonLoggedIn;
    else return LoggedInState.GoogleLoggedIn;
  } else {
    return LoggedInState.NotLoggedIn;
  }
}

//
// Redux store
//

private void _initReduxStore() {
  _log = new ReduxDebugLog();
  State state = new State();
  com.brianegan.bansa.Reducer reducer = new Reducer(this);
  _store = new BaseStore<>(state, reducer);
}

public State getReduxState() {
  return _store.getState();
}

public BaseStore<State> getReduxStore() {
  return _store;
}

public ReduxDebugLog getReduxLog() {
  return _log;
}

}// end MyApplication class