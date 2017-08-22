package com.extralarge.fujitsu.xl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.extralarge.fujitsu.xl.NewsSection.BollywoodNews;
import com.extralarge.fujitsu.xl.NewsSection.BusinessNews;
import com.extralarge.fujitsu.xl.NewsSection.CitiesNews;
import com.extralarge.fujitsu.xl.NewsSection.EntertainmentNews;
import com.extralarge.fujitsu.xl.NewsSection.International;
import com.extralarge.fujitsu.xl.NewsSection.MainNews;
import com.extralarge.fujitsu.xl.NewsSection.National;
import com.extralarge.fujitsu.xl.NewsSection.SportsNews;
import com.extralarge.fujitsu.xl.NewsSection.State;

/**
 * Created by Fujitsu on 28/04/2017.
 */

public class TabFragment  extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 9 ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflater.inflate(R.layout.tab_layout,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return x;

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new MainNews();
                case 1 : return new National();
                case 2 : return new International();
                case 3 : return new State();
                case 4 : return new BusinessNews();
                case 5 : return new CitiesNews();
                case 6 : return new SportsNews();
                case 7 : return new EntertainmentNews();
                case 8 : return new BollywoodNews();

            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){

                case 0 :
                    return "मुख्य समाचार";
                case 1 :
                    return "राष्ट्रीय";
                case 2 :
                    return "अंतरराष्ट्रीय";
                case 3 :
                    return "राज्य";
                case 4 :
                    return "व्यापार";
                case 5 :
                    return "शहरों";
                case 6 :
                    return "खेल";
                case 7 :
                    return "मनोरंजन";
                case 8 :
                    return "बॉलीवुड";
            }
            return null;
        }
    }

}


