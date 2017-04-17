package com.example.myapplication;

import android.app.Application;

import org.litepal.LitePal;
import org.litepal.parser.LitePalAttr;

/**
 * Created by User on 2017/3/20.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
