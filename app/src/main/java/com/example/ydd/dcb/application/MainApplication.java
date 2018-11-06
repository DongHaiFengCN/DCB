package com.example.ydd.dcb.application;

import android.app.Application;

import com.example.ydd.common.tools.Util;
import com.example.ydd.common.lite.common.CDLFactory;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.List;


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

    public CDLFactory cdlFactory;

    private CDLFactory.LoginChangerListener loginChangerListener;

    @Override
    public void onCreate() {
        super.onCreate();

        //内存溢出监听
        LeakCanary.install(this);

        //首次校验配置文件
        configurationExist = Util.verifyConfiguration(this);


    }

    public MainApplication initCDLite() {

        if(cdlFactory == null){

            cdlFactory = new CDLFactory();

            cdlFactory.initCouchBaseLite(this);
        }


        return this;
    }

    public CDLFactory.LoginChangerListener getLoginChangerListener() {
        return loginChangerListener;
    }

    public void setRepChangerListener(CDLFactory.LoginChangerListener loginChangerListener) {

        this.loginChangerListener = loginChangerListener;

    }
    public void startReplication(String[] ss) {

        List<String> channels = new ArrayList<>();

        channels.add(ss[0]);

        if (cdlFactory != null) {

            cdlFactory.startReplicator(channels, ss[1], ss[2]);

            cdlFactory.setLoginChangerListener(loginChangerListener);
        }
    }



    public void detchChangerListener() {


        loginChangerListener = null;

        cdlFactory.setLoginChangerListener(null);


    }


}
