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
import com.afollestad.materialdialogs.MaterialDialog;
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

protected TextView             text_time;
protected TextView             text_sessionId;
private   ReduxDebugLogAdapter rv_adapter;
private   Subscription         subscriber;

public ReduxDebugFragment() {
  // need a default constructor
}

@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
  App ctx = (App) getActivity().getApplicationContext();

  View view = inflater.inflate(R.layout.debug_layout_recyclerview, container, false);

  text_time = (TextView) view.findViewById(R.id.text_time);
  text_sessionId = (TextView) view.findViewById(R.id.text_sessionId);

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
  RecyclerView rv = (RecyclerView) view.findViewById(R.id.recycler_view);
  rv_adapter = new ReduxDebugLogAdapter(ctx);
  rv.setAdapter(rv_adapter);
  rv.setLayoutManager(new LinearLayoutManager(ctx));
  rv.setHasFixedSize(true);
  rv.addItemDecoration(new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL));
}

//
// Event bus stuff
//

@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(ReduxDebugLog.LE_StateAddedToReduxDebugLog event) {
  rv_adapter.notifyDataSetChanged();
}

@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(ReduxDebugLogAdapter.LE_ShowDialogWithHistoryEntryDetails event) {
  _showUsingMyDialogFragment(event);
  //_showUsingMaterialDesignDialogs(event);
}

private void _showUsingMyDialogFragment(ReduxDebugLogAdapter.LE_ShowDialogWithHistoryEntryDetails event) {
  DebugDetailsDialogFragment.newInstance(event.position)
                            .show(getFragmentManager(), "dialog");
}

/**
 * more info - https://guides.codepath.com/android/Using-DialogFragment#full-screen-dialog
 * more info - https://github.com/afollestad/material-dialogs
 */
private void _showUsingMaterialDesignDialogs(ReduxDebugLogAdapter.LE_ShowDialogWithHistoryEntryDetails event) {
  StringBuilder sb = new StringBuilder();
  sb.append(">>>>>>>>>>>>>>>>>>>>>>>>>\n")
    .append(">> ACTION: ")
    .append("\n>>>>>>>>>>>>>>>>>>>>>>>>>\n")
    .append(event.entry.actionParam.toString())
    .append("\n>>>>>>>>>>>>>>>>>>>>>>>>>\n")
    .append(">> OLD STATE: ")
    .append("\n>>>>>>>>>>>>>>>>>>>>>>>>>\n")
    .append(event.entry.oldState.toString())
    .append("\n>>>>>>>>>>>>>>>>>>>>>>>>>\n")
    .append(">> NEW STATE: ")
    .append("\n>>>>>>>>>>>>>>>>>>>>>>>>>\n")
    .append(event.entry.newState.toString());

  new MaterialDialog.Builder(getActivity())
    .title(String.format("State @ %s", event.entry.time))
    .content(sb.toString())
    .show();
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
  text_time.setText(String.format("Change @ %s",
                                  ctx.getTime()));
  String sessionId = ctx.getSessionId();
  text_sessionId.setText(String.format("Sess ID: %s...%s",
                                       sessionId.substring(0, 6),
                                       sessionId.substring(sessionId.length() - 6)));
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