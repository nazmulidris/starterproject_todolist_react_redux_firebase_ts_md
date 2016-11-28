package com.r3bl.todo_app.ui.reduxdebug;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.ReduxDebugLog;
import com.r3bl.todo_app.todoapp.R;

/**
 * Created by nazmul on 11/27/16.
 */

public class DebugDetailsDialogFragment extends DialogFragment {

private TextView text_title;
private TextView text_description;

public DebugDetailsDialogFragment() {
  // empty constructor is required
}

public static DebugDetailsDialogFragment newInstance(int position) {
  DebugDetailsDialogFragment fragment = new DebugDetailsDialogFragment();
  Bundle args = new Bundle();
  args.putInt("position", position);
  fragment.setArguments(args);
  return fragment;
}

/**
 * more info - https://guides.codepath.com/android/Using-DialogFragment#full-screen-dialog
 */
@Nullable
@Override
public View onCreateView(LayoutInflater inflater,
                         @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
  View view = inflater.inflate(R.layout.debug_dialog_layout, container);

  text_title = (TextView) view.findViewById(R.id.text_title);
  text_description = (TextView) view.findViewById(R.id.text_description);

  int position = getArguments().getInt("position", -1);
  if (position != -1) {
    ReduxDebugLog.HistoryEntry historyEntry = App.getContext(getActivity().getApplicationContext())
                                                 .getReduxLog()._stateHistory.get(position);
    text_title.setText(historyEntry.time);
    StringBuilder sb = new StringBuilder();
    sb.append("Action: ").append(historyEntry.actionParam.toString());
    sb.append("\nOld State: ").append(historyEntry.oldState.toString());
    sb.append("\nNew State: ").append(historyEntry.newState.toString());
    text_description.setText(sb.toString());
  }

  setStyle(STYLE_NORMAL, R.style.Dialog_FullScreen);

  return view;
}

@Override
public void onResume() {
// Get existing layout params for the window
  ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
  // Assign window properties to fill the parent
  params.width = WindowManager.LayoutParams.MATCH_PARENT;
  params.height = WindowManager.LayoutParams.MATCH_PARENT;
  getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
  // Call super onResume after sizing
  super.onResume();
}

}// end class DebugDetailsDialogFragment