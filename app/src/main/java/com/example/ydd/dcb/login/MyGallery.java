package com.example.ydd.dcb.login;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ydd.common.tools.Util;
import com.example.ydd.dcb.R;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

public class MyGallery extends RecyclerView {

    private int galleryWidth;
    private Context context;

    private int mCurrentItemOffset = 1;

    private int currentPosition = -1;

    private float max = 1.2f;


    private String name;

    public boolean isFirst() {
        return first;
    }

    private boolean first = true;

    private MyAdapter myAdapter;
    LinearLayoutManager layoutManager;


    private List<String> users = new ArrayList<>();

    public MyGallery(@NonNull final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        layoutManager = new LinearLayoutManager(context);

        // galleryWidth = (Util.getScreenWidth(context) / 3);

        this.setLayoutManager(layoutManager);

        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);

        this.context = context;
        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();

        linearSnapHelper.attachToRecyclerView(this);


        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mCurrentItemOffset += dx;

                //计算实时的位置 总长度/单位长度
                currentPosition = mCurrentItemOffset / galleryWidth;

                //得到一个单位长度中的变化数值
                float value = mCurrentItemOffset - (currentPosition * galleryWidth);

                //得到变化率的百分比，这里取0.01到0.2之间的变化率
                float scale = value / galleryWidth / 5;


                if (dx > 0) {

                    //左移动

                    View view1;
                    View view2;

                    //要选中的
                    view1 = getLayoutManager().findViewByPosition(currentPosition + 2);

                    view2 = getLayoutManager().findViewByPosition(currentPosition + 1);


                    //name = users.get(currentPosition + 2);

                    // Log.e("DOAING", "左 " + name);

                    if (view1 != null) {


                        //v1变大
                        view1.setScaleX(scale + 1);
                        view1.setScaleY(scale + 1);

                        //v2变小

                        view2.setScaleY(max - scale);
                        view2.setScaleX(max - scale);

                    }

                    //最左侧的要

                } else if (dx < 0) {

                    //右移动移动
                    //Log.e("DOAING", "变大的位置：" + (p + 1));
                    // Log.e("DOAING", "缩小的位置：" + (p + 2));

                    //当前位置变大
                    View view1 = getLayoutManager().findViewByPosition(currentPosition + 1);
                    View view2 = getLayoutManager().findViewByPosition(currentPosition + 2);


                    if (view1 != null) {
                        //  name = users.get(currentPosition + 1);

                        //Log.e("DOAING", "右  " + name);
                        view1.setScaleX(max - scale);
                        view1.setScaleY(max - scale);

                        view2.setScaleX(scale + 1);
                        view2.setScaleY(scale + 1);

                    }


                } else {

                    View view = getLayoutManager().findViewByPosition(1);


                    if (view != null) {

                        view.setScaleX(max);
                        view.setScaleY(max);

                        name = users.get(1);
                    }

                }
            }
        });
    }

    public void setData(List<com.couchbase.lite.Dictionary> users) {

        first = false;
        myAdapter = new MyAdapter(users);
        setAdapter(myAdapter);


    }


    public String getName() {

        return users.get(layoutManager.findFirstCompletelyVisibleItemPosition()  + 1);
    }

    private class MyAdapter extends Adapter<MyAdapter.ViewHolder> {


        public MyAdapter(List<com.couchbase.lite.Dictionary> title) {

            if (users.size() > 0) {

                users.clear();
            }


             users.add("");

            for (int j = 0; j < title.size(); j++) {


                users.add(title.get(j).getString("username"));

            }

             users.add("");
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(context).inflate(R.layout.card, null);

            return new ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

            if (!"".equals(users.get(i))) {
                viewHolder.imageView.setVisibility(VISIBLE);
                viewHolder.textView.setText(users.get(i));

            }else {

                viewHolder.textView.setText("");
                viewHolder.imageView.setVisibility(INVISIBLE);
            }



        }

        @Override
        public int getItemCount() {


            return users.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            TextView textView;
            LinearLayout linearLayout;

            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);


                imageView = itemView.findViewById(R.id.head_im);
                linearLayout = itemView.findViewById(R.id.lin);
                textView = itemView.findViewById(R.id.text2);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();

                layoutParams.width = getMeasuredWidth() / 3;
                layoutParams.height = getMeasuredWidth() / 3;

                Log.e("DOAING", getMeasuredWidth() + "..........");

                linearLayout.setLayoutParams(layoutParams);

                galleryWidth = layoutParams.width;

            }
        }

    }


}
