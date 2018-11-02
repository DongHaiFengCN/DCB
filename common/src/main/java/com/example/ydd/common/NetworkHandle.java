package com.example.ydd.common;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * @author dong
 * @date 2018/10/31
 * 网络访问的操作对象
 */

public class NetworkHandle {

    private static OkHttpClient.Builder okHttpBuilder;

    private static OkHttpClient okHttpClient;

    static {

        //创建我们Client对象的构建者
        okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.followRedirects(true);

    }

    private static OkHttpClient.Builder getOkHttpBuilder() {
        return okHttpBuilder;
    }

    public static class Builder {

        private Request request;

        private Authenticator authenticator;

        private MyCallBackListener myCallBackListener;

        public Builder onCallBack(@NonNull MyCallBackListener myCallBackListener) {

            this.myCallBackListener = myCallBackListener;

            return this;
        }

        /**
         * 设置验证对象
         * @param name 名称
         * @param password 密码
         * @return Builder
         *
         * 只需要设置一次，全局
         */
        public Builder initGlobBasicAuth(final String name, final String password) {

            this.authenticator = new Authenticator() {
                @Override
                public Request authenticate(@NonNull Route route, @NonNull Response response){
                    String credential = Credentials.basic(name, password);
                    return response.request().newBuilder().header("Authorization", credential).build();
                }
            };

            return this;

        }

        public Builder setRequest(Request request) {

            this.request = request;

            return this;
        }

        /**
         * 创建网络访问
         *
         * @return okhttp
         */

        public void create() {

            if (request == null) {

                throw new IllegalArgumentException("Request 不能为空！");

            }

            if (myCallBackListener == null) {

                throw new IllegalArgumentException("网络请求回调不能为空！");
            }

            if (okHttpClient == null && authenticator == null) {

                //初始化有无验证的okhttp验证
                okHttpClient = NetworkHandle.getOkHttpBuilder().build();

                Log.e("DOAING", "无校验");

            }
            if (okHttpClient == null && authenticator != null) {

                //初始化有验证的okhttp验证
                okHttpClient = NetworkHandle.getOkHttpBuilder().authenticator(authenticator).build();

                Log.e("DOAING", "有校验");
            }

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    myCallBackListener.onFailure(e.getMessage());

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                    myCallBackListener.onResponse(response);
                }
            });
        }

    }

    public interface MyCallBackListener {

        void onResponse(Response response);

        void onFailure(String failMsg);


    }
}

