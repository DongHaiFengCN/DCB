package com.example.ydd.dcb.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.media.SoundPool;
import android.util.Log;

import com.example.ydd.common.tools.Util;
import com.example.ydd.common.lite.common.CDLFactory;
import com.example.ydd.dcb.R;
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



    private String employeeId;

    /**
     * 判断配置文件的完整性
     */
    public static boolean configurationExist = false;

    private static SoundPool soundPool;//声明一个SoundPool
    private static int soundID;//创建某个声音对应的音频ID

    @Override
    public void onCreate() {
        super.onCreate();

        initSound();

        //内存溢出监听
        LeakCanary.install(this);

        //首次校验配置文件
        configurationExist = Util.verifyConfiguration(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    private void initSound() {
        soundPool = new SoundPool.Builder().build();
        soundID = soundPool.load(this, R.raw.t5, 1);
    }

    public static void playSound() {
        soundPool.play(
                soundID,
                1f,   //左耳道音量【0~1】
                1f,   //右耳道音量【0~1】
                1,     //播放优先级【0表示最低优先级】
                0,     //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1     //播放速度【1是正常，范围从0~2】
        );
    }
}
