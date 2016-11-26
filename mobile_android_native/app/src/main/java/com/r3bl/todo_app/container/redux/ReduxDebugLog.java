package com.r3bl.todo_app.container.redux;

import com.brianegan.bansa.Action;
import com.r3bl.todo_app.container.redux.state.State;

import java.util.ArrayList;

/**
 * Created by nazmul on 11/13/16.
 */
public class ReduxDebugLog {

private final ArrayList<HistoryEntry> _stateHistory;

public ReduxDebugLog() {
  _stateHistory = new ArrayList<HistoryEntry>();
}

public void push(String time,
                 State oldState,
                 Action actionParam,
                 State newState) {

  _stateHistory.add(new HistoryEntry(time, oldState, actionParam, newState));

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
  private final String time;
  private final State  oldState;
  private final Action actionParam;
  private final State  newState;

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

}// end class MyApplicationReduxLog