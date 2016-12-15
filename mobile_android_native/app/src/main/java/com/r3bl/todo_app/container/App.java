package com.r3bl.todo_app.container;

import android.support.annotation.NonNull;
import android.util.Log;
import com.brianegan.bansa.BaseStore;
import com.r3bl.todo_app.container.firebase.Auth;
import com.r3bl.todo_app.container.firebase.Database;
import com.r3bl.todo_app.container.redux.Actions;
import com.r3bl.todo_app.container.redux.Reducer;
import com.r3bl.todo_app.container.redux.ReduxDebugLog;
import com.r3bl.todo_app.container.redux.state.State;

import java.text.DateFormat;
import java.util.*;

/**
 * Created by nazmul on 10/26/16.
 */

public class App extends android.app.Application {

private static final String TAG = App.class.getSimpleName();
private BaseStore<State> _store;
private Auth             _auth;
private Database         _database;
private String sessionId = UUID.randomUUID().toString();
private ReduxDebugLog _log;

//
// Constructor
//

@Override
public void onCreate() {
  super.onCreate();
  log("App.onCreate", "[START]");

  _initReduxStore();

  _initFromSharedPrefs();

  log("App.onCreate", "created a new redux store object");
  String msg = getReduxStore().getState().toString();
  log("App.onCreate", String.format("{%s}", msg));

  _initDatabase();

  _initFirebaseAuth();

  log("App.onCreate", String.format("[END]", msg));
}

//
// Shared Preferences
//
private void _initFromSharedPrefs() {
  State oldState = Database.loadStateFromSharedPrefs(this);
  if (oldState != null) {
    getReduxStore().dispatch(new Actions.RestoreState(oldState));
    log("App._initFromSharedPrefs", "loaded saved state from SharedPreferences");
  }
}

//
// Database
//

public Database getDatabase() {
  return _database;
}

private void _initDatabase() {
  _database = new Database(this);
}

//
// Utility
//
public static App getContext(Object activity) {
  return (App) activity;
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

public static String diff(String text1, String text2) {
  try {
    StringTokenizer at = new StringTokenizer(text1, " ");
    StringTokenizer bt = null;
    int i = 0, token_count = 0;
    String token = null;
    boolean flag = false;
    List<String> missingWords = new ArrayList<String>();
    while (at.hasMoreTokens()) {
      token = at.nextToken();
      bt = new StringTokenizer(text2, " ");
      token_count = bt.countTokens();
      while (i < token_count) {
        String s = bt.nextToken();
        if (token.equals(s)) {
          flag = true;
          break;
        } else {
          flag = false;
        }
        i++;
      }
      i = 0;
      if (flag == false)
        missingWords.add(token);
    }
    List<String> list = missingWords;
    StringBuilder sb = new StringBuilder();
    for (String s : list) {
      sb.append(s).append("\n");
    }
    String retval = sb.toString();
    if (retval.trim().isEmpty()) return "N/A";
    else return String.format("`%s`", retval);
  } catch (Exception e) {
    return "N/A";
  }
}

//
// Auth
//

private void _initFirebaseAuth() {
  _auth = new Auth(this);
}

public Auth getAuth() {
  return _auth;
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