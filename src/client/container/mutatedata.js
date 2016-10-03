/// <reference path="../../../typings/globals/node/index.d.ts" />
"use strict";
var GLOBAL_CONSTANTS = require('../../global/constants').GLOBAL_CONSTANTS;
var persistence = require('./firebase');
var context_1 = require('./context');
var lodash = require('lodash');
/** this sets up all the event listeners for data mutation events */
function init(ctx) {
    ctx.addListener(GLOBAL_CONSTANTS.LE_ADD_TODO_ITEM, _addTodoItem);
    ctx.addListener(GLOBAL_CONSTANTS.LE_MODIFY_TODO_ITEM, _modifyTodoItem);
}
exports.init = init;
/**
 * this actually adds the given todo_text string to the todoArray in DataIF in the
 * context
 * more info: https://lodash.com/docs/4.16.2#clone
 */
function _addTodoItem(todo_text) {
    if (!lodash.isNil(todo_text)) {
        var data_copy = lodash.cloneDeep(context_1.applicationContext.getData());
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
        persistence.saveDataToFirebase(data_copy, context_1.applicationContext);
    }
}
/**
 * this actually modifies the completed state of the todoItem that is selected in the UI
 */
function _modifyTodoItem(index) {
    try {
        var data_copy = lodash.cloneDeep(context_1.applicationContext.getData());
        var todoObject = data_copy.todoArray[index];
        todoObject.done = !todoObject.done;
        persistence.saveDataToFirebase(data_copy, context_1.applicationContext);
    }
    catch (e) {
        console.log("_modifyTodoItem had a problem ...");
        console.dir(e);
    }
}
/** expose these functions publicly */
//# sourceMappingURL=mutatedata.js.map