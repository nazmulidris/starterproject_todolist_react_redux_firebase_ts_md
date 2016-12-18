package com.r3bl.todo_app.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.r3bl.todo_app.container.App;
import com.r3bl.todo_app.container.redux.Actions;
import com.r3bl.todo_app.todoapp.R;
import com.r3bl.todo_app.ui.groupchat.GroupChatFragment;
import com.r3bl.todo_app.ui.reduxdebug.ReduxDebugFragment;
import com.r3bl.todo_app.ui.todo.TodoFragment;
import me.relex.circleindicator.CircleIndicator;


public class MainActivity extends AppCompatActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.main_activity);

  // toolbar setup
  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
  setSupportActionBar(toolbar);

  // fab setup
  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
  fab.setOnClickListener(
    view ->
      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              .setAction("Action",
                         v -> {
                           App ctx = (App) getApplicationContext();
                           String msg = ctx.getReduxState().toString();
                           Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT)
                                .show();
                         })
              .show());

  // view pager setup
  ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewPager);
  ViewPagerAdapter myPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
  viewPager.setAdapter(myPagerAdapter);
  CircleIndicator indicator = (CircleIndicator) findViewById(R.id.main_indicator);
  indicator.setViewPager(viewPager);

}

@Override
protected void onDestroy() {
  super.onDestroy();
  App.log("MainActivity", "onDestroy: ran");
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
  // Inflate the menu; this adds items to the action bar if it is present.
  getMenuInflater().inflate(R.menu.menu_main, menu);
  return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
  // Handle action bar item clicks here. The action bar will
  // automatically handle clicks on the Home/Up button, so long
  // as you specify a parent activity in AndroidManifest.xml.
  int id = item.getItemId();

  //noinspection SimplifiableIfStatement
  if (id == R.id.action_settings) {
    return true;
  } else if (id == R.id.action_account) {
    App ctx = (App) getApplicationContext();
    ctx.getReduxStore().dispatch(new Actions.AddTodoItem(ctx.getTime(), false));
    Toast.makeText(MainActivity.this, "todo login action", Toast.LENGTH_SHORT)
         .show();
    return true;
  }

  return super.onOptionsItemSelected(item);
}

//
// View pager adapter support
//
class ViewPagerAdapter extends FragmentPagerAdapter {

  public ViewPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int tabPosition) {
    switch (tabPosition) {
      case 0:
        return new TodoFragment();
      case 1:
        return new GroupChatFragment();
      case 2:
        return new ReduxDebugFragment();
    }
    return null;
  }

  @Override
  public int getCount() {
    return 3;
  }

  @Override
  public CharSequence getPageTitle(int tabPosition) {
    switch (tabPosition) {
      case 0:
        return "Todo List";
      case 1:
        return "Group Chat";
      case 2:
        return "Redux Debug";
    }
    return super.getPageTitle(tabPosition);
  }

}

}// end MainActivity class