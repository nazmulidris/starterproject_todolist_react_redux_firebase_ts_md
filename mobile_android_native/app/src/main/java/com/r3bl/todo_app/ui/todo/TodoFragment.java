package com.r3bl.todo_app.ui.todo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.brianegan.bansa.Subscription;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.todoapp.R;


public class TodoFragment extends Fragment {
protected TextView     text_title;
protected TextView     text_description;
private   Subscription subscriber;

public TodoFragment() {
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

  App ctx = (App) getActivity().getApplicationContext();

  View view = inflater.inflate(R.layout.fragment_todo, container, false);

  text_title = (TextView) view.findViewById(R.id.todo_text_title);
  text_description = (TextView) view.findViewById(R.id.todo_text_description);

  _updateUI(ctx);

  _bindToReduxState(ctx);

  return view;

}

@Override
public void onDestroyView() {
  if (subscriber != null) subscriber.unsubscribe();
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

private void _updateUI(App ctx) {
  text_title.setText(String.format("redux state changed at: %s",
                                   ctx.getTime()));
  text_description.setText(ctx.getReduxState().toString());
}

}// end class TodoFragment