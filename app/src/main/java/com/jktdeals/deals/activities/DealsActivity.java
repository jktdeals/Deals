package com.jktdeals.deals.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.jktdeals.deals.R;
import com.jktdeals.deals.adapters.DealsFragmentPagerAdapter;

public class DealsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

        // get rid of actionbar drop shadow
        getSupportActionBar().setElevation(0);

        // Get the ViewPager and set its PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.vpPager);
        viewPager.setAdapter(new DealsFragmentPagerAdapter(getSupportFragmentManager(),
                DealsActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

}
