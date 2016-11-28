package com.r3bl.todo_app.ui.reduxdebug;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.ReduxDebugLog;
import com.r3bl.todo_app.todoapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by nazmul on 11/27/16.
 */

public class DebugDetailsDialogFragment extends BottomSheetDialogFragment {

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
    sb.append("\nDiff: ").append(_diff(historyEntry.oldState.toString(),
                                       historyEntry.newState.toString()));
    sb.append("\nDiff Details: ").append(_diff2(historyEntry.oldState.toString(),
                                                historyEntry.newState.toString()));
    sb.append("\nOld State: ").append(historyEntry.oldState.toString());
    sb.append("\nNew State: ").append(historyEntry.newState.toString());
    text_description.setText(sb.toString());
  }

  setStyle(STYLE_NORMAL, R.style.Dialog_FullScreen);

  return view;
}

private String _diff(String text1, String text2) {
  try {
    StringTokenizer at = new StringTokenizer(text1, " ");
    StringTokenizer bt = null;
    int i = 0, token_count = 0;
    String token = null;
    boolean flag = false;
    List<String> missingWords = new ArrayList<String>();
    while (at.hasMoreTokens()) {
      token = at.nextToken();
      bt = new StringTokenizer(text2, " ");
      token_count = bt.countTokens();
      while (i < token_count) {
        String s = bt.nextToken();
        if (token.equals(s)) {
          flag = true;
          break;
        } else {
          flag = false;
        }
        i++;
      }
      i = 0;
      if (flag == false)
        missingWords.add(token);
    }
    List<String> retval = missingWords;
    StringBuilder sb = new StringBuilder();
    for (String s : retval) {
      sb.append(s).append("\n");
    }
    return sb.toString();
  } catch (Exception e) {
    return "N/A";
  }
}

private String _diff2(String text1, String text2) {
  try {
    return StringComparisonUtil.getComparisonString(text1, text2);
  } catch (Exception e) {
    return "N/A";
  }
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