const GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;
import {applicationContext} from '../container/context'
import React, {Component} from 'react';

const lodash = require('lodash');

import {
  TextField
}
  from 'material-ui';

/**
 * this is a simple component that allows the user to type a new todoitem (and emits
 * an event LE_ADD_TODO_ITEM. it also listens to changes in the LE_SET_USER event.
 *
 * more info
 *   - https://blog.iansinnott.com/managing-state-and-controlled-form-fields-with-react/
 *   - https://facebook.github.io/react/docs/events.html#keyboard-events
 *   - pressing enter key & onChange - http://goo.gl/hZAbTk
 *   - https://nodesource.com/blog/understanding-socketio/
 *
 * Props that are passed to it: n/a
 * State that it manages: n/a
 * Context that is requires: app
 *
 * more info
 * - http://reactkungfu.com/2016/01/react-context-feature-in-practice/
 */
class InputArea extends Component {
  
  constructor(props, context) {
    super(props, context);
    this.state = {inputValue: ""}
  }
  
  render() {
    
    const {user, type} = this.props;
    
    const labeltext_todo = `Add a todo`;
    const labeltext_chat = `Write a group chat message`;
    const labeltext_cant_chat = `Must sign in to chat`;
    let labeltext;
    
    
    if (lodash.isEqual(type, "todo")) {
      labeltext = labeltext_todo;
    } else {
      labeltext = labeltext_cant_chat;
    }
    
    if (!lodash.isNil(user)) {
      if (!user.isAnonymous) {
        // user is signed in && not anonymous
        if (lodash.isEqual(type, "todo")) {
          labeltext = `${labeltext_todo} ${user.displayName}`;
        } else {
          labeltext = `${labeltext_chat} ${user.displayName}`;
        }
      }
    }
    
    const style = {
      height: 56,
      paddingLeft: 16,
      fontFamily: "Roboto Mono",
    };
    
    return (
      <TextField
        style={style}
        hintText={labeltext}
        value={this.state.inputValue}
        onChange={::this.onChange}
        onKeyPress={::this.onKeyPress}
        fullWidth={true}
      />
    );
  }
  
  /** used to check for ENTER key presses */
  onKeyPress(e) {
  
    // ENTER is pressed
    if (e.key == 'Enter') {
      // get user input
      let newval = e.target.value;
      
      // reset the text input field
      this.setState({inputValue: ""});

      const {type, user} = this.props;
      const {app} = this.context;
      
      if (lodash.isEqual(type, "todo")) {
        // call passed action to update redux!
        this.props.action_add_todo_text(newval);
      } else {
        // chat
        if (!lodash.isNil(user)) {
          if (!user.isAnonymous) {
            // user is signed in && not anonymous
            // emit this to the via the app
            // create a ChatMessageIF and send it
            const chatMessage = {
              message: newval,
              timeStamp: new Date().getTime(),
              displayName: user.displayName,
              photoURL: user.photoURL,
              sessionId: applicationContext.getSessionId(),
            };
            app.sndMsgToServer(chatMessage);
          }else {
            app.showSnackBar("Message not sent - you must be signed in to chat");
          }
        }
        
      }
      
    }
  }
  
  /** used to sync user input w/ component state */
  onChange(e) {
    this.setState({inputValue: e.target.value});
  }
  
  /** tell react that we have this object in the context ... note static keyword */
  static contextTypes = {
    app: React.PropTypes.object.isRequired,
  }
  
}

export {InputArea}