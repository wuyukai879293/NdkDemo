package com.example.wuyukai.ndkdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by wuyukai on 16/12/19.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
