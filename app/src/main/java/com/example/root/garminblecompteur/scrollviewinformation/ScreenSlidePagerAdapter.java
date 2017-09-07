package com.example.root.garminblecompteur.scrollviewinformation;

/**
 * Created by cyrilstern1 on 07/09/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private final List fragments;
    private Fragment fragment;

    //On fournit à l'adapter la liste des fragments à afficher
    public ScreenSlidePagerAdapter(FragmentManager fm, List fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("instantiateItem", "instantiateItem: " + position);

        return (Fragment) this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    private Fragment mCurrentFragment;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        Log.i("instantiateItem", "instantiateItem: " + position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        Log.i("instantiateItem", "instantiateItem: " + position);

        return registeredFragments.get(position);
    }

}
