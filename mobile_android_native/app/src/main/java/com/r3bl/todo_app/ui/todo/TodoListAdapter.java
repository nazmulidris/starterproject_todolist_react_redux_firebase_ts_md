package com.r3bl.todo_app.ui.todo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.Actions;
import com.r3bl.todo_app.container.redux.state.Data;
import com.r3bl.todo_app.todoapp.R;

/**
 * Created by nazmul on 1/12/17.
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {
private final App                      _ctx;

public TodoListAdapter(App ctx) {
  this._ctx = ctx;
}

//
// Adapter implementation
//

@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
  View view = LayoutInflater.from(_ctx)
                            .inflate(R.layout.todo_listview_row, parent, false);
  return new ViewHolder(view);
}

@Override
public void onBindViewHolder(ViewHolder holder, int position) {
  Data.TodoItem row = _ctx.getReduxState().data.todoArray.get(position);
  holder.item.setText(row.item);
  holder.done.setText(String.valueOf(row.done));
}

@Override
public int getItemCount() {
  return _ctx.getReduxState().data.todoArray.size();
}

//
// ViewHolder
//

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  public TextView item;
  public TextView done;

  public ViewHolder(View itemView) {
    super(itemView);
    item = (TextView) itemView.findViewById(R.id.todo_listview_row_item);
    done = (TextView) itemView.findViewById(R.id.todo_listview_row_done);
    itemView.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    _ctx.getReduxStore().dispatch(new Actions.ToggleTodoItem(getAdapterPosition()));
  }

}// end ViewHolder

}// end TodoListAdapter