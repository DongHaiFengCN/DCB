/*
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
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.dcb.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;


public class TableFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView recyclerView;
    private String id;
    private TableAdapter tableAdapter;

    private List<Result> resultList;
    private ListenerToken listenerToken;
    private Query query;
    private List<Result> mData = new ArrayList<>();
    public TableFragment() {
    }


    */
/**
     * Returns a new instance of this fragment for the given section
     * number.
     *//*

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


    public TableAdapter getTableAdapter() {
        return tableAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //startListener(id);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        tableAdapter = new TableAdapter(mData);

        recyclerView = rootView.findViewById(R.id.table_rcv);
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));


        return rootView;
    }


    */
/**
     * 处理销毁fragment时的操作
     *//*

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    //    query.removeChangeListener(listenerToken);

 */
/*       if (tableAdapter != null) {

            tableAdapter = null;
        }
        recyclerView.setAdapter(null);
        recyclerView = null;*//*


    }




    }


*/
