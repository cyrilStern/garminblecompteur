package com.example.root.garminblecompteur.scrollviewinformation;

/**
 * Created by cyrilstern1 on 07/09/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
    private final List fragments;
    private Fragment fragment;

    //On fournit à l'adapter la liste des fragments à afficher
    public ScreenSlidePagerAdapter(FragmentManager fm, List fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        fragment = (Fragment) fragments.get(position);
        return (Fragment) this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    public Fragment getVisibleFragment(){
        if(fragments != null){
        }
            return fragments != null ? this.fragment : null;
    }
}
