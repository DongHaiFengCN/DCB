package com.example.ydd.dcb.order;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.couchbase.lite.Dictionary;
import com.example.ydd.common.lite.query.QueryWithMultipleConditional;
import com.example.ydd.common.tools.Util;
import com.example.ydd.dcb.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PopupWindow popWindow;

    private List<Dictionary> dictionaries;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private List<String> title = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.tablayout);

        dictionaries = QueryWithMultipleConditional.getInstance()
                .addConditional("className", "Area").generate();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < dictionaries.size(); i++) {
            title.add(dictionaries.get(i).getString("id"));
            tabLayout.getTabAt(i).setText(title.get(i));
        }

        findViewById(R.id.jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createPopWindow();
            }
        });

    }

    private void createPopWindow() {

        LinearLayout relativeLayout = findViewById(R.id.title_rl);

        View productListView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popwindow, null);
        popWindow = new PopupWindow(productListView, Util.getScreenWidth(getApplicationContext()), 300, true);

        RecyclerView recyclerView = productListView.findViewById(R.id.pop_rcv);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new MoreAdapter());

        popWindow.showAsDropDown(relativeLayout);
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


    private class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.popwindow_item, viewGroup, false);

            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

            viewHolder.button.setText(title.get(i));

            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mViewPager.setCurrentItem(i);
                    tabLayout.getTabAt(i).select();
                    popWindow.dismiss();

                }
            });

        }

        @Override
        public int getItemCount() {
            return dictionaries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            Button button;

            ViewHolder(View itemView) {
                super(itemView);
                button = itemView.findViewById(R.id.area_bt);
            }
        }

    }
}
