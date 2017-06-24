package com.example.theodosis.calendar365;

/**
 * Created by Theodosis on 11/19/2015.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case(0): // if the position is 0 we are returning the First tab
                CalendarView calendarView = new CalendarView();
                return calendarView;

            case(1): // if the position is 2 we are returning the Second tab
                UpcomingEvnt upcomingEvnt = new UpcomingEvnt();
                return upcomingEvnt;

            case(2): // if the position is 3 we are returning the Third tab
                SyncFromGoogle syncFromGoogle = new SyncFromGoogle();
                return syncFromGoogle;

            case(3): // if the position is 4 we are returning the Fourth tab
                SyncToGoogle syncToGoogle = new SyncToGoogle();
                return syncToGoogle;
        }
        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

}
