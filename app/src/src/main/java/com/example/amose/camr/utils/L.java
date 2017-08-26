package com.example.amose.camr.utils;

import android.util.Log;

public class L {
    private static String TAG = "CamS";
    public static void v(String msg){
        Log.v(TAG, msg);
    }
    public static void e(String msg){
        Log.e(TAG, msg);
    }
    public static void d(String msg){
        Log.d(TAG, msg);
    }
}

