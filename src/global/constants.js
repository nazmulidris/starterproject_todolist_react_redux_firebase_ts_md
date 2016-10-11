/**
 * this holds all the constants defined for exchanging messages locally and remotely.
 * this uses node.js module exports and require statements, and NOT ES6 imports (since
 * at this time node (v6.4.0) does not support them and it's possible to use babel to
 * use node style require statements and transpile them in the client web app.
 */

/** these are event names */
const GLOBAL_CONSTANTS = {
  REMOTE_MESSAGE_FROM_CLIENT: "messageFromClient",
  REMOTE_MESSAGE_FROM_SERVER: "messageFromServer",
  LE_SHOW_SNACKBAR: "localevent_showSnackbar",
  LE_SET_USER: "localevent_setUser",
  LE_PRESENCE_USER_ADDED: "localevent_presenceUserAdded",
  LE_PRESENCE_USER_REMOVED: "localevent_presenceUserRemoved",
  LE_PRESENCE_USER_CHANGED: "localevent_presenceUserChanged",
  LE_CONTAINER_NETWORK_CONNECTION_STATE: "localevent_containerNetworkConnectionState",
};

/** this determines if log messages are shown or not in the console */
const LOGGING_ENABLED = true;

/** put your firebase config data here */
const FIREBASE_CONFIG = {
  apiKey: "AIzaSyBRJ4xl-15s08fI_H82fX-weap0oHjOcwc",
  authDomain: "todolist-starter-project.firebaseapp.com",
  databaseURL: "https://todolist-starter-project.firebaseio.com",
  storageBucket: "todolist-starter-project.appspot.com",
  messagingSenderId: "455882594322"
};

/** export these constants */
module.exports = {
  GLOBAL_CONSTANTS,
  FIREBASE_CONFIG,
  LOGGING_ENABLED,
};