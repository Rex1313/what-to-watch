package uk.co.sszymanski.cinema.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import uk.co.sszymanski.cinema.fragments.InfoFragment;
import uk.co.sszymanski.cinema.fragments.MediaFragment;
import uk.co.sszymanski.cinema.R;
import uk.co.sszymanski.cinema.fragments.StoryFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rex on 10/16/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] tabs;
    private List<Fragment> fragments;

    public List<Fragment> getFragments() {
        return fragments;
    }

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.tabs = context.getResources().getStringArray(R.array.fragment_titles);
        setUpFragments();
    }

    @Override
    public Fragment getItem(int position) {
        return position < 3 ? getFragments().get(position) : new InfoFragment();
    }


    private void setUpFragments() {
        fragments = new ArrayList<>();
        fragments.add(0, StoryFragment.newInstance());
        fragments.add(1, InfoFragment.newInstance());
        fragments.add(2, MediaFragment.newInstance());
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }
}
