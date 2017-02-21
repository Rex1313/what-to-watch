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
    private List<Fragment> fragments = new ArrayList<>();

    public List<Fragment> getFragments() {
        return fragments;
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                StoryFragment storyFragment = new StoryFragment();


                return storyFragment;

            case 1:
                InfoFragment infoFragment = new InfoFragment();


                return infoFragment;
            case 2:
                MediaFragment mediaFragment = new MediaFragment();


                return mediaFragment;

        }

        return new InfoFragment();
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
