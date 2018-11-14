package com.example.ydd.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.ydd.common.R;

import java.util.List;

public class Indicator extends LinearLayout {

    private static final int TEXT_DEFAULT_COLOR = 0x77ffffff;

    private ViewPager mViewPager;

    /**
     * 默认设置三角形的底边长度是导航的1/6高
     */
    private static final float RADIO_TRIANGLE_WIDTH = 1 / 5f;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 用来构成三角形
     */
    private Path mPath;

    /**
     * 三角形的宽
     */
    private int mTriangleWidth;

    /**
     * 三角形的长
     */
    private int mTriangleHeight;


    /**
     * 初始化的时候三角形的位置
     */
    private int mInitTranslationX;

    /**
     * 移动的时候三角形的位置
     */
    private int mTranslationX;


    private int visibleTableCount = 4;

    private int tableWidth;

    public Indicator(Context context) {
        this(context, null);

    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Indicator);


        int max_visibleTableCount = typedArray.getInt(R.styleable.Indicator_visible_table_count, 0);


        if (max_visibleTableCount > visibleTableCount) {

            visibleTableCount = max_visibleTableCount;
        }

        typedArray.recycle();


        mPaint = new Paint();

        mPaint.setAntiAlias(true);

        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(Color.parseColor("#ffffff"));

        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    /**
     * 测量控件的高度设置三角形的属性
     */

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        tableWidth = w / visibleTableCount;

        mTriangleWidth = (int) ((w / visibleTableCount) * RADIO_TRIANGLE_WIDTH);

        //三角形的左起点在每个table中间位置左偏移底边长度一半的位置
        //mInitTranslationX = w / visibleTableCount / 2 - mTriangleWidth / 2;

        //initTriangle();
        initRec();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int cCount = getChildCount();

        if (cCount == 0) {
            return;
        }

        View view;

        for (int i = 0; i < cCount; i++) {

            view = getChildAt(i);

            LayoutParams params = (LayoutParams) view.getLayoutParams();

            params.weight = 0;

            params.width = getScreenWidth() / visibleTableCount;

            view.setLayoutParams(params);

        }

    }

    private int getScreenWidth() {

        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();


        //mInitTranslationX 初始化的位置，mTranslationX 默认为0，在Y轴getHeight方向绘制
        canvas.translate(mInitTranslationX + mTranslationX, getHeight());

        //平移到指定位置就可以绘制了
        canvas.drawPath(mPath, mPaint);

        canvas.restore();

        super.dispatchDraw(canvas);

    }

    /**
     * 绘制三角形的形状
     */

    private void initTriangle() {

        //先设置角度为45度
        mTriangleHeight = mTriangleWidth / 2;

        mPath = new Path();

        mPath.moveTo(0, 0);

        mPath.lineTo(mTriangleWidth, 0);

        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);

        mPath.close();
    }

    /**
     * 绘制矩形
     */

    private void initRec() {

        mTriangleHeight = 4;

        mPath = new Path();

        mPath.moveTo(0, 0);

        mPath.lineTo(tableWidth, 0);

        mPath.lineTo(tableWidth , -mTriangleHeight);

        mPath.lineTo(0 , -mTriangleHeight);

        mPath.close();
    }
    /*
     * 第几个table 然后偏移了多少
     * @param position 第几个table下
     * @param offset 偏移比例（0 - 1）
     */
    public void scrolled(int position, float offset) {

        int width = getWidth() / visibleTableCount;

        mTranslationX = (int) (tableWidth * (position + offset));

        if (position >= visibleTableCount - 2 &&
                offset > 0 &&
                getChildCount() > visibleTableCount && getChildCount() != position + 2) {

            if (visibleTableCount != 1) {

                int sum = (position - visibleTableCount + 2) * width;

                this.scrollTo((int) (sum + width * offset), 0);

            } else {

                this.scrollTo((int) ((position + offset) * width), 0);
            }

        }

        invalidate();

    }

    public void setTableItemTitle(List<String> tableItemTitle) {

        if (tableItemTitle != null && tableItemTitle.size() > 0) {

            this.removeAllViews();

            for (int i = 0; i < tableItemTitle.size(); i++) {

                addView(generateTextView(tableItemTitle.get(i), i));

            }

        }
    }

    private View generateTextView(String name, final int position) {

        TextView view = new TextView(getContext());

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        layoutParams.weight = 0;

        layoutParams.width = getScreenWidth() / visibleTableCount;

        view.setLayoutParams(layoutParams);

        view.setText(name);

        view.setGravity(Gravity.CENTER);

        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        view.setTextColor(TEXT_DEFAULT_COLOR);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mViewPager.setCurrentItem(position);
            }
        });

        return view;
    }

    public void setViewPageAdapter(ViewPager mViewPager) {

        this.mViewPager = mViewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

                scrolled(i, v);

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


}
