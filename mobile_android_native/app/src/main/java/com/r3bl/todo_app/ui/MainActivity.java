package com.r3bl.todo_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.Actions;
import com.r3bl.todo_app.todoapp.R;
import com.r3bl.todo_app.ui.groupchat.GroupChatFragment;
import com.r3bl.todo_app.ui.reduxdebug.DebugFragment;
import com.r3bl.todo_app.ui.todo.TodoFragment;
import me.relex.circleindicator.CircleIndicator;
import org.greenrobot.eventbus.EventBus;

/**
 * more info on google signin & firebase auth -
 * https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/GoogleSignInActivity.java#L135-L153
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

/**
 * constant used for Google sign in onActivityResult
 */
public static final int RC_SIGN_IN = 999;
private GoogleApiClient     mGoogleApiClient;
private GoogleSignInOptions gso;

//
// Google sign in
//

/**
 * one time google signin setup
 */
private void _setupGoogleSignin() {
  gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(getString(R.string.default_web_client_id))
          .requestEmail()
          .build();

  mGoogleApiClient = new GoogleApiClient.Builder(this)
                       .enableAutoManage(this /* FragmentActivity */,
                                         this /* OnConnectionFailedListener */)
                       .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                       .build();
}

public void _googleSignIn() {

  Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
  startActivityForResult(signInIntent, RC_SIGN_IN);
}

/**
 * more info - http://stackoverflow.com/questions/38707133/google-firebase-sign-out-and-forget-user-in-android-app
 */
private void _googleSignOut() {
  // reset state
  App _ctx = App.getContext(this);
  _ctx.getReduxStore().dispatch(new Actions.ResetState());
  _ctx.resetSessionId();
  _ctx.getAuth().signOut();

  // Google sign out
  if (mGoogleApiClient.isConnected()) {
    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
      new ResultCallback<Status>() {
        @Override
        public void onResult(@NonNull Status status) {
        }
      });
  }
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);

  // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
  if (requestCode == RC_SIGN_IN) {
    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    if (result.isSuccess()) {
      // Google Sign In was successful, authenticate with Firebase
      GoogleSignInAccount account = result.getSignInAccount();
      _firebaseAuthWithGoogle(account);
    } else {
      // Google Sign In failed, update UI appropriately
      App.logErr("MainActivity",
                 "problem signing into Google",
                 result.toString());
    }
  }
}

private void _firebaseAuthWithGoogle(GoogleSignInAccount acct) {
  String googleIdToken = acct.getIdToken();
  AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
  App.getContext(this)
     .getAuth()
     .firebaseAuthWithGoogle(acct, googleIdToken, credential, this);
}


@Override
public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
  App.logErr("MainActivity",
             "problem connection to Google Play Services",
             connectionResult.toString());
}

//
// UI inflate
//

@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.main_activity);

  // toolbar setup
  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
  setSupportActionBar(toolbar);

  // fab setup
  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
  fab.setOnClickListener(
    view ->
      _actionFab(view));

  // view pager setup
  ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewPager);
  ViewPagerAdapter myPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
  viewPager.setAdapter(myPagerAdapter);
  CircleIndicator indicator = (CircleIndicator) findViewById(R.id.main_indicator);
  indicator.setViewPager(viewPager);

  // google signin setup
  _setupGoogleSignin();
}

//
// FAB pressed
//
private void _actionFab(View view) {
  App ctx = (App) getApplicationContext();
  if (ctx.getUserLoginState() == App.LoggedInState.NotLoggedIn) {
    Toast.makeText(MainActivity.this, "You must sign in to do this", Toast.LENGTH_SHORT)
         .show();
  } else {
    EventBus.getDefault().post(new TodoFragment.LE_AddTodoListItem());
    Snackbar.make(view, "Todo list item added", Snackbar.LENGTH_LONG)
            .setAction("State",
                       v -> {
                         String msg = ctx.getReduxState().toString();
                         Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT)
                              .show();
                       })
            .show();
  }
}

@Override
protected void onDestroy() {
  super.onDestroy();
  App.log("MainActivity", "onDestroy: ran");
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
  // Inflate the menu; this adds items to the action bar if it is present.
  getMenuInflater().inflate(R.menu.menu_main, menu);
  return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
  // Handle action bar item clicks here. The action bar will
  // automatically handle clicks on the Home/Up button, so long
  // as you specify a parent activity in AndroidManifest.xml.
  int id = item.getItemId();

  //noinspection SimplifiableIfStatement
  if (id == R.id.action_settings) {
    return true;
  } else if (id == R.id.action_account) {
    _actionLogin();
    return true;
  }

  return super.onOptionsItemSelected(item);
}

//
// Login Action
//

private void _actionLogin() {
  switch (App.getContext(this).getUserLoginState()) {
    case NotLoggedIn:
      // do nothing
      break;
    case AnonLoggedIn:
      _googleSignIn();
      break;
    case GoogleLoggedIn:
      _googleSignOut();
      break;
  }
}

//
// View pager adapter support
//
class ViewPagerAdapter extends FragmentPagerAdapter {

  public ViewPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int tabPosition) {
    switch (tabPosition) {
      case 0:
        return new TodoFragment();
      case 1:
        return new GroupChatFragment();
      case 2:
        return new DebugFragment();
    }
    return null;
  }

  @Override
  public int getCount() {
    return 3;
  }

  @Override
  public CharSequence getPageTitle(int tabPosition) {
    switch (tabPosition) {
      case 0:
        return "Todo List";
      case 1:
        return "Group Chat";
      case 2:
        return "Redux Debug";
    }
    return super.getPageTitle(tabPosition);
  }

}

}// end MainActivity class