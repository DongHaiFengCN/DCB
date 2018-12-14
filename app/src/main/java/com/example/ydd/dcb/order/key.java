package com.example.ydd.dcb.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ydd.common.tools.Util;
import com.example.ydd.dcb.R;

import org.greenrobot.eventbus.EventBus;

import static com.example.ydd.dcb.application.MainApplication.playSound;

public class key extends LinearLayout {


    @SuppressLint("ClickableViewAccessibility")
    public key(final Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);

        setGravity(Gravity.CENTER);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.key);

        final String name = typedArray.getString(R.styleable.key_keyName);

      //  final Integer type = typedArray.getInteger(R.styleable.key_inputType, -1);

        typedArray.recycle();

        int width = Util.getScreenWidth(context) / 10;

        //添加触摸展示框
        final TextView title = new TextView(context);

        title.setTypeface(Typeface.MONOSPACE);
        LayoutParams layoutParams0 = new LayoutParams(width, width);

        title.setLayoutParams(layoutParams0);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        title.setTextColor(context.getResources().getColor(R.color.white));

        title.setText(name);
        title.setGravity(Gravity.CENTER);
        title.setVisibility(INVISIBLE);

        title.setBackgroundResource(R.drawable.circle_border_and_fill);

        addView(title);

        LinearLayout op = new LinearLayout(context);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (width * 1.2));

        op.setLayoutParams(layoutParams);

        op.setClickable(true);

        final CardView cardView = new CardView(context);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        TextView button = new TextView(context);

        button.setGravity(Gravity.CENTER);

        button.setTypeface(Typeface.MONOSPACE);

        button.setText(name);
        CardView.LayoutParams params1 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        button.setLayoutParams(params1);

        cardView.addView(button);

        if (name.equals("备")) {

            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.md_blue_grey_500));
            button.setTextColor(context.getResources().getColor(R.color.white));
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            params.setMargins(12, 2, 10, 2);
            cardView.setLayoutParams(params);

        } else if (name.equals("✕")) {

            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.md_red_500));
            button.setTextColor(context.getResources().getColor(R.color.white));
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            params.setMargins(12, 2, 10, 2);
            cardView.setLayoutParams(params);

        } else {

            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            button.setTextColor(context.getResources().getColor(R.color.black90));
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            params.setMargins(3, 2, 3, 2);
            cardView.setLayoutParams(params);
        }


        op.addView(cardView);

        addView(op);

        if("✕".equals(name)){

            op.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    EventBus.getDefault().post("delAll");

                    return false;
                }
            });
        }

        op.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    playSound();

                    title.setVisibility(VISIBLE);

                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.divider));

                    EventBus.getDefault().post(name);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {


                    if (name.equals("备")) {

                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.md_blue_grey_500));

                    } else if (name.equals("✕")) {

                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.md_red_600));

                    } else {
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));

                    }

                    title.setVisibility(INVISIBLE);

                }

                return false;
            }
        });


    }


}
