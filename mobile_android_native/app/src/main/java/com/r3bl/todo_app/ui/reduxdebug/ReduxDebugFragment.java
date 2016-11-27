package com.r3bl.todo_app.ui.reduxdebug;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.brianegan.bansa.Subscription;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.ReduxDebugLog;
import com.r3bl.todo_app.todoapp.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by nazmul on 11/13/16.
 */

public class ReduxDebugFragment extends Fragment {

protected TextView             titleTextView;
private   ReduxDebugLogAdapter adapter;
private   Subscription         subscriber;

public ReduxDebugFragment() {
  // need a default constructor
}

@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
  App ctx = (App) getActivity().getApplicationContext();

  View view = inflater.inflate(R.layout.debug_layout_recyclerview, container, false);

  titleTextView = (TextView) view.findViewById(R.id.titleText);

  _updateUI(ctx);

  _bindToReduxState(ctx);

  _setupRecyclerView(view, ctx);

  return view;
}

@Override
public void onDestroyView() {
  if (subscriber != null) subscriber.unsubscribe();
  App.log("ReduxDebugFragment", "onDestroyView: [RAN]");
  super.onDestroyView();
}

private void _setupRecyclerView(View view, App ctx) {
  RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
  adapter = new ReduxDebugLogAdapter(ctx);
  rv.setAdapter(adapter);
  rv.setLayoutManager(new LinearLayoutManager(ctx));
  rv.setHasFixedSize(true);
  rv.addItemDecoration(new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL));
}

//
// Event bus stuff
//

@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(ReduxDebugLog.Event event) {
  adapter.notifyDataSetChanged();
}

@Override
public void onStart() {
  super.onStart();
  EventBus.getDefault().register(this);
}

@Override
public void onStop() {
  EventBus.getDefault().unregister(this);
  super.onStop();
}

//
// Render stuff
//

private void _updateUI(App ctx) {
  titleTextView.setText(String.format("Redux state changed at: %s",
                                      ctx.getTime()));
}

private void _bindToReduxState(App ctx) {
  subscriber = ctx.getReduxStore().subscribe(state -> {
    App.log("ReduxDebugFragment", "redux state changed");
    // update the UI w/ stuff in the state object
    _updateUI(ctx);
  });
  App.log("ReduxDebugFragment", "_bindToReduxState: [RAN]");
}

}// end class ReduxDebugFragment