package com.example.ydd.dcb.application;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.ydd.dcb.R;

import static com.example.ydd.common.tools.Util.fixInputMethodManagerLeak;

public class BaseActivity extends AppCompatActivity {
    Typeface typeface;
    private boolean back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/字体管家润行体.ttf");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onBackPressed() {

        if (!back) {

            Toast.makeText(this, "再按一次关闭当前页面", Toast.LENGTH_SHORT).show();


            back = true;

        } else {
            finish();
        }
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        fixInputMethodManagerLeak(this);
    }

    public Typeface getTypeface() {
        return typeface;
    }
}
