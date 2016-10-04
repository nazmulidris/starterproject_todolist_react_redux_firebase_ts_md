import React, {Component} from 'react';
import {applicationContext} from '../container/context';
import {InputArea} from './inputarea';
import {GroupChat} from './groupchat';
import {TodoList} from './todolist';
import {Header} from './header';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
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
 */
@connect(
  (state) => ({
    todoArray: state.data.todoArray,
    user: state.user
  }),
  (dispatch) => bindActionCreatorsToFirebase(actions, dispatch, applicationContext)
)

class App extends Component {
  
  constructor(props) {
    super(props);
    
    // these keys control the state of the snackbar component
    this.state = {
      snackbar_open: false,
      snackbar_msg: "Default Message",
    }
  }
  
  render() {
    
    return (
      <MuiThemeProvider muiTheme={muiTheme}>
        <div>
          <div className="container">
            
            <Header />
            
            <div className="content">
              <div className="side_container_in_content">
                <div id="scroll_todolist" className="todolist_items">
                  <TodoList />
                </div>
                <div className="input_area">
                  <InputArea />
                </div>
              </div>
              <div className="groupchat_in_content">
                <GroupChat />
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
    
    socket.on(GLOBAL_CONSTANTS.REMOTE_MESSAGE_FROM_SERVER, ::this.rcvMsgFromServer);
    
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