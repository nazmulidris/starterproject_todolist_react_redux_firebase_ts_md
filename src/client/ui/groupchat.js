import React, {Component} from 'react';
import {applicationContext} from '../container/context';
import {Paper, Card, CardActions, CardHeader, CardText, FlatButton} from 'material-ui';
const GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;

/**
 * Props that are passed to it: n/a
 * State that it manages: n/a
 * Context that is requires: app
 */
class GroupChat extends Component {
  
  constructor(props, context) {
    super(props, context);
  }
  
  render() {
    const style = {
      height: 100,
      width: "100%",
      padding: 20,
      textAlign: 'center',
      display: 'inline-block',
    };
    
    return (
      <div>
        <Paper style={style}>
          This is where the group chat functionality goes
        </Paper>
      </div>
    );
  }
  
  /** tell react that we have this object in the context ... note static keyword */
  static contextTypes = {
    app: React.PropTypes.object.isRequired,
  }
  
}// end class GroupChat

export {GroupChat}