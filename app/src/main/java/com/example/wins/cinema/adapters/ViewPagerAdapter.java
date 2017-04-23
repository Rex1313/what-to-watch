package com.example.wins.cinema.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.wins.cinema.InfoFragment;
import com.example.wins.cinema.MediaFragment;
import com.example.wins.cinema.StoryFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wins on 10/16/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] tabs = {"Story", "Info", "Media"};
    private List<Fragment> fragments;

    public List<Fragment> getFragments() {
        return fragments;
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        setUpFragments();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return getFragments().get(0);
            case 1:
                return getFragments().get(1);
            case 2:
                return getFragments().get(2);
        }

        return new InfoFragment();
    }


    private void setUpFragments(){
        fragments = new ArrayList<>();
        StoryFragment storyFragment = new StoryFragment();
        fragments.add(0, storyFragment);
        InfoFragment infoFragment = new InfoFragment();
        fragments.add(1, infoFragment);
        MediaFragment mediaFragment = new MediaFragment();
        fragments.add(2,mediaFragment);

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
