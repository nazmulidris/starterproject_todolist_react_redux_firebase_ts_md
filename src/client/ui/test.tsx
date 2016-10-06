import * as React from "react";
import {Paper, Card, CardActions, CardHeader, CardText, FlatButton} from 'material-ui';

export interface HelloProps { compiler: string; framework: string; }

export class Test extends React.Component<HelloProps, {}> {
  
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
          This is where the group chat functionality goes.
          Hello from {this.props.compiler} and {this.props.framework}!
        </Paper>
      </div>
    );
  }
  
}
