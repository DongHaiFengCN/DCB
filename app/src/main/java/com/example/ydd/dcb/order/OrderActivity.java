package com.example.ydd.dcb.order;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ydd.dcb.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class OrderActivity extends AppCompatActivity {

    TextView dishNameTv;
    Button submit;

    StringBuilder sbl = new StringBuilder();

    ListView orderLv;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);


        dishNameTv = findViewById(R.id.dishName_tv);

        orderLv = findViewById(R.id.order_lv);

        orderLv.setAdapter(new MyAdapter());

        submit = findViewById(R.id.submit);

        String id = getIntent().getStringExtra("TableId");

        EventBus.getDefault().register(this);


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

        } else if ("delAll".equals(msg)) {

            sbl.setLength(0);
            //TODO 清空搜索栏数据


        } else {
            sbl.append(msg);
            //TODO 发起查询菜品

        }


        dishNameTv.setText(sbl.toString());

    }

    public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return getLayoutInflater().inflate(R.layout.goods_item, null);
        }
    }

}
