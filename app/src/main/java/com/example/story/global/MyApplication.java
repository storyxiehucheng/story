package com.example.story.global;

import android.app.Activity;
import android.app.Application;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by story on 2017/4/8.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e("story","捕获到异常......");
                ex.printStackTrace();
                Log.e("story",ex.toString());
                Log.e("story","捕获到异常.......");
                Toast.makeText(getApplicationContext(),"很抱歉，【消谣】出现了异常，即将关闭程序",Toast.LENGTH_LONG).show();
            }
        });
    }
}
