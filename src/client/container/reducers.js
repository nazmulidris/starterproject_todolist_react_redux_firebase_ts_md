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
function reducer_data_add_todo(state, action) {
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
        return {
            user: state.user,
            data: data_copy
        };
    }
    else {
        return state;
    }
}
function reducer_data_toggle_todo(state, action) {
    try {
        var index = action.payload;
        var data_copy = lodash.cloneDeep(context_1.applicationContext.getData());
        var todoObject = data_copy.todoArray[index];
        todoObject.done = !todoObject.done;
        return {
            user: state.user,
            data: data_copy
        };
    }
    catch (e) {
        console.log("_modifyTodoItem had a problem ...");
        console.dir(e);
    }
    return state;
}
function reducer_main(state, action) {
    if (state === void 0) { state = initialState; }
    if (action.type === actions.TYPES.ADD_TODO) {
        return reducer_data_add_todo(state, action);
    }
    else if (action.type === actions.TYPES.TOGGLE_TODO) {
        return reducer_data_toggle_todo(state, action);
    }
    else if (action.type === actions.TYPES.SET_STATE_DATA) {
        return {
            data: action.payload,
            user: state.user
        };
    }
    else if (action.type === actions.TYPES.SET_STATE_USER) {
        return {
            data: state.data,
            user: action.payload
        };
    }
}
exports.reducer_main = reducer_main;
//# sourceMappingURL=reducers.js.map