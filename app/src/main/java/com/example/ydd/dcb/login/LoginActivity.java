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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.common.lite.query.QueryWithMultipleConditional;
import com.example.ydd.common.tools.Util;
import com.example.ydd.dcb.R;
import com.example.ydd.dcb.application.MainApplication;
import com.example.ydd.dcb.order.DeskActivity;

import static com.example.ydd.dcb.login.LoginPresent.CONFIG_STATUS;


public class LoginActivity extends AppCompatActivity implements LoginView {


    private LoginPresent loginPresent;

    private EditText nameEt;

    private EditText passwordEt;

    private Button submitBt;

    private CheckBox rememberCk;

    private ProgressBar progressBar;

    private String adminName;

    private String adminPassword;

    private MainApplication mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mp = (MainApplication) getApplicationContext();

        //获取文件读写权限
        Util.checkPermission(this);

        loginPresent = new LoginPresent(this);

        initView();


    }

    @Override
    protected void onStart() {
        super.onStart();


        if (MainApplication.configurationExist) {

            mp.initCDLite().startReplication(Util.getVerifyConfiguration(getApplicationContext()));

        }

        mp.setRepChangerListener(new CDLFactory.LoginChangerListener() {
            @Override
            public void getProgress(final Long completed, final Long total) {

                if (total > 0 && progressBar.getVisibility() == View.INVISIBLE) {

                    progressBar.setVisibility(View.VISIBLE);
                }

                final int t = total.intValue();

                final int c = completed.intValue();

                progressBar.setMax(t);

                progressBar.setProgress(c);

                Log.e("DOAING", "total" + total + " completed" + completed);

                if (progressBar.getProgress() == t) {

                    progressBar.setVisibility(View.INVISIBLE);

                }

            }


        });

        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (loginPresent.submit(nameEt.getText().toString()
                        , passwordEt.getText().toString())) {

                    QueryWithMultipleConditional.getInstance()
                            .addConditional("className", "Employee")
                            .addConditional("name", "董海峰")
                            .generate();

                }


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
        if (i == R.id.action_rebind1) {

            bindDevice();

        }else if(i == R.id.action_rebind2){


        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        loginPresent.detach();

        mp.detchChangerListener();


    }


    /**
     * ui线程中的一些操作返回
     *
     * @param msg  返回信息
     * @param type 返回操作符号
     */

    @Override
    public void setMsg(String msg, int type) {

        switch (type) {

            case LoginPresent.NAME_STATUS_FAIL:
                nameEt.setError(msg);

                break;
            case LoginPresent.PASSWORD_STATUS:
                passwordEt.setError(msg);

                break;
            case CONFIG_STATUS:

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

    /**
     * 子线程返回的数据需要在ui线程中回调，
     * 主要处理绑定新设备的时候数据的处理，
     * 网络获取配置时候的回调，
     * 文件读取与保存时候的状态回调。
     *
     * @param msg  信息
     * @param type 返回类型
     */
    @Override
    public void postMsg(final String msg, final int type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                switch (type) {

                    case LoginPresent.BINDVIEW_STATUS_SUCCESS:

                        //开启同步
                        mp.initCDLite().startReplication(Util.getVerifyConfiguration(getApplicationContext()));

                        break;

                    case LoginPresent.BINDVIEW_STATUS_FAIL:

                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();

                        break;
                    case LoginPresent.COMMON_MSG:

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
        progressBar = findViewById(R.id.progress_bar);


    }

    /**
     * 触发绑定设备的方法，这里可以是打开摄像头扫描二维码
     */
    private void bindDevice() {

        View view = getLayoutInflater().inflate(R.layout.activity_login_bind_view, null);

        final EditText telePhoneNumberEt = view.findViewById(R.id.telephone_number_et);

        final EditText passwordEt = view.findViewById(R.id.password_et);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view)
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

                            adminName = telePhoneNumberEt.getText().toString();

                            adminPassword = passwordEt.getText().toString();

                            loginPresent.bindNewMsg(adminName
                                    , adminPassword);
                            alertDialog.dismiss();
                        }

                    }
                });
    }

}
