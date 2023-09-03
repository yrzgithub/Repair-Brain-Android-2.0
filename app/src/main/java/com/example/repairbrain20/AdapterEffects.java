package com.example.repairbrain20;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdapterEffects extends FragmentPagerAdapter {

    List<String> tab_title = new ArrayList<>();
    List<Fragment> fragments = new ArrayList<>();

    AdapterEffects(FragmentManager manager, int position) {
        super(manager, position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void add_fragment(Fragment fragment, String title) {
        fragments.add(fragment);
        tab_title.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_title.get(position);
    }
}
