package com.devian.detected.utils.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.devian.detected.main.MapFragment;
import com.devian.detected.main.MapFragment2;
import com.devian.detected.main.ProfileFragment;
import com.devian.detected.main.TaskFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }
    
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ProfileFragment();
                break;
            case 1:
                fragment = new TaskFragment();
                break;
            case 2:
                fragment = new MapFragment2();
                //fragment = new MapFragment();
                break;
        }
        return fragment;
    }
    
    @Override
    public int getCount() {
        return 3;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Профиль";
            case 1:
                return "Задачи";
            case 2:
                return "Карта";
        }
        return "";
    }
}
