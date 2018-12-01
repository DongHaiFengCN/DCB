package com.example.ydd.dcb.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.MutableDocument;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.common.lite.query.QueryWithMultipleConditional;
import com.example.ydd.common.tools.Util;
import com.example.ydd.dcb.R;
import com.example.ydd.dcb.application.MainApplication;
import com.example.ydd.dcb.order.MainActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.UUID;

import static com.example.ydd.dcb.application.MainApplication.playSound;
import static com.example.ydd.dcb.login.LoginPresent.COMMON_MSG;
import static com.example.ydd.dcb.login.LoginPresent.CONFIG_STATUS;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private LoginPresent loginPresent;

    private EditText passwordEt;

    private ProgressBar progressBar;

    private String adminName;

    private String adminPassword;

    private MainApplication mp;

    private MyGallery myGallery;


    int width;

    int tabWidth;

    int marginWidth;

    int marginHeight;

    List<Button> buttonList = new ArrayList<>(12);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mp = (MainApplication) getApplicationContext();

        //获取文件读写权限
        Util.checkPermission(this);

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

                    onStart();

                }
            }

        });

        loginPresent = new LoginPresent(this);

        initView();


    }


    @Override
    protected void onStart() {
        super.onStart();

        if (MainApplication.configurationExist && myGallery.isFirst()) {

            List<com.couchbase.lite.Dictionary> list = QueryWithMultipleConditional.getInstance()
                    .addConditional("className", "Employee").addOrder("username").generate();

            myGallery.setData(list);

        }


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
                // nameEt.setError(msg);

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
            case LoginPresent.COMMON_MSG:

                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();

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

        finish();
    }


    /**
     * 初始化控件
     */
    private void initView() {


        width = Util.getScreenWidth(getApplicationContext());

        tabWidth = (width / 6);

        marginWidth = width / 13;

        marginHeight = width / 26;


        myGallery = findViewById(R.id.list_item);

        LinearLayout.LayoutParams l = (LinearLayout.LayoutParams) myGallery.getLayoutParams();

        l.width = tabWidth * 3 + marginWidth * 2;

        myGallery.setLayoutParams(l);


        passwordEt = findViewById(R.id.password_et);

        disableShowInput(passwordEt);

        progressBar = findViewById(R.id.progress_bar);

        passwordEt.setSelection(passwordEt.length(), passwordEt.length());

        LinearLayout.LayoutParams etLayoutParams = (LinearLayout.LayoutParams) passwordEt.getLayoutParams();

        etLayoutParams.width = tabWidth * 3 + marginWidth * 2;

        passwordEt.setLayoutParams(etLayoutParams);
        buttonList.add((Button) findViewById(R.id.zero));
        buttonList.add((Button) findViewById(R.id.one));
        buttonList.add((Button) findViewById(R.id.two));
        buttonList.add((Button) findViewById(R.id.three));
        buttonList.add((Button) findViewById(R.id.four));
        buttonList.add((Button) findViewById(R.id.five));
        buttonList.add((Button) findViewById(R.id.six));
        buttonList.add((Button) findViewById(R.id.seven));
        buttonList.add((Button) findViewById(R.id.eight));
        buttonList.add((Button) findViewById(R.id.nine));
        buttonList.add((Button) findViewById(R.id.delete));
        buttonList.add((Button) findViewById(R.id.submit));

        TableRow row1 = findViewById(R.id.r1);
        TableLayout.LayoutParams row1LayoutParams = (TableLayout.LayoutParams) row1.getLayoutParams();
        row1LayoutParams.topMargin = marginHeight;
        row1.setLayoutParams(row1LayoutParams);

        TableRow row2 = findViewById(R.id.r2);
        TableLayout.LayoutParams row2LayoutParams = (TableLayout.LayoutParams) row2.getLayoutParams();
        row2LayoutParams.topMargin = marginHeight;
        row2.setLayoutParams(row2LayoutParams);

        TableRow row3 = findViewById(R.id.r3);
        TableLayout.LayoutParams row3LayoutParams = (TableLayout.LayoutParams) row3.getLayoutParams();
        row3LayoutParams.topMargin = marginHeight;
        row3.setLayoutParams(row3LayoutParams);

        Button button;

        String tag;

        for (int i = 0; i < 12; i++) {

            button = buttonList.get(i);

            TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) button.getLayoutParams();

            layoutParams.width = tabWidth;
            layoutParams.height = tabWidth;

            tag = (String) button.getTag();

            if (tag != null && tag.equals("0")) {
                layoutParams.rightMargin = marginWidth;


            } else if (tag != null && tag.equals("1")) {
                layoutParams.leftMargin = marginWidth;

            }

            button.setLayoutParams(layoutParams);

            final int finalI = i;

            final Button finalButton = button;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playSound();
                    int index = passwordEt.getSelectionStart();
                    Editable editable = passwordEt.getText();

                    if (finalI == 10) {

                        if (index > 0) {
                            editable.delete(index - 1, index);
                        }

                    } else if (finalI == 11) {

                        String name = myGallery.getName();
                        if (name == null) {
                            setMsg("未选择用户", COMMON_MSG);
                            return;
                        }
                        loginPresent.submit(name
                                , passwordEt.getText().toString());
                    } else {

                        editable.insert(index, finalButton.getText());

                    }
                }
            });

            if (i == 10) {

                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        playSound();
                        passwordEt.setText("");

                        return true;
                    }
                });
            }
        }
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

    public void disableShowInput(final EditText editText) {

        if (android.os.Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {//TODO: handle exception
            }
            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {//TODO: handle exception
            }
        }
    }

}

