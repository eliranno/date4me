package com.example.elirannoach.date4me.ui;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    List<Fragment> fragmentList;
    FragmentManager fm;

    public PagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.mNumOfTabs = fragmentList.size();
        this.fragmentList = fragmentList;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public Fragment instantiateItem(ViewGroup container, int position){
        Fragment fragment = getItem(position);
        FragmentTransaction trans = fm.beginTransaction();
        trans.add(container.getId(),fragment,"fragment:"+position);
        trans.commit();
        return fragment;
    }


}
