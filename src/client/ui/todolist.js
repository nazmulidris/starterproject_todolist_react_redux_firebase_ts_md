import React, {Component} from 'react';
const GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;
import {applicationContext} from '../container/context';
const lodash = require('lodash');
import {
  List, ListItem
}
  from 'material-ui';
import CheckBox from "material-ui/svg-icons/toggle/check-box";
import CheckBoxOutlineBlank from "material-ui/svg-icons/toggle/check-box-outline-blank";

/**
 * this renders the list of todoItems using the data in the LE_SET_DATA event.
 *
 * more info - http://stackoverflow.com/questions/22876978/loop-inside-react-jsx
 *
 * Props that are passed to it: n/a
 * State that it manages: n/a
 */
class TodoList extends Component {
  
  constructor(props, context) {
    super(props, context);
  }
  
  componentWillMount() {
    
    this.setState({todoArray: null});
    
    this.le_setDataListener = applicationContext.addListener(
      GLOBAL_CONSTANTS.LE_SET_DATA,
      (data)=> {
        this.setState({todoArray: data.todoArray});
      }
    );
    
  }
  
  componentWillUnmount() {
    applicationContext.removeListener(GLOBAL_CONSTANTS.LE_SET_DATA, this.le_setDataListener);
  }
  
  componentDidMount() {
    this.scrollToBottom();
  }
  
  componentDidUpdate() {
    this.scrollToBottom();
  }
  
  scrollToBottom() {
    setTimeout(
      () => {
        let div = document.getElementById('scroll_todolist');
        div.scrollTop = div.scrollHeight - div.clientHeight;
        div.animate({scrollTop: div.scrollHeight});
      }, 0
    );
  }
  
  render() {
    
    const todoArray = this.state.todoArray;
    
    let jsxElements = [];
    
    if (!lodash.isNil(todoArray)) {
      todoArray.forEach(
        (todoItem, index)=> {
          jsxElements.push(
            <TodoListItem
              key={index}
              index={index}
              todoItem={todoItem}
            />
          );
        }
      );
    }
    
    return (
      <List className="todolist">{jsxElements}</List>
    );
    
  }
  
}

/**
 * this is a simple component which renders a single todoItem based on the props
 *
 * Props that are passed to it: todoItem
 * State that it manages: n/a
 */
class TodoListItem extends Component {
  
  render() {
    let todoItem = this.props.todoItem;
    
    if (todoItem.done) {
      return (
        <ListItem
          primaryText={todoItem.item}
          onClick={::this.onClick}
          rightIcon={<CheckBox />}
        />
      );
    } else {
      return (
        <ListItem
          primaryText={todoItem.item}
          onClick={::this.onClick}
          rightIcon={<CheckBoxOutlineBlank />}
        />
      );
    }
    
  }
  
  onClick(e) {
    let index = this.props.index;
    applicationContext.emit(GLOBAL_CONSTANTS.LE_MODIFY_TODO_ITEM, index);
  }
  
}

export {TodoList}