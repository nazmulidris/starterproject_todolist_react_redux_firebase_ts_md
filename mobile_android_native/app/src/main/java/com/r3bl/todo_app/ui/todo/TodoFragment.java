package com.r3bl.todo_app.ui.todo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.todoapp.R;


public class TodoFragment extends Fragment {
protected TextView titleTextView;
protected TextView descriptionTextView;

public TodoFragment() {
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

  App ctx = (App) getActivity().getApplicationContext();

  View view = inflater.inflate(R.layout.fragment_groupchat_layout, container, false);

  titleTextView = (TextView) view.findViewById(R.id.titleText);
  descriptionTextView = (TextView) view.findViewById(R.id.descriptionText);

  _updateUI(ctx);

  _bindToReduxState(ctx);

  return view;

}

private void _bindToReduxState(App ctx) {
  App.log("TodoFragment", "_bindToReduxState: [START]");
  ctx.getReduxStore().subscribe(state -> {
    App.log("TodoFragment", "redux state changed");
    // update the UI w/ stuff in the state object
    _updateUI(ctx);
  });
  App.log("TodoFragment", "_bindToReduxState: [END]");
}

private void _updateUI(App ctx) {
  titleTextView.setText(String.format("redux state changed at: %s",
                                      ctx.getTime()));
  descriptionTextView.setText(ctx.getReduxState().toString());
}

@Override
public void onAttach(Context context) {
  super.onAttach(context);
}

@Override
public void onDetach() {
  super.onDetach();
}

}// end class TodoFragment