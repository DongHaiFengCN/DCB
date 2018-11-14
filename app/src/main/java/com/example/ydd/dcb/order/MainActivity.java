package com.example.ydd.dcb.order;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ydd.common.view.Indicator;
import com.example.ydd.dcb.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TableAdapter tableAdapter;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;


    private Indicator mIndicator;
    private static List<String> title = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 得到当前activity的信息
         */
        Log.e("DOAING", "Activity.toString:" + this.toString());


        initData();

        mIndicator = findViewById(R.id.indicator);

        mViewPager = findViewById(R.id.container);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);


        mIndicator.setViewPageAdapter(mViewPager);

        mIndicator.setTableItemTitle(title);

    }

    private void initData() {

        title.add("短信1");
        title.add("收藏2");
        title.add("推荐3");
        title.add("短信4");
        title.add("短信5");
        title.add("短信6");
      /*  title.add("短信7");
        title.add("短信8");
        title.add("短信9");
        title.add("短信10");
        title.add("短信11");
        title.add("短信12");
        title.add("短信13");
        title.add("短信14");
        title.add("短信15");*/
    }


    private void initView() {

      /*  recyclerView = findViewById(R.id.table_rcv);

        tableAdapter = new TableAdapter();

        //设置布局管理器
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        //设置Adapter
        recyclerView.setAdapter(tableAdapter);

        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        tableAdapter.startListener("Area.58ae4286-642a-4dcc-9516-475479bec1d0");
*/

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        tableAdapter.onDestroy();

        Log.e("DOAING", "onDestroy");

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {

            return title.size();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";


        public PlaceholderFragment() {
        }


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, title.get(sectionNumber));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);
            textView.setText(getArguments().getString(ARG_SECTION_NUMBER));

            //TODO 开启监听

            return rootView;
        }

        @Override
        public void onStop() {
            super.onStop();

            //TODO 释放监听

        }
    }
}
