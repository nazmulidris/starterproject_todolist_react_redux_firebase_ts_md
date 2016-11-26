package com.r3bl.todo_app.ui.groupchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.todoapp.R;


public class GroupChatFragment extends Fragment {

protected TextView titleTextView;
protected TextView descriptionTextView;

public GroupChatFragment() {
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

  App ctx = (App) getActivity().getApplicationContext();

  View view = inflater.inflate(R.layout.fragment_groupchat_layout, container, false);

  titleTextView = (TextView) view.findViewById(R.id.titleText);
  descriptionTextView = (TextView) view.findViewById(R.id.descriptionText);

  titleTextView.setText("GroupChatFragment");
  descriptionTextView.setText("Group chat details");

  _bindToReduxState(ctx);

  return view;

}

private void _bindToReduxState(App ctx) {
  App.log("GroupChat", "_bindToReduxState: [START]");
  ctx.getReduxStore().subscribe(state -> {
  });
  App.log("GroupChat", "_bindToReduxState: [END]");
}

@Override
public void onAttach(Context context) {
  super.onAttach(context);
}

@Override
public void onDetach() {
  super.onDetach();
}

}// end class GroupChatFragment