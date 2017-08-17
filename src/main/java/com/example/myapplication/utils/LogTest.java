package com.example.myapplication.utils;

import android.util.Log;

/**
 * Created by User on 2017/3/30.
 */

public class LogTest {
    private static String TAG="test";
    public static void D(String s){
        Log.d(TAG, "Debug------: "+s);
    }
}
