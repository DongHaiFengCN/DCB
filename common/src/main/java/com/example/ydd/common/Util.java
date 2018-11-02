package com.example.ydd.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static boolean isPhoneNumber(String msg) {

        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (msg.length() != 11) {

            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(msg);
            return m.matches();
        }


    }

    /**
     * 动态注册文件读写权限
     */
    public static void checkPermission(Activity activity) {

        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    /**
     * 查看本地的配置的{通道,用户名，密码}；
     * 重新配置完成后，会重启调用此方法查看是否保存成功，并启动数据库的同步。
     */
    public static boolean verifyConfiguration(Context context) {

        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences("Configuration", Context.MODE_PRIVATE);

        String channel = preferences.getString("channel", "");
        String password = preferences.getString("password", "");
        String telePhoneNumber = preferences.getString("password", "");

        if ("".equals(channel) || "".equals(password) || "".equals(telePhoneNumber)) {

            return false;

        }

        return true;


    }
}
