package com.r3bl.todo_app.container.firebase;

import android.support.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.state.User;
import com.r3bl.todo_app.ui.MainActivity;

/**
 * Created by nazmul on 11/8/16.
 */

public class MyAuth implements FirebaseAuth.AuthStateListener {

private final FirebaseAuth _auth;
private final App          _ctx;

public MyAuth(App myApplication) {
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

/**
 * main firebase auth callback that pretty much takes care of all auth state changes
 */
@Override
public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
  FirebaseUser user = firebaseAuth.getCurrentUser();
  App.log("Auth",
          String.format("onAuthStateChanged: %s",
                        user) == null ? "user is null" : "user is NOT null");
  if (user != null) {
    _processUserLogin(user);

  } else {
    // user isn't signed in, so kick off anon auth
    _forceAnonSignIn();
  }
}

private void _processUserLogin(@NonNull FirebaseUser firebaseUserObject) {
  if (firebaseUserObject == null) return;

  // new_user is signed in (anon or social)
  User old_user = _ctx.getReduxState().user;
  User new_user = new User(firebaseUserObject);

  boolean performMigration = old_user != null &&
                             old_user.isAnonymous &&
                             !new_user.isAnonymous;

  // stop saving state to firebase
  _ctx.getDatabase().removeValueListener(); // stop listening to db updates
  _ctx.getDatabase().stopSavingStateToFirebase(); // stop saving to db

  if (performMigration) {
    // anon -> signedin ... do data migration
    _ctx.getDatabase().performDataMigration(old_user, new_user);
  }

  // start saving state to firebase
  _ctx.getDatabase().startSavingStateToFirebase(); // start saving to db again

  // process user login
  _ctx.getDatabase().saveUserAndLoadData(new_user);
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

//
// Google sign in
//

public void firebaseAuthWithGoogle(GoogleSignInAccount acct,
                                   String googleIdToken,
                                   AuthCredential credential,
                                   MainActivity mainActivity) {
  _auth.signInWithCredential(credential)
       .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull Task<AuthResult> task) {
           App.log("Auth", "firebaseAuthWithGoogle -> signInWithCredential:onComplete:" + task.isSuccessful());
           if (!task.isSuccessful()) {
             App.logErr("Auth", String.format("firebaseAuthWithGoogle: problem with signin, %s",
                                              task.getException()));
           }
         }
       });
}

public void signOut() {
  _auth.signOut();
}

}// end class MyApplicationAuth