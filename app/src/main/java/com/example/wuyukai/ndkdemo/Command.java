package com.example.wuyukai.ndkdemo;

/**
 * Created by wuyukai on 16/9/5.
 */
public class Command {
    public static native String getStringFromC();

    static{
        System.loadLibrary("ndk-demo");
    }
}
