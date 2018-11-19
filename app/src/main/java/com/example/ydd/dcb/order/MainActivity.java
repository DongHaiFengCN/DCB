package com.example.ydd.dcb.order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.couchbase.lite.Dictionary;
import com.example.ydd.common.lite.query.QueryWithMultipleConditional;
import com.example.ydd.common.view.Indicator;
import com.example.ydd.dcb.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private List<Dictionary> dictionaries;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;


    private Indicator mIndicator;
    private  List<String> title = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        mIndicator = findViewById(R.id.indicator);

        mViewPager = findViewById(R.id.container);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mIndicator.setViewPageAdapter(mViewPager);

        mIndicator.setTableItemTitle(title);


    }


    private void initData() {

     dictionaries = QueryWithMultipleConditional.getInstance()
                .addConditional("className", "Area").generate();

        for (Dictionary dictionary : dictionaries) {

            title.add(dictionary.getString("name"));
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return TableFragment
                    .newInstance(dictionaries.get(position).getString("id"));
        }

        @Override
        public int getCount() {

            return dictionaries.size();
        }

    }

}
