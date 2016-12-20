package com.r3bl.todo_app.container.firebase;

import android.support.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.ui.MainActivity;

/**
 * Created by nazmul on 11/8/16.
 */

public class FirebaseAuthentication implements FirebaseAuth.AuthStateListener {

private final FirebaseAuth _auth;
private final App          _ctx;

public FirebaseAuthentication(App myApplication) {
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

private void _processUserLogin(UserInfo user) {
  _ctx.getDatabase().saveUserAndLoadData(user);
}

//
// Anon sign in
//

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

public void firebaseAuthWithGoogle(GoogleSignInAccount acct,
                                   String googleIdToken,
                                   AuthCredential credential,
                                   MainActivity mainActivity) {
  // TODO: 12/19/16 complete this integration ... figure out what the linking does ... update UI with avatar

  _auth.getCurrentUser().linkWithCredential(credential)
       .addOnCompleteListener(
         task -> {
           App.log("Auth", "firebaseAuthWithGoogle -> linkWithCredential:onComplete:" + task.isSuccessful());

           // TODO: 12/19/16
           // If sign in fails, display a message to the user. If sign in succeeds
           // the auth state listener will be notified and logic to handle the
           // signed in user can be handled in the listener.

           if (!task.isSuccessful()) {
             App.logErr("Auth", String.format("firebaseAuthWithGoogle: problem with signin, %s",
                                              task.getException()));
           }
         });

}

}// end class MyApplicationAuth