/// <reference path="../../../typings/globals/node/index.d.ts" />

const GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;
const LOGGING_ENABLED = require('../../global/constants').LOGGING_ENABLED;

import * as persistence from './firebase';
import * as mutateData from './mutatedata';
import {UserIF, DataIF} from "./interfaces";

const lodash = require('lodash');
const events = require("events");

/**
 * this holds the app's state which is comprised of:
 * 1) user object: UserIF
 * 2) data for the user: DataIF
 *
 * any time this user or data is modified, it emits events to notify any listeners
 * that are interested in listening to these changes via:
 * 1) LE_SET_USER
 * 2) LE_SET_DATA
 */
class ApplicationContext {
  
  public sessionId;
  public socket;
  public firebase;
  public eventEmitter;
  public user: UserIF;
  public data: DataIF;
  
  constructor() {
    
    // init firebase
    this.initFirebase();
    
    // setup websocket (used for group chat)
    this.initSocket();
    
    // unique session id
    this.sessionId = lodash.uniqueId();
    
    // create event emitter
    this.initEventEmitter();
    
    // setup firebase auth
    persistence.initAuth(this);
    
    // enable data to be saved to firebase when user changes it using in the UI
    mutateData.init(this);
    
  }
  
  isProduction() {
    const hostname = window.location.hostname;
    
    if (!lodash.isEqual(hostname, "localhost")) {
      // prod app
      return true;
    } else {
      // dev app
      return false;
    }
  }
  
  isDevelopment() {
    return !this.isProduction();
  }
  
  /**
   * this generates a different URL depending on whether the code is running on
   * localhost or not.
   * DEV - If it's running in localhost, then it understands this to be
   * the dev environment and it tries to connect to "localhost:8080".
   * PROD - If it's NOT running in localhost, then it understands this to be the
   * production environment and tries to connect to "/".
   * @returns {string}
   */
  getSocketURL() {
    let socketURL = "http://localhost:8080";
    if (this.isProduction()) {
      socketURL = "/";
    }
    return socketURL;
  }
  
  /**
   * this sets up the socket object for use by this context
   */
  initSocket() {
    let io = require("socket.io-client");
    this.socket = new io.connect(this.getSocketURL());
  }
  
  /**
   * to access the socket for this context use this method ... you can emit()
   * using it, and you can attach on() listeners to this as well ... if you attach
   * listeners, it's up to you to remove them from the socket when they're no longer
   * needed. This class will NOT do the cleanup for you.
   * @returns {io.connect|*}
   */
  getSocket() {
    return this.socket;
  }
  
  /**
   * this returns an ephermeral session id for this session ... will change every
   * time this session is restarted (ApplicationContext is created).
   * @returns {string|*}
   */
  getSessionId() {
    return this.sessionId;
  }
  
  /**
   * is true if the user object is set, and it contains a uid field.
   * you can get the user object from getUserObject()
   * you can get the uid from getUserId()
   * @returns {boolean}
   */
  isUserSet() {
    if (!lodash.isNil(this.getUserObject())) {
      if (!lodash.isNil(this.getUserId())) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * this saves a reference to the given data object (doesn't deep copy it).
   * it also fires an event so that listeners who are interested in this are notified.
   */
  setData(data: DataIF) {
    this.data = data;
    // emit event to let everyone know that the data is set
    this.emit(GLOBAL_CONSTANTS.LE_SET_DATA, data);
  }
  
  /**
   * get a reference to the saved data object
   * @returns {DataIF}
   */
  getData() {
    return this.data;
  }
  
  /**
   * this saves a reference to the given user object (doesn't deep copy it).
   * it also fires an event so that listeners who are interested in this are notified.
   */
  setUserObject(user: UserIF) {
    this.user = user;
    // emit event that user has signed in
    this.emit(GLOBAL_CONSTANTS.LE_SET_USER, user);
  }
  
  /**
   * get a reference to the saved user object
   * @returns {UserIF}
   */
  getUserObject() {
    return this.user;
  }
  
  /** gets the uid field of the userObject */
  getUserId() {
    return this.getUserObject().uid;
  }
  
  /** this tells firebase to start sign-in using Google (vs anon auth) */
  forceSignIn() {
    persistence.forceSignIn(this);
  }
  
  /** this tells firebase to initiate sign-out (of users who came in thru any
   *  auth providers - Google and anon) */
  forceSignOut() {
    persistence.forceSignOut(this);
  }
  
  /** setup the internal firebase object */
  initFirebase() {
    this.firebase = require("firebase");
    const config = require('../../global/constants').FIREBASE_CONFIG;
    this.firebase.initializeApp(config);
  }
  
  /**
   * get a ref to the firebase instance
   * @returns {firebase|*}
   */
  getFirebase() {
    return this.firebase;
  }
  
  /** this is a convenience method that allows you to get the firebase server
   * timestamp object
   */
  getFirebaseServerTimestampObject() {
    return this.firebase.database.ServerValue.TIMESTAMP
  }
  
  /**
   * get a ref to the firebase.database() instance
   * @returns {*|firebase.database.Database|!firebase.database.Database}
   */
  getDatabase() {
    return this.firebase.database();
  }
  
  /** creates the event emitter */
  initEventEmitter() {
    this.eventEmitter = new events.EventEmitter();
  }
  
  /** disconnect the socket connection */
  disconnectSocket() {
    this.socket.disconnect();
  }
  
  /** convenience method to emit an event to the server */
  emitToServer(eventName, payload) {
    this.socket.emit(eventName, payload);
  }
  
  /** convenience method to emit an event */
  emit(eventName, payload) {
    if (LOGGING_ENABLED){
      console.log(`emit: eventName ${eventName} fired`);
      console.dir(payload);
    }
    this.eventEmitter.emit(eventName, payload);
  }
  
  /** convenience method to listen to event
   * @returns the listener that is passed as param
   */
  addListener(eventName, listener) {
    function logging_listener() {
      if (LOGGING_ENABLED) {
        console.log(`listener: for eventName ${eventName} responding`);
      }
      listener.apply(this, arguments);
    }
    this.eventEmitter.addListener(
      eventName, logging_listener
    );
    return logging_listener;
  }
  
  /** convenience method to remove listener for event */
  removeListener(eventName, listener) {
    this.eventEmitter.removeListener(eventName, listener);
  }
  
}

/** create a singleton that will be used everywhere in the project */
const applicationContext = new ApplicationContext();

/** export the singleton */
export {applicationContext}