import React, {Component} from 'react';
import {InputArea} from './inputarea';
import {TodoList} from './todolist';
import {Header} from './header';
import {GroupChat} from './groupchat';

import {connect} from 'react-redux';
import * as actions from '../container/actions';
import {applicationContext, bindActionCreatorsToFirebase} from '../container/context';

const GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;

/**
 * utility function library import
 * more info - https://lodash.com/
 */
const lodash = require('lodash');

/** Material UI stuff */
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import {
  lightBlue50,
  grey900,
  blueGrey200
} from 'material-ui/styles/colors';
import {
  Snackbar
}
  
  from 'material-ui';

/**
 * Material UI themes
 * look at getMuiTheme.js to find which palette keys to override in order to
 * manipulate the components that you want to colorize
 * eg: from getMuiTheme.js:
 * <pre>
 * muiTheme = (0, _merge2.default)({
 *   appBar: {
 *     color: palette.primary1Color,
 *     textColor: palette.alternateTextColor,
 *     height: spacing.desktopKeylineIncrement,
 *     titleFontWeight: _typography2.default.fontWeightNormal,
 *     padding: spacing.desktopGutter
 * },
 * </pre>
 * appBar's color is palette.primary1Color, which is overridden below...
 */
const muiTheme = getMuiTheme(
  {
    snackbar: {
      backgroundColor: blueGrey200,
    },
    palette: {
      primary1Color: lightBlue50,
      alternateTextColor: grey900,
      textColor: grey900,
    },
  }
);

/**
 * this class contains the UI components for the entire single page app
 *
 * material-ui library things:
 * 1) added <MuiThemeProvider> as the root of the app view hierarchy
 * 2) have to call the injectTapEventPlugin() in order for this things to work
 * more info - http://www.material-ui.com/#/get-started/usage
 *
 * more info on using React Context to share the App object thru out the entire view
 * hierarchy.
 * - http://reactkungfu.com/2016/01/react-context-feature-in-practice/
 *
 * mapStateToProps & mapDispatchToProps more info:
 * -
 * http://stackoverflow.com/questions/32646920/whats-the-at-symbol-in-the-redux-connect-decorator
 */
@connect(
  (state) => {
    if (lodash.isNil(state)) {
      return {
        data: null,
        user: null,
      }
    } else
      return {
        data: state.data,
        user: state.user
      }
  },
  (dispatch) => bindActionCreatorsToFirebase(actions, dispatch, applicationContext)
)

class App extends Component {
  
  constructor(props, context) {
    super(props, context);
    
    // these keys control the state of the snackbar component
    this.state = {
      snackbar_open: false,
      snackbar_msg: "Default Message",
    }
  }
  
  render() {
    
    const {
      data,
      user,
      action_add_todo_text,
      action_toggle_todo_index
    } = this.props;
    
    const todoArray = lodash.isNil(data) ? null : data.todoArray;
    
    return (
      <MuiThemeProvider muiTheme={muiTheme}>
        <div>
          <div className="container">
            
            <Header user={user}/>
            
            <div className="content">
              <div className="side_container_in_content">
                <div id="scroll_todolist" className="todolist_items">
                  <TodoList todoArray={todoArray}
                            action_toggle_todo_index={action_toggle_todo_index}/>
                </div>
                <div className="todo_input_area">
                  <InputArea type="todo"
                             user={user}
                             action_add_todo_text={action_add_todo_text}/>
                </div>
              </div>
              <div className="groupchat_container_in_content">
                <div id="scroll_chatlist" className="todolist_items">
                  <GroupChat />
                </div>
                <div className="chat_input_area">
                  <InputArea type="chat"
                             user={user}/>
                </div>
              </div>
            </div>
          
          </div>
          
          <Snackbar
            message={this.state.snackbar_msg}
            open={this.state.snackbar_open}
            autoHideDuration={1000}
            onRequestClose={::this.handleRequestClose}
          />
        
        </div>
      
      </MuiThemeProvider>
    );
    
  }
  
  handleRequestClose() {
    this.showSnackBar(null);
  }
  
  showSnackBar(msg) {
    if (lodash.isNil(msg)) {
      this.setState(
        {
          snackbar_open: false,
        }
      );
    }
    else {
      this.setState(
        {
          snackbar_msg: msg,
          snackbar_open: true,
        }
      );
    }
  }
  
  /** before the component is loaded to the DOM */
  componentWillMount() {
    
    // Listener for snackbar event
    this.le_showSnackbarListener = applicationContext.addListener(
      GLOBAL_CONSTANTS.LE_SHOW_SNACKBAR,
      (param) => {
        this.showSnackBar(param);
      }
    );
    
    // Socket stuff
    const socket = applicationContext.getSocket();
    
    socket.on(
      "connect",
      ()=> {
        this.showSnackBar("socket.io is connected to server");
      }
    );
    
  }
  
  /** before component is removed from the DOM */
  componentWillUnmount() {
    // remove listeners
    applicationContext.removeListener(GLOBAL_CONSTANTS.LE_SHOW_SNACKBAR, this.le_showSnackbarListener);
    // disconnect socket
    applicationContext.disconnectSocket();
  }
  
  /** actually write the data to the socket and update the snackbar */
  sndMsgToServer(data) {
    applicationContext.emitToServer(GLOBAL_CONSTANTS.REMOTE_MESSAGE_FROM_CLIENT, data);
    this.showSnackBar(`send text to server: ${data}`);
  }
  
  /** called by socket.io, when data is received from the server */
  rcvMsgFromServer(data) {
    this.showSnackBar(data);
  }
  
  /** put app in the context, which will be passed down to the children */
  getChildContext() {
    return {
      app: this,
    };
  }
  
  /** tell react that we have this object in the context ... note static keyword */
  static childContextTypes = {
    app: React.PropTypes.object.isRequired,
  }
  
}// end App

export {App}