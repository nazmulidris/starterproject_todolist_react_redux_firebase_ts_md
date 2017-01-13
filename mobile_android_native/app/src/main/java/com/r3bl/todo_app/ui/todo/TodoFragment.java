package com.r3bl.todo_app.ui.todo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.brianegan.bansa.Subscription;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.Actions;
import com.r3bl.todo_app.todoapp.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * the todolist ui that is bound to the state
 */
public class TodoFragment extends Fragment {
protected EditText        textInput;
protected RecyclerView    recyclerView;
private   Subscription    subscriber;
private   TodoListAdapter rv_adapter;

public TodoFragment() {
}

@Override
public View onCreateView(LayoutInflater inflater,
                         ViewGroup container,
                         Bundle savedInstanceState) {

  App ctx = (App) getActivity().getApplicationContext();

  View view = inflater.inflate(R.layout.todo_fragment, container, false);

  textInput = (EditText) view.findViewById(R.id.todo_text_input);
  recyclerView = (RecyclerView) view.findViewById(R.id.todo_list_recyclerview);

  EventBus.getDefault().register(this);

  _setupRecyclerView(view, ctx);

  _updateUI(ctx);

  _bindToReduxState(ctx);

  return view;

}

private void _setupRecyclerView(View view, App ctx) {
  RecyclerView rv = (RecyclerView) view.findViewById(R.id.todo_list_recyclerview);
  rv_adapter = new TodoListAdapter(ctx);
  rv.setAdapter(rv_adapter);
  rv.setLayoutManager(new LinearLayoutManager(ctx));
  rv.setHasFixedSize(true);
}

//
// Event Bus Stuff
//

@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(LE_AddTodoListItem event) {
  String item = textInput.getText().toString();
  App.getContext(getActivity())
     .getReduxStore()
     .dispatch(new Actions.AddTodoItem(item, false));
  textInput.setText("");
}

public static class LE_AddTodoListItem {
}

//
// Redux binding and unbinding
//

@Override
public void onDestroyView() {
  if (subscriber != null) subscriber.unsubscribe();
  EventBus.getDefault().unregister(this);
  App.log("TodoFragment", "onDestroyView: [RAN]");
  super.onDestroyView();
}

private void _bindToReduxState(App ctx) {
  subscriber = ctx.getReduxStore().subscribe(state -> {
    App.log("TodoFragment", "redux state changed");
    // update the UI w/ stuff in the state object
    _updateUI(ctx);
  });
  App.log("TodoFragment", "_bindToReduxState: [RAN]");
}

//
// Update the UI with the data in the state
//

private void _updateUI(App ctx) {
  rv_adapter.notifyDataSetChanged();
}

}// end class TodoFragment