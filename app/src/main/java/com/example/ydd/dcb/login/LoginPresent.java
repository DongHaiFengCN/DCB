package com.example.ydd.dcb.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ydd.common.NetworkHandle;
import com.example.ydd.common.Util;
import com.example.ydd.dcb.R;
import com.example.ydd.dcb.application.MainApplication;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.ydd.common.Util.verifyConfiguration;

public class LoginPresent {


    public static final int NAME_STATUS = 0;
    public static final int PASSWORD_STATUS = 1;
    public static final int CONFIG_STATUS = 2;
    public static final int BINDVIEW_STATUS_SUCCESS = 3;
    public static final int BINDVIEW_STATUS_FAIL = 4;
    private LoginView loginView;

    private Context context;

    public LoginPresent(Context context) {


        this.context = context;
        this.loginView = (LoginView) context;


    }

    /**
     * 发起登录
     *
     * @param msg（手机号或员工号）
     * @param password     密码
     */
    public void submit(@NonNull String msg, @NonNull String password) {
        final RequestBody requestBody;

        if (!MainApplication.configurationExist) {

            loginView.setMsg("新注册设备或配置丢失，请先绑定再启用设备！"
                    + "\n" + "(点击右上角按钮进行绑定)", CONFIG_STATUS);

            return;
        }

        if ("".equals(msg)) {

            loginView.setMsg("手机号或用户名为空！", NAME_STATUS);

            return;
        }

        if ("".equals(password)) {

            loginView.setMsg("密码空！", PASSWORD_STATUS);

            return;
        }


        if (Util.isPhoneNumber(msg)) {

            Log.e("DOAING", "手机号");

            requestBody = new FormBody.Builder()
                    .add("mobile", msg)
                    .add("pwd", password)
                    .build();

        } else {

            Log.e("DOAING", "不是手机号");

            //走员工号查询员工
            requestBody = new FormBody.Builder()
                    .add("mobile", msg)
                    .add("pwd", password)
                    .build();

        }

        Request request = new Request.Builder()
                .url("https://www.baidu.com/")
                .post(requestBody)
                .build();

        new NetworkHandle.Builder().initGlobBasicAuth("", "123456")
                .onCallBack(new NetworkHandle.MyCallBackListener() {
                    @Override
                    public void onResponse(Response response) {

                        try {
                            Log.e("DOAING", response.body().string());

                            //TODO 信息处理

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(String failMsg) {
                        Log.e("DOAING", failMsg);
                    }
                }).setRequest(request).create();


    }

    /**
     * 绑定新的账号
     *
     * @param mobile 管理员手机号
     * @param psw    管理员密码
     */
    public void bindNewMsg(String mobile, String psw) {

        //管理员的手机号
        RequestBody requestBody = new FormBody.Builder()
                .add("mobile", mobile)
                .add("pwd", psw)
                .build();
        Request request = new Request.Builder()
                .url("https://www.baidu.com/")
                .post(requestBody)
                .build();

        new NetworkHandle.Builder().onCallBack(new NetworkHandle.MyCallBackListener() {
            @Override
            public void onResponse(Response response) {


                //重新拉取网络数据，配置channel，用户，密码
                loginView.postMsg("", BINDVIEW_STATUS_SUCCESS);


            }

            @Override
            public void onFailure(String failMsg) {

                loginView.postMsg("网络拉取数据失败，提示重新试，或者检查网络状况", BINDVIEW_STATUS_FAIL);


            }
        }).setRequest(request).create();


    }

    public void clearAll() {

        loginView = null;

        context = null;

    }

}
