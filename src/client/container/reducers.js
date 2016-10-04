/// <reference path="../../../typings/globals/node/index.d.ts" />
"use strict";
var actions = require("./actions");
var context_1 = require("./context");
var lodash = require('lodash');
/**
 * these are initialized with null to match Firebase, since it doens't store any
 * empty values and set empty values to null
 * @type {{user: any; data: any}}
 */
var initialState = {
    user: null,
    data: null
};
exports.initialState = initialState;
function add_todo(state, action) {
    var todo_text = action.payload;
    if (!lodash.isNil(todo_text)) {
        var data_copy = lodash.cloneDeep(state.data);
        var todoObject = {
            item: todo_text,
            done: false
        };
        if (lodash.isNil(data_copy)) {
            data_copy = { todoArray: [todoObject] };
        }
        else {
            data_copy.todoArray.push(todoObject);
        }
        var retval = {
            user: state.user,
            data: data_copy
        };
        return retval;
    }
    else {
        return state;
    }
}
function toggle_todo(state, action) {
    try {
        var index = action.payload;
        var data_copy = lodash.cloneDeep(context_1.applicationContext.getData());
        var todoObject = data_copy.todoArray[index];
        todoObject.done = !todoObject.done;
        var retval = {
            user: state.user,
            data: data_copy
        };
        return retval;
    }
    catch (e) {
        console.log("_modifyTodoItem had a problem ...");
        console.dir(e);
    }
    return state;
}
function set_data(state, action) {
    var retval = {
        data: action.payload,
        user: state.user
    };
    return retval;
}
function set_user(state, action) {
    var retval = {
        data: state.data,
        user: action.payload
    };
    return retval;
}
function reducer_main(state, action) {
    switch (action.type) {
        case actions.TYPES.REDUX_INIT:
            return initialState;
        case actions.TYPES.ADD_TODO:
            return add_todo(state, action);
        case actions.TYPES.TOGGLE_TODO:
            return toggle_todo(state, action);
        case actions.TYPES.SET_STATE_DATA:
            return set_data(state, action);
        case actions.TYPES.SET_STATE_USER:
            return set_user(state, action);
    }
}
exports.reducer_main = reducer_main;
//# sourceMappingURL=reducers.js.map