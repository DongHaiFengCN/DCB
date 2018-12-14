package com.example.ydd.dcb.order;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.dcb.R;
import com.example.ydd.dcb.application.MainApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.example.ydd.dcb.application.MainApplication.playSound;

public class OrderActivity extends AppCompatActivity {

    TextView amountTv;
    TextView dishNameTv;
    Button submit;
    StringBuilder sbl = new StringBuilder();
    ListView orderLv;
    RecyclerView recyclerView;
    List<MutableDocument> searchList = new ArrayList<>();

    List<Document> goodsList = new ArrayList<>();
    MyRecycleAdapter myRecycleAdapter;
    LinearLayout number;
    String id;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        amountTv = findViewById(R.id.editAmount_tv);

        number = findViewById(R.id.number_lv);

        dishNameTv = findViewById(R.id.dishName_tv);

        orderLv = findViewById(R.id.order_lv);

        orderLv.setAdapter(new MyListViewAdapter());

        submit = findViewById(R.id.submit);

        id = getIntent().getStringExtra("TableId");

        EventBus.getDefault().register(this);

        recyclerView = findViewById(R.id.dish_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        myRecycleAdapter = new MyRecycleAdapter();

        recyclerView.setAdapter(myRecycleAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

    }


    @TargetApi(Build.VERSION_CODES.M)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getKeyLetter(String msg) {

        if ("备".equals(msg)) {

            return;

        } else if ("✕".equals(msg)) {

            sbl.deleteCharAt(sbl.length() - 1);

            search();

        } else if ("delAll".equals(msg)) {

            sbl.setLength(0);
            searchList.clear();
            myRecycleAdapter.notifyDataSetChanged();

        } else {

            sbl.append(msg);
            search();
        }

        dishNameTv.setText(sbl.toString());
    }

    private void search() {

        recyclerView.setVisibility(View.VISIBLE);

        number.setVisibility(View.GONE);

        if (searchList.size() > 0) {

            searchList.clear();
        }


        if (sbl.length() > 0) {
            Query query = QueryBuilder.select(SelectResult.expression(Meta.id),
                    SelectResult.all()).from(DataSource.database(CDLFactory.getInstance().getDatabase()))
                    .where(Expression.property("code26").like(Expression.string(sbl.toString() + "%")));

            ResultSet results;


            try {
                results = query.execute();

                Result result;
                while ((result = results.next()) != null) {

                    //查询口味的个数

                    Array array = result.getDictionary(1).getArray("tasteIds");


                    if (array != null && array.count() > 0) {

                        for (int i = 0; i < array.count(); i++) {

                            Document document = CDLFactory.getInstance()
                                    .getDocument(array.getString(i));

                            MutableDocument mutableDocument = new MutableDocument("Goods." + UUID.randomUUID());

                            mutableDocument.setString("dishId", result.getString(0));

                            mutableDocument.setString("name", result.getDictionary(1).getString("name") + "(" + document.getString("name") + ")");

                            mutableDocument.setInt("count", 1);

                            searchList.add(mutableDocument);
                        }


                    } else {

                        MutableDocument mutableDocument = new MutableDocument("Goods." + UUID.randomUUID());

                        mutableDocument.setString("dishId", result.getString(0));

                        mutableDocument.setString("name", result.getDictionary(1).getString("name"));

                        mutableDocument.setInt("count", 1);
                        searchList.add(mutableDocument);
                    }


                }


            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }

        myRecycleAdapter.notifyDataSetChanged();
    }

    public class MyListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return goodsList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            VH vh;

            if (convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.goods_item, null);

                vh = new VH();
                vh.t1 = convertView.findViewById(R.id.state_tv);
                vh.t2 = convertView.findViewById(R.id.name_tv);
                vh.t3 = convertView.findViewById(R.id.price_tv);
                vh.t4 = convertView.findViewById(R.id.total_tv);

                convertView.setTag(vh);

            }else {


                vh = (VH) convertView.getTag();
            }


            return convertView;
        }

        public class VH {

            TextView t1;
            TextView t2;
            TextView t3;
            TextView t4;


        }
    }


    public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleAdapter.MyVH> {
        @NonNull
        @Override
        public MyRecycleAdapter.MyVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


            View view = getLayoutInflater().inflate(R.layout.dish_item, null);

            return new MyVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyVH myVH, final int i) {

            final String name = searchList.get(i).getString("name");

            myVH.name.setText(name);

            myVH.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playSound();

                    MutableDocument mutableDocument = searchList.get(i);

                    mutableDocument.setString("className", "Goods");
                    mutableDocument.setString("channelId", "f4b572c2");

                    mutableDocument.setString("tableId", id);

                    CDLFactory.getInstance().saveDocument(mutableDocument);

                    dishNameTv.setText(mutableDocument.getString("name"));
                    dishNameTv.setTag(mutableDocument.getId());

                    sbl.setLength(0);

                    recyclerView.setVisibility(View.GONE);

                    number.setVisibility(View.VISIBLE);

                }
            });

        }

        @Override
        public int getItemCount() {
            return searchList.size();
        }

        class MyVH extends RecyclerView.ViewHolder {

            TextView name;

            public MyVH(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.dish_tv);
            }
        }

    }

}
