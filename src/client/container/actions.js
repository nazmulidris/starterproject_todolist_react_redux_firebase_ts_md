"use strict";
/**
 * these are all the named redux actions
 */
var TYPES = {
    SET_STATE_USER: "SET_STATE_USER",
    SET_STATE_DATA: "SET_STATE_DATA",
    ADD_TODO: "ADD_TODO",
    TOGGLE_TODO: "TOGGLE_TODO"
};
exports.TYPES = TYPES;
function action_set_state_user(user) {
    return {
        type: TYPES.SET_STATE_USER,
        payload: user
    };
}
exports.action_set_state_user = action_set_state_user;
function action_set_state_data(data) {
    return {
        type: TYPES.SET_STATE_DATA,
        payload: data
    };
}
exports.action_set_state_data = action_set_state_data;
function action_add_todo_text(text) {
    return {
        type: TYPES.ADD_TODO,
        payload: text
    };
}
exports.action_add_todo_text = action_add_todo_text;
function action_toggle_todo_index(index) {
    return {
        type: TYPES.TOGGLE_TODO,
        payload: index
    };
}
exports.action_toggle_todo_index = action_toggle_todo_index;
//# sourceMappingURL=actions.js.map