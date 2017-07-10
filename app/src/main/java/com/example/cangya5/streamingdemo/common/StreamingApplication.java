package com.example.cangya5.streamingdemo.common;

import android.app.Application;

import com.qiniu.pili.droid.streaming.StreamingEnv;

/**
 * Created by cangya5 on 2017/7/7.
 */

public class StreamingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StreamingEnv.init(getApplicationContext());
    }
}
