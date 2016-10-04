/**
 * Created by nazmul on 9/2/16.
 */

import React, {Component} from 'react';
const lodash = require('lodash');
import {applicationContext} from '../container/context';
import {
  AppBar, Avatar, IconButton
}
  from 'material-ui';
const GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;

/**
 * material UI icons
 * google material icons site - https://design.google.com/icons/
 *
 * To construct an icon, find it in the google site. Then transform the name.
 * Eg: search for cloud, you will find "cloud download" in the "file" category ...
 *
 * so the React component class is: [File][CloudDownload]
 * so the import is: material-ui/svg-icons/[file]/[cloud-download]
 */
import ActionAccountCircle from "material-ui/svg-icons/action/account-circle";

/**
 * this class holds the app bar and the login button
 * Props that are passed to it: n/a
 * State that it manages: n/a
 * Context that is requires: app
 */
class Header extends Component {
  
  constructor(props, context) {
    super(props, context);
  }
  
  render() {
    
    const {user} = this.props;
    
    // depending on whether the user is signed in or not, provide different appbar
    let titleString = `Todo List Sample App`;
    
    const avatar_icon_size = 32;
    const custom_padding = {
      padding: 8,
    };
    
    // depending on whether the user is signed in or not, provide different appbar
    let appbar_code = "";
    if (applicationContext.isUserSet() && !applicationContext.getUser().isAnonymous) {
      // SIGNEDIN
      appbar_code =
        <AppBar className="appbar_in_main" title={titleString}
                iconElementRight={
                  <IconButton
                    style={custom_padding}
                    onTouchTap={::this.loginAction}>
                    <Avatar size={avatar_icon_size}
                            src={applicationContext.getUser().photoURL}/>
                  </IconButton>}
        />;
    } else {
      // ANON AUTH
      appbar_code =
        <AppBar className="appbar_in_main" title={titleString}
                iconElementRight={
                  <IconButton
                    style={custom_padding}
                    onTouchTap={::this.loginAction}>
                    <Avatar size={avatar_icon_size} icon={<ActionAccountCircle/>}/>
                  </IconButton>}
        />;
    }
    
    return appbar_code;
    
  }// end render()
  
  loginAction() {
    
    // get the App object from the React context
    const app = this.context.app;
    
    if (applicationContext.isUserSet()) {
      if (applicationContext.getUser().isAnonymous) {
        // go from anonauth->signedinauth
        app.showSnackBar("anonauth->signedinauth");
        applicationContext.forceSignIn();
      } else {
        // logout
        app.showSnackBar("logout");
        applicationContext.forceSignOut();
      }
    }
    else {
      // do nothing .. user must be set!
      app.showSnackBar("should never happen -> user must be set");
    }
    
  }
  
  /** tell react that we have this object in the context ... note static keyword */
  static contextTypes = {
    app: React.PropTypes.object.isRequired,
  }
  
}// end class Header

export {Header}