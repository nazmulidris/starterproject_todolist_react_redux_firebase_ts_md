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
    
    const {
      todoArray,
      action_toggle_todo_index,
    } = this.props;
    
    let jsxElements = [];
    
    if (!lodash.isNil(todoArray)) {
      todoArray.forEach(
        (todoItem, index)=> {
          jsxElements.push(
            <TodoListItem
              key={index}
              index={index}
              todoItem={todoItem}
              action_toggle_todo_index={action_toggle_todo_index}
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
    
    const todoItem = this.props.todoItem;
    const done = todoItem.done;
    const text = todoItem.item;
    
    if (done) {
      return (
        <ListItem
          primaryText={text}
          onClick={::this.onClick}
          rightIcon={<CheckBox />}
        />
      );
    } else {
      return (
        <ListItem
          primaryText={text}
          onClick={::this.onClick}
          rightIcon={<CheckBoxOutlineBlank />}
        />
      );
    }
    
  }
  
  onClick(e) {
    let {
      index,
      action_toggle_todo_index
    } = this.props;
    action_toggle_todo_index(index);
  }
  
}

export {TodoList}