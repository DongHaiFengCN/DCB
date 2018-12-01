package com.example.ydd.dcb.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.common.tools.Util;
import com.example.ydd.dcb.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.example.ydd.dcb.application.MainApplication.playSound;


public class MainActivity extends AppCompatActivity {
    private PopupWindow popWindow;


    private List<Result> resultList;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    LinearLayout relativeLayout;
    View productListView;
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initDate();

        findViewById(R.id.jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createPopWindow();
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {

        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
        intent.putExtra("TableId",event);

        startActivity(intent);


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFreeEvent(final MutableDocument event) {

        playSound();

        new AlertDialog.Builder(this).setTitle(" 桌号："+event.getInt("serialNumber")).setPositiveButton("消台", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playSound();
                event.setInt("state", 0);
                event.setInt("currentRepastTotal",0);
                event.setLong("startTime",0);
                CDLFactory.getInstance().saveDocument(event);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playSound();
            }
        }).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initDate() {

        relativeLayout = findViewById(R.id.title_rl);

        productListView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popwindow, null);

        recyclerView = productListView.findViewById(R.id.pop_rcv);

        layoutManager = new GridLayoutManager(getApplicationContext(), 5);

        mViewPager = findViewById(R.id.container);

        tabLayout = findViewById(R.id.tablayout);

        Query query = QueryBuilder.select(SelectResult.expression(Meta.id),
                SelectResult.property("name")).from(DataSource.database(CDLFactory.getInstance().getDatabase()))
                .where(Expression.property("className").equalTo(Expression.string("Area")));

        ResultSet results = null;
        try {
            results = query.execute();

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        resultList = results.allResults();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < resultList.size(); i++) {

            tabLayout.getTabAt(i).setText(resultList.get(i).getString("name"));

        }

    }

    private void createPopWindow() {


        if (popWindow == null) {

            popWindow = new PopupWindow(productListView, Util.getScreenWidth(getApplicationContext()), Util.getScreenWidth(getApplicationContext()) / 2, true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new MoreAdapter());
            popWindow.setBackgroundDrawable(new BitmapDrawable());

        }

        popWindow.showAsDropDown(relativeLayout);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return TableFragment
                    .newInstance(resultList.get(position).getString("id"));
        }

        @Override
        public int getCount() {

            return resultList.size();
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

            viewHolder.button.setText(resultList.get(i).getString("name"));

            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mViewPager.setCurrentItem(i);
                    tabLayout.getTabAt(i).select();
                    popWindow.dismiss();
                    popWindow = null;

                }
            });

        }

        @Override
        public int getItemCount() {
            return resultList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            Button button;

            ViewHolder(View itemView) {
                super(itemView);

                button = itemView.findViewById(R.id.area_bt);

                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();

                layoutParams.width = Util.getScreenWidth(getApplicationContext()) / 6;

                layoutParams.height = layoutParams.width;

                button.setLayoutParams(layoutParams);
            }
        }

    }


}
