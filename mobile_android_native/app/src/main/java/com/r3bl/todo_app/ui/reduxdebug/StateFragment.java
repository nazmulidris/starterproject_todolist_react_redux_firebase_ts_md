package com.r3bl.todo_app.ui.reduxdebug;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.ReduxDebugLog;
import com.r3bl.todo_app.container.utils.DiffMatchPatch;
import com.r3bl.todo_app.todoapp.R;

import static com.r3bl.todo_app.ui.reduxdebug.StateFragment.Type.Action;

/**
 * Created by nazmul on 12/17/16.
 */

public class StateFragment extends Fragment {

public String                     actionType;
public int                        position;
public ReduxDebugLog.HistoryEntry historyEntry;

enum Type {Action, Diff, OldState, NewState}

public TextView text_title;
public TextView text_description;

public StateFragment() {
  // empty constructor required
}

public static StateFragment newInstance(Type type, int position) {
  StateFragment fragment = new StateFragment();
  Bundle args = new Bundle();
  args.putString("type", type.toString());
  args.putInt("position", position);
  fragment.setArguments(args);
  return fragment;
}

@Override
public View onCreateView(LayoutInflater inflater,
                         @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
  View view = inflater.inflate(R.layout.debug_dialog, container, false);

  text_title = (TextView) view.findViewById(R.id.debug_text_title);
  text_description = (TextView) view.findViewById(R.id.debug_text_description);

  actionType = getArguments().getString("type", "");
  position = getArguments().getInt("position", -1);
  if (position != -1) {
    historyEntry = App.getContext(getActivity())
                      .getReduxLog()._stateHistory.get(position);
  }

  new AsyncRender(this, actionType).execute();

  return view;
}

/**
 * this code is NOT run in the EDT! ... runs in a background thread
 */
public String _render() {
  if (historyEntry == null) return "N/A";
  // do something different with type & position ...
  StringBuilder sb = new StringBuilder();

  if (actionType.equals(Action.toString())) {
    // action
    sb.append(historyEntry.actionParam.toString());
  } else if (actionType.equals(Type.Diff.toString())) {
    // diff
    sb.append(
      DiffMatchPatch.diff(historyEntry.oldState,
                          historyEntry.newState));
  } else if (actionType.equals(Type.NewState.toString())) {
    // new state
    sb.append(historyEntry.newState.toString());
  } else if (actionType.equals(Type.OldState.toString())) {
    // old state
    sb.append(historyEntry.oldState.toString());
  }

  return sb.toString();
}


public class AsyncRender extends AsyncTask<Void, Void, String> {

  private final String        actionType;
  private       StateFragment fragment;

  public AsyncRender(StateFragment stateFragment, String actionType) {
    fragment = stateFragment;
    this.actionType = actionType;
  }

  @Override
  protected String doInBackground(Void... params) {
    return fragment._render();
  }

  @Override
  protected void onPreExecute() {
    fragment.text_title.setText("Loading...");
    fragment.text_description.setText("Activating awesomeness...");
  }

  @Override
  protected void onPostExecute(String descriptionText) {
    fragment.text_title.setText(historyEntry.time);
    if (actionType.equals(Type.Diff.toString())) {
      fragment.text_description.setText(Html.fromHtml(descriptionText, Html.FROM_HTML_MODE_COMPACT));
    } else {
      fragment.text_description.setText(descriptionText);
    }
  }
}// end AsyncRender class

}// end StateFragment class