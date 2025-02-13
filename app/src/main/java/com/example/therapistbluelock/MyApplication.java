package com.example.therapistbluelock;

import android.app.Application;

//import com.zegocloud.zimkit.services.ZIMKit;
//import com.zegocloud.zimkit.services.ZIMKitConfig;

public class MyApplication extends Application {

    public static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

//        ZIMKitConfig zimKitConfig = new ZIMKitConfig();
//        ZIMKit.initWith(this, KeyCenter.APP_ID, KeyCenter.APP_SIGN, zimKitConfig);
//        ZIMKit.initNotifications();
    }
}
