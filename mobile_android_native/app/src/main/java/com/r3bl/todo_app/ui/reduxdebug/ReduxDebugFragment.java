package com.r3bl.todo_app.ui.reduxdebug;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.todoapp.R;

/**
 * Created by nazmul on 11/13/16.
 */

public class ReduxDebugFragment extends Fragment {

protected TextView titleTextView;
protected TextView descriptionTextView;

public ReduxDebugFragment() {
}

@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
  App ctx = (App) getActivity().getApplicationContext();

  View view = inflater.inflate(R.layout.debug_layout, container, false);

  titleTextView = (TextView) view.findViewById(R.id.titleText);
  descriptionTextView = (TextView) view.findViewById(R.id.descriptionText);

  _updateUI(ctx);

  _bindToReduxState(ctx);

  return view;
}

private void _updateUI(App ctx) {
  titleTextView.setText(String.format("Redux state changed at: %s",
                                      ctx.getTime()));
  descriptionTextView.setText(ctx.getReduxLog().toString());
}

private void _bindToReduxState(App ctx) {
  App.log("ReduxDebugFragment", "_bindToReduxState: [START]");
  ctx.getReduxStore().subscribe(state -> {
    App.log("ReduxDebugFragment", "redux state changed");
    // update the UI w/ stuff in the state object
    _updateUI(ctx);
  });
  App.log("ReduxDebugFragment", "_bindToReduxState: [END]");
}

}// end class ReduxDebugFragment
