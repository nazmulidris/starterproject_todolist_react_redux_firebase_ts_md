package com.r3bl.todo_app.ui.reduxdebug;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.ReduxDebugLog;
import com.r3bl.todo_app.todoapp.R;
import org.greenrobot.eventbus.EventBus;

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
                            .inflate(R.layout.debug_list_row_layout, parent, false);
  return new ViewHolder(view);
}

@Override
public void onBindViewHolder(ViewHolder holder, int position) {
  ReduxDebugLog.HistoryEntry row = _list.get(position);
  holder.text_time.setText("Time:" + row.time);
  holder.text_description.setText("Action:" + row.actionParam.getClass().getSimpleName());
}

@Override
public int getItemCount() {
  return _list.size();
}

//
// ViewHolder
//

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  public TextView text_description;
  public TextView text_time;

  public ViewHolder(View itemView) {
    super(itemView);
    text_description = (TextView) itemView.findViewById(R.id.text_description);
    text_time = (TextView) itemView.findViewById(R.id.text_time);
    // attach the click listener
    itemView.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    int position = getAdapterPosition();
    if (position != RecyclerView.NO_POSITION) {
      ReduxDebugLog.HistoryEntry entry = _list.get(position);
      EventBus.getDefault().post(new LE_ShowDialogWithHistoryEntryDetails(position, entry));
      // Toast.makeText(_ctx, entry.toString(), Toast.LENGTH_SHORT).show();
    } else {
      // do nothing, since nothing is selected
    }
  }

}// end class ViewHolder

public class LE_ShowDialogWithHistoryEntryDetails {
  public ReduxDebugLog.HistoryEntry entry;
  public int                        position;

  public LE_ShowDialogWithHistoryEntryDetails(int position, ReduxDebugLog.HistoryEntry entry) {
    this.position = position;
    this.entry = entry;
  }
}

}// end class ReduxDebugLogAdapter