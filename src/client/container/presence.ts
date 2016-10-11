/// <reference path="../../../typings/globals/node/index.d.ts" />

/**
 * these functions handle dealing presence state with firebase
 */

const GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;
const LOGGING_ENABLED = require('../../global/constants').LOGGING_ENABLED;

import {UserIF, PresenceIF} from "./interfaces";

const lodash = require('lodash');

const PRESENCE_STATES = {
  ONLINE: "online",
  OFFLINE: "offline",
  IDLE: "idle",
  AWAY: "away",
};

const DB_CONST = {
  CONNECTED: ".info/connected",
  USER_LIST_ROOT: "USERS_ONLINE",
};

let currentStatus: string;
let myUserRef: any;

function initPresence(ctx) {
  
  // Get a reference to the presence data in Firebase.
  const userListRef = _getUserListRootRef(ctx);
  
  // Generate a reference to a new location for my user with push.
  myUserRef = userListRef.push();
  console.log('creating a new user presence obj');
  
  _getConnectedStateRef(ctx)
    .on(
      "value", (snap)=> {
        if (snap.val()) {
          // If we lose our internet connection, we want ourselves removed from the list.
          myUserRef.onDisconnect()
                   .remove();
          
          // Set our initial online status.
          console.log('_getConnectedStateRef tiggered setUserStatus()');
          setUserStatus(PRESENCE_STATES.ONLINE, ctx);
        } else {
          
          // We need to catch anytime we are marked as offline and then set the correct
          // status. We could be marked as offline 1) on page load or 2) when we lose our
          // internet connection temporarily.
          console.log('_getConnectedStateRef tiggered setUserStatus()');
          setUserStatus(PRESENCE_STATES.OFFLINE, ctx);
        }
      }
    );
  
  ctx.addListener(
    GLOBAL_CONSTANTS.LE_SET_USER, (userObject: UserIF)=> {
      console.log('local event tiggered setUserStatus()');
      setUserStatus(PRESENCE_STATES.ONLINE, ctx);
    }
  );
  
  //
  // more info - https://github.com/soixantecircuits/idle-js
  //
  const idleJs = require("idle-js");
  new idleJs(
    {
      idle: 10000, // idle time in ms
      // events that will trigger the idle resetter
      events: ['mousemove', 'keydown', 'mousedown', 'touchstart'],
      // callback function to be executed after idle time
      onIdle: function () {
        console.log('IDLE');
        setUserStatus(PRESENCE_STATES.IDLE, ctx);
      },
      // callback function to be executed after back form idleness
      onActive: function () {
        console.log('ACTIVE');
        setUserStatus(PRESENCE_STATES.ONLINE, ctx);
      },
      // callback function to be executed when window become hidden
      onHide: function () {
        console.log('HIDE');
        setUserStatus(PRESENCE_STATES.AWAY, ctx)
      },
      // callback function to be executed when window become visible
      onShow: function () {
        console.log('SHOW');
        setUserStatus(PRESENCE_STATES.ONLINE, ctx)
      },
      keepTracking: true, // set it to false of you want to track only once
      startAtIdle: false // set it to true if you want to start in the idle state
    }
  ).start();
  
}

function setUserStatus(status: string, ctx: any) {
  // Set our status in the list of online users.
  currentStatus = status;
  
  if (ctx.isUserSet() && !ctx.getUser().isAnonymous) {
    const presenceObject: PresenceIF = {
      status: status,
      user: ctx.getUser(),
    };
    
    // this works even when the myUserRef is removed onDisconnect()
    // set will just recreate a new object at the root level with the
    // old key!
    myUserRef.set(presenceObject);
    
    console.log(`setUserStatus: ` + JSON.stringify(presenceObject));
  } else {
    console.log('setUserStatus: not possible since user is anonymous!');
  }
  
}


function _getConnectedStateRef(ctx) {
  return ctx.getDatabase()
            .ref(DB_CONST.CONNECTED);
}

function _getUserListRootRef(ctx) {
  return ctx.getDatabase()
            .ref(DB_CONST.USER_LIST_ROOT);
}

export{
  initPresence,
}