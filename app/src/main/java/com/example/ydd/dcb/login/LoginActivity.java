package com.example.ydd.dcb.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.MutableDocument;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.common.tools.Util;
import com.example.ydd.dcb.R;
import com.example.ydd.dcb.application.MainApplication;

import com.example.ydd.dcb.order.MainActivity;

import java.util.UUID;

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
    private SharedPreferences preferences;

    private static int sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        mp = (MainApplication) getApplicationContext();

        //获取文件读写权限
        Util.checkPermission(this);

        loginPresent = new LoginPresent(this);


    }

    @Override
    protected void onStart() {
        super.onStart();


        if (MainApplication.configurationExist) {

            CDLFactory.getInstance().initCouchBaseLite(getApplicationContext())
                    .startReplicator(Util.getVerifyConfiguration(getApplicationContext()));

        }


        //获取同步监听
        CDLFactory.getInstance().setLoginChangerListener(new CDLFactory.LoginChangerListener() {
            @Override
            public void getProgress(final Long completed, final Long total) {

                if (total > 0 && progressBar.getVisibility() == View.INVISIBLE) {

                    progressBar.setVisibility(View.VISIBLE);
                }

                final int t = total.intValue();

                final int c = completed.intValue();

                progressBar.setMax(t);

                progressBar.setProgress(c);

                Log.e("DOAING", "completed " + completed + " total " + total);

                if (progressBar.getProgress() == t) {

                    progressBar.setVisibility(View.INVISIBLE);

                }
            }

        });

        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                loginPresent.submit(nameEt.getText().toString()
                        , passwordEt.getText().toString());

            }
        });

        rememberCk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();

                if (isChecked) {

                    editor.putString("name", nameEt.getText().toString());
                    editor.putString("paw", passwordEt.getText().toString());
                    editor.putBoolean("isCk", isChecked);

                    editor.apply();

                } else {

                    editor.clear();
                    editor.commit();

                }

            }
        });


        findViewById(R.id.re_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                MutableDocument mutableDocument = new MutableDocument("Table."+UUID.randomUUID().toString());


                mutableDocument.setString("channelId","e310cfc1");

                mutableDocument.setString("className","Table");

                mutableDocument.setString("areaId","Area.43dbace1-7b30-4d32-97f2-3e2bf710ef32");
                mutableDocument.setInt("serialNumber",6);
                mutableDocument.setInt("state",0);
                mutableDocument.setBoolean("valid",true);

                CDLFactory.getInstance().saveDocument(mutableDocument);


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

        } else if (i == R.id.action_rebind2) {


        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        loginPresent.detach();

        //解绑监听
        CDLFactory.getInstance().setLoginChangerListener(null);


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

                        CDLFactory.getInstance().initCouchBaseLite(getApplicationContext())
                                .startReplicator(Util.getVerifyConfiguration(getApplicationContext()));

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
    public void success(String id) {

        mp.setEmployeeId(id);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }


    /**
     * 初始化控件
     */
    private void initView() {

        preferences = getApplicationContext()
                .getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);

        nameEt = findViewById(R.id.name_et);
        passwordEt = findViewById(R.id.password_et);
        submitBt = findViewById(R.id.submit_bt);
        rememberCk = findViewById(R.id.remember_ck);
        progressBar = findViewById(R.id.progress_bar);

        String name = preferences.getString("name", "");
        String paw = preferences.getString("paw", "");
        Boolean isCheck = preferences.getBoolean("isCk", false);

        rememberCk.setChecked(isCheck);
        nameEt.setText(name);
        passwordEt.setText(paw);


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
