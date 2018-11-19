package com.example.ydd.dcb.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.common.lite.query.QueryWithMultipleConditional;
import com.example.ydd.common.tools.Constant;
import com.example.ydd.common.tools.NetworkHandle;
import com.example.ydd.common.tools.Util;
import com.example.ydd.common.dto.BindDeviceResponse;
import com.example.ydd.dcb.application.MainApplication;
import com.google.gson.Gson;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.ydd.common.dto.BindDeviceResponse.BIND_DEVICE_RESPONSE_SUCCESS;

public class LoginPresent {


    public static final int NAME_STATUS_FAIL = 0;
    public static final int PASSWORD_STATUS = 1;
    public static final int CONFIG_STATUS = 2;
    public static final int BINDVIEW_STATUS_SUCCESS = 3;
    public static final int BINDVIEW_STATUS_FAIL = 4;

    public static final int COMMON_MSG = 5;

    private LoginView loginView;

    private Context context;

    private BindDeviceResponse bindDeviceResponse;

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


        if (!MainApplication.configurationExist) {

            loginView.setMsg("新注册设备或配置丢失，请先绑定再启用设备！"
                    + "\n" + "(点击右上角按钮进行绑定)", CONFIG_STATUS);

            return;
        }

        if ("".equals(msg)) {

            loginView.setMsg("手机号或用户名为空！", NAME_STATUS_FAIL);

            return;
        }

        if ("".equals(password)) {

            loginView.setMsg("密码空！", PASSWORD_STATUS);

            return;
        }

/*
        CDLFactory.getInstance().deleteDocument("MsgTable.2b8f5a21-b100-4ff7-a8f2-f4413a6d3794");
        CDLFactory.getInstance().deleteDocument("MsgTable.4aa1452e-0a1c-4640-8f80-ec914fb210a5");
        CDLFactory.getInstance().deleteDocument("MsgTable.4b531718-d8f7-4487-abd1-db35eb7a34a8");
        CDLFactory.getInstance().deleteDocument("MsgTable.abfcce56-70be-4279-8510-0c83b6e5395c");
        CDLFactory.getInstance().deleteDocument("MsgTable.dc1fa359-d883-4e3b-9985-19d91fbc0d10");
        CDLFactory.getInstance().deleteDocument("MsgTable.e9860ca8-f1cb-413a-ab8f-2b0d68c28fcc");
        CDLFactory.getInstance().deleteDocument("MsgTable.f9709928-ebd1-4440-999c-1e0020e65c15");
      */  List<Dictionary> dictionaries;

        if (Util.isPhoneNumber(msg)) {


            dictionaries = QueryWithMultipleConditional.getInstance()
                    .addConditional("className", "Employee")
                    .addConditional("mobile", msg)
                    .addConditional("pwd", password)
                    .generate();
        } else {


            dictionaries = QueryWithMultipleConditional
                    .getInstance()
                    .addConditional("className", "Employee")
                    .addConditional("userName", msg)
                    .addConditional("pwd", password)
                    .generate();
        }


        Log.e("DOAING", dictionaries.size() + "");

        if (dictionaries.size() > 0) {


            loginView.success(dictionaries.get(0).getString("id"));
        }

    }

    /**
     * 绑定新的账号
     *
     * @param mobile 管理员手机号
     * @param psw    管理员密码
     */
    public void bindNewMsg(final String mobile, final String psw) {


        //如果网络访问获取数据正常，本地保存出问题，则只会触发本地保存方法
        if (bindDeviceResponse != null && BIND_DEVICE_RESPONSE_SUCCESS.equals(bindDeviceResponse.getMsg())) {

            initLocalConfig(psw, mobile, bindDeviceResponse.getData().getChannelId());

            return;
        }

        postBindRequestMsg(mobile, psw);

    }

    /**
     * 发送绑定请求信息
     *
     * @param mobile 网关手机号/名字
     * @param psw    网关用户密码
     */
    private void postBindRequestMsg(final String mobile, final String psw) {
        //发起绑定数据的方法
        RequestBody requestBody = new FormBody.Builder()
                .add("mobile", mobile)
                .add("pwd", psw)
                .build();
        Request request = new Request.Builder()
                .url(Constant.rebindUrl)
                .post(requestBody)
                .build();

        new NetworkHandle.Builder().onCallBack(new NetworkHandle.MyCallBackListener() {
            @Override
            public void onResponse(Response response) {

                try {
                    String msg = response.body().string();

                    initNetConfig(msg, psw, mobile);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String failMsg) {

                loginView.postMsg("网络拉取数据失败，提示重新试，或者检查网络状况", BINDVIEW_STATUS_FAIL);


            }
        }).setRequest(request).create();
    }

    private void initNetConfig(String msg, String psw, String mobile) {

        Gson gson = new Gson();

        BindDeviceResponse bindDeviceResponse = gson.fromJson(msg, BindDeviceResponse.class);

        if (BIND_DEVICE_RESPONSE_SUCCESS.equals(bindDeviceResponse.getStatus())) {

            String channel = bindDeviceResponse.getData().getChannelId();

            Log.e("DOAING",channel);

            initLocalConfig(psw, mobile, channel);


        } else {

            loginView.postMsg(bindDeviceResponse.getMsg(), COMMON_MSG);

        }
    }

    private void initLocalConfig(String psw, String mobile, String channel) {

        //保存配置文件,暂时保存channle， 用户名不是电话
        if (Util.saveConfiguration(context, channel, psw, channel)) {

            //二次读取配置文件并启动数据库同步
            if (Util.verifyConfiguration(context)) {

                loginView.postMsg(channel, BINDVIEW_STATUS_SUCCESS);

                MainApplication.configurationExist = true;

            } else {

                loginView.postMsg("读取配置文件失败，请重试", COMMON_MSG);
            }

        } else {

            //未知原因导致本地缓存保存失败
            loginView.postMsg("未知原因导致配置文件保存保存失败，请重试", COMMON_MSG);
        }
    }

    public void detach() {

        loginView = null;

    }

}
