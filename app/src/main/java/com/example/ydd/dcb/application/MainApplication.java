package com.example.ydd.dcb.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.leakcanary.LeakCanary;

import static com.example.ydd.common.Util.verifyConfiguration;


/**
 * 项目名称：点餐宝
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/10/31 17:34
 * 修改人：donghaifeng
 * 修改时间：2018/2/3 17:34
 * 修改备注：
 *
 * @author donghaifeng
 */

public class MainApplication extends Application {

    /**
     * 判断配置文件的完整性
     */
    public static boolean configurationExist = false;

    @Override
    public void onCreate() {
        super.onCreate();

        //内存溢出监听
        LeakCanary.install(this);

        //首次校验配置文件
        verityConfiguration();


    }

    public boolean verityConfiguration() {
        //校验配置文件，缓存下来用于登录时校验
        configurationExist = verifyConfiguration(this);

        if(configurationExist){

            //TODO 开启lite数据库的同步


        }

        return configurationExist;
    }


}
