package com.r3bl.todo_app.ui.reduxdebug;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.r3bl.todo_app.todoapp.R;
import me.relex.circleindicator.CircleIndicator;

/**
 * more info - https://guides.codepath.com/android/Using-DialogFragment#full-screen-dialog
 * Created by nazmul on 11/27/16.
 */

public class DebugDetailsDialogFragment extends BottomSheetDialogFragment {

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
 * more info - https://stackoverflow.com/questions/19544829/viewpager-with-fragments-inside-popupwindow-or-dialogfragment-error-no-view/19552298#19552298
 * You have to use the getChildFragmentManager() -> otherwise this doesn't work!!!
 */
@Nullable
@Override
public View onCreateView(LayoutInflater inflater,
                         @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
  View view = inflater.inflate(R.layout.debug_details_viewpager, container, false);

  // get the position int from the bundle
  int position = getArguments().getInt("position", -1);

  // view pager setup
  ViewPager viewPager = (ViewPager) view.findViewById(R.id.debug_view_pager);
  //
  // Note the use of getChildFragmentManager() since this is a dialogfragment!!!
  // and not getFragmentManager()
  //
  ViewPagerAdapter myPagerAdapter =
    new ViewPagerAdapter(getChildFragmentManager(), position);
  viewPager.setAdapter(myPagerAdapter);
  CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.debug_indicator);
  indicator.setViewPager(viewPager);
  return view;
}

private class ViewPagerAdapter extends FragmentPagerAdapter {

  private final int position;

  public ViewPagerAdapter(FragmentManager fm, int position) {
    super(fm);
    this.position = position;
  }

  @Override
  public Fragment getItem(int tabPosition) {
    switch (tabPosition) {
      case 0:
        return StateFragment.newInstance(StateFragment.Type.Action, position);
      case 1:
        return StateFragment.newInstance(StateFragment.Type.Diff, position);
      case 2:
        return StateFragment.newInstance(StateFragment.Type.OldState, position);
      case 3:
        return StateFragment.newInstance(StateFragment.Type.NewState, position);
    }
    return null;
  }

  @Override
  public int getCount() {
    return 4;
  }

  @Override
  public CharSequence getPageTitle(int tabPosition) {
    switch (tabPosition) {
      case 0:
        return "Action";
      case 1:
        return "State diff";
      case 2:
        return "Old State";
      case 3:
        return "New State";
    }
    return super.getPageTitle(tabPosition);
  }

}// end class ViewPagerAdapter

}// end class DebugDetailsDialogFragment