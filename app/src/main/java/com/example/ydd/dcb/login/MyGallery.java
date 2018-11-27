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

    private int mCurrentItemOffset = 2;

    private int currentPosition = -1;

    private String name;

    private MyAdapter myAdapter;

    public MyGallery(@NonNull final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

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

                Log.e("DOAING", "偏移量：" + mCurrentItemOffset + " galleryWidth：" + galleryWidth);

                if (currentPosition != mCurrentItemOffset / galleryWidth) {
                    currentPosition = mCurrentItemOffset / galleryWidth;
                    myAdapter.setP(currentPosition + 1);
                    Log.e("DOAING", "选择的位置：" + (currentPosition + 1));
                }
            }
        });
    }

    public void setData(List<com.couchbase.lite.Dictionary> users) {

        myAdapter = new MyAdapter(users);
        setAdapter(myAdapter);

    }


    public String getName() {

        return name;
    }

    private class MyAdapter extends Adapter<MyAdapter.ViewHolder> {

        int p = 1;

        private List<String> users = new ArrayList<>(12);


        public MyAdapter(List<com.couchbase.lite.Dictionary> title) {


            users.add("");

            for (int j = 0; j < title.size(); j++) {


                users.add(title.get(j).getString("username"));

            }

            users.add("");
        }


        public void setP(int p) {
            this.p = p;
            notifyDataSetChanged();
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

                viewHolder.textView.setText(users.get(i));


              /*  viewHolder.linearLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        name = users.get(i);
                        scrollToPosition(i);

                    }
                });*/

            } else {

                viewHolder.linearLayout.setVisibility(INVISIBLE);

            }

            if (p == i) {

                viewHolder.imageView.setImageResource(R.mipmap.h1);

                name = users.get(i);

            } else {
                viewHolder.imageView.setImageResource(R.mipmap.h0);

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
