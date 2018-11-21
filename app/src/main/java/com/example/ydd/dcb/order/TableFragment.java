package com.example.ydd.dcb.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ydd.dcb.R;


public class TableFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView recyclerView;
    private String id;
    private TableAdapter tableAdapter;


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

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = rootView.findViewById(R.id.table_rcv);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

       // Log.e("DOAING","onStart "+id);

        tableAdapter = new TableAdapter();

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);

        recyclerView.setLayoutManager(layoutManager);

        tableAdapter.startListener(id);

        recyclerView.setAdapter(tableAdapter);


    }

    /**
     * 处理不可见的时候的数据（home键）
     */
    @Override
    public void onStop() {
        super.onStop();
        //Log.e("DOAING","onStop "+id);
        if (tableAdapter != null) {

            tableAdapter.onDestroy();

            tableAdapter = null;
        }

    }

    /**
     * 处理销毁fragment时的操作
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // Log.e("DOAING","onDestroyView "+id);
        recyclerView = null;
    }
}