package com.r3bl.todo_app.container.firebase;

import android.support.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.state.User;

/**
 * Created by nazmul on 11/8/16.
 */

public class Auth implements FirebaseAuth.AuthStateListener {

private final FirebaseAuth _auth;
private final App          _ctx;

public Auth(App myApplication) {
  _ctx = myApplication;
  _auth = FirebaseAuth.getInstance();
  attachAuthListener();
}

public void attachAuthListener() {
  _auth.addAuthStateListener(this);
}

public void detachAuthListener() {
  _auth.removeAuthStateListener(this);
}

@Override
public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
  FirebaseUser user = firebaseAuth.getCurrentUser();
  App.log("Auth", "onAuthStateChanged",
          user == null ? "user is null" : "user is not null");

  if (user != null) {
    // user is signed in (auth or social)
    _processUserLogin(user);

  } else {
    // user isn't signed in, so kick off anon auth
    _forceAnonSignIn();
  }

}

private void _processUserLogin(FirebaseUser user) {
  try {
    User userObject = new User(user);
    if (!userObject.equals(_ctx.getReduxState().user)) {
      _ctx.getDatabase().saveUserAndLoadData(userObject);
    }
  } catch (Exception e) {
  }
}

private void _forceAnonSignIn() {
  _auth.signInAnonymously()
       .addOnCompleteListener(
         task -> {
           App.log("Auth", "_forceAnonSignIn: anon auth complete");
           if (!task.isSuccessful()) {
             App.logErr("Auth", String.format("_forceAnonSignIn: problem with anon auth, %s",
                                              task.getException()));
           }
         });
}


}// end class MyApplicationAuth