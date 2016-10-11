import * as React from "react";
import {
  Paper, Card, CardActions, CardHeader, CardText, FlatButton, List, ListItem, Avatar
}
  from 'material-ui';
import {applicationContext} from '../container/context';
import {ChatMessageIF} from "../container/interfaces";
import Props = __React.Props;

const GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;

const lodash = require('lodash');

export class GroupChat extends React.Component<Props, {}> {
  
  constructor(props, context) {
    super(props, context);
    this.state = {chatMessageList: []};
  }
  
  componentDidMount() {
    this.scrollToBottom();
    const socket = applicationContext.getSocket();
    socket.on(
      GLOBAL_CONSTANTS.REMOTE_MESSAGE_FROM_SERVER, (data)=> {
        this.rcvMsgFromServer(data)
      }
    );
  }
  
  componentDidUpdate() {
    this.scrollToBottom();
  }
  
  scrollToBottom() {
    setTimeout(
      () => {
        let div = document.getElementById('scroll_chatlist');
        div.scrollTop = div.scrollHeight - div.clientHeight;
        div.animate({scrollTop: div.scrollHeight});
      }, 0
    );
  }
  
  rcvMsgFromServer(data: ChatMessageIF) {
    const {chatMessageList} = this.state;
    let copy = lodash.clone(chatMessageList);
    copy.push(data);
    this.setState({chatMessageList: copy});
  }
  
  render() {
    
    const style = {
      height: "50pt",
      width: "95%",
      margin: 20,
      padding: 20,
      textAlign: 'center',
      display: 'inline-block',
    };
    
    const {chatMessageList} = this.state;
    
    if (chatMessageList.length == 0) {
      return this._renderEmptyMsg(style);
    } else {
      return this._renderMessageList(chatMessageList);
    }
  }
  
  _renderMessageList(chatMessageList) {
    
    let jsxElements = [];
    
    if (!lodash.isNil(chatMessageList)) {
      chatMessageList.forEach(
        (chatMessage, index)=> {
          jsxElements.push(
            <ChatMessageListItem
              key={index}
              index={index}
              chatMessage={chatMessage}
            />
          );
        }
      );
    }
    
    return (
      <List className="todolist">{jsxElements}</List>
    );
    
  }
  
  _renderEmptyMsg(style: {height: string; width: string; margin: number; padding: number; textAlign: string; display: string}) {
    return (
      <div>
        <Paper zDepth={3} style={style}>
          There are no messages right now. Please type a message below.
        </Paper>
      </div>
    );
  }
  
}

class ChatMessageListItem extends React.Component<Props, {}> {
  
  render() {
    
    const chatMessage: ChatMessageIF = this.props.chatMessage;
    const avatar_icon_size = 32;
    
    return (
      <ListItem
        primaryText={chatMessage.message}
        rightIcon={<Avatar size={avatar_icon_size} src={chatMessage.photoURL}/>}
      />
    );
    
  }
  
}
