package com.r3bl.todo_app.container.redux;

import com.brianegan.bansa.Action;
import com.r3bl.todo_app.container.redux.state.State;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by nazmul on 11/13/16.
 */
public class ReduxDebugLog {

public ArrayList<HistoryEntry> _stateHistory;

public ReduxDebugLog() {
  _stateHistory = new ArrayList<HistoryEntry>();
}

public void push(String time,
                 State oldState,
                 Action actionParam,
                 State newState) {

  _stateHistory.add(new HistoryEntry(time, oldState, actionParam, newState));
  EventBus.getDefault().post(new Event());
}

@Override
public String toString() {
  StringBuffer sb = new StringBuffer();
  for (HistoryEntry historyEntry : _stateHistory) {
    sb.append(historyEntry.toString());
  }
  return sb.toString();
}

public class HistoryEntry {
  public String time;
  public State  oldState;
  public Action actionParam;
  public State  newState;

  public HistoryEntry(String time,
                      State oldState,
                      Action actionParam,
                      State newState) {
    this.time = time;
    this.oldState = oldState;
    this.actionParam = actionParam;
    this.newState = newState;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
    sb.append(">> TIME: ").append(time);
    sb.append("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
    //sb.append("oldState: ").append(oldState.toString());
    //sb.append("\n");
    sb.append(">> ACTION: ").append(actionParam);
    sb.append("\n>> STATE: ").append(newState.toString());
    sb.append("\n");
    return sb.toString();
  }

}

//
// MessageEvent fired over EventBus
//
public class Event {
}

}// end class MyApplicationReduxLog