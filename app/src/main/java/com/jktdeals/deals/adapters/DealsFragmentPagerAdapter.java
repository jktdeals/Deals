package com.jktdeals.deals.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.jktdeals.deals.fragments.AlertsFragment;
import com.jktdeals.deals.fragments.FavoritesFragment;
import com.jktdeals.deals.fragments.MyDealsFragment;
import com.jktdeals.deals.fragments.NearMeFragment;

// return the order of the fragments in the ViewPager
public class DealsFragmentPagerAdapter extends FragmentPagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private String tabTitles[] = new String[] { "Near me", "My deals", "Favorites", "Alerts" };
    private Context context;

    // adapter gets the manager - insert or remove fragments from activity
    public DealsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    // order and creation of fragments within the pager
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new NearMeFragment();
        } else if (position == 1) {
            return new MyDealsFragment();
        } else if (position == 2) {
            return new FavoritesFragment();
        } else if (position == 3) {
            return new AlertsFragment();
        } else {
            return null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    // returns the tab title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    // how many fragments there are to swipe between
    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
