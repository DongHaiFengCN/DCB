package com.example.ydd.dcb.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.common.tools.Util;
import com.example.ydd.dcb.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.ydd.common.tools.Util.fixInputMethodManagerLeak;
import static com.example.ydd.dcb.application.MainApplication.playSound;


public class MainActivity extends AppCompatActivity {


    private PopupWindow popWindow;

    public static HashMap<String, Long> timer = new HashMap<>();

    private List<Result> resultList;

    //private SectionsPagerAdapter mSectionsPagerAdapter;


    private TabLayout tabLayout;

    LinearLayout relativeLayout;
    View productListView;
    RecyclerView recyclerView;

    RecyclerView tableRc;
    GridLayoutManager layoutManager;
    TableAdapter tableAdapter;
    private boolean timerLive;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerLive = true;

        initDate();

        findViewById(R.id.jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createPopWindow();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);


    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        Log.e("DOAING", "onStop");


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {

        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
        intent.putExtra("TableId", event);

        startActivity(intent);


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFreeEvent(final MutableDocument event) {

        playSound();

        new AlertDialog.Builder(this).setTitle(" 桌号：" + event.getInt("serialNumber")).setPositiveButton("消台", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playSound();
                event.setInt("state", 0);
                event.setInt("currentRepastTotal", 0);
                event.setLong("startTime", 0);
                CDLFactory.getInstance().saveDocument(event);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }
/*    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyDataChanged(Integer event) {

        tableAdapter.notifyDataSetChanged();


    }*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        fixInputMethodManagerLeak(this);
        timerLive = false;
        tableAdapter.removeListenerToken();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initDate() {


        tableAdapter = new TableAdapter();

        tableRc = findViewById(R.id.table_rcv);


        relativeLayout = findViewById(R.id.title_rl);

        productListView = LayoutInflater.from(MainActivity.this).inflate(R.layout.table_pop, null);

        recyclerView = productListView.findViewById(R.id.pop_rcv);

        layoutManager = new GridLayoutManager(getApplicationContext(), 5);

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


        tableRc.setLayoutManager(new GridLayoutManager(this, 3));

        tableRc.setAdapter(tableAdapter);


        tableAdapter.startListener(resultList.get(0).getString(0));

        for (int i = 0; i < resultList.size(); i++) {

            TabLayout.Tab tab = tabLayout.newTab();
            tabLayout.addTab(tab);
            TextView textView = new TextView(MainActivity.this);
            textView.setTextColor(getResources().getColorStateList( R.color.tab_text_selector) );
            textView.setGravity(Gravity.CENTER);
            textView.setText(resultList.get(i).getString("name"));
            final int finalI = i;
            tabLayout.getTabAt(i).setCustomView(textView);
            View view = (View) tabLayout.getTabAt(i).getCustomView().getParent();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tableAdapter.startListener(resultList.get(finalI).getString(0));


                }
            });

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


    private class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.table_pop_item, viewGroup, false);

            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

            viewHolder.button.setText(resultList.get(i).getString("name"));

            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tabLayout.getTabAt(i).select();
                    tableAdapter.startListener(resultList.get(i).getString(0));

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


    public class MyRunable implements Runnable {

        @Override
        public void run() {


            List<Result> resultList;
            TableAdapter tableAdapter;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



           /*  while (timerLive){


                TableFragment tableFragment = (TableFragment) getSupportFragmentManager().findFragmentById(fragments.get(mViewPager.getCurrentItem()));

                tableAdapter = tableFragment.getTableAdapter();

                if (tableAdapter != null) {

                    resultList = tableAdapter.getTableList();

                    if (resultList == null) {

                        continue;
                    }
                    Result result;

                    for (int i = 0; i < resultList.size(); i++) {

                        result = resultList.get(i);

                        if (result.getInt("state") == 1) {


                            Long D_value = timer.get(result.getString(0));
                            if (D_value == null) {

                                continue;
                            }

                            Log.e("DOAINGH", "老数据：" + D_value);

                            long new_D_value = (System.currentTimeMillis() - result.getLong("startTime")) / 60000;
                            Log.e("DOAINGH", "新数据：" + new_D_value);

                            if (D_value != new_D_value) {

                                timer.put(result.getString(0), new_D_value);

                                final int finalI = i;
                                final TableAdapter finalTableAdapter = tableAdapter;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        finalTableAdapter.notifyItemChanged(finalI);

                                    }
                                });
                            }
                        }
                    }
                }


                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }*/

        }
    }


}
