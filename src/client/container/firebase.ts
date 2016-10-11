/// <reference path="../../../typings/globals/node/index.d.ts" />

/**
 * these functions handle dealing with firebase for loading and saving data from it
 */

const GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;
const LOGGING_ENABLED = require('../../global/constants').LOGGING_ENABLED;

import {AuthStateIF, DataIF, UserIF, TodoIF, ReduxActionIF} from "./interfaces";

import * as actions from "./actions";

/** firebase database names */
const DB_CONST = {
  USER_ACCOUNT_ROOT: "USER_ACCOUNT_ROOT",
  USER_DATA_ROOT: "USER_DATA_ROOT",
  DATA_KEY: "DATA_KEY",
  SESSION_ID: "sessionId",
  TIMESTAMP: "timestamp",
};

/**
 * utility function library import
 * more info - https://lodash.com/
 */
const lodash = require('lodash');

/** holds information about the user (anonymous or signed-in) */
let authStateObject: AuthStateIF = {};

/** holds firebase listener that has on('value') registered */
let firebase_on_listener = null;

/**
 * gets a firebase reference that points to the node where the user's data is stored
 * @param ctx
 * @param id this is the project id
 * @returns {!firebase.database.Reference|firebase.database.Reference}
 * @private
 */
function _getUserDataRootRef(ctx, id?) {
  if (lodash.isNil(id)) {
    return ctx.getDatabase()
              .ref(DB_CONST.USER_DATA_ROOT);
  } else {
    return ctx.getDatabase()
              .ref(DB_CONST.USER_DATA_ROOT + "/" + id);
  }
}

/**
 * gets a firebase reference that points to the node where the user's account info is
 * stored
 * @param ctx
 * @param id this is the user id
 * @returns {!firebase.database.Reference|firebase.database.Reference}
 * @private
 */
function _getUserAccountRootRef(ctx, id?) {
  if (lodash.isNil(id)) {
    return ctx.getDatabase()
              .ref(DB_CONST.USER_ACCOUNT_ROOT);
  } else {
    return ctx.getDatabase()
              .ref(DB_CONST.USER_ACCOUNT_ROOT + "/" + id);
  }
}

/**
 * setup firebase auth ... the onAuthStateChanged() method is the main method that
 * firebase uses to manage authentication
 * @param ctx
 */
function initAuth(ctx) {
  
  // setup auth
  ctx.getFirebase()
     .auth()
     .onAuthStateChanged(
       (user)=> {
         if (user) {
           // user is signed in
           if (LOGGING_ENABLED) {
             console.log(
               `onAuthStateChanged: user is signed in: isAnonymous=${user.isAnonymous}, uid:${user.uid}`
             );
           }
           _processAuthStateChange(ctx, user);
         } else {
           // user is signed out
           if (LOGGING_ENABLED) {
             console.log("onAuthStateChanged: user is signed out");
           }
           _forceAnonSignIn(ctx);
         }
       }
     );
  
}

/**
 * actually process the auth state change from firebase
 */
function _processAuthStateChange(ctx, user) {
  if (lodash.isNil(authStateObject.old_uid) && lodash.isNil(authStateObject.new_uid)) {
    // signed-in user is being logged in ...
    _saveUserAccountDataAndSetUser(ctx, user);
  } else {
    // anon -> new / existing user needs to be taken care of ...
    _dealWithUserDataMigration(ctx, user);
  }
  // reset the authStateObject!
  authStateObject = {};
}

/**
 * save the given user to firebase ... this also adds a timestamp key with value of
 * ServerValue.TIMESTAMP so that smarter listeners for onChange can be attached
 * more info - https://goo.gl/AbvF03
 * @param ctx
 * @param user
 * @private
 */
function _saveUserAccountDataAndSetUser(ctx, user) {
  
  // note that the timestamp is NOT set on this object ... it will only be set by
  // firebase on the server side when this is set() on the ref
  let userObject: UserIF = {
    displayName: user.displayName,
    photoURL: user.photoURL,
    isAnonymous: user.isAnonymous,
    email: user.email,
    emailVerified: user.emailVerified,
    uid: user.uid,
    timestamp: ctx.getFirebaseServerTimestampObject(),
  };
  
  if (!lodash.isNil(user.googleAccessToken)) {
    userObject.googleAccessToken = user.googleAccessToken;
  }
  
  const root_ref = _getUserAccountRootRef(ctx, user.uid);
  
  // save this to db
  root_ref.set(userObject);
  
  // save this user object
  ctx.getReduxStore()
     .dispatch(actions.action_set_state_user(userObject));
  
  // fire a local event in case anyone wants to know about the login state change
  ctx.emit(GLOBAL_CONSTANTS.LE_SET_USER, userObject);
  
  // load the rest of the data from firebase
  _loadDataForUserAndAttachListenerToFirebase(ctx);
  
}

/** perform anonymous sign in using firebase ... this is done by default */
function _forceAnonSignIn(ctx) {
  ctx.getFirebase()
     .auth()
     .signInAnonymously()
     .catch(
       (error)=> {
         const errorCode = error.code;
         const errorMessage = error.message;
         if (LOGGING_ENABLED) {
           console.log("anonSignIn(): problem signing in");
           console.dir(error);
         }
       }
     );
}

/**
 * this is called when the user initiates Google signin. this is just called just once,
 * while the user is signed out or is an anonymous user (and the UI action is called).
 * subsequently this does not get called. the user is already auth'd ... it's handled
 * via the regular callback (onAuthStateChanged).
 * @param ctx
 * more info:
 * - google signin scopes - https://developers.google.com/identity/protocols/googlescopes
 * - firebase google provider - https://firebase.google.com/docs/auth/web/google-signin
 * - update firebase key - http://stackoverflow.com/questions/29115990/firebase-update-key
 */
function forceSignIn(ctx) {
  let provider = new ctx.firebase.auth.GoogleAuthProvider();
  provider.addScope('profile');
  provider.addScope('email');
  ctx.getFirebase()
     .auth()
     .signInWithPopup(provider)
     .then(
       (result)=> {
         // This gives you a Google Access Token. You can use it to access the Google API.
         let token = result.credential.accessToken;
         // The signed-in user info.
         let new_user = result.user;
         new_user.googleAccessToken = token;
      
         let old_uid = ctx.getUserId();
         let new_uid = new_user.uid;
      
         authStateObject = {
           old_user: ctx.getUser(),
           new_user: new_user,
           old_uid: ctx.getUserId(),
           new_uid: new_user.uid,
         };
      
       }
     )
     .catch(
       (error)=> {
         // Handle Errors here.
         let errorCode = error.code;
         let errorMessage = error.message;
         // The email of the user's account used.
         let email = error.email;
         // The firebase.auth.AuthCredential type that was used.
         let credential = error.credential;
         if (LOGGING_ENABLED) {
           console.log("forceSignIn(): problem signing in");
           console.dir(error);
         }
       }
     );
}

/** check to see if there is any pre-existing data when user changes signin state from
 * auth -> signed in
 */
function _dealWithUserDataMigration(ctx, user) {
  
  // The signed-in user info.
  let new_user = authStateObject.new_user;
  let old_uid = authStateObject.old_uid;
  let new_uid = authStateObject.new_uid;
  
  if (LOGGING_ENABLED) {
    console.log(`_dealWithUserDataMigration(): FROM old_uid=${old_uid} TO new_uid=${new_uid}`);
  }
  
  // check to see if new user or existing user
  let proj_data_root_ref = _getUserDataRootRef(ctx);
  let new_child_ref = proj_data_root_ref.child(new_uid);
  new_child_ref.once(
    'value', (snap)=> {
      if (!lodash.isNil(snap) && !lodash.isNil(snap.val())) {
        // anon->existing user
        if (LOGGING_ENABLED) {
          console.log("anon->existing user");
        }
        _migrateUserAnonToExisting(ctx, old_uid, new_user);
      } else {
        // anon->new user
        if (LOGGING_ENABLED) {
          console.log("anon->brand new user");
        }
        _migrateUserAnonToNew(ctx, old_uid, new_uid, new_user);
      }
    }
  );
  
}

/**
 * there is no pre-existing user, taking the anon-user to brand new signed in user
 * @param ctx
 * @param new_user
 * @private
 */
function _migrateUserAnonToNew(ctx, old_uid, new_uid, new_user) {
  
  // migrate data from the old user
  let user_data_root_ref = _getUserAccountRootRef(ctx);
  let proj_data_root_ref = _getUserDataRootRef(ctx);
  let old_child_ref = proj_data_root_ref.child(old_uid);
  
  // copy the data from old -> new user
  old_child_ref.once(
    'value', (snap)=> {
      proj_data_root_ref.child(new_uid)
                        .set(
                          snap.val(), (error)=> {
                            // save this new user
                            _saveUserAccountDataAndSetUser(ctx, new_user);
                          }
                        );
      old_child_ref.remove();
      user_data_root_ref.child(old_uid)
                        .remove();
    }
  );
  
}

/**
 * don't delete the existing user's data .. delete the anon user's data instead
 * the existing (new_user) data already exists (since they are pre-existing)
 * @param ctx
 * @param new_user
 * @private
 */
function _migrateUserAnonToExisting(ctx, old_uid, new_user) {
  
  // remove the old_user (anon user) account
  let user_account_root_ref = _getUserAccountRootRef(ctx);
  user_account_root_ref.child(old_uid)
                       .remove();
  
  // remove the old_user (anon user) data
  let user_data_root_ref = _getUserDataRootRef(ctx);
  user_data_root_ref.child(old_uid)
                    .remove();
  
  // get going with the pre-existing user (their data already exists)
  _saveUserAccountDataAndSetUser(ctx, new_user);
  
}

/** initiate sign out, called by the UI */
function forceSignOut(ctx) {
  
  ctx.getFirebase()
     .auth()
     .signOut()
     .then(
       () => {
         // Sign-out successful.
         ctx.getReduxStore()
            .dispatch(actions.action_init());
       },
       (error)=> {
         // An error happened.
       }
     );
  
}

/** this loads the data for the current user and sets it on the context */
function _loadDataForUserAndAttachListenerToFirebase(ctx) {
  
  // check to see if Redux state needs to be rehydrated (this is a one time operation)
  let userId = ctx.getUserId();
  let userDataRootRef = _getUserDataRootRef(ctx, userId);
  
  // if there's an old listener then detach it now
  if (!lodash.isNil(firebase_on_listener)) {
    firebase_on_listener.off("value");
    firebase_on_listener = null;
  }
  
  // save to detach for next time
  firebase_on_listener = userDataRootRef;
  
  userDataRootRef.on(
    "value",
    (snap)=> {
      _processUpdateFromFirebase(snap, ctx);
    }
  );
  
}

function _processUpdateFromFirebase(snap, ctx) {
  
  const value = snap.val();
  
  if (lodash.isNil(value)) {
    // nothing to do!
    return;
  }
  
  let data: DataIF = value[DB_CONST.DATA_KEY];
  const payload_session_id = data[DB_CONST.SESSION_ID];
  const timestamp = data[DB_CONST.TIMESTAMP];
  
  if (!lodash.isNil(payload_session_id)) {
    if (lodash.isEqual(payload_session_id, ctx.getSessionId())) {
      // do nothing! ignore this change ... it was made by me
      // this change has been accounted for with dispatched redux
      // actions already
      if (LOGGING_ENABLED) {
        console.log(
          "_loadDataForUserAndAttachListenerToFirebase() - ignoring Firebase" +
          " update since I made this change."
        );
      }
      return;
    }
  }
  
  // save the user's data
  ctx.getReduxStore()
     .dispatch(actions.action_set_state_data(data));
  
}


function dispatchActionAndSaveStateToFirebase(orig_action: ReduxActionIF, ctx) {
  
  let action = orig_action;
  
  // apply the action locally, and this will change the state
  ctx.getReduxStore()
     .dispatch(action);
  
  // save to persistence
  let root_ref = _getUserDataRootRef(ctx, ctx.getUserId());
  
  let value = ctx.getReduxState().data;
  value[DB_CONST.SESSION_ID] = ctx.getSessionId();
  value[DB_CONST.TIMESTAMP] = ctx.getFirebaseServerTimestampObject();
  
  root_ref.child(DB_CONST.DATA_KEY)
          .set(value);
  
}

/** export public functions */
export {
  forceSignOut,
  forceSignIn,
  initAuth,
  dispatchActionAndSaveStateToFirebase,
}