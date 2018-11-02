package com.example.ydd.dcb.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ydd.common.Util;
import com.example.ydd.dcb.R;
import com.example.ydd.dcb.application.MainApplication;
import com.example.ydd.dcb.order.DeskActivity;


public class LoginActivity extends AppCompatActivity implements LoginView {


    private LoginPresent loginPresent;

    private EditText nameEt;

    private EditText passwordEt;

    private Button submitBt;

    private CheckBox rememberCk;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //关闭测试#6


        //获取文件读写权限
        Util.checkPermission(this);

        //注册层控制
        loginPresent = new LoginPresent(this);

        //初始化控件
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginPresent.submit(nameEt.getText().toString(), passwordEt.getText().toString());
            }
        });


        rememberCk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    Log.e("DOAING", "选中的");

                } else {

                    Log.e("DOAING", "取消了");

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_rebind) {

            View view = getLayoutInflater().inflate(R.layout.activity_login_bind_view, null);

            final EditText telePhoneNumberEt = view.findViewById(R.id.telephone_number_et);

            final EditText passwordEt = view.findViewById(R.id.password_et);

            AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view)
                    .setTitle("绑定新账号").setPositiveButton("确定", null).show();

            alertDialog.getButton(Dialog.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (telePhoneNumberEt.getText().length() == 0) {
                                telePhoneNumberEt.setError("电话不能为空！");
                                return;
                            } else if (passwordEt.getText().length() == 0) {
                                passwordEt.setError("密码不能为空！");
                                return;
                            } else {

                                loginPresent.bindNewMsg(telePhoneNumberEt.getText().toString()
                                        , passwordEt.getText().toString());
                            }

                        }
                    });

        }
        return true;
    }

    @Override
    public void setMsg(String msg, int type) {

        switch (type) {

            case LoginPresent.NAME_STATUS:
                nameEt.setError(msg);

                break;
            case LoginPresent.PASSWORD_STATUS:
                passwordEt.setError(msg);

                break;
            case LoginPresent.CONFIG_STATUS:

                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("使用说明")
                        .setMessage(msg)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

                break;
            default:
                break;
        }

    }

    @Override
    public void postMsg(final String msg, final int type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                switch (type) {

                    case LoginPresent.BINDVIEW_STATUS_SUCCESS:
                        //TODO 网络拉取数据成功，处理异步回调的数据

                        //发起二次读取配置的请求，并开启数据库同步
                       if(((MainApplication) getApplicationContext()).verityConfiguration()){

                           alertDialog.dismiss();

                       } else {

                           Toast.makeText(LoginActivity.this,"配置成功！",Toast.LENGTH_LONG).show();
                       }

                        break;

                    case LoginPresent.BINDVIEW_STATUS_FAIL:

                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();

                        break;

                    default:
                        break;
                }

            }
        });


    }

    @Override
    public void success() {

        startActivity(new Intent(LoginActivity.this, DeskActivity.class));

    }

    /**
     * 初始化控件
     */
    private void initView() {

        nameEt = findViewById(R.id.name_et);
        passwordEt = findViewById(R.id.password_et);
        submitBt = findViewById(R.id.submit_bt);
        rememberCk = findViewById(R.id.remember_ck);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        loginPresent.clearAll();

    }
}
