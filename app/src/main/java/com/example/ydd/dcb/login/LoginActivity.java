package com.example.ydd.dcb.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Dictionary;
import com.example.ydd.common.lite.query.QueryWithMultipleConditional;
import com.example.ydd.common.tools.Util;
import com.example.ydd.dcb.R;
import com.example.ydd.dcb.application.BaseActivity;
import com.example.ydd.dcb.google.zxing.activity.CaptureActivity;
import com.example.ydd.dcb.order.MainActivity;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends BaseActivity {


    private static final int PERMS_REQUEST_CODE = 2;
    private TextView titleTv;
    private TextView welcomeTv;

    private AutoCompleteTextView autoCompleteTextView;
    private EditText psw;
    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;
    private Button submitBt;

    Intent regIntent;
    private ProgressDialog dialog;

    private List<String> users = new ArrayList<>();
    private List<String> psws = new ArrayList<>();

    List<Dictionary> dictionaries;

    private boolean isRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        regIntent = new Intent(getApplicationContext(), CDServer.class);
        startProgress(Util.getVerifyConfiguration(getApplicationContext()));

        EventBus.getDefault().register(this);

        String[] permissions = new String[]{Manifest.permission.
                WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            requestPermissions(permissions, PERMS_REQUEST_CODE);
        }

        autoCompleteTextView = findViewById(R.id.name_et);
        psw = findViewById(R.id.password_et);

        titleTv = findViewById(R.id.head_tv);
        titleTv.setTypeface(getTypeface());
        titleTv.setText("首页");

        welcomeTv = findViewById(R.id.welcome_tv);
        welcomeTv.setTypeface(getTypeface());
        welcomeTv.setText("欢迎使用肴点点");


        psw = findViewById(R.id.password_et);

        submitBt = findViewById(R.id.submit_bt);
        submitBt.setTypeface(getTypeface());
        submitBt.setText("登录");
        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = autoCompleteTextView.getText().toString();
                if (name.length() == 0) {

                    Toast.makeText(LoginActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();

                    return;
                }

                String p = psw.getText().toString();

                if (p.length() == 0) {
                    Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();

                    return;
                }

                boolean find = false;

                for (int i = 0; i < users.size(); i++) {

                    if (name.equals(users.get(i))&&p.equals(psws.get(i))) {

                        find = true;

                        break;
                    }
                }

                if (find) {

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {

                    Toast.makeText(LoginActivity.this, "当前账号不存在", Toast.LENGTH_SHORT).show();

                }


            }
        });

        findViewById(R.id.sm_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isRun){

                    Intent intent = new Intent(LoginActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }else {

                    Toast.makeText(LoginActivity.this,"正在同步不可中断～",Toast.LENGTH_SHORT).show();
                }


            }
        });



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);


        stopProgress();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void progress(Double myProgress) {

        if (myProgress == 100) {

            dialog.setTitle("同步完成");
            dialog.setProgress(myProgress.intValue());

            initUser();
            isRun = false;
        } else if (myProgress == 0) {

            dialog.setTitle("同步完成");
            dialog.setProgress(100);
            initUser();
            isRun = false;

        } else {

            isRun = true;
            dialog.setTitle("正在同步...");
            dialog.setProgress(myProgress.intValue());
        }


    }

    private void initUser() {

        if (users.size() > 0) {

            users.clear();
        }

        dictionaries = QueryWithMultipleConditional
                .getInstance()
                .addConditional("className", "Employee")
                .generate();

        for (Dictionary dictionary : dictionaries) {

            users.add(dictionary.getString("userName"));
            psws.add(dictionary.getString("pwd"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);

        autoCompleteTextView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //stopProgress();
        //扫描结果回调
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("qr_scan_result");

            //保存同步数据，要是之前有数据就覆盖

            Util.saveConfiguration(getApplicationContext(), scanResult);

            startProgress(scanResult);

        }
    }


    private void stopProgress() {

        if (regIntent != null) {

            stopService(regIntent);

        }
    }

    private void startProgress(String scanResult) {


        if (scanResult == null || "".equals(scanResult)) {

            Toast.makeText(LoginActivity.this, "点击右上角扫描器注册当前设备", Toast.LENGTH_LONG).show();

            return;

        }

        isRun = true;

        startService(regIntent);

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("正在同步....");
        dialog.setMax(100);
        dialog.show();


    }
}

