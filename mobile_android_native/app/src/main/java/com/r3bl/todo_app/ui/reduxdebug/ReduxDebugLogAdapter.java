package com.r3bl.todo_app.ui.reduxdebug;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.ReduxDebugLog;
import com.r3bl.todo_app.todoapp.R;

import java.util.ArrayList;

/**
 * Created by nazmul on 11/26/16.
 */

public class ReduxDebugLogAdapter
  extends RecyclerView.Adapter<ReduxDebugLogAdapter.ViewHolder> {
private final ArrayList<ReduxDebugLog.HistoryEntry> _list;
private final App                                   _ctx;

//
// Constructor
//

public ReduxDebugLogAdapter(App ctx) {
  _ctx = ctx;
  _list = ctx.getReduxLog()._stateHistory;
}

//
// Adapter implementation
//

@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
  View view = LayoutInflater.from(_ctx)
                            .inflate(R.layout.debug_layout_list_row, parent, false);
  return new ViewHolder(view);
}

@Override
public void onBindViewHolder(ViewHolder holder, int position) {
  ReduxDebugLog.HistoryEntry row = _list.get(position);
  StringBuffer sb = new StringBuffer();
  sb.append("Time:").append(row.time)
    .append(", Action:").append(row.actionParam.getClass().getSimpleName());
  holder.text.setText(sb.toString());
}

@Override
public int getItemCount() {
  return _list.size();
}

//
// ViewHolder
//

public class ViewHolder extends RecyclerView.ViewHolder {
  public TextView text;
  public Button   button;

  public ViewHolder(View itemView) {
    super(itemView);
    text = (TextView) itemView.findViewById(R.id.text);
    button = (Button) itemView.findViewById(R.id.button);
  }
}

}// end class ReduxDebugLogAdapter