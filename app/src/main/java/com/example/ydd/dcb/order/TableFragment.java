package com.example.ydd.dcb.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.dcb.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;


public class TableFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView recyclerView;
    private String id;
    private TableAdapter tableAdapter;

    private boolean timerLive;

    public TableFragment() {
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TableFragment newInstance(String id) {
        TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NUMBER, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        id = getArguments().getString(ARG_SECTION_NUMBER);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  EventBus.getDefault().register(this);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        tableAdapter = new TableAdapter();
        tableAdapter.startListener(id);
        recyclerView = rootView.findViewById(R.id.table_rcv);
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        timerLive = true;

        startTime();
        return rootView;
    }

    private void startTime() {

        Log.e("DOAING", "线程池："+MainActivity.getFixedThreadPool().toString());

        MainActivity.getFixedThreadPool().execute(new MyRunable(id));

    }

    /**
     * 处理销毁fragment时的操作
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (tableAdapter != null) {

            tableAdapter.onDestroy();

            tableAdapter = null;
        }
        recyclerView.setAdapter(null);
        recyclerView = null;
        timerLive = false;
    }


    public class MyRunable implements Runnable {

        String id;

        public MyRunable(String id) {

            this.id = id;
        }

        @Override
        public void run() {


            List<Result> resultList;

            while (timerLive) {

                resultList = tableAdapter.getTableList();

                if (resultList != null) {


                    Result result;

                    for (int i = 0; i < resultList.size(); i++) {

                        result = resultList.get(i);

                        if (result.getInt("state") == 1) {

                            long D_value = tableAdapter.timer.get(result.getString(0));

                            long new_D_value = (System.currentTimeMillis() - result.getLong("startTime")) /60000;

                            if (D_value != new_D_value) {

                                tableAdapter.timer.put(result.getString(0), new_D_value);

                                final int finalI = i;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        tableAdapter.notifyItemChanged(finalI);
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

            }

            Log.e("DOAING", "结束： " + id);
        }
    }


}